/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.conversion.annotations;

public enum ConversionRule {
    PROPERTY,
    COLLECTION,
    MAP,
    KEY,
    KEY_PROPERTY,
    ELEMENT,
    CREATE_IF_NULL;


    public String toString() {
        return super.toString().toUpperCase();
    }
}

