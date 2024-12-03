/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonLocation
 */
package com.sun.jersey.json.impl.reader;

import javax.xml.stream.Location;
import org.codehaus.jackson.JsonLocation;

class StaxLocation
implements Location {
    private int charOffset = -1;
    private int column = -1;
    private int line = -1;

    StaxLocation(int charOffset, int column, int line) {
        this.charOffset = charOffset;
        this.column = column;
        this.line = line;
    }

    StaxLocation(JsonLocation location) {
        this((int)location.getCharOffset(), location.getColumnNr(), location.getLineNr());
    }

    @Override
    public int getCharacterOffset() {
        return this.charOffset;
    }

    @Override
    public int getColumnNumber() {
        return this.column;
    }

    @Override
    public int getLineNumber() {
        return this.line;
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

