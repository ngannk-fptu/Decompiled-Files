/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax.helpers;

import org.xml.sax.Locator;

public class LocatorImpl
implements Locator {
    private String publicId;
    private String systemId;
    private int lineNumber;
    private int columnNumber;

    public LocatorImpl() {
    }

    public LocatorImpl(Locator locator) {
        this.setPublicId(locator.getPublicId());
        this.setSystemId(locator.getSystemId());
        this.setLineNumber(locator.getLineNumber());
        this.setColumnNumber(locator.getColumnNumber());
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public void setPublicId(String string) {
        this.publicId = string;
    }

    public void setSystemId(String string) {
        this.systemId = string;
    }

    public void setLineNumber(int n) {
        this.lineNumber = n;
    }

    public void setColumnNumber(int n) {
        this.columnNumber = n;
    }
}

