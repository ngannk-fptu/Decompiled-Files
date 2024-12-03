/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.SwitchExpression;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBindingVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

public class NullAnnotationMatching {
    public static final NullAnnotationMatching NULL_ANNOTATIONS_OK = new NullAnnotationMatching(Severity.OK, 1, null);
    public static final NullAnnotationMatching NULL_ANNOTATIONS_OK_NONNULL = new NullAnnotationMatching(Severity.OK, 4, null);
    public static final NullAnnotationMatching NULL_ANNOTATIONS_UNCHECKED = new NullAnnotationMatching(Severity.UNCHECKED, 1, null);
    public static final NullAnnotationMatching NULL_ANNOTATIONS_MISMATCH = new NullAnnotationMatching(Severity.MISMATCH, 1, null);
    private final Severity severity;
    public final TypeBinding superTypeHint;
    public final int nullStatus;

    NullAnnotationMatching(Severity severity, int nullStatus, TypeBinding superTypeHint) {
        this.severity = severity;
        this.superTypeHint = superTypeHint;
        this.nullStatus = nullStatus;
    }

    public NullAnnotationMatching withNullStatus(int updatedNullStatus) {
        return updatedNullStatus == this.nullStatus ? this : new NullAnnotationMatching(this.severity, updatedNullStatus, this.superTypeHint);
    }

    public boolean isAnyMismatch() {
        return this.severity.isAnyMismatch();
    }

    public boolean isUnchecked() {
        return this.severity == Severity.UNCHECKED || this.severity == Severity.UNCHECKED_TO_UNANNOTATED;
    }

    public boolean isAnnotatedToUnannotated() {
        return this.severity == Severity.UNCHECKED_TO_UNANNOTATED;
    }

    public boolean isDefiniteMismatch() {
        return this.severity == Severity.MISMATCH;
    }

    public boolean wantToReport() {
        return this.severity == Severity.LEGACY_WARNING;
    }

    public boolean isPotentiallyNullMismatch() {
        return !this.isDefiniteMismatch() && this.nullStatus != -1 && (this.nullStatus & 0x10) != 0;
    }

    public String superTypeHintName(CompilerOptions options, boolean shortNames) {
        return String.valueOf(this.superTypeHint.nullAnnotatedReadableName(options, shortNames));
    }

    public static int checkAssignment(BlockScope currentScope, FlowContext flowContext, VariableBinding var, FlowInfo flowInfo, int nullStatus, Expression expression, TypeBinding providedType) {
        if (providedType == null) {
            return 1;
        }
        long lhsTagBits = 0L;
        boolean hasReported = false;
        boolean usesNullTypeAnnotations = currentScope.environment().usesNullTypeAnnotations();
        if (!usesNullTypeAnnotations) {
            lhsTagBits = var.tagBits & 0x180000000000000L;
        } else {
            if (expression instanceof ConditionalExpression && expression.isPolyExpression()) {
                int status2;
                ConditionalExpression ce = (ConditionalExpression)expression;
                int status1 = NullAnnotationMatching.checkAssignment(currentScope, flowContext, var, flowInfo, ce.ifTrueNullStatus, ce.valueIfTrue, ce.valueIfTrue.resolvedType);
                if (status1 == (status2 = NullAnnotationMatching.checkAssignment(currentScope, flowContext, var, flowInfo, ce.ifFalseNullStatus, ce.valueIfFalse, ce.valueIfFalse.resolvedType))) {
                    return status1;
                }
                return nullStatus;
            }
            if (expression instanceof SwitchExpression && expression.isPolyExpression()) {
                SwitchExpression se = (SwitchExpression)expression;
                Expression[] resExprs = se.resultExpressions.toArray(new Expression[0]);
                Expression re = resExprs[0];
                int status0 = NullAnnotationMatching.checkAssignment(currentScope, flowContext, var, flowInfo, re.nullStatus(flowInfo, flowContext), re, re.resolvedType);
                boolean identicalStatus = true;
                int i = 1;
                int l = resExprs.length;
                while (i < l) {
                    re = resExprs[i];
                    int otherStatus = NullAnnotationMatching.checkAssignment(currentScope, flowContext, var, flowInfo, re.nullStatus(flowInfo, flowContext), re, re.resolvedType);
                    identicalStatus &= status0 == otherStatus;
                    ++i;
                }
                return identicalStatus ? status0 : nullStatus;
            }
            lhsTagBits = var.type.tagBits & 0x180000000000000L;
            NullAnnotationMatching annotationStatus = NullAnnotationMatching.analyse(var.type, providedType, null, null, nullStatus, expression, CheckMode.COMPATIBLE);
            if (annotationStatus.isAnyMismatch()) {
                flowContext.recordNullityMismatch(currentScope, expression, providedType, var.type, flowInfo, nullStatus, annotationStatus);
                hasReported = true;
            } else {
                if (annotationStatus.wantToReport()) {
                    annotationStatus.report(currentScope);
                }
                if (annotationStatus.nullStatus != 1) {
                    return annotationStatus.nullStatus;
                }
            }
        }
        if (lhsTagBits == 0x100000000000000L && nullStatus != 4) {
            if (!hasReported) {
                flowContext.recordNullityMismatch(currentScope, expression, providedType, var.type, flowInfo, nullStatus, null);
            }
            return 4;
        }
        if (lhsTagBits == 0x80000000000000L && nullStatus == 1) {
            if (usesNullTypeAnnotations && providedType.isTypeVariable() && (providedType.tagBits & 0x180000000000000L) == 0L) {
                return 48;
            }
            return 24;
        }
        return nullStatus;
    }

