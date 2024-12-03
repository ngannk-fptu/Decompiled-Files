/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ConstraintFormula;
import org.eclipse.jdt.internal.compiler.lookup.ConstraintTypeFormula;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InferenceFailureException;
import org.eclipse.jdt.internal.compiler.lookup.InferenceSubstitution;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReductionResult;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBound;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

class BoundSet {
    public static boolean enableOptimizationForBug543480 = true;
    static final BoundSet TRUE;
    static final BoundSet FALSE;
    HashMap<InferenceVariable, ThreeSets> boundsPerVariable = new HashMap();
    HashMap<ParameterizedTypeBinding, ParameterizedTypeBinding> captures = new HashMap();
    Set<InferenceVariable> inThrows = new HashSet<InferenceVariable>();
    private TypeBound[] incorporatedBounds = Binding.NO_TYPE_BOUNDS;
    private TypeBound[] unincorporatedBounds = new TypeBound[8];
    private int unincorporatedBoundsCount = 0;
    private TypeBound[] mostRecentBounds = new TypeBound[4];

    static {
        String enableOptimizationForBug543480Property = System.getProperty("enableOptimizationForBug543480");
        if (enableOptimizationForBug543480Property != null) {
            enableOptimizationForBug543480 = enableOptimizationForBug543480Property.equalsIgnoreCase("true");
        }
        TRUE = new BoundSet();
        FALSE = new BoundSet();
    }

    public void addBoundsFromTypeParameters(InferenceContext18 context, TypeVariableBinding[] typeParameters, InferenceVariable[] variables) {
        int length = typeParameters.length;
        int i = 0;
        while (i < length) {
            TypeVariableBinding typeParameter = typeParameters[i];
            InferenceVariable variable = variables[i];
            TypeBound[] someBounds = typeParameter.getTypeBounds(variable, new InferenceSubstitution(context));
            boolean hasProperBound = false;
            if (someBounds.length > 0) {
                hasProperBound = this.addBounds(someBounds, context.environment);
            }
            if (!hasProperBound) {
                this.addBound(new TypeBound(variable, context.object, 2), context.environment);
            }
            ++i;
        }
    }

    public TypeBound[] flatten() {
        int size = 0;
        Iterator<ThreeSets> outerIt = this.boundsPerVariable.values().iterator();
        while (outerIt.hasNext()) {
            size += outerIt.next().size();
        }
        if (size == 0) {
            return Binding.NO_TYPE_BOUNDS;
        }
        TypeBound[] collected = new TypeBound[size];
        outerIt = this.boundsPerVariable.values().iterator();
        int idx = 0;
        while (outerIt.hasNext()) {
            idx = outerIt.next().flattenInto(collected, idx);
        }
        return collected;
    }

    public BoundSet copy() {
        BoundSet copy = new BoundSet();
        if (!this.boundsPerVariable.isEmpty()) {
            for (Map.Entry<InferenceVariable, ThreeSets> entry : this.boundsPerVariable.entrySet()) {
                copy.boundsPerVariable.put(entry.getKey(), entry.getValue().copy());
            }
        }
        copy.inThrows.addAll(this.inThrows);
        copy.captures.putAll(this.captures);
        if (this.incorporatedBounds.length > 0) {
            copy.incorporatedBounds = new TypeBound[this.incorporatedBounds.length];
            System.arraycopy(this.incorporatedBounds, 0, copy.incorporatedBounds, 0, this.incorporatedBounds.length);
        }
        if (this.unincorporatedBoundsCount > 0) {
            copy.unincorporatedBounds = new TypeBound[this.unincorporatedBounds.length];
            System.arraycopy(this.unincorporatedBounds, 0, copy.unincorporatedBounds, 0, this.unincorporatedBounds.length);
        }
        copy.unincorporatedBoundsCount = this.unincorporatedBoundsCount;
        return copy;
    }

    public void addBound(TypeBound bound, LookupEnvironment environment) {
        if (bound.relation == 2 && bound.right.id == 1) {
            return;
        }
        if (bound.left == bound.right) {
            return;
        }
        int recent = 0;
        while (recent < 4) {
            if (bound.equals(this.mostRecentBounds[recent])) {
                if (environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                    TypeBound existing = this.mostRecentBounds[recent];
                    long boundNullBits = bound.right.tagBits & 0x180000000000000L;
                    long existingNullBits = existing.right.tagBits & 0x180000000000000L;
                    if (boundNullBits != existingNullBits) {
                        if (existingNullBits == 0L) {
                            existing.right = bound.right;
                        } else if (boundNullBits != 0L) {
                            existing.right = environment.createAnnotatedType(existing.right, environment.nullAnnotationsFromTagBits(boundNullBits));
                        }
                    }
                }
                return;
            }
            ++recent;
        }
        this.mostRecentBounds[3] = this.mostRecentBounds[2];
        this.mostRecentBounds[2] = this.mostRecentBounds[1];
        this.mostRecentBounds[1] = this.mostRecentBounds[0];
        this.mostRecentBounds[0] = bound;
        InferenceVariable variable = bound.left.prototype();
        ThreeSets three = this.boundsPerVariable.get(variable);
        if (three == null) {
            three = new ThreeSets();
            this.boundsPerVariable.put(variable, three);
        }
        if (three.addBound(bound)) {
            int unincorporatedBoundsLength = this.unincorporatedBounds.length;
            if (this.unincorporatedBoundsCount >= unincorporatedBoundsLength) {
                this.unincorporatedBounds = new TypeBound[unincorporatedBoundsLength * 2];
                System.arraycopy(this.unincorporatedBounds, 0, this.unincorporatedBounds, 0, unincorporatedBoundsLength);
            }
            this.unincorporatedBounds[this.unincorporatedBoundsCount++] = bound;
            TypeBinding typeBinding = bound.right;
            if (bound.relation == 4 && typeBinding.isProperType(true)) {
                three.setInstantiation(typeBinding, variable, environment);
            }
            if (bound.right instanceof InferenceVariable) {
                InferenceVariable rightIV = (InferenceVariable)bound.right.prototype();
                three = this.boundsPerVariable.get(rightIV);
                if (three == null) {
                    three = new ThreeSets();
                    this.boundsPerVariable.put(rightIV, three);
                }
                if (three.inverseBounds == null) {
                    three.inverseBounds = new HashMap<InferenceVariable, TypeBound>();
                }
                three.inverseBounds.put(rightIV, bound);
            }
        }
    }

