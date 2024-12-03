/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class UnknownRecordPlaceholder
extends RecordAtom {
    private static final int MAX_RECORD_LENGTH = 20000000;
    private byte[] _contents;
    private long _type;

    protected UnknownRecordPlaceholder(byte[] source, int start, int len) {
        if (len < 0) {
            len = 0;
        }
        this._contents = IOUtils.safelyClone(source, start, len, 20000000);
        this._type = LittleEndian.getUShort(this._contents, 2);
    }

    @Override
    public long getRecordType() {
        return this._type;
    }

    public RecordTypes getRecordTypeEnum() {
        return RecordTypes.forTypeID((int)this._type);
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._contents);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("contents", () -> this._contents);
    }
}

