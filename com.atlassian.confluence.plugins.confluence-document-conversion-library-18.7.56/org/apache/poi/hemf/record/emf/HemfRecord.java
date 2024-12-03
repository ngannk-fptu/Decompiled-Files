/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emf;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfHeader;
import org.apache.poi.hemf.record.emf.HemfRecordType;
import org.apache.poi.hwmf.record.HwmfRecord;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInputStream;

@Internal
public interface HemfRecord
extends GenericRecord {
    public HemfRecordType getEmfRecordType();

    public long init(LittleEndianInputStream var1, long var2, long var4) throws IOException;

    default public void draw(HemfGraphics ctx) {
        if (this instanceof HwmfRecord) {
            ((HwmfRecord)((Object)this)).draw(ctx);
        }
    }

    default public void calcBounds(RenderBounds holder) {
    }

    default public void setHeader(HemfHeader header) {
    }

    default public HemfRecordType getGenericRecordType() {
        return this.getEmfRecordType();
    }

    public static interface RenderBounds {
        public HemfGraphics.EmfRenderState getState();

        public void setState(HemfGraphics.EmfRenderState var1);

        public Rectangle2D getWindow();

        public Rectangle2D getViewport();

        public Rectangle2D getBounds();
    }
}

