/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.OperatorExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnlikelyArgumentCheck;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.IrritantSet;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolymorphicMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class CastExpression
extends Expression {
    public Expression expression;
    public TypeReference type;
    public TypeBinding expectedType;
    public TypeBinding instanceofType;
    public boolean isVarTypeDeclaration;

    public CastExpression(Expression expression, TypeReference type) {
        this.expression = expression;
        this.type = type;
        type.bits |= 0x40000000;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        UnconditionalFlowInfo result = this.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
        this.expression.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        flowContext.recordAbruptExit();
        return result;
    }

    public static void checkNeedForAssignedCast(BlockScope scope, TypeBinding expectedType, CastExpression rhs) {
        CompilerOptions compilerOptions = scope.compilerOptions();
        if (compilerOptions.getSeverity(0x4000000) == 256) {
            return;
        }
        TypeBinding castedExpressionType = rhs.expression.resolvedType;
        if (castedExpressionType == null || rhs.resolvedType.isBaseType()) {
            return;
        }
        if (castedExpressionType.isCompatibleWith(expectedType, scope)) {
            if (scope.environment().usesNullTypeAnnotations() && NullAnnotationMatching.analyse(expectedType, castedExpressionType, -1).isAnyMismatch()) {
                return;
            }
            scope.problemReporter().unnecessaryCast(rhs);
        }
    }

    public static void checkNeedForCastCast(BlockScope scope, CastExpression enclosingCast) {
        if (scope.compilerOptions().getSeverity(0x4000000) == 256) {
            return;
        }
        CastExpression nestedCast = (CastExpression)enclosingCast.expression;
        if ((nestedCast.bits & 0x4000) == 0) {
            return;
        }
        if (nestedCast.losesPrecision(scope)) {
            return;
        }
        CastExpression alternateCast = new CastExpression(null, enclosingCast.type);
        alternateCast.resolvedType = enclosingCast.resolvedType;
        if (!alternateCast.checkCastTypesCompatibility(scope, enclosingCast.resolvedType, nestedCast.expression.resolvedType, null, true)) {
            return;
        }
        scope.problemReporter().unnecessaryCast(nestedCast);
    }

    private boolean losesPrecision(Scope scope) {
        TypeBinding exprType = this.expression.resolvedType;
        if (exprType.isBoxedPrimitiveType()) {
            exprType = scope.environment().computeBoxingType(exprType);
        }
        switch (this.resolvedType.id) {
            case 9: 
            case 31: {
                return exprType.id == 10 || exprType.id == 7;
            }
            case 8: 
            case 32: {
                return exprType.id == 7;
            }
        }
        return false;
    }

    public static void checkNeedForEnclosingInstanceCast(BlockScope scope, Expression enclosingInstance, TypeBinding enclosingInstanceType, TypeBinding memberType) {
        if (scope.compilerOptions().getSeverity(0x4000000) == 256) {
            return;
        }
        TypeBinding castedExpressionType = ((CastExpression)enclosingInstance).expression.resolvedType;
        if (castedExpressionType == null) {
            return;
        }
        if (TypeBinding.equalsEquals(castedExpressionType, enclosingInstanceType)) {
            scope.problemReporter().unnecessaryCast((CastExpression)enclosingInstance);
        } else {
            if (castedExpressionType == TypeBinding.NULL) {
                return;
            }
            TypeBinding alternateEnclosingInstanceType = castedExpressionType;
            if (castedExpressionType.isBaseType() || castedExpressionType.isArrayType()) {
                return;
            }
            if (TypeBinding.equalsEquals(memberType, scope.getMemberType(memberType.sourceName(), (ReferenceBinding)alternateEnclosingInstanceType))) {
                scope.problemReporter().unnecessaryCast((CastExpression)enclosingInstance);
            }
        }
    }

    public static void checkNeedForArgumentCast(BlockScope scope, int operator, int operatorSignature, Expression expression, int expressionTypeId) {
        if (scope.compilerOptions().getSeverity(0x4000000) == 256) {
            return;
        }
        if ((expression.bits & 0x4000) == 0 && expression.resolvedType.isBaseType()) {
            return;
        }
        TypeBinding alternateLeftType = ((CastExpression)expression).expression.resolvedType;
        if (alternateLeftType == null) {
            return;
        }
        if (alternateLeftType.id == expressionTypeId) {
            scope.problemReporter().unnecessaryCast((CastExpression)expression);
            return;
        }
    }

    public static void checkNeedForArgumentCasts(BlockScope scope, Expression receiver, TypeBinding receiverType, MethodBinding binding, Expression[] arguments, TypeBinding[] argumentTypes, InvocationSite invocationSite) {
        if (scope.compilerOptions().getSeverity(0x4000000) == 256) {
            return;
        }
        int length = argumentTypes.length;
        TypeBinding[] rawArgumentTypes = argumentTypes;
        int i = 0;
        while (i < length) {
            Expression argument = arguments[i];
            if (argument instanceof CastExpression && ((argument.bits & 0x4000) != 0 || !argument.resolvedType.isBaseType())) {
                TypeBinding castedExpressionType = ((CastExpression)argument).expression.resolvedType;
                if (castedExpressionType == null) {
                    return;
                }
                if (TypeBinding.equalsEquals(castedExpressionType, argumentTypes[i])) {
                    scope.problemReporter().unnecessaryCast((CastExpression)argument);
                } else if (castedExpressionType != TypeBinding.NULL && (argument.implicitConversion & 0x200) == 0) {
                    if (rawArgumentTypes == argumentTypes) {
                        TypeBinding[] typeBindingArray = rawArgumentTypes;
                        rawArgumentTypes = new TypeBinding[length];
                        System.arraycopy(typeBindingArray, 0, rawArgumentTypes, 0, length);
                    }
                    rawArgumentTypes[i] = castedExpressionType;
                }
            }
            ++i;
        }
        if (rawArgumentTypes != argumentTypes) {
            CastExpression.checkAlternateBinding(scope, receiver, receiverType, binding, arguments, argumentTypes, rawArgumentTypes, invocationSite);
        }
    }

    public static void checkNeedForArgumentCasts(BlockScope scope, int operator, int operatorSignature, Expression left, int leftTypeId, boolean leftIsCast, Expression right, int rightTypeId, boolean rightIsCast) {
        if (scope.compilerOptions().getSeverity(0x4000000) == 256) {
            return;
        }
        boolean useAutoBoxing = operator != 18 && operator != 29;
        int alternateLeftTypeId = leftTypeId;
        if (leftIsCast) {
            if ((left.bits & 0x4000) == 0 && left.resolvedType.isBaseType()) {
                leftIsCast = false;
            } else {
                TypeBinding alternateLeftType = ((CastExpression)left).expression.resolvedType;
                if (alternateLeftType == null) {
                    return;
                }
                alternateLeftTypeId = alternateLeftType.id;
                if (alternateLeftTypeId == leftTypeId || (useAutoBoxing ? scope.environment().computeBoxingType((TypeBinding)alternateLeftType).id == leftTypeId : TypeBinding.equalsEquals(alternateLeftType, left.resolvedType))) {
                    scope.problemReporter().unnecessaryCast((CastExpression)left);
                    leftIsCast = false;
                } else if (alternateLeftTypeId == 12) {
                    alternateLeftTypeId = leftTypeId;
                    leftIsCast = false;
                }
            }
        }
        int alternateRightTypeId = rightTypeId;
        if (rightIsCast) {
            if ((right.bits & 0x4000) == 0 && right.resolvedType.isBaseType()) {
                rightIsCast = false;
            } else {
                TypeBinding alternateRightType = ((CastExpression)right).expression.resolvedType;
                if (alternateRightType == null) {
                    return;
                }
                alternateRightTypeId = alternateRightType.id;
                if (alternateRightTypeId == rightTypeId || (useAutoBoxing ? scope.environment().computeBoxingType((TypeBinding)alternateRightType).id == rightTypeId : TypeBinding.equalsEquals(alternateRightType, right.resolvedType))) {
                    scope.problemReporter().unnecessaryCast((CastExpression)right);
                    rightIsCast = false;
                } else if (alternateRightTypeId == 12) {
                    alternateRightTypeId = rightTypeId;
                    rightIsCast = false;
                }
            }
        }
        if (leftIsCast || rightIsCast) {
            int alternateOperatorSignature;
            if (alternateLeftTypeId > 15 || alternateRightTypeId > 15) {
                if (alternateLeftTypeId == 11) {
                    alternateRightTypeId = 1;
                } else if (alternateRightTypeId == 11) {
                    alternateLeftTypeId = 1;
                } else {
                    return;
                }
            }
            if ((operatorSignature & 0xF0F0F) == ((alternateOperatorSignature = OperatorExpression.OperatorSignatures[operator][(alternateLeftTypeId << 4) + alternateRightTypeId]) & 0xF0F0F)) {
                if (leftIsCast) {
                    scope.problemReporter().unnecessaryCast((CastExpression)left);
                }
                if (rightIsCast) {
                    scope.problemReporter().unnecessaryCast((CastExpression)right);
                }
            }
        }
    }

    @Override
    public boolean checkNPE(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, int ttlForFieldCheck) {
        if ((this.resolvedType.tagBits & 0x100000000000000L) != 0L) {
            return true;
        }
        this.checkNPEbyUnboxing(scope, flowContext, flowInfo);
        return this.expression.checkNPE(scope, flowContext, flowInfo, ttlForFieldCheck);
    }

    private static void checkAlternateBinding(BlockScope scope, Expression receiver, TypeBinding receiverType, MethodBinding binding, Expression[] arguments, TypeBinding[] originalArgumentTypes, TypeBinding[] alternateArgumentTypes, final InvocationSite invocationSite) {
        MethodBinding bindingIfNoCast;
        InvocationSite fakeInvocationSite = new InvocationSite(){

            @Override
            public TypeBinding[] genericTypeArguments() {
                return null;
            }

            @Override
            public boolean isSuperAccess() {
                return invocationSite.isSuperAccess();
            }

            @Override
            public boolean isTypeAccess() {
                return invocationSite.isTypeAccess();
            }

            @Override
            public void setActualReceiverType(ReferenceBinding actualReceiverType) {
            }

            @Override
            public void setDepth(int depth) {
            }

            @Override
            public void setFieldIndex(int depth) {
            }

            @Override
            public int sourceStart() {
                return 0;
            }

            @Override
            public int sourceEnd() {
                return 0;
            }

            @Override
            public TypeBinding invocationTargetType() {
                return invocationSite.invocationTargetType();
            }

            @Override
            public boolean receiverIsImplicitThis() {
                return invocationSite.receiverIsImplicitThis();
            }

            @Override
            public InferenceContext18 freshInferenceContext(Scope someScope) {
                return invocationSite.freshInferenceContext(someScope);
            }

            @Override
            public ExpressionContext getExpressionContext() {
                return invocationSite.getExpressionContext();
            }

            @Override
            public boolean isQualifiedSuper() {
                return invocationSite.isQualifiedSuper();
            }

            @Override
            public boolean checkingPotentialCompatibility() {
                return false;
            }

            @Override
            public void acceptPotentiallyCompatibleMethods(MethodBinding[] methods) {
            }
        };
        if (binding.isConstructor()) {
            bindingIfNoCast = scope.getConstructor((ReferenceBinding)receiverType, alternateArgumentTypes, fakeInvocationSite);
        } else {
            MethodBinding methodBinding = bindingIfNoCast = receiver.isImplicitThis() ? scope.getImplicitMethod(binding.selector, alternateArgumentTypes, fakeInvocationSite) : scope.getMethod(receiverType, binding.selector, alternateArgumentTypes, fakeInvocationSite);
        }
        if (bindingIfNoCast == binding) {
            int paramLength;
            int argumentLength = originalArgumentTypes.length;
            if (binding.isVarargs() && (paramLength = binding.parameters.length) == argumentLength) {
                int varargsIndex = paramLength - 1;
                ArrayBinding varargsType = (ArrayBinding)binding.parameters[varargsIndex];
                TypeBinding lastArgType = alternateArgumentTypes[varargsIndex];
                if (varargsType.dimensions != lastArgType.dimensions()) {
                    return;
                }
                if (lastArgType.isCompatibleWith(varargsType.elementsType()) && lastArgType.isCompatibleWith(varargsType)) {
                    return;
                }
            }
            int i = 0;
            while (i < argumentLength) {
                if (TypeBinding.notEquals(originalArgumentTypes[i], alternateArgumentTypes[i]) && !CastExpression.preventsUnlikelyTypeWarning(originalArgumentTypes[i], alternateArgumentTypes[i], receiverType, binding, scope)) {
                    scope.problemReporter().unnecessaryCast((CastExpression)arguments[i]);
                }
                ++i;
            }
        }
    }

    private static boolean preventsUnlikelyTypeWarning(TypeBinding castedType, TypeBinding uncastedType, TypeBinding receiverType, MethodBinding binding, BlockScope scope) {
        if (!scope.compilerOptions().isAnyEnabled(IrritantSet.UNLIKELY_ARGUMENT_TYPE)) {
            return false;
        }
        if (binding.isStatic() || binding.parameters.length != 1) {
            return false;
        }
        UnlikelyArgumentCheck argumentChecks = UnlikelyArgumentCheck.determineCheckForNonStaticSingleArgumentMethod(uncastedType, scope, binding.selector, receiverType, binding.parameters);
        return argumentChecks != null && argumentChecks.isDangerous(scope) && ((argumentChecks = UnlikelyArgumentCheck.determineCheckForNonStaticSingleArgumentMethod(castedType, scope, binding.selector, receiverType, binding.parameters)) == null || !argumentChecks.isDangerous(scope));
    }

    @Override
    public boolean checkUnsafeCast(Scope scope, TypeBinding castType, TypeBinding expressionType, TypeBinding match, boolean isNarrowing) {
        return CastExpression.checkUnsafeCast(this, scope, castType, expressionType, match, isNarrowing);
    }

    /*
     * Unable to fully structure code
     */
    public static boolean checkUnsafeCast(Expression expression, Scope scope, TypeBinding castType, TypeBinding expressionType, TypeBinding match, boolean isNarrowing) {
        v0 = resolvedType = expression.resolvedType != null ? expression.resolvedType : castType;
        if (TypeBinding.equalsEquals(match, castType)) {
            if (!(isNarrowing || !TypeBinding.equalsEquals(match, resolvedType.leafComponentType()) || expressionType.isParameterizedType() && expressionType.isProvablyDistinct(castType))) {
                expression.tagAsUnnecessaryCast(scope, castType);
            }
            return true;
        }
        if (match != null && (isNarrowing != false ? match.isProvablyDistinct(expressionType) != false : castType.isProvablyDistinct(match) != false)) {
            return false;
        }
        block0 : switch (castType.kind()) {
            case 260: {
                if (castType.isReifiable()) break;
                if (match == null) {
                    expression.bits |= 128;
                    return true;
                }
                switch (match.kind()) {
                    case 260: {
                        if (!isNarrowing) ** GOTO lbl46
                        if (expressionType.isRawType() || !expressionType.isEquivalentTo(match)) {
                            expression.bits |= 128;
                            return true;
                        }
                        paramCastType = (ParameterizedTypeBinding)castType;
                        paramMatch = (ParameterizedTypeBinding)match;
                        castArguments = paramCastType.arguments;
                        v1 = length = castArguments == null ? 0 : castArguments.length;
                        if (paramMatch.arguments != null && length <= paramMatch.arguments.length) ** GOTO lbl27
                        expression.bits |= 128;
                        ** GOTO lbl45
lbl27:
                        // 1 sources

                        if ((paramCastType.tagBits & 0x60000000L) == 0L) ** GOTO lbl45
                        i = 0;
                        while (i < length) {
                            switch (castArguments[i].kind()) {
                                case 516: 
                                case 4100: {
                                    break;
                                }
                                default: {
                                    ** GOTO lbl43
                                }
                            }
                            alternateArguments = new TypeBinding[length];
                            System.arraycopy(paramCastType.arguments, 0, alternateArguments, 0, length);
                            alternateArguments[i] = scope.getJavaLangObject();
                            environment = scope.environment();
                            alternateCastType = environment.createParameterizedType((ReferenceBinding)castType.erasure(), alternateArguments, castType.enclosingType());
                            if (TypeBinding.equalsEquals(alternateCastType.findSuperTypeOriginatingFrom(expressionType), match)) {
                                expression.bits |= 128;
                                break;
                            }
lbl43:
                            // 3 sources

                            ++i;
                        }
lbl45:
                        // 4 sources

                        return true;
lbl46:
                        // 1 sources

                        if (match.isEquivalentTo(castType)) break block0;
                        expression.bits |= 128;
                        return true;
                    }
                    case 1028: {
                        expression.bits |= 128;
                        return true;
                    }
                }
                if (!isNarrowing) break;
                expression.bits |= 128;
                return true;
            }
            case 68: {
                leafType = castType.leafComponentType();
                if (!isNarrowing || leafType.isReifiable() && !leafType.isTypeVariable()) break;
                expression.bits |= 128;
                return true;
            }
            case 4100: {
                expression.bits |= 128;
                return true;
            }
        }
        if (!isNarrowing && TypeBinding.equalsEquals(match, resolvedType.leafComponentType())) {
            expression.tagAsUnnecessaryCast(scope, castType);
        }
        return true;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        boolean needRuntimeCheckcast;
        int pc = codeStream.position;
        boolean annotatedCast = (this.type.bits & 0x100000) != 0;
        boolean bl = needRuntimeCheckcast = (this.bits & 0x40) != 0;
        if (this.constant != Constant.NotAConstant) {
            if (valueRequired || needRuntimeCheckcast || annotatedCast) {
                codeStream.generateConstant(this.constant, this.implicitConversion);
                if (needRuntimeCheckcast || annotatedCast) {
                    codeStream.checkcast(this.type, this.resolvedType, pc);
                }
                if (!valueRequired) {
                    codeStream.pop();
                }
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        this.expression.generateCode(currentScope, codeStream, annotatedCast || valueRequired || needRuntimeCheckcast);
        if (annotatedCast || needRuntimeCheckcast && TypeBinding.notEquals(this.expression.postConversionType(currentScope), this.resolvedType.erasure())) {
            codeStream.checkcast(this.type, this.resolvedType, pc);
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        } else if (annotatedCast || needRuntimeCheckcast) {
            switch (this.resolvedType.id) {
                case 7: 
                case 8: {
                    codeStream.pop2();
                    break;
                }
                default: {
                    codeStream.pop();
                }
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    public Expression innermostCastedExpression() {
        Expression current = this.expression;
        while (current instanceof CastExpression) {
            current = ((CastExpression)current).expression;
        }
        return current;
    }

    @Override
    public LocalVariableBinding localVariableBinding() {
        return this.expression.localVariableBinding();
    }

    @Override
    public int nullStatus(FlowInfo flowInfo, FlowContext flowContext) {
        if ((this.implicitConversion & 0x200) != 0) {
            return 4;
        }
        return this.expression.nullStatus(flowInfo, flowContext);
    }

    @Override
    public Constant optimizedBooleanConstant() {
        switch (this.resolvedType.id) {
            case 5: 
            case 33: {
                return this.expression.optimizedBooleanConstant();
            }
        }
        return Constant.NotAConstant;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        int parenthesesCount = (this.bits & 0x1FE00000) >> 21;
        String suffix = "";
        int i = 0;
        while (i < parenthesesCount) {
            output.append('(');
            suffix = String.valueOf(suffix) + ')';
            ++i;
        }
        output.append('(');
        this.type.print(0, output).append(") ");
        return this.expression.printExpression(0, output).append(suffix);
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        this.constant = Constant.NotAConstant;
        this.implicitConversion = 0;
        boolean exprContainCast = false;
        TypeBinding castType = this.resolvedType = this.type.resolveType(scope);
        if (scope.compilerOptions().sourceLevel >= 0x340000L) {
            this.expression.setExpressionContext(ExpressionContext.CASTING_CONTEXT);
            if (this.expression instanceof FunctionalExpression) {
                this.expression.setExpectedType(this.resolvedType);
                this.bits |= 0x20;
            }
        }
        if (this.expression instanceof CastExpression) {
            this.expression.bits |= 0x20;
            exprContainCast = true;
        }
        TypeBinding expressionType = this.expression.resolveType(scope);
        if (this.expression instanceof MessageSend) {
            MessageSend messageSend = (MessageSend)this.expression;
            MethodBinding methodBinding = messageSend.binding;
            if (methodBinding != null && methodBinding.isPolymorphic()) {
                messageSend.binding = scope.environment().updatePolymorphicMethodReturnType((PolymorphicMethodBinding)methodBinding, castType);
                if (TypeBinding.notEquals(expressionType, castType)) {
                    expressionType = castType;
                    this.bits |= 0x20;
                }
            }
        }
        if (castType != null) {
            if (expressionType != null) {
                boolean isLegal;
                boolean nullAnnotationMismatch;
                boolean bl = nullAnnotationMismatch = scope.compilerOptions().isAnnotationBasedNullAnalysisEnabled && NullAnnotationMatching.analyse(castType, expressionType, -1).isAnyMismatch();
                if (this.instanceofType != null && expressionType.isParameterizedType() && expressionType.isProvablyDistinct(this.instanceofType)) {
                    this.bits |= 0x20;
                }
                if (this.isVarTypeDeclaration && TypeBinding.notEquals(expressionType, castType)) {
                    this.bits |= 0x20;
                }
                if (isLegal = this.checkCastTypesCompatibility(scope, castType, expressionType, this.expression, true)) {
                    this.expression.computeConversion(scope, castType, expressionType);
                    if ((this.bits & 0x80) != 0) {
                        if (scope.compilerOptions().reportUnavoidableGenericTypeProblems || !expressionType.isRawType() || !this.expression.forcedToBeRaw(scope.referenceContext())) {
                            scope.problemReporter().unsafeCast(this, scope);
                        }
                    } else if (nullAnnotationMismatch) {
                        scope.problemReporter().unsafeNullnessCast(this, scope);
                    } else {
                        if (castType.isRawType() && scope.compilerOptions().getSeverity(0x20010000) != 256) {
                            scope.problemReporter().rawTypeReference(this.type, castType);
                        }
                        if ((this.bits & 0x4020) == 16384 && !this.isIndirectlyUsed()) {
                            scope.problemReporter().unnecessaryCast(this);
                        }
                    }
                } else {
                    if ((castType.tagBits & 0x80L) == 0L) {
                        scope.problemReporter().typeCastError(this, castType, expressionType);
                    }
                    this.bits |= 0x20;
                }
            }
            this.resolvedType = castType.capture(scope, this.type.sourceStart, this.type.sourceEnd);
            if (exprContainCast) {
                CastExpression.checkNeedForCastCast(scope, this);
            }
        }
        return this.resolvedType;
    }

    @Override
    public void setExpectedType(TypeBinding expectedType) {
        this.expectedType = expectedType;
    }

    private boolean isIndirectlyUsed() {
        MethodBinding method;
        if (this.expression instanceof MessageSend && (method = ((MessageSend)this.expression).binding) instanceof ParameterizedGenericMethodBinding && ((ParameterizedGenericMethodBinding)method).inferredReturnType) {
            if (this.expectedType == null) {
                return true;
            }
            if (TypeBinding.notEquals(this.resolvedType, this.expectedType)) {
                return true;
            }
        }
        return this.expectedType != null && this.resolvedType.isBaseType() && !this.resolvedType.isCompatibleWith(this.expectedType);
    }

    @Override
    public void tagAsNeedCheckCast() {
        this.bits |= 0x40;
    }

    @Override
    public void tagAsUnnecessaryCast(Scope scope, TypeBinding castType) {
        this.bits |= 0x4000;
    }

    public void setInstanceofType(TypeBinding instanceofTypeBinding) {
        this.instanceofType = instanceofTypeBinding;
    }

    public void setVarTypeDeclaration(boolean value) {
        this.isVarTypeDeclaration = value;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.type.traverse(visitor, blockScope);
            this.expression.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }
}

