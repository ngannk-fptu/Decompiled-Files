/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

public class XMLSchemaException
extends Exception {
    static final long serialVersionUID = -9096984648537046218L;
    String key;
    Object[] args;

    public XMLSchemaException(String string, Object[] objectArray) {
        this.key = string;
        this.args = objectArray;
    }

    public String getKey() {
        return this.key;
    }

    public Object[] getArgs() {
        return this.args;
    }
}