    public static NullAnnotationMatching analyse(TypeBinding requiredType, TypeBinding providedType, int nullStatus) {
        return NullAnnotationMatching.analyse(requiredType, providedType, null, null, nullStatus, null, CheckMode.COMPATIBLE);
    }

    public static NullAnnotationMatching analyse(TypeBinding requiredType, TypeBinding providedType, TypeBinding providedSubstitute, Substitution substitution, int nullStatus, Expression providedExpression, CheckMode mode) {
        if (!requiredType.enterRecursiveFunction()) {
            return NULL_ANNOTATIONS_OK;
        }
        try {
            Severity severity = Severity.OK;
            TypeBinding superTypeHint = null;
            TypeBinding originalRequiredType = requiredType;
            NullAnnotationMatching okStatus = NULL_ANNOTATIONS_OK;
            if (NullAnnotationMatching.areSameTypes(requiredType, providedType, providedSubstitute)) {
                if ((requiredType.tagBits & 0x100000000000000L) != 0L) {
                    NullAnnotationMatching nullAnnotationMatching = NullAnnotationMatching.okNonNullStatus(providedExpression);
                    return nullAnnotationMatching;
                }
                NullAnnotationMatching nullAnnotationMatching = okStatus;
                return nullAnnotationMatching;
            }
            if (requiredType instanceof TypeVariableBinding && substitution != null && (mode == CheckMode.EXACT || mode == CheckMode.COMPATIBLE || mode == CheckMode.BOUND_SUPER_CHECK)) {
                requiredType.exitRecursiveFunction();
                requiredType = Scope.substitute(substitution, requiredType);
                if (!requiredType.enterRecursiveFunction()) {
                    NullAnnotationMatching nullAnnotationMatching = NULL_ANNOTATIONS_OK;
                    return nullAnnotationMatching;
                }
                if (NullAnnotationMatching.areSameTypes(requiredType, providedType, providedSubstitute)) {
                    if ((requiredType.tagBits & 0x100000000000000L) != 0L) {
                        NullAnnotationMatching nullAnnotationMatching = NullAnnotationMatching.okNonNullStatus(providedExpression);
                        return nullAnnotationMatching;
                    }
                    NullAnnotationMatching nullAnnotationMatching = okStatus;
                    return nullAnnotationMatching;
                }
            }
            if (mode == CheckMode.BOUND_CHECK && requiredType instanceof TypeVariableBinding) {
                boolean passedBoundCheck;
                boolean bl = passedBoundCheck = substitution instanceof ParameterizedTypeBinding && (((ParameterizedTypeBinding)substitution).tagBits & 0x400000L) != 0L;
                if (!passedBoundCheck) {
                    ReferenceBinding[] superInterfaces;
                    ReferenceBinding superClass = requiredType.superclass();
                    if (superClass != null && (superClass.hasNullTypeAnnotations() || substitution != null)) {
                        NullAnnotationMatching status = NullAnnotationMatching.analyse(superClass, providedType, null, substitution, nullStatus, providedExpression, CheckMode.BOUND_SUPER_CHECK);
                        if ((severity = severity.max(status.severity)) == Severity.MISMATCH) {
                            NullAnnotationMatching nullAnnotationMatching = new NullAnnotationMatching(severity, nullStatus, superTypeHint);
                            return nullAnnotationMatching;
                        }
                    }
                    if ((superInterfaces = requiredType.superInterfaces()) != null) {
                        int i = 0;
                        while (i < superInterfaces.length) {
                            if (superInterfaces[i].hasNullTypeAnnotations() || substitution != null) {
                                NullAnnotationMatching status = NullAnnotationMatching.analyse(superInterfaces[i], providedType, null, substitution, nullStatus, providedExpression, CheckMode.BOUND_SUPER_CHECK);
                                if ((severity = severity.max(status.severity)) == Severity.MISMATCH) {
                                    NullAnnotationMatching nullAnnotationMatching = new NullAnnotationMatching(severity, nullStatus, superTypeHint);
                                    return nullAnnotationMatching;
                                }
                            }
                            ++i;
                        }
                    }
                }
            }
            if (requiredType instanceof ArrayBinding) {
                long[] requiredDimsTagBits = ((ArrayBinding)requiredType).nullTagBitsPerDimension;
                if (requiredDimsTagBits != null) {
                    int dims = requiredType.dimensions();
                    if (requiredType.dimensions() == providedType.dimensions()) {
                        long[] providedDimsTagBits = ((ArrayBinding)providedType).nullTagBitsPerDimension;
                        if (providedDimsTagBits == null) {
                            providedDimsTagBits = new long[dims + 1];
                        }
                        int currentNullStatus = nullStatus;
                        int i = 0;
                        while (i <= dims) {
                            long requiredBits = NullAnnotationMatching.validNullTagBits(requiredDimsTagBits[i]);
                            long providedBits = NullAnnotationMatching.validNullTagBits(providedDimsTagBits[i]);
                            if (i == 0 && requiredBits == 0x80000000000000L && nullStatus != -1 && mode.requiredNullableMatchesAll()) {
                                if (nullStatus == 2) {
                                    break;
                                }
                            } else {
                                Expression[] dimensions;
                                Expression previousDim;
                                if (i > 0) {
                                    currentNullStatus = -1;
                                }
                                Severity dimSeverity = NullAnnotationMatching.computeNullProblemSeverity(requiredBits, providedBits, currentNullStatus, i == 0 ? mode : mode.toDetail(), null);
                                if (i > 0 && dimSeverity == Severity.UNCHECKED && providedExpression instanceof ArrayAllocationExpression && providedBits == 0L && requiredBits != 0L && (previousDim = (dimensions = ((ArrayAllocationExpression)providedExpression).dimensions)[i - 1]) instanceof IntLiteral && previousDim.constant.intValue() == 0) {
                                    dimSeverity = Severity.OK;
                                    nullStatus = -1;
                                    break;
                                }
                                if ((severity = severity.max(dimSeverity)) == Severity.MISMATCH) {
                                    if (nullStatus == 2) {
                                        NullAnnotationMatching nullAnnotationMatching = new NullAnnotationMatching(severity, nullStatus, null);
                                        return nullAnnotationMatching;
                                    }
                                    NullAnnotationMatching nullAnnotationMatching = NULL_ANNOTATIONS_MISMATCH;
                                    return nullAnnotationMatching;
                                }
                            }
                            if (severity == Severity.OK) {
                                nullStatus = -1;
                            }
                            ++i;
                        }
                    } else if (providedType.id == 12 && dims > 0 && requiredDimsTagBits[0] == 0x100000000000000L) {
                        NullAnnotationMatching nullAnnotationMatching = NULL_ANNOTATIONS_MISMATCH;
                        return nullAnnotationMatching;
                    }
                }
            } else if (requiredType.hasNullTypeAnnotations() || providedType.hasNullTypeAnnotations() || requiredType.isTypeVariable()) {
                long requiredBits = NullAnnotationMatching.requiredNullTagBits(requiredType, mode);
                if (requiredBits != 0x80000000000000L || nullStatus == -1 || !mode.requiredNullableMatchesAll()) {
                    long providedBits = NullAnnotationMatching.providedNullTagBits(providedType);
                    Severity s = NullAnnotationMatching.computeNullProblemSeverity(requiredBits, providedBits, nullStatus, mode, originalRequiredType);
                    if (s.isAnyMismatch() && requiredType.isWildcard() && requiredBits != 0L && ((WildcardBinding)requiredType).determineNullBitsFromDeclaration(null, null) == 0L) {
                        TypeVariableBinding typeVariable = ((WildcardBinding)requiredType).typeVariable();
                        if ((typeVariable.tagBits & 0x180000000000000L) != 0L) {
                            s = Severity.OK;
                        }
                    }
                    if (!(severity = severity.max(s)).isAnyMismatch() && (providedBits & 0x180000000000000L) == 0x100000000000000L) {
                        okStatus = NullAnnotationMatching.okNonNullStatus(providedExpression);
                    }
                }
                if (severity != Severity.MISMATCH && nullStatus != 2) {
                    TypeBinding providedSubstituteSuper;
                    TypeBinding providedSuper = providedType.findSuperTypeOriginatingFrom(requiredType);
                    TypeBinding typeBinding = providedSubstituteSuper = providedSubstitute != null ? providedSubstitute.findSuperTypeOriginatingFrom(requiredType) : null;
                    if (severity == Severity.UNCHECKED && requiredType.isTypeVariable() && providedType.isTypeVariable() && (providedSuper == requiredType || providedSubstituteSuper == requiredType)) {
                        severity = Severity.OK;
                    }
                    if (providedSuper != providedType) {
                        superTypeHint = providedSuper;
                    }
                    if (requiredType.isParameterizedType() && providedSuper instanceof ParameterizedTypeBinding) {
                        TypeBinding[] providedSubstitutes;
                        TypeBinding[] requiredArguments = ((ParameterizedTypeBinding)requiredType).arguments;
                        TypeBinding[] providedArguments = ((ParameterizedTypeBinding)providedSuper).arguments;
                        TypeBinding[] typeBindingArray = providedSubstitutes = providedSubstituteSuper instanceof ParameterizedTypeBinding ? ((ParameterizedTypeBinding)providedSubstituteSuper).arguments : null;
                        if (requiredArguments != null && providedArguments != null && requiredArguments.length == providedArguments.length) {
                            int i = 0;
                            while (i < requiredArguments.length) {
                                TypeBinding providedArgSubstitute = providedSubstitutes != null ? providedSubstitutes[i] : null;
                                NullAnnotationMatching status = NullAnnotationMatching.analyse(requiredArguments[i], providedArguments[i], providedArgSubstitute, substitution, -1, providedExpression, mode.toDetail());
                                if ((severity = severity.max(status.severity)) == Severity.MISMATCH) {
                                    NullAnnotationMatching nullAnnotationMatching = new NullAnnotationMatching(severity, nullStatus, superTypeHint);
                                    return nullAnnotationMatching;
                                }
                                ++i;
                            }
                        }
                    }
                    ReferenceBinding requiredEnclosing = requiredType.enclosingType();
                    ReferenceBinding providedEnclosing = providedType.enclosingType();
                    if (requiredEnclosing != null && providedEnclosing != null) {
                        ReferenceBinding providedEnclSubstitute = providedSubstitute != null ? providedSubstitute.enclosingType() : null;
                        NullAnnotationMatching status = NullAnnotationMatching.analyse(requiredEnclosing, providedEnclosing, providedEnclSubstitute, substitution, -1, providedExpression, mode);
                        severity = severity.max(status.severity);
                    }
                }
            }
            if (!severity.isAnyMismatch()) {
                NullAnnotationMatching nullAnnotationMatching = okStatus;
                return nullAnnotationMatching;
            }
            NullAnnotationMatching nullAnnotationMatching = new NullAnnotationMatching(severity, nullStatus, superTypeHint);
            return nullAnnotationMatching;
        }
        finally {
            requiredType.exitRecursiveFunction();
        }
    }

