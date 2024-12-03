/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.generic.ClassGenException;
import org.aspectj.apache.bcel.generic.Type;

public final class BasicType
extends Type {
    BasicType(byte type) {
        super(type, Constants.SHORT_TYPE_NAMES[type]);
        if (type < 4 || type > 12) {
            throw new ClassGenException("Invalid type: " + type);
        }
    }

    public static final BasicType getType(byte type) {
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

    public boolean equals(Object type) {
        return type instanceof BasicType ? ((BasicType)type).type == this.type : false;
    }
}

