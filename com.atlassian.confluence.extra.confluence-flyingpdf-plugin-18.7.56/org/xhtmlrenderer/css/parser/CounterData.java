/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser;

public class CounterData {
    private final String _name;
    private final int _value;

    public CounterData(String name, int value) {
        this._name = name;
        this._value = value;
    }

    public String getName() {
        return this._name;
    }

    public int getValue() {
        return this._value;
    }
}

