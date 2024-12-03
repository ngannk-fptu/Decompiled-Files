/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc.spatialdatatypes;

public class Segment {
    private byte segmentType;

    public Segment(byte segmentType) {
        this.segmentType = segmentType;
    }

    public byte getSegmentType() {
        return this.segmentType;
    }
}

