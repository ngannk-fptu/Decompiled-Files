/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.TypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;

public class NonNullDefaultAwareTypeAnnotationWalker
extends TypeAnnotationWalker {
    private final int defaultNullness;
    private final boolean atDefaultLocation;
    private final boolean atTypeBound;
    private final boolean currentArrayContentIsNonNull;
    private final boolean isEmpty;
    private final IBinaryAnnotation nonNullAnnotation;
    private final LookupEnvironment environment;
    private boolean nextIsDefaultLocation;
    private boolean nextIsTypeBound;
    private boolean nextArrayContentIsNonNull;

    public NonNullDefaultAwareTypeAnnotationWalker(IBinaryTypeAnnotation[] typeAnnotations, int defaultNullness, LookupEnvironment environment) {
        super(typeAnnotations);
        this.nonNullAnnotation = NonNullDefaultAwareTypeAnnotationWalker.getNonNullAnnotation(environment);
        this.defaultNullness = defaultNullness;
        this.environment = environment;
        this.atDefaultLocation = false;
        this.atTypeBound = false;
        this.isEmpty = false;
        this.currentArrayContentIsNonNull = false;
    }

    public NonNullDefaultAwareTypeAnnotationWalker(int defaultNullness, LookupEnvironment environment) {
        this(defaultNullness, NonNullDefaultAwareTypeAnnotationWalker.getNonNullAnnotation(environment), false, false, environment, false);
    }

    NonNullDefaultAwareTypeAnnotationWalker(IBinaryTypeAnnotation[] typeAnnotations, long newMatches, int newPathPtr, int defaultNullness, IBinaryAnnotation nonNullAnnotation, boolean atDefaultLocation, boolean atTypeBound, LookupEnvironment environment, boolean currentArrayContentIsNonNull) {
        super(typeAnnotations, newMatches, newPathPtr);
        this.defaultNullness = defaultNullness;
        this.nonNullAnnotation = nonNullAnnotation;
        this.atDefaultLocation = atDefaultLocation;
        this.atTypeBound = atTypeBound;
        this.environment = environment;
        this.currentArrayContentIsNonNull = this.nextArrayContentIsNonNull = currentArrayContentIsNonNull;
        this.isEmpty = false;
    }

    NonNullDefaultAwareTypeAnnotationWalker(int defaultNullness, IBinaryAnnotation nonNullAnnotation, boolean atDefaultLocation, boolean atTypeBound, LookupEnvironment environment, boolean currentArrayContentIsNonNull) {
        super(null, 0L, 0);
        this.nonNullAnnotation = nonNullAnnotation;
        this.defaultNullness = defaultNullness;
        this.atDefaultLocation = atDefaultLocation;
        this.atTypeBound = atTypeBound;
        this.isEmpty = true;
        this.environment = environment;
        this.currentArrayContentIsNonNull = this.nextArrayContentIsNonNull = currentArrayContentIsNonNull;
    }

    private static IBinaryAnnotation getNonNullAnnotation(LookupEnvironment environment) {
        final char[] nonNullAnnotationName = CharOperation.concat('L', CharOperation.concatWith(environment.getNonNullAnnotationName(), '/'), ';');
        return new IBinaryAnnotation(){

            @Override
            public char[] getTypeName() {
                return nonNullAnnotationName;
            }

            @Override
            public IBinaryElementValuePair[] getElementValuePairs() {
                return null;
            }
        };
    }

    @Override
    protected TypeAnnotationWalker restrict(long newMatches, int newPathPtr) {
        try {
            if (this.matches == newMatches && this.pathPtr == newPathPtr && this.atDefaultLocation == this.nextIsDefaultLocation && this.atTypeBound == this.nextIsTypeBound && this.currentArrayContentIsNonNull == this.nextArrayContentIsNonNull) {
                NonNullDefaultAwareTypeAnnotationWalker nonNullDefaultAwareTypeAnnotationWalker = this;
                return nonNullDefaultAwareTypeAnnotationWalker;
            }
            if (newMatches == 0L || this.typeAnnotations == null || this.typeAnnotations.length == 0) {
                NonNullDefaultAwareTypeAnnotationWalker nonNullDefaultAwareTypeAnnotationWalker = new NonNullDefaultAwareTypeAnnotationWalker(this.defaultNullness, this.nonNullAnnotation, this.nextIsDefaultLocation, this.nextIsTypeBound, this.environment, this.nextArrayContentIsNonNull);
                return nonNullDefaultAwareTypeAnnotationWalker;
            }
            NonNullDefaultAwareTypeAnnotationWalker nonNullDefaultAwareTypeAnnotationWalker = new NonNullDefaultAwareTypeAnnotationWalker(this.typeAnnotations, newMatches, newPathPtr, this.defaultNullness, this.nonNullAnnotation, this.nextIsDefaultLocation, this.nextIsTypeBound, this.environment, this.nextArrayContentIsNonNull);
            return nonNullDefaultAwareTypeAnnotationWalker;
        }
        finally {
            this.nextIsDefaultLocation = false;
            this.nextIsTypeBound = false;
            this.nextArrayContentIsNonNull = this.currentArrayContentIsNonNull;
        }
    }

    @Override
    public ITypeAnnotationWalker toSupertype(short index, char[] superTypeSignature) {
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toSupertype(index, superTypeSignature);
    }

    @Override
    public ITypeAnnotationWalker toMethodParameter(short index) {
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toMethodParameter(index);
    }

    @Override
    public ITypeAnnotationWalker toField() {
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toField();
    }

    @Override
    public ITypeAnnotationWalker toMethodReturn() {
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toMethodReturn();
    }

    @Override
    public ITypeAnnotationWalker toTypeBound(short boundIndex) {
        this.nextIsDefaultLocation = (this.defaultNullness & 0x100) != 0;
        this.nextIsTypeBound = true;
        this.nextArrayContentIsNonNull = false;
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toTypeBound(boundIndex);
    }

    @Override
    public ITypeAnnotationWalker toWildcardBound() {
        this.nextIsDefaultLocation = (this.defaultNullness & 0x100) != 0;
        this.nextIsTypeBound = true;
        this.nextArrayContentIsNonNull = false;
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toWildcardBound();
    }

    @Override
    public ITypeAnnotationWalker toTypeParameterBounds(boolean isClassTypeParameter, int parameterRank) {
        this.nextIsDefaultLocation = (this.defaultNullness & 0x100) != 0;
        this.nextIsTypeBound = true;
        this.nextArrayContentIsNonNull = false;
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toTypeParameterBounds(isClassTypeParameter, parameterRank);
    }

    @Override
    public ITypeAnnotationWalker toTypeArgument(int rank) {
        this.nextIsDefaultLocation = (this.defaultNullness & 0x40) != 0;
        this.nextIsTypeBound = false;
        this.nextArrayContentIsNonNull = false;
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toTypeArgument(rank);
    }

    @Override
    public ITypeAnnotationWalker toTypeParameter(boolean isClassTypeParameter, int rank) {
        this.nextIsDefaultLocation = (this.defaultNullness & 0x80) != 0;
        this.nextIsTypeBound = false;
        this.nextArrayContentIsNonNull = false;
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toTypeParameter(isClassTypeParameter, rank);
    }

    @Override
    protected ITypeAnnotationWalker toNextDetail(int detailKind) {
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toNextDetail(detailKind);
    }

    @Override
    public IBinaryAnnotation[] getAnnotationsAtCursor(int currentTypeId, boolean mayApplyArrayContentsDefaultNullness) {
        IBinaryAnnotation[] normalAnnotations;
        IBinaryAnnotation[] iBinaryAnnotationArray = normalAnnotations = this.isEmpty ? NO_ANNOTATIONS : super.getAnnotationsAtCursor(currentTypeId, mayApplyArrayContentsDefaultNullness);
        if ((this.atDefaultLocation || mayApplyArrayContentsDefaultNullness && this.currentArrayContentIsNonNull) && currentTypeId != -1 && (!this.atTypeBound || currentTypeId != 1)) {
            if (normalAnnotations == null || normalAnnotations.length == 0) {
                return new IBinaryAnnotation[]{this.nonNullAnnotation};
            }
            if (this.environment.containsNullTypeAnnotation(normalAnnotations)) {
                return normalAnnotations;
            }
            int len = normalAnnotations.length;
            IBinaryAnnotation[] newAnnots = new IBinaryAnnotation[len + 1];
            System.arraycopy(normalAnnotations, 0, newAnnots, 0, len);
            newAnnots[len] = this.nonNullAnnotation;
            return newAnnots;
        }
        return normalAnnotations;
    }

    @Override
    public ITypeAnnotationWalker toNextArrayDimension() {
        boolean hasNNBDForArrayContents;
        boolean bl = hasNNBDForArrayContents = (this.defaultNullness & 0x200) != 0;
        if (hasNNBDForArrayContents) {
            this.nextArrayContentIsNonNull = true;
        }
        this.nextIsDefaultLocation = false;
        this.nextIsTypeBound = false;
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toNextArrayDimension();
    }

    public static ITypeAnnotationWalker updateWalkerForParamNonNullDefault(ITypeAnnotationWalker walker, int defaultNullness, LookupEnvironment environment) {
        if (environment.globalOptions.isAnnotationBasedNullAnalysisEnabled && defaultNullness != 0) {
            if (defaultNullness == 2) {
                if (walker instanceof NonNullDefaultAwareTypeAnnotationWalker) {
                    NonNullDefaultAwareTypeAnnotationWalker nonNullDefaultAwareTypeAnnotationWalker = (NonNullDefaultAwareTypeAnnotationWalker)walker;
                    return new TypeAnnotationWalker(nonNullDefaultAwareTypeAnnotationWalker.typeAnnotations, nonNullDefaultAwareTypeAnnotationWalker.matches, nonNullDefaultAwareTypeAnnotationWalker.pathPtr);
                }
                return walker;
            }
            if (walker instanceof TypeAnnotationWalker) {
                IBinaryAnnotation nonNullAnnotation2;
                TypeAnnotationWalker typeAnnotationWalker = (TypeAnnotationWalker)walker;
                if (walker instanceof NonNullDefaultAwareTypeAnnotationWalker) {
                    NonNullDefaultAwareTypeAnnotationWalker nonNullDefaultAwareTypeAnnotationWalker = (NonNullDefaultAwareTypeAnnotationWalker)walker;
                    if (nonNullDefaultAwareTypeAnnotationWalker.isEmpty) {
                        return new NonNullDefaultAwareTypeAnnotationWalker(defaultNullness, environment);
                    }
                    nonNullAnnotation2 = nonNullDefaultAwareTypeAnnotationWalker.nonNullAnnotation;
                } else {
                    nonNullAnnotation2 = NonNullDefaultAwareTypeAnnotationWalker.getNonNullAnnotation(environment);
                }
                return new NonNullDefaultAwareTypeAnnotationWalker(typeAnnotationWalker.typeAnnotations, typeAnnotationWalker.matches, typeAnnotationWalker.pathPtr, defaultNullness, nonNullAnnotation2, false, false, environment, false);
            }
            return new NonNullDefaultAwareTypeAnnotationWalker(defaultNullness, environment);
        }
        return walker;
    }
}