    public void report(Scope scope) {
    }

    public static NullAnnotationMatching okNonNullStatus(final Expression providedExpression) {
        MethodBinding method;
        if (providedExpression instanceof MessageSend && (method = ((MessageSend)providedExpression).binding) != null && method.isValidBinding()) {
            MethodBinding originalMethod = method.original();
            ReferenceBinding originalDeclaringClass = originalMethod.declaringClass;
            if (originalDeclaringClass instanceof BinaryTypeBinding && ((BinaryTypeBinding)originalDeclaringClass).externalAnnotationStatus.isPotentiallyUnannotatedLib() && originalMethod.returnType.isTypeVariable() && (originalMethod.returnType.tagBits & 0x180000000000000L) == 0L) {
                final int severity = ((BinaryTypeBinding)originalDeclaringClass).externalAnnotationStatus == BinaryTypeBinding.ExternalAnnotationStatus.NO_EEA_FILE ? 0 : 1024;
                return new NullAnnotationMatching(Severity.LEGACY_WARNING, 1, null){

                    @Override
                    public void report(Scope scope) {
                        scope.problemReporter().nonNullTypeVariableInUnannotatedBinary(scope.environment(), method, providedExpression, severity);
                    }
                };
            }
        }
        return NULL_ANNOTATIONS_OK_NONNULL;
    }

