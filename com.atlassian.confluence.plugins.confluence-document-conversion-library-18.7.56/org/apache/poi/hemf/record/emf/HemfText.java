/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emf;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfDraw;
import org.apache.poi.hemf.record.emf.HemfFont;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emf.HemfRecordType;
import org.apache.poi.hemf.record.emf.UnimplementedHemfRecord;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfText;
import org.apache.poi.util.Dimension2DDouble;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.RecordFormatException;

@Internal
public class HemfText {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 1000000;
    private static int MAX_RECORD_LENGTH = 1000000;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public static class PolyTextOutW
    extends UnimplementedHemfRecord {
    }

    public static class PolyTextOutA
    extends UnimplementedHemfRecord {
    }

    public static class SetTextJustification
    extends UnimplementedHemfRecord {
    }

    public static class EmfExtTextOutOptions
    extends HwmfText.WmfExtTextOutOptions {
        @Override
        public int init(LittleEndianInputStream leis) {
            this.flags = (int)leis.readUInt();
            return 4;
        }
    }

    public static class EmfExtCreateFontIndirectW
    extends HwmfText.WmfCreateFontIndirect
    implements HemfRecord {
        int fontIdx;

        public EmfExtCreateFontIndirectW() {
            super(new HemfFont());
        }

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.extCreateFontIndirectW;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.fontIdx = (int)leis.readUInt();
            long size = this.font.init(leis, (int)(recordSize - 4L));
            return size + 4L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.addObjectTableEntry(this, this.fontIdx);
        }

        @Override
        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public int getFontIdx() {
            return this.fontIdx;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "fontIdx", this::getFontIdx);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSetTextColor
    extends HwmfText.WmfSetTextColor
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setTextColor;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return this.colorRef.init(leis);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSetTextAlign
    extends HwmfText.WmfSetTextAlign
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setTextAlign;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.textAlignmentMode = (int)leis.readUInt();
            return 4L;
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfExtTextOutW
    extends EmfExtTextOutA {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.extTextOutW;
        }

        public String getText() throws IOException {
            return this.getText(StandardCharsets.UTF_16LE);
        }

        @Override
        protected boolean isUnicode() {
            return true;
        }
    }

    public static class EmfExtTextOutA
    extends HwmfText.WmfExtTextOut
    implements HemfRecord {
        protected Rectangle2D boundsIgnored = new Rectangle2D.Double();
        protected EmfGraphicsMode graphicsMode;
        protected final Dimension2D scale = new Dimension2DDouble();

        public EmfExtTextOutA() {
            super(new EmfExtTextOutOptions());
        }

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.extTextOutA;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            if (recordSize < 0L || Integer.MAX_VALUE <= recordSize) {
                throw new RecordFormatException("recordSize must be a positive integer (0-0x7FFFFFFF)");
            }
            long size = HemfDraw.readRectL(leis, this.boundsIgnored);
            this.graphicsMode = EmfGraphicsMode.values()[leis.readInt() - 1];
            size += 4L;
            size += HemfDraw.readDimensionFloat(leis, this.scale);
            size += HemfDraw.readPointL(leis, this.reference);
            this.stringLength = (int)leis.readUInt();
            int offString = (int)leis.readUInt();
            size += 8L;
            size += (long)this.options.init(leis);
            if (this.options.isClipped() || this.options.isOpaque()) {
                size += HemfDraw.readRectL(leis, this.bounds);
            }
            int offDx = (int)leis.readUInt();
            size += 4L;
            String order = offDx < offString ? "ds" : "sd";
            int strEnd = (int)(offDx <= 8 ? recordSize : (long)(offDx - 8));
            block3: for (char op : order.toCharArray()) {
                switch (op) {
                    case 'd': {
                        int maxSize;
                        this.dx.clear();
                        int undefinedSpace2 = (int)((long)offDx - (size + 8L));
                        if (offDx > 0 && undefinedSpace2 >= 0 && (long)(offDx - 8) < recordSize) {
                            leis.skipFully(undefinedSpace2);
                            size += (long)undefinedSpace2;
                            maxSize = (int)Math.min(offDx < offString ? (long)(offString - 8) : recordSize, recordSize);
                            while (size <= (long)(maxSize - 4)) {
                                this.dx.add((int)leis.readUInt());
                                size += 4L;
                            }
                        }
                        if (this.dx.size() < this.stringLength) {
                            this.dx.clear();
                        }
                        strEnd = Math.toIntExact(recordSize);
                        continue block3;
                    }
                    default: {
                        int undefinedSpace1 = Math.toIntExact((long)offString - (size + 8L));
                        if (offString <= 0 || undefinedSpace1 < 0 || (long)(offString - 8) >= recordSize) continue block3;
                        leis.skipFully(undefinedSpace1);
                        int maxSize = Math.toIntExact(Math.min(recordSize, (long)strEnd) - (size += (long)undefinedSpace1));
                        this.rawTextBytes = IOUtils.safelyAllocate(maxSize, MAX_RECORD_LENGTH);
                        leis.readFully(this.rawTextBytes);
                        size += (long)maxSize;
                        continue block3;
                    }
                }
            }
            return size;
        }

        @Override
        public String getText(Charset charset) throws IOException {
            return super.getText(charset);
        }

        public EmfGraphicsMode getGraphicsMode() {
            return this.graphicsMode;
        }

        public Dimension2D getScale() {
            return this.scale;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            Dimension2D scl = this.graphicsMode == EmfGraphicsMode.GM_COMPATIBLE ? this.scale : null;
            ctx.setCharsetProvider(this.charsetProvider);
            ctx.drawString(this.rawTextBytes, this.stringLength, this.reference, scl, this.bounds, this.options, this.dx, this.isUnicode());
        }

        @Override
        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "boundsIgnored", () -> this.boundsIgnored, "graphicsMode", this::getGraphicsMode, "scale", this::getScale);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static enum EmfGraphicsMode {
        GM_COMPATIBLE,
        GM_ADVANCED;

    }
}

