/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emf;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hemf.draw.HemfDrawProperties;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfDraw;
import org.apache.poi.hemf.record.emf.HemfFill;
import org.apache.poi.hemf.record.emf.HemfHeader;
import org.apache.poi.hemf.record.emf.HemfPenStyle;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emf.HemfRecordType;
import org.apache.poi.hemf.record.emf.HemfRecordWithoutProperties;
import org.apache.poi.hwmf.draw.HwmfDrawProperties;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfBinaryRasterOp;
import org.apache.poi.hwmf.record.HwmfBitmapDib;
import org.apache.poi.hwmf.record.HwmfBrushStyle;
import org.apache.poi.hwmf.record.HwmfColorRef;
import org.apache.poi.hwmf.record.HwmfFill;
import org.apache.poi.hwmf.record.HwmfHatchStyle;
import org.apache.poi.hwmf.record.HwmfMapMode;
import org.apache.poi.hwmf.record.HwmfMisc;
import org.apache.poi.hwmf.record.HwmfObjectTableEntry;
import org.apache.poi.hwmf.record.HwmfPalette;
import org.apache.poi.hwmf.record.HwmfPenStyle;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInputStream;

public class HemfMisc {

    public static class EmfCreateMonoBrush
    implements HemfRecord,
    HwmfObjectTableEntry {
        protected int penIndex;
        protected HwmfFill.ColorUsage colorUsage;
        protected final HwmfBitmapDib bitmap = new HwmfBitmapDib();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.createMonoBrush;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            int startIdx = leis.getReadIndex();
            this.penIndex = (int)leis.readUInt();
            this.colorUsage = HwmfFill.ColorUsage.valueOf((int)leis.readUInt());
            int offBmi = (int)leis.readUInt();
            int cbBmi = (int)leis.readUInt();
            int offBits = (int)leis.readUInt();
            int cbBits = Math.toIntExact(leis.readUInt());
            int size = 24;
            size = Math.toIntExact((long)size + HemfFill.readBitmap(leis, this.bitmap, startIdx, offBmi, cbBmi, offBits, cbBits));
            return size;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.addObjectTableEntry(this, this.penIndex);
        }

        @Override
        public void applyObject(HwmfGraphics ctx) {
            if (!this.bitmap.isValid()) {
                return;
            }
            HwmfDrawProperties props = ctx.getProperties();
            props.setBrushStyle(HwmfBrushStyle.BS_PATTERN);
            props.setBrushBitmap(this.bitmap.getImage());
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public int getPenIndex() {
            return this.penIndex;
        }

        public HwmfFill.ColorUsage getColorUsage() {
            return this.colorUsage;
        }

        public HwmfBitmapDib getBitmap() {
            return this.bitmap;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("penIndex", this::getPenIndex, "colorUsage", this::getColorUsage, "bitmap", this::getBitmap);
        }
    }

    public static class EmfModifyWorldTransform
    implements HemfRecord {
        protected final AffineTransform xForm = new AffineTransform();
        protected HemfModifyWorldTransformMode modifyWorldTransformMode;
        protected HemfHeader header;

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.modifyWorldTransform;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long size = HemfFill.readXForm(leis, this.xForm);
            this.modifyWorldTransformMode = HemfModifyWorldTransformMode.valueOf((int)leis.readUInt());
            return size + 4L;
        }

        @Override
        public void setHeader(HemfHeader header) {
            this.header = header;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            if (this.modifyWorldTransformMode == null) {
                return;
            }
            HemfDrawProperties prop = ctx.getProperties();
            switch (this.modifyWorldTransformMode) {
                case MWT_LEFTMULTIPLY: {
                    prop.addLeftTransform(this.xForm);
                    break;
                }
                case MWT_RIGHTMULTIPLY: {
                    prop.addRightTransform(this.xForm);
                    break;
                }
                case MWT_IDENTITY: {
                    prop.clearTransform();
                    break;
                }
                default: {
                    prop.clearTransform();
                    prop.addLeftTransform(this.xForm);
                }
            }
            ctx.updateWindowMapMode();
            ctx.getProperties().setLocation(0.0, 0.0);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public AffineTransform getXForm() {
            return this.xForm;
        }

        public HemfModifyWorldTransformMode getModifyWorldTransformMode() {
            return this.modifyWorldTransformMode;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("xForm", this::getXForm, "modifyWorldTransformMode", this::getModifyWorldTransformMode);
        }
    }

