/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hwmf.draw.HwmfDrawProperties;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfBinaryRasterOp;
import org.apache.poi.hwmf.record.HwmfBitmap16;
import org.apache.poi.hwmf.record.HwmfBitmapDib;
import org.apache.poi.hwmf.record.HwmfBrushStyle;
import org.apache.poi.hwmf.record.HwmfColorRef;
import org.apache.poi.hwmf.record.HwmfFill;
import org.apache.poi.hwmf.record.HwmfHatchStyle;
import org.apache.poi.hwmf.record.HwmfMapMode;
import org.apache.poi.hwmf.record.HwmfObjectTableEntry;
import org.apache.poi.hwmf.record.HwmfPenStyle;
import org.apache.poi.hwmf.record.HwmfRecord;
import org.apache.poi.hwmf.record.HwmfRecordType;
import org.apache.poi.util.Dimension2DDouble;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInputStream;

public class HwmfMisc {

    public static class WmfCreateBrushIndirect
    implements HwmfRecord,
    HwmfObjectTableEntry {
        protected HwmfBrushStyle brushStyle;
        protected HwmfColorRef colorRef;
        protected HwmfHatchStyle brushHatch;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.createBrushIndirect;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.brushStyle = HwmfBrushStyle.valueOf(leis.readUShort());
            this.colorRef = new HwmfColorRef();
            int size = this.colorRef.init(leis);
            this.brushHatch = HwmfHatchStyle.valueOf(leis.readUShort());
            return size + 4;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.addObjectTableEntry(this);
        }

        @Override
        public void applyObject(HwmfGraphics ctx) {
            HwmfDrawProperties p = ctx.getProperties();
            p.setBrushStyle(this.brushStyle);
            p.setBrushColor(this.colorRef);
            p.setBrushHatch(this.brushHatch);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public HwmfBrushStyle getBrushStyle() {
            return this.brushStyle;
        }

        public HwmfColorRef getColorRef() {
            return this.colorRef;
        }

        public HwmfHatchStyle getBrushHatch() {
            return this.brushHatch;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("brushStyle", this::getBrushStyle, "colorRef", this::getColorRef, "brushHatch", this::getBrushHatch);
        }
    }

    public static class WmfCreatePenIndirect
    implements HwmfRecord,
    HwmfObjectTableEntry {
        protected HwmfPenStyle penStyle;
        protected final Dimension2D dimension = new Dimension2DDouble();
        protected final HwmfColorRef colorRef = new HwmfColorRef();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.createPenIndirect;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.penStyle = HwmfPenStyle.valueOf(leis.readUShort());
            short xWidth = leis.readShort();
            short yWidth = leis.readShort();
            this.dimension.setSize(xWidth, yWidth);
            int size = this.colorRef.init(leis);
            return size + 6;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.addObjectTableEntry(this);
        }

        @Override
        public void applyObject(HwmfGraphics ctx) {
            HwmfDrawProperties p = ctx.getProperties();
            p.setPenStyle(this.penStyle);
            p.setPenColor(this.colorRef);
            p.setPenWidth(this.dimension.getWidth());
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public HwmfPenStyle getPenStyle() {
            return this.penStyle;
        }

        public Dimension2D getDimension() {
            return this.dimension;
        }

        public HwmfColorRef getColorRef() {
            return this.colorRef;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("penStyle", this::getPenStyle, "dimension", this::getDimension, "colorRef", this::getColorRef);
        }
    }

    public static class WmfCreatePatternBrush
    implements HwmfRecord,
    HwmfObjectTableEntry {
        private HwmfBitmap16 pattern;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.createPatternBrush;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.pattern = new HwmfBitmap16(true);
            return this.pattern.init(leis);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.addObjectTableEntry(this);
        }

        @Override
        public void applyObject(HwmfGraphics ctx) {
            HwmfDrawProperties dp = ctx.getProperties();
            dp.setBrushBitmap(this.pattern.getImage());
            dp.setBrushStyle(HwmfBrushStyle.BS_PATTERN);
        }

