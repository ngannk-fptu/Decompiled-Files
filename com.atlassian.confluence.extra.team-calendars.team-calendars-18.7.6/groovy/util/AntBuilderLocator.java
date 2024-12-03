/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import org.xml.sax.Locator;

class AntBuilderLocator
implements Locator {
    AntBuilderLocator() {
    }

    @Override
    public int getColumnNumber() {
        return 0;
    }

    @Override
    public int getLineNumber() {
        return 0;
    }

    @Override
    public String getPublicId() {
        return "";
    }

    @Override
    public String getSystemId() {
        return "";
    }
}