    protected static boolean areSameTypes(TypeBinding requiredType, TypeBinding providedType, TypeBinding providedSubstitute) {
        if (requiredType == providedType) {
            return true;
        }
        if (requiredType.isParameterizedType() || requiredType.isArrayType()) {
            return false;
        }
        if (TypeBinding.notEquals(requiredType, providedType)) {
            if (requiredType instanceof CaptureBinding) {
                TypeBinding lowerBound = ((CaptureBinding)requiredType).lowerBound;
                if (lowerBound != null && NullAnnotationMatching.areSameTypes(lowerBound, providedType, providedSubstitute)) {
                    return (requiredType.tagBits & 0x180000000000000L) == (providedType.tagBits & 0x180000000000000L);
                }
            } else {
                TypeBinding upperBound;
                if (requiredType.kind() == 4100 && requiredType == providedSubstitute) {
                    return true;
                }
                if (providedType instanceof CaptureBinding && (upperBound = ((CaptureBinding)providedType).upperBound()) != null && NullAnnotationMatching.areSameTypes(requiredType, upperBound, providedSubstitute)) {
                    return (requiredType.tagBits & 0x180000000000000L) == (providedType.tagBits & 0x180000000000000L);
                }
            }
            return false;
        }
        return (requiredType.tagBits & 0x180000000000000L) == (providedType.tagBits & 0x180000000000000L);
    }

