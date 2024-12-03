/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

public class XMLNotationDecl {
    public String name;
    public String publicId;
    public String systemId;
    public String baseSystemId;

    public void setValues(String string, String string2, String string3, String string4) {
        this.name = string;
        this.publicId = string2;
        this.systemId = string3;
        this.baseSystemId = string4;
    }

    public void clear() {
        this.name = null;
        this.publicId = null;
        this.systemId = null;
        this.baseSystemId = null;
    }
}

