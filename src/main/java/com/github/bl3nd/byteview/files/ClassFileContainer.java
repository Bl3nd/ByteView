/*
 * MIT License
 *
 * Copyright (c) 2024 Cody March
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.bl3nd.byteview.files;

import com.github.bl3nd.byteview.tokens.location.*;
import com.github.bl3nd.byteview.misc.FileMisc;
import com.github.bl3nd.byteview.misc.Icons;
import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.Type;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A container for .class files
 * <p>
 * Created by Bl3nd.
 * Date: 5/20/2024
 */
public class ClassFileContainer extends FileContainer {
	public boolean hasBeenDecompiled = false;
	public transient NavigableMap<String, ArrayList<ClassFieldLocation>> fieldMembers = new TreeMap<>();
	public transient NavigableMap<String, ArrayList<ClassParameterLocation>> methodParameterMembers = new TreeMap<>();
	public transient NavigableMap<String, ArrayList<ClassLocalVariableLocation>> methodLocalMembers = new TreeMap<>();
	public transient NavigableMap<String, ArrayList<ClassMethodLocation>> methodMembers = new TreeMap<>();
	public transient NavigableMap<String, ArrayList<TestParameterLocation>> testParameterMembers = new TreeMap<>();

	public ClassFileContainer(final File file) throws IOException {
		super(FileMisc.readBytes(file), file.getName());
	}

	public ClassFileContainer(byte[] bytes, String fileName) {
		super(bytes, fileName);
	}

	/**
	 * Parse the Java content using JavaParser. This let us use the parsed Java in the structure pane.
	 */
	public void parse() {
		// TODO: Sometimes this doesn't work
		JavaParser parser = new JavaParser();
		ParseResult<CompilationUnit> result = parser.parse(content);
		CompilationUnit cu = result.getResult().orElse(null);
		if (!result.isSuccessful()) {
			result.getProblems().forEach(problem -> System.err.println(problem.getCause().orElse(null)));
		}
		if (cu != null) {
			NodeList<TypeDeclaration<?>> types = cu.getTypes();
			types.stream().map(TypeDeclaration::getMembers).forEach(members -> members.forEach(member -> {
				if (member instanceof MethodDeclaration method) {
					String key = getMethodKey(method);
					putMethod(method, key);
				} else if (member instanceof FieldDeclaration field) {
					List<Node> childNodes = field.getChildNodes();
					String declaratorType = "";
					Type type = null;
					for (Node childNode : childNodes) {
						if (childNode instanceof VariableDeclarator variable) {
							type = variable.getType();
							declaratorType = type.asString();
						}
					}

					Optional<TokenRange> tokenRange = field.getTokenRange();
					String owner = FileMisc.removeExtension(fileName);
					Type finalType = type;
					tokenRange.orElseThrow().forEach(javaToken -> findToken(javaToken.toTokenRange(),
							field.getVariable(0).getNameAsString(), finalType, owner));

					putField(field, declaratorType);
				} else if (member instanceof ConstructorDeclaration constructor) {
					String key = getConstructorKeyString(constructor);
					if (constructor.isPrivate()) {
						this.memberMap.put(key, Icons.methodPrivateIcon);
					} else {
						this.memberMap.put(key, Icons.methodIcon);
					}
				}
			}));
		}
	}

	public void setHasBeenDecompiled(boolean hasBeenDecompiled) {
		this.hasBeenDecompiled = hasBeenDecompiled;
	}

	/**
	 * Get the method and turn it into the structure pane text
	 *
	 * @param method the method
	 * @return the structure text
	 */
	private @NotNull String getMethodKey(@NotNull MethodDeclaration method) {
		String methodType = method.getTypeAsString();
		String methodName = method.getNameAsString();
		NodeList<Parameter> parameters = method.getParameters();
		NodeList<Statement> statements = method.getBody().isPresent() ?
				method.getBody().get().asBlockStmt().getStatements() : null;
		/*for (Statement statement : statements) {
			if (statement instanceof TryStmt tryStmt) {
				for (Statement tryBlockStatement : tryStmt.getTryBlock().getStatements()) {
					if (tryBlockStatement instanceof ExpressionStmt expressionStmt) {
						Expression expression = expressionStmt.getExpression();
						if (expression instanceof VariableDeclarationExpr variableDeclarationExpr) {
							for (VariableDeclarator variableDeclarator : variableDeclarationExpr.getVariables()) {
								String variableName = variableDeclarator.getNameAsString();
								Optional<Range> tokenRange = variableDeclarator.getRange();
								int line = tokenRange.map(range -> range.begin.line).orElse(-1);
								int columnStart = tokenRange.map(range -> range.begin.column).orElse(-1);
								int columnEnd = tokenRange.map(range -> range.end.column).orElse(-1);
							}
						} else if (expression instanceof UnaryExpr unaryExpr) {
							TokenRange tokenRange = unaryExpr.getTokenRange().orElseThrow();
							String variableName = tokenRange.getBegin().getText();
							int line = tokenRange.getBegin().getRange().orElseThrow().begin.line;
							int columnStart = tokenRange.getBegin().getRange().orElseThrow().begin.column;
							int columnEnd = tokenRange.getBegin().getRange().orElseThrow().end.column;
						}
					}
				}
			}
		}*/

		List<String> parameterTypeList = new ArrayList<>();
		for (Iterator<Parameter> it = parameters.stream().iterator(); it.hasNext(); ) {
			Parameter parameter = it.next();
			String type = parameter.getTypeAsString();
			parameterTypeList.add(type);
			Optional<Range> tokenRange = parameter.getTokenRange().isPresent() ?
					parameter.getTokenRange().get().toRange() : Optional.empty();
			int line = tokenRange.map(range -> range.begin.line).orElse(-1);
			int columnStart = tokenRange.map(range -> range.begin.column).orElse(-1);
			int columnEnd = tokenRange.map(range -> range.end.column).orElse(-1);

			putTestParameter(parameter.getNameAsString(), new TestParameterLocation(method, true,
					line,
					columnStart,
					columnEnd));
		}

		String parameterTypeString = parameterTypeList.toString().substring(1,
				parameterTypeList.toString().length() - 1);
		return methodName + "(" + parameterTypeString + "): " + methodType;
	}

