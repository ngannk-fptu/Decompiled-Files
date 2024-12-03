/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.ognl;

public class ObjectProxy {
    private Object value;
    private Class lastClassAccessed;
    private String lastPropertyAccessed;

    public Class getLastClassAccessed() {
        return this.lastClassAccessed;
    }

    public void setLastClassAccessed(Class lastClassAccessed) {
        this.lastClassAccessed = lastClassAccessed;
    }

    public String getLastPropertyAccessed() {
        return this.lastPropertyAccessed;
    }

    public void setLastPropertyAccessed(String lastPropertyAccessed) {
        this.lastPropertyAccessed = lastPropertyAccessed;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}

