/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import org.xml.sax.Locator;

public class DummyLocator
implements Locator {
    public int getColumnNumber() {
        return -1;
    }

    public int getLineNumber() {
        return -1;
    }

    public String getPublicId() {
        return null;
    }

    public String getSystemId() {
        return null;
    }
}

