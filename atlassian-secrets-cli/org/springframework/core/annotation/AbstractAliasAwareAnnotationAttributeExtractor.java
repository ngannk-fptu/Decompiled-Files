/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.springframework.core.annotation.AnnotationAttributeExtractor;
import org.springframework.core.annotation.AnnotationConfigurationException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

abstract class AbstractAliasAwareAnnotationAttributeExtractor<S>
implements AnnotationAttributeExtractor<S> {
    private final Class<? extends Annotation> annotationType;
    @Nullable
    private final Object annotatedElement;
    private final S source;
    private final Map<String, List<String>> attributeAliasMap;

    AbstractAliasAwareAnnotationAttributeExtractor(Class<? extends Annotation> annotationType, @Nullable Object annotatedElement, S source) {
        Assert.notNull(annotationType, "annotationType must not be null");
        Assert.notNull(source, "source must not be null");
        this.annotationType = annotationType;
        this.annotatedElement = annotatedElement;
        this.source = source;
        this.attributeAliasMap = AnnotationUtils.getAttributeAliasMap(annotationType);
    }

    @Override
    public final Class<? extends Annotation> getAnnotationType() {
        return this.annotationType;
    }

    @Override
    @Nullable
    public final Object getAnnotatedElement() {
        return this.annotatedElement;
    }

    @Override
    public final S getSource() {
        return this.source;
    }

    @Override
    @Nullable
    public final Object getAttributeValue(Method attributeMethod) {
        String attributeName = attributeMethod.getName();
        Object attributeValue = this.getRawAttributeValue(attributeMethod);
        List<String> aliasNames = this.attributeAliasMap.get(attributeName);
        if (aliasNames != null) {
            Object defaultValue = AnnotationUtils.getDefaultValue(this.annotationType, attributeName);
            for (String aliasName : aliasNames) {
                Object aliasValue = this.getRawAttributeValue(aliasName);
                if (!(ObjectUtils.nullSafeEquals(attributeValue, aliasValue) || ObjectUtils.nullSafeEquals(attributeValue, defaultValue) || ObjectUtils.nullSafeEquals(aliasValue, defaultValue))) {
                    String elementName = this.annotatedElement != null ? this.annotatedElement.toString() : "unknown element";
                    throw new AnnotationConfigurationException(String.format("In annotation [%s] declared on %s and synthesized from [%s], attribute '%s' and its alias '%s' are present with values of [%s] and [%s], but only one is permitted.", this.annotationType.getName(), elementName, this.source, attributeName, aliasName, ObjectUtils.nullSafeToString(attributeValue), ObjectUtils.nullSafeToString(aliasValue)));
                }
                if (!ObjectUtils.nullSafeEquals(attributeValue, defaultValue)) continue;
                attributeValue = aliasValue;
            }
        }
        return attributeValue;
    }

    @Nullable
    protected abstract Object getRawAttributeValue(Method var1);

    @Nullable
    protected abstract Object getRawAttributeValue(String var1);
}

