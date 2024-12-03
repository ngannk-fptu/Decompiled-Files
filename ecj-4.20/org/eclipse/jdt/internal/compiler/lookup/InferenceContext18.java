/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.SwitchExpression;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.BoundSet;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding18;
import org.eclipse.jdt.internal.compiler.lookup.ConstraintExceptionFormula;
import org.eclipse.jdt.internal.compiler.lookup.ConstraintExpressionFormula;
import org.eclipse.jdt.internal.compiler.lookup.ConstraintFormula;
import org.eclipse.jdt.internal.compiler.lookup.ConstraintTypeFormula;
import org.eclipse.jdt.internal.compiler.lookup.InferenceFailureException;
import org.eclipse.jdt.internal.compiler.lookup.InferenceSubstitution;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReductionResult;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticFactoryMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBound;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.util.Sorting;

public class InferenceContext18 {
    static final boolean SIMULATE_BUG_JDK_8026527 = true;
    static final boolean SHOULD_WORKAROUND_BUG_JDK_8054721 = true;
    static final boolean SHOULD_WORKAROUND_BUG_JDK_8153748 = true;
    static final boolean ARGUMENT_CONSTRAINTS_ARE_SOFT = false;
    InvocationSite currentInvocation;
    Expression[] invocationArguments;
    InferenceVariable[] inferenceVariables;
    ConstraintFormula[] initialConstraints;
    ConstraintExpressionFormula[] finalConstraints;
    BoundSet currentBounds;
    int inferenceKind;
    public int stepCompleted = 0;
    public static final int NOT_INFERRED = 0;
    public static final int APPLICABILITY_INFERRED = 1;
    public static final int TYPE_INFERRED = 2;
    public static final int TYPE_INFERRED_FINAL = 3;
    public List<ConstraintFormula> constraintsWithUncheckedConversion;
    public boolean usesUncheckedConversion;
    public InferenceContext18 outerContext;
    private Set<InferenceContext18> seenInnerContexts;
    Scope scope;
    LookupEnvironment environment;
    ReferenceBinding object;
    public BoundSet b2;
    private BoundSet b3;
    private BoundSet innerInbox;
    private boolean directlyAcceptingInnerBounds = false;
    private Runnable pushToOuterJob = null;
    public static final int CHECK_UNKNOWN = 0;
    public static final int CHECK_STRICT = 1;
    public static final int CHECK_LOOSE = 2;
    public static final int CHECK_VARARG = 3;
    int captureId = 0;

    public static boolean isSameSite(InvocationSite site1, InvocationSite site2) {
        if (site1 == site2) {
            return true;
        }
        if (site1 == null || site2 == null) {
            return false;
        }
        return site1.sourceStart() == site2.sourceStart() && site1.sourceEnd() == site2.sourceEnd();
    }

    public InferenceContext18(Scope scope, Expression[] arguments, InvocationSite site, InferenceContext18 outerContext) {
        this.scope = scope;
        this.environment = scope.environment();
        this.object = scope.getJavaLangObject();
        this.invocationArguments = arguments;
        this.currentInvocation = site;
        this.outerContext = outerContext;
        if (site instanceof Invocation) {
            scope.compilationUnitScope().registerInferredInvocation((Invocation)site);
        }
    }

    public InferenceContext18(Scope scope) {
        this.scope = scope;
        this.environment = scope.environment();
        this.object = scope.getJavaLangObject();
    }

    public InferenceVariable[] createInitialBoundSet(TypeVariableBinding[] typeParameters) {
        if (this.currentBounds == null) {
            this.currentBounds = new BoundSet();
        }
        if (typeParameters != null) {
            InferenceVariable[] newInferenceVariables = this.addInitialTypeVariableSubstitutions(typeParameters);
            this.currentBounds.addBoundsFromTypeParameters(this, typeParameters, newInferenceVariables);
            return newInferenceVariables;
        }
        return Binding.NO_INFERENCE_VARIABLES;
    }

    public TypeBinding substitute(TypeBinding type) {
        InferenceSubstitution inferenceSubstitution = new InferenceSubstitution(this);
        return inferenceSubstitution.substitute((Substitution)inferenceSubstitution, type);
    }

    public void createInitialConstraintsForParameters(TypeBinding[] parameters, boolean checkVararg, TypeBinding varArgsType, MethodBinding method) {
        boolean ownConstraints;
        if (this.invocationArguments == null) {
            return;
        }
        int len = checkVararg ? parameters.length - 1 : Math.min(parameters.length, this.invocationArguments.length);
        int maxConstraints = checkVararg ? this.invocationArguments.length : len;
        int numConstraints = 0;
        if (this.initialConstraints == null) {
            this.initialConstraints = new ConstraintFormula[maxConstraints];
            ownConstraints = true;
        } else {
            numConstraints = this.initialConstraints.length;
            this.initialConstraints = new ConstraintFormula[maxConstraints += numConstraints];
            System.arraycopy(this.initialConstraints, 0, this.initialConstraints, 0, numConstraints);
            ownConstraints = false;
        }
        int i = 0;
        while (i < len) {
            TypeBinding thetaF = this.substitute(parameters[i]);
            if (this.invocationArguments[i].isPertinentToApplicability(parameters[i], method)) {
                this.initialConstraints[numConstraints++] = new ConstraintExpressionFormula(this.invocationArguments[i], thetaF, 1, false);
            } else if (!this.isTypeVariableOfCandidate(parameters[i], method)) {
                this.initialConstraints[numConstraints++] = new ConstraintExpressionFormula(this.invocationArguments[i], thetaF, 8);
            }
            ++i;
        }
        if (checkVararg && varArgsType instanceof ArrayBinding) {
            varArgsType = ((ArrayBinding)varArgsType).elementsType();
            TypeBinding thetaF = this.substitute(varArgsType);
            int i2 = len;
            while (i2 < this.invocationArguments.length) {
                if (this.invocationArguments[i2].isPertinentToApplicability(varArgsType, method)) {
                    this.initialConstraints[numConstraints++] = new ConstraintExpressionFormula(this.invocationArguments[i2], thetaF, 1, false);
                } else if (!this.isTypeVariableOfCandidate(varArgsType, method)) {
                    this.initialConstraints[numConstraints++] = new ConstraintExpressionFormula(this.invocationArguments[i2], thetaF, 8);
                }
                ++i2;
            }
        }
        if (numConstraints == 0) {
            this.initialConstraints = ConstraintFormula.NO_CONSTRAINTS;
        } else if (numConstraints < maxConstraints) {
            this.initialConstraints = new ConstraintFormula[numConstraints];
            System.arraycopy(this.initialConstraints, 0, this.initialConstraints, 0, numConstraints);
        }
        if (ownConstraints) {
            int length = this.initialConstraints.length;
            this.finalConstraints = new ConstraintExpressionFormula[length];
            System.arraycopy(this.initialConstraints, 0, this.finalConstraints, 0, length);
        }
    }

    private boolean isTypeVariableOfCandidate(TypeBinding type, MethodBinding candidate) {
        if (type instanceof TypeVariableBinding) {
            Binding declaringElement = ((TypeVariableBinding)type).declaringElement;
            if (declaringElement == candidate) {
                return true;
            }
            if (candidate.isConstructor() && declaringElement == candidate.declaringClass) {
                return true;
            }
        }
        return false;
    }

    private InferenceVariable[] addInitialTypeVariableSubstitutions(TypeBinding[] typeVariables) {
        int len = typeVariables.length;
        if (len == 0) {
            if (this.inferenceVariables == null) {
                this.inferenceVariables = Binding.NO_INFERENCE_VARIABLES;
            }
            return Binding.NO_INFERENCE_VARIABLES;
        }
        InferenceVariable[] newVariables = new InferenceVariable[len];
        int i = 0;
        while (i < len) {
            newVariables[i] = InferenceVariable.get(typeVariables[i], i, this.currentInvocation, this.scope, this.object, true);
            ++i;
        }
        this.addInferenceVariables(newVariables);
        return newVariables;
    }

    private void addInferenceVariables(InferenceVariable[] newVariables) {
        if (this.inferenceVariables == null || this.inferenceVariables.length == 0) {
            this.inferenceVariables = newVariables;
        } else {
            int len = newVariables.length;
            int prev = this.inferenceVariables.length;
            this.inferenceVariables = new InferenceVariable[len + prev];
            System.arraycopy(this.inferenceVariables, 0, this.inferenceVariables, 0, prev);
            System.arraycopy(newVariables, 0, this.inferenceVariables, prev, len);
        }
    }

