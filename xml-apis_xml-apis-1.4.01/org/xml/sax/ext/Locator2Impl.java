/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax.ext;

import org.xml.sax.Locator;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.LocatorImpl;

public class Locator2Impl
extends LocatorImpl
implements Locator2 {
    private String encoding;
    private String version;

    public Locator2Impl() {
    }

    public Locator2Impl(Locator locator) {
        super(locator);
        if (locator instanceof Locator2) {
            Locator2 locator2 = (Locator2)locator;
            this.version = locator2.getXMLVersion();
            this.encoding = locator2.getEncoding();
        }
    }

    public String getXMLVersion() {
        return this.version;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setXMLVersion(String string) {
        this.version = string;
    }

    public void setEncoding(String string) {
        this.encoding = string;
    }
}