    static long requiredNullTagBits(TypeBinding type, CheckMode mode) {
        long tagBits = type.tagBits & 0x180000000000000L;
        if (tagBits != 0L) {
            return NullAnnotationMatching.validNullTagBits(tagBits);
        }
        if (type.isWildcard()) {
            WildcardBinding wildcardBinding = (WildcardBinding)type;
            TypeBinding bound = wildcardBinding.bound;
            tagBits = bound != null ? bound.tagBits & 0x180000000000000L : 0L;
            switch (wildcardBinding.boundKind) {
                case 2: {
                    if (tagBits != 0x80000000000000L) break;
                    return 0x80000000000000L;
                }
                case 1: {
                    if (tagBits != 0x100000000000000L) break;
                    return tagBits;
                }
            }
            return 0x180000000000000L;
        }
        if (type.isTypeVariable()) {
            TypeBinding lowerBound;
            if (type.isCapture() && (lowerBound = ((CaptureBinding)type).lowerBound) != null && (tagBits = lowerBound.tagBits & 0x180000000000000L) == 0x80000000000000L) {
                return 0x80000000000000L;
            }
            switch (mode) {
                case BOUND_CHECK: 
                case BOUND_SUPER_CHECK: 
                case OVERRIDE_RETURN: 
                case OVERRIDE: {
                    break;
                }
                default: {
                    return 0x100000000000000L;
                }
            }
        }
        return 0L;
    }

    static long providedNullTagBits(TypeBinding type) {
        long tagBits = type.tagBits & 0x180000000000000L;
        if (tagBits != 0L) {
            return NullAnnotationMatching.validNullTagBits(tagBits);
        }
        if (type.isWildcard()) {
            return 0x180000000000000L;
        }
        if (type.isTypeVariable()) {
            TypeBinding lowerBound;
            TypeVariableBinding typeVariable = (TypeVariableBinding)type;
            boolean haveNullBits = false;
            if (typeVariable.isCapture() && (lowerBound = ((CaptureBinding)typeVariable).lowerBound) != null) {
                tagBits = lowerBound.tagBits & 0x180000000000000L;
                if (tagBits == 0x80000000000000L) {
                    return 0x80000000000000L;
                }
                haveNullBits |= tagBits != 0L;
            }
            if (typeVariable.firstBound != null) {
                long boundBits = typeVariable.firstBound.tagBits & 0x180000000000000L;
                if (boundBits == 0x100000000000000L) {
                    return 0x100000000000000L;
                }
                haveNullBits |= boundBits != 0L;
            }
            if (haveNullBits) {
                return 0x180000000000000L;
            }
        }
        return 0L;
    }