    private boolean addBounds(TypeBound[] newBounds, LookupEnvironment environment) {
        boolean hasProperBound = false;
        int i = 0;
        while (i < newBounds.length) {
            this.addBound(newBounds[i], environment);
            hasProperBound |= newBounds[i].isBound();
            ++i;
        }
        return hasProperBound;
    }

    public void addBounds(BoundSet that, LookupEnvironment environment) {
        if (that == null || environment == null) {
            return;
        }
        this.addBounds(that.flatten(), environment);
    }

    public boolean isInstantiated(InferenceVariable inferenceVariable) {
        ThreeSets three = this.boundsPerVariable.get(inferenceVariable.prototype());
        if (three != null) {
            return three.instantiation != null;
        }
        return false;
    }

    public TypeBinding getInstantiation(InferenceVariable inferenceVariable, LookupEnvironment environment) {
        ThreeSets three = this.boundsPerVariable.get(inferenceVariable.prototype());
        if (three != null) {
            TypeBinding instantiation = three.instantiation;
            if (environment != null && environment.globalOptions.isAnnotationBasedNullAnalysisEnabled && instantiation != null && (instantiation.tagBits & 0x180000000000000L) == 0L) {
                return three.combineAndUseNullHints(instantiation, inferenceVariable.nullHints, environment);
            }
            return instantiation;
        }
        return null;
    }

    public int numUninstantiatedVariables(InferenceVariable[] variables) {
        int num = 0;
        int i = 0;
        while (i < variables.length) {
            if (!this.isInstantiated(variables[i])) {
                ++num;
            }
            ++i;
        }
        return num;
    }

    boolean incorporate(InferenceContext18 context) throws InferenceFailureException {
        if (this.unincorporatedBoundsCount == 0 && this.captures.isEmpty()) {
            return true;
        }
        do {
            TypeBound[] freshBounds = new TypeBound[this.unincorporatedBoundsCount];
            System.arraycopy(this.unincorporatedBounds, 0, freshBounds, 0, this.unincorporatedBoundsCount);
            this.unincorporatedBoundsCount = 0;
            if (!this.incorporate(context, this.incorporatedBounds, freshBounds)) {
                return false;
            }
            if (!this.incorporate(context, freshBounds, freshBounds)) {
                return false;
            }
            int incorporatedLength = this.incorporatedBounds.length;
            int unincorporatedLength = freshBounds.length;
            TypeBound[] aggregate = new TypeBound[incorporatedLength + unincorporatedLength];
            System.arraycopy(this.incorporatedBounds, 0, aggregate, 0, incorporatedLength);
            System.arraycopy(freshBounds, 0, aggregate, incorporatedLength, unincorporatedLength);
            this.incorporatedBounds = aggregate;
        } while (this.unincorporatedBoundsCount > 0);
        return true;
    }

