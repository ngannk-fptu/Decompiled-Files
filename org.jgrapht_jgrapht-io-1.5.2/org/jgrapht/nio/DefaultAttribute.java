/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.nio;

import java.io.Serializable;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;

public class DefaultAttribute<T>
implements Attribute,
Serializable {
    private static final long serialVersionUID = 366113727410278952L;
    public static final Attribute NULL = new DefaultAttribute<Object>(null, AttributeType.NULL);
    private T value;
    private AttributeType type;

    public DefaultAttribute(T value, AttributeType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String getValue() {
        return String.valueOf(this.value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public AttributeType getType() {
        return this.type;
    }

    public static Attribute createAttribute(Boolean value) {
        return new DefaultAttribute<Boolean>(value, AttributeType.BOOLEAN);
    }

    public static Attribute createAttribute(Integer value) {
        return new DefaultAttribute<Integer>(value, AttributeType.INT);
    }

    public static Attribute createAttribute(Long value) {
        return new DefaultAttribute<Long>(value, AttributeType.LONG);
    }

    public static Attribute createAttribute(Float value) {
        return new DefaultAttribute<Float>(value, AttributeType.FLOAT);
    }

    public static Attribute createAttribute(Double value) {
        return new DefaultAttribute<Double>(value, AttributeType.DOUBLE);
    }

    public static Attribute createAttribute(String value) {
        return new DefaultAttribute<String>(value, AttributeType.STRING);
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.type == null ? 0 : this.type.hashCode());
        result = 31 * result + (this.value == null ? 0 : this.value.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        DefaultAttribute other = (DefaultAttribute)obj;
        if (this.type != other.type) {
            return false;
        }
        return !(this.value == null ? other.value != null : !this.value.equals(other.value));
    }
}

