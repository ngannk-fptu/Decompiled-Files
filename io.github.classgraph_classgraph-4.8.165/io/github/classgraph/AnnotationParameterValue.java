/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.HasName;
import io.github.classgraph.ObjectTypedValueWrapper;
import io.github.classgraph.ScanResult;
import io.github.classgraph.ScanResultObject;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import nonapi.io.github.classgraph.utils.LogNode;

public class AnnotationParameterValue
extends ScanResultObject
implements HasName,
Comparable<AnnotationParameterValue> {
    private String name;
    private ObjectTypedValueWrapper value;

    AnnotationParameterValue() {
    }

    AnnotationParameterValue(String name, Object value) {
        this.name = name;
        this.value = new ObjectTypedValueWrapper(value);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value == null ? null : this.value.get();
    }

    void setValue(Object newValue) {
        this.value = new ObjectTypedValueWrapper(newValue);
    }

    @Override
    protected String getClassName() {
        throw new IllegalArgumentException("getClassName() cannot be called here");
    }

    @Override
    protected ClassInfo getClassInfo() {
        throw new IllegalArgumentException("getClassInfo() cannot be called here");
    }

    @Override
    void setScanResult(ScanResult scanResult) {
        super.setScanResult(scanResult);
        if (this.value != null) {
            this.value.setScanResult(scanResult);
        }
    }

    @Override
    protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo, LogNode log) {
        if (this.value != null) {
            this.value.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
        }
    }

    void convertWrapperArraysToPrimitiveArrays(ClassInfo annotationClassInfo) {
        if (this.value != null) {
            this.value.convertWrapperArraysToPrimitiveArrays(annotationClassInfo, this.name);
        }
    }

    Object instantiate(ClassInfo annotationClassInfo) {
        return this.value.instantiateOrGet(annotationClassInfo, this.name);
    }

    @Override
    public int compareTo(AnnotationParameterValue other) {
        if (other == this) {
            return 0;
        }
        int diff = this.name.compareTo(other.getName());
        if (diff != 0) {
            return diff;
        }
        if (this.value.equals(other.value)) {
            return 0;
        }
        Object p0 = this.getValue();
        Object p1 = other.getValue();
        return p0 == null || p1 == null ? (p0 == null ? 0 : 1) - (p1 == null ? 0 : 1) : this.toStringParamValueOnly().compareTo(other.toStringParamValueOnly());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AnnotationParameterValue)) {
            return false;
        }
        AnnotationParameterValue other = (AnnotationParameterValue)obj;
        return this.name.equals(other.name) && this.value == null == (other.value == null) && (this.value == null || this.value.equals(other.value));
    }

    public int hashCode() {
        return Objects.hash(this.name, this.value);
    }

    @Override
    protected void toString(boolean useSimpleNames, StringBuilder buf) {
        buf.append(this.name);
        buf.append("=");
        this.toStringParamValueOnly(useSimpleNames, buf);
    }

    private static void toString(Object val, boolean useSimpleNames, StringBuilder buf) {
        if (val == null) {
            buf.append("null");
        } else if (val instanceof ScanResultObject) {
            ((ScanResultObject)val).toString(useSimpleNames, buf);
        } else {
            buf.append(val);
        }
    }

    void toStringParamValueOnly(boolean useSimpleNames, StringBuilder buf) {
        if (this.value == null) {
            buf.append("null");
        } else {
            Object paramVal = this.value.get();
            Class<?> valClass = paramVal.getClass();
            if (valClass.isArray()) {
                buf.append('{');
                int n = Array.getLength(paramVal);
                for (int j = 0; j < n; ++j) {
                    if (j > 0) {
                        buf.append(", ");
                    }
                    Object elt = Array.get(paramVal, j);
                    AnnotationParameterValue.toString(elt, useSimpleNames, buf);
                }
                buf.append('}');
            } else if (paramVal instanceof String) {
                buf.append('\"');
                buf.append(paramVal.toString().replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r"));
                buf.append('\"');
            } else if (paramVal instanceof Character) {
                buf.append('\'');
                buf.append(paramVal.toString().replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r"));
                buf.append('\'');
            } else {
                AnnotationParameterValue.toString(paramVal, useSimpleNames, buf);
            }
        }
    }

    private String toStringParamValueOnly() {
        StringBuilder buf = new StringBuilder();
        this.toStringParamValueOnly(false, buf);
        return buf.toString();
    }
}