    public InferenceVariable[] addTypeVariableSubstitutions(TypeBinding[] typeVariables) {
        int len2 = typeVariables.length;
        InferenceVariable[] newVariables = new InferenceVariable[len2];
        InferenceVariable[] toAdd = new InferenceVariable[len2];
        int numToAdd = 0;
        int i = 0;
        while (i < typeVariables.length) {
            if (typeVariables[i] instanceof InferenceVariable) {
                newVariables[i] = (InferenceVariable)typeVariables[i];
            } else {
                toAdd[numToAdd++] = newVariables[i] = InferenceVariable.get(typeVariables[i], i, this.currentInvocation, this.scope, this.object, false);
            }
            ++i;
        }
        if (numToAdd > 0) {
            int start = 0;
            if (this.inferenceVariables != null) {
                int len1 = this.inferenceVariables.length;
                this.inferenceVariables = new InferenceVariable[len1 + numToAdd];
                System.arraycopy(this.inferenceVariables, 0, this.inferenceVariables, 0, len1);
                start = len1;
            } else {
                this.inferenceVariables = new InferenceVariable[numToAdd];
            }
            System.arraycopy(toAdd, 0, this.inferenceVariables, start, numToAdd);
        }
        return newVariables;
    }

    public void addThrowsContraints(TypeBinding[] parameters, InferenceVariable[] variables, ReferenceBinding[] thrownExceptions) {
        int i = 0;
        while (i < parameters.length) {
            TypeBinding parameter = parameters[i];
            int j = 0;
            while (j < thrownExceptions.length) {
                if (TypeBinding.equalsEquals(parameter, thrownExceptions[j])) {
                    this.currentBounds.inThrows.add(variables[i].prototype());
                    break;
                }
                ++j;
            }
            ++i;
        }
    }

    public void inferInvocationApplicability(MethodBinding method, TypeBinding[] arguments, boolean isDiamond) {
        ConstraintExpressionFormula.inferInvocationApplicability(this, method, arguments, isDiamond, this.inferenceKind);
    }

    boolean computeB3(InvocationSite invocationSite, TypeBinding targetType, MethodBinding method) throws InferenceFailureException {
        boolean result = ConstraintExpressionFormula.inferPolyInvocationType(this, invocationSite, targetType, method);
        if (result) {
            this.mergeInnerBounds();
            if (this.b3 == null) {
                this.b3 = this.currentBounds.copy();
            }
        }
        return result;
    }

    public BoundSet inferInvocationType(TypeBinding expectedType, InvocationSite invocationSite, MethodBinding method) throws InferenceFailureException {
        if (expectedType == null && method.returnType != null) {
            this.substitute(method.returnType);
        }
        this.currentBounds = this.b2.copy();
        int step = expectedType == null || expectedType.isProperType(true) ? 3 : 2;
        try {
            ReductionResult jdk8153748result;
            if (expectedType != null && expectedType != TypeBinding.VOID && invocationSite instanceof Expression && ((Expression)((Object)invocationSite)).isTrulyExpression() && ((Expression)((Object)invocationSite)).isPolyExpression(method)) {
                if (!this.computeB3(invocationSite, expectedType, method)) {
                    return null;
                }
            } else {
                this.mergeInnerBounds();
                this.b3 = this.currentBounds.copy();
            }
            if ((jdk8153748result = this.addJDK_8153748ConstraintsFromInvocation(this.invocationArguments, method, new InferenceSubstitution(this))) != null && !this.currentBounds.incorporate(this)) {
                return null;
            }
            this.pushBoundsToOuter();
            this.directlyAcceptingInnerBounds = true;
            HashSet<ConstraintFormula> c = new HashSet<ConstraintFormula>();
            if (!this.addConstraintsToC(this.invocationArguments, c, method, this.inferenceKind, invocationSite)) {
                return null;
            }
            List<Set<InferenceVariable>> components = this.currentBounds.computeConnectedComponents(this.inferenceVariables);
            while (!c.isEmpty()) {
                Set<ConstraintFormula> bottomSet = this.findBottomSet(c, this.allOutputVariables(c), components);
                if (bottomSet.isEmpty()) {
                    bottomSet.add(this.pickFromCycle(c));
                }
                c.removeAll(bottomSet);
                HashSet<InferenceVariable> allInputs = new HashSet<InferenceVariable>();
                Iterator<ConstraintFormula> bottomIt = bottomSet.iterator();
                while (bottomIt.hasNext()) {
                    allInputs.addAll(bottomIt.next().inputVariables(this));
                }
                InferenceVariable[] variablesArray = allInputs.toArray(new InferenceVariable[allInputs.size()]);
                if (!this.currentBounds.incorporate(this)) {
                    return null;
                }
                BoundSet solution = this.resolve(variablesArray);
                if (solution == null) {
                    solution = this.resolve(this.inferenceVariables);
                }
                for (ConstraintFormula constraint : bottomSet) {
                    if (solution != null && !constraint.applySubstitution(solution, variablesArray)) {
                        return null;
                    }
                    if (this.currentBounds.reduceOneConstraint(this, constraint)) continue;
                    return null;
                }
            }
            BoundSet solution = this.solve();
            if (solution == null || !this.isResolved(solution)) {
                this.currentBounds = this.b2;
                return null;
            }
            this.reportUncheckedConversions(solution);
            if (step == 3) {
                this.currentBounds = solution;
            }
            BoundSet boundSet = solution;
            return boundSet;
        }
        finally {
            this.stepCompleted = step;
        }
    }

    private void pushBoundsToOuter() {
        this.pushBoundsTo(this.outerContext);
    }

    public void pushBoundsTo(InferenceContext18 outer) {
        if (outer != null && outer.stepCompleted >= 1) {
            boolean deferred = outer.currentInvocation instanceof Invocation;
            BoundSet toPush = deferred ? this.currentBounds.copy() : this.currentBounds;
            Runnable job = () -> {
                if (inferenceContext18.directlyAcceptingInnerBounds) {
                    inferenceContext18.currentBounds.addBounds(toPush, this.environment);
                } else if (inferenceContext18.innerInbox == null) {
                    inferenceContext18.innerInbox = deferred ? toPush : toPush.copy();
                } else {
                    inferenceContext18.innerInbox.addBounds(toPush, this.environment);
                }
            };
            if (deferred) {
                this.pushToOuterJob = job;
            } else {
                job.run();
            }
        }
    }

    public void flushBoundOutbox() {
        if (this.pushToOuterJob != null) {
            this.pushToOuterJob.run();
            this.pushToOuterJob = null;
        }
    }

    private void mergeInnerBounds() {
        if (this.innerInbox != null) {
            this.currentBounds.addBounds(this.innerInbox, this.environment);
            this.innerInbox = null;
        }
    }

    private boolean collectingInnerBounds(InferenceOperation operation) throws InferenceFailureException {
        boolean result = operation.perform();
        if (result) {
            this.mergeInnerBounds();
        } else {
            this.innerInbox = null;
        }
        return result;
    }

    private ReductionResult addJDK_8153748ConstraintsFromInvocation(Expression[] arguments, MethodBinding method, InferenceSubstitution substitution) throws InferenceFailureException {
        boolean constraintAdded = false;
        if (arguments != null) {
            int i = 0;
            while (i < arguments.length) {
                Expression argument = arguments[i];
                TypeBinding parameter = InferenceContext18.getParameter(method.parameters, i, method.isVarargs());
                if (parameter == null) {
                    return ReductionResult.FALSE;
                }
                ReductionResult result = this.addJDK_8153748ConstraintsFromExpression(argument, parameter = substitution.substitute((Substitution)substitution, parameter), method, substitution);
                if (result == ReductionResult.FALSE) {
                    return ReductionResult.FALSE;
                }
                if (result == ReductionResult.TRUE) {
                    constraintAdded = true;
                }
                ++i;
            }
        }
        return constraintAdded ? ReductionResult.TRUE : null;
    }

    private ReductionResult addJDK_8153748ConstraintsFromExpression(Expression argument, TypeBinding parameter, MethodBinding method, InferenceSubstitution substitution) throws InferenceFailureException {
        if (argument instanceof FunctionalExpression) {
            return this.addJDK_8153748ConstraintsFromFunctionalExpr((FunctionalExpression)argument, parameter, method);
        }
        if (argument instanceof Invocation && argument.isPolyExpression(method)) {
            Invocation invocation = (Invocation)((Object)argument);
            Expression[] innerArgs = invocation.arguments();
            MethodBinding innerMethod = invocation.binding();
            if (innerMethod != null && innerMethod.isValidBinding()) {
                substitution = this.enrichSubstitution(substitution, invocation, innerMethod);
                return this.addJDK_8153748ConstraintsFromInvocation(innerArgs, innerMethod.shallowOriginal(), substitution);
            }
        } else {
            if (argument instanceof ConditionalExpression) {
                ConditionalExpression ce = (ConditionalExpression)argument;
                if (this.addJDK_8153748ConstraintsFromExpression(ce.valueIfTrue, parameter, method, substitution) == ReductionResult.FALSE) {
                    return ReductionResult.FALSE;
                }
                return this.addJDK_8153748ConstraintsFromExpression(ce.valueIfFalse, parameter, method, substitution);
            }
            if (argument instanceof SwitchExpression) {
                SwitchExpression se = (SwitchExpression)argument;
                ReductionResult result = ReductionResult.FALSE;
                for (Expression re : se.resultExpressions) {
                    result = this.addJDK_8153748ConstraintsFromExpression(re, parameter, method, substitution);
                    if (result == ReductionResult.FALSE) break;
                }
                return result;
            }
        }
        return null;
    }

