/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.impl.Constant;

public class StringConstant
extends Constant {
    private String value;

    public static Constant fromValue(String value) {
        return new StringConstant(value);
    }

    private StringConstant(String value) {
        this.value = value;
    }

    @Override
    public String stringValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "(String)\"" + this.value + "\"";
    }

    @Override
    public int typeID() {
        return 11;
    }

    public int hashCode() {
        int result = 1;
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
        StringConstant other = (StringConstant)obj;
        if (this.value == null) {
            return other.value == null;
        }
        return this.value.equals(other.value);
    }
}

