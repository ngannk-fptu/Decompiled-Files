/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

public class MimeInfo {
    String type;
    String dims;

    public MimeInfo(String type, String dims) {
        this.type = type;
        this.dims = dims;
    }

    public String getDimensions() {
        return this.dims;
    }

    public String getType() {
        return this.type;
    }

    public String toString() {
        return "(" + this.type + "," + this.dims + ")";
    }
}

