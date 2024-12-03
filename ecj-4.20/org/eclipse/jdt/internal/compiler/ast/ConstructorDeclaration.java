/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.CompactConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.AnnotationContext;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ConstructorDeclaration
extends AbstractMethodDeclaration {
    public ExplicitConstructorCall constructorCall;
    public TypeParameter[] typeParameters;

    public ConstructorDeclaration(CompilationResult compilationResult) {
        super(compilationResult);
    }

    public void analyseCode(ClassScope classScope, InitializationFlowContext initializerFlowContext, FlowInfo flowInfo, int initialReachMode) {
        int nonStaticFieldInfoReachMode;
        block23: {
            block24: {
                MethodBinding methodBinding;
                if (this.ignoreFurtherInvestigation) {
                    return;
                }
                nonStaticFieldInfoReachMode = flowInfo.reachMode();
                flowInfo.setReachMode(initialReachMode);
                MethodBinding constructorBinding = this.binding;
                if (constructorBinding == null || (this.bits & 0x80) != 0 || constructorBinding.isUsed() || (!constructorBinding.isPrivate() ? !constructorBinding.isOrEnclosedByPrivateType() : (this.binding.declaringClass.tagBits & 0x1000000000000000L) == 0L) || this.constructorCall == null) break block23;
                if (this.constructorCall.accessMode == 3) break block24;
                ReferenceBinding superClass = constructorBinding.declaringClass.superclass();
                if (superClass == null || (methodBinding = superClass.getExactConstructor(Binding.NO_PARAMETERS)) == null || !methodBinding.canBeSeenBy(SuperReference.implicitSuperConstructorCall(), this.scope)) break block23;
                ReferenceBinding declaringClass = constructorBinding.declaringClass;
                if (constructorBinding.isPublic() && constructorBinding.parameters.length == 0 && declaringClass.isStatic() && declaringClass.findSuperTypeOriginatingFrom(56, false) != null) break block23;
            }
            if ((this.bits & 0x400) == 0) {
                this.scope.problemReporter().unusedPrivateConstructor(this);
            }
        }
        if (this.isRecursive(null)) {
            this.scope.problemReporter().recursiveConstructorInvocation(this.constructorCall);
        }
        if (this.typeParameters != null && !this.scope.referenceCompilationUnit().compilationResult.hasSyntaxError) {
            int i = 0;
            int length = this.typeParameters.length;
            while (i < length) {
                TypeParameter typeParameter = this.typeParameters[i];
                if ((typeParameter.binding.modifiers & 0x8000000) == 0) {
                    this.scope.problemReporter().unusedTypeParameter(typeParameter);
                }
                ++i;
            }
        }
        try {
            FieldBinding[] fields;
            int size;
            List computedExceptions;
            ExceptionHandlingFlowContext constructorContext = new ExceptionHandlingFlowContext(initializerFlowContext.parent, this, this.binding.thrownExceptions, initializerFlowContext, this.scope, FlowInfo.DEAD_END);
            initializerFlowContext.checkInitializerExceptions(this.scope, constructorContext, flowInfo);
            if (this.binding.declaringClass.isAnonymousType() && (computedExceptions = constructorContext.extendedExceptions) != null && (size = computedExceptions.size()) > 0) {
                ReferenceBinding[] actuallyThrownExceptions = new ReferenceBinding[size];
                computedExceptions.toArray(actuallyThrownExceptions);
                this.binding.thrownExceptions = actuallyThrownExceptions;
            }
            ConstructorDeclaration.analyseArguments(classScope.environment(), flowInfo, this.arguments, this.binding);
            if (this.constructorCall != null) {
                if (this.constructorCall.accessMode == 3) {
                    fields = this.binding.declaringClass.fields();
                    int i = 0;
                    int count = fields.length;
                    while (i < count) {
                        FieldBinding field = fields[i];
                        if (!field.isStatic()) {
                            flowInfo.markAsDefinitelyAssigned(field);
                        }
                        ++i;
                    }
                }
                flowInfo = this.constructorCall.analyseCode(this.scope, constructorContext, flowInfo);
            }
            flowInfo.setReachMode(nonStaticFieldInfoReachMode);
            if (this.statements != null) {
                CompilerOptions compilerOptions = this.scope.compilerOptions();
                boolean enableSyntacticNullAnalysisForFields = compilerOptions.enableSyntacticNullAnalysisForFields;
                int complaintLevel = (nonStaticFieldInfoReachMode & 3) == 0 ? 0 : 1;
                int i = 0;
                int count = this.statements.length;
                while (i < count) {
                    Statement stat = this.statements[i];
                    if ((complaintLevel = stat.complainIfUnreachable(flowInfo, this.scope, complaintLevel, true)) < 2) {
                        flowInfo = stat.analyseCode(this.scope, constructorContext, flowInfo);
                    }
                    if (enableSyntacticNullAnalysisForFields) {
                        constructorContext.expireNullCheckedFieldInfo();
                    }
                    if (compilerOptions.analyseResourceLeaks) {
                        FakedTrackingVariable.cleanUpUnassigned(this.scope, stat, flowInfo);
                    }
                    ++i;
                }
            }
            if ((flowInfo.tagBits & 1) == 0) {
                this.bits |= 0x40;
            }
            if (this.constructorCall != null && this.constructorCall.accessMode != 3) {
                flowInfo = flowInfo.mergedWith(constructorContext.initsOnReturn);
                fields = this.binding.declaringClass.fields();
                this.checkAndGenerateFieldAssignment(initializerFlowContext, flowInfo, fields);
                this.doFieldReachAnalysis(flowInfo, fields);
            }
            constructorContext.complainIfUnusedExceptionHandlers(this);
            this.scope.checkUnusedParameters(this.binding);
            this.scope.checkUnclosedCloseables(flowInfo, null, null, null);
        }
        catch (AbortMethod abortMethod) {
            this.ignoreFurtherInvestigation = true;
        }
    }

    protected void doFieldReachAnalysis(FlowInfo flowInfo, FieldBinding[] fields) {
        int i = 0;
        int count = fields.length;
        while (i < count) {
            FieldBinding field = fields[i];
            if (!field.isStatic() && !flowInfo.isDefinitelyAssigned(field)) {
                FieldDeclaration fieldDecl;
                if (field.isFinal()) {
                    this.scope.problemReporter().uninitializedBlankFinalField(field, (this.bits & 0x80) != 0 ? this.scope.referenceType().declarationOf(field.original()) : this);
                } else if ((field.isNonNull() || field.type.isFreeTypeVariable()) && !this.isValueProvidedUsingAnnotation(fieldDecl = this.scope.referenceType().declarationOf(field.original()))) {
                    this.scope.problemReporter().uninitializedNonNullField(field, (this.bits & 0x80) != 0 ? fieldDecl : this);
                }
            }
            ++i;
        }
    }

    protected void checkAndGenerateFieldAssignment(FlowContext flowContext, FlowInfo flowInfo, FieldBinding[] fields) {
    }

    boolean isValueProvidedUsingAnnotation(FieldDeclaration fieldDecl) {
        if (fieldDecl.annotations != null) {
            int length = fieldDecl.annotations.length;
            int i = 0;
            while (i < length) {
                int j;
                MemberValuePair[] memberValuePairs;
                Annotation annotation = fieldDecl.annotations[i];
                if (annotation.resolvedType.id == 80) {
                    return true;
                }
                if (annotation.resolvedType.id == 81) {
                    memberValuePairs = annotation.memberValuePairs();
                    if (memberValuePairs == Annotation.NoValuePairs) {
                        return true;
                    }
                    j = 0;
                    while (j < memberValuePairs.length) {
                        if (CharOperation.equals(memberValuePairs[j].name, TypeConstants.OPTIONAL)) {
                            return memberValuePairs[j].value instanceof FalseLiteral;
                        }
                        ++j;
                    }
                } else if (annotation.resolvedType.id == 82) {
                    memberValuePairs = annotation.memberValuePairs();
                    if (memberValuePairs == Annotation.NoValuePairs) {
                        return true;
                    }
                    j = 0;
                    while (j < memberValuePairs.length) {
                        if (CharOperation.equals(memberValuePairs[j].name, TypeConstants.REQUIRED)) {
                            return memberValuePairs[j].value instanceof TrueLiteral;
                        }
                        ++j;
                    }
                }
                ++i;
            }
        }
        return false;
    }

    @Override
    public void generateCode(ClassScope classScope, ClassFile classFile) {
        TypeDeclaration referenceContext;
        int problemResetPC = 0;
        if (this.ignoreFurtherInvestigation) {
            if (this.binding == null) {
                return;
            }
            CategorizedProblem[] problems = this.scope.referenceCompilationUnit().compilationResult.getProblems();
            int problemsLength = problems.length;
            CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength];
            System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
            classFile.addProblemConstructor(this, this.binding, problemsCopy);
            return;
        }
        boolean restart = false;
        boolean abort = false;
        CompilationResult unitResult = null;
        int problemCount = 0;
        if (classScope != null && (referenceContext = classScope.referenceContext) != null) {
            unitResult = referenceContext.compilationResult();
            problemCount = unitResult.problemCount;
        }
        do {
            try {
                problemResetPC = classFile.contentsOffset;
                this.internalGenerateCode(classScope, classFile);
                restart = false;
            }
            catch (AbortMethod e) {
                if (e.compilationResult == CodeStream.RESTART_IN_WIDE_MODE) {
                    classFile.contentsOffset = problemResetPC;
                    --classFile.methodCount;
                    classFile.codeStream.resetInWideMode();
                    if (unitResult != null) {
                        unitResult.problemCount = problemCount;
                    }
                    restart = true;
                    continue;
                }
                if (e.compilationResult == CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE) {
                    classFile.contentsOffset = problemResetPC;
                    --classFile.methodCount;
                    classFile.codeStream.resetForCodeGenUnusedLocals();
                    if (unitResult != null) {
                        unitResult.problemCount = problemCount;
                    }
                    restart = true;
                    continue;
                }
                restart = false;
                abort = true;
            }
        } while (restart);
        if (abort) {
            CategorizedProblem[] problems = this.scope.referenceCompilationUnit().compilationResult.getAllProblems();
            int problemsLength = problems.length;
            CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength];
            System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
            classFile.addProblemConstructor(this, this.binding, problemsCopy, problemResetPC);
        }
    }

    public void generateSyntheticFieldInitializationsIfNecessary(MethodScope methodScope, CodeStream codeStream, ReferenceBinding declaringClass) {
        SyntheticArgumentBinding syntheticArg;
        int max;
        int i;
        if (!declaringClass.isNestedType()) {
            return;
        }
        NestedTypeBinding nestedType = (NestedTypeBinding)declaringClass;
        SyntheticArgumentBinding[] syntheticArgs = nestedType.syntheticEnclosingInstances();
        if (syntheticArgs != null) {
            i = 0;
            max = syntheticArgs.length;
            while (i < max) {
                syntheticArg = syntheticArgs[i];
                if (syntheticArg.matchingField != null) {
                    codeStream.aload_0();
                    codeStream.load(syntheticArg);
                    codeStream.fieldAccess((byte)-75, syntheticArg.matchingField, null);
                }
                ++i;
            }
        }
        if ((syntheticArgs = nestedType.syntheticOuterLocalVariables()) != null) {
            i = 0;
            max = syntheticArgs.length;
            while (i < max) {
                syntheticArg = syntheticArgs[i];
                if (syntheticArg.matchingField != null) {
                    codeStream.aload_0();
                    codeStream.load(syntheticArg);
                    codeStream.fieldAccess((byte)-75, syntheticArg.matchingField, null);
                }
                ++i;
            }
        }
    }

    private void internalGenerateCode(ClassScope classScope, ClassFile classFile) {
        classFile.generateMethodInfoHeader(this.binding);
        int methodAttributeOffset = classFile.contentsOffset;
        int attributeNumber = classFile.generateMethodInfoAttributes(this.binding);
        if (!this.binding.isNative() && !this.binding.isAbstract()) {
            int max;
            int i;
            boolean preInitSyntheticFields;
            TypeDeclaration declaringType = classScope.referenceContext;
            int codeAttributeOffset = classFile.contentsOffset;
            classFile.generateCodeAttributeHeader();
            CodeStream codeStream = classFile.codeStream;
            codeStream.reset(this, classFile);
            ReferenceBinding declaringClass = this.binding.declaringClass;
            int enumOffset = declaringClass.isEnum() ? 2 : 0;
            int argSlotSize = 1 + enumOffset;
            if (declaringClass.isNestedType()) {
                this.scope.extraSyntheticArguments = declaringClass.syntheticOuterLocalVariables();
                this.scope.computeLocalVariablePositions(declaringClass.getEnclosingInstancesSlotSize() + 1 + enumOffset, codeStream);
                argSlotSize += declaringClass.getEnclosingInstancesSlotSize();
                argSlotSize += declaringClass.getOuterLocalVariablesSlotSize();
            } else {
                this.scope.computeLocalVariablePositions(1 + enumOffset, codeStream);
            }
            if (this.arguments != null) {
                int i2 = 0;
                int max2 = this.arguments.length;
                while (i2 < max2) {
                    LocalVariableBinding argBinding = this.arguments[i2].binding;
                    codeStream.addVisibleLocalVariable(argBinding);
                    argBinding.recordInitializationStartPC(0);
                    switch (argBinding.type.id) {
                        case 7: 
                        case 8: {
                            argSlotSize += 2;
                            break;
                        }
                        default: {
                            ++argSlotSize;
                        }
                    }
                    ++i2;
                }
            }
            MethodScope initializerScope = declaringType.initializerScope;
            initializerScope.computeLocalVariablePositions(argSlotSize, codeStream);
            boolean needFieldInitializations = this.constructorCall == null || this.constructorCall.accessMode != 3;
            boolean bl = preInitSyntheticFields = this.scope.compilerOptions().targetJDK >= 0x300000L;
            if (needFieldInitializations && preInitSyntheticFields) {
                this.generateSyntheticFieldInitializationsIfNecessary(this.scope, codeStream, declaringClass);
                codeStream.recordPositionsFrom(0, this.bodyStart > 0 ? this.bodyStart : this.sourceStart);
            }
            if (this.constructorCall != null) {
                this.constructorCall.generateCode(this.scope, codeStream);
            }
            if (needFieldInitializations) {
                if (!preInitSyntheticFields) {
                    this.generateSyntheticFieldInitializationsIfNecessary(this.scope, codeStream, declaringClass);
                }
                if (declaringType.fields != null) {
                    i = 0;
                    max = declaringType.fields.length;
                    while (i < max) {
                        FieldDeclaration fieldDecl = declaringType.fields[i];
                        if (!fieldDecl.isStatic()) {
                            fieldDecl.generateCode(initializerScope, codeStream);
                        }
                        ++i;
                    }
                }
            }
            if (this.statements != null) {
                i = 0;
                max = this.statements.length;
                while (i < max) {
                    this.statements[i].generateCode(this.scope, codeStream);
                    ++i;
                }
            }
            if (this.ignoreFurtherInvestigation) {
                throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, null);
            }
            if ((this.bits & 0x40) != 0) {
                codeStream.return_();
            }
            codeStream.exitUserScope(this.scope);
            codeStream.recordPositionsFrom(0, this.bodyEnd > 0 ? this.bodyEnd : this.sourceStart);
            try {
                classFile.completeCodeAttribute(codeAttributeOffset, this.scope);
            }
            catch (NegativeArraySizeException negativeArraySizeException) {
                throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, null);
            }
            ++attributeNumber;
            if (codeStream instanceof StackMapFrameCodeStream && needFieldInitializations && declaringType.fields != null) {
                ((StackMapFrameCodeStream)codeStream).resetSecretLocals();
            }
        }
        classFile.completeMethodInfo(this.binding, methodAttributeOffset, attributeNumber);
    }

    @Override
    protected AnnotationBinding[][] getPropagatedRecordComponentAnnotations() {
        if ((this.bits & 0x600) == 0) {
            return null;
        }
        if (this.binding == null) {
            return null;
        }
        AnnotationBinding[][] paramAnnotations = null;
        ReferenceBinding declaringClass = this.binding.declaringClass;
        if (declaringClass instanceof SourceTypeBinding) {
            assert (declaringClass.isRecord());
            RecordComponentBinding[] rcbs = ((SourceTypeBinding)declaringClass).components();
            int i = 0;
            int length = rcbs.length;
            while (i < length) {
                RecordComponentBinding rcb = rcbs[i];
                RecordComponent recordComponent = rcb.sourceRecordComponent();
                long rcMask = 0x20008000000000L;
                ArrayList<AnnotationBinding> relevantAnnotationBindings = new ArrayList<AnnotationBinding>();
                Annotation[] relevantAnnotations = ASTNode.getRelevantAnnotations(recordComponent.annotations, rcMask, relevantAnnotationBindings);
                if (relevantAnnotations != null) {
                    if (paramAnnotations == null) {
                        paramAnnotations = new AnnotationBinding[length][];
                        int j = 0;
                        while (j < i) {
                            paramAnnotations[j] = Binding.NO_ANNOTATIONS;
                            ++j;
                        }
                    }
                    this.binding.tagBits |= 0x400L;
                    paramAnnotations[i] = relevantAnnotationBindings.toArray(new AnnotationBinding[0]);
                } else if (paramAnnotations != null) {
                    paramAnnotations[i] = Binding.NO_ANNOTATIONS;
                }
                ++i;
            }
        }
        return paramAnnotations;
    }

    @Override
    public void getAllAnnotationContexts(int targetType, List allAnnotationContexts) {
        SingleTypeReference fakeReturnType = new SingleTypeReference(this.selector, 0L);
        fakeReturnType.resolvedType = this.binding.declaringClass;
        TypeReference.AnnotationCollector collector = new TypeReference.AnnotationCollector(fakeReturnType, targetType, (List<AnnotationContext>)allAnnotationContexts);
        int i = 0;
        int max = this.annotations.length;
        while (i < max) {
            Annotation annotation = this.annotations[i];
            annotation.traverse((ASTVisitor)collector, (BlockScope)null);
            ++i;
        }
    }

    @Override
    public boolean isConstructor() {
        return true;
    }

    @Override
    public boolean isCanonicalConstructor() {
        return (this.bits & 0x200) != 0;
    }

    @Override
    public boolean isDefaultConstructor() {
        return (this.bits & 0x80) != 0;
    }

    @Override
    public boolean isInitializationMethod() {
        return true;
    }

    public boolean isRecursive(ArrayList visited) {
        if (this.binding == null || this.constructorCall == null || this.constructorCall.binding == null || this.constructorCall.isSuperAccess() || !this.constructorCall.binding.isValidBinding()) {
            return false;
        }
        ConstructorDeclaration targetConstructor = (ConstructorDeclaration)this.scope.referenceType().declarationOf(this.constructorCall.binding.original());
        if (targetConstructor == null) {
            return false;
        }
        if (this == targetConstructor) {
            return true;
        }
        if (visited == null) {
            visited = new ArrayList<ConstructorDeclaration>(1);
        } else {
            int index = visited.indexOf(this);
            if (index >= 0) {
                return index == 0;
            }
        }
        visited.add(this);
        return targetConstructor.isRecursive(visited);
    }

    @Override
    public void parseStatements(Parser parser, CompilationUnitDeclaration unit) {
        if ((this.bits & 0x80) != 0 && this.constructorCall == null) {
            this.constructorCall = SuperReference.implicitSuperConstructorCall();
            this.constructorCall.sourceStart = this.sourceStart;
            this.constructorCall.sourceEnd = this.sourceEnd;
            return;
        }
        parser.parse(this, unit, false);
        this.containsSwitchWithTry = parser.switchWithTry;
    }

    @Override
    public StringBuffer printBody(int indent, StringBuffer output) {
        output.append(" {");
        if (this.constructorCall != null) {
            output.append('\n');
            this.constructorCall.printStatement(indent, output);
        }
        if (this.statements != null) {
            int i = 0;
            while (i < this.statements.length) {
                output.append('\n');
                this.statements[i].printStatement(indent, output);
                ++i;
            }
        }
        output.append('\n');
        ConstructorDeclaration.printIndent(indent == 0 ? 0 : indent - 1, output).append('}');
        return output;
    }

    @Override
    public void resolveJavadoc() {
        if (this.binding == null || this.javadoc != null) {
            super.resolveJavadoc();
        } else if ((this.bits & 0x80) == 0) {
            if ((this.bits & 0x400) != 0) {
                return;
            }
            if (this.binding.declaringClass != null && !this.binding.declaringClass.isLocalType()) {
                int javadocVisibility = this.binding.modifiers & 7;
                ClassScope classScope = this.scope.classScope();
                ProblemReporter reporter = this.scope.problemReporter();
                int severity = reporter.computeSeverity(-1610612250);
                if (severity != 256) {
                    if (classScope != null) {
                        javadocVisibility = Util.computeOuterMostVisibility(classScope.referenceType(), javadocVisibility);
                    }
                    int javadocModifiers = this.binding.modifiers & 0xFFFFFFF8 | javadocVisibility;
                    reporter.javadocMissing(this.sourceStart, this.sourceEnd, severity, javadocModifiers);
                }
            }
        }
    }

    @Override
    public void resolveStatements() {
        SourceTypeBinding sourceType = this.scope.enclosingSourceType();
        if (!CharOperation.equals(sourceType.sourceName, this.selector)) {
            this.scope.problemReporter().missingReturnType(this);
        }
        if (this.binding != null && !this.binding.isPrivate()) {
            sourceType.tagBits |= 0x1000000000000000L;
        }
        if (this.constructorCall != null) {
            if (sourceType.id == 1 && this.constructorCall.accessMode != 3) {
                if (this.constructorCall.accessMode == 2) {
                    this.scope.problemReporter().cannotUseSuperInJavaLangObject(this.constructorCall);
                }
                this.constructorCall = null;
            } else if (sourceType.isRecord() && !(this instanceof CompactConstructorDeclaration) && this.binding != null && (this.binding.tagBits & 0x800L) == 0L && this.constructorCall.accessMode != 3) {
                this.scope.problemReporter().recordMissingExplicitConstructorCallInNonCanonicalConstructor(this);
                this.constructorCall = null;
            } else {
                this.constructorCall.resolve(this.scope);
            }
        }
        if ((this.modifiers & 0x1000000) != 0) {
            this.scope.problemReporter().methodNeedBody(this);
        }
        super.resolveStatements();
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope classScope) {
        if (visitor.visit(this, classScope)) {
            int i;
            if (this.javadoc != null) {
                this.javadoc.traverse(visitor, this.scope);
            }
            if (this.annotations != null) {
                int annotationsLength = this.annotations.length;
                i = 0;
                while (i < annotationsLength) {
                    this.annotations[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
            if (this.typeParameters != null) {
                int typeParametersLength = this.typeParameters.length;
                i = 0;
                while (i < typeParametersLength) {
                    this.typeParameters[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
            if (this.arguments != null) {
                int argumentLength = this.arguments.length;
                i = 0;
                while (i < argumentLength) {
                    this.arguments[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
            if (this.thrownExceptions != null) {
                int thrownExceptionsLength = this.thrownExceptions.length;
                i = 0;
                while (i < thrownExceptionsLength) {
                    this.thrownExceptions[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
            if (this.constructorCall != null) {
                this.constructorCall.traverse(visitor, this.scope);
            }
            if (this.statements != null) {
                int statementsLength = this.statements.length;
                i = 0;
                while (i < statementsLength) {
                    this.statements[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
        }
        visitor.endVisit(this, classScope);
    }

    @Override
    public TypeParameter[] typeParameters() {
        return this.typeParameters;
    }
}

