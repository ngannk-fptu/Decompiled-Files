/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate;

import com.fasterxml.classmate.AnnotationInclusion;
import com.fasterxml.classmate.util.ClassKey;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;

public abstract class AnnotationConfiguration
implements Serializable {
    public abstract AnnotationInclusion getInclusionForClass(Class<? extends Annotation> var1);

    public abstract AnnotationInclusion getInclusionForConstructor(Class<? extends Annotation> var1);

    public abstract AnnotationInclusion getInclusionForField(Class<? extends Annotation> var1);

    public abstract AnnotationInclusion getInclusionForMethod(Class<? extends Annotation> var1);

    public abstract AnnotationInclusion getInclusionForParameter(Class<? extends Annotation> var1);

    public static class StdConfiguration
    extends AnnotationConfiguration
    implements Serializable {
        protected final AnnotationInclusion _defaultInclusion;
        protected final HashMap<ClassKey, AnnotationInclusion> _inclusions = new HashMap();

        public StdConfiguration(AnnotationInclusion defaultBehavior) {
            this._defaultInclusion = defaultBehavior;
        }

        @Override
        public AnnotationInclusion getInclusionForClass(Class<? extends Annotation> annotationType) {
            return this._inclusionFor(annotationType);
        }

        @Override
        public AnnotationInclusion getInclusionForConstructor(Class<? extends Annotation> annotationType) {
            return this._inclusionFor(annotationType);
        }

        @Override
        public AnnotationInclusion getInclusionForField(Class<? extends Annotation> annotationType) {
            return this.getInclusionForClass(annotationType);
        }

        @Override
        public AnnotationInclusion getInclusionForMethod(Class<? extends Annotation> annotationType) {
            return this.getInclusionForClass(annotationType);
        }

        @Override
        public AnnotationInclusion getInclusionForParameter(Class<? extends Annotation> annotationType) {
            return this.getInclusionForClass(annotationType);
        }

        public void setInclusion(Class<? extends Annotation> annotationType, AnnotationInclusion incl) {
            this._inclusions.put(new ClassKey(annotationType), incl);
        }

        protected AnnotationInclusion _inclusionFor(Class<? extends Annotation> annotationType) {
            ClassKey key = new ClassKey(annotationType);
            AnnotationInclusion beh = this._inclusions.get(key);
            return beh == null ? this._defaultInclusion : beh;
        }
    }
}

