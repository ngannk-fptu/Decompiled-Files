/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax;

import javax.xml.stream.Location;

public class ImmutableLocation
implements Location {
    private final int fCharacterOffset;
    private final int fColumnNumber;
    private final int fLineNumber;
    private final String fPublicId;
    private final String fSystemId;

    public ImmutableLocation(Location location) {
        this(location.getCharacterOffset(), location.getColumnNumber(), location.getLineNumber(), location.getPublicId(), location.getSystemId());
    }

    public ImmutableLocation(int n, int n2, int n3, String string, String string2) {
        this.fCharacterOffset = n;
        this.fColumnNumber = n2;
        this.fLineNumber = n3;
        this.fPublicId = string;
        this.fSystemId = string2;
    }

    @Override
    public int getCharacterOffset() {
        return this.fCharacterOffset;
    }

    @Override
    public int getColumnNumber() {
        return this.fColumnNumber;
    }

    @Override
    public int getLineNumber() {
        return this.fLineNumber;
    }

    @Override
    public String getPublicId() {
        return this.fPublicId;
    }

    @Override
    public String getSystemId() {
        return this.fSystemId;
    }
}

