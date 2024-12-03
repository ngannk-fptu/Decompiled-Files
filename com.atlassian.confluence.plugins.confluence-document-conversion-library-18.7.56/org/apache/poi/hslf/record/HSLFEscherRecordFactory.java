/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.util.function.Supplier;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hslf.record.EscherPlaceholder;
import org.apache.poi.hslf.record.HSLFEscherClientDataRecord;

public class HSLFEscherRecordFactory
extends DefaultEscherRecordFactory {
    @Override
    protected Supplier<? extends EscherRecord> getConstructor(short options, short recordId) {
        if (recordId == EscherPlaceholder.RECORD_ID) {
            return EscherPlaceholder::new;
        }
        if (recordId == EscherClientDataRecord.RECORD_ID) {
            return HSLFEscherClientDataRecord::new;
        }
        return super.getConstructor(options, recordId);
    }
}

