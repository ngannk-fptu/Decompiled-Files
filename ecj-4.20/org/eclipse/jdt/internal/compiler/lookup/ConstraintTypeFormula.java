/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BoundSet;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.ConstraintFormula;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBound;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

class ConstraintTypeFormula
extends ConstraintFormula {
    TypeBinding left;
    boolean isSoft;

    public static ConstraintTypeFormula create(TypeBinding exprType, TypeBinding right, int relation) {
        if (exprType == null || right == null) {
            return FALSE;
        }
        return new ConstraintTypeFormula(exprType, right, relation, false);
    }

    public static ConstraintTypeFormula create(TypeBinding exprType, TypeBinding right, int relation, boolean isSoft) {
        if (exprType == null || right == null) {
            return FALSE;
        }
        return new ConstraintTypeFormula(exprType, right, relation, isSoft);
    }

    private ConstraintTypeFormula(TypeBinding exprType, TypeBinding right, int relation, boolean isSoft) {
        this.left = exprType;
        this.right = right;
        this.relation = relation;
        this.isSoft = isSoft;
    }

    ConstraintTypeFormula() {
    }

    @Override
    public Object reduce(InferenceContext18 inferenceContext) {
        switch (this.relation) {
            case 1: {
                if (this.left.isProperType(true) && this.right.isProperType(true)) {
                    return this.left.isCompatibleWith(this.right, inferenceContext.scope) || this.left.isBoxingCompatibleWith(this.right, inferenceContext.scope) ? TRUE : FALSE;
                }
                if (this.left.isPrimitiveType()) {
                    TypeBinding sPrime = inferenceContext.environment.computeBoxingType(this.left);
                    return ConstraintTypeFormula.create(sPrime, this.right, 1, this.isSoft);
                }
                if (this.right.isPrimitiveType()) {
                    TypeBinding tPrime = inferenceContext.environment.computeBoxingType(this.right);
                    return ConstraintTypeFormula.create(this.left, tPrime, 4, this.isSoft);
                }
                switch (this.right.kind()) {
                    case 68: {
                        if (this.right.leafComponentType().kind() != 260) break;
                    }
                    case 260: {
                        TypeBinding gs = this.left.findSuperTypeOriginatingFrom(this.right);
                        if (gs == null || !gs.leafComponentType().isRawType()) break;
                        inferenceContext.recordUncheckedConversion(this);
                        return TRUE;
                    }
                }
                return ConstraintTypeFormula.create(this.left, this.right, 2, this.isSoft);
            }
            case 2: {
                return this.reduceSubType(inferenceContext.scope, this.left, this.right);
            }
            case 3: {
                return this.reduceSubType(inferenceContext.scope, this.right, this.left);
            }
            case 4: {
                if (inferenceContext.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled && !this.checkIVFreeTVmatch(this.left, this.right)) {
                    this.checkIVFreeTVmatch(this.right, this.left);
                }
                return this.reduceTypeEquality(inferenceContext.object, inferenceContext);
            }
            case 5: {
                if (this.right.kind() != 516) {
                    if (this.left.kind() != 516) {
                        return ConstraintTypeFormula.create(this.left, this.right, 4, this.isSoft);
                    }
                    if (this.right instanceof InferenceVariable) {
                        return new TypeBound((InferenceVariable)this.right, this.left, 4, this.isSoft);
                    }
                    return FALSE;
                }
                WildcardBinding t = (WildcardBinding)this.right;
                if (t.boundKind == 0) {
                    return TRUE;
                }
                if (t.boundKind == 1) {
                    if (this.left.kind() != 516) {
                        return ConstraintTypeFormula.create(this.left, t.bound, 2, this.isSoft);
                    }
                    WildcardBinding s = (WildcardBinding)this.left;
                    switch (s.boundKind) {
                        case 0: {
                            return ConstraintTypeFormula.create(inferenceContext.object, t.bound, 2, this.isSoft);
                        }
                        case 1: {
                            return ConstraintTypeFormula.create(s.bound, t.bound, 2, this.isSoft);
                        }
                        case 2: {
                            return ConstraintTypeFormula.create(inferenceContext.object, t.bound, 4, this.isSoft);
                        }
                    }
                    throw new IllegalArgumentException("Unexpected boundKind " + s.boundKind);
                }
                if (this.left.kind() != 516) {
                    return ConstraintTypeFormula.create(t.bound, this.left, 2, this.isSoft);
                }
                WildcardBinding s = (WildcardBinding)this.left;
                if (s.boundKind == 2) {
                    return ConstraintTypeFormula.create(t.bound, s.bound, 2, this.isSoft);
                }
                return FALSE;
            }
        }
        throw new IllegalStateException("Unexpected relation kind " + this.relation);
    }

    boolean checkIVFreeTVmatch(TypeBinding one, TypeBinding two) {
        if (one instanceof InferenceVariable && two.isTypeVariable() && (two.tagBits & 0x180000000000000L) == 0L) {
            ((InferenceVariable)one).nullHints = 0x180000000000000L;
            return true;
        }
        return false;
    }

    private Object reduceTypeEquality(TypeBinding object, InferenceContext18 inferenceContext) {
        if (this.left.kind() == 516) {
            if (this.right.kind() == 516) {
                WildcardBinding leftWC = (WildcardBinding)this.left;
                WildcardBinding rightWC = (WildcardBinding)this.right;
                if (leftWC.boundKind == 0 && rightWC.boundKind == 0) {
                    return TRUE;
                }
                if (leftWC.boundKind == 0 && rightWC.boundKind == 1) {
                    return ConstraintTypeFormula.create(object, rightWC.bound, 4, this.isSoft);
                }
                if (leftWC.boundKind == 1 && rightWC.boundKind == 0) {
                    return ConstraintTypeFormula.create(leftWC.bound, object, 4, this.isSoft);
                }
                if (leftWC.boundKind == 1 && rightWC.boundKind == 1 || leftWC.boundKind == 2 && rightWC.boundKind == 2) {
                    return ConstraintTypeFormula.create(leftWC.bound, rightWC.bound, 4, this.isSoft);
                }
            }
        } else if (this.right.kind() != 516) {
            if (this.left.isProperType(true) && this.right.isProperType(true)) {
                if (TypeBinding.equalsEquals(this.left, this.right)) {
                    return TRUE;
                }
                return FALSE;
            }
            if (this.left.id == 12 || this.right.id == 12) {
                return FALSE;
            }
            if (this.left instanceof InferenceVariable && !this.right.isPrimitiveType()) {
                return new TypeBound((InferenceVariable)this.left, this.right, 4, this.isSoft);
            }
            if (this.right instanceof InferenceVariable && !this.left.isPrimitiveType()) {
                return new TypeBound((InferenceVariable)this.right, this.left, 4, this.isSoft);
            }
            if ((this.left.isClass() || this.left.isInterface()) && (this.right.isClass() || this.right.isInterface()) && TypeBinding.equalsEquals(this.left.erasure(), this.right.erasure())) {
                TypeBinding[] leftParams = this.left.typeArguments();
                TypeBinding[] rightParams = this.right.typeArguments();
                if (leftParams == null || rightParams == null) {
                    return leftParams == rightParams ? TRUE : FALSE;
                }
                if (leftParams.length != rightParams.length) {
                    return FALSE;
                }
                int len = leftParams.length;
                ConstraintFormula[] constraints = new ConstraintFormula[len];
                int i = 0;
                while (i < len) {
                    constraints[i] = ConstraintTypeFormula.create(leftParams[i], rightParams[i], 4, this.isSoft);
                    ++i;
                }
                return constraints;
            }
            if (this.left.isArrayType() && this.right.isArrayType()) {
                if (this.left.dimensions() == this.right.dimensions()) {
                    return ConstraintTypeFormula.create(this.left.leafComponentType(), this.right.leafComponentType(), 4, this.isSoft);
                }
                if (this.left.dimensions() > 0 && this.right.dimensions() > 0) {
                    TypeBinding leftPrime = this.peelOneDimension(this.left, inferenceContext.environment);
                    TypeBinding rightPrime = this.peelOneDimension(this.right, inferenceContext.environment);
                    return ConstraintTypeFormula.create(leftPrime, rightPrime, 4, this.isSoft);
                }
            }
        }
        return FALSE;
    }

    private TypeBinding peelOneDimension(TypeBinding arrayType, LookupEnvironment env) {
        if (arrayType.dimensions() == 1) {
            return arrayType.leafComponentType();
        }
        return env.createArrayType(arrayType.leafComponentType(), arrayType.dimensions() - 1);
    }

    private Object reduceSubType(Scope scope, TypeBinding subCandidate, TypeBinding superCandidate) {
        if (subCandidate.isProperType(true) && superCandidate.isProperType(true)) {
            if (subCandidate.isSubtypeOf(superCandidate, true)) {
                return TRUE;
            }
            return FALSE;
        }
        if (subCandidate.id == 12) {
            return TRUE;
        }
        if (superCandidate.id == 12) {
            return FALSE;
        }
        if (subCandidate instanceof InferenceVariable) {
            return new TypeBound((InferenceVariable)subCandidate, superCandidate, 2, this.isSoft);
        }
        if (superCandidate instanceof InferenceVariable) {
            return new TypeBound((InferenceVariable)superCandidate, subCandidate, 3, this.isSoft);
        }
        switch (superCandidate.kind()) {
            case 4: 
            case 1028: 
            case 2052: {
                if (subCandidate.isSubtypeOf(superCandidate, true)) {
                    return TRUE;
                }
                return FALSE;
            }
            case 260: {
                ArrayList<ConstraintFormula> constraints = new ArrayList<ConstraintFormula>();
                boolean isFirst = true;
                while (superCandidate != null && superCandidate.kind() == 260 && subCandidate != null) {
                    if (!this.addConstraintsFromTypeParameters(subCandidate, (ParameterizedTypeBinding)superCandidate, constraints) && isFirst) {
                        return FALSE;
                    }
                    isFirst = false;
                    superCandidate = superCandidate.enclosingType();
                    subCandidate = subCandidate.enclosingType();
                }
                switch (constraints.size()) {
                    case 0: {
                        return TRUE;
                    }
                    case 1: {
                        return constraints.get(0);
                    }
                }
                return constraints.toArray(new ConstraintFormula[constraints.size()]);
            }
            case 68: {
                TypeBinding tPrime = ((ArrayBinding)superCandidate).elementsType();
                ArrayBinding sPrimeArray = null;
                switch (subCandidate.kind()) {
                    case 8196: {
                        WildcardBinding intersection = (WildcardBinding)subCandidate;
                        sPrimeArray = this.findMostSpecificSuperArray(intersection.bound, intersection.otherBounds, intersection);
                        break;
                    }
                    case 68: {
                        sPrimeArray = (ArrayBinding)subCandidate;
                        break;
                    }
                    case 4100: {
                        TypeVariableBinding subTVB = (TypeVariableBinding)subCandidate;
                        sPrimeArray = this.findMostSpecificSuperArray(subTVB.firstBound, subTVB.otherUpperBounds(), subTVB);
                        break;
                    }
                    default: {
                        return FALSE;
                    }
                }
                if (sPrimeArray == null) {
                    return FALSE;
                }
                TypeBinding sPrime = sPrimeArray.elementsType();
                if (!tPrime.isPrimitiveType() && !sPrime.isPrimitiveType()) {
                    return ConstraintTypeFormula.create(sPrime, tPrime, 2, this.isSoft);
                }
                return TypeBinding.equalsEquals(tPrime, sPrime) ? TRUE : FALSE;
            }
            case 516: {
                ReferenceBinding[] intersectingTypes;
                if (subCandidate.kind() == 8196 && (intersectingTypes = subCandidate.getIntersectingTypes()) != null) {
                    int i = 0;
                    while (i < intersectingTypes.length) {
                        if (TypeBinding.equalsEquals(intersectingTypes[i], superCandidate)) {
                            return true;
                        }
                        ++i;
                    }
                }
                WildcardBinding variable = (WildcardBinding)superCandidate;
                if (variable.boundKind == 2) {
                    return ConstraintTypeFormula.create(subCandidate, variable.bound, 2, this.isSoft);
                }
                return FALSE;
            }
            case 4100: {
                ReferenceBinding[] intersectingTypes;
                if (subCandidate.kind() == 8196 && (intersectingTypes = subCandidate.getIntersectingTypes()) != null) {
                    int i = 0;
                    while (i < intersectingTypes.length) {
                        if (TypeBinding.equalsEquals(intersectingTypes[i], superCandidate)) {
                            return true;
                        }
                        ++i;
                    }
                }
                if (superCandidate instanceof CaptureBinding) {
                    CaptureBinding capture = (CaptureBinding)superCandidate;
                    if (capture.lowerBound != null) {
                        return ConstraintTypeFormula.create(subCandidate, capture.lowerBound, 2, this.isSoft);
                    }
                }
                return FALSE;
            }
            case 8196: {
                superCandidate = ((WildcardBinding)superCandidate).allBounds();
            }
            case 32772: {
                ReferenceBinding[] intersectingTypes = ((IntersectionTypeBinding18)superCandidate).intersectingTypes;
                ConstraintFormula[] result = new ConstraintFormula[intersectingTypes.length];
                int i = 0;
                while (i < intersectingTypes.length) {
                    result[i] = ConstraintTypeFormula.create(subCandidate, intersectingTypes[i], 2, this.isSoft);
                    ++i;
                }
                return result;
            }
            case 65540: {
                PolyTypeBinding poly = (PolyTypeBinding)superCandidate;
                Invocation invocation = (Invocation)((Object)poly.expression);
                MethodBinding binding = invocation.binding();
                if (binding == null || !binding.isValidBinding()) {
                    return FALSE;
                }
                TypeBinding returnType = binding.isConstructor() ? binding.declaringClass : binding.returnType;
                return this.reduceSubType(scope, subCandidate, returnType.capture(scope, invocation.sourceStart(), invocation.sourceEnd()));
            }
        }
        throw new IllegalStateException("Unexpected RHS " + superCandidate);
    }

    private ArrayBinding findMostSpecificSuperArray(TypeBinding firstBound, TypeBinding[] otherUpperBounds, TypeBinding theType) {
        int numArrayBounds = 0;
        ArrayBinding result = null;
        if (firstBound != null && firstBound.isArrayType()) {
            result = (ArrayBinding)firstBound;
            ++numArrayBounds;
        }
        int i = 0;
        while (i < otherUpperBounds.length) {
            if (otherUpperBounds[i].isArrayType()) {
                result = (ArrayBinding)otherUpperBounds[i];
                ++numArrayBounds;
            }
            ++i;
        }
        if (numArrayBounds == 0) {
            return null;
        }
        if (numArrayBounds == 1) {
            return result;
        }
        InferenceContext18.missingImplementation("Extracting array from intersection is not defined");
        return null;
    }

    boolean addConstraintsFromTypeParameters(TypeBinding subCandidate, ParameterizedTypeBinding ca, List<ConstraintFormula> constraints) {
        TypeBinding cb = subCandidate.findSuperTypeOriginatingFrom(ca);
        if (cb == null) {
            return false;
        }
        if (TypeBinding.equalsEquals(ca, cb)) {
            return true;
        }
        if (!(cb instanceof ParameterizedTypeBinding)) {
            return ca.isParameterizedWithOwnVariables();
        }
        TypeBinding[] bi = ((ParameterizedTypeBinding)cb).arguments;
        TypeBinding[] ai = ca.arguments;
        if (ai == null) {
            return true;
        }
        if (cb.isRawType() || bi == null || bi.length == 0) {
            return this.isSoft;
        }
        int i = 0;
        while (i < ai.length) {
            constraints.add(ConstraintTypeFormula.create(bi[i], ai[i], 5, this.isSoft));
            ++i;
        }
        return true;
    }

    public boolean equalsEquals(ConstraintTypeFormula that) {
        return that != null && this.relation == that.relation && this.isSoft == that.isSoft && TypeBinding.equalsEquals(this.left, that.left) && TypeBinding.equalsEquals(this.right, that.right);
    }

    @Override
    public boolean applySubstitution(BoundSet solutionSet, InferenceVariable[] variables) {
        super.applySubstitution(solutionSet, variables);
        int i = 0;
        while (i < variables.length) {
            InferenceVariable variable = variables[i];
            TypeBinding instantiation = solutionSet.getInstantiation(variables[i], null);
            if (instantiation == null) {
                return false;
            }
            this.left = this.left.substituteInferenceVariable(variable, instantiation);
            ++i;
        }
        return true;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("Type Constraint:\n");
        buf.append('\t').append('\u27e8');
        this.appendTypeName(buf, this.left);
        buf.append(ConstraintTypeFormula.relationToString(this.relation));
        this.appendTypeName(buf, this.right);
        buf.append('\u27e9');
        return buf.toString();
    }
}

