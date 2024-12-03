/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;

public final class ArrayType
extends ReferenceType {
    private final int dimensions;
    private final Type basicType;

    public ArrayType(byte type, int dimensions) {
        this(BasicType.getType(type), dimensions);
    }

    public ArrayType(String className, int dimensions) {
        this(ObjectType.getInstance(className), dimensions);
    }

    public ArrayType(Type type, int dimensions) {
        super((byte)13, "<dummy>");
        if (dimensions < 1 || dimensions > 255) {
            throw new ClassGenException("Invalid number of dimensions: " + dimensions);
        }
        switch (type.getType()) {
            case 13: {
                ArrayType array = (ArrayType)type;
                this.dimensions = dimensions + array.dimensions;
                this.basicType = array.basicType;
                break;
            }
            case 12: {
                throw new ClassGenException("Invalid type: void[]");
            }
            default: {
                this.dimensions = dimensions;
                this.basicType = type;
            }
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < this.dimensions; ++i) {
            buf.append('[');
        }
        buf.append(this.basicType.getSignature());
        super.setSignature(buf.toString());
    }

    @Override
    public boolean equals(Object type) {
        if (type instanceof ArrayType) {
            ArrayType array = (ArrayType)type;
            return array.dimensions == this.dimensions && array.basicType.equals(this.basicType);
        }
        return false;
    }

    public Type getBasicType() {
        return this.basicType;
    }

    @Override
    public String getClassName() {
        return this.signature;
    }

    public int getDimensions() {
        return this.dimensions;
    }

    public Type getElementType() {
        if (this.dimensions == 1) {
            return this.basicType;
        }
        return new ArrayType(this.basicType, this.dimensions - 1);
    }

    @Override
    public int hashCode() {
        return this.basicType.hashCode() ^ this.dimensions;
    }
}

