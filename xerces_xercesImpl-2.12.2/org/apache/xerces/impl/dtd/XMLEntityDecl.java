/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

public class XMLEntityDecl {
    public String name;
    public String publicId;
    public String systemId;
    public String baseSystemId;
    public String notation;
    public boolean isPE;
    public boolean inExternal;
    public String value;

    public void setValues(String string, String string2, String string3, String string4, String string5, boolean bl, boolean bl2) {
        this.setValues(string, string2, string3, string4, string5, null, bl, bl2);
    }

    public void setValues(String string, String string2, String string3, String string4, String string5, String string6, boolean bl, boolean bl2) {
        this.name = string;
        this.publicId = string2;
        this.systemId = string3;
        this.baseSystemId = string4;
        this.notation = string5;
        this.value = string6;
        this.isPE = bl;
        this.inExternal = bl2;
    }

    public void clear() {
        this.name = null;
        this.publicId = null;
        this.systemId = null;
        this.baseSystemId = null;
        this.notation = null;
        this.value = null;
        this.isPE = false;
        this.inExternal = false;
    }
}

