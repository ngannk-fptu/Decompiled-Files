/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResultObject;
import java.lang.reflect.Field;

public class AnnotationEnumValue
extends ScanResultObject
implements Comparable<AnnotationEnumValue> {
    private String className;
    private String valueName;

    AnnotationEnumValue() {
    }

    AnnotationEnumValue(String className, String constValueName) {
        this.className = className;
        this.valueName = constValueName;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    public String getValueName() {
        return this.valueName;
    }

    public String getName() {
        return this.className + "." + this.valueName;
    }

    public Object loadClassAndReturnEnumValue(boolean ignoreExceptions) throws IllegalArgumentException {
        Field field;
        Class<?> classRef = super.loadClass(ignoreExceptions);
        if (classRef == null) {
            if (ignoreExceptions) {
                return null;
            }
            throw new IllegalArgumentException("Enum class " + this.className + " could not be loaded");
        }
        if (!classRef.isEnum()) {
            throw new IllegalArgumentException("Class " + this.className + " is not an enum");
        }
        try {
            field = classRef.getDeclaredField(this.valueName);
        }
        catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalArgumentException("Could not find enum constant " + this, e);
        }
        if (!field.isEnumConstant()) {
            throw new IllegalArgumentException("Field " + this + " is not an enum constant");
        }
        try {
            return field.get(null);
        }
        catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalArgumentException("Field " + this + " is not accessible", e);
        }
    }

    public Object loadClassAndReturnEnumValue() throws IllegalArgumentException {
        return this.loadClassAndReturnEnumValue(false);
    }

    @Override
    public int compareTo(AnnotationEnumValue o) {
        int diff = this.className.compareTo(o.className);
        return diff == 0 ? this.valueName.compareTo(o.valueName) : diff;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AnnotationEnumValue)) {
            return false;
        }
        return this.compareTo((AnnotationEnumValue)obj) == 0;
    }

    public int hashCode() {
        return this.className.hashCode() * 11 + this.valueName.hashCode();
    }

    @Override
    protected void toString(boolean useSimpleNames, StringBuilder buf) {
        buf.append(useSimpleNames ? ClassInfo.getSimpleName(this.className) : this.className);
        buf.append('.');
        buf.append(this.valueName);
    }
}

