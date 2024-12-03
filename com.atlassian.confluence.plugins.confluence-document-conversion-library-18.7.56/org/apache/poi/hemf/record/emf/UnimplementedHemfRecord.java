/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emf;

import java.io.IOException;
import org.apache.poi.hemf.record.emf.HemfRecordType;
import org.apache.poi.hemf.record.emf.HemfRecordWithoutProperties;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInputStream;

@Internal
public class UnimplementedHemfRecord
implements HemfRecordWithoutProperties {
    private HemfRecordType recordType;

    @Override
    public HemfRecordType getEmfRecordType() {
        return this.recordType;
    }

    @Override
    public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
        this.recordType = HemfRecordType.getById(recordId);
        long skipped = IOUtils.skipFully(leis, recordSize);
        if (skipped < recordSize) {
            throw new IOException("End of stream reached before record read");
        }
        return skipped;
    }
}

