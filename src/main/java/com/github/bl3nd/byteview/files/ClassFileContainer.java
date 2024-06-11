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

import com.github.bl3nd.byteview.misc.ClassMemberLocation;
import com.github.bl3nd.byteview.misc.FileMisc;
import com.github.bl3nd.byteview.misc.Icons;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.Type;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Bl3nd.
 * Date: 5/20/2024
 */
public class ClassFileContainer extends FileContainer {
	public boolean hasBeenDecompiled = false;
	public transient NavigableMap<String, ArrayList<ClassMemberLocation>> fieldMembers = new TreeMap<>();
	public transient NavigableMap<String, ArrayList<ClassMemberLocation>> methodParameterMembers = new TreeMap<>();
	public transient NavigableMap<String, ArrayList<ClassMemberLocation>> methodLocalMembers = new TreeMap<>();
	public transient NavigableMap<String, ArrayList<ClassMemberLocation>> methodMembers = new TreeMap<>();
	private String content;

	public ClassFileContainer(final File file) throws IOException {
		super(FileMisc.readBytes(file), file.getName());
	}

	public ClassFileContainer(byte[] bytes, String fileName) {
		super(bytes, fileName);
	}

	public boolean hasBeenDecompiled() {
		return hasBeenDecompiled;
	}

	public void setHasBeenDecompiled(boolean hasBeenDecompiled) {
		this.hasBeenDecompiled = hasBeenDecompiled;
	}

	public void parse(String content) {
		this.content = content;

		JavaParser parser = new JavaParser();
		ParseResult<CompilationUnit> result = parser.parse(content);
		CompilationUnit cu = result.getResult().orElse(null);
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

	private @NotNull String getMethodKey(@NotNull MethodDeclaration method) {
		String methodType = method.getTypeAsString();
		String methodName = method.getNameAsString();
		NodeList<Parameter> parameters = method.getParameters();
		List<String> parameterTypeList = new ArrayList<>();
		for (Iterator<Parameter> it = parameters.stream().iterator(); it.hasNext(); ) {
			Parameter parameter = it.next();
			String type = parameter.getTypeAsString();
			parameterTypeList.add(type);
		}

		String parameterTypeString = parameterTypeList.toString().substring(1,
				parameterTypeList.toString().length() - 1);
		return methodName + "(" + parameterTypeString + "): " + methodType;
	}

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

	private static @NotNull String getConstructorKeyString(@NotNull ConstructorDeclaration constructor) {
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

	private void findToken(@NotNull TokenRange tokenRange, @NotNull String token, Type type, String owner) {
		List<ClassMemberLocation> locations = new ArrayList<>();
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

				ClassMemberLocation location = new ClassMemberLocation(owner, line, beginColumn, endColumn);
				if (!locations.contains(location)) {
					locations.add(location);
//					this.tokenRanges.put(token, locations);
				}
			}
		}*/
	}

	public String getContent() {
		return content;
	}

	public NavigableMap<String, ArrayList<ClassMemberLocation>> getFieldMembers() {
		return fieldMembers;
	}

	public List<ClassMemberLocation> getMemberLocationsFor(String key) {
		return fieldMembers.getOrDefault(key, new ArrayList<>());
	}

	public List<ClassMemberLocation> getParameterLocationsFor(String key) {
		return methodParameterMembers.getOrDefault(key, new ArrayList<>());
	}

	public List<ClassMemberLocation> getLocalLocationsFor(String key) {
		return methodLocalMembers.getOrDefault(key, new ArrayList<>());
	}

	public List<ClassMemberLocation> getMethodLocationsFor(String key) {
		return methodMembers.getOrDefault(key, new ArrayList<>());
	}
}
