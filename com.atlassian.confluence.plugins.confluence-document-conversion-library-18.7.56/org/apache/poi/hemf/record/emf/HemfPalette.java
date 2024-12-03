/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emf;

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emf.HemfRecordType;
import org.apache.poi.hwmf.record.HwmfPalette;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInputStream;

public class HemfPalette {

    public static class EmfSetIcmMode
    implements HemfRecord {
        private ICMMode icmMode;

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.seticmmode;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.icmMode = ICMMode.valueOf(leis.readInt());
            return 4L;
        }

        public ICMMode getIcmMode() {
            return this.icmMode;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("icmMode", this::getIcmMode);
        }

        public static enum ICMMode {
            ICM_OFF(1),
            ICM_ON(2),
            ICM_QUERY(3),
            ICM_DONE_OUTSIDEDC(4);

            public final int id;

            private ICMMode(int id) {
                this.id = id;
            }

            public static ICMMode valueOf(int id) {
                for (ICMMode wrt : ICMMode.values()) {
                    if (wrt.id != id) continue;
                    return wrt;
                }
                return null;
            }
        }
    }

    public static class EmfRealizePalette
    extends HwmfPalette.WmfRealizePalette
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.realizePalette;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return 0L;
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfResizePalette
    extends HwmfPalette.WmfResizePalette
    implements HemfRecord {
        int paletteIndex;

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.resizePalette;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.paletteIndex = (int)leis.readUInt();
            this.numberOfEntries = (int)leis.readUInt();
            return 8L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.addObjectTableEntry(this, this.paletteIndex);
        }

        public int getPaletteIndex() {
            return this.paletteIndex;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "paletteIndex", this::getPaletteIndex);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSetPaletteEntries
    extends HwmfPalette.WmfSetPaletteEntries
    implements HemfRecord {
        int paletteIndex;

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setPaletteEntries;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.paletteIndex = (int)leis.readUInt();
            this.start = (int)leis.readUInt();
            int nbrOfEntries = (int)leis.readUInt();
            int size = this.readPaletteEntries(leis, nbrOfEntries);
            return (long)size + 12L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.addObjectTableEntry(this, this.paletteIndex);
        }

        public int getPaletteIndex() {
            return this.paletteIndex;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "paletteIndex", this::getPaletteIndex);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfCreatePalette
    extends HwmfPalette.WmfCreatePalette
    implements HemfRecord {
        protected int paletteIndex;

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.createPalette;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.start = 768;
            this.paletteIndex = (int)leis.readUInt();
            int version = leis.readUShort();
            assert (version == 768);
            long size = this.readPaletteEntries(leis, -1);
            return size + 4L + 2L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.addObjectTableEntry(this, this.paletteIndex);
        }

        public int getPaletteIndex() {
            return this.paletteIndex;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "paletteIndex", this::getPaletteIndex);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSelectPalette
    extends HwmfPalette.WmfSelectPalette
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.selectPalette;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.paletteIndex = (int)leis.readUInt();
            return 4L;
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }
}

