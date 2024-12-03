/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.impl.Constant;

public class BooleanConstant
extends Constant {
    private boolean value;
    private static final BooleanConstant TRUE = new BooleanConstant(true);
    private static final BooleanConstant FALSE = new BooleanConstant(false);

    public static Constant fromValue(boolean value) {
        return value ? TRUE : FALSE;
    }

    private BooleanConstant(boolean value) {
        this.value = value;
    }

    @Override
    public boolean booleanValue() {
        return this.value;
    }

    @Override
    public String stringValue() {
        return String.valueOf(this.value);
    }

    @Override
    public String toString() {
        return "(boolean)" + this.value;
    }

    @Override
    public int typeID() {
        return 5;
    }

    public int hashCode() {
        return this.value ? 1231 : 1237;
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
        return false;
    }
}

