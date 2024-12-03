/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import org.apache.poi.hslf.record.ExMCIMovie;
import org.apache.poi.hslf.record.RecordTypes;

public final class ExAviMovie
extends ExMCIMovie {
    protected ExAviMovie(byte[] source, int start, int len) {
        super(source, start, len);
    }

    public ExAviMovie() {
    }

    @Override
    public long getRecordType() {
        return RecordTypes.ExAviMovie.typeID;
    }
}