    boolean incorporate(InferenceContext18 context, TypeBound[] first, TypeBound[] next) throws InferenceFailureException {
        boolean analyzeNull = context.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled;
        ConstraintTypeFormula[] mostRecentFormulas = new ConstraintTypeFormula[4];
        int i = 0;
        int iLength = first.length;
        while (i < iLength) {
            TypeBound boundI = first[i];
            int j = 0;
            int jLength = next.length;
            while (j < jLength) {
                TypeBound boundJ = next[j];
                if (boundI != boundJ) {
                    int iteration = 1;
                    do {
                        ConstraintTypeFormula[] typeArgumentConstraints;
                        ConstraintTypeFormula newConstraint = null;
                        boolean deriveTypeArgumentConstraints = false;
                        if (iteration == 2) {
                            TypeBound boundX = boundI;
                            boundI = boundJ;
                            boundJ = boundX;
                        }
                        block0 : switch (boundI.relation) {
                            case 4: {
                                switch (boundJ.relation) {
                                    case 4: {
                                        newConstraint = this.combineSameSame(boundI, boundJ, first, next);
                                        break;
                                    }
                                    case 2: 
                                    case 3: {
                                        newConstraint = this.combineSameSubSuper(boundI, boundJ, first, next);
                                    }
                                }
                                break;
                            }
                            case 2: {
                                switch (boundJ.relation) {
                                    case 4: {
                                        newConstraint = this.combineSameSubSuper(boundJ, boundI, first, next);
                                        break;
                                    }
                                    case 3: {
                                        newConstraint = this.combineSuperAndSub(boundJ, boundI);
                                        break;
                                    }
                                    case 2: {
                                        newConstraint = this.combineEqualSupers(boundI, boundJ);
                                        deriveTypeArgumentConstraints = TypeBinding.equalsEquals(boundI.left, boundJ.left);
                                    }
                                }
                                break;
                            }
                            case 3: {
                                switch (boundJ.relation) {
                                    case 4: {
                                        newConstraint = this.combineSameSubSuper(boundJ, boundI, first, next);
                                        break block0;
                                    }
                                    case 2: {
                                        newConstraint = this.combineSuperAndSub(boundI, boundJ);
                                        break block0;
                                    }
                                    case 3: {
                                        newConstraint = this.combineEqualSupers(boundI, boundJ);
                                    }
                                }
                            }
                        }
                        if (newConstraint != null) {
                            if (newConstraint.left == newConstraint.right) {
                                newConstraint = null;
                            } else if (newConstraint.equalsEquals(mostRecentFormulas[0]) || newConstraint.equalsEquals(mostRecentFormulas[1]) || newConstraint.equalsEquals(mostRecentFormulas[2]) || newConstraint.equalsEquals(mostRecentFormulas[3])) {
                                newConstraint = null;
                            }
                        }
                        if (newConstraint != null) {
                            long nullHints;
                            mostRecentFormulas[3] = mostRecentFormulas[2];
                            mostRecentFormulas[2] = mostRecentFormulas[1];
                            mostRecentFormulas[1] = mostRecentFormulas[0];
                            mostRecentFormulas[0] = newConstraint;
                            if (!this.reduceOneConstraint(context, newConstraint)) {
                                return false;
                            }
                            if (analyzeNull && (nullHints = (newConstraint.left.tagBits | newConstraint.right.tagBits) & 0x180000000000000L) != 0L && (TypeBinding.equalsEquals(boundI.left, boundJ.left) || boundI.relation == 4 && TypeBinding.equalsEquals(boundI.right, boundJ.left) || boundJ.relation == 4 && TypeBinding.equalsEquals(boundI.left, boundJ.right))) {
                                boundI.nullHints |= nullHints;
                                boundJ.nullHints |= nullHints;
                            }
                        }
                        ConstraintTypeFormula[] constraintTypeFormulaArray = typeArgumentConstraints = deriveTypeArgumentConstraints ? this.deriveTypeArgumentConstraints(boundI, boundJ) : null;
                        if (typeArgumentConstraints != null) {
                            int k = 0;
                            int length = typeArgumentConstraints.length;
                            while (k < length) {
                                if (!this.reduceOneConstraint(context, typeArgumentConstraints[k])) {
                                    return false;
                                }
                                ++k;
                            }
                        }
                        if (iteration != 2) continue;
                        TypeBound boundX = boundI;
                        boundI = boundJ;
                        boundJ = boundX;
                    } while (first != next && ++iteration <= 2);
                }
                ++j;
            }
            ++i;
        }
        for (Map.Entry<ParameterizedTypeBinding, ParameterizedTypeBinding> capt : this.captures.entrySet()) {
            ParameterizedTypeBinding gAlpha = capt.getKey();
            ParameterizedTypeBinding gA = capt.getValue();
            ReferenceBinding g = (ReferenceBinding)gA.original();
            final TypeVariableBinding[] parameters = g.typeVariables();
            InferenceVariable[] alphas = new InferenceVariable[gAlpha.arguments.length];
            System.arraycopy(gAlpha.arguments, 0, alphas, 0, alphas.length);
            InferenceSubstitution theta = new InferenceSubstitution(context.environment, alphas, context.currentInvocation){

                @Override
                protected TypeBinding getP(int i) {
                    return parameters[i];
                }
            };
            int i2 = 0;
            int length = parameters.length;
            while (i2 < length) {
                TypeVariableBinding pi = parameters[i2];
                InferenceVariable alpha = (InferenceVariable)gAlpha.arguments[i2];
                this.addBounds(pi.getTypeBounds(alpha, theta), context.environment);
                TypeBinding ai = gA.arguments[i2];
                if (ai instanceof WildcardBinding) {
                    WildcardBinding wildcardBinding = (WildcardBinding)ai;
                    TypeBinding t = wildcardBinding.bound;
                    ThreeSets three = this.boundsPerVariable.get(alpha.prototype());
                    if (three != null) {
                        if (three.sameBounds != null) {
                            for (TypeBound bound : three.sameBounds) {
                                if (bound.right instanceof InferenceVariable) continue;
                                return false;
                            }
                        }
                        if (three.subBounds != null) {
                            TypeBinding bi1 = pi.firstBound;
                            if (bi1 == null) {
                                bi1 = context.object;
                            }
                            for (TypeBound bound : three.subBounds) {
                                TypeBinding bi;
                                if (bound.right instanceof InferenceVariable) continue;
                                TypeBinding r = bound.right;
                                ReferenceBinding[] otherBounds = pi.superInterfaces;
                                if (otherBounds == Binding.NO_SUPERINTERFACES) {
                                    bi = bi1;
                                } else {
                                    int n = otherBounds.length + 1;
                                    ReferenceBinding[] allBounds = new ReferenceBinding[n];
                                    allBounds[0] = (ReferenceBinding)bi1;
                                    System.arraycopy(otherBounds, 0, allBounds, 1, n - 1);
                                    bi = context.environment.createIntersectionType18(allBounds);
                                }
                                this.addTypeBoundsFromWildcardBound(context, theta, wildcardBinding.boundKind, t, r, bi);
                            }
                        }
                        if (three.superBounds != null) {
                            for (TypeBound bound : three.superBounds) {
                                if (bound.right instanceof InferenceVariable) continue;
                                if (wildcardBinding.boundKind == 2) {
                                    this.reduceOneConstraint(context, ConstraintTypeFormula.create(bound.right, t, 2));
                                    continue;
                                }
                                return false;
                            }
                        }
                    }
                } else {
                    this.addBound(new TypeBound(alpha, ai, 4), context.environment);
                }
                ++i2;
            }
        }
        this.captures.clear();
        return true;
    }

    void addTypeBoundsFromWildcardBound(InferenceContext18 context, InferenceSubstitution theta, int boundKind, TypeBinding t, TypeBinding r, TypeBinding bi) throws InferenceFailureException {
        ConstraintTypeFormula formula = null;
        if (boundKind == 1) {
            if (bi.id == 1) {
                formula = ConstraintTypeFormula.create(t, r, 2);
            }
            if (t.id == 1) {
                formula = ConstraintTypeFormula.create(theta.substitute((Substitution)theta, bi), r, 2);
            }
        } else {
            formula = ConstraintTypeFormula.create(theta.substitute((Substitution)theta, bi), r, 2);
        }
        if (formula != null) {
            this.reduceOneConstraint(context, formula);
        }
    }

    private ConstraintTypeFormula combineSameSame(TypeBound boundS, TypeBound boundT, TypeBound[] firstBounds, TypeBound[] nextBounds) {
        if (TypeBinding.equalsEquals(boundS.left, boundT.left)) {
            return ConstraintTypeFormula.create(boundS.right, boundT.right, 4, boundS.isSoft || boundT.isSoft);
        }
        ConstraintTypeFormula newConstraint = this.combineSameSameWithProperType(boundS, boundT, firstBounds, nextBounds);
        if (newConstraint != null) {
            return newConstraint;
        }
        newConstraint = this.combineSameSameWithProperType(boundT, boundS, firstBounds, nextBounds);
        if (newConstraint != null) {
            return newConstraint;
        }
        return null;
    }

