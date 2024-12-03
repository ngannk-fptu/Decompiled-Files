/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax;

import javax.xml.stream.Location;

public final class EmptyLocation
implements Location {
    private static final EmptyLocation EMPTY_LOCATION_INSTANCE = new EmptyLocation();

    private EmptyLocation() {
    }

    public static EmptyLocation getInstance() {
        return EMPTY_LOCATION_INSTANCE;
    }

    @Override
    public int getLineNumber() {
        return -1;
    }

    @Override
    public int getColumnNumber() {
        return -1;
    }

    @Override
    public int getCharacterOffset() {
        return -1;
    }

    @Override
    public String getPublicId() {
        return null;
    }

    @Override
    public String getSystemId() {
        return null;
    }
}

