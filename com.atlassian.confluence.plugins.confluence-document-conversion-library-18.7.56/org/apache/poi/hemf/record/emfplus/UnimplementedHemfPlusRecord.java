/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emfplus;

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecord;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecordType;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInputStream;

@Internal
public class UnimplementedHemfPlusRecord
implements HemfPlusRecord {
    private HemfPlusRecordType recordType;
    private int flags;

    @Override
    public HemfPlusRecordType getEmfPlusRecordType() {
        return this.recordType;
    }

    @Override
    public int getFlags() {
        return this.flags;
    }

    @Override
    public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
        this.recordType = HemfPlusRecordType.getById(recordId);
        this.flags = flags;
        long skipped = IOUtils.skipFully(leis, dataSize);
        if (skipped < dataSize) {
            throw new IOException("End of stream reached before record read");
        }
        return skipped;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("flags", this::getFlags);
    }
}

