/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;

public class TypeAnnotationWalker
implements ITypeAnnotationWalker {
    protected final IBinaryTypeAnnotation[] typeAnnotations;
    protected final long matches;
    protected final int pathPtr;

    public TypeAnnotationWalker(IBinaryTypeAnnotation[] typeAnnotations) {
        this(typeAnnotations, -1L >>> 64 - typeAnnotations.length);
    }

    TypeAnnotationWalker(IBinaryTypeAnnotation[] typeAnnotations, long matchBits) {
        this(typeAnnotations, matchBits, 0);
    }

    protected TypeAnnotationWalker(IBinaryTypeAnnotation[] typeAnnotations, long matchBits, int pathPtr) {
        this.typeAnnotations = typeAnnotations;
        this.matches = matchBits;
        this.pathPtr = pathPtr;
    }

    protected ITypeAnnotationWalker restrict(long newMatches, int newPathPtr) {
        if (this.matches == newMatches && this.pathPtr == newPathPtr) {
            return this;
        }
        if (newMatches == 0L || this.typeAnnotations == null || this.typeAnnotations.length == 0) {
            return EMPTY_ANNOTATION_WALKER;
        }
        return new TypeAnnotationWalker(this.typeAnnotations, newMatches, newPathPtr);
    }

    @Override
    public ITypeAnnotationWalker toField() {
        return this.toTarget(19);
    }

    @Override
    public ITypeAnnotationWalker toMethodReturn() {
        return this.toTarget(20);
    }

    @Override
    public ITypeAnnotationWalker toReceiver() {
        return this.toTarget(21);
    }

    protected ITypeAnnotationWalker toTarget(int targetType) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return EMPTY_ANNOTATION_WALKER;
        }
        int length = this.typeAnnotations.length;
        long mask = 1L;
        int i = 0;
        while (i < length) {
            if (this.typeAnnotations[i].getTargetType() != targetType) {
                newMatches &= mask ^ 0xFFFFFFFFFFFFFFFFL;
            }
            ++i;
            mask <<= 1;
        }
        return this.restrict(newMatches, 0);
    }

    @Override
    public ITypeAnnotationWalker toTypeParameter(boolean isClassTypeParameter, int rank) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return EMPTY_ANNOTATION_WALKER;
        }
        int targetType = isClassTypeParameter ? 0 : 1;
        int length = this.typeAnnotations.length;
        long mask = 1L;
        int i = 0;
        while (i < length) {
            IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            if (candidate.getTargetType() != targetType || candidate.getTypeParameterIndex() != rank) {
                newMatches &= mask ^ 0xFFFFFFFFFFFFFFFFL;
            }
            ++i;
            mask <<= 1;
        }
        return this.restrict(newMatches, 0);
    }

    @Override
    public ITypeAnnotationWalker toTypeParameterBounds(boolean isClassTypeParameter, int parameterRank) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return EMPTY_ANNOTATION_WALKER;
        }
        int length = this.typeAnnotations.length;
        int targetType = isClassTypeParameter ? 17 : 18;
        long mask = 1L;
        int i = 0;
        while (i < length) {
            IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            if (candidate.getTargetType() != targetType || (short)candidate.getTypeParameterIndex() != parameterRank) {
                newMatches &= mask ^ 0xFFFFFFFFFFFFFFFFL;
            }
            ++i;
            mask <<= 1;
        }
        return this.restrict(newMatches, 0);
    }

    @Override
    public ITypeAnnotationWalker toTypeBound(short boundIndex) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return EMPTY_ANNOTATION_WALKER;
        }
        int length = this.typeAnnotations.length;
        long mask = 1L;
        int i = 0;
        while (i < length) {
            IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            if ((short)candidate.getBoundIndex() != boundIndex) {
                newMatches &= mask ^ 0xFFFFFFFFFFFFFFFFL;
            }
            ++i;
            mask <<= 1;
        }
        return this.restrict(newMatches, 0);
    }

    @Override
    public ITypeAnnotationWalker toSupertype(short index, char[] superTypeSignature) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return EMPTY_ANNOTATION_WALKER;
        }
        int length = this.typeAnnotations.length;
        long mask = 1L;
        int i = 0;
        while (i < length) {
            IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            if (candidate.getTargetType() != 16 || (short)candidate.getSupertypeIndex() != index) {
                newMatches &= mask ^ 0xFFFFFFFFFFFFFFFFL;
            }
            ++i;
            mask <<= 1;
        }
        return this.restrict(newMatches, 0);
    }

    @Override
    public ITypeAnnotationWalker toMethodParameter(short index) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return EMPTY_ANNOTATION_WALKER;
        }
        int length = this.typeAnnotations.length;
        long mask = 1L;
        int i = 0;
        while (i < length) {
            IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            if (candidate.getTargetType() != 22 || (short)candidate.getMethodFormalParameterIndex() != index) {
                newMatches &= mask ^ 0xFFFFFFFFFFFFFFFFL;
            }
            ++i;
            mask <<= 1;
        }
        return this.restrict(newMatches, 0);
    }

    @Override
    public ITypeAnnotationWalker toThrows(int index) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return EMPTY_ANNOTATION_WALKER;
        }
        int length = this.typeAnnotations.length;
        long mask = 1L;
        int i = 0;
        while (i < length) {
            IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            if (candidate.getTargetType() != 23 || candidate.getThrowsTypeIndex() != index) {
                newMatches &= mask ^ 0xFFFFFFFFFFFFFFFFL;
            }
            ++i;
            mask <<= 1;
        }
        return this.restrict(newMatches, 0);
    }

    @Override
    public ITypeAnnotationWalker toTypeArgument(int rank) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return EMPTY_ANNOTATION_WALKER;
        }
        int length = this.typeAnnotations.length;
        long mask = 1L;
        int i = 0;
        while (i < length) {
            IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            int[] path = candidate.getTypePath();
            if (this.pathPtr >= path.length || path[this.pathPtr] != 3 || path[this.pathPtr + 1] != rank) {
                newMatches &= mask ^ 0xFFFFFFFFFFFFFFFFL;
            }
            ++i;
            mask <<= 1;
        }
        return this.restrict(newMatches, this.pathPtr + 2);
    }

    @Override
    public ITypeAnnotationWalker toWildcardBound() {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return EMPTY_ANNOTATION_WALKER;
        }
        int length = this.typeAnnotations.length;
        long mask = 1L;
        int i = 0;
        while (i < length) {
            IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            int[] path = candidate.getTypePath();
            if (this.pathPtr >= path.length || path[this.pathPtr] != 2) {
                newMatches &= mask ^ 0xFFFFFFFFFFFFFFFFL;
            }
            ++i;
            mask <<= 1;
        }
        return this.restrict(newMatches, this.pathPtr + 2);
    }

    @Override
    public ITypeAnnotationWalker toNextArrayDimension() {
        return this.toNextDetail(0);
    }

    @Override
    public ITypeAnnotationWalker toNextNestedType() {
        return this.toNextDetail(1);
    }

    protected ITypeAnnotationWalker toNextDetail(int detailKind) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return this.restrict(newMatches, this.pathPtr + 2);
        }
        int length = this.typeAnnotations.length;
        long mask = 1L;
        int i = 0;
        while (i < length) {
            IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            int[] path = candidate.getTypePath();
            if (this.pathPtr >= path.length || path[this.pathPtr] != detailKind) {
                newMatches &= mask ^ 0xFFFFFFFFFFFFFFFFL;
            }
            ++i;
            mask <<= 1;
        }
        return this.restrict(newMatches, this.pathPtr + 2);
    }

    @Override
    public IBinaryAnnotation[] getAnnotationsAtCursor(int currentTypeId, boolean mayApplyArrayContentsDefaultNullness) {
        int length = this.typeAnnotations.length;
        IBinaryAnnotation[] filtered = new IBinaryAnnotation[length];
        long ptr = 1L;
        int count = 0;
        int i = 0;
        while (i < length) {
            IBinaryTypeAnnotation candidate;
            if ((this.matches & ptr) != 0L && (candidate = this.typeAnnotations[i]).getTypePath().length <= this.pathPtr) {
                filtered[count++] = candidate.getAnnotation();
            }
            ++i;
            ptr <<= 1;
        }
        if (count == 0) {
            return NO_ANNOTATIONS;
        }
        if (count < length) {
            IBinaryAnnotation[] iBinaryAnnotationArray = filtered;
            filtered = new IBinaryAnnotation[count];
            System.arraycopy(iBinaryAnnotationArray, 0, filtered, 0, count);
        }
        return filtered;
    }
}

