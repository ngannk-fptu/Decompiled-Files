/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ddf;

import java.util.function.Supplier;
import org.apache.poi.ddf.EscherBlipRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherRecordFactory;
import org.apache.poi.ddf.EscherRecordTypes;
import org.apache.poi.ddf.UnknownEscherRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndian;

public class DefaultEscherRecordFactory
implements EscherRecordFactory {
    private static final BitField IS_CONTAINER = BitFieldFactory.getInstance(15);

    @Override
    public EscherRecord createRecord(byte[] data, int offset) {
        short options = LittleEndian.getShort(data, offset);
        short recordId = LittleEndian.getShort(data, offset + 2);
        EscherRecord escherRecord = this.getConstructor(options, recordId).get();
        escherRecord.setRecordId(recordId);
        escherRecord.setOptions(options);
        return escherRecord;
    }

    protected Supplier<? extends EscherRecord> getConstructor(short options, short recordId) {
        EscherRecordTypes recordTypes = EscherRecordTypes.forTypeID(recordId);
        if (recordTypes == EscherRecordTypes.UNKNOWN && IS_CONTAINER.isAllSet(options)) {
            return EscherContainerRecord::new;
        }
        if (recordTypes.constructor != null && recordTypes != EscherRecordTypes.UNKNOWN) {
            return recordTypes.constructor;
        }
        if (EscherBlipRecord.RECORD_ID_START <= recordId && recordId <= EscherBlipRecord.RECORD_ID_END) {
            return EscherBlipRecord::new;
        }
        return UnknownEscherRecord::new;
    }
}

