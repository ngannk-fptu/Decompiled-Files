/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.reflection;

public class FieldKey {
    private final String fieldName;
    private final Class declaringClass;
    private final int depth;
    private final int order;

    public FieldKey(String fieldName, Class declaringClass, int order) {
        if (fieldName == null || declaringClass == null) {
            throw new IllegalArgumentException("fieldName or declaringClass is null");
        }
        this.fieldName = fieldName;
        this.declaringClass = declaringClass;
        this.order = order;
        Class c = declaringClass;
        int i = 0;
        while (c.getSuperclass() != null) {
            ++i;
            c = c.getSuperclass();
        }
        this.depth = i;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public Class getDeclaringClass() {
        return this.declaringClass;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getOrder() {
        return this.order;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FieldKey)) {
            return false;
        }
        FieldKey fieldKey = (FieldKey)o;
        if (!this.declaringClass.equals(fieldKey.declaringClass)) {
            return false;
        }
        return this.fieldName.equals(fieldKey.fieldName);
    }

    public int hashCode() {
        int result = this.fieldName.hashCode();
        result = 29 * result + this.declaringClass.hashCode();
        return result;
    }

    public String toString() {
        return "FieldKey{order=" + this.order + ", writer=" + this.depth + ", declaringClass=" + this.declaringClass + ", fieldName='" + this.fieldName + "'}";
    }
}