    private ConstraintTypeFormula combineSameSameWithProperType(TypeBound boundLeft, TypeBound boundRight, TypeBound[] firstBounds, TypeBound[] nextBounds) {
        TypeBinding u = boundLeft.right;
        if (enableOptimizationForBug543480 && this.isParameterizedDependency(boundRight)) {
            return this.incorporateIntoParameterizedDependencyIfAllArgumentsAreProperTypes(boundRight, firstBounds, nextBounds);
        }
        if (u.isProperType(true)) {
            InferenceVariable alpha = boundLeft.left;
            InferenceVariable left = boundRight.left;
            TypeBinding right = boundRight.right.substituteInferenceVariable(alpha, u);
            return ConstraintTypeFormula.create(left, right, 4, boundLeft.isSoft || boundRight.isSoft);
        }
        return null;
    }

    private ConstraintTypeFormula combineSameSubSuper(TypeBound boundS, TypeBound boundT, TypeBound[] firstBounds, TypeBound[] nextBounds) {
        InferenceVariable alpha = boundS.left;
        TypeBinding s = boundS.right;
        if (TypeBinding.equalsEquals(alpha, boundT.left)) {
            TypeBinding t = boundT.right;
            return ConstraintTypeFormula.create(s, t, boundT.relation, boundT.isSoft || boundS.isSoft);
        }
        if (TypeBinding.equalsEquals(alpha, boundT.right)) {
            InferenceVariable t = boundT.left;
            return ConstraintTypeFormula.create(t, s, boundT.relation, boundT.isSoft || boundS.isSoft);
        }
        if (boundS.right instanceof InferenceVariable) {
            alpha = (InferenceVariable)boundS.right;
            s = boundS.left;
            if (TypeBinding.equalsEquals(alpha, boundT.left)) {
                TypeBinding t = boundT.right;
                return ConstraintTypeFormula.create(s, t, boundT.relation, boundT.isSoft || boundS.isSoft);
            }
            if (TypeBinding.equalsEquals(alpha, boundT.right)) {
                InferenceVariable t = boundT.left;
                return ConstraintTypeFormula.create(t, s, boundT.relation, boundT.isSoft || boundS.isSoft);
            }
        }
        return this.combineSameSubSuperWithProperType(boundS, boundT, alpha, firstBounds, nextBounds);
    }

    private ConstraintTypeFormula combineSameSubSuperWithProperType(TypeBound boundLeft, TypeBound boundRight, InferenceVariable alpha, TypeBound[] firstBounds, TypeBound[] nextBounds) {
        TypeBinding u = boundLeft.right;
        if (enableOptimizationForBug543480 && this.isParameterizedDependency(boundRight)) {
            return this.incorporateIntoParameterizedDependencyIfAllArgumentsAreProperTypes(boundRight, firstBounds, nextBounds);
        }
        if (u.isProperType(true)) {
            boolean substitute = TypeBinding.equalsEquals(alpha, boundRight.left);
            TypeBinding left = substitute ? u : boundRight.left;
            TypeBinding right = boundRight.right.substituteInferenceVariable(alpha, u);
            if (substitute |= TypeBinding.notEquals(right, boundRight.right)) {
                return ConstraintTypeFormula.create(left, right, boundRight.relation, boundRight.isSoft || boundLeft.isSoft);
            }
        }
        return null;
    }

    private ConstraintTypeFormula combineSuperAndSub(TypeBound boundS, TypeBound boundT) {
        InferenceVariable alpha = boundS.left;
        if (TypeBinding.equalsEquals(alpha, boundT.left)) {
            return ConstraintTypeFormula.create(boundS.right, boundT.right, 2, boundT.isSoft || boundS.isSoft);
        }
        if (boundS.right instanceof InferenceVariable && TypeBinding.equalsEquals(alpha = (InferenceVariable)boundS.right, boundT.right)) {
            return ConstraintTypeFormula.create(boundS.left, boundT.left, 3, boundT.isSoft || boundS.isSoft);
        }
        return null;
    }

    private ConstraintTypeFormula combineEqualSupers(TypeBound boundS, TypeBound boundT) {
        if (TypeBinding.equalsEquals(boundS.left, boundT.right)) {
            return ConstraintTypeFormula.create(boundT.left, boundS.right, boundS.relation, boundT.isSoft || boundS.isSoft);
        }
        if (TypeBinding.equalsEquals(boundS.right, boundT.left)) {
            return ConstraintTypeFormula.create(boundS.left, boundT.right, boundS.relation, boundT.isSoft || boundS.isSoft);
        }
        return null;
    }

    private boolean isParameterizedDependency(TypeBound typeBound) {
        return typeBound.right.kind() == 260 && !typeBound.right.isProperType(true) && typeBound.right.isParameterizedTypeWithActualArguments();
    }

    private ConstraintTypeFormula incorporateIntoParameterizedDependencyIfAllArgumentsAreProperTypes(TypeBound typeBound, TypeBound[] firstBounds, TypeBound[] nextBounds) {
        Collection<TypeBound> properTypesForAllInferenceVariables = this.getProperTypesForAllInferenceVariablesOrNull((ParameterizedTypeBinding)typeBound.right, firstBounds, nextBounds);
        if (properTypesForAllInferenceVariables != null) {
            return this.combineWithProperTypes(properTypesForAllInferenceVariables, typeBound);
        }
        return null;
    }

    private Collection<TypeBound> getProperTypesForAllInferenceVariablesOrNull(ParameterizedTypeBinding parameterizedType, TypeBound[] firstBounds, TypeBound[] nextBounds) {
        Map<InferenceVariable, TypeBound> properTypesByInferenceVariable = this.properTypesByInferenceVariable(firstBounds, nextBounds);
        if (properTypesByInferenceVariable.size() == 0) {
            return null;
        }
        Set<InferenceVariable> inferenceVariables = this.getInferenceVariables(parameterizedType);
        if (properTypesByInferenceVariable.keySet().containsAll(inferenceVariables)) {
            return properTypesByInferenceVariable.values();
        }
        return null;
    }

    private Map<InferenceVariable, TypeBound> properTypesByInferenceVariable(TypeBound[] firstBounds, TypeBound[] nextBounds) {
        return this.getBoundsStream(firstBounds, nextBounds).filter(bound -> bound.relation == 4).filter(bound -> bound.right.isProperType(true)).collect(Collectors.toMap(bound -> bound.left, Function.identity(), (boundFromNextBounds, boundFromFirstBounds) -> boundFromNextBounds));
    }

