/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.properties;

import aQute.bnd.properties.IRegion;

public class Region
implements IRegion {
    private final int offset;
    private final int length;

    public Region(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    @Override
    public int getOffset() {
        return this.offset;
    }

    @Override
    public int getLength() {
        return this.length;
    }
}

