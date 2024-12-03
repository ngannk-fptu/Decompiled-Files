/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emfplus;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hemf.draw.HemfDrawProperties;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emfplus.HemfPlusDraw;
import org.apache.poi.hemf.record.emfplus.HemfPlusHeader;
import org.apache.poi.hemf.record.emfplus.HemfPlusObject;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInputStream;

public class HemfPlusPath {

    public static class EmfPlusPath
    implements HemfPlusObject.EmfPlusObjectData,
    HemfPlusDraw.EmfPlusCompressed,
    HemfPlusDraw.EmfPlusRelativePosition {
        private static final BitField RLE_COMPRESSED = BitFieldFactory.getInstance(4096);
        private static final BitField POINT_TYPE_DASHED = BitFieldFactory.getInstance(16);
        private static final BitField POINT_TYPE_MARKER = BitFieldFactory.getInstance(32);
        private static final BitField POINT_TYPE_CLOSE = BitFieldFactory.getInstance(128);
        private static final BitField POINT_TYPE_ENUM = BitFieldFactory.getInstance(15);
        private static final BitField POINT_RLE_BEZIER = BitFieldFactory.getInstance(128);
        private static final BitField POINT_RLE_COUNT = BitFieldFactory.getInstance(63);
        private static final int[] FLAGS_MASKS = new int[]{2048, 4096, 16384};
        private static final String[] FLAGS_NAMES = new String[]{"RELATIVE_POSITION", "RLE_COMPRESSED", "FORMAT_COMPRESSED"};
        private static final int[] TYPE_MASKS = new int[]{16, 32, 128};
        private static final String[] TYPE_NAMES = new String[]{"DASHED", "MARKER", "CLOSE"};
        private final HemfPlusHeader.EmfPlusGraphicsVersion graphicsVersion = new HemfPlusHeader.EmfPlusGraphicsVersion();
        private int pointFlags;
        private Point2D[] pathPoints;
        private byte[] pointTypes;

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, HemfPlusObject.EmfPlusObjectType objectType, int flags) throws IOException {
            long size = this.graphicsVersion.init(leis);
            int pointCount = leis.readInt();
            this.pointFlags = leis.readShort();
            leis.skipFully(2);
            size += 8L;
            BiFunction<LittleEndianInputStream, Point2D, Integer> readPoint = this.isRelativePosition() ? HemfPlusDraw::readPointR : (this.isCompressed() ? HemfPlusDraw::readPointS : HemfPlusDraw::readPointF);
            this.pathPoints = new Point2D[pointCount];
            for (int i = 0; i < pointCount; ++i) {
                this.pathPoints[i] = new Point2D.Double();
                size += (long)readPoint.apply(leis, this.pathPoints[i]).intValue();
            }
            this.pointTypes = new byte[pointCount];
            boolean isRLE = RLE_COMPRESSED.isSet(this.pointFlags);
            if (isRLE) {
                int i = 0;
                while (i < pointCount) {
                    int rleCount = POINT_RLE_COUNT.getValue(leis.readByte());
                    Arrays.fill(this.pointTypes, pointCount, pointCount + rleCount, leis.readByte());
                    i += rleCount;
                    size += 2L;
                }
            } else {
                leis.readFully(this.pointTypes);
                size += (long)pointCount;
            }
            int padding = (int)((4L - size % 4L) % 4L);
            leis.skipFully(padding);
            return size += (long)padding;
        }

        @Override
        public HemfPlusHeader.EmfPlusGraphicsVersion getGraphicsVersion() {
            return this.graphicsVersion;
        }

        public boolean isPointDashed(int index) {
            return POINT_TYPE_DASHED.isSet(this.pointTypes[index]);
        }

        public boolean isPointMarker(int index) {
            return POINT_TYPE_MARKER.isSet(this.pointTypes[index]);
        }

        public boolean isPointClosed(int index) {
            return POINT_TYPE_CLOSE.isSet(this.pointTypes[index]);
        }

        public EmfPlusPathPointType getPointType(int index) {
            return EmfPlusPathPointType.values()[POINT_TYPE_ENUM.getValue(this.pointTypes[index])];
        }

        @Override
        public int getFlags() {
            return this.pointFlags;
        }

        public Point2D getPoint(int index) {
            return this.pathPoints[index];
        }

        @Override
        public void applyObject(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            HemfDrawProperties prop = ctx.getProperties();
            prop.setPath(this.getPath());
        }

        public Path2D getPath() {
            return this.getPath(1);
        }

        public Path2D getPath(int windingRule) {
            Path2D.Double path = new Path2D.Double(windingRule);
            for (int idx = 0; idx < this.pathPoints.length; ++idx) {
                Point2D p1 = this.pathPoints[idx];
                switch (this.getPointType(idx)) {
                    case START: {
                        ((Path2D)path).moveTo(p1.getX(), p1.getY());
                        break;
                    }
                    case LINE: {
                        ((Path2D)path).lineTo(p1.getX(), p1.getY());
                        break;
                    }
                    case BEZIER: {
                        Point2D p2 = this.pathPoints[++idx];
                        Point2D p3 = this.pathPoints[++idx];
                        ((Path2D)path).curveTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
                        break;
                    }
                }
                if (!this.isPointClosed(idx)) continue;
                path.closePath();
            }
            return path;
        }

        public Enum getGenericRecordType() {
            return HemfPlusObject.EmfPlusObjectType.PATH;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("graphicsVersion", this::getGraphicsVersion, "flags", GenericRecordUtil.getBitsAsString(this::getFlags, FLAGS_MASKS, FLAGS_NAMES), "points", this::getGenericPoints);
        }

        private List<GenericRecord> getGenericPoints() {
            return IntStream.range(0, this.pathPoints.length).mapToObj(this::getGenericPoint).collect(Collectors.toList());
        }

        private GenericRecord getGenericPoint(int idx) {
            return () -> GenericRecordUtil.getGenericProperties("flags", GenericRecordUtil.getBitsAsString(() -> this.pointTypes[idx], TYPE_MASKS, TYPE_NAMES), "type", () -> this.getPointType(idx), "point", () -> this.getPoint(idx));
        }
    }

    public static enum EmfPlusPathPointType {
        START,
        LINE,
        UNUSED,
        BEZIER;

    }
}

