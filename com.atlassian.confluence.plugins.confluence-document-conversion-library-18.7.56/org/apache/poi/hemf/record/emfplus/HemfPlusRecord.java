/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emfplus;

import java.io.IOException;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecordType;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInputStream;

@Internal
public interface HemfPlusRecord
extends GenericRecord {
    public HemfPlusRecordType getEmfPlusRecordType();

    public int getFlags();

    public long init(LittleEndianInputStream var1, long var2, long var4, int var6) throws IOException;

    default public void draw(HemfGraphics ctx) {
    }

    default public void calcBounds(HemfRecord.RenderBounds holder) {
    }

    default public HemfPlusRecordType getGenericRecordType() {
        return this.getEmfPlusRecordType();
    }
}

