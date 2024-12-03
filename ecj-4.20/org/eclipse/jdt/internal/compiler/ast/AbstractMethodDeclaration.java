/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.List;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.Receiver;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.problem.AbortType;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.util.Util;

public abstract class AbstractMethodDeclaration
extends ASTNode
implements ProblemSeverities,
ReferenceContext {
    public MethodScope scope;
    public char[] selector;
    public int declarationSourceStart;
    public int declarationSourceEnd;
    public int modifiers;
    public int modifiersSourceStart;
    public Annotation[] annotations;
    public Receiver receiver;
    public Argument[] arguments;
    public TypeReference[] thrownExceptions;
    public Statement[] statements;
    public int explicitDeclarations;
    public MethodBinding binding;
    public boolean ignoreFurtherInvestigation = false;
    public Javadoc javadoc;
    public int bodyStart;
    public int bodyEnd = -1;
    public CompilationResult compilationResult;
    public boolean containsSwitchWithTry = false;

    AbstractMethodDeclaration(CompilationResult compilationResult) {
        this.compilationResult = compilationResult;
        this.containsSwitchWithTry = false;
    }

    @Override
    public void abort(int abortLevel, CategorizedProblem problem) {
        switch (abortLevel) {
            case 2: {
                throw new AbortCompilation(this.compilationResult, problem);
            }
            case 4: {
                throw new AbortCompilationUnit(this.compilationResult, problem);
            }
            case 8: {
                throw new AbortType(this.compilationResult, problem);
            }
        }
        throw new AbortMethod(this.compilationResult, problem);
    }

    public void createArgumentBindings() {
        AbstractMethodDeclaration.createArgumentBindings(this.arguments, this.binding, this.scope);
    }

    static void createArgumentBindings(Argument[] arguments, MethodBinding binding, MethodScope scope) {
        boolean useTypeAnnotations = scope.environment().usesNullTypeAnnotations();
        if (arguments != null && binding != null) {
            int i = 0;
            int length = arguments.length;
            while (i < length) {
                long argTypeTagBits;
                Argument argument = arguments[i];
                binding.parameters[i] = argument.createBinding(scope, binding.parameters[i]);
                if (!useTypeAnnotations && (argTypeTagBits = argument.binding.tagBits & 0x180000000000000L) != 0L) {
                    if (binding.parameterNonNullness == null) {
                        binding.parameterNonNullness = new Boolean[arguments.length];
                        binding.tagBits |= 0x1000L;
                    }
                    binding.parameterNonNullness[i] = argTypeTagBits == 0x100000000000000L;
                }
                ++i;
            }
        }
    }

    public void bindArguments() {
        if (this.arguments != null) {
            if (this.binding == null) {
                int i = 0;
                int length = this.arguments.length;
                while (i < length) {
                    this.arguments[i].bind(this.scope, null, true);
                    ++i;
                }
                return;
            }
            boolean used = this.binding.isAbstract() || this.binding.isNative();
            AnnotationBinding[][] paramAnnotations = null;
            int i = 0;
            int length = this.arguments.length;
            while (i < length) {
                Argument argument = this.arguments[i];
                this.binding.parameters[i] = argument.bind(this.scope, this.binding.parameters[i], used);
                if (argument.annotations != null) {
                    if (paramAnnotations == null) {
                        paramAnnotations = new AnnotationBinding[length][];
                        int j = 0;
                        while (j < i) {
                            paramAnnotations[j] = Binding.NO_ANNOTATIONS;
                            ++j;
                        }
                    }
                    paramAnnotations[i] = argument.binding.getAnnotations();
                } else if (paramAnnotations != null) {
                    paramAnnotations[i] = Binding.NO_ANNOTATIONS;
                }
                ++i;
            }
            if (paramAnnotations == null) {
                paramAnnotations = this.getPropagatedRecordComponentAnnotations();
            }
            if (paramAnnotations != null) {
                this.binding.setParameterAnnotations(paramAnnotations);
            }
        }
    }

    protected AnnotationBinding[][] getPropagatedRecordComponentAnnotations() {
        return null;
    }

    public void bindThrownExceptions() {
        block9: {
            if (this.thrownExceptions == null || this.binding == null || this.binding.thrownExceptions == null) break block9;
            int length = this.binding.thrownExceptions.length;
            int thrownExceptionLength = this.thrownExceptions.length;
            if (length == thrownExceptionLength) {
                int i = 0;
                while (i < length) {
                    this.thrownExceptions[i].resolvedType = this.binding.thrownExceptions[i];
                    ++i;
                }
            } else {
                int bindingIndex = 0;
                int i = 0;
                while (i < thrownExceptionLength && bindingIndex < length) {
                    TypeReference thrownException = this.thrownExceptions[i];
                    ReferenceBinding thrownExceptionBinding = this.binding.thrownExceptions[bindingIndex];
                    char[][] bindingCompoundName = thrownExceptionBinding.compoundName;
                    if (bindingCompoundName != null) {
                        if (thrownException instanceof SingleTypeReference) {
                            int lengthName = bindingCompoundName.length;
                            char[] thrownExceptionTypeName = thrownException.getTypeName()[0];
                            if (CharOperation.equals(thrownExceptionTypeName, bindingCompoundName[lengthName - 1])) {
                                thrownException.resolvedType = thrownExceptionBinding;
                                ++bindingIndex;
                            }
                        } else if (CharOperation.equals(thrownException.getTypeName(), bindingCompoundName)) {
                            thrownException.resolvedType = thrownExceptionBinding;
                            ++bindingIndex;
                        }
                    }
                    ++i;
                }
            }
        }
    }

    static void analyseArguments(LookupEnvironment environment, FlowInfo flowInfo, Argument[] methodArguments, MethodBinding methodBinding) {
        if (methodArguments != null) {
            boolean usesNullTypeAnnotations = environment.usesNullTypeAnnotations();
            int length = Math.min(methodBinding.parameters.length, methodArguments.length);
            int i = 0;
            while (i < length) {
                Boolean nonNullNess;
                if (usesNullTypeAnnotations) {
                    long tagBits = methodBinding.parameters[i].tagBits & 0x180000000000000L;
                    if (tagBits == 0x100000000000000L) {
                        flowInfo.markAsDefinitelyNonNull(methodArguments[i].binding);
                    } else if (tagBits == 0x80000000000000L) {
                        flowInfo.markPotentiallyNullBit(methodArguments[i].binding);
                    } else if (methodBinding.parameters[i].isFreeTypeVariable()) {
                        flowInfo.markNullStatus(methodArguments[i].binding, 48);
                    }
                } else if (methodBinding.parameterNonNullness != null && (nonNullNess = methodBinding.parameterNonNullness[i]) != null) {
                    if (nonNullNess.booleanValue()) {
                        flowInfo.markAsDefinitelyNonNull(methodArguments[i].binding);
                    } else {
                        flowInfo.markPotentiallyNullBit(methodArguments[i].binding);
                    }
                }
                flowInfo.markAsDefinitelyAssigned(methodArguments[i].binding);
                ++i;
            }
        }
    }

    @Override
    public CompilationResult compilationResult() {
        return this.compilationResult;
    }

    public void generateCode(ClassScope classScope, ClassFile classFile) {
        TypeDeclaration referenceContext;
        classFile.codeStream.wideMode = false;
        if (this.ignoreFurtherInvestigation) {
            if (this.binding == null) {
                return;
            }
            CategorizedProblem[] problems = this.scope.referenceCompilationUnit().compilationResult.getProblems();
            int problemsLength = problems.length;
            CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength];
            System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
            classFile.addProblemMethod(this, this.binding, problemsCopy);
            return;
        }
        int problemResetPC = 0;
        CompilationResult unitResult = null;
        int problemCount = 0;
        if (classScope != null && (referenceContext = classScope.referenceContext) != null) {
            unitResult = referenceContext.compilationResult();
            problemCount = unitResult.problemCount;
        }
        boolean restart = false;
        boolean abort = false;
        do {
            try {
                problemResetPC = classFile.contentsOffset;
                this.generateCode(classFile);
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
            classFile.addProblemMethod(this, this.binding, problemsCopy, problemResetPC);
        }
    }

    public void generateCode(ClassFile classFile) {
        classFile.generateMethodInfoHeader(this.binding);
        int methodAttributeOffset = classFile.contentsOffset;
        int attributeNumber = classFile.generateMethodInfoAttributes(this.binding);
        if (!this.binding.isNative() && !this.binding.isAbstract()) {
            int codeAttributeOffset = classFile.contentsOffset;
            classFile.generateCodeAttributeHeader();
            CodeStream codeStream = classFile.codeStream;
            codeStream.reset(this, classFile);
            this.scope.computeLocalVariablePositions(this.binding.isStatic() ? 0 : 1, codeStream);
            if (this.arguments != null) {
                int i = 0;
                int max = this.arguments.length;
                while (i < max) {
                    LocalVariableBinding argBinding = this.arguments[i].binding;
                    codeStream.addVisibleLocalVariable(argBinding);
                    argBinding.recordInitializationStartPC(0);
                    ++i;
                }
            }
            if (this.statements != null) {
                Statement[] statementArray = this.statements;
                int n = this.statements.length;
                int n2 = 0;
                while (n2 < n) {
                    Statement stmt = statementArray[n2];
                    stmt.generateCode(this.scope, codeStream);
                    ++n2;
                }
            }
            if (this.ignoreFurtherInvestigation) {
                throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, null);
            }
            if ((this.bits & 0x40) != 0) {
                codeStream.return_();
            }
            codeStream.exitUserScope(this.scope);
            codeStream.recordPositionsFrom(0, this.declarationSourceEnd);
            try {
                classFile.completeCodeAttribute(codeAttributeOffset, this.scope);
            }
            catch (NegativeArraySizeException negativeArraySizeException) {
                throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, null);
            }
            ++attributeNumber;
        } else {
            this.checkArgumentsSize();
        }
        classFile.completeMethodInfo(this.binding, methodAttributeOffset, attributeNumber);
    }

    public void getAllAnnotationContexts(int targetType, List allAnnotationContexts) {
    }

    private void checkArgumentsSize() {
        TypeBinding[] parameters = this.binding.parameters;
        int size = 1;
        int i = 0;
        int max = parameters.length;
        while (i < max) {
            switch (parameters[i].id) {
                case 7: 
                case 8: {
                    size += 2;
                    break;
                }
                default: {
                    ++size;
                }
            }
            if (size > 255) {
                this.scope.problemReporter().noMoreAvailableSpaceForArgument(this.scope.locals[i], this.scope.locals[i].declaration);
            }
            ++i;
        }
    }

    @Override
    public CompilationUnitDeclaration getCompilationUnitDeclaration() {
        if (this.scope != null) {
            return this.scope.compilationUnitScope().referenceContext;
        }
        return null;
    }

    @Override
    public boolean hasErrors() {
        return this.ignoreFurtherInvestigation;
    }

    public boolean isAbstract() {
        if (this.binding != null) {
            return this.binding.isAbstract();
        }
        return (this.modifiers & 0x400) != 0;
    }

    public boolean isAnnotationMethod() {
        return false;
    }

    public boolean isClinit() {
        return false;
    }

    public boolean isConstructor() {
        return false;
    }

    public boolean isCanonicalConstructor() {
        return false;
    }

    public boolean isDefaultConstructor() {
        return false;
    }

    public boolean isDefaultMethod() {
        return false;
    }

    public boolean isInitializationMethod() {
        return false;
    }

    public boolean isMethod() {
        return false;
    }

    public boolean isNative() {
        if (this.binding != null) {
            return this.binding.isNative();
        }
        return (this.modifiers & 0x100) != 0;
    }

    public RecordComponent getRecordComponent() {
        return null;
    }

    public boolean isStatic() {
        if (this.binding != null) {
            return this.binding.isStatic();
        }
        return (this.modifiers & 8) != 0;
    }

    public abstract void parseStatements(Parser var1, CompilationUnitDeclaration var2);

    @Override
    public StringBuffer print(int tab, StringBuffer output) {
        int i;
        TypeParameter[] typeParams;
        if (this.javadoc != null) {
            this.javadoc.print(tab, output);
        }
        AbstractMethodDeclaration.printIndent(tab, output);
        AbstractMethodDeclaration.printModifiers(this.modifiers, output);
        if (this.annotations != null) {
            AbstractMethodDeclaration.printAnnotations(this.annotations, output);
            output.append(' ');
        }
        if ((typeParams = this.typeParameters()) != null) {
            output.append('<');
            int max = typeParams.length - 1;
            int j = 0;
            while (j < max) {
                typeParams[j].print(0, output);
                output.append(", ");
                ++j;
            }
            typeParams[max].print(0, output);
            output.append('>');
        }
        this.printReturnType(0, output).append(this.selector).append('(');
        if (this.receiver != null) {
            this.receiver.print(0, output);
        }
        if (this.arguments != null) {
            i = 0;
            while (i < this.arguments.length) {
                if (i > 0 || this.receiver != null) {
                    output.append(", ");
                }
                this.arguments[i].print(0, output);
                ++i;
            }
        }
        output.append(')');
        if (this.thrownExceptions != null) {
            output.append(" throws ");
            i = 0;
            while (i < this.thrownExceptions.length) {
                if (i > 0) {
                    output.append(", ");
                }
                this.thrownExceptions[i].print(0, output);
                ++i;
            }
        }
        this.printBody(tab + 1, output);
        return output;
    }

    public StringBuffer printBody(int indent, StringBuffer output) {
        if (this.isAbstract() || (this.modifiers & 0x1000000) != 0) {
            return output.append(';');
        }
        output.append(" {");
        if (this.statements != null) {
            int i = 0;
            while (i < this.statements.length) {
                output.append('\n');
                this.statements[i].printStatement(indent, output);
                ++i;
            }
        }
        output.append('\n');
        AbstractMethodDeclaration.printIndent(indent == 0 ? 0 : indent - 1, output).append('}');
        return output;
    }

    public StringBuffer printReturnType(int indent, StringBuffer output) {
        return output;
    }

    public void resolve(ClassScope upperScope) {
        if (this.binding == null) {
            this.ignoreFurtherInvestigation = true;
        }
        try {
            this.bindArguments();
            this.resolveReceiver();
            this.bindThrownExceptions();
            AbstractMethodDeclaration.resolveAnnotations(this.scope, this.annotations, this.binding, this.isConstructor());
            long sourceLevel = this.scope.compilerOptions().sourceLevel;
            if (sourceLevel < 0x340000L) {
                this.validateNullAnnotations(this.scope.environment().usesNullTypeAnnotations());
            }
            this.resolveStatements();
            if (this.binding != null && (this.binding.getAnnotationTagBits() & 0x400000000000L) == 0L && (this.binding.modifiers & 0x100000) != 0 && sourceLevel >= 0x310000L) {
                this.scope.problemReporter().missingDeprecatedAnnotationForMethod(this);
            }
        }
        catch (AbortMethod abortMethod) {
            this.ignoreFurtherInvestigation = true;
        }
    }

    public void resolveReceiver() {
        char[][] tokens;
        if (this.receiver == null) {
            return;
        }
        if (this.receiver.modifiers != 0) {
            this.scope.problemReporter().illegalModifiers(this.receiver.declarationSourceStart, this.receiver.declarationSourceEnd);
        }
        TypeBinding resolvedReceiverType = this.receiver.type.resolvedType;
        if (this.binding == null || resolvedReceiverType == null || !resolvedReceiverType.isValidBinding()) {
            return;
        }
        ReferenceBinding declaringClass = this.binding.declaringClass;
        if (this.isStatic() || declaringClass.isAnonymousType()) {
            this.scope.problemReporter().disallowedThisParameter(this.receiver);
            return;
        }
        ReferenceBinding enclosingReceiver = this.scope.enclosingReceiverType();
        if (this.isConstructor()) {
            if (declaringClass.isStatic() || (declaringClass.tagBits & 0x18L) == 0L) {
                this.scope.problemReporter().disallowedThisParameter(this.receiver);
                return;
            }
            enclosingReceiver = enclosingReceiver.enclosingType();
        }
        char[][] cArray = tokens = this.receiver.qualifyingName == null ? null : this.receiver.qualifyingName.getName();
        if (this.isConstructor()) {
            if (tokens == null || tokens.length > 1 || !CharOperation.equals(enclosingReceiver.sourceName(), tokens[0])) {
                this.scope.problemReporter().illegalQualifierForExplicitThis(this.receiver, enclosingReceiver);
                this.receiver.qualifyingName = null;
            }
        } else if (tokens != null && tokens.length > 0) {
            this.scope.problemReporter().illegalQualifierForExplicitThis2(this.receiver);
            this.receiver.qualifyingName = null;
        }
        if (TypeBinding.notEquals(enclosingReceiver, resolvedReceiverType)) {
            this.scope.problemReporter().illegalTypeForExplicitThis(this.receiver, enclosingReceiver);
        }
        if (this.receiver.type.hasNullTypeAnnotation(TypeReference.AnnotationPosition.ANY)) {
            this.scope.problemReporter().nullAnnotationUnsupportedLocation(this.receiver.type);
        }
    }

    public void resolveJavadoc() {
        if (this.binding == null) {
            return;
        }
        if (this.javadoc != null) {
            this.javadoc.resolve(this.scope);
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

    public void resolveStatements() {
        if (this.statements != null) {
            int i = 0;
            int length = this.statements.length;
            while (i < length) {
                Statement stmt = this.statements[i];
                stmt.resolve(this.scope);
                ++i;
            }
        } else if (!((this.bits & 8) == 0 || this.isConstructor() && this.arguments == null)) {
            this.scope.problemReporter().undocumentedEmptyBlock(this.bodyStart - 1, this.bodyEnd + 1);
        }
    }

    @Override
    public void tagAsHavingErrors() {
        this.ignoreFurtherInvestigation = true;
    }

    @Override
    public void tagAsHavingIgnoredMandatoryErrors(int problemId) {
    }

    public void traverse(ASTVisitor visitor, ClassScope classScope) {
    }

    public TypeParameter[] typeParameters() {
        return null;
    }

    void validateNullAnnotations(boolean useTypeAnnotations) {
        block6: {
            block5: {
                if (this.binding == null) {
                    return;
                }
                if (useTypeAnnotations) break block5;
                if (this.binding.parameterNonNullness == null) break block6;
                int length = this.binding.parameters.length;
                int i = 0;
                while (i < length) {
                    if (this.binding.parameterNonNullness[i] != null) {
                        long nullAnnotationTagBit;
                        long l = nullAnnotationTagBit = this.binding.parameterNonNullness[i] != false ? 0x100000000000000L : 0x80000000000000L;
                        if (!this.scope.validateNullAnnotation(nullAnnotationTagBit, this.arguments[i].type, this.arguments[i].annotations)) {
                            this.binding.parameterNonNullness[i] = null;
                        }
                    }
                    ++i;
                }
                break block6;
            }
            int length = this.binding.parameters.length;
            int i = 0;
            while (i < length) {
                this.scope.validateNullAnnotation(this.binding.parameters[i].tagBits, this.arguments[i].type, this.arguments[i].annotations);
                ++i;
            }
        }
    }
}

