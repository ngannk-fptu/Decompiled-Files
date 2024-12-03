/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.tidy.AttrCheck;

public class Attribute {
    private String name;
    private boolean nowrap;
    private boolean literal;
    private short versions;
    private AttrCheck attrchk;

    public Attribute(String attributeName, short htmlVersions, AttrCheck check) {
        this.name = attributeName;
        this.versions = htmlVersions;
        this.attrchk = check;
    }

    public void setLiteral(boolean isLiteral) {
        this.literal = isLiteral;
    }

    public void setNowrap(boolean isNowrap) {
        this.nowrap = isNowrap;
    }

    public AttrCheck getAttrchk() {
        return this.attrchk;
    }

    public boolean isLiteral() {
        return this.literal;
    }

    public String getName() {
        return this.name;
    }

    public boolean isNowrap() {
        return this.nowrap;
    }

    public short getVersions() {
        return this.versions;
    }
}

