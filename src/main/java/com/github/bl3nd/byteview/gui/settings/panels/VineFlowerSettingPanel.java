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

package com.github.bl3nd.byteview.gui.settings.panels;

import com.formdev.flatlaf.FlatClientProperties;
import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.gui.settings.DecompilerSettingCheckBox;

import javax.swing.*;
import java.util.HashMap;

/**
 * VineFlower settings page
 * <p>
 * Created by Bl3nd.
 * Date: 5/27/2024
 */
public class VineFlowerSettingPanel extends JPanel {
	public VineFlowerSettingPanel() {
		create();
	}

	private void create() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);

		HashMap<String, Boolean> settings = ByteView.configuration.getVineFlowerSettings();

		JLabel title = new JLabel("VineFlower:");
		title.putClientProperty(FlatClientProperties.STYLE, "font:bold");

		// --ascii-strings
		JLabel encodeNonASCII = new JLabel("Encode non-ASCII characters in string and character literals as unicode " +
				"escapes");
		DecompilerSettingCheckBox encodeNonASCIICheckbox = new DecompilerSettingCheckBox("--ascii-strings");
		encodeNonASCIICheckbox.setSelected(settings.get("--ascii-strings"));

		// --boolean-as-int
		JLabel booleanAsInt = new JLabel("Represent integers 0 and 1 as booleans");
		DecompilerSettingCheckBox booleanAsIntCheckbox = new DecompilerSettingCheckBox("--boolean-as-int");
		booleanAsIntCheckbox.setSelected(settings.get("--boolean-as-int"));

		// --bytecode-source-mapping
		JLabel sourceMapping = new JLabel("Map bytecode to source lines");
		DecompilerSettingCheckBox sourceMappingCheckbox = new DecompilerSettingCheckBox("--bytecode-source-mapping");
		sourceMappingCheckbox.setSelected(settings.get("--bytecode-source-mapping"));

		// --decompile-assert
		JLabel decompileAssert = new JLabel("Decompile assert statements");
		DecompilerSettingCheckBox decompileAssertCheckbox = new DecompilerSettingCheckBox("--decompile-assert");
		decompileAssertCheckbox.setSelected(settings.get("--decompile-assert"));

		// --decompile-complex-constant-dynamic
		JLabel complexConstantDynamic = new JLabel("Decompile complex constant-dynamic. See VineFlower's usage for " +
				"this setting.");
		DecompilerSettingCheckBox complexConstantDynamicCheckbox = new DecompilerSettingCheckBox("--decompile-complex" +
				"-constant-dynamic");
		complexConstantDynamicCheckbox.setSelected(settings.get("--decompile-complex-constant-dynamic"));

		// --decompile-enums
		JLabel enumerations = new JLabel("Decompile enumerations");
		DecompilerSettingCheckBox enumerationsCheckbox = new DecompilerSettingCheckBox("--decompile-enums");
		enumerationsCheckbox.setSelected(settings.get("--decompile-enums"));

		// --decompile-finally
		JLabel decompileFinally = new JLabel("Decompile finally blocks");
		DecompilerSettingCheckBox decompileFinallyCheckbox = new DecompilerSettingCheckBox("--decompile-finally");
		decompileFinallyCheckbox.setSelected(settings.get("--decompile-finally"));

		// --decompile-generics
		JLabel generics = new JLabel("Decompile generics in classes, methods, fields, and variables");
		DecompilerSettingCheckBox genericsCheckbox = new DecompilerSettingCheckBox("--decompile-generics");
		genericsCheckbox.setSelected(settings.get("--decompile-generics"));

		// --decompile-inner
		JLabel innerClasses = new JLabel("Decompile inner classes");
		DecompilerSettingCheckBox innerClassesCheckbox = new DecompilerSettingCheckBox("--decompile-inner");
		innerClassesCheckbox.setSelected(settings.get("--decompile-inner"));

		// --decompile-java4
		JLabel java4Class = new JLabel("Re-sugar the java 1-4 class reference format instead of leaving " +
				"the synthetic code");
		DecompilerSettingCheckBox java4ClassCheckbox = new DecompilerSettingCheckBox("--decompile-java4");
		java4ClassCheckbox.setSelected(settings.get("--decompile-java4"));

		// --decompile-preview
		JLabel decompilePreview = new JLabel("Decompile features marked as preview or incubating in the latest Java " +
				"versions");
		DecompilerSettingCheckBox decompilePreviewCheckbox = new DecompilerSettingCheckBox("--decompile-preview");
		decompilePreviewCheckbox.setSelected(settings.get("--decompile-preview"));

		// --decompile-switch-expressions
		JLabel switchExpressions = new JLabel("Decompile switch expressions in modern Java class files");
		DecompilerSettingCheckBox switchExpressionsCheckbox = new DecompilerSettingCheckBox("--decompile-switch" +
				"-expressions");
		switchExpressionsCheckbox.setSelected(settings.get("--decompile-switch-expressions"));

		// --decompiler-comments
		JLabel decompilerComments = new JLabel("Enable/disable the adding of comments from the decompiler");
		DecompilerSettingCheckBox decompilerCommentsCheckbox = new DecompilerSettingCheckBox("--decompiler-comments");
		decompilerCommentsCheckbox.setSelected(settings.get("--decompiler-comments"));

		// --dump-bytecode-on-error
		JLabel dumpBytecodeOnError = new JLabel("Dump the bytecode in the method body when an error occurs");
		DecompilerSettingCheckBox dumpBytecodeOnErrorCheckbox = new DecompilerSettingCheckBox("--dump-bytecode-on" +
				"-error");
		dumpBytecodeOnErrorCheckbox.setSelected(settings.get("--dump-bytecode-on-error"));

		// --dump-code-lines
		JLabel dumpCodeLines = new JLabel("Dump line mappings to output archive zip entry extra data");
		DecompilerSettingCheckBox dumpCodeLinesCheckbox = new DecompilerSettingCheckBox("--dump-code-lines");
		dumpCodeLinesCheckbox.setSelected(settings.get("--dump-code-lines"));

		// --dump-exception-on-error
		JLabel dumpExceptionOnError = new JLabel("Dump the exception message in the method body or source file when " +
				"an error occurs");
		DecompilerSettingCheckBox dumpExceptionOnErrorCheckbox = new DecompilerSettingCheckBox("--dump-exception-on" +
				"-error");
		dumpExceptionOnErrorCheckbox.setSelected(settings.get("--dump-exception-on-error"));

		// --explicit-generics
		JLabel explicitGenerics = new JLabel("Put explicit diamond generic arguments on method calls");
		DecompilerSettingCheckBox explicitGenericsCheckbox = new DecompilerSettingCheckBox("--explicit-generics");
		explicitGenericsCheckbox.setSelected(settings.get("--explicit-generics"));

		// --force-jsr-inline
		JLabel forceJSRInline = new JLabel("Forces the processing of JSR instructions even if the class files " +
				"shouldn't contain it");
		DecompilerSettingCheckBox forceJSRInlineCheckbox = new DecompilerSettingCheckBox("--force-jsr-inline");
		forceJSRInlineCheckbox.setSelected(settings.get("--force-jsr-inline"));

		// --hide-default-constructor
		JLabel defaultConstructor = new JLabel("Hide constructors with no parameters and no code");
		DecompilerSettingCheckBox defaultConstructorCheckbox = new DecompilerSettingCheckBox("--hide-default" +
				"-constructor");
		defaultConstructorCheckbox.setSelected(settings.get("--hide-default-constructor"));

		// --hide-empty-super
		JLabel emptySuperInvocation = new JLabel("Hide super() calls with no parameters");
		DecompilerSettingCheckBox emptySuperInvocationCheckbox = new DecompilerSettingCheckBox("--hide-empty-super");
		emptySuperInvocationCheckbox.setSelected(settings.get("--hide-empty-super"));

		// --ignore-invalid-bytecode
		JLabel invalidBytecode = new JLabel("Ignore bytecode that is malformed");
		DecompilerSettingCheckBox invalidBytecodeCheckbox = new DecompilerSettingCheckBox("--ignore-invalid-bytecode");
		invalidBytecodeCheckbox.setSelected(settings.get("--ignore-invalid-bytecode"));

		// --include-classpath
		JLabel includeClasspath = new JLabel("Give the decompiler information about every jar on the classpath");
		DecompilerSettingCheckBox includeClasspathCheckbox = new DecompilerSettingCheckBox("--include-classpath");
		includeClasspathCheckbox.setSelected(settings.get("--include-classpath"));

		// --incorporate-returns
		JLabel incorporateReturns = new JLabel("Integrate returns better in try-catch blocks instead of storing them" +
				" in a temp variable");
		DecompilerSettingCheckBox incorporateReturnsCheckbox = new DecompilerSettingCheckBox("--incorporate-returns");
		incorporateReturnsCheckbox.setSelected(settings.get("--incorporate-returns"));

		// --inline-simple-lambdas
		JLabel simpleLambdas = new JLabel("Remove braces on simple, one line, lambda expressions");
		DecompilerSettingCheckBox simpleLambdasCheckbox = new DecompilerSettingCheckBox("--inline-simple-lambdas");
		simpleLambdasCheckbox.setSelected(settings.get("--inline-simple-lambdas"));

		// --keep-literals
		JLabel keepLiterals = new JLabel("Keep Nan, infinities, and pi values as-is without re-sugaring them");
		DecompilerSettingCheckBox keepLiteralsCheckbox = new DecompilerSettingCheckBox("--keep-literals");
		keepLiteralsCheckbox.setSelected(settings.get("--keep-literals"));

		// --lambda-to-anonymous-class
		JLabel lambdaToAnonymous = new JLabel("Decompile lambda expressions as anonymous classes");
		DecompilerSettingCheckBox lambdaToAnonymousCheckbox = new DecompilerSettingCheckBox("--lambda-to-anonymous" +
				"-class");
		lambdaToAnonymousCheckbox.setSelected(settings.get("--lambda-to-anonymous-class"));

		// --mark-corresponding-synthetics
		JLabel correspondingSynthetics = new JLabel("Mark lambdas and anonymous and local classes with their " +
				"respective synthetic constructs");
		DecompilerSettingCheckBox correspondingSyntheticsCheckbox = new DecompilerSettingCheckBox("--mark" +
				"-corresponding-synthetics");
		correspondingSyntheticsCheckbox.setSelected(settings.get("--mark-corresponding-synthetics"));

		// --override-annotation
		JLabel overrideAnnotation = new JLabel("Display override annotations for methods known to the decompiler");
		DecompilerSettingCheckBox overrideAnnotationCheckbox = new DecompilerSettingCheckBox("--override-annotation");
		overrideAnnotationCheckbox.setSelected(settings.get("--override-annotation"));

		// --pattern-matching
		JLabel patternMatching = new JLabel("Decompile with if and switch pattern matching enabled");
		DecompilerSettingCheckBox patternMatchingCheckbox = new DecompilerSettingCheckBox("--pattern-matching");
		patternMatchingCheckbox.setSelected(settings.get("--pattern-matching"));

		// --remove-bridge
		JLabel bridgeMethods = new JLabel("Removes any methods that are marked as bridge from the decompiled output");
		DecompilerSettingCheckBox bridgeMethodsCheckbox = new DecompilerSettingCheckBox("--remove-bridge");
		bridgeMethodsCheckbox.setSelected(settings.get("--remove-bridge"));

		// --remove-empty-try-catch
		JLabel emptyTryCatch = new JLabel("Remove try-catch blocks with no code");
		DecompilerSettingCheckBox emptyTryCatchCheckbox = new DecompilerSettingCheckBox("--remove-empty-try-catch");
		emptyTryCatchCheckbox.setSelected(settings.get("--remove-empty-try-catch"));

		// --remove-getclass
		JLabel removeGetClassInvocation = new JLabel("Remove synthetic getClass() calls");
		DecompilerSettingCheckBox removeGetClassInvocationCheckbox = new DecompilerSettingCheckBox("--remove" +
				"-getclass");
		removeGetClassInvocationCheckbox.setSelected(settings.get("--remove-getclass"));

		// --remove-synthetic
		JLabel syntheticClassMembers = new JLabel("Removes any methods and fields that are marked as synthetic");
		DecompilerSettingCheckBox syntheticClassMembersCheckbox = new DecompilerSettingCheckBox("--remove-synthetic");
		syntheticClassMembersCheckbox.setSelected(settings.get("--remove-synthetic"));

		// --show-hidden-statements
		JLabel hiddenStatements = new JLabel("Display hidden code blocks for debugging purposes");
		DecompilerSettingCheckBox hiddenStatementsCheckbox = new DecompilerSettingCheckBox("--show-hidden-statements");
		hiddenStatementsCheckbox.setSelected(settings.get("--show-hidden-statements"));

		// --skip-extra-files
		JLabel extraFiles = new JLabel("Skip copying non-class files from the input folder or file to the output");
		DecompilerSettingCheckBox extraFilesCheckbox = new DecompilerSettingCheckBox("--skip-extra-files");
		extraFilesCheckbox.setSelected(settings.get("--skip-extra-files"));

		// --synthetic-not-set
		JLabel syntheticNotSet = new JLabel("Treat some known structures as synthetic even when not explicitly set");
		DecompilerSettingCheckBox syntheticNotSetCheckbox = new DecompilerSettingCheckBox("--synthetic-not-set");
		syntheticNotSetCheckbox.setSelected(settings.get("--synthetic-not-set"));

		// --ternary-constant-simplification
		JLabel constantSimplification = new JLabel("Fold branches of ternary expressions that have boolean true and " +
				"false constants");
		DecompilerSettingCheckBox constantSimplificationCheckbox = new DecompilerSettingCheckBox("--ternary-constant" +
				"-simplification");
		constantSimplificationCheckbox.setSelected(settings.get("--ternary-constant-simplification"));

		// --ternary-in-if
		JLabel ternaryInIf = new JLabel("Tries to collapse if statements that have a ternary in their condition");
		DecompilerSettingCheckBox ternaryInIfCheckbox = new DecompilerSettingCheckBox("--ternary-in-if");
		ternaryInIfCheckbox.setSelected(settings.get("--ternary-in-if"));

		// --try-loop-fix
		JLabel tryLoopFix = new JLabel("Fixes rare cases of malformed decompilation when try blocks are found inside" +
				" of while loops");
		DecompilerSettingCheckBox tryLoopFixCheckbox = new DecompilerSettingCheckBox("--try-loop-fix");
		tryLoopFixCheckbox.setSelected(settings.get("--try-loop-fix"));

		// --undefined-as-object
		JLabel undefinedAsObject = new JLabel("Treat nameless types as java.lang.Object");
		DecompilerSettingCheckBox undefinedAsObjectCheckbox = new DecompilerSettingCheckBox("--undefined-as-object");
		undefinedAsObjectCheckbox.setSelected(settings.get("--undefined-as-object"));

		// --use-lvt-names
		JLabel useLVTNames = new JLabel("Use LVT names for local variables and parameters instead of " +
				"var<index>_<version>");
		DecompilerSettingCheckBox useLVTNamesCheckbox = new DecompilerSettingCheckBox("--use-lvt-names");
		useLVTNamesCheckbox.setSelected(settings.get("--use-lvt-names"));

		// --verify-anonymous-classes
		JLabel verifyAnonymousClasses = new JLabel("Verify that anonymous classes are local");
		DecompilerSettingCheckBox verifyAnonymousClassesCheckbox = new DecompilerSettingCheckBox("--verify-anonymous" +
				"-classes");
		verifyAnonymousClassesCheckbox.setSelected(settings.get("--verify-anonymous-classes"));

		// --verify-merges
		JLabel verifyMerges = new JLabel("Tries harder to verify the validity of variable merges. Change if strange " +
				"variable issues");
		DecompilerSettingCheckBox verifyMergesCheckbox = new DecompilerSettingCheckBox("--verify-merges");
		verifyMergesCheckbox.setSelected(settings.get("--verify-merges"));

		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(title)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, encodeNonASCIICheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(encodeNonASCIICheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(encodeNonASCII)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, booleanAsIntCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(booleanAsIntCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(booleanAsInt)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, sourceMappingCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(sourceMappingCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(sourceMapping)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, decompileAssertCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(decompileAssertCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(decompileAssert)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, complexConstantDynamicCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(complexConstantDynamicCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(complexConstantDynamic)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, enumerationsCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(enumerationsCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(enumerations)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, decompileFinallyCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(decompileFinallyCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(decompileFinally)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, genericsCheckbox, LayoutStyle.ComponentPlacement.INDENT, 15,
										15)
								.addComponent(genericsCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(generics)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, innerClassesCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(innerClassesCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(innerClasses)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, java4ClassCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(java4ClassCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(java4Class)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, decompilePreviewCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(decompilePreviewCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(decompilePreview)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, switchExpressionsCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(switchExpressionsCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(switchExpressions)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, decompilerCommentsCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(decompilerCommentsCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(decompilerComments)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, dumpBytecodeOnErrorCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(dumpBytecodeOnErrorCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(dumpBytecodeOnError)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, dumpCodeLinesCheckbox, LayoutStyle.ComponentPlacement.INDENT,
										15, 15)
								.addComponent(dumpCodeLinesCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(dumpCodeLines)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, dumpExceptionOnErrorCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(dumpExceptionOnErrorCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(dumpExceptionOnError)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, explicitGenericsCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(explicitGenericsCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(explicitGenerics)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, forceJSRInlineCheckbox, LayoutStyle.ComponentPlacement.INDENT,
										15, 15)
								.addComponent(forceJSRInlineCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(forceJSRInline)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, defaultConstructorCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(defaultConstructorCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(defaultConstructor)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, emptySuperInvocationCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(emptySuperInvocationCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(emptySuperInvocation)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, invalidBytecodeCheckbox, LayoutStyle.ComponentPlacement.INDENT
										, 15, 15)
								.addComponent(invalidBytecodeCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(invalidBytecode)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, includeClasspathCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(includeClasspathCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(includeClasspath)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, incorporateReturnsCheckbox,
										LayoutStyle.ComponentPlacement.INDENT,
										15, 15)
								.addComponent(incorporateReturnsCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(incorporateReturns)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, simpleLambdasCheckbox, LayoutStyle.ComponentPlacement.INDENT,
										15, 15)
								.addComponent(simpleLambdasCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(simpleLambdas)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, keepLiteralsCheckbox, LayoutStyle.ComponentPlacement.INDENT,
										15, 15)
								.addComponent(keepLiteralsCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(keepLiterals)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, lambdaToAnonymousCheckbox,
										LayoutStyle.ComponentPlacement.INDENT,
										15, 15)
								.addComponent(lambdaToAnonymousCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(lambdaToAnonymous)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, correspondingSyntheticsCheckbox,
										LayoutStyle.ComponentPlacement.INDENT,
										15, 15)
								.addComponent(correspondingSyntheticsCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(correspondingSynthetics)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, overrideAnnotationCheckbox,
										LayoutStyle.ComponentPlacement.INDENT,
										15, 15)
								.addComponent(overrideAnnotationCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(overrideAnnotation)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, patternMatchingCheckbox, LayoutStyle.ComponentPlacement.INDENT,
										15, 15)
								.addComponent(patternMatchingCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(patternMatching)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, bridgeMethodsCheckbox, LayoutStyle.ComponentPlacement.INDENT,
										15, 15)
								.addComponent(bridgeMethodsCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(bridgeMethods)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, emptyTryCatchCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(emptyTryCatchCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(emptyTryCatch)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, removeGetClassInvocationCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(removeGetClassInvocationCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(removeGetClassInvocation)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, syntheticClassMembersCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(syntheticClassMembersCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(syntheticClassMembers)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, hiddenStatementsCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(hiddenStatementsCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(hiddenStatements)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, extraFilesCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(extraFilesCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(extraFiles)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, syntheticNotSetCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(syntheticNotSetCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(syntheticNotSet)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, constantSimplificationCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(constantSimplificationCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(constantSimplification)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, ternaryInIfCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(ternaryInIfCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(ternaryInIf)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, tryLoopFixCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(tryLoopFixCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(tryLoopFix)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, undefinedAsObjectCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(undefinedAsObjectCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(undefinedAsObject)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, useLVTNamesCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(useLVTNamesCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(useLVTNames)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, verifyAnonymousClassesCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(verifyAnonymousClassesCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(verifyAnonymousClasses)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(title, verifyMergesCheckbox,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(verifyMergesCheckbox)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(verifyMerges)
				)
		);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup()
								.addComponent(title)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(encodeNonASCIICheckbox)
								.addComponent(encodeNonASCII)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(booleanAsIntCheckbox)
								.addComponent(booleanAsInt)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(sourceMappingCheckbox)
								.addComponent(sourceMapping)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(decompileAssertCheckbox)
								.addComponent(decompileAssert)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(complexConstantDynamicCheckbox)
								.addComponent(complexConstantDynamic)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(enumerationsCheckbox)
								.addComponent(enumerations)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(decompileFinallyCheckbox)
								.addComponent(decompileFinally)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(genericsCheckbox)
								.addComponent(generics)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(innerClassesCheckbox)
								.addComponent(innerClasses)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(java4ClassCheckbox)
								.addComponent(java4Class)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(decompilePreviewCheckbox)
								.addComponent(decompilePreview)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(switchExpressionsCheckbox)
								.addComponent(switchExpressions)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(decompilerCommentsCheckbox)
								.addComponent(decompilerComments)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(dumpBytecodeOnErrorCheckbox)
								.addComponent(dumpBytecodeOnError)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(dumpCodeLinesCheckbox)
								.addComponent(dumpCodeLines)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(dumpExceptionOnErrorCheckbox)
								.addComponent(dumpExceptionOnError)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(explicitGenericsCheckbox)
								.addComponent(explicitGenerics)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(forceJSRInlineCheckbox)
								.addComponent(forceJSRInline)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(defaultConstructorCheckbox)
								.addComponent(defaultConstructor)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(emptySuperInvocationCheckbox)
								.addComponent(emptySuperInvocation)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(invalidBytecodeCheckbox)
								.addComponent(invalidBytecode)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(includeClasspathCheckbox)
								.addComponent(includeClasspath)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(incorporateReturnsCheckbox)
								.addComponent(incorporateReturns)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(simpleLambdasCheckbox)
								.addComponent(simpleLambdas)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(keepLiteralsCheckbox)
								.addComponent(keepLiterals)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(lambdaToAnonymousCheckbox)
								.addComponent(lambdaToAnonymous)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(correspondingSyntheticsCheckbox)
								.addComponent(correspondingSynthetics)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(overrideAnnotationCheckbox)
								.addComponent(overrideAnnotation)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(patternMatchingCheckbox)
								.addComponent(patternMatching)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(bridgeMethodsCheckbox)
								.addComponent(bridgeMethods)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(emptyTryCatchCheckbox)
								.addComponent(emptyTryCatch)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(removeGetClassInvocationCheckbox)
								.addComponent(removeGetClassInvocation)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(syntheticClassMembersCheckbox)
								.addComponent(syntheticClassMembers)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(hiddenStatementsCheckbox)
								.addComponent(hiddenStatements)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(extraFilesCheckbox)
								.addComponent(extraFiles)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(syntheticNotSetCheckbox)
								.addComponent(syntheticNotSet)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(constantSimplificationCheckbox)
								.addComponent(constantSimplification)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(ternaryInIfCheckbox)
								.addComponent(ternaryInIf)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(tryLoopFixCheckbox)
								.addComponent(tryLoopFix)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(undefinedAsObjectCheckbox)
								.addComponent(undefinedAsObject)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(useLVTNamesCheckbox)
								.addComponent(useLVTNames)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(verifyAnonymousClassesCheckbox)
								.addComponent(verifyAnonymousClasses)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(verifyMergesCheckbox)
								.addComponent(verifyMerges)
				)
		);
	}

	public static HashMap<String, Boolean> settingChanges = new HashMap<>();

}
