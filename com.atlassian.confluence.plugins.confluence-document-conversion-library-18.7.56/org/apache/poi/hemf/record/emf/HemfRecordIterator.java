/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emf;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.poi.hemf.record.emf.HemfMisc;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emf.HemfRecordType;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.RecordFormatException;

public class HemfRecordIterator
implements Iterator<HemfRecord> {
    static final int HEADER_SIZE = 8;
    private final LittleEndianInputStream stream;
    private HemfRecord currentRecord;

    public HemfRecordIterator(LittleEndianInputStream leis) {
        this.stream = leis;
        this.currentRecord = this._next();
    }

    @Override
    public boolean hasNext() {
        return this.currentRecord != null;
    }

    @Override
    public HemfRecord next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        HemfRecord toReturn = this.currentRecord;
        this.currentRecord = this.currentRecord instanceof HemfMisc.EmfEof ? null : this._next();
        return toReturn;
    }

    private HemfRecord _next() {
        long recordSize;
        long recordId;
        if (this.currentRecord != null && HemfRecordType.eof == this.currentRecord.getEmfRecordType()) {
            return null;
        }
        int readIndex = this.stream.getReadIndex();
        try {
            recordId = this.stream.readUInt();
            recordSize = this.stream.readUInt();
        }
        catch (RuntimeException e) {
            return null;
        }
        HemfRecordType type = HemfRecordType.getById(recordId);
        if (type == null) {
            throw new RecordFormatException("Undefined record of type: " + recordId + " at " + Integer.toHexString(readIndex));
        }
        HemfRecord record = type.constructor.get();
        try {
            long remBytes = recordSize - 8L;
            long readBytes = record.init(this.stream, remBytes, recordId);
            if (readBytes > remBytes) {
                throw new RecordFormatException("Record limit exceeded - readBytes: " + readBytes + " / remBytes: " + remBytes);
            }
            this.stream.skipFully((int)(remBytes - readBytes));
        }
        catch (RecordFormatException e) {
            throw e;
        }
        catch (IOException | RuntimeException e) {
            throw new RecordFormatException(e);
        }
        return record;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported");
    }
}

