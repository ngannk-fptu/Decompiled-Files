/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.conversion.impl;

public class ConversionData {
    private Object value;
    private Class toClass;

    public ConversionData() {
    }

    public ConversionData(Object value, Class toClass) {
        this.value = value;
        this.toClass = toClass;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Class getToClass() {
        return this.toClass;
    }

    public void setToClass(Class toClass) {
        this.toClass = toClass;
    }
}

