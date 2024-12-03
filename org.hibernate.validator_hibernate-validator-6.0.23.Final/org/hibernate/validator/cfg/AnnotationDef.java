/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public abstract class AnnotationDef<C extends AnnotationDef<C, A>, A extends Annotation> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final AnnotationDescriptor.Builder<A> annotationDescriptorBuilder;
    private final Map<String, List<AnnotationDef<?, ?>>> annotationsAsParameters;
    private final Map<String, Class<?>> annotationsAsParametersTypes;

    protected AnnotationDef(Class<A> annotationType) {
        this.annotationDescriptorBuilder = new AnnotationDescriptor.Builder<Class<A>>(annotationType);
        this.annotationsAsParameters = new HashMap();
        this.annotationsAsParametersTypes = new HashMap();
    }

    protected AnnotationDef(AnnotationDef<?, A> original) {
        this.annotationDescriptorBuilder = original.annotationDescriptorBuilder;
        this.annotationsAsParameters = original.annotationsAsParameters;
        this.annotationsAsParametersTypes = original.annotationsAsParametersTypes;
    }

    private C getThis() {
        return (C)this;
    }

    protected C addParameter(String key, Object value) {
        this.annotationDescriptorBuilder.setAttribute(key, value);
        return this.getThis();
    }

    protected C addAnnotationAsParameter(String key, AnnotationDef<?, ?> value) {
        this.annotationsAsParameters.compute(key, (k, oldValue) -> {
            if (oldValue == null) {
                return Collections.singletonList(value);
            }
            ArrayList resultingList = CollectionHelper.newArrayList(oldValue);
            resultingList.add(value);
            return resultingList;
        });
        this.annotationsAsParametersTypes.putIfAbsent(key, value.annotationDescriptorBuilder.getType());
        return this.getThis();
    }

    private AnnotationDescriptor<A> createAnnotationDescriptor() {
        for (Map.Entry<String, List<AnnotationDef<?, ?>>> annotationAsParameter : this.annotationsAsParameters.entrySet()) {
            this.annotationDescriptorBuilder.setAttribute(annotationAsParameter.getKey(), this.toAnnotationParameterArray(annotationAsParameter.getValue(), this.annotationsAsParametersTypes.get(annotationAsParameter.getKey())));
        }
        try {
            return this.annotationDescriptorBuilder.build();
        }
        catch (RuntimeException e) {
            throw LOG.getUnableToCreateAnnotationForConfiguredConstraintException(e);
        }
    }

    private A createAnnotationProxy() {
        return this.createAnnotationDescriptor().getAnnotation();
    }

    private <T> T[] toAnnotationParameterArray(List<AnnotationDef<?, ?>> list, Class<T> aClass) {
        return list.stream().map(AnnotationDef::createAnnotationProxy).toArray(n -> (Object[])Array.newInstance(aClass, n));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append('{');
        sb.append(this.annotationDescriptorBuilder);
        sb.append('}');
        return sb.toString();
    }
}

