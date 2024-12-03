/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hwmf.draw.HwmfDrawProperties;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfObjectTableEntry;
import org.apache.poi.hwmf.record.HwmfRecord;
import org.apache.poi.hwmf.record.HwmfRecordType;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInputStream;

public class HwmfPalette {

    public static class WmfAnimatePalette
    extends WmfPaletteParent {
        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.animatePalette;
        }

        @Override
        public void applyObject(HwmfGraphics ctx) {
            int i;
            HwmfDrawProperties props = ctx.getProperties();
            List<PaletteEntry> dest = props.getPalette();
            List<PaletteEntry> src = this.getPaletteCopy();
            int start = this.getPaletteStart();
            if (dest == null) {
                dest = new ArrayList<PaletteEntry>();
            }
            for (i = dest.size(); i < start; ++i) {
                dest.add(new PaletteEntry());
            }
            for (i = 0; i < src.size(); ++i) {
                PaletteEntry pe = src.get(i);
                if (dest.size() <= start + i) {
                    dest.add(pe);
                    continue;
                }
                PaletteEntry peDst = dest.get(start + i);
                if (!peDst.isReserved()) continue;
                dest.set(start + i, pe);
            }
            props.setPalette(dest);
        }
    }

    public static class WmfRealizePalette
    implements HwmfRecord {
        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.realizePalette;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return 0;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return null;
        }
    }

    public static class WmfSelectPalette
    implements HwmfRecord {
        protected int paletteIndex;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.selectPalette;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.paletteIndex = leis.readUShort();
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.applyObjectTableEntry(this.paletteIndex);
        }

        public int getPaletteIndex() {
            return this.paletteIndex;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("paletteIndex", this::getPaletteIndex);
        }
    }

    public static class WmfResizePalette
    implements HwmfRecord,
    HwmfObjectTableEntry {
        protected int numberOfEntries;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.resizePalette;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.numberOfEntries = leis.readUShort();
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.addObjectTableEntry(this);
        }

        @Override
        public void applyObject(HwmfGraphics ctx) {
            HwmfDrawProperties props = ctx.getProperties();
            List<PaletteEntry> palette = props.getPalette();
            if (palette == null) {
                palette = new ArrayList<PaletteEntry>();
            }
            for (int i = palette.size(); i < this.numberOfEntries; ++i) {
                palette.add(new PaletteEntry());
            }
            palette = palette.subList(0, this.numberOfEntries);
            props.setPalette(palette);
        }

        public int getNumberOfEntries() {
            return this.numberOfEntries;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("numberOfEntries", this::getNumberOfEntries);
        }
    }

    public static class WmfSetPaletteEntries
    extends WmfPaletteParent {
        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setPalEntries;
        }

        @Override
        public void applyObject(HwmfGraphics ctx) {
            HwmfDrawProperties props = ctx.getProperties();
            List<PaletteEntry> palette = props.getPalette();
            if (palette == null) {
                palette = new ArrayList<PaletteEntry>();
            }
            int start = this.getPaletteStart();
            for (int i = palette.size(); i < start; ++i) {
                palette.add(new PaletteEntry());
            }
            int index = start;
            for (PaletteEntry palCopy : this.getPaletteCopy()) {
                if (palette.size() <= index) {
                    palette.add(palCopy);
                } else {
                    palette.set(index, palCopy);
                }
                ++index;
            }
            props.setPalette(palette);
        }
    }

    public static class WmfCreatePalette
    extends WmfPaletteParent
    implements HwmfObjectTableEntry {
        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.createPalette;
        }

        @Override
        public void applyObject(HwmfGraphics ctx) {
            ctx.getProperties().setPalette(this.getPaletteCopy());
        }
    }

    public static abstract class WmfPaletteParent
    implements HwmfRecord,
    HwmfObjectTableEntry {
        protected int start;
        protected final List<PaletteEntry> palette = new ArrayList<PaletteEntry>();

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.start = leis.readUShort();
            int size = this.readPaletteEntries(leis, -1);
            return size + 2;
        }

        protected int readPaletteEntries(LittleEndianInputStream leis, int nbrOfEntries) throws IOException {
            int numberOfEntries = nbrOfEntries > -1 ? nbrOfEntries : leis.readUShort();
            int size = nbrOfEntries > -1 ? 0 : 2;
            for (int i = 0; i < numberOfEntries; ++i) {
                PaletteEntry pe = new PaletteEntry();
                size += pe.init(leis);
                this.palette.add(pe);
            }
            return size;
        }

        @Override
        public final void draw(HwmfGraphics ctx) {
            ctx.addObjectTableEntry(this);
        }

        List<PaletteEntry> getPaletteCopy() {
            ArrayList<PaletteEntry> newPalette = new ArrayList<PaletteEntry>();
            for (PaletteEntry et : this.palette) {
                newPalette.add(new PaletteEntry(et));
            }
            return newPalette;
        }

        int getPaletteStart() {
            return this.start;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("paletteStart", this::getPaletteStart, "pallete", this::getPaletteCopy);
        }
    }

    public static class PaletteEntry
    implements GenericRecord {
        private static final BitField PC_RESERVED = BitFieldFactory.getInstance(1);
        private static final BitField PC_EXPLICIT = BitFieldFactory.getInstance(2);
        private static final BitField PC_NOCOLLAPSE = BitFieldFactory.getInstance(4);
        private static final int[] FLAGS_MASKS = new int[]{1, 2, 4};
        private static final String[] FLAGS_NAMES = new String[]{"RESERVED", "EXPLICIT", "NOCOLLAPSE"};
        private int values;
        private Color colorRef;

        public PaletteEntry() {
            this.values = PC_RESERVED.set(0);
            this.colorRef = Color.BLACK;
        }

        public PaletteEntry(PaletteEntry other) {
            this.values = other.values;
            this.colorRef = other.colorRef;
        }

        public int init(LittleEndianInputStream leis) throws IOException {
            this.values = leis.readUByte();
            int blue = leis.readUByte();
            int green = leis.readUByte();
            int red = leis.readUByte();
            this.colorRef = new Color(red, green, blue);
            return 4;
        }

        public boolean isReserved() {
            return PC_RESERVED.isSet(this.values);
        }

        public boolean isExplicit() {
            return PC_EXPLICIT.isSet(this.values);
        }

        public boolean isNoCollapse() {
            return PC_NOCOLLAPSE.isSet(this.values);
        }

        public Color getColorRef() {
            return this.colorRef;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", GenericRecordUtil.getBitsAsString(() -> this.values, FLAGS_MASKS, FLAGS_NAMES), "color", this::getColorRef);
        }
    }
}

