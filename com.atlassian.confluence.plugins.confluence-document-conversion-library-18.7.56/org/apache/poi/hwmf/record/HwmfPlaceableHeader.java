/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import org.apache.poi.util.LittleEndianInputStream;

public class HwmfPlaceableHeader {
    public static final int WMF_HEADER_MAGIC = -1698247209;
    final Rectangle2D bounds;
    final int unitsPerInch;

    protected HwmfPlaceableHeader(LittleEndianInputStream leis) throws IOException {
        leis.readShort();
        short x1 = leis.readShort();
        short y1 = leis.readShort();
        short x2 = leis.readShort();
        short y2 = leis.readShort();
        this.bounds = new Rectangle2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
        this.unitsPerInch = leis.readShort();
        leis.readInt();
        leis.readShort();
        leis.mark(4);
        if (leis.readShort() != 0) {
            leis.reset();
        }
    }

    public static HwmfPlaceableHeader readHeader(LittleEndianInputStream leis) throws IOException {
        leis.mark(4);
        int magic = leis.readInt();
        if (magic == -1698247209) {
            return new HwmfPlaceableHeader(leis);
        }
        leis.reset();
        return null;
    }

    public Rectangle2D getBounds() {
        return this.bounds;
    }

    public int getUnitsPerInch() {
        return this.unitsPerInch;
    }
}

