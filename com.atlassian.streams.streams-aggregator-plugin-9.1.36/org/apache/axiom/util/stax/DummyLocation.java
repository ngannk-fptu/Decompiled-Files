/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax;

import javax.xml.stream.Location;

public class DummyLocation
implements Location {
    public static final DummyLocation INSTANCE = new DummyLocation();

    protected DummyLocation() {
    }

    public int getLineNumber() {
        return -1;
    }

    public int getColumnNumber() {
        return -1;
    }

    public int getCharacterOffset() {
        return 0;
    }

    public String getPublicId() {
        return null;
    }

    public String getSystemId() {
        return null;
    }
}