    public static class EmfSetWorldTransform
    implements HemfRecord {
        protected final AffineTransform xForm = new AffineTransform();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setWorldTransform;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfFill.readXForm(leis, this.xForm);
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            prop.clearTransform();
            prop.addLeftTransform(this.xForm);
            ctx.updateWindowMapMode();
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public AffineTransform getXForm() {
            return this.xForm;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("xForm", this::getXForm);
        }
    }

    public static class EmfSetBrushOrgEx
    implements HemfRecord {
        protected final Point2D origin = new Point2D.Double();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setBrushOrgEx;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfDraw.readPointL(leis, this.origin);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Point2D getOrigin() {
            return this.origin;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("origin", this::getOrigin);
        }
    }

    public static class EmfSetMiterLimit
    implements HemfRecord {
        protected int miterLimit;

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setMiterLimit;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.miterLimit = (int)leis.readUInt();
            return 4L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.getProperties().setPenMiterLimit(this.miterLimit);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public int getMiterLimit() {
            return this.miterLimit;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("miterLimit", this::getMiterLimit);
        }
    }

    public static class EmfExtCreatePen
    extends EmfCreatePen {
        protected HwmfBrushStyle brushStyle;
        protected HwmfHatchStyle hatchStyle;
        protected final HwmfBitmapDib bitmap = new HwmfBitmapDib();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.extCreatePen;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            int startIdx = leis.getReadIndex();
            this.penIndex = (int)leis.readUInt();
            int offBmi = (int)leis.readUInt();
            int cbBmi = (int)leis.readUInt();
            int offBits = (int)leis.readUInt();
            int cbBits = (int)leis.readUInt();
            HemfPenStyle emfPS = HemfPenStyle.valueOf((int)leis.readUInt());
            this.penStyle = emfPS;
            long width = leis.readUInt();
            this.dimension.setSize(width, 0.0);
            int size = 28;
            this.brushStyle = HwmfBrushStyle.valueOf((int)leis.readUInt());
            size += 4;
            size += this.colorRef.init(leis);
            this.hatchStyle = HwmfHatchStyle.valueOf(leis.readInt());
            size += 4;
            int numStyleEntries = (int)leis.readUInt();
            size += 4;
            assert (numStyleEntries == 0 || this.penStyle.getLineDash() == HwmfPenStyle.HwmfLineDash.USERSTYLE);
            float[] dashPattern = new float[numStyleEntries];
            for (int i = 0; i < numStyleEntries; ++i) {
                dashPattern[i] = (int)leis.readUInt();
            }
            if (this.penStyle.getLineDash() == HwmfPenStyle.HwmfLineDash.USERSTYLE) {
                emfPS.setLineDashes(dashPattern);
            }
            size = Math.addExact(size, numStyleEntries * 4);
            size = Math.toIntExact((long)size + HemfFill.readBitmap(leis, this.bitmap, startIdx, offBmi, cbBmi, offBits, cbBits));
            return size;
        }

        @Override
        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public HwmfBrushStyle getBrushStyle() {
            return this.brushStyle;
        }

        public HwmfHatchStyle getHatchStyle() {
            return this.hatchStyle;
        }

        public HwmfBitmapDib getBitmap() {
            return this.bitmap;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "brushStyle", this::getBrushStyle, "hatchStyle", this::getHatchStyle, "bitmap", this::getBitmap);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfCreatePen
    extends HwmfMisc.WmfCreatePenIndirect
    implements HemfRecord {
        protected int penIndex;

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.createPen;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.penIndex = (int)leis.readUInt();
            this.penStyle = HwmfPenStyle.valueOf((int)leis.readUInt());
            int widthX = leis.readInt();
            int widthY = leis.readInt();
            this.dimension.setSize(widthX, widthY);
            int size = this.colorRef.init(leis);
            return (long)size + 16L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.addObjectTableEntry(this, this.penIndex);
        }

        @Override
        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public int getPenIndex() {
            return this.penIndex;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "penIndex", this::getPenIndex);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfDeleteObject
    extends HwmfMisc.WmfDeleteObject
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.deleteobject;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.objectIndex = (int)leis.readUInt();
            return 4L;
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfCreateDibPatternBrushPt
    extends HwmfMisc.WmfDibCreatePatternBrush
    implements HemfRecord {
        protected int brushIdx;

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.createDibPatternBrushPt;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            int startIdx = leis.getReadIndex();
            this.style = HwmfBrushStyle.BS_DIBPATTERNPT;
            this.brushIdx = (int)leis.readUInt();
            this.colorUsage = HwmfFill.ColorUsage.valueOf((int)leis.readUInt());
            int offBmi = leis.readInt();
            int cbBmi = leis.readInt();
            int offBits = leis.readInt();
            int cbBits = leis.readInt();
            int size = 24;
            this.patternDib = new HwmfBitmapDib();
            size = Math.toIntExact((long)size + HemfFill.readBitmap(leis, this.patternDib, startIdx, offBmi, cbBmi, offBits, cbBits));
            return size;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.addObjectTableEntry(this, this.brushIdx);
        }