    private Stream<TypeBound> getBoundsStream(TypeBound[] firstBounds, TypeBound[] nextBounds) {
        if (firstBounds == nextBounds) {
            return Arrays.stream(firstBounds);
        }
        return Stream.concat(Arrays.stream(nextBounds), Arrays.stream(firstBounds));
    }

    private Set<InferenceVariable> getInferenceVariables(ParameterizedTypeBinding parameterizedType) {
        LinkedHashSet<InferenceVariable> inferenceVariables = new LinkedHashSet<InferenceVariable>();
        TypeBinding[] typeBindingArray = parameterizedType.arguments;
        int n = parameterizedType.arguments.length;
        int n2 = 0;
        while (n2 < n) {
            TypeBinding argument = typeBindingArray[n2];
            argument.collectInferenceVariables(inferenceVariables);
            ++n2;
        }
        return inferenceVariables;
    }

    private ConstraintTypeFormula combineWithProperTypes(Collection<TypeBound> properTypesForAllInferenceVariables, TypeBound boundRight) {
        if (properTypesForAllInferenceVariables.size() == 0) {
            return null;
        }
        boolean isAnyLeftSoft = false;
        InferenceVariable left = boundRight.left;
        TypeBinding right = boundRight.right;
        Iterator<TypeBound> iterator = properTypesForAllInferenceVariables.iterator();
        while (iterator.hasNext()) {
            TypeBound properTypeForInferenceVariable;
            TypeBound boundLeft = properTypeForInferenceVariable = iterator.next();
            InferenceVariable alpha = boundLeft.left;
            TypeBinding u = boundLeft.right;
            isAnyLeftSoft |= boundLeft.isSoft;
            right = right.substituteInferenceVariable(alpha, u);
        }
        return ConstraintTypeFormula.create(left, right, boundRight.relation, isAnyLeftSoft || boundRight.isSoft);
    }

    private ConstraintTypeFormula[] deriveTypeArgumentConstraints(TypeBound boundS, TypeBound boundT) {
        TypeBinding[] supers = this.superTypesWithCommonGenericType(boundS.right, boundT.right);
        if (supers != null) {
            return this.typeArgumentEqualityConstraints(supers[0], supers[1], boundS.isSoft || boundT.isSoft);
        }
        return null;
    }

    private ConstraintTypeFormula[] typeArgumentEqualityConstraints(TypeBinding s, TypeBinding t, boolean isSoft) {
        if (s == null || s.kind() != 260 || t == null || t.kind() != 260) {
            return null;
        }
        if (TypeBinding.equalsEquals(s, t)) {
            return null;
        }
        TypeBinding[] sis = s.typeArguments();
        TypeBinding[] tis = t.typeArguments();
        if (sis == null || tis == null || sis.length != tis.length) {
            return null;
        }
        ArrayList<ConstraintTypeFormula> result = new ArrayList<ConstraintTypeFormula>();
        int i = 0;
        while (i < sis.length) {
            TypeBinding si = sis[i];
            TypeBinding ti = tis[i];
            if (!(si.isWildcard() || ti.isWildcard() || TypeBinding.equalsEquals(si, ti))) {
                result.add(ConstraintTypeFormula.create(si, ti, 4, isSoft));
            }
            ++i;
        }
        if (result.size() > 0) {
            return result.toArray(new ConstraintTypeFormula[result.size()]);
        }
        return null;
    }

    public boolean reduceOneConstraint(InferenceContext18 context, ConstraintFormula currentConstraint) throws InferenceFailureException {
        Object result = currentConstraint.reduce(context);
        if (result == ReductionResult.FALSE) {
            return false;
        }
        if (result == ReductionResult.TRUE) {
            return true;
        }
        if (result == currentConstraint) {
            throw new IllegalStateException("Failed to reduce constraint formula");
        }
        if (result != null) {
            if (result instanceof ConstraintFormula) {
                if (!this.reduceOneConstraint(context, (ConstraintFormula)result)) {
                    return false;
                }
            } else if (result instanceof ConstraintFormula[]) {
                ConstraintFormula[] resultArray = (ConstraintFormula[])result;
                int i = 0;
                while (i < resultArray.length) {
                    if (!this.reduceOneConstraint(context, resultArray[i])) {
                        return false;
                    }
                    ++i;
                }
            } else {
                this.addBound((TypeBound)result, context.environment);
            }
        }
        return true;
    }

    public boolean dependsOnResolutionOf(InferenceVariable alpha, InferenceVariable beta) {
        ThreeSets sets;
        if (TypeBinding.equalsEquals(alpha = alpha.prototype(), beta = beta.prototype())) {
            return true;
        }
        Iterator<Map.Entry<ParameterizedTypeBinding, ParameterizedTypeBinding>> captureIter = this.captures.entrySet().iterator();
        boolean betaIsInCaptureLhs = false;
        while (captureIter.hasNext()) {
            Map.Entry<ParameterizedTypeBinding, ParameterizedTypeBinding> entry = captureIter.next();
            ParameterizedTypeBinding g = entry.getKey();
            int i = 0;
            while (i < g.arguments.length) {
                if (TypeBinding.equalsEquals(g.arguments[i], alpha)) {
                    ParameterizedTypeBinding captured = entry.getValue();
                    if (captured.mentionsAny(new TypeBinding[]{beta}, -1)) {
                        return true;
                    }
                    if (g.mentionsAny(new TypeBinding[]{beta}, i)) {
                        return true;
                    }
                } else if (TypeBinding.equalsEquals(g.arguments[i], beta)) {
                    betaIsInCaptureLhs = true;
                }
                ++i;
            }
        }
        return betaIsInCaptureLhs ? (sets = this.boundsPerVariable.get(beta)) != null && sets.hasDependency(alpha) : (sets = this.boundsPerVariable.get(alpha)) != null && sets.hasDependency(beta);
    }

