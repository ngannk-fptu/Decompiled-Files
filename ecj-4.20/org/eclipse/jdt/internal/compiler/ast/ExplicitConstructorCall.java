/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CompactConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

public class ExplicitConstructorCall
extends Statement
implements Invocation {
    public Expression[] arguments;
    public Expression qualification;
    public MethodBinding binding;
    MethodBinding syntheticAccessor;
    public int accessMode;
    public TypeReference[] typeArguments;
    public TypeBinding[] genericTypeArguments;
    public static final int ImplicitSuper = 1;
    public static final int Super = 2;
    public static final int This = 3;
    public VariableBinding[][] implicitArguments;
    public int typeArgumentsSourceStart;

    public ExplicitConstructorCall(int accessMode) {
        this.accessMode = accessMode;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        try {
            ((MethodScope)currentScope).isConstructorCall = true;
            if (this.qualification != null) {
                flowInfo = this.qualification.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            }
            if (this.arguments != null) {
                boolean analyseResources = currentScope.compilerOptions().analyseResourceLeaks;
                int i = 0;
                int max = this.arguments.length;
                while (i < max) {
                    flowInfo = this.arguments[i].analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
                    if (analyseResources) {
                        flowInfo = FakedTrackingVariable.markPassedToOutside(currentScope, this.arguments[i], flowInfo, flowContext, false);
                    }
                    this.arguments[i].checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
                    ++i;
                }
                this.analyseArguments(currentScope, flowContext, flowInfo, this.binding, this.arguments);
            }
            TypeBinding[] thrownExceptions = this.binding.thrownExceptions;
            if (this.binding.thrownExceptions != Binding.NO_EXCEPTIONS) {
                if ((this.bits & 0x10000) != 0 && this.genericTypeArguments == null) {
                    thrownExceptions = currentScope.environment().convertToRawTypes(this.binding.thrownExceptions, true, true);
                }
                flowContext.checkExceptionHandlers(thrownExceptions, this.accessMode == 1 ? (ASTNode)((Object)currentScope.methodScope().referenceContext) : this, flowInfo, currentScope);
            }
            this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
            this.manageSyntheticAccessIfNecessary(currentScope, flowInfo);
            FlowInfo flowInfo2 = flowInfo;
            return flowInfo2;
        }
        finally {
            ((MethodScope)currentScope).isConstructorCall = false;
        }
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        try {
            ((MethodScope)currentScope).isConstructorCall = true;
            int pc = codeStream.position;
            codeStream.aload_0();
            MethodBinding codegenBinding = this.binding.original();
            ReferenceBinding targetType = codegenBinding.declaringClass;
            if (targetType.erasure().id == 41 || targetType.isEnum()) {
                codeStream.aload_1();
                codeStream.iload_2();
            }
            if (targetType.isNestedType()) {
                codeStream.generateSyntheticEnclosingInstanceValues(currentScope, targetType, (this.bits & 0x2000) != 0 ? null : this.qualification, this);
            }
            this.generateArguments(this.binding, this.arguments, currentScope, codeStream);
            if (targetType.isNestedType()) {
                codeStream.generateSyntheticOuterArgumentValues(currentScope, targetType, this);
            }
            if (this.syntheticAccessor != null) {
                int i = 0;
                int max = this.syntheticAccessor.parameters.length - codegenBinding.parameters.length;
                while (i < max) {
                    codeStream.aconst_null();
                    ++i;
                }
                codeStream.invoke((byte)-73, this.syntheticAccessor, null, this.typeArguments);
            } else {
                codeStream.invoke((byte)-73, codegenBinding, null, this.typeArguments);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
        }
        finally {
            ((MethodScope)currentScope).isConstructorCall = false;
        }
    }

    @Override
    public TypeBinding[] genericTypeArguments() {
        return this.genericTypeArguments;
    }

    public boolean isImplicitSuper() {
        return this.accessMode == 1;
    }

    @Override
    public boolean isSuperAccess() {
        return this.accessMode != 3;
    }

    @Override
    public boolean isTypeAccess() {
        return true;
    }

    void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
        ReferenceBinding superTypeErasure = (ReferenceBinding)this.binding.declaringClass.erasure();
        if ((flowInfo.tagBits & 1) == 0 && superTypeErasure.isNestedType() && currentScope.enclosingSourceType().isLocalType()) {
            if (superTypeErasure.isLocalType()) {
                ((LocalTypeBinding)superTypeErasure).addInnerEmulationDependent(currentScope, this.qualification != null);
            } else {
                currentScope.propagateInnerEmulation(superTypeErasure, this.qualification != null);
            }
        }
    }

    public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 1) == 0) {
            MethodBinding codegenBinding = this.binding.original();
            if (this.binding.isPrivate() && !currentScope.enclosingSourceType().isNestmateOf(this.binding.declaringClass) && this.accessMode != 3) {
                ReferenceBinding declaringClass = codegenBinding.declaringClass;
                if ((declaringClass.tagBits & 0x10L) != 0L && currentScope.compilerOptions().complianceLevel >= 0x300000L) {
                    codegenBinding.tagBits |= 0x200L;
                } else {
                    this.syntheticAccessor = ((SourceTypeBinding)declaringClass).addSyntheticMethod(codegenBinding, this.isSuperAccess());
                    currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
                }
            }
        }
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {
        ExplicitConstructorCall.printIndent(indent, output);
        if (this.qualification != null) {
            this.qualification.printExpression(0, output).append('.');
        }
        if (this.typeArguments != null) {
            output.append('<');
            int max = this.typeArguments.length - 1;
            int j = 0;
            while (j < max) {
                this.typeArguments[j].print(0, output);
                output.append(", ");
                ++j;
            }
            this.typeArguments[max].print(0, output);
            output.append('>');
        }
        if (this.accessMode == 3) {
            output.append("this(");
        } else {
            output.append("super(");
        }
        if (this.arguments != null) {
            int i = 0;
            while (i < this.arguments.length) {
                if (i > 0) {
                    output.append(", ");
                }
                this.arguments[i].printExpression(0, output);
                ++i;
            }
        }
        return output.append(");");
    }

    @Override
    public void resolve(BlockScope scope) {
        MethodScope methodScope = scope.methodScope();
        try {
            AbstractMethodDeclaration methodDeclaration = methodScope.referenceMethod();
            if (methodDeclaration != null && methodDeclaration.binding != null && (methodDeclaration.binding.tagBits & 0x800L) != 0L && !this.checkAndFlagExplicitConstructorCallInCanonicalConstructor(methodDeclaration, scope)) {
                return;
            }
            if (methodDeclaration == null || !methodDeclaration.isConstructor() || ((ConstructorDeclaration)methodDeclaration).constructorCall != this) {
                int max;
                int i;
                if (!(methodDeclaration instanceof CompactConstructorDeclaration)) {
                    scope.problemReporter().invalidExplicitConstructorCall(this);
                }
                if (this.qualification != null) {
                    this.qualification.resolveType(scope);
                }
                if (this.typeArguments != null) {
                    i = 0;
                    max = this.typeArguments.length;
                    while (i < max) {
                        this.typeArguments[i].resolveType(scope, true);
                        ++i;
                    }
                }
                if (this.arguments != null) {
                    i = 0;
                    max = this.arguments.length;
                    while (i < max) {
                        this.arguments[i].resolveType(scope);
                        ++i;
                    }
                }
                return;
            }
            methodScope.isConstructorCall = true;
            ReferenceBinding receiverType = scope.enclosingReceiverType();
            boolean rcvHasError = false;
            if (this.accessMode != 3) {
                receiverType = receiverType.superclass();
                TypeReference superclassRef = scope.referenceType().superclass;
                if (superclassRef != null && superclassRef.resolvedType != null && !superclassRef.resolvedType.isValidBinding()) {
                    rcvHasError = true;
                }
            }
            if (receiverType != null) {
                if (this.accessMode == 2 && receiverType.erasure().id == 41) {
                    scope.problemReporter().cannotInvokeSuperConstructorInEnum(this, methodScope.referenceMethod().binding);
                }
                if (this.qualification != null) {
                    if (this.accessMode != 2) {
                        scope.problemReporter().unnecessaryEnclosingInstanceSpecification(this.qualification, receiverType);
                    }
                    if (!rcvHasError) {
                        ReferenceBinding enclosingType = receiverType.enclosingType();
                        if (enclosingType == null) {
                            scope.problemReporter().unnecessaryEnclosingInstanceSpecification(this.qualification, receiverType);
                            this.bits |= 0x2000;
                        } else {
                            TypeBinding qTb = this.qualification.resolveTypeExpecting(scope, enclosingType);
                            this.qualification.computeConversion(scope, qTb, qTb);
                        }
                    }
                }
            }
            long sourceLevel = scope.compilerOptions().sourceLevel;
            if (this.typeArguments != null) {
                boolean argHasError = sourceLevel < 0x310000L;
                int length = this.typeArguments.length;
                this.genericTypeArguments = new TypeBinding[length];
                int i = 0;
                while (i < length) {
                    TypeReference typeReference = this.typeArguments[i];
                    this.genericTypeArguments[i] = typeReference.resolveType(scope, true);
                    if (this.genericTypeArguments[i] == null) {
                        argHasError = true;
                    }
                    if (argHasError && typeReference instanceof Wildcard) {
                        scope.problemReporter().illegalUsageOfWildcard(typeReference);
                    }
                    ++i;
                }
                if (argHasError) {
                    if (this.arguments != null) {
                        i = 0;
                        int max = this.arguments.length;
                        while (i < max) {
                            this.arguments[i].resolveType(scope);
                            ++i;
                        }
                    }
                    return;
                }
            }
            TypeBinding[] argumentTypes = Binding.NO_PARAMETERS;
            boolean argsContainCast = false;
            if (this.arguments != null) {
                boolean argHasError = false;
                int length = this.arguments.length;
                argumentTypes = new TypeBinding[length];
                int i = 0;
                while (i < length) {
                    Expression argument = this.arguments[i];
                    if (argument instanceof CastExpression) {
                        argument.bits |= 0x20;
                        argsContainCast = true;
                    }
                    argument.setExpressionContext(ExpressionContext.INVOCATION_CONTEXT);
                    argumentTypes[i] = argument.resolveType(scope);
                    if (argumentTypes[i] == null) {
                        argHasError = true;
                    }
                    ++i;
                }
                if (argHasError) {
                    MethodBinding closestMatch;
                    if (receiverType == null) {
                        return;
                    }
                    TypeBinding[] pseudoArgs = new TypeBinding[length];
                    int i2 = length;
                    while (--i2 >= 0) {
                        TypeBinding typeBinding = pseudoArgs[i2] = argumentTypes[i2] == null ? TypeBinding.NULL : argumentTypes[i2];
                    }
                    this.binding = scope.findMethod(receiverType, TypeConstants.INIT, pseudoArgs, this, false);
                    if (this.binding != null && !this.binding.isValidBinding() && (closestMatch = ((ProblemMethodBinding)this.binding).closestMatch) != null) {
                        if (closestMatch.original().typeVariables != Binding.NO_TYPE_VARIABLES) {
                            closestMatch = scope.environment().createParameterizedGenericMethod(closestMatch.original(), (RawTypeBinding)null);
                        }
                        this.binding = closestMatch;
                        MethodBinding closestMatchOriginal = closestMatch.original();
                        if (closestMatchOriginal.isOrEnclosedByPrivateType() && !scope.isDefinedInMethod(closestMatchOriginal)) {
                            closestMatchOriginal.modifiers |= 0x8000000;
                        }
                    }
                    return;
                }
            } else if (receiverType.erasure().id == 41) {
                argumentTypes = new TypeBinding[]{scope.getJavaLangString(), TypeBinding.INT};
            }
            if (receiverType == null) {
                return;
            }
            this.binding = this.findConstructorBinding(scope, this, receiverType, argumentTypes);
            if (this.binding.isValidBinding()) {
                if ((this.binding.tagBits & 0x80L) != 0L && !methodScope.enclosingSourceType().isAnonymousType()) {
                    scope.problemReporter().missingTypeInConstructor(this, this.binding);
                }
                if (this.isMethodUseDeprecated(this.binding, scope, this.accessMode != 1, this)) {
                    scope.problemReporter().deprecatedMethod(this.binding, this);
                }
                if (ExplicitConstructorCall.checkInvocationArguments(scope, null, receiverType, this.binding, this.arguments, argumentTypes, argsContainCast, this)) {
                    this.bits |= 0x10000;
                }
                if (this.binding.isOrEnclosedByPrivateType()) {
                    this.binding.original().modifiers |= 0x8000000;
                }
                if (this.typeArguments != null && this.binding.original().typeVariables == Binding.NO_TYPE_VARIABLES) {
                    scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(this.binding, this.genericTypeArguments, this.typeArguments);
                }
            } else {
                if (this.binding.declaringClass == null) {
                    this.binding.declaringClass = receiverType;
                }
                if (rcvHasError) {
                    return;
                }
                scope.problemReporter().invalidConstructor(this, this.binding);
            }
        }
        finally {
            methodScope.isConstructorCall = false;
        }
    }

    private boolean checkAndFlagExplicitConstructorCallInCanonicalConstructor(AbstractMethodDeclaration methodDecl, BlockScope scope) {
        if (methodDecl.binding == null || methodDecl.binding.declaringClass == null || !methodDecl.binding.declaringClass.isRecord()) {
            return true;
        }
        boolean isInsideCCD = methodDecl instanceof CompactConstructorDeclaration;
        if (this.accessMode != 1) {
            if (isInsideCCD) {
                scope.problemReporter().recordCompactConstructorHasExplicitConstructorCall(this);
            } else {
                scope.problemReporter().recordCanonicalConstructorHasExplicitConstructorCall(this);
            }
            return false;
        }
        return true;
    }

    @Override
    public void setActualReceiverType(ReferenceBinding receiverType) {
    }

    @Override
    public void setDepth(int depth) {
    }

    @Override
    public void setFieldIndex(int depth) {
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            int i;
            if (this.qualification != null) {
                this.qualification.traverse(visitor, scope);
            }
            if (this.typeArguments != null) {
                i = 0;
                int typeArgumentsLength = this.typeArguments.length;
                while (i < typeArgumentsLength) {
                    this.typeArguments[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.arguments != null) {
                i = 0;
                int argumentLength = this.arguments.length;
                while (i < argumentLength) {
                    this.arguments[i].traverse(visitor, scope);
                    ++i;
                }
            }
        }
        visitor.endVisit(this, scope);
    }

    @Override
    public MethodBinding binding() {
        return this.binding;
    }

    @Override
    public void registerInferenceContext(ParameterizedGenericMethodBinding method, InferenceContext18 infCtx18) {
    }

    @Override
    public void registerResult(TypeBinding targetType, MethodBinding method) {
    }

    @Override
    public InferenceContext18 getInferenceContext(ParameterizedMethodBinding method) {
        return null;
    }

    @Override
    public void cleanUpInferenceContexts() {
    }

    @Override
    public Expression[] arguments() {
        return this.arguments;
    }

    @Override
    public InferenceContext18 freshInferenceContext(Scope scope) {
        return new InferenceContext18(scope, this.arguments, this, null);
    }
}