    private ReductionResult addJDK_8153748ConstraintsFromFunctionalExpr(FunctionalExpression functionalExpr, TypeBinding targetType, MethodBinding method) throws InferenceFailureException {
        ConstraintExpressionFormula exprConstraint;
        if (!functionalExpr.isPertinentToApplicability(targetType, method) && this.collectingInnerBounds(() -> this.lambda$1(exprConstraint = new ConstraintExpressionFormula(functionalExpr, targetType, 1, false)))) {
            if (!this.collectingInnerBounds(() -> this.reduceAndIncorporate(exprConstraint))) {
                return ReductionResult.FALSE;
            }
            ConstraintExceptionFormula excConstraint = new ConstraintExceptionFormula(functionalExpr, targetType);
            if (!this.collectingInnerBounds(() -> this.reduceAndIncorporate(excConstraint))) {
                return ReductionResult.FALSE;
            }
            return ReductionResult.TRUE;
        }
        return null;
    }

    InferenceSubstitution enrichSubstitution(InferenceSubstitution substitution, Invocation innerInvocation, MethodBinding innerMethod) {
        InferenceContext18 innerContext;
        if (innerMethod instanceof ParameterizedGenericMethodBinding && (innerContext = innerInvocation.getInferenceContext((ParameterizedMethodBinding)innerMethod)) != null) {
            return substitution.addContext(innerContext);
        }
        return substitution;
    }

    private boolean addConstraintsToC(Expression[] exprs, Set<ConstraintFormula> c, MethodBinding method, int inferenceKindForMethod, InvocationSite site) throws InferenceFailureException {
        if (exprs != null) {
            TypeBinding[] fs;
            int k = exprs.length;
            int p = method.parameters.length;
            if (method.isVarargs() ? k < p - 1 : k != p) {
                return false;
            }
            switch (inferenceKindForMethod) {
                case 1: 
                case 2: {
                    fs = method.parameters;
                    break;
                }
                case 3: {
                    fs = this.varArgTypes(method.parameters, k);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unexpected checkKind " + this.inferenceKind);
                }
            }
            int i = 0;
            while (i < k) {
                InferenceSubstitution inferenceSubstitution;
                TypeBinding substF;
                TypeBinding fsi = fs[Math.min(i, p - 1)];
                if (!this.addConstraintsToC_OneExpr(exprs[i], c, fsi, substF = (inferenceSubstitution = new InferenceSubstitution(this.environment, this.inferenceVariables, site)).substitute((Substitution)inferenceSubstitution, fsi), method)) {
                    return false;
                }
                ++i;
            }
        }
        return true;
    }

    private boolean addConstraintsToC_OneExpr(Expression expri, Set<ConstraintFormula> c, TypeBinding fsi, TypeBinding substF, MethodBinding method) throws InferenceFailureException {
        boolean substFIsProperType = substF.isProperType(true);
        substF = Scope.substitute(this.getResultSubstitution(this.b3), substF);
        if (!expri.isPertinentToApplicability(fsi, method)) {
            c.add(new ConstraintExpressionFormula(expri, substF, 1, false));
        }
        if (expri instanceof FunctionalExpression) {
            c.add(new ConstraintExceptionFormula((FunctionalExpression)expri, substF));
            if (expri instanceof LambdaExpression) {
                LambdaExpression lambda = (LambdaExpression)expri;
                BlockScope skope = lambda.enclosingScope;
                if (substF.isFunctionalInterface(skope)) {
                    MethodBinding functionType;
                    ReferenceBinding t = (ReferenceBinding)substF;
                    ParameterizedTypeBinding withWildCards = InferenceContext18.parameterizedWithWildcard(t);
                    if (withWildCards != null) {
                        t = ConstraintExpressionFormula.findGroundTargetType(this, skope, lambda, withWildCards);
                    }
                    if (t != null && (functionType = t.getSingleAbstractMethod(skope, true)) != null && (lambda = lambda.resolveExpressionExpecting(t, this.scope, this)) != null) {
                        TypeBinding r = functionType.returnType;
                        Expression[] resultExpressions = lambda.resultExpressions();
                        int i = 0;
                        int length = resultExpressions == null ? 0 : resultExpressions.length;
                        while (i < length) {
                            Expression resultExpression = resultExpressions[i];
                            if (!this.addConstraintsToC_OneExpr(resultExpression, c, r.original(), r, method)) {
                                return false;
                            }
                            ++i;
                        }
                    }
                }
            }
        } else {
            if (expri instanceof Invocation && expri.isPolyExpression()) {
                if (substFIsProperType) {
                    return true;
                }
                Invocation invocation = (Invocation)((Object)expri);
                MethodBinding innerMethod = invocation.binding();
                if (innerMethod == null) {
                    return true;
                }
                Expression[] arguments = invocation.arguments();
                TypeBinding[] argumentTypes = arguments == null ? Binding.NO_PARAMETERS : new TypeBinding[arguments.length];
                int i = 0;
                while (i < argumentTypes.length) {
                    argumentTypes[i] = arguments[i].resolvedType;
                    ++i;
                }
                InferenceContext18 innerContext = null;
                if (innerMethod instanceof ParameterizedGenericMethodBinding) {
                    innerContext = invocation.getInferenceContext((ParameterizedGenericMethodBinding)innerMethod);
                }
                if (innerContext != null) {
                    MethodBinding shallowMethod = innerMethod.shallowOriginal();
                    innerContext.outerContext = this;
                    if (innerContext.stepCompleted < 1) {
                        innerContext.inferInvocationApplicability(shallowMethod, argumentTypes, shallowMethod.isConstructor());
                    }
                    if (!innerContext.computeB3(invocation, substF, shallowMethod)) {
                        return false;
                    }
                    if (innerContext.addConstraintsToC(arguments, c, innerMethod.genericMethod(), innerContext.inferenceKind, invocation)) {
                        this.currentBounds.addBounds(innerContext.currentBounds, this.environment);
                        return true;
                    }
                    return false;
                }
                int applicabilityKind = this.getInferenceKind(innerMethod, argumentTypes);
                return this.addConstraintsToC(arguments, c, innerMethod.genericMethod(), applicabilityKind, invocation);
            }
            if (expri instanceof ConditionalExpression) {
                ConditionalExpression ce = (ConditionalExpression)expri;
                return this.addConstraintsToC_OneExpr(ce.valueIfTrue, c, fsi, substF, method) && this.addConstraintsToC_OneExpr(ce.valueIfFalse, c, fsi, substF, method);
            }
            if (expri instanceof SwitchExpression) {
                SwitchExpression se = (SwitchExpression)expri;
                for (Expression re : se.resultExpressions) {
                    if (this.addConstraintsToC_OneExpr(re, c, fsi, substF, method)) continue;
                    return false;
                }
                return true;
            }
        }
        return true;
    }

    protected int getInferenceKind(MethodBinding nonGenericMethod, TypeBinding[] argumentTypes) {
        switch (this.scope.parameterCompatibilityLevel(nonGenericMethod, argumentTypes)) {
            case 1: {
                return 2;
            }
            case 2: {
                return 3;
            }
        }
        return 1;
    }

    public ReferenceBinding inferFunctionalInterfaceParameterization(LambdaExpression lambda, BlockScope blockScope, ParameterizedTypeBinding targetTypeWithWildCards) {
        TypeBinding[] q = this.createBoundsForFunctionalInterfaceParameterizationInference(targetTypeWithWildCards);
        if (q != null && q.length == lambda.arguments().length && this.reduceWithEqualityConstraints(lambda.argumentTypes(), q)) {
            ReferenceBinding genericType = targetTypeWithWildCards.genericType();
            TypeBinding[] a = targetTypeWithWildCards.arguments;
            TypeBinding[] aprime = this.getFunctionInterfaceArgumentSolutions(a);
            ParameterizedTypeBinding ptb = blockScope.environment().createParameterizedType(genericType, aprime, targetTypeWithWildCards.enclosingType());
            TypeVariableBinding[] vars = ptb.genericType().typeVariables();
            ParameterizedTypeBinding captured = ptb.capture(blockScope, lambda.sourceStart, lambda.sourceEnd);
            int i = 0;
            while (i < vars.length) {
                if (vars[i].boundCheck(captured, aprime[i], blockScope, lambda) == TypeConstants.BoundCheckStatus.MISMATCH) {
                    return null;
                }
                ++i;
            }
            return ptb;
        }
        return targetTypeWithWildCards;
    }

