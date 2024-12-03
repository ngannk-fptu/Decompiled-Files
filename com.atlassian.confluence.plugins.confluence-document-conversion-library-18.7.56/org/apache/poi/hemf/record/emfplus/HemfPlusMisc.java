/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emfplus;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hemf.draw.HemfDrawProperties;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfFill;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emfplus.HemfPlusDraw;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecord;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecordType;
import org.apache.poi.hwmf.record.HwmfRegionMode;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInputStream;

public class HemfPlusMisc {

    public static class EmfPlusSetRenderingOrigin
    implements HemfPlusRecord {
        int flags;
        Point2D origin = new Point2D.Double();

        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.setRenderingOrigin;
        }

        @Override
        public int getFlags() {
            return this.flags;
        }

        public Point2D getOrigin() {
            return this.origin;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
            this.flags = flags;
            int x = leis.readInt();
            int y = leis.readInt();
            this.origin.setLocation(x, y);
            return 8L;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", this::getFlags, "origin", this::getOrigin);
        }
    }

    public static class EmfPlusRestore
    extends EmfPlusSave {
        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.restore;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.restorePlusProperties(this.getStackIndex());
        }
    }

    public static class EmfPlusSave
    implements HemfPlusRecord {
        private int flags;
        private int stackIndex;

        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.save;
        }

        @Override
        public int getFlags() {
            return this.flags;
        }

        public int getStackIndex() {
            return this.stackIndex;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
            this.flags = flags;
            this.stackIndex = leis.readInt();
            return 4L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.savePlusProperties(this.getStackIndex());
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", this::getFlags, "stackIndex", this::getStackIndex);
        }
    }

    public static class EmfPlusResetClip
    extends EmfPlusFlagOnly {
    }

    public static class EmfPlusSetClipRect
    implements HemfPlusRecord {
        private static final BitField COMBINE_MODE = BitFieldFactory.getInstance(3840);
        private static final int[] FLAGS_MASK = new int[]{3840};
        private static final String[] FLAGS_NAMES = new String[]{"COMBINE_MODE"};
        private int flags;
        private final Rectangle2D clipRect = new Rectangle2D.Double();

        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.setClipRect;
        }

        @Override
        public int getFlags() {
            return this.flags;
        }

        public CombineMode getCombineMode() {
            return CombineMode.valueOf(COMBINE_MODE.getValue(this.getFlags()));
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
            this.flags = flags;
            return HemfPlusDraw.readRectF(leis, this.clipRect);
        }

        public Rectangle2D getClipRect() {
            return this.clipRect;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", GenericRecordUtil.getBitsAsString(this::getFlags, FLAGS_MASK, FLAGS_NAMES), "clipRect", this::getClipRect);
        }
    }

    public static class EmfPlusSetClipPath
    extends EmfPlusFlagOnly
    implements EmfPlusObjectId {
        private static final BitField COMBINE_MODE = BitFieldFactory.getInstance(3840);

        public CombineMode getCombineMode() {
            return CombineMode.valueOf(COMBINE_MODE.getValue(this.getFlags()));
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            ctx.applyPlusObjectTableEntry(this.getObjectId());
            Path2D clip = prop.getPath();
            ctx.setClip(clip, clip == null ? HwmfRegionMode.RGN_COPY : this.getCombineMode().regionMode, false);
        }
    }

    public static class EmfPlusSetClipRegion
    extends EmfPlusSetClipPath {
    }

    public static class EmfPlusSetPageTransform
    implements HemfPlusRecord {
        private int flags;
        private double pageScale;

        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.setPageTransform;
        }

        @Override
        public int getFlags() {
            return this.flags;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
            this.flags = flags;
            this.pageScale = leis.readFloat();
            return 4L;
        }

        public double getPageScale() {
            return this.pageScale;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", this::getFlags, "pageScale", this::getPageScale);
        }
    }

    public static class EmfPlusMultiplyWorldTransform
    extends EmfPlusSetWorldTransform {
        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.multiplyWorldTransform;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            AffineTransform tx = ctx.getInitTransform();
            tx.concatenate(this.getMatrixData());
            ctx.setTransform(tx);
        }
    }

    public static class EmfPlusSetWorldTransform
    implements HemfPlusRecord {
        private int flags;
        private final AffineTransform matrixData = new AffineTransform();

        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.setWorldTransform;
        }

        @Override
        public int getFlags() {
            return this.flags;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
            this.flags = flags;
            return HemfFill.readXForm(leis, this.matrixData);
        }

        public AffineTransform getMatrixData() {
            return this.matrixData;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            prop.clearTransform();
            prop.addLeftTransform(this.getMatrixData());
            ctx.updateWindowMapMode();
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", this::getFlags, "matrixData", this::getMatrixData);
        }
    }

    public static class EmfPlusResetWorldTransform
    extends EmfPlusFlagOnly {
        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            prop.clearTransform();
            ctx.updateWindowMapMode();
        }
    }

    public static class EmfPlusSetTextRenderingHint
    extends EmfPlusFlagOnly {
    }

    public static class EmfPlusGetDC
    extends EmfPlusFlagOnly {
        @Override
        public void draw(HemfGraphics ctx) {
            ctx.setRenderState(HemfGraphics.EmfRenderState.EMF_DCONTEXT);
        }

        @Override
        public void calcBounds(HemfRecord.RenderBounds holder) {
            holder.setState(HemfGraphics.EmfRenderState.EMF_DCONTEXT);
        }
    }

    public static class EmfPlusSetInterpolationMode
    extends EmfPlusFlagOnly {
    }

    public static class EmfPlusSetCompositingQuality
    extends EmfPlusFlagOnly {
    }

    public static class EmfPlusSetCompositingMode
    extends EmfPlusFlagOnly {
    }

    public static class EmfPlusSetAntiAliasMode
    extends EmfPlusFlagOnly {
    }

    public static class EmfPlusSetPixelOffsetMode
    extends EmfPlusFlagOnly {
    }

    public static class EmfPlusEOF
    extends EmfPlusFlagOnly {
    }

    public static abstract class EmfPlusFlagOnly
    implements HemfPlusRecord {
        private int flags;
        private HemfPlusRecordType recordType;
        private static final int[] FLAGS_MASK = new int[]{3840};
        private static final String[] FLAGS_NAMES = new String[]{"COMBINE_MODE"};

        @Override
        public int getFlags() {
            return this.flags;
        }

        @Override
        public final HemfPlusRecordType getEmfPlusRecordType() {
            return this.recordType;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
            this.flags = flags;
            assert (dataSize == 0L);
            this.recordType = HemfPlusRecordType.getById(recordId);
            return 0L;
        }

        @Override
        public HemfPlusRecordType getGenericRecordType() {
            return this.getEmfPlusRecordType();
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", GenericRecordUtil.getBitsAsString(this::getFlags, FLAGS_MASK, FLAGS_NAMES));
        }
    }

    public static enum CombineMode {
        REPLACE(0, HwmfRegionMode.RGN_COPY),
        INTERSECT(1, HwmfRegionMode.RGN_AND),
        UNION(2, HwmfRegionMode.RGN_OR),
        XOR(3, HwmfRegionMode.RGN_XOR),
        EXCLUDE(4, HwmfRegionMode.RGN_DIFF),
        COMPLEMENT(5, HwmfRegionMode.RGN_COMPLEMENT);

        public final int id;
        public final HwmfRegionMode regionMode;

        private CombineMode(int id, HwmfRegionMode regionMode) {
            this.id = id;
            this.regionMode = regionMode;
        }

        public static CombineMode valueOf(int id) {
            for (CombineMode wrt : CombineMode.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }

    public static interface EmfPlusObjectId {
        public static final BitField OBJECT_ID = BitFieldFactory.getInstance(255);

        public int getFlags();

        default public int getObjectId() {
            return OBJECT_ID.getValue(this.getFlags());
        }
    }
}