    public static int nullStatusFromExpressionType(TypeBinding type) {
        if (type.isFreeTypeVariable()) {
            return 48;
        }
        long bits = type.tagBits & 0x180000000000000L;
        if (bits == 0L) {
            return 1;
        }
        if (bits == 0x100000000000000L) {
            return 4;
        }
        return 48;
    }

    public static long validNullTagBits(long bits) {
        return (bits &= 0x180000000000000L) == 0x180000000000000L ? 0L : bits;
    }

    public static TypeBinding moreDangerousType(TypeBinding one, TypeBinding two) {
        long twoNullBits;
        if (one == null) {
            return null;
        }
        long oneNullBits = NullAnnotationMatching.validNullTagBits(one.tagBits);
        if (oneNullBits != (twoNullBits = NullAnnotationMatching.validNullTagBits(two.tagBits))) {
            if (oneNullBits == 0x80000000000000L) {
                return one;
            }
            if (twoNullBits == 0x80000000000000L) {
                return two;
            }
            if (oneNullBits == 0L) {
                return one;
            }
            return two;
        }
        if (one != two && NullAnnotationMatching.analyse(one, two, -1).isAnyMismatch()) {
            return two;
        }
        return one;
    }

    private static Severity computeNullProblemSeverity(long requiredBits, long providedBits, int nullStatus, CheckMode mode, TypeBinding requiredType) {
        if (requiredBits == providedBits) {
            return Severity.OK;
        }
        if (requiredBits == 0L) {
            switch (mode) {
                case EXACT: {
                    if (providedBits == 0x100000000000000L && !(requiredType instanceof TypeVariableBinding)) {
                        return Severity.UNCHECKED_TO_UNANNOTATED;
                    }
                    return Severity.OK;
                }
                case COMPATIBLE: 
                case BOUND_CHECK: 
                case BOUND_SUPER_CHECK: {
                    return Severity.OK;
                }
                case OVERRIDE_RETURN: {
                    if (providedBits == 0x100000000000000L) {
                        return Severity.OK;
                    }
                    if (!(requiredType instanceof TypeVariableBinding)) {
                        return Severity.OK;
                    }
                    return Severity.UNCHECKED;
                }
                case OVERRIDE: {
                    return Severity.UNCHECKED;
                }
            }
        } else {
            if (requiredBits == 0x180000000000000L) {
                if (mode == CheckMode.EXACT && providedBits == 0x100000000000000L && requiredType instanceof WildcardBinding) {
                    WildcardBinding wildcard = (WildcardBinding)requiredType;
                    if (wildcard.boundKind == 2 && providedBits == 0x100000000000000L) {
                        TypeBinding bound = wildcard.bound;
                        if (bound != null && (bound.tagBits & 0x180000000000000L) != 0L) {
                            return Severity.OK;
                        }
                        return Severity.UNCHECKED_TO_UNANNOTATED;
                    }
                }
                return Severity.OK;
            }
            if (requiredBits == 0x100000000000000L) {
                switch (mode) {
                    case COMPATIBLE: {
                        if (nullStatus == 2) {
                            return Severity.MISMATCH;
                        }
                    }
                    case BOUND_SUPER_CHECK: {
                        if (nullStatus == 4) {
                            return Severity.OK;
                        }
                    }
                    case EXACT: 
                    case BOUND_CHECK: 
                    case OVERRIDE_RETURN: 
                    case OVERRIDE: {
                        if (providedBits == 0L) {
                            return Severity.UNCHECKED;
                        }
                        return Severity.MISMATCH;
                    }
                }
            } else if (requiredBits == 0x80000000000000L) {
                switch (mode) {
                    case COMPATIBLE: 
                    case BOUND_SUPER_CHECK: 
                    case OVERRIDE_RETURN: {
                        return Severity.OK;
                    }
                    case EXACT: 
                    case BOUND_CHECK: {
                        if (providedBits == 0L) {
                            return Severity.UNCHECKED;
                        }
                        return Severity.MISMATCH;
                    }
                    case OVERRIDE: {
                        return Severity.MISMATCH;
                    }
                }
            }
        }
        return Severity.OK;
    }

