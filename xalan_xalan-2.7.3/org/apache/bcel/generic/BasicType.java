/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.Const;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.Type;

public final class BasicType
extends Type {
    public static BasicType getType(byte type) {
        switch (type) {
            case 12: {
                return VOID;
            }
            case 4: {
                return BOOLEAN;
            }
            case 8: {
                return BYTE;
            }
            case 9: {
                return SHORT;
            }
            case 5: {
                return CHAR;
            }
            case 10: {
                return INT;
            }
            case 11: {
                return LONG;
            }
            case 7: {
                return DOUBLE;
            }
            case 6: {
                return FLOAT;
            }
        }
        throw new ClassGenException("Invalid type: " + type);
    }

    BasicType(byte type) {
        super(type, Const.getShortTypeName(type));
        if (type < 4 || type > 12) {
            throw new ClassGenException("Invalid type: " + type);
        }
    }

    @Override
    public boolean equals(Object type) {
        return type instanceof BasicType && ((BasicType)type).getType() == this.getType();
    }

    @Override
    public int hashCode() {
        return super.getType();
    }
}