    TypeBinding[] createBoundsForFunctionalInterfaceParameterizationInference(ParameterizedTypeBinding functionalInterface) {
        TypeBinding[] a;
        if (this.currentBounds == null) {
            this.currentBounds = new BoundSet();
        }
        if ((a = functionalInterface.arguments) == null) {
            return null;
        }
        InferenceVariable[] alpha = this.addInitialTypeVariableSubstitutions(a);
        int i = 0;
        while (i < a.length) {
            block10: {
                TypeBound bound;
                block9: {
                    block8: {
                        if (a[i].kind() != 516) break block8;
                        WildcardBinding wildcard = (WildcardBinding)a[i];
                        switch (wildcard.boundKind) {
                            case 1: {
                                bound = new TypeBound(alpha[i], wildcard.allBounds(), 2);
                                break block9;
                            }
                            case 2: {
                                bound = new TypeBound(alpha[i], wildcard.bound, 3);
                                break block9;
                            }
                            case 0: {
                                bound = new TypeBound(alpha[i], this.object, 2);
                                break block9;
                            }
                        }
                        break block10;
                    }
                    bound = new TypeBound(alpha[i], a[i], 4);
                }
                this.currentBounds.addBound(bound, this.environment);
            }
            ++i;
        }
        TypeBinding falpha = this.substitute(functionalInterface);
        return falpha.getSingleAbstractMethod((Scope)this.scope, (boolean)true).parameters;
    }