    public static MethodBinding checkForContradictions(MethodBinding method, Object location, Scope scope) {
        int start = 0;
        int end = 0;
        if (location instanceof InvocationSite) {
            start = ((InvocationSite)location).sourceStart();
            end = ((InvocationSite)location).sourceEnd();
        } else if (location instanceof ASTNode) {
            start = ((ASTNode)location).sourceStart;
            end = ((ASTNode)location).sourceEnd;
        }
        SearchContradictions searchContradiction = new SearchContradictions();
        TypeBindingVisitor.visit((TypeBindingVisitor)searchContradiction, method.returnType);
        if (searchContradiction.typeWithContradiction != null) {
            if (scope == null) {
                return new ProblemMethodBinding(method, method.selector, method.parameters, 25);
            }
            scope.problemReporter().contradictoryNullAnnotationsInferred(method, start, end, location instanceof FunctionalExpression);
            return method;
        }
        Expression[] arguments = null;
        if (location instanceof Invocation) {
            arguments = ((Invocation)location).arguments();
        }
        int i = 0;
        while (i < method.parameters.length) {
            TypeBindingVisitor.visit((TypeBindingVisitor)searchContradiction, method.parameters[i]);
            if (searchContradiction.typeWithContradiction != null) {
                if (scope == null) {
                    return new ProblemMethodBinding(method, method.selector, method.parameters, 25);
                }
                if (arguments != null && i < arguments.length) {
                    scope.problemReporter().contradictoryNullAnnotationsInferred(method, arguments[i]);
                } else {
                    scope.problemReporter().contradictoryNullAnnotationsInferred(method, start, end, location instanceof FunctionalExpression);
                }
                return method;
            }
            ++i;
        }
        return method;
    }

    public static boolean hasContradictions(TypeBinding type) {
        SearchContradictions searchContradiction = new SearchContradictions();
        TypeBindingVisitor.visit((TypeBindingVisitor)searchContradiction, type);
        return searchContradiction.typeWithContradiction != null;
    }

    public static TypeBinding strongerType(TypeBinding type1, TypeBinding type2, LookupEnvironment environment) {
        if ((type1.tagBits & 0x100000000000000L) != 0L) {
            return NullAnnotationMatching.mergeTypeAnnotations(type1, type2, true, environment);
        }
        return NullAnnotationMatching.mergeTypeAnnotations(type2, type1, true, environment);
    }

    public static TypeBinding[] weakerTypes(TypeBinding[] parameters1, TypeBinding[] parameters2, LookupEnvironment environment) {
        TypeBinding[] newParameters = new TypeBinding[parameters1.length];
        int i = 0;
        while (i < newParameters.length) {
            long tagBits1 = parameters1[i].tagBits;
            long tagBits2 = parameters2[i].tagBits;
            newParameters[i] = (tagBits1 & 0x80000000000000L) != 0L ? NullAnnotationMatching.mergeTypeAnnotations(parameters1[i], parameters2[i], true, environment) : ((tagBits2 & 0x80000000000000L) != 0L ? NullAnnotationMatching.mergeTypeAnnotations(parameters2[i], parameters1[i], true, environment) : ((tagBits1 & 0x100000000000000L) == 0L ? NullAnnotationMatching.mergeTypeAnnotations(parameters1[i], parameters2[i], true, environment) : NullAnnotationMatching.mergeTypeAnnotations(parameters2[i], parameters1[i], true, environment)));
            ++i;
        }
        return newParameters;
    }

    private static TypeBinding mergeTypeAnnotations(TypeBinding type, TypeBinding otherType, boolean top, LookupEnvironment environment) {
        AnnotationBinding[] otherAnnotations;
        TypeBinding mainType = type;
        if (!top && (otherAnnotations = otherType.getTypeAnnotations()) != Binding.NO_ANNOTATIONS) {
            mainType = environment.createAnnotatedType(type, otherAnnotations);
        }
        if (mainType.isParameterizedType() && otherType.isParameterizedType()) {
            ParameterizedTypeBinding ptb = (ParameterizedTypeBinding)type;
            ParameterizedTypeBinding otherPTB = (ParameterizedTypeBinding)otherType;
            TypeBinding[] typeArguments = ptb.arguments;
            TypeBinding[] otherTypeArguments = otherPTB.arguments;
            TypeBinding[] newTypeArguments = new TypeBinding[typeArguments.length];
            int i = 0;
            while (i < typeArguments.length) {
                newTypeArguments[i] = NullAnnotationMatching.mergeTypeAnnotations(typeArguments[i], otherTypeArguments[i], false, environment);
                ++i;
            }
            return environment.createParameterizedType(ptb.genericType(), newTypeArguments, ptb.enclosingType());
        }
        return mainType;
    }

