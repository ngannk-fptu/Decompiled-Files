/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;

public interface ITypeAnnotationWalker {
    public static final IBinaryAnnotation[] NO_ANNOTATIONS = new IBinaryAnnotation[0];
    public static final ITypeAnnotationWalker EMPTY_ANNOTATION_WALKER = new ITypeAnnotationWalker(){

        @Override
        public ITypeAnnotationWalker toField() {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toThrows(int rank) {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toTypeArgument(int rank) {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toMethodParameter(short index) {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toSupertype(short index, char[] superTypeSignature) {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toTypeParameterBounds(boolean isClassTypeParameter, int parameterRank) {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toTypeBound(short boundIndex) {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toTypeParameter(boolean isClassTypeParameter, int rank) {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toMethodReturn() {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toReceiver() {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toWildcardBound() {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toNextArrayDimension() {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toNextNestedType() {
            return this;
        }

        @Override
        public IBinaryAnnotation[] getAnnotationsAtCursor(int currentTypeId, boolean mayApplyArrayContentsDefaultNullness) {
            return NO_ANNOTATIONS;
        }
    };

    public ITypeAnnotationWalker toField();

    public ITypeAnnotationWalker toMethodReturn();

    public ITypeAnnotationWalker toReceiver();

    public ITypeAnnotationWalker toTypeParameter(boolean var1, int var2);

    public ITypeAnnotationWalker toTypeParameterBounds(boolean var1, int var2);

    public ITypeAnnotationWalker toTypeBound(short var1);

    public ITypeAnnotationWalker toSupertype(short var1, char[] var2);

    public ITypeAnnotationWalker toMethodParameter(short var1);

    public ITypeAnnotationWalker toThrows(int var1);

    public ITypeAnnotationWalker toTypeArgument(int var1);

    public ITypeAnnotationWalker toWildcardBound();

    public ITypeAnnotationWalker toNextArrayDimension();

    public ITypeAnnotationWalker toNextNestedType();

    public IBinaryAnnotation[] getAnnotationsAtCursor(int var1, boolean var2);
}