        public int getBrushIdx() {
            return this.brushIdx;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "brushIdx", this::getBrushIdx);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfCreateBrushIndirect
    extends HwmfMisc.WmfCreateBrushIndirect
    implements HemfRecord {
        private int brushIdx;

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.createBrushIndirect;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.brushIdx = (int)leis.readUInt();
            this.brushStyle = HwmfBrushStyle.valueOf((int)leis.readUInt());
            this.colorRef = new HwmfColorRef();
            int size = this.colorRef.init(leis);
            this.brushHatch = HwmfHatchStyle.valueOf((int)leis.readUInt());
            return (long)size + 12L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.addObjectTableEntry(this, this.brushIdx);
        }

        public int getBrushIdx() {
            return this.brushIdx;
        }

        @Override
        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "brushIdx", this::getBrushIdx);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSetStretchBltMode
    extends HwmfMisc.WmfSetStretchBltMode
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setStretchBltMode;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.stretchBltMode = HwmfMisc.WmfSetStretchBltMode.StretchBltMode.valueOf((int)leis.readUInt());
            return 4L;
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSetRop2
    extends HwmfMisc.WmfSetRop2
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setRop2;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.drawMode = HwmfBinaryRasterOp.valueOf((int)leis.readUInt());
            return 4L;
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSetMapMode
    extends HwmfMisc.WmfSetMapMode
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setMapMode;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.mapMode = HwmfMapMode.valueOf((int)leis.readUInt());
            return 4L;
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSetMapperFlags
    extends HwmfMisc.WmfSetMapperFlags
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setMapperFlags;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return super.init(leis, recordSize, (int)recordId);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSetBkMode
    extends HwmfMisc.WmfSetBkMode
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setBkMode;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.bkMode = HwmfMisc.WmfSetBkMode.HwmfBkMode.valueOf((int)leis.readUInt());
            return 4L;
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSetBkColor
    extends HwmfMisc.WmfSetBkColor
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setBkColor;
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

    public static class EmfRestoreDc
    extends HwmfMisc.WmfRestoreDc
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.restoreDc;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.nSavedDC = leis.readInt();
            return 4L;
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSaveDc
    extends HwmfMisc.WmfSaveDc
    implements HemfRecordWithoutProperties {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.saveDc;
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

    public static class EmfEof
    implements HemfRecord {
        protected final List<HwmfPalette.PaletteEntry> palette = new ArrayList<HwmfPalette.PaletteEntry>();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.eof;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            int startIdx = leis.getReadIndex();
            int nPalEntries = (int)leis.readUInt();
            int offPalEntries = (int)leis.readUInt();
            int size = 8;
            if (nPalEntries > 0 && offPalEntries > 0) {
                int undefinedSpace1 = offPalEntries - (size + 8);
                assert (undefinedSpace1 >= 0);
                leis.skipFully(undefinedSpace1);
                size += undefinedSpace1;
                for (int i = 0; i < nPalEntries; ++i) {
                    HwmfPalette.PaletteEntry pe = new HwmfPalette.PaletteEntry();
                    size += pe.init(leis);
                }
                int undefinedSpace2 = (int)(recordSize - (long)size - 4L);
                assert (undefinedSpace2 >= 0);
                leis.skipFully(undefinedSpace2);
                size += undefinedSpace2;
            }
            long sizeLast = leis.readUInt();
            assert (recordSize == (long)(size += 4));
            return size;
        }

        public List<HwmfPalette.PaletteEntry> getPalette() {
            return this.palette;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("palette", this::getPalette);
        }
    }

    public static enum HemfModifyWorldTransformMode {
        MWT_IDENTITY(1),
        MWT_LEFTMULTIPLY(2),
        MWT_RIGHTMULTIPLY(3),
        MWT_SET(4);

        public final int id;

        private HemfModifyWorldTransformMode(int id) {
            this.id = id;
        }

        public static HemfModifyWorldTransformMode valueOf(int id) {
            for (HemfModifyWorldTransformMode wrt : HemfModifyWorldTransformMode.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }
}

