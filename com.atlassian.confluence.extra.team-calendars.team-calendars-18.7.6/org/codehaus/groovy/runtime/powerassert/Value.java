/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.powerassert;

public class Value {
    private final Object value;
    private final int column;

    public Value(Object value, int column) {
        this.value = value;
        this.column = column;
    }

    public Object getValue() {
        return this.value;
    }

    public int getColumn() {
        return this.column;
    }
}

