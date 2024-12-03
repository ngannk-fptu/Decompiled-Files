/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.util;

import org.apache.xerces.xni.XMLLocator;

public final class SimpleLocator
implements XMLLocator {
    private String lsid;
    private String esid;
    private int line;
    private int column;
    private int charOffset;

    public SimpleLocator() {
    }

    public SimpleLocator(String string, String string2, int n, int n2) {
        this(string, string2, n, n2, -1);
    }

    public void setValues(String string, String string2, int n, int n2) {
        this.setValues(string, string2, n, n2, -1);
    }

    public SimpleLocator(String string, String string2, int n, int n2, int n3) {
        this.line = n;
        this.column = n2;
        this.lsid = string;
        this.esid = string2;
        this.charOffset = n3;
    }

    public void setValues(String string, String string2, int n, int n2, int n3) {
        this.line = n;
        this.column = n2;
        this.lsid = string;
        this.esid = string2;
        this.charOffset = n3;
    }

    @Override
    public int getLineNumber() {
        return this.line;
    }

    @Override
    public int getColumnNumber() {
        return this.column;
    }

    @Override
    public int getCharacterOffset() {
        return this.charOffset;
    }

    @Override
    public String getPublicId() {
        return null;
    }

    @Override
    public String getExpandedSystemId() {
        return this.esid;
    }

    @Override
    public String getLiteralSystemId() {
        return this.lsid;
    }

    @Override
    public String getBaseSystemId() {
        return null;
    }

    public void setColumnNumber(int n) {
        this.column = n;
    }

    public void setLineNumber(int n) {
        this.line = n;
    }

    public void setCharacterOffset(int n) {
        this.charOffset = n;
    }

    public void setBaseSystemId(String string) {
    }

    public void setExpandedSystemId(String string) {
        this.esid = string;
    }

    public void setLiteralSystemId(String string) {
        this.lsid = string;
    }

    public void setPublicId(String string) {
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public String getXMLVersion() {
        return null;
    }
}

