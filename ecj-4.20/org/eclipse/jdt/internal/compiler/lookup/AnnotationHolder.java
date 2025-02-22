/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding;

public class AnnotationHolder {
    AnnotationBinding[] annotations;

    static AnnotationHolder storeAnnotations(AnnotationBinding[] annotations, AnnotationBinding[][] parameterAnnotations, Object defaultValue, LookupEnvironment optionalEnv) {
        if (parameterAnnotations != null) {
            boolean isEmpty = true;
            int i = parameterAnnotations.length;
            while (isEmpty && --i >= 0) {
                if (parameterAnnotations[i] == null || parameterAnnotations[i].length <= 0) continue;
                isEmpty = false;
            }
            if (isEmpty) {
                parameterAnnotations = null;
            }
        }
        if (defaultValue != null) {
            return new AnnotationMethodHolder(annotations, parameterAnnotations, defaultValue, optionalEnv);
        }
        if (parameterAnnotations != null) {
            return new MethodHolder(annotations, parameterAnnotations);
        }
        return new AnnotationHolder().setAnnotations(annotations);
    }

    AnnotationBinding[] getAnnotations() {
        return this.annotations;
    }

    Object getDefaultValue() {
        return null;
    }

    public AnnotationBinding[][] getParameterAnnotations() {
        return null;
    }

    AnnotationBinding[] getParameterAnnotations(int paramIndex) {
        return Binding.NO_ANNOTATIONS;
    }

    AnnotationHolder setAnnotations(AnnotationBinding[] annotations) {
        this.annotations = annotations;
        if (annotations == null || annotations.length == 0) {
            return null;
        }
        return this;
    }

    static class AnnotationMethodHolder
    extends MethodHolder {
        Object defaultValue;
        LookupEnvironment env;

        AnnotationMethodHolder(AnnotationBinding[] annotations, AnnotationBinding[][] parameterAnnotations, Object defaultValue, LookupEnvironment optionalEnv) {
            super(annotations, parameterAnnotations);
            this.defaultValue = defaultValue;
            this.env = optionalEnv;
        }

        @Override
        Object getDefaultValue() {
            if (this.defaultValue instanceof UnresolvedReferenceBinding) {
                if (this.env == null) {
                    throw new IllegalStateException();
                }
                this.defaultValue = ((UnresolvedReferenceBinding)this.defaultValue).resolve(this.env, false);
            }
            return this.defaultValue;
        }
    }

    static class MethodHolder
    extends AnnotationHolder {
        AnnotationBinding[][] parameterAnnotations;

        MethodHolder(AnnotationBinding[] annotations, AnnotationBinding[][] parameterAnnotations) {
            this.setAnnotations(annotations);
            this.parameterAnnotations = parameterAnnotations;
        }

        @Override
        public AnnotationBinding[][] getParameterAnnotations() {
            return this.parameterAnnotations;
        }

        @Override
        AnnotationBinding[] getParameterAnnotations(int paramIndex) {
            AnnotationBinding[] result = this.parameterAnnotations == null ? null : this.parameterAnnotations[paramIndex];
            return result == null ? Binding.NO_ANNOTATIONS : result;
        }

        @Override
        AnnotationHolder setAnnotations(AnnotationBinding[] annotations) {
            this.annotations = annotations == null || annotations.length == 0 ? Binding.NO_ANNOTATIONS : annotations;
            return this;
        }
    }
}

