/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.XMLAttributes;
import org.xml.sax.AttributeList;
import org.xml.sax.ext.Attributes2;

public final class AttributesProxy
implements AttributeList,
Attributes2 {
    private XMLAttributes fAttributes;

    public AttributesProxy(XMLAttributes xMLAttributes) {
        this.fAttributes = xMLAttributes;
    }

    public void setAttributes(XMLAttributes xMLAttributes) {
        this.fAttributes = xMLAttributes;
    }

    public XMLAttributes getAttributes() {
        return this.fAttributes;
    }

    @Override
    public int getLength() {
        return this.fAttributes.getLength();
    }

    @Override
    public String getQName(int n) {
        return this.fAttributes.getQName(n);
    }

    @Override
    public String getURI(int n) {
        String string = this.fAttributes.getURI(n);
        return string != null ? string : XMLSymbols.EMPTY_STRING;
    }

    @Override
    public String getLocalName(int n) {
        return this.fAttributes.getLocalName(n);
    }

    @Override
    public String getType(int n) {
        return this.fAttributes.getType(n);
    }

    @Override
    public String getType(String string) {
        return this.fAttributes.getType(string);
    }

    @Override
    public String getType(String string, String string2) {
        return string.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getType(null, string2) : this.fAttributes.getType(string, string2);
    }

    @Override
    public String getValue(int n) {
        return this.fAttributes.getValue(n);
    }

    @Override
    public String getValue(String string) {
        return this.fAttributes.getValue(string);
    }

    @Override
    public String getValue(String string, String string2) {
        return string.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getValue(null, string2) : this.fAttributes.getValue(string, string2);
    }

    @Override
    public int getIndex(String string) {
        return this.fAttributes.getIndex(string);
    }

    @Override
    public int getIndex(String string, String string2) {
        return string.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getIndex(null, string2) : this.fAttributes.getIndex(string, string2);
    }

    @Override
    public boolean isDeclared(int n) {
        if (n < 0 || n >= this.fAttributes.getLength()) {
            throw new ArrayIndexOutOfBoundsException(n);
        }
        return Boolean.TRUE.equals(this.fAttributes.getAugmentations(n).getItem("ATTRIBUTE_DECLARED"));
    }

    @Override
    public boolean isDeclared(String string) {
        int n = this.getIndex(string);
        if (n == -1) {
            throw new IllegalArgumentException(string);
        }
        return Boolean.TRUE.equals(this.fAttributes.getAugmentations(n).getItem("ATTRIBUTE_DECLARED"));
    }

    @Override
    public boolean isDeclared(String string, String string2) {
        int n = this.getIndex(string, string2);
        if (n == -1) {
            throw new IllegalArgumentException(string2);
        }
        return Boolean.TRUE.equals(this.fAttributes.getAugmentations(n).getItem("ATTRIBUTE_DECLARED"));
    }

    @Override
    public boolean isSpecified(int n) {
        if (n < 0 || n >= this.fAttributes.getLength()) {
            throw new ArrayIndexOutOfBoundsException(n);
        }
        return this.fAttributes.isSpecified(n);
    }

    @Override
    public boolean isSpecified(String string) {
        int n = this.getIndex(string);
        if (n == -1) {
            throw new IllegalArgumentException(string);
        }
        return this.fAttributes.isSpecified(n);
    }

    @Override
    public boolean isSpecified(String string, String string2) {
        int n = this.getIndex(string, string2);
        if (n == -1) {
            throw new IllegalArgumentException(string2);
        }
        return this.fAttributes.isSpecified(n);
    }

    @Override
    public String getName(int n) {
        return this.fAttributes.getQName(n);
    }
}

