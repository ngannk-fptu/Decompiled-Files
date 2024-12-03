/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.type;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

public class StandardMethodMetadata
implements MethodMetadata {
    private final Method introspectedMethod;
    private final boolean nestedAnnotationsAsMap;

    public StandardMethodMetadata(Method introspectedMethod) {
        this(introspectedMethod, false);
    }

    public StandardMethodMetadata(Method introspectedMethod, boolean nestedAnnotationsAsMap) {
        Assert.notNull((Object)introspectedMethod, "Method must not be null");
        this.introspectedMethod = introspectedMethod;
        this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
    }

    public final Method getIntrospectedMethod() {
        return this.introspectedMethod;
    }

    @Override
    public String getMethodName() {
        return this.introspectedMethod.getName();
    }

    @Override
    public String getDeclaringClassName() {
        return this.introspectedMethod.getDeclaringClass().getName();
    }

    @Override
    public String getReturnTypeName() {
        return this.introspectedMethod.getReturnType().getName();
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this.introspectedMethod.getModifiers());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(this.introspectedMethod.getModifiers());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedMethod.getModifiers());
    }

    @Override
    public boolean isOverridable() {
        return !this.isStatic() && !this.isFinal() && !Modifier.isPrivate(this.introspectedMethod.getModifiers());
    }

    @Override
    public boolean isAnnotated(String annotationName) {
        return AnnotatedElementUtils.isAnnotated((AnnotatedElement)this.introspectedMethod, annotationName);
    }

    @Override
    @Nullable
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        return this.getAnnotationAttributes(annotationName, false);
    }

    @Override
    @Nullable
    public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        return AnnotatedElementUtils.getMergedAnnotationAttributes(this.introspectedMethod, annotationName, classValuesAsString, this.nestedAnnotationsAsMap);
    }

    @Override
    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
        return this.getAllAnnotationAttributes(annotationName, false);
    }

    @Override
    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        return AnnotatedElementUtils.getAllAnnotationAttributes(this.introspectedMethod, annotationName, classValuesAsString, this.nestedAnnotationsAsMap);
    }
}