    List<Set<InferenceVariable>> computeConnectedComponents(InferenceVariable[] inferenceVariables) {
        HashMap<InferenceVariable, Set<InferenceVariable>> allEdges = new HashMap<InferenceVariable, Set<InferenceVariable>>();
        int i = 0;
        while (i < inferenceVariables.length) {
            InferenceVariable iv1 = inferenceVariables[i];
            HashSet<InferenceVariable> targetSet = new HashSet<InferenceVariable>();
            allEdges.put(iv1, targetSet);
            int j = 0;
            while (j < i) {
                InferenceVariable iv2 = inferenceVariables[j];
                if (this.dependsOnResolutionOf(iv1, iv2) || this.dependsOnResolutionOf(iv2, iv1)) {
                    targetSet.add(iv2);
                    ((Set)allEdges.get(iv2)).add(iv1);
                }
                ++j;
            }
            ++i;
        }
        HashSet<InferenceVariable> visited = new HashSet<InferenceVariable>();
        ArrayList<Set<InferenceVariable>> allComponents = new ArrayList<Set<InferenceVariable>>();
        InferenceVariable[] inferenceVariableArray = inferenceVariables;
        int n = inferenceVariables.length;
        int n2 = 0;
        while (n2 < n) {
            InferenceVariable inferenceVariable = inferenceVariableArray[n2];
            HashSet<InferenceVariable> component = new HashSet<InferenceVariable>();
            this.addConnected(component, inferenceVariable, allEdges, visited);
            if (!component.isEmpty()) {
                allComponents.add(component);
            }
            ++n2;
        }
        return allComponents;
    }

    private void addConnected(Set<InferenceVariable> component, InferenceVariable seed, Map<InferenceVariable, Set<InferenceVariable>> allEdges, Set<InferenceVariable> visited) {
        if (visited.add(seed)) {
            component.add(seed);
            for (InferenceVariable next : allEdges.get(seed)) {
                this.addConnected(component, next, allEdges, visited);
            }
        }
    }

    public boolean hasCaptureBound(Set<InferenceVariable> variableSet) {
        for (ParameterizedTypeBinding g : this.captures.keySet()) {
            int i = 0;
            while (i < g.arguments.length) {
                if (variableSet.contains(g.arguments[i])) {
                    return true;
                }
                ++i;
            }
        }
        return false;
    }

    public boolean hasOnlyTrivialExceptionBounds(InferenceVariable variable, TypeBinding[] upperBounds) {
        if (upperBounds != null) {
            int i = 0;
            while (i < upperBounds.length) {
                switch (upperBounds[i].id) {
                    case 1: 
                    case 21: 
                    case 25: {
                        break;
                    }
                    default: {
                        return false;
                    }
                }
                ++i;
            }
        }
        return true;
    }

    public TypeBinding[] upperBounds(InferenceVariable variable, boolean onlyProper) {
        ThreeSets three = this.boundsPerVariable.get(variable.prototype());
        if (three == null || three.subBounds == null) {
            return Binding.NO_TYPES;
        }
        return three.upperBounds(onlyProper, variable);
    }

    TypeBinding[] lowerBounds(InferenceVariable variable, boolean onlyProper) {
        ThreeSets three = this.boundsPerVariable.get(variable.prototype());
        if (three == null || three.superBounds == null) {
            return Binding.NO_TYPES;
        }
        return three.lowerBounds(onlyProper, variable);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("Type Bounds:\n");
        TypeBound[] flattened = this.flatten();
        int i = 0;
        while (i < flattened.length) {
            buf.append('\t').append(flattened[i].toString()).append('\n');
            ++i;
        }
        buf.append("Capture Bounds:\n");
        for (Map.Entry<ParameterizedTypeBinding, ParameterizedTypeBinding> capt : this.captures.entrySet()) {
            String lhs = String.valueOf(((TypeBinding)capt.getKey()).shortReadableName());
            String rhs = String.valueOf(((TypeBinding)capt.getValue()).shortReadableName());
            buf.append('\t').append(lhs).append(" = capt(").append(rhs).append(")\n");
        }
        return buf.toString();
    }

    public TypeBinding findWrapperTypeBound(InferenceVariable variable) {
        ThreeSets three = this.boundsPerVariable.get(variable.prototype());
        if (three == null) {
            return null;
        }
        return three.findSingleWrapperType();
    }

    public boolean condition18_5_2_bullet_3_3_1(InferenceVariable alpha, TypeBinding targetType) {
        if (targetType.isBaseType()) {
            return false;
        }
        if (InferenceContext18.parameterizedWithWildcard(targetType) != null) {
            return false;
        }
        ThreeSets ts = this.boundsPerVariable.get(alpha.prototype());
        if (ts == null) {
            return false;
        }
        if (ts.sameBounds != null) {
            for (TypeBound bound : ts.sameBounds) {
                if (InferenceContext18.parameterizedWithWildcard(bound.right) == null) continue;
                return true;
            }
        }
        if (ts.superBounds != null) {
            for (TypeBound bound : ts.superBounds) {
                if (InferenceContext18.parameterizedWithWildcard(bound.right) == null) continue;
                return true;
            }
        }
        if (ts.superBounds != null) {
            ArrayList<TypeBound> superBounds = new ArrayList<TypeBound>(ts.superBounds);
            int len = superBounds.size();
            int i = 0;
            while (i < len) {
                TypeBinding s1 = superBounds.get((int)i).right;
                int j = i + 1;
                while (j < len) {
                    TypeBinding s2 = superBounds.get((int)j).right;
                    TypeBinding[] supers = this.superTypesWithCommonGenericType(s1, s2);
                    if (supers != null && supers[0].isProperType(true) && supers[1].isProperType(true) && !TypeBinding.equalsEquals(supers[0], supers[1])) {
                        return true;
                    }
                    ++j;
                }
                ++i;
            }
        }
        return false;
    }