    public boolean reduceWithEqualityConstraints(TypeBinding[] p, TypeBinding[] q) {
        if (p != null) {
            int i = 0;
            while (i < p.length) {
                try {
                    if (!this.reduceAndIncorporate(ConstraintTypeFormula.create(p[i], q[i], 4))) {
                        return false;
                    }
                }
                catch (InferenceFailureException inferenceFailureException) {
                    return false;
                }
                ++i;
            }
        }
        return true;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean isMoreSpecificThan(MethodBinding m1, MethodBinding m2, boolean isVarArgs, boolean isVarArgs2) {
        if (isVarArgs != isVarArgs2) {
            return isVarArgs2;
        }
        Expression[] arguments = this.invocationArguments;
        int numInvocArgs = arguments == null ? 0 : arguments.length;
        TypeVariableBinding[] p = m2.typeVariables();
        TypeBinding[] s = m1.parameters;
        TypeBinding[] t = new TypeBinding[m2.parameters.length];
        this.createInitialBoundSet(p);
        int i = 0;
        while (i < t.length) {
            t[i] = this.substitute(m2.parameters[i]);
            ++i;
        }
        try {
            i = 0;
            while (true) {
                TypeBinding ti;
                if (i >= numInvocArgs) {
                    TypeBinding tkplus1;
                    TypeBinding skplus1;
                    if (t.length != numInvocArgs + 1 || this.reduceAndIncorporate(ConstraintTypeFormula.create(skplus1 = InferenceContext18.getParameter(s, numInvocArgs, true), tkplus1 = InferenceContext18.getParameter(t, numInvocArgs, true), 2))) break;
                    return false;
                }
                TypeBinding si = InferenceContext18.getParameter(s, i, isVarArgs);
                Boolean result = this.moreSpecificMain(si, ti = InferenceContext18.getParameter(t, i, isVarArgs), this.invocationArguments[i]);
                if (result == Boolean.FALSE) {
                    return false;
                }
                if (result == null && !this.reduceAndIncorporate(ConstraintTypeFormula.create(si, ti, 2))) {
                    return false;
                }
                ++i;
            }
            return this.solve() != null;
        }
        catch (InferenceFailureException inferenceFailureException) {
            return false;
        }
    }

    private Boolean moreSpecificMain(TypeBinding si, TypeBinding ti, Expression expri) throws InferenceFailureException {
        if (si.isProperType(true) && ti.isProperType(true)) {
            return expri.sIsMoreSpecific(si, ti, this.scope) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (!ti.isFunctionalInterface(this.scope)) {
            return null;
        }
        TypeBinding funcI = ti.original();
        if (si.isFunctionalInterface(this.scope)) {
            if (this.siSuperI(si, funcI) || this.siSubI(si, funcI)) {
                return null;
            }
            if (si instanceof IntersectionTypeBinding18) {
                int i;
                ReferenceBinding[] elements;
                block9: {
                    elements = ((IntersectionTypeBinding18)si).intersectingTypes;
                    i = 0;
                    while (i < elements.length) {
                        if (this.siSuperI(elements[i], funcI)) {
                            ++i;
                            continue;
                        }
                        break block9;
                    }
                    return null;
                }
                i = 0;
                while (i < elements.length) {
                    if (this.siSubI(elements[i], funcI)) {
                        return null;
                    }
                    ++i;
                }
            }
            TypeBinding siCapture = si.capture(this.scope, expri.sourceStart, expri.sourceEnd);
            MethodBinding sam = siCapture.getSingleAbstractMethod(this.scope, false);
            TypeBinding[] u = sam.parameters;
            TypeBinding r1 = sam.isConstructor() ? sam.declaringClass : sam.returnType;
            sam = ti.getSingleAbstractMethod(this.scope, true);
            TypeBinding[] v = sam.parameters;
            TypeBinding r2 = sam.isConstructor() ? sam.declaringClass : sam.returnType;
            return this.checkExpression(expri, u, r1, v, r2);
        }
        return null;
    }

    private boolean checkExpression(Expression expri, TypeBinding[] u, TypeBinding r1, TypeBinding[] v, TypeBinding r2) throws InferenceFailureException {
        if (expri instanceof LambdaExpression && !((LambdaExpression)expri).argumentsTypeElided()) {
            block24: {
                int i = 0;
                while (i < u.length) {
                    if (!this.reduceAndIncorporate(ConstraintTypeFormula.create(u[i], v[i], 4))) {
                        return false;
                    }
                    ++i;
                }
                if (r2.id == 6) {
                    return true;
                }
                LambdaExpression lambda = (LambdaExpression)expri;
                Expression[] results = lambda.resultExpressions();
                if (results != Expression.NO_EXPRESSIONS) {
                    int i2;
                    block23: {
                        if (r1.isFunctionalInterface(this.scope) && r2.isFunctionalInterface(this.scope) && !r1.isCompatibleWith(r2) && !r2.isCompatibleWith(r1)) {
                            int i3 = 0;
                            while (i3 < results.length) {
                                if (!this.checkExpression(results[i3], u, r1, v, r2)) {
                                    return false;
                                }
                                ++i3;
                            }
                            return true;
                        }
                        if (r1.isPrimitiveType() && !r2.isPrimitiveType()) {
                            i2 = 0;
                            while (i2 < results.length) {
                                if (!results[i2].isPolyExpression() && (results[i2].resolvedType == null || results[i2].resolvedType.isPrimitiveType())) {
                                    ++i2;
                                    continue;
                                }
                                break block23;
                            }
                            return true;
                        }
                    }
                    if (r2.isPrimitiveType() && !r1.isPrimitiveType()) {
                        i2 = 0;
                        while (i2 < results.length) {
                            if (!results[i2].isPolyExpression() && results[i2].resolvedType != null && !results[i2].resolvedType.isPrimitiveType() || results[i2].isPolyExpression()) {
                                ++i2;
                                continue;
                            }
                            break block24;
                        }
                        return true;
                    }
                }
            }
            return this.reduceAndIncorporate(ConstraintTypeFormula.create(r1, r2, 2));
        }
        if (expri instanceof ReferenceExpression && ((ReferenceExpression)expri).isExactMethodReference()) {
            TypeBinding returnType;
            ReferenceExpression reference = (ReferenceExpression)expri;
            int i = 0;
            while (i < u.length) {
                if (!this.reduceAndIncorporate(ConstraintTypeFormula.create(u[i], v[i], 4))) {
                    return false;
                }
                ++i;
            }
            if (r2.id == 6) {
                return true;
            }
            MethodBinding method = reference.getExactMethod();
            TypeBinding typeBinding = returnType = method.isConstructor() ? method.declaringClass : method.returnType;
            if (r1.isPrimitiveType() && !r2.isPrimitiveType() && returnType.isPrimitiveType()) {
                return true;
            }
            if (r2.isPrimitiveType() && !r1.isPrimitiveType() && !returnType.isPrimitiveType()) {
                return true;
            }
            return this.reduceAndIncorporate(ConstraintTypeFormula.create(r1, r2, 2));
        }
        if (expri instanceof ConditionalExpression) {
            ConditionalExpression cond = (ConditionalExpression)expri;
            return this.checkExpression(cond.valueIfTrue, u, r1, v, r2) && this.checkExpression(cond.valueIfFalse, u, r1, v, r2);
        }
        if (expri instanceof SwitchExpression) {
            SwitchExpression se = (SwitchExpression)expri;
            for (Expression re : se.resultExpressions) {
                if (this.checkExpression(re, u, r1, v, r2)) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean siSuperI(TypeBinding si, TypeBinding funcI) {
        if (TypeBinding.equalsEquals(si, funcI) || TypeBinding.equalsEquals(si.original(), funcI)) {
            return true;
        }
        ReferenceBinding[] superIfcs = funcI.superInterfaces();
        if (superIfcs == null) {
            return false;
        }
        int i = 0;
        while (i < superIfcs.length) {
            if (this.siSuperI(si, superIfcs[i].original())) {
                return true;
            }
            ++i;
        }
        return false;
    }

    private boolean siSubI(TypeBinding si, TypeBinding funcI) {
        if (TypeBinding.equalsEquals(si, funcI) || TypeBinding.equalsEquals(si.original(), funcI)) {
            return true;
        }
        ReferenceBinding[] superIfcs = si.superInterfaces();
        if (superIfcs == null) {
            return false;
        }
        int i = 0;
        while (i < superIfcs.length) {
            if (this.siSubI(superIfcs[i], funcI)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public BoundSet solve(boolean inferringApplicability) throws InferenceFailureException {
        if (!this.reduce()) {
            return null;
        }
        if (!this.currentBounds.incorporate(this)) {
            return null;
        }
        if (inferringApplicability) {
            this.b2 = this.currentBounds.copy();
        }
        BoundSet solution = this.resolve(this.inferenceVariables);
        if (inferringApplicability && solution != null && this.finalConstraints != null) {
            ConstraintExpressionFormula[] constraintExpressionFormulaArray = this.finalConstraints;
            int n = this.finalConstraints.length;
            int n2 = 0;
            while (n2 < n) {
                ConstraintExpressionFormula constraint = constraintExpressionFormulaArray[n2];
                if (!constraint.left.isPolyExpression()) {
                    constraint.applySubstitution(solution, this.inferenceVariables);
                    if (!this.currentBounds.reduceOneConstraint(this, constraint)) {
                        return null;
                    }
                }
                ++n2;
            }
        }
        return solution;
    }

    public BoundSet solve() throws InferenceFailureException {
        return this.solve(false);
    }

    public BoundSet solve(InferenceVariable[] toResolve) throws InferenceFailureException {
        if (!this.reduce()) {
            return null;
        }
        if (!this.currentBounds.incorporate(this)) {
            return null;
        }
        return this.resolve(toResolve);
    }

    private boolean reduce() throws InferenceFailureException {
        int i = 0;
        while (this.initialConstraints != null && i < this.initialConstraints.length) {
            ConstraintFormula currentConstraint = this.initialConstraints[i];
            if (currentConstraint != null) {
                this.initialConstraints[i] = null;
                if (!this.currentBounds.reduceOneConstraint(this, currentConstraint)) {
                    return false;
                }
            }
            ++i;
        }
        this.initialConstraints = null;
        return true;
    }

    public boolean isResolved(BoundSet boundSet) {
        if (this.inferenceVariables != null) {
            int i = 0;
            while (i < this.inferenceVariables.length) {
                if (!boundSet.isInstantiated(this.inferenceVariables[i])) {
                    return false;
                }
                ++i;
            }
        }
        return true;
    }

    public TypeBinding[] getSolutions(TypeVariableBinding[] typeParameters, InvocationSite site, BoundSet boundSet) {
        int len = typeParameters.length;
        TypeBinding[] substitutions = new TypeBinding[len];
        InferenceVariable[] outerVariables = null;
        if (this.outerContext != null && this.outerContext.stepCompleted < 2) {
            outerVariables = this.outerContext.inferenceVariables;
        }
        int i = 0;
        while (i < typeParameters.length) {
            int j = 0;
            while (j < this.inferenceVariables.length) {
                InferenceVariable variable = this.inferenceVariables[j];
                if (InferenceContext18.isSameSite(variable.site, site) && TypeBinding.equalsEquals(variable.typeParameter, typeParameters[i])) {
                    TypeBinding outerVar = null;
                    if (outerVariables != null && (outerVar = boundSet.getEquivalentOuterVariable(variable, outerVariables)) != null) {
                        substitutions[i] = outerVar;
                        break;
                    }
                    substitutions[i] = boundSet.getInstantiation(variable, this.environment);
                    break;
                }
                ++j;
            }
            if (substitutions[i] == null) {
                return null;
            }
            ++i;
        }
        return substitutions;
    }

    public boolean reduceAndIncorporate(ConstraintFormula constraint) throws InferenceFailureException {
        return this.currentBounds.reduceOneConstraint(this, constraint);
    }

    private BoundSet resolve(InferenceVariable[] toResolve) throws InferenceFailureException {
        BoundSet tmpBoundSet;
        block30: {
            Set<InferenceVariable> variableSet;
            this.captureId = 0;
            tmpBoundSet = this.currentBounds;
            if (this.inferenceVariables == null) break block30;
            while ((variableSet = this.getSmallestVariableSet(tmpBoundSet, toResolve)) != null) {
                int j;
                InferenceVariable[] variables;
                int numVars;
                int oldNumUninstantiated;
                block29: {
                    oldNumUninstantiated = tmpBoundSet.numUninstantiatedVariables(this.inferenceVariables);
                    numVars = variableSet.size();
                    if (numVars <= 0) continue;
                    variables = variableSet.toArray(new InferenceVariable[numVars]);
                    if (!tmpBoundSet.hasCaptureBound(variableSet)) {
                        BoundSet prevBoundSet = tmpBoundSet;
                        tmpBoundSet = tmpBoundSet.copy();
                        j = 0;
                        while (j < variables.length) {
                            InferenceVariable variable = variables[j];
                            TypeBinding[] lowerBounds = tmpBoundSet.lowerBounds(variable, true);
                            if (lowerBounds != Binding.NO_TYPES) {
                                TypeBinding lub = this.scope.lowerUpperBound(lowerBounds);
                                if (lub == TypeBinding.VOID || lub == null) {
                                    return null;
                                }
                                tmpBoundSet.addBound(new TypeBound(variable, lub, 4), this.environment);
                            } else {
                                TypeBinding[] upperBounds = tmpBoundSet.upperBounds(variable, true);
                                if (tmpBoundSet.inThrows.contains(variable.prototype()) && tmpBoundSet.hasOnlyTrivialExceptionBounds(variable, upperBounds)) {
                                    TypeBinding runtimeException = this.scope.getType(TypeConstants.JAVA_LANG_RUNTIMEEXCEPTION, 3);
                                    tmpBoundSet.addBound(new TypeBound(variable, runtimeException, 4), this.environment);
                                } else {
                                    TypeBinding glb = this.object;
                                    if (upperBounds != Binding.NO_TYPES) {
                                        if (upperBounds.length == 1) {
                                            glb = upperBounds[0];
                                        } else {
                                            TypeBinding[] glbs = Scope.greaterLowerBound(upperBounds, this.scope, this.environment);
                                            if (glbs == null) {
                                                return null;
                                            }
                                            if (glbs.length == 1) {
                                                glb = glbs[0];
                                            } else {
                                                glb = this.intersectionFromGlb(glbs);
                                                if (glb == null) {
                                                    tmpBoundSet = prevBoundSet;
                                                    break block29;
                                                }
                                            }
                                        }
                                    }
                                    tmpBoundSet.addBound(new TypeBound(variable, glb, 4), this.environment);
                                }
                            }
                            ++j;
                        }
                        if (tmpBoundSet.incorporate(this)) continue;
                        tmpBoundSet = prevBoundSet;
                    }
                }
                Sorting.sortInferenceVariables(variables);
                final CaptureBinding18[] zs = new CaptureBinding18[numVars];
                j = 0;
                while (j < numVars) {
                    zs[j] = this.freshCapture(variables[j]);
                    ++j;
                }
                final BoundSet kurrentBoundSet = tmpBoundSet;
                Substitution theta = new Substitution(){

                    @Override
                    public LookupEnvironment environment() {
                        return InferenceContext18.this.environment;
                    }

                    @Override
                    public boolean isRawSubstitution() {
                        return false;
                    }

                    @Override
                    public TypeBinding substitute(TypeVariableBinding typeVariable) {
                        InferenceVariable inferenceVariable;
                        TypeBinding instantiation;
                        int j = 0;
                        while (j < numVars) {
                            if (TypeBinding.equalsEquals(variables[j], typeVariable)) {
                                return zs[j];
                            }
                            ++j;
                        }
                        if (typeVariable instanceof InferenceVariable && (instantiation = kurrentBoundSet.getInstantiation(inferenceVariable = (InferenceVariable)typeVariable, null)) != null) {
                            return instantiation;
                        }
                        return typeVariable;
                    }
                };
                int j2 = 0;
                while (j2 < numVars) {
                    block32: {
                        CaptureBinding18 zsj;
                        InferenceVariable variable;
                        block31: {
                            TypeBinding[] upperBounds;
                            TypeBinding lub;
                            variable = variables[j2];
                            zsj = zs[j2];
                            TypeBinding[] lowerBounds = tmpBoundSet.lowerBounds(variable, true);
                            if (lowerBounds != Binding.NO_TYPES && (lub = this.scope.lowerUpperBound(lowerBounds)) != TypeBinding.VOID && lub != null) {
                                zsj.lowerBound = lub;
                            }
                            if ((upperBounds = tmpBoundSet.upperBounds(variable, false)) == Binding.NO_TYPES) break block31;
                            int k = 0;
                            while (k < upperBounds.length) {
                                upperBounds[k] = Scope.substitute(theta, upperBounds[k]);
                                ++k;
                            }
                            if (!this.setUpperBounds(zsj, upperBounds)) break block32;
                        }
                        if (tmpBoundSet == this.currentBounds) {
                            tmpBoundSet = tmpBoundSet.copy();
                        }
                        Iterator<ParameterizedTypeBinding> captureKeys = tmpBoundSet.captures.keySet().iterator();
                        HashSet<ParameterizedTypeBinding> toRemove = new HashSet<ParameterizedTypeBinding>();
                        block5: while (captureKeys.hasNext()) {
                            ParameterizedTypeBinding key = captureKeys.next();
                            int len = key.arguments.length;
                            int i = 0;
                            while (i < len) {
                                if (TypeBinding.equalsEquals(key.arguments[i], variable)) {
                                    toRemove.add(key);
                                    continue block5;
                                }
                                ++i;
                            }
                        }
                        captureKeys = toRemove.iterator();
                        while (captureKeys.hasNext()) {
                            tmpBoundSet.captures.remove(captureKeys.next());
                        }
                        tmpBoundSet.addBound(new TypeBound(variable, zsj, 4), this.environment);
                    }
                    ++j2;
                }
                if (tmpBoundSet.incorporate(this)) {
                    if (tmpBoundSet.numUninstantiatedVariables(this.inferenceVariables) != oldNumUninstantiated) continue;
                    return null;
                }
                return null;
            }
        }
        return tmpBoundSet;
    }

    private TypeBinding intersectionFromGlb(TypeBinding[] glbs) {
        ReferenceBinding[] refGlbs = new ReferenceBinding[glbs.length];
        int i = 0;
        while (i < glbs.length) {
            TypeBinding typeBinding = glbs[i];
            if (!(typeBinding instanceof ReferenceBinding)) {
                return null;
            }
            refGlbs[i] = (ReferenceBinding)typeBinding;
            ++i;
        }
        IntersectionTypeBinding18 intersection = (IntersectionTypeBinding18)this.environment.createIntersectionType18(refGlbs);
        if (ReferenceBinding.isConsistentIntersection(intersection.intersectingTypes)) {
            return intersection;
        }
        return null;
    }

    private CaptureBinding18 freshCapture(InferenceVariable variable) {
        int id = this.captureId++;
        char[] sourceName = CharOperation.concat("Z".toCharArray(), '#', String.valueOf(id).toCharArray(), '-', variable.sourceName);
        int start = this.currentInvocation != null ? this.currentInvocation.sourceStart() : 0;
        int end = this.currentInvocation != null ? this.currentInvocation.sourceEnd() : 0;
        return new CaptureBinding18(this.scope.enclosingSourceType(), sourceName, variable.typeParameter.shortReadableName(), start, end, id, this.environment);
    }

    private boolean setUpperBounds(CaptureBinding18 typeVariable, TypeBinding[] substitutedUpperBounds) {
        if (substitutedUpperBounds.length == 1) {
            return typeVariable.setUpperBounds(substitutedUpperBounds, this.object);
        }
        TypeBinding[] glbs = Scope.greaterLowerBound(substitutedUpperBounds, this.scope, this.environment);
        if (glbs == null) {
            return false;
        }
        if (typeVariable.lowerBound != null) {
            int i = 0;
            while (i < glbs.length) {
                if (!typeVariable.lowerBound.isCompatibleWith(glbs[i])) {
                    return false;
                }
                ++i;
            }
        }
        InferenceContext18.sortTypes(glbs);
        return typeVariable.setUpperBounds(glbs, this.object);
    }

    static void sortTypes(TypeBinding[] types) {
        Arrays.sort(types, new Comparator<TypeBinding>(){

            @Override
            public int compare(TypeBinding o1, TypeBinding o2) {
                int i1 = o1.id;
                int i2 = o2.id;
                return i1 < i2 ? -1 : (i1 == i2 ? 0 : 1);
            }
        });
    }

    private Set<InferenceVariable> getSmallestVariableSet(BoundSet bounds, InferenceVariable[] subSet) {
        HashSet<InferenceVariable> v = new HashSet<InferenceVariable>();
        HashMap<InferenceVariable, HashSet<InferenceVariable>> dependencies = new HashMap<InferenceVariable, HashSet<InferenceVariable>>();
        InferenceVariable[] inferenceVariableArray = subSet;
        int n = subSet.length;
        int n2 = 0;
        while (n2 < n) {
            InferenceVariable iv = inferenceVariableArray[n2];
            HashSet<InferenceVariable> tmp = new HashSet<InferenceVariable>();
            this.addDependencies(bounds, tmp, iv);
            dependencies.put(iv, tmp);
            v.addAll(tmp);
            ++n2;
        }
        int min = Integer.MAX_VALUE;
        HashSet<InferenceVariable> result = null;
        for (InferenceVariable currentVariable : v) {
            int cur;
            if (bounds.isInstantiated(currentVariable)) continue;
            HashSet<InferenceVariable> set = (HashSet<InferenceVariable>)dependencies.get(currentVariable);
            if (set == null) {
                set = new HashSet<InferenceVariable>();
                this.addDependencies(bounds, set, currentVariable);
            }
            if ((cur = set.size()) == 1) {
                return set;
            }
            if (cur >= min) continue;
            result = set;
            min = cur;
        }
        return result;
    }

    private void addDependencies(BoundSet boundSet, Set<InferenceVariable> variableSet, InferenceVariable currentVariable) {
        if (boundSet.isInstantiated(currentVariable)) {
            return;
        }
        if (!variableSet.add(currentVariable)) {
            return;
        }
        int j = 0;
        while (j < this.inferenceVariables.length) {
            InferenceVariable nextVariable = this.inferenceVariables[j];
            if (!TypeBinding.equalsEquals(nextVariable, currentVariable) && boundSet.dependsOnResolutionOf(currentVariable, nextVariable)) {
                this.addDependencies(boundSet, variableSet, nextVariable);
            }
            ++j;
        }
    }

    /*
     * WARNING - void declaration
     */
    private ConstraintFormula pickFromCycle(Set<ConstraintFormula> c) {
        HashMap<ConstraintFormula, Set<ConstraintFormula>> dependencies = new HashMap<ConstraintFormula, Set<ConstraintFormula>>();
        HashSet cycles = new HashSet();
        for (ConstraintFormula constraint : c) {
            Collection<InferenceVariable> infVars = constraint.inputVariables(this);
            for (ConstraintFormula constraintFormula : c) {
                void var9_19;
                if (constraintFormula == constraint || !this.dependsOn(infVars, constraintFormula.outputVariables(this))) continue;
                Set set = (Set)dependencies.get(constraint);
                if (set == null) {
                    HashSet hashSet = new HashSet();
                    dependencies.put(constraint, hashSet);
                }
                var9_19.add(constraintFormula);
                HashSet nodesInCycle = new HashSet();
                if (!this.isReachable(dependencies, constraintFormula, constraint, new HashSet<ConstraintFormula>(), nodesInCycle)) continue;
                cycles.addAll(nodesInCycle);
            }
        }
        HashSet<ConstraintFormula> outside = new HashSet<ConstraintFormula>(c);
        outside.removeAll(cycles);
        Set<Object> candidatesII = new HashSet();
        block2: for (ConstraintFormula candidate : cycles) {
            Collection<InferenceVariable> collection = candidate.inputVariables(this);
            for (ConstraintFormula constraintFormula : outside) {
                if (this.dependsOn(collection, constraintFormula.outputVariables(this))) continue block2;
            }
            candidatesII.add(candidate);
        }
        if (candidatesII.isEmpty()) {
            candidatesII = c;
        }
        Set<Object> candidatesIII = new HashSet();
        for (ConstraintFormula constraintFormula : candidatesII) {
            if (!(constraintFormula instanceof ConstraintExpressionFormula)) continue;
            candidatesIII.add(constraintFormula);
        }
        if (candidatesIII.isEmpty()) {
            candidatesIII = candidatesII;
        } else {
            HashMap<ConstraintExpressionFormula, ConstraintExpressionFormula> hashMap = new HashMap<ConstraintExpressionFormula, ConstraintExpressionFormula>();
            for (ConstraintFormula constraintFormula : candidatesIII) {
                ConstraintExpressionFormula oneCEF = (ConstraintExpressionFormula)constraintFormula;
                Expression exprOne = oneCEF.left;
                for (ConstraintFormula constraintFormula2 : candidatesIII) {
                    ConstraintExpressionFormula previous;
                    if (constraintFormula == constraintFormula2) continue;
                    ConstraintExpressionFormula twoCEF = (ConstraintExpressionFormula)constraintFormula2;
                    Expression exprTwo = twoCEF.left;
                    if (!this.doesExpressionContain(exprOne, exprTwo) || (previous = (ConstraintExpressionFormula)hashMap.get(constraintFormula2)) != null && !this.doesExpressionContain(previous.left, exprOne)) continue;
                    hashMap.put(twoCEF, oneCEF);
                }
            }
            HashMap<ConstraintExpressionFormula, Set<ConstraintExpressionFormula>> hashMap2 = new HashMap<ConstraintExpressionFormula, Set<ConstraintExpressionFormula>>();
            for (Map.Entry entry : hashMap.entrySet()) {
                void var12_35;
                ConstraintExpressionFormula parent = (ConstraintExpressionFormula)entry.getValue();
                Set set = (Set)hashMap2.get(parent);
                if (set == null) {
                    HashSet hashSet = new HashSet();
                    hashMap2.put(parent, hashSet);
                }
                var12_35.add((ConstraintExpressionFormula)entry.getKey());
            }
            int n = -1;
            ConstraintExpressionFormula candidate = null;
            for (ConstraintExpressionFormula parent : hashMap2.keySet()) {
                int n2;
                int rank = this.rankNode(parent, hashMap, hashMap2);
                if (rank <= n2) continue;
                n2 = rank;
                candidate = parent;
            }
            if (candidate != null) {
                return candidate;
            }
        }
        if (candidatesIII.isEmpty()) {
            throw new IllegalStateException("cannot pick constraint from cyclic set");
        }
        return (ConstraintFormula)candidatesIII.iterator().next();
    }

    private boolean dependsOn(Collection<InferenceVariable> inputsOfFirst, Collection<InferenceVariable> outputsOfOther) {
        for (InferenceVariable iv : inputsOfFirst) {
            for (InferenceVariable otherIV : outputsOfOther) {
                if (!this.currentBounds.dependsOnResolutionOf(iv, otherIV)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean isReachable(Map<ConstraintFormula, Set<ConstraintFormula>> deps, ConstraintFormula from, ConstraintFormula to, Set<ConstraintFormula> nodesVisited, Set<ConstraintFormula> nodesInCycle) {
        if (from == to) {
            nodesInCycle.add(from);
            return true;
        }
        if (!nodesVisited.add(from)) {
            return false;
        }
        Set<ConstraintFormula> targetSet = deps.get(from);
        if (targetSet != null) {
            for (ConstraintFormula tgt : targetSet) {
                if (!this.isReachable(deps, tgt, to, nodesVisited, nodesInCycle)) continue;
                nodesInCycle.add(from);
                return true;
            }
        }
        return false;
    }

    private boolean doesExpressionContain(Expression exprOne, Expression exprTwo) {
        if (exprTwo.sourceStart > exprOne.sourceStart) {
            return exprTwo.sourceEnd <= exprOne.sourceEnd;
        }
        if (exprTwo.sourceStart == exprOne.sourceStart) {
            return exprTwo.sourceEnd < exprOne.sourceEnd;
        }
        return false;
    }

    private int rankNode(ConstraintExpressionFormula parent, Map<ConstraintExpressionFormula, ConstraintExpressionFormula> expressionContainedBy, Map<ConstraintExpressionFormula, Set<ConstraintExpressionFormula>> containmentForest) {
        if (expressionContainedBy.get(parent) != null) {
            return -1;
        }
        Set<ConstraintExpressionFormula> children = containmentForest.get(parent);
        if (children == null) {
            return 1;
        }
        int sum = 1;
        for (ConstraintExpressionFormula child : children) {
            int cRank = this.rankNode(child, expressionContainedBy, containmentForest);
            if (cRank <= 0) continue;
            sum += cRank;
        }
        return sum;
    }

    private Set<ConstraintFormula> findBottomSet(Set<ConstraintFormula> constraints, Set<InferenceVariable> allOutputVariables, List<Set<InferenceVariable>> components) {
        HashSet<ConstraintFormula> result = new HashSet<ConstraintFormula>();
        block0: for (ConstraintFormula constraint : constraints) {
            for (InferenceVariable in : constraint.inputVariables(this)) {
                if (this.canInfluenceAnyOf(in, allOutputVariables, components)) continue block0;
            }
            result.add(constraint);
        }
        return result;
    }

    private boolean canInfluenceAnyOf(InferenceVariable in, Set<InferenceVariable> allOuts, List<Set<InferenceVariable>> components) {
        for (Set<InferenceVariable> component : components) {
            if (!component.contains(in)) continue;
            for (InferenceVariable out : allOuts) {
                if (!component.contains(out)) continue;
                return true;
            }
            return false;
        }
        return false;
    }

    Set<InferenceVariable> allOutputVariables(Set<ConstraintFormula> constraints) {
        HashSet<InferenceVariable> result = new HashSet<InferenceVariable>();
        Iterator<ConstraintFormula> it = constraints.iterator();
        while (it.hasNext()) {
            result.addAll(it.next().outputVariables(this));
        }
        return result;
    }

    private TypeBinding[] varArgTypes(TypeBinding[] parameters, int k) {
        TypeBinding[] types = new TypeBinding[k];
        int declaredLength = parameters.length - 1;
        System.arraycopy(parameters, 0, types, 0, declaredLength);
        TypeBinding last = ((ArrayBinding)parameters[declaredLength]).elementsType();
        int i = declaredLength;
        while (i < k) {
            types[i] = last;
            ++i;
        }
        return types;
    }

    public SuspendedInferenceRecord enterPolyInvocation(InvocationSite invocation, Expression[] innerArguments) {
        SuspendedInferenceRecord record = new SuspendedInferenceRecord(this.currentInvocation, this.invocationArguments, this.inferenceVariables, this.inferenceKind, this.usesUncheckedConversion);
        this.inferenceVariables = null;
        this.invocationArguments = innerArguments;
        this.currentInvocation = invocation;
        this.usesUncheckedConversion = false;
        return record;
    }

    public SuspendedInferenceRecord enterLambda(LambdaExpression lambda) {
        SuspendedInferenceRecord record = new SuspendedInferenceRecord(this.currentInvocation, this.invocationArguments, this.inferenceVariables, this.inferenceKind, this.usesUncheckedConversion);
        this.inferenceVariables = null;
        this.invocationArguments = null;
        this.usesUncheckedConversion = false;
        return record;
    }

    public void integrateInnerInferenceB2(InferenceContext18 innerCtx) {
        this.currentBounds.addBounds(innerCtx.b2, this.environment);
        this.inferenceVariables = innerCtx.inferenceVariables;
        this.inferenceKind = innerCtx.inferenceKind;
        if (!InferenceContext18.isSameSite(innerCtx.currentInvocation, this.currentInvocation)) {
            innerCtx.outerContext = this;
        }
        this.usesUncheckedConversion = innerCtx.usesUncheckedConversion;
    }

    public void resumeSuspendedInference(SuspendedInferenceRecord record, InferenceContext18 innerContext) {
        boolean firstTime = this.collectInnerContext(innerContext);
        if (this.inferenceVariables == null) {
            this.inferenceVariables = record.inferenceVariables;
        } else if (!firstTime) {
            LinkedHashSet<InferenceVariable> uniqueVariables = new LinkedHashSet<InferenceVariable>();
            uniqueVariables.addAll(Arrays.asList(record.inferenceVariables));
            uniqueVariables.addAll(Arrays.asList(this.inferenceVariables));
            this.inferenceVariables = uniqueVariables.toArray(new InferenceVariable[uniqueVariables.size()]);
        } else {
            int l1 = this.inferenceVariables.length;
            int l2 = record.inferenceVariables.length;
            this.inferenceVariables = new InferenceVariable[l1 + l2];
            System.arraycopy(this.inferenceVariables, 0, this.inferenceVariables, l2, l1);
            System.arraycopy(record.inferenceVariables, 0, this.inferenceVariables, 0, l2);
        }
        this.currentInvocation = record.site;
        this.invocationArguments = record.invocationArguments;
        this.inferenceKind = record.inferenceKind;
        this.usesUncheckedConversion = record.usesUncheckedConversion;
    }

    private boolean collectInnerContext(InferenceContext18 innerContext) {
        if (innerContext == null) {
            return false;
        }
        if (this.seenInnerContexts == null) {
            this.seenInnerContexts = new HashSet<InferenceContext18>();
        }
        return this.seenInnerContexts.add(innerContext);
    }

    private Substitution getResultSubstitution(final BoundSet result) {
        return new Substitution(){

            @Override
            public LookupEnvironment environment() {
                return InferenceContext18.this.environment;
            }

            @Override
            public boolean isRawSubstitution() {
                return false;
            }

            @Override
            public TypeBinding substitute(TypeVariableBinding typeVariable) {
                TypeBinding instantiation;
                if (typeVariable instanceof InferenceVariable && (instantiation = result.getInstantiation((InferenceVariable)typeVariable, InferenceContext18.this.environment)) != null) {
                    return instantiation;
                }
                return typeVariable;
            }
        };
    }

    public boolean isVarArgs() {
        return this.inferenceKind == 3;
    }

    public static TypeBinding getParameter(TypeBinding[] parameters, int rank, boolean isVarArgs) {
        if (isVarArgs) {
            if (rank >= parameters.length - 1) {
                return ((ArrayBinding)parameters[parameters.length - 1]).elementsType();
            }
        } else if (rank >= parameters.length) {
            return null;
        }
        return parameters[rank];
    }

    public MethodBinding getReturnProblemMethodIfNeeded(TypeBinding expectedType, MethodBinding method) {
        if (expectedType != null && !(method.original() instanceof SyntheticFactoryMethodBinding) && (method.returnType instanceof ReferenceBinding || method.returnType instanceof ArrayBinding)) {
            if (!expectedType.isProperType(true)) {
                return null;
            }
            if (this.environment.convertToRawType(method.returnType.erasure(), false).isCompatibleWith(expectedType)) {
                return method;
            }
        }
        ProblemMethodBinding problemMethod = new ProblemMethodBinding(method, method.selector, method.parameters, 23);
        problemMethod.returnType = expectedType != null ? expectedType : method.returnType;
        problemMethod.inferenceContext = this;
        return problemMethod;
    }

    public String toString() {
        int i;
        StringBuffer buf = new StringBuffer("Inference Context");
        switch (this.stepCompleted) {
            case 0: {
                buf.append(" (initial)");
                break;
            }
            case 1: {
                buf.append(" (applicability inferred)");
                break;
            }
            case 2: {
                buf.append(" (type inferred)");
                break;
            }
            case 3: {
                buf.append(" (type inferred final)");
            }
        }
        switch (this.inferenceKind) {
            case 1: {
                buf.append(" (strict)");
                break;
            }
            case 2: {
                buf.append(" (loose)");
                break;
            }
            case 3: {
                buf.append(" (vararg)");
            }
        }
        if (this.currentBounds != null && this.isResolved(this.currentBounds)) {
            buf.append(" (resolved)");
        }
        buf.append('\n');
        if (this.inferenceVariables != null) {
            buf.append("Inference Variables:\n");
            i = 0;
            while (i < this.inferenceVariables.length) {
                buf.append('\t').append(this.inferenceVariables[i].sourceName).append("\t:\t");
                if (this.currentBounds != null && this.currentBounds.isInstantiated(this.inferenceVariables[i])) {
                    buf.append(this.currentBounds.getInstantiation(this.inferenceVariables[i], this.environment).readableName());
                } else {
                    buf.append("NOT INSTANTIATED");
                }
                buf.append('\n');
                ++i;
            }
        }
        if (this.initialConstraints != null) {
            buf.append("Initial Constraints:\n");
            i = 0;
            while (i < this.initialConstraints.length) {
                if (this.initialConstraints[i] != null) {
                    buf.append('\t').append(this.initialConstraints[i].toString()).append('\n');
                }
                ++i;
            }
        }
        if (this.currentBounds != null) {
            buf.append(this.currentBounds.toString());
        }
        return buf.toString();
    }

    public static ParameterizedTypeBinding parameterizedWithWildcard(TypeBinding type) {
        if (type == null || type.kind() != 260) {
            return null;
        }
        ParameterizedTypeBinding parameterizedType = (ParameterizedTypeBinding)type;
        TypeBinding[] arguments = parameterizedType.arguments;
        if (arguments != null) {
            int i = 0;
            while (i < arguments.length) {
                if (arguments[i].isWildcard()) {
                    return parameterizedType;
                }
                ++i;
            }
        }
        return null;
    }

    public TypeBinding[] getFunctionInterfaceArgumentSolutions(TypeBinding[] a) {
        int m = a.length;
        TypeBinding[] aprime = new TypeBinding[m];
        int i = 0;
        while (i < this.inferenceVariables.length) {
            InferenceVariable alphai = this.inferenceVariables[i];
            TypeBinding t = this.currentBounds.getInstantiation(alphai, this.environment);
            aprime[i] = t != null ? t : a[i];
            ++i;
        }
        return aprime;
    }

    public void recordUncheckedConversion(ConstraintTypeFormula constraint) {
        if (this.constraintsWithUncheckedConversion == null) {
            this.constraintsWithUncheckedConversion = new ArrayList<ConstraintFormula>();
        }
        this.constraintsWithUncheckedConversion.add(constraint);
        this.usesUncheckedConversion = true;
    }

    void reportUncheckedConversions(BoundSet solution) {
        if (this.constraintsWithUncheckedConversion != null) {
            int len = this.constraintsWithUncheckedConversion.size();
            Substitution substitution = this.getResultSubstitution(solution);
            int i = 0;
            while (i < len) {
                ConstraintTypeFormula constraint = (ConstraintTypeFormula)this.constraintsWithUncheckedConversion.get(i);
                TypeBinding expectedType = constraint.right;
                TypeBinding providedType = constraint.left;
                if (!expectedType.isProperType(true)) {
                    expectedType = Scope.substitute(substitution, expectedType);
                }
                if (!providedType.isProperType(true)) {
                    providedType = Scope.substitute(substitution, providedType);
                }
                ++i;
            }
        }
    }

    public boolean usesUncheckedConversion() {
        return this.constraintsWithUncheckedConversion != null;
    }

    public static void missingImplementation(String msg) {
        throw new UnsupportedOperationException(msg);
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public void forwardResults(BoundSet result, Invocation invocation, ParameterizedMethodBinding pmb, TypeBinding targetType) {
        if (targetType != null) {
            invocation.registerResult(targetType, pmb);
        }
        arguments = invocation.arguments();
        i = 0;
        length = arguments == null ? 0 : arguments.length;
        while (i < length) {
            expressions = arguments[i].getPolyExpressions();
            j = 0;
            jLength = expressions.length;
            while (j < jLength) {
                block9: {
                    block11: {
                        block10: {
                            expression = expressions[j];
                            if (!(expression instanceof Invocation) || (binding = (polyInvocation = (Invocation)expression).binding()) == null || !binding.isValidBinding()) break block9;
                            methodSubstitute /* !! */  = null;
                            if (!(binding instanceof ParameterizedGenericMethodBinding)) break block10;
                            shallowOriginal = binding.shallowOriginal();
                            solutions = this.getSolutions(shallowOriginal.typeVariables(), polyInvocation, result);
                            if (solutions == null) break block9;
                            methodSubstitute /* !! */  = this.environment.createParameterizedGenericMethod(shallowOriginal, solutions);
                            break block11;
                        }
                        if (binding.isConstructor() && binding instanceof ParameterizedMethodBinding) {
                            shallowOriginal = binding.shallowOriginal();
                            genericType = shallowOriginal.declaringClass;
                            solutions = this.getSolutions(genericType.typeVariables(), polyInvocation, result);
                            if (solutions != null) {
                                parameterizedType = this.environment.createParameterizedType(genericType, solutions, binding.declaringClass.enclosingType());
                                var22_23 = parameterizedType.methods();
                                var21_22 = var22_23.length;
                                var20_21 = 0;
                                while (var20_21 < var21_22) {
                                    parameterizedMethod = var22_23[var20_21];
                                    if (parameterizedMethod.original() == shallowOriginal) {
                                        methodSubstitute /* !! */  = (ParameterizedMethodBinding)parameterizedMethod;
                                        ** break;
                                    }
                                    ++var20_21;
                                }
                            }
                        }
                        break block9;
                    }
                    if (methodSubstitute /* !! */  != null && methodSubstitute /* !! */ .isValidBinding()) {
                        variableArity = pmb.isVarargs();
                        parameters = pmb.parameters;
                        if (variableArity && parameters.length == arguments.length && i == length - 1 && (returnType = methodSubstitute /* !! */ .returnType.capture(this.scope, expression.sourceStart, expression.sourceEnd)).isCompatibleWith(parameters[parameters.length - 1], this.scope)) {
                            variableArity = false;
                        }
                        parameterType = InferenceContext18.getParameter(parameters, i, variableArity);
                        this.forwardResults(result, polyInvocation, methodSubstitute /* !! */ , parameterType);
                    }
                }
                ++j;
            }
            ++i;
        }
    }

    public void cleanUp() {
        this.b2 = null;
        this.currentBounds = null;
    }

    private /* synthetic */ boolean lambda$1(ConstraintFormula constraintFormula) throws InferenceFailureException {
        return constraintFormula.inputVariables(this).isEmpty();
    }

    static interface InferenceOperation {
        public boolean perform() throws InferenceFailureException;
    }

    static class SuspendedInferenceRecord {
        InvocationSite site;
        Expression[] invocationArguments;
        InferenceVariable[] inferenceVariables;
        int inferenceKind;
        boolean usesUncheckedConversion;

        SuspendedInferenceRecord(InvocationSite site, Expression[] invocationArguments, InferenceVariable[] inferenceVariables, int inferenceKind, boolean usesUncheckedConversion) {
            this.site = site;
            this.invocationArguments = invocationArguments;
            this.inferenceVariables = inferenceVariables;
            this.inferenceKind = inferenceKind;
            this.usesUncheckedConversion = usesUncheckedConversion;
        }
    }
}

