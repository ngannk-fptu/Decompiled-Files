/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.LinkedList;
import java.util.List;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PICF;
import org.apache.poi.hwpf.model.types.PICFAbstractType;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public class PICFAndOfficeArtData {
    private final List<EscherRecord> _blipRecords = new LinkedList<EscherRecord>();
    private final PICF _picf;
    private final EscherContainerRecord _shape = new EscherContainerRecord();
    private byte[] _stPicName;

    public PICFAndOfficeArtData(byte[] dataStream, int startOffset) {
        EscherRecord nextRecord;
        int offset = startOffset;
        this._picf = new PICF(dataStream, offset);
        offset += PICFAbstractType.getSize();
        if (this._picf.getMm() == 102) {
            short _cchPicName = LittleEndian.getUByte(dataStream, offset);
            this._stPicName = IOUtils.safelyClone(dataStream, ++offset, _cchPicName, HWPFDocument.getMaxRecordLength());
            offset += _cchPicName;
        }
        DefaultEscherRecordFactory escherRecordFactory = new DefaultEscherRecordFactory();
        int recordSize = this._shape.fillFields(dataStream, offset, escherRecordFactory);
        offset += recordSize;
        while (offset - startOffset < this._picf.getLcb() && ((nextRecord = escherRecordFactory.createRecord(dataStream, offset)).getRecordId() == -4089 || nextRecord.getRecordId() >= -4072 && nextRecord.getRecordId() <= -3817)) {
            int blipRecordSize = nextRecord.fillFields(dataStream, offset, escherRecordFactory);
            offset += blipRecordSize;
            this._blipRecords.add(nextRecord);
            assert (this._blipRecords.size() == 1);
        }
    }

    public List<EscherRecord> getBlipRecords() {
        return this._blipRecords;
    }

    public PICF getPicf() {
        return this._picf;
    }

    public EscherContainerRecord getShape() {
        return this._shape;
    }

    public byte[] getStPicName() {
        return this._stPicName;
    }
}

