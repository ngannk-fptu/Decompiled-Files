/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.annotationfactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public final class AnnotationDescriptor {
    private final Class<? extends Annotation> type;
    private Map<String, Object> elements;

    public AnnotationDescriptor(Class<? extends Annotation> annotationType) {
        this.type = annotationType;
    }

    public void setValue(String elementName, Object value) {
        if (this.elements == null) {
            this.elements = new HashMap<String, Object>(4);
        }
        this.elements.put(elementName, value);
    }

    public Object valueOf(String elementName) {
        return this.elements == null ? null : this.elements.get(elementName);
    }

    public boolean containsElement(String elementName) {
        return this.elements == null ? false : this.elements.containsKey(elementName);
    }

    public int numberOfElements() {
        return this.elements == null ? 0 : this.elements.size();
    }

    public Class<? extends Annotation> type() {
        return this.type;
    }
}

