/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate;

import com.fasterxml.classmate.util.ClassKey;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AnnotationOverrides
implements Serializable {
    public List<Class<?>> mixInsFor(Class<?> beanClass) {
        return this.mixInsFor(new ClassKey(beanClass));
    }

    public abstract List<Class<?>> mixInsFor(ClassKey var1);

    public static StdBuilder builder() {
        return new StdBuilder();
    }

    public static class StdImpl
    extends AnnotationOverrides {
        protected final HashMap<ClassKey, List<Class<?>>> _targetsToOverrides;

        public StdImpl(HashMap<ClassKey, List<Class<?>>> overrides) {
            this._targetsToOverrides = new HashMap(overrides);
        }

        @Override
        public List<Class<?>> mixInsFor(ClassKey target) {
            return this._targetsToOverrides.get(target);
        }
    }

    public static class StdBuilder {
        protected final HashMap<ClassKey, List<Class<?>>> _targetsToOverrides = new HashMap();

        public StdBuilder add(Class<?> target, Class<?> mixin) {
            return this.add(new ClassKey(target), mixin);
        }

        public StdBuilder add(ClassKey target, Class<?> mixin) {
            List<Class<?>> mixins = this._targetsToOverrides.get(target);
            if (mixins == null) {
                mixins = new ArrayList();
                this._targetsToOverrides.put(target, mixins);
            }
            mixins.add(mixin);
            return this;
        }

        public AnnotationOverrides build() {
            return new StdImpl(this._targetsToOverrides);
        }
    }
}

