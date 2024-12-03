/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.model;

public class PathValue {
    private String value;

    public PathValue(String path) {
        this.value = path;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + (null == this.getValue() ? "null" : "\"" + this.getValue() + "\"") + ")";
    }
}

