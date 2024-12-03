/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emfplus;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecord;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecordType;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.RecordFormatException;

public class HemfPlusRecordIterator
implements Iterator<HemfPlusRecord> {
    private final LittleEndianInputStream leis;
    private final int startIdx;
    private final int limit;
    private HemfPlusRecord currentRecord;

    public HemfPlusRecordIterator(LittleEndianInputStream leis) {
        this(leis, -1);
    }

    public HemfPlusRecordIterator(LittleEndianInputStream leis, int limit) {
        this.leis = leis;
        this.limit = limit;
        this.startIdx = leis.getReadIndex();
        this.currentRecord = this._next();
    }

    @Override
    public boolean hasNext() {
        return this.currentRecord != null;
    }

    @Override
    public HemfPlusRecord next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        HemfPlusRecord toReturn = this.currentRecord;
        boolean isEOF = this.limit == -1 || this.leis.getReadIndex() - this.startIdx + 12 > this.limit;
        this.currentRecord = isEOF ? null : this._next();
        return toReturn;
    }

    private HemfPlusRecord _next() {
        if (this.currentRecord != null && HemfPlusRecordType.eof == this.currentRecord.getEmfPlusRecordType()) {
            return null;
        }
        int recordId = this.leis.readUShort();
        int flags = this.leis.readUShort();
        int recordSize = (int)this.leis.readUInt();
        int dataSize = (int)this.leis.readUInt();
        HemfPlusRecordType type = HemfPlusRecordType.getById(recordId);
        if (type == null) {
            throw new RecordFormatException("Undefined record of type:" + recordId);
        }
        HemfPlusRecord record = type.constructor.get();
        try {
            long readBytes = record.init(this.leis, dataSize, recordId, flags);
            assert (readBytes <= (long)(recordSize - 12));
            this.leis.skipFully((int)((long)(recordSize - 12) - readBytes));
        }
        catch (IOException e) {
            throw new RecordFormatException(e);
        }
        return record;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported");
    }
}

