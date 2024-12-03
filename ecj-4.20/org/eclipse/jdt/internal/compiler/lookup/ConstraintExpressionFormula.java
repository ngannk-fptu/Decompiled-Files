/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.SwitchExpression;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.BoundSet;
import org.eclipse.jdt.internal.compiler.lookup.ConstraintFormula;
import org.eclipse.jdt.internal.compiler.lookup.ConstraintTypeFormula;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InferenceFailureException;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBound;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

class ConstraintExpressionFormula
extends ConstraintFormula {
    Expression left;
    boolean isSoft;

    ConstraintExpressionFormula(Expression expression, TypeBinding type, int relation) {
        this.left = expression;
        this.right = type;
        this.relation = relation;
    }

    ConstraintExpressionFormula(Expression expression, TypeBinding type, int relation, boolean isSoft) {
        this(expression, type, relation);
        this.isSoft = isSoft;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public Object reduce(InferenceContext18 inferenceContext) throws InferenceFailureException {
        block43: {
            block40: {
                block44: {
                    block41: {
                        block42: {
                            if (this.relation == 8) {
                                return this.left.isPotentiallyCompatibleWith(this.right, inferenceContext.scope) != false ? ConstraintExpressionFormula.TRUE : ConstraintExpressionFormula.FALSE;
                            }
                            if (this.right.isProperType(true)) {
                                if (this.left.isCompatibleWith(this.right, inferenceContext.scope) || this.left.isBoxingCompatibleWith(this.right, inferenceContext.scope)) {
                                    if (this.left.resolvedType != null && this.left.resolvedType.needsUncheckedConversion(this.right)) {
                                        inferenceContext.usesUncheckedConversion = true;
                                    }
                                    return ConstraintExpressionFormula.TRUE;
                                }
                                return ConstraintExpressionFormula.FALSE;
                            }
                            if (!this.canBePolyExpression(this.left)) {
                                exprType = this.left.resolvedType;
                                if (exprType == null || !exprType.isValidBinding()) {
                                    if (this.left instanceof MessageSend && ((MessageSend)this.left).actualReceiverType instanceof InferenceVariable) {
                                        return null;
                                    }
                                    return ConstraintExpressionFormula.FALSE;
                                }
                                return ConstraintTypeFormula.create(exprType, this.right, 1, this.isSoft);
                            }
                            if (!(this.left instanceof Invocation)) break block43;
                            invocation = (Invocation)this.left;
                            previousMethod = invocation.binding();
                            if (previousMethod == null) {
                                return null;
                            }
                            method = previousMethod;
                            method = previousMethod.shallowOriginal();
                            prevInvocation = inferenceContext.enterPolyInvocation(invocation, invocation.arguments());
                            innerCtx = null;
                            try {
                                arguments = invocation.arguments();
                                argumentTypes = arguments == null ? Binding.NO_PARAMETERS : new TypeBinding[arguments.length];
                                i = 0;
                                while (i < argumentTypes.length) {
                                    argumentTypes[i] = arguments[i].resolvedType;
                                    ++i;
                                }
                                if (!(previousMethod instanceof ParameterizedGenericMethodBinding)) break block40;
                                innerCtx = invocation.getInferenceContext((ParameterizedGenericMethodBinding)previousMethod);
                                if (innerCtx != null) break block41;
                                exprType = this.left.resolvedType;
                                if (exprType != null && exprType.isValidBinding()) break block42;
                                var11_27 = ConstraintExpressionFormula.FALSE;
                                inferenceContext.resumeSuspendedInference(prevInvocation, innerCtx);
                                return var11_27;
                            }
                            catch (Throwable var10_32) {
                                inferenceContext.resumeSuspendedInference(prevInvocation, innerCtx);
                                throw var10_32;
                            }
                        }
                        var11_28 = ConstraintTypeFormula.create(exprType, this.right, 1, this.isSoft);
                        inferenceContext.resumeSuspendedInference(prevInvocation, innerCtx);
                        return var11_28;
                    }
                    if (innerCtx.stepCompleted < 1) break block44;
                    inferenceContext.integrateInnerInferenceB2(innerCtx);
                    ** GOTO lbl63
                }
                var11_29 = ConstraintExpressionFormula.FALSE;
                inferenceContext.resumeSuspendedInference(prevInvocation, innerCtx);
                return var11_29;
            }
            inferenceContext.inferenceKind = inferenceContext.getInferenceKind(previousMethod, argumentTypes);
            isDiamond = method.isConstructor() != false && this.left.isPolyExpression(method) != false;
            ConstraintExpressionFormula.inferInvocationApplicability(inferenceContext, method, argumentTypes, isDiamond, inferenceContext.inferenceKind);
lbl63:
            // 2 sources

            if (!inferenceContext.computeB3(invocation, this.right, method)) {
                var11_30 = ConstraintExpressionFormula.FALSE;
                inferenceContext.resumeSuspendedInference(prevInvocation, innerCtx);
                return var11_30;
            }
            inferenceContext.resumeSuspendedInference(prevInvocation, innerCtx);
            return null;
        }
        if (this.left instanceof ConditionalExpression) {
            conditional = (ConditionalExpression)this.left;
            return new ConstraintFormula[]{new ConstraintExpressionFormula(conditional.valueIfTrue, this.right, this.relation, this.isSoft), new ConstraintExpressionFormula(conditional.valueIfFalse, this.right, this.relation, this.isSoft)};
        }
        if (this.left instanceof SwitchExpression) {
            se = (SwitchExpression)this.left;
            cfs = new ConstraintFormula[se.resultExpressions.size()];
            i = 0;
            for (Expression re : se.resultExpressions) {
                cfs[i++] = new ConstraintExpressionFormula(re, this.right, this.relation, this.isSoft);
            }
            return cfs;
        }
        if (this.left instanceof LambdaExpression) {
            lambda = (LambdaExpression)this.left;
            scope = lambda.enclosingScope;
            if (this.right instanceof InferenceVariable) {
                return ConstraintExpressionFormula.TRUE;
            }
            if (!this.right.isFunctionalInterface(scope)) {
                return ConstraintExpressionFormula.FALSE;
            }
            t = (ReferenceBinding)this.right;
            withWildCards = InferenceContext18.parameterizedWithWildcard(t);
            if (withWildCards != null) {
                t = ConstraintExpressionFormula.findGroundTargetType(inferenceContext, scope, lambda, withWildCards);
            }
            if (t == null) {
                return ConstraintExpressionFormula.FALSE;
            }
            functionType = t.getSingleAbstractMethod(scope, true);
            if (functionType == null) {
                return ConstraintExpressionFormula.FALSE;
            }
            parameters = functionType.parameters;
            if (parameters.length != lambda.arguments().length) {
                return ConstraintExpressionFormula.FALSE;
            }
            if (lambda.argumentsTypeElided()) {
                i = 0;
                while (i < parameters.length) {
                    if (!parameters[i].isProperType(true)) {
                        return ConstraintExpressionFormula.FALSE;
                    }
                    ++i;
                }
            }
            if ((lambda = lambda.resolveExpressionExpecting(t, inferenceContext.scope, inferenceContext)) == null) {
                return ConstraintExpressionFormula.FALSE;
            }
            if (functionType.returnType == TypeBinding.VOID ? lambda.isVoidCompatible() == false : lambda.isValueCompatible() == false) {
                return ConstraintExpressionFormula.FALSE;
            }
            result = new ArrayList<ConstraintFormula>();
            if (!lambda.argumentsTypeElided()) {
                arguments = lambda.arguments();
                i = 0;
                while (i < parameters.length) {
                    result.add(ConstraintTypeFormula.create(parameters[i], arguments[i].type.resolvedType, 4));
                    ++i;
                }
                if (lambda.resolvedType != null) {
                    result.add(ConstraintTypeFormula.create(lambda.resolvedType, this.right, 2));
                }
            }
            if (functionType.returnType != TypeBinding.VOID) {
                r = functionType.returnType;
                exprs = lambda.resultExpressions();
                i = 0;
                length = exprs == null ? 0 : exprs.length;
                while (i < length) {
                    expr = exprs[i];
                    if (r.isProperType(true) && expr.resolvedType != null) {
                        exprType = expr.resolvedType;
                        if (!(expr.isConstantValueOfTypeAssignableToType(exprType, r) || exprType.isCompatibleWith(r) || expr.isBoxingCompatible(exprType, r, expr, scope))) {
                            return ConstraintExpressionFormula.FALSE;
                        }
                    } else {
                        result.add(new ConstraintExpressionFormula(expr, r, 1, this.isSoft));
                    }
                    ++i;
                }
            }
            if (result.size() == 0) {
                return ConstraintExpressionFormula.TRUE;
            }
            return result.toArray(new ConstraintFormula[result.size()]);
        }
        if (this.left instanceof ReferenceExpression) {
            return this.reduceReferenceExpressionCompatibility((ReferenceExpression)this.left, inferenceContext);
        }
        return ConstraintExpressionFormula.FALSE;
    }

    public static ReferenceBinding findGroundTargetType(InferenceContext18 inferenceContext, BlockScope scope, LambdaExpression lambda, ParameterizedTypeBinding targetTypeWithWildCards) {
        if (lambda.argumentsTypeElided()) {
            return lambda.findGroundTargetTypeForElidedLambda(scope, targetTypeWithWildCards);
        }
        InferenceContext18.SuspendedInferenceRecord previous = inferenceContext.enterLambda(lambda);
        try {
            ReferenceBinding referenceBinding = inferenceContext.inferFunctionalInterfaceParameterization(lambda, scope, targetTypeWithWildCards);
            return referenceBinding;
        }
        finally {
            inferenceContext.resumeSuspendedInference(previous, null);
        }
    }

    private boolean canBePolyExpression(Expression expr) {
        ExpressionContext previousExpressionContext = expr.getExpressionContext();
        if (previousExpressionContext == ExpressionContext.VANILLA_CONTEXT) {
            this.left.setExpressionContext(ExpressionContext.ASSIGNMENT_CONTEXT);
        }
        try {
            boolean bl = expr.isPolyExpression();
            return bl;
        }
        finally {
            expr.setExpressionContext(previousExpressionContext);
        }
    }

    private Object reduceReferenceExpressionCompatibility(ReferenceExpression reference, InferenceContext18 inferenceContext) {
        ReferenceBinding rPrime;
        TypeBinding r;
        MethodBinding potentiallyApplicable;
        TypeBinding t = this.right;
        if (t.isProperType(true)) {
            throw new IllegalStateException("Should not reach here with T being a proper type");
        }
        if (!t.isFunctionalInterface(inferenceContext.scope)) {
            return FALSE;
        }
        MethodBinding functionType = t.getSingleAbstractMethod(inferenceContext.scope, true);
        if (functionType == null) {
            return FALSE;
        }
        MethodBinding methodBinding = potentiallyApplicable = (reference = reference.resolveExpressionExpecting(t, inferenceContext.scope, inferenceContext)) != null ? reference.binding : null;
        if (potentiallyApplicable == null) {
            return FALSE;
        }
        if (reference.isExactMethodReference()) {
            ArrayList<ConstraintTypeFormula> newConstraints = new ArrayList<ConstraintTypeFormula>();
            TypeBinding[] p = functionType.parameters;
            int n = p.length;
            TypeBinding[] pPrime = potentiallyApplicable.parameters;
            int k = pPrime.length;
            int offset = 0;
            if (n == k + 1) {
                newConstraints.add(ConstraintTypeFormula.create(p[0], reference.lhs.resolvedType, 1));
                offset = 1;
            }
            int i = offset;
            while (i < n) {
                newConstraints.add(ConstraintTypeFormula.create(p[i], pPrime[i - offset], 1));
                ++i;
            }
            TypeBinding r2 = functionType.returnType;
            if (r2 != TypeBinding.VOID) {
                TypeBinding rAppl;
                TypeBinding typeBinding = rAppl = potentiallyApplicable.isConstructor() && !reference.isArrayConstructorReference() ? potentiallyApplicable.declaringClass : potentiallyApplicable.returnType;
                if (rAppl == TypeBinding.VOID) {
                    return FALSE;
                }
                TypeBinding rPrime2 = rAppl.capture(inferenceContext.scope, reference.sourceStart, reference.sourceEnd);
                newConstraints.add(ConstraintTypeFormula.create(rPrime2, r2, 1));
            }
            return newConstraints.toArray(new ConstraintFormula[newConstraints.size()]);
        }
        int n = functionType.parameters.length;
        int i = 0;
        while (i < n) {
            if (!functionType.parameters[i].isProperType(true)) {
                return FALSE;
            }
            ++i;
        }
        MethodBinding compileTimeDecl = potentiallyApplicable;
        if (!compileTimeDecl.isValidBinding()) {
            return FALSE;
        }
        TypeBinding typeBinding = r = functionType.isConstructor() ? functionType.declaringClass : functionType.returnType;
        if (r.id == 6) {
            return TRUE;
        }
        MethodBinding original = compileTimeDecl.shallowOriginal();
        if (this.needsInference(reference, original)) {
            TypeBinding[] argumentTypes;
            if (t.isParameterizedType()) {
                MethodBinding capturedFunctionType = ((ParameterizedTypeBinding)t).getSingleAbstractMethod(inferenceContext.scope, true, reference.sourceStart, reference.sourceEnd);
                argumentTypes = capturedFunctionType.parameters;
            } else {
                argumentTypes = functionType.parameters;
            }
            InferenceContext18.SuspendedInferenceRecord prevInvocation = inferenceContext.enterPolyInvocation(reference, reference.createPseudoExpressions(argumentTypes));
            InferenceContext18 innerContext = null;
            try {
                innerContext = reference.getInferenceContext((ParameterizedMethodBinding)compileTimeDecl);
                if (innerContext != null) {
                    innerContext.pushBoundsTo(inferenceContext);
                }
                int innerInferenceKind = this.determineInferenceKind(compileTimeDecl, argumentTypes, innerContext);
                ConstraintExpressionFormula.inferInvocationApplicability(inferenceContext, original, argumentTypes, original.isConstructor(), innerInferenceKind);
                if (!inferenceContext.computeB3(reference, r, original)) {
                    ConstraintTypeFormula constraintTypeFormula = FALSE;
                    return constraintTypeFormula;
                }
                return null;
            }
            catch (InferenceFailureException inferenceFailureException) {
                ConstraintTypeFormula constraintTypeFormula = FALSE;
                return constraintTypeFormula;
            }
            finally {
                inferenceContext.resumeSuspendedInference(prevInvocation, innerContext);
            }
        }
        TypeBinding typeBinding2 = rPrime = compileTimeDecl.isConstructor() ? compileTimeDecl.declaringClass : compileTimeDecl.returnType.capture(inferenceContext.scope, reference.sourceStart(), reference.sourceEnd());
        if (rPrime.id == 6) {
            return FALSE;
        }
        return ConstraintTypeFormula.create(rPrime, r, 1, this.isSoft);
    }

    private boolean needsInference(ReferenceExpression reference, MethodBinding original) {
        TypeBinding compileTimeReturn;
        if (reference.typeArguments != null) {
            return false;
        }
        if (original.isConstructor()) {
            if (original.declaringClass.typeVariables() != Binding.NO_TYPE_VARIABLES && reference.receiverType.isRawType()) {
                return true;
            }
            compileTimeReturn = original.declaringClass;
        } else {
            compileTimeReturn = original.returnType;
        }
        return original.typeVariables() != Binding.NO_TYPE_VARIABLES && compileTimeReturn.mentionsAny(original.typeVariables(), -1);
    }

    private int determineInferenceKind(MethodBinding original, TypeBinding[] argumentTypes, InferenceContext18 innerContext) {
        if (innerContext != null) {
            return innerContext.inferenceKind;
        }
        if (original.isVarargs()) {
            TypeBinding expectedLast;
            TypeBinding providedLast;
            int expectedLen = original.parameters.length;
            int providedLen = argumentTypes.length;
            if (expectedLen < providedLen) {
                return 3;
            }
            if (expectedLen == providedLen && !(providedLast = argumentTypes[expectedLen - 1]).isCompatibleWith(expectedLast = original.parameters[expectedLen - 1]) && expectedLast.isArrayType() && providedLast.isCompatibleWith(expectedLast = expectedLast.leafComponentType())) {
                return 3;
            }
        }
        return 1;
    }

    static void inferInvocationApplicability(InferenceContext18 inferenceContext, MethodBinding method, TypeBinding[] arguments, boolean isDiamond, int checkType) {
        TypeBinding[] typeVariables = method.getAllTypeVariables(isDiamond);
        InferenceVariable[] inferenceVariables = inferenceContext.createInitialBoundSet((TypeVariableBinding[])typeVariables);
        int paramLength = method.parameters.length;
        TypeBinding varArgsType = null;
        if (method.isVarargs()) {
            int varArgPos = paramLength - 1;
            varArgsType = method.parameters[varArgPos];
        }
        inferenceContext.createInitialConstraintsForParameters(method.parameters, checkType == 3, varArgsType, method);
        inferenceContext.addThrowsContraints(typeVariables, inferenceVariables, method.thrownExceptions);
    }

    static boolean inferPolyInvocationType(InferenceContext18 inferenceContext, InvocationSite invocationSite, TypeBinding targetType, MethodBinding method) throws InferenceFailureException {
        TypeBinding[] typeArguments = invocationSite.genericTypeArguments();
        if (typeArguments == null) {
            ConstraintTypeFormula newConstraint;
            TypeBinding returnType;
            TypeBinding typeBinding = returnType = method.isConstructor() ? method.declaringClass : method.returnType;
            if (returnType == TypeBinding.VOID) {
                throw new InferenceFailureException("expression has no value");
            }
            if (inferenceContext.usesUncheckedConversion) {
                TypeBinding erasure = ConstraintExpressionFormula.getRealErasure(returnType, inferenceContext.environment);
                ConstraintTypeFormula newConstraint2 = ConstraintTypeFormula.create(erasure, targetType, 1);
                return inferenceContext.reduceAndIncorporate(newConstraint2);
            }
            TypeBinding rTheta = inferenceContext.substitute(returnType);
            ParameterizedTypeBinding parameterizedType = InferenceContext18.parameterizedWithWildcard(rTheta);
            if (parameterizedType != null && parameterizedType.arguments != null) {
                TypeBinding[] arguments = parameterizedType.arguments;
                TypeBinding[] betas = inferenceContext.addTypeVariableSubstitutions(arguments);
                ParameterizedTypeBinding gbeta = inferenceContext.environment.createParameterizedType(parameterizedType.genericType(), betas, parameterizedType.enclosingType(), parameterizedType.getTypeAnnotations());
                inferenceContext.currentBounds.captures.put(gbeta, parameterizedType);
                int i = 0;
                int length = arguments.length;
                while (i < length) {
                    if (arguments[i].isWildcard()) {
                        WildcardBinding wc = (WildcardBinding)arguments[i];
                        switch (wc.boundKind) {
                            case 1: {
                                inferenceContext.currentBounds.addBound(new TypeBound((InferenceVariable)betas[i], wc.bound(), 2), inferenceContext.environment);
                                break;
                            }
                            case 2: {
                                inferenceContext.currentBounds.addBound(new TypeBound((InferenceVariable)betas[i], wc.bound(), 3), inferenceContext.environment);
                            }
                        }
                    }
                    ++i;
                }
                ConstraintTypeFormula newConstraint3 = ConstraintTypeFormula.create(gbeta, targetType, 1);
                return inferenceContext.reduceAndIncorporate(newConstraint3);
            }
            if (rTheta.leafComponentType() instanceof InferenceVariable) {
                TypeBinding wrapper;
                InferenceVariable alpha = (InferenceVariable)rTheta.leafComponentType();
                TypeBinding targetLeafType = targetType.leafComponentType();
                boolean toResolve = false;
                if (inferenceContext.currentBounds.condition18_5_2_bullet_3_3_1(alpha, targetLeafType)) {
                    toResolve = true;
                } else if (inferenceContext.currentBounds.condition18_5_2_bullet_3_3_2(alpha, targetLeafType, inferenceContext)) {
                    toResolve = true;
                } else if (targetLeafType.isPrimitiveType() && (wrapper = inferenceContext.currentBounds.findWrapperTypeBound(alpha)) != null) {
                    toResolve = true;
                }
                if (toResolve) {
                    BoundSet solution = inferenceContext.solve(new InferenceVariable[]{alpha});
                    if (solution == null) {
                        return false;
                    }
                    TypeBinding u = solution.getInstantiation(alpha, null).capture(inferenceContext.scope, invocationSite.sourceStart(), invocationSite.sourceEnd());
                    if (rTheta.dimensions() != 0) {
                        u = inferenceContext.environment.createArrayType(u, rTheta.dimensions());
                    }
                    ConstraintTypeFormula newConstraint4 = ConstraintTypeFormula.create(u, targetType, 1);
                    return inferenceContext.reduceAndIncorporate(newConstraint4);
                }
            }
            if (!inferenceContext.reduceAndIncorporate(newConstraint = ConstraintTypeFormula.create(rTheta, targetType, 1))) {
                return false;
            }
        }
        return true;
    }

    private static TypeBinding getRealErasure(TypeBinding type, LookupEnvironment environment) {
        TypeBinding erasure = type.erasure();
        TypeBinding erasedLeaf = erasure.leafComponentType();
        if (erasedLeaf.isGenericType()) {
            erasedLeaf = environment.convertToRawType(erasedLeaf, false);
        }
        if (erasure.isArrayType()) {
            return environment.createArrayType(erasedLeaf, erasure.dimensions());
        }
        return erasedLeaf;
    }

    @Override
    Collection<InferenceVariable> inputVariables(InferenceContext18 context) {
        if (this.left instanceof LambdaExpression) {
            if (this.right instanceof InferenceVariable) {
                return Collections.singletonList((InferenceVariable)this.right);
            }
            if (this.right.isFunctionalInterface(context.scope)) {
                LambdaExpression lambda = (LambdaExpression)this.left;
                ReferenceBinding targetType = (ReferenceBinding)this.right;
                ParameterizedTypeBinding withWildCards = InferenceContext18.parameterizedWithWildcard(targetType);
                if (withWildCards != null) {
                    targetType = ConstraintExpressionFormula.findGroundTargetType(context, lambda.enclosingScope, lambda, withWildCards);
                }
                if (targetType == null) {
                    return EMPTY_VARIABLE_LIST;
                }
                MethodBinding sam = targetType.getSingleAbstractMethod(context.scope, true);
                HashSet<InferenceVariable> variables = new HashSet<InferenceVariable>();
                if (lambda.argumentsTypeElided()) {
                    int len = sam.parameters.length;
                    int i = 0;
                    while (i < len) {
                        sam.parameters[i].collectInferenceVariables(variables);
                        ++i;
                    }
                }
                if (sam.returnType != TypeBinding.VOID) {
                    TypeBinding r = sam.returnType;
                    LambdaExpression resolved = lambda.resolveExpressionExpecting(this.right, context.scope, context);
                    Expression[] resultExpressions = resolved != null ? resolved.resultExpressions() : null;
                    int i = 0;
                    int length = resultExpressions == null ? 0 : resultExpressions.length;
                    while (i < length) {
                        variables.addAll(new ConstraintExpressionFormula(resultExpressions[i], r, 1).inputVariables(context));
                        ++i;
                    }
                }
                return variables;
            }
        } else if (this.left instanceof ReferenceExpression) {
            if (this.right instanceof InferenceVariable) {
                return Collections.singletonList((InferenceVariable)this.right);
            }
            if (this.right.isFunctionalInterface(context.scope) && !this.left.isExactMethodReference()) {
                MethodBinding sam = this.right.getSingleAbstractMethod(context.scope, true);
                HashSet<InferenceVariable> variables = new HashSet<InferenceVariable>();
                int len = sam.parameters.length;
                int i = 0;
                while (i < len) {
                    sam.parameters[i].collectInferenceVariables(variables);
                    ++i;
                }
                return variables;
            }
        } else {
            if (this.left instanceof ConditionalExpression && this.left.isPolyExpression()) {
                ConditionalExpression expr = (ConditionalExpression)this.left;
                HashSet<InferenceVariable> variables = new HashSet<InferenceVariable>();
                variables.addAll(new ConstraintExpressionFormula(expr.valueIfTrue, this.right, 1).inputVariables(context));
                variables.addAll(new ConstraintExpressionFormula(expr.valueIfFalse, this.right, 1).inputVariables(context));
                return variables;
            }
            if (this.left instanceof SwitchExpression && this.left.isPolyExpression()) {
                SwitchExpression expr = (SwitchExpression)this.left;
                HashSet<InferenceVariable> variables = new HashSet<InferenceVariable>();
                for (Expression re : expr.resultExpressions) {
                    variables.addAll(new ConstraintExpressionFormula(re, this.right, 1).inputVariables(context));
                }
                return variables;
            }
        }
        return EMPTY_VARIABLE_LIST;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer().append('\u27e8');
        this.left.printExpression(4, buf);
        buf.append(ConstraintExpressionFormula.relationToString(this.relation));
        this.appendTypeName(buf, this.right);
        buf.append('\u27e9');
        return buf.toString();
    }
}