    public String toString() {
        if (this == NULL_ANNOTATIONS_OK) {
            return "OK";
        }
        if (this == NULL_ANNOTATIONS_MISMATCH) {
            return "MISMATCH";
        }
        if (this == NULL_ANNOTATIONS_OK_NONNULL) {
            return "OK NonNull";
        }
        if (this == NULL_ANNOTATIONS_UNCHECKED) {
            return "UNCHECKED";
        }
        StringBuilder buf = new StringBuilder();
        buf.append("Analysis result: severity=" + (Object)((Object)this.severity));
        buf.append(" nullStatus=" + this.nullStatus);
        return buf.toString();
    }

    public static enum CheckMode {
        COMPATIBLE{

            @Override
            boolean requiredNullableMatchesAll() {
                return true;
            }
        }
        ,
        EXACT,
        BOUND_CHECK,
        BOUND_SUPER_CHECK,
        OVERRIDE_RETURN{

            @Override
            CheckMode toDetail() {
                return OVERRIDE;
            }
        }
        ,
        OVERRIDE{

            @Override
            boolean requiredNullableMatchesAll() {
                return true;
            }

            @Override
            CheckMode toDetail() {
                return OVERRIDE;
            }
        };


        boolean requiredNullableMatchesAll() {
            return false;
        }

        CheckMode toDetail() {
            return EXACT;
        }
    }

    static class SearchContradictions
    extends TypeBindingVisitor {
        ReferenceBinding typeWithContradiction;

        SearchContradictions() {
        }

        @Override
        public boolean visit(ReferenceBinding referenceBinding) {
            if ((referenceBinding.tagBits & 0x180000000000000L) == 0x180000000000000L) {
                this.typeWithContradiction = referenceBinding;
                return false;
            }
            return true;
        }

        @Override
        public boolean visit(TypeVariableBinding typeVariable) {
            if (!this.visit((ReferenceBinding)typeVariable)) {
                return false;
            }
            long allNullBits = typeVariable.tagBits & 0x180000000000000L;
            if (typeVariable.firstBound != null) {
                allNullBits = typeVariable.firstBound.tagBits & 0x180000000000000L;
            }
            TypeBinding[] typeBindingArray = typeVariable.otherUpperBounds();
            int n = typeBindingArray.length;
            int n2 = 0;
            while (n2 < n) {
                TypeBinding otherBound = typeBindingArray[n2];
                allNullBits |= otherBound.tagBits & 0x180000000000000L;
                ++n2;
            }
            if (allNullBits == 0x180000000000000L) {
                this.typeWithContradiction = typeVariable;
                return false;
            }
            return true;
        }

        @Override
        public boolean visit(RawTypeBinding rawType) {
            return this.visit((ReferenceBinding)rawType);
        }

        @Override
        public boolean visit(WildcardBinding wildcardBinding) {
            long allNullBits = wildcardBinding.tagBits & 0x180000000000000L;
            switch (wildcardBinding.boundKind) {
                case 1: {
                    allNullBits |= wildcardBinding.bound.tagBits & 0x100000000000000L;
                    break;
                }
                case 2: {
                    allNullBits |= wildcardBinding.bound.tagBits & 0x80000000000000L;
                }
            }
            if (allNullBits == 0x180000000000000L) {
                this.typeWithContradiction = wildcardBinding;
                return false;
            }
            return true;
        }

        @Override
        public boolean visit(ParameterizedTypeBinding parameterizedTypeBinding) {
            if (!this.visit((ReferenceBinding)parameterizedTypeBinding)) {
                return false;
            }
            return super.visit(parameterizedTypeBinding);
        }
    }

    private static enum Severity {
        OK,
        LEGACY_WARNING,
        UNCHECKED,
        UNCHECKED_TO_UNANNOTATED,
        MISMATCH;


        public Severity max(Severity severity) {
            if (this.compareTo(severity) < 0) {
                return severity;
            }
            return this;
        }

        public boolean isAnyMismatch() {
            return this.compareTo(LEGACY_WARNING) > 0;
        }
    }
}