        public HwmfBitmap16 getPattern() {
            return this.pattern;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("pattern", this::getPattern);
        }
    }

    public static class WmfDeleteObject
    implements HwmfRecord {
        protected int objectIndex;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.deleteObject;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.objectIndex = leis.readUShort();
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.unsetObjectTableEntry(this.objectIndex);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public int getObjectIndex() {
            return this.objectIndex;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("objectIndex", this::getObjectIndex);
        }
    }

    public static class WmfDibCreatePatternBrush
    implements HwmfRecord,
    HwmfFill.HwmfImageRecord,
    HwmfObjectTableEntry {
        protected HwmfBrushStyle style;
        protected HwmfFill.ColorUsage colorUsage;
        protected HwmfBitmapDib patternDib;
        private HwmfBitmap16 pattern16;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.dibCreatePatternBrush;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.style = HwmfBrushStyle.valueOf(leis.readUShort());
            this.colorUsage = HwmfFill.ColorUsage.valueOf(leis.readUShort());
            int size = 4;
            switch (this.style) {
                case BS_SOLID: 
                case BS_NULL: 
                case BS_DIBPATTERN: 
                case BS_DIBPATTERNPT: 
                case BS_HATCHED: 
                case BS_PATTERN: {
                    this.patternDib = new HwmfBitmapDib();
                    size += this.patternDib.init(leis, (int)(recordSize - 6L - (long)size));
                    break;
                }
                case BS_INDEXED: 
                case BS_DIBPATTERN8X8: 
                case BS_MONOPATTERN: 
                case BS_PATTERN8X8: {
                    throw new RuntimeException("pattern not supported");
                }
            }
            return size;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.addObjectTableEntry(this);
        }

        @Override
        public void applyObject(HwmfGraphics ctx) {
            if (this.patternDib != null && !this.patternDib.isValid()) {
                return;
            }
            HwmfDrawProperties prop = ctx.getProperties();
            prop.setBrushStyle(this.style);
            BufferedImage bufImg = this.getImage(prop.getBrushColor().getColor(), prop.getBackgroundColor().getColor(), prop.getBkMode() == WmfSetBkMode.HwmfBkMode.TRANSPARENT);
            prop.setBrushBitmap(bufImg);
        }

        @Override
        public BufferedImage getImage(Color foreground, Color background, boolean hasAlpha) {
            if (this.patternDib != null && this.patternDib.isValid()) {
                return this.patternDib.getImage(foreground, background, hasAlpha);
            }
            if (this.pattern16 != null) {
                return this.pattern16.getImage();
            }
            return null;
        }

        @Override
        public byte[] getBMPData() {
            if (this.patternDib != null && this.patternDib.isValid()) {
                return this.patternDib.getBMPData();
            }
            if (this.pattern16 != null) {
                return null;
            }
            return null;
        }

        public HwmfBrushStyle getStyle() {
            return this.style;
        }

        public HwmfFill.ColorUsage getColorUsage() {
            return this.colorUsage;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("style", this::getStyle, "colorUsage", this::getColorUsage, "pattern", () -> this.patternDib != null && this.patternDib.isValid() ? this.patternDib : this.pattern16, "bmpData", this::getBMPData);
        }
    }

    public static class WmfSetStretchBltMode
    implements HwmfRecord {
        protected StretchBltMode stretchBltMode;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setStretchBltMode;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.stretchBltMode = StretchBltMode.valueOf(leis.readUShort());
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public StretchBltMode getStretchBltMode() {
            return this.stretchBltMode;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("stretchBltMode", this::getStretchBltMode);
        }

        public static enum StretchBltMode {
            BLACKONWHITE(1),
            WHITEONBLACK(2),
            COLORONCOLOR(3),
            HALFTONE(4);

            public final int flag;

            private StretchBltMode(int flag) {
                this.flag = flag;
            }

            public static StretchBltMode valueOf(int flag) {
                for (StretchBltMode bs : StretchBltMode.values()) {
                    if (bs.flag != flag) continue;
                    return bs;
                }
                return null;
            }
        }
    }

    public static class WmfSetRop2
    implements HwmfRecord {
        protected HwmfBinaryRasterOp drawMode;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setRop2;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.drawMode = HwmfBinaryRasterOp.valueOf(leis.readUShort());
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            HwmfDrawProperties prop = ctx.getProperties();
            prop.setRasterOp2(this.drawMode);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public HwmfBinaryRasterOp getDrawMode() {
            return this.drawMode;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("drawMode", this::getDrawMode);
        }
    }

    public static class WmfSetMapperFlags
    implements HwmfRecord {
        private long mapperValues;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setMapperFlags;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.mapperValues = leis.readUInt();
            return 4;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("mapperValues", () -> this.mapperValues);
        }
    }

    public static class WmfSetMapMode
    implements HwmfRecord {
        protected HwmfMapMode mapMode;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setMapMode;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.mapMode = HwmfMapMode.valueOf(leis.readUShort());
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.getProperties().setMapMode(this.mapMode);
            ctx.updateWindowMapMode();
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public HwmfMapMode getMapMode() {
            return this.mapMode;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("mapMode", this::getMapMode);
        }
    }

    public static class WmfSetLayout
    implements HwmfRecord {
        private int layout;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setLayout;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.layout = leis.readUShort();
            leis.readShort();
            return 4;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("layout", () -> this.layout);
        }
    }

    public static class WmfSetBkMode
    implements HwmfRecord {
        protected HwmfBkMode bkMode;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setBkMode;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.bkMode = HwmfBkMode.valueOf(leis.readUShort());
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.getProperties().setBkMode(this.bkMode);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public HwmfBkMode getBkMode() {
            return this.bkMode;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("bkMode", this::getBkMode);
        }

        public static enum HwmfBkMode {
            TRANSPARENT(1),
            OPAQUE(2);

            final int flag;

            private HwmfBkMode(int flag) {
                this.flag = flag;
            }

            public static HwmfBkMode valueOf(int flag) {
                for (HwmfBkMode bs : HwmfBkMode.values()) {
                    if (bs.flag != flag) continue;
                    return bs;
                }
                return null;
            }
        }
    }

    public static class WmfSetBkColor
    implements HwmfRecord {
        protected final HwmfColorRef colorRef = new HwmfColorRef();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setBkColor;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return this.colorRef.init(leis);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.getProperties().setBackgroundColor(this.colorRef);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public HwmfColorRef getColorRef() {
            return this.colorRef;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("colorRef", this::getColorRef);
        }
    }

    public static class WmfRestoreDc
    implements HwmfRecord {
        protected int nSavedDC;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.restoreDc;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.nSavedDC = leis.readShort();
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.restoreProperties(this.nSavedDC);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public int getNSavedDC() {
            return this.nSavedDC;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("nSavedDC", this::getNSavedDC);
        }
    }

    public static class WmfSetRelabs
    implements HwmfRecord {
        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setRelabs;
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

    public static class WmfSaveDc
    implements HwmfRecord {
        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.saveDc;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return 0;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.saveProperties();
        }

        public String toString() {
            return "{}";
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return null;
        }
    }
}