    public boolean condition18_5_2_bullet_3_3_2(InferenceVariable alpha, TypeBinding targetType, InferenceContext18 ctx18) {
        if (!targetType.isParameterizedType()) {
            return false;
        }
        TypeBinding g = targetType.original();
        ThreeSets ts = this.boundsPerVariable.get(alpha.prototype());
        if (ts == null) {
            return false;
        }
        if (ts.sameBounds != null) {
            for (TypeBound b : ts.sameBounds) {
                if (!this.superOnlyRaw(g, b.right, ctx18.environment)) continue;
                return true;
            }
        }
        if (ts.superBounds != null) {
            for (TypeBound b : ts.superBounds) {
                if (!this.superOnlyRaw(g, b.right, ctx18.environment)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean superOnlyRaw(TypeBinding g, TypeBinding s, LookupEnvironment env) {
        if (s instanceof InferenceVariable) {
            return false;
        }
        TypeBinding superType = s.findSuperTypeOriginatingFrom(g);
        if (superType != null && !superType.isParameterizedType()) {
            return s.isCompatibleWith(env.convertToRawType(g, false));
        }
        return false;
    }

    protected TypeBinding[] superTypesWithCommonGenericType(TypeBinding s, TypeBinding t) {
        if (s == null || s.id == 1 || t == null || t.id == 1) {
            return null;
        }
        if (TypeBinding.equalsEquals(s.original(), t.original())) {
            return new TypeBinding[]{s, t};
        }
        TypeBinding tSuper = t.findSuperTypeOriginatingFrom(s);
        if (tSuper != null) {
            return new TypeBinding[]{s, tSuper};
        }
        TypeBinding[] result = this.superTypesWithCommonGenericType(s.superclass(), t);
        if (result != null) {
            return result;
        }
        ReferenceBinding[] superInterfaces = s.superInterfaces();
        if (superInterfaces != null) {
            int i = 0;
            while (i < superInterfaces.length) {
                result = this.superTypesWithCommonGenericType(superInterfaces[i], t);
                if (result != null) {
                    return result;
                }
                ++i;
            }
        }
        return null;
    }

    public TypeBinding getEquivalentOuterVariable(InferenceVariable variable, InferenceVariable[] outerVariables) {
        ThreeSets three = this.boundsPerVariable.get(variable);
        if (three != null) {
            for (TypeBound bound : three.sameBounds) {
                InferenceVariable[] inferenceVariableArray = outerVariables;
                int n = outerVariables.length;
                int n2 = 0;
                while (n2 < n) {
                    InferenceVariable iv = inferenceVariableArray[n2];
                    if (TypeBinding.equalsEquals(bound.right, iv)) {
                        return iv;
                    }
                    ++n2;
                }
            }
        }
        InferenceVariable[] inferenceVariableArray = outerVariables;
        int n = outerVariables.length;
        int n3 = 0;
        while (n3 < n) {
            InferenceVariable iv = inferenceVariableArray[n3];
            three = this.boundsPerVariable.get(iv);
            if (three != null && three.sameBounds != null) {
                for (TypeBound bound : three.sameBounds) {
                    if (!TypeBinding.equalsEquals(bound.right, variable)) continue;
                    return iv;
                }
            }
            ++n3;
        }
        return null;
    }

    private static class ThreeSets {
        Set<TypeBound> superBounds;
        Set<TypeBound> sameBounds;
        Set<TypeBound> subBounds;
        TypeBinding instantiation;
        Map<InferenceVariable, TypeBound> inverseBounds;
        Set<InferenceVariable> dependencies;

        public boolean addBound(TypeBound bound) {
            boolean result = this.addBound1(bound);
            if (result) {
                HashSet<InferenceVariable> set = this.dependencies == null ? new HashSet() : this.dependencies;
                bound.right.collectInferenceVariables(set);
                if (this.dependencies == null && set.size() > 0) {
                    this.dependencies = set;
                }
            }
            return result;
        }

        private boolean addBound1(TypeBound bound) {
            switch (bound.relation) {
                case 3: {
                    if (this.superBounds == null) {
                        this.superBounds = new HashSet<TypeBound>();
                    }
                    return this.superBounds.add(bound);
                }
                case 4: {
                    if (this.sameBounds == null) {
                        this.sameBounds = new HashSet<TypeBound>();
                    }
                    return this.sameBounds.add(bound);
                }
                case 2: {
                    if (this.subBounds == null) {
                        this.subBounds = new HashSet<TypeBound>();
                    }
                    return this.subBounds.add(bound);
                }
            }
            throw new IllegalArgumentException("Unexpected bound relation in : " + bound);
        }

        public TypeBinding[] lowerBounds(boolean onlyProper, InferenceVariable variable) {
            TypeBinding[] boundTypes = new TypeBinding[this.superBounds.size()];
            Iterator<TypeBound> it = this.superBounds.iterator();
            long nullHints = variable.nullHints;
            int i = 0;
            while (it.hasNext()) {
                TypeBound current = it.next();
                TypeBinding boundType = current.right;
                if (onlyProper && !boundType.isProperType(true)) continue;
                boundTypes[i++] = boundType;
                nullHints |= current.nullHints;
            }
            if (i == 0) {
                return Binding.NO_TYPES;
            }
            if (i < boundTypes.length) {
                TypeBinding[] typeBindingArray = boundTypes;
                boundTypes = new TypeBinding[i];
                System.arraycopy(typeBindingArray, 0, boundTypes, 0, i);
            }
            this.useNullHints(nullHints, boundTypes, variable.environment);
            InferenceContext18.sortTypes(boundTypes);
            return boundTypes;
        }

        public TypeBinding[] upperBounds(boolean onlyProper, InferenceVariable variable) {
            TypeBinding[] rights = new TypeBinding[this.subBounds.size()];
            TypeBinding simpleUpper = null;
            Iterator<TypeBound> it = this.subBounds.iterator();
            long nullHints = variable.nullHints;
            int i = 0;
            while (it.hasNext()) {
                TypeBinding right = it.next().right;
                if (onlyProper && !right.isProperType(true)) continue;
                if (right instanceof ReferenceBinding) {
                    rights[i++] = right;
                    nullHints |= right.tagBits & 0x180000000000000L;
                    continue;
                }
                if (simpleUpper != null) {
                    return Binding.NO_TYPES;
                }
                simpleUpper = right;
            }
            if (i == 0) {
                TypeBinding[] typeBindingArray;
                if (simpleUpper != null) {
                    TypeBinding[] typeBindingArray2 = new TypeBinding[1];
                    typeBindingArray = typeBindingArray2;
                    typeBindingArray2[0] = simpleUpper;
                } else {
                    typeBindingArray = Binding.NO_TYPES;
                }
                return typeBindingArray;
            }
            if (i == 1 && simpleUpper != null) {
                return new TypeBinding[]{simpleUpper};
            }
            if (i < rights.length) {
                TypeBinding[] typeBindingArray = rights;
                rights = new TypeBinding[i];
                System.arraycopy(typeBindingArray, 0, rights, 0, i);
            }
            this.useNullHints(nullHints, rights, variable.environment);
            InferenceContext18.sortTypes(rights);
            return rights;
        }

        public boolean hasDependency(InferenceVariable beta) {
            if (this.dependencies != null && this.dependencies.contains(beta)) {
                return true;
            }
            return this.inverseBounds != null && this.inverseBounds.containsKey(beta);
        }

        public int size() {
            int size = 0;
            if (this.superBounds != null) {
                size += this.superBounds.size();
            }
            if (this.sameBounds != null) {
                size += this.sameBounds.size();
            }
            if (this.subBounds != null) {
                size += this.subBounds.size();
            }
            return size;
        }

        public int flattenInto(TypeBound[] collected, int idx) {
            int len;
            if (this.superBounds != null) {
                len = this.superBounds.size();
                System.arraycopy(this.superBounds.toArray(), 0, collected, idx, len);
                idx += len;
            }
            if (this.sameBounds != null) {
                len = this.sameBounds.size();
                System.arraycopy(this.sameBounds.toArray(), 0, collected, idx, len);
                idx += len;
            }
            if (this.subBounds != null) {
                len = this.subBounds.size();
                System.arraycopy(this.subBounds.toArray(), 0, collected, idx, len);
                idx += len;
            }
            return idx;
        }

        public ThreeSets copy() {
            ThreeSets copy = new ThreeSets();
            if (this.superBounds != null) {
                copy.superBounds = new HashSet<TypeBound>(this.superBounds);
            }
            if (this.sameBounds != null) {
                copy.sameBounds = new HashSet<TypeBound>(this.sameBounds);
            }
            if (this.subBounds != null) {
                copy.subBounds = new HashSet<TypeBound>(this.subBounds);
            }
            copy.instantiation = this.instantiation;
            if (this.dependencies != null) {
                copy.dependencies = new HashSet<InferenceVariable>(this.dependencies);
            }
            return copy;
        }

        public TypeBinding findSingleWrapperType() {
            TypeBinding boundType;
            Iterator<TypeBound> it;
            if (this.instantiation != null && this.instantiation.isProperType(true)) {
                switch (this.instantiation.id) {
                    case 26: 
                    case 27: 
                    case 28: 
                    case 29: 
                    case 30: 
                    case 31: 
                    case 32: 
                    case 33: {
                        return this.instantiation;
                    }
                }
            }
            if (this.subBounds != null) {
                it = this.subBounds.iterator();
                while (it.hasNext()) {
                    boundType = it.next().right;
                    if (!boundType.isProperType(true)) continue;
                    switch (boundType.id) {
                        case 26: 
                        case 27: 
                        case 28: 
                        case 29: 
                        case 30: 
                        case 31: 
                        case 32: 
                        case 33: {
                            return boundType;
                        }
                    }
                }
            }
            if (this.superBounds != null) {
                it = this.superBounds.iterator();
                while (it.hasNext()) {
                    boundType = it.next().right;
                    if (!boundType.isProperType(true)) continue;
                    switch (boundType.id) {
                        case 26: 
                        case 27: 
                        case 28: 
                        case 29: 
                        case 30: 
                        case 31: 
                        case 32: 
                        case 33: {
                            return boundType;
                        }
                    }
                }
            }
            return null;
        }

        private void useNullHints(long nullHints, TypeBinding[] boundTypes, LookupEnvironment environment) {
            block3: {
                block2: {
                    if (nullHints != 0x180000000000000L) break block2;
                    int i = 0;
                    while (i < boundTypes.length) {
                        boundTypes[i] = boundTypes[i].withoutToplevelNullAnnotation();
                        ++i;
                    }
                    break block3;
                }
                AnnotationBinding[] annot = environment.nullAnnotationsFromTagBits(nullHints);
                if (annot == null) break block3;
                int i = 0;
                while (i < boundTypes.length) {
                    boundTypes[i] = environment.createAnnotatedType(boundTypes[i], annot);
                    ++i;
                }
            }
        }

        TypeBinding combineAndUseNullHints(TypeBinding type, long nullHints, LookupEnvironment environment) {
            Iterator<TypeBound> it;
            if (this.sameBounds != null) {
                it = this.sameBounds.iterator();
                while (it.hasNext()) {
                    nullHints |= it.next().nullHints;
                }
            }
            if (this.superBounds != null) {
                it = this.superBounds.iterator();
                while (it.hasNext()) {
                    nullHints |= it.next().nullHints;
                }
            }
            if (this.subBounds != null) {
                it = this.subBounds.iterator();
                while (it.hasNext()) {
                    nullHints |= it.next().nullHints;
                }
            }
            if (nullHints == 0x180000000000000L) {
                return type.withoutToplevelNullAnnotation();
            }
            AnnotationBinding[] annot = environment.nullAnnotationsFromTagBits(nullHints);
            if (annot != null) {
                return environment.createAnnotatedType(type, annot);
            }
            return type;
        }

        public void setInstantiation(TypeBinding type, InferenceVariable variable, LookupEnvironment environment) {
            if (environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                long variableBits = variable.tagBits & 0x180000000000000L;
                long allBits = type.tagBits | variableBits;
                if (this.instantiation != null) {
                    allBits |= this.instantiation.tagBits;
                }
                if ((allBits &= 0x180000000000000L) == 0x180000000000000L) {
                    allBits = variableBits;
                }
                if (allBits != (type.tagBits & 0x180000000000000L)) {
                    AnnotationBinding[] annot = environment.nullAnnotationsFromTagBits(allBits);
                    if (annot != null) {
                        type = environment.createAnnotatedType(type.withoutToplevelNullAnnotation(), annot);
                    } else if (type.hasNullTypeAnnotations()) {
                        type = type.withoutToplevelNullAnnotation();
                    }
                }
            }
            this.instantiation = type;
        }
    }
}

