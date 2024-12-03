/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

public class FlexInteger {
    private Integer value;

    public FlexInteger(String value) {
        this.value = Integer.decode(value);
    }

    public int intValue() {
        return this.value;
    }

    public String toString() {
        return this.value.toString();
    }
}

