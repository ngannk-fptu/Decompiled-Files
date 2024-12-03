/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.AnnotatedValue;
import com.atlassian.velocity.htmlsafe.introspection.AnnotatedValueHelper;
import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxedElement;
import com.atlassian.velocity.htmlsafe.introspection.BoxedValue;
import com.atlassian.velocity.htmlsafe.introspection.BoxingUtils;
import com.atlassian.velocity.htmlsafe.introspection.ObjectClassResolver;
import com.google.common.base.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

class AnnotatedValueHelperFactory {
    AnnotatedValueHelperFactory() {
    }

    static AnnotatedValueHelper getValueHelper(Object object, ObjectClassResolver classResolver) {
        Preconditions.checkNotNull((Object)classResolver, (Object)"classResolver must not be null");
        if (object instanceof BoxedValue) {
            return new DefaultAnnotatedValueHelper(object, classResolver);
        }
        return new NonAnnotatedValueHelper(object, classResolver);
    }

    static final class DefaultAnnotatedValueHelper
    implements AnnotatedValueHelper {
        private final Object originalObject;
        private final Object targetObject;
        private final ObjectClassResolver classResolver;
        private final boolean boxedValue;

        DefaultAnnotatedValueHelper(Object targetObject, ObjectClassResolver classResolver) {
            this.originalObject = targetObject;
            this.targetObject = BoxingUtils.unboxObject(targetObject);
            this.classResolver = classResolver;
            this.boxedValue = this.originalObject instanceof BoxedValue;
        }

        @Override
        public Object get() {
            return this.originalObject;
        }

        public Object unbox() {
            return this.targetObject;
        }

        @Override
        public Collection<Annotation> getAnnotations() {
            if (this.originalObject instanceof AnnotatedElement) {
                return Arrays.asList(((AnnotatedElement)this.originalObject).getAnnotations());
            }
            return Collections.emptyList();
        }

        @Override
        public AnnotationBoxedElement getBoxedValueWithInheritedAnnotations() {
            if (!(this.originalObject instanceof AnnotatedValue)) {
                return null;
            }
            AnnotatedValue annotatedValue = (AnnotatedValue)this.originalObject;
            Collection<Annotation> inheritableAnnotations = annotatedValue.getCollectionInheritableAnnotations();
            return new AnnotatedValue<Object>(this.targetObject, inheritableAnnotations);
        }

        @Override
        public Class getTargetClass() {
            return this.classResolver.resolveClass(this.targetObject);
        }

        @Override
        public boolean isBoxedValue() {
            return this.boxedValue;
        }
    }

    static final class NonAnnotatedValueHelper
    implements AnnotatedValueHelper {
        private final Object targetObject;
        private final ObjectClassResolver classResolver;

        NonAnnotatedValueHelper(Object targetObject, ObjectClassResolver classResolver) {
            this.targetObject = targetObject;
            this.classResolver = classResolver;
        }

        @Override
        public Object get() {
            return this.targetObject;
        }

        @Override
        public Collection<Annotation> getAnnotations() {
            return Collections.emptyList();
        }

        @Override
        public AnnotationBoxedElement getBoxedValueWithInheritedAnnotations() {
            return null;
        }

        @Override
        public Class getTargetClass() {
            return this.classResolver.resolveClass(this.targetObject);
        }

        public Object unbox() {
            return this.targetObject;
        }

        @Override
        public boolean isBoxedValue() {
            return false;
        }
    }
}

