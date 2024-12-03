/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import org.apache.xerces.xni.XMLLocator;
import org.xml.sax.ext.Locator2;

public class LocatorProxy
implements Locator2 {
    private final XMLLocator fLocator;

    public LocatorProxy(XMLLocator xMLLocator) {
        this.fLocator = xMLLocator;
    }

    @Override
    public String getPublicId() {
        return this.fLocator.getPublicId();
    }

    @Override
    public String getSystemId() {
        return this.fLocator.getExpandedSystemId();
    }

    @Override
    public int getLineNumber() {
        return this.fLocator.getLineNumber();
    }

    @Override
    public int getColumnNumber() {
        return this.fLocator.getColumnNumber();
    }

    @Override
    public String getXMLVersion() {
        return this.fLocator.getXMLVersion();
    }

    @Override
    public String getEncoding() {
        return this.fLocator.getEncoding();
    }
}