	private void putTestParameter(String key, TestParameterLocation value) {
		this.testParameterMembers.computeIfAbsent(key, _ -> new ArrayList<>()).add(value);
	}

	/**
	 * Get the classes constructor and turns it into the structure pane text
	 *
	 * @param constructor the constructor
	 * @return the constructor as the structure text
	 */
	private @NotNull String getConstructorKeyString(@NotNull ConstructorDeclaration constructor) {
		String constructorName = constructor.getNameAsString();
		NodeList<Parameter> parameters = constructor.getParameters();
		List<String> parameterTypeList = new ArrayList<>();
		for (Iterator<Parameter> it = parameters.stream().iterator(); it.hasNext(); ) {
			Parameter parameter = it.next();
			String type = parameter.getTypeAsString();
			parameterTypeList.add(type);
		}

		String parameterTypeString = parameterTypeList.toString().substring(1,
				parameterTypeList.toString().length() - 1);
		return constructorName + "(" + parameterTypeString + ")";
	}

	/**
	 * Put the method into the containers {@code memberMap}.
	 *
	 * @param method the method
	 * @param key    the method's structure text
	 */
	private void putMethod(@NotNull MethodDeclaration method, String key) {
		if (method.isPrivate() && !method.isStatic()) {
			this.memberMap.put(key, Icons.methodPrivateIcon);
		} else if (method.isPrivate() && method.isStatic()) {
			this.memberMap.put(key, Icons.methodPrivateStaticIcon);
		} else if (method.isStatic()) {
			this.memberMap.put(key, Icons.methodStaticIcon);
		} else {
			this.memberMap.put(key, Icons.methodIcon);
		}
	}

	/**
	 * Put the field into the containers {@code memberMap}.
	 *
	 * @param field          the field
	 * @param declaratorType the field's structure text
	 */
	private void putField(@NotNull FieldDeclaration field, String declaratorType) {
		if (field.isPrivate() && !field.isStatic()) {
			this.memberMap.put(field.getVariable(0).getNameAsString() + ": " + declaratorType,
					Icons.fieldPrivateIcon);
		} else if (field.isPrivate() && field.isStatic()) {
			this.memberMap.put(field.getVariable(0).getNameAsString() + ": " + declaratorType,
					Icons.fieldPrivateStaticIcon);
		} else if (field.isStatic()) {
			this.memberMap.put(field.getVariable(0).getNameAsString() + ": " + declaratorType,
					Icons.fieldStaticIcon);
		} else {
			this.memberMap.put(field.getVariable(0).getNameAsString() + ": " + declaratorType,
					Icons.fieldIcon);
		}
	}

	private void findToken(@NotNull TokenRange tokenRange, @NotNull String token, Type type, String owner) {
		List<ClassFieldLocation> locations = new ArrayList<>();
		/*Iterator<JavaToken> it = tokenRange.iterator();
		while (it.hasNext()) {
			JavaToken javaToken = it.next();
			String tokenText = javaToken.getText();
			if (tokenText.equals(token)) {
				int line = javaToken.getRange().orElseThrow().begin.line;
				int beginColumn = javaToken.getRange().orElseThrow().begin.column;
				int endColumn = javaToken.getRange().orElseThrow().end.column;
//				if (type instanceof ClassOrInterfaceType t) {
//					if (!t.asString().equals(owner)) {
//						owner = t.asString();
//					}
//				}

				ClassFieldLocation location = new ClassFieldLocation(owner, line, beginColumn, endColumn);
				if (!locations.contains(location)) {
					locations.add(location);
//					this.tokenRanges.put(token, locations);
				}
			}
		}*/
	}

	public NavigableMap<String, ArrayList<ClassFieldLocation>> getFieldMembers() {
		return fieldMembers;
	}

	public List<ClassFieldLocation> getMemberLocationsFor(String key) {
		return fieldMembers.getOrDefault(key, new ArrayList<>());
	}

	public List<ClassParameterLocation> getParameterLocationsFor(String key) {
		return methodParameterMembers.getOrDefault(key, new ArrayList<>());
	}

	public List<ClassLocalVariableLocation> getLocalLocationsFor(String key) {
		return methodLocalMembers.getOrDefault(key, new ArrayList<>());
	}

	public List<ClassMethodLocation> getMethodLocationsFor(String key) {
		return methodMembers.getOrDefault(key, new ArrayList<>());
	}
}
