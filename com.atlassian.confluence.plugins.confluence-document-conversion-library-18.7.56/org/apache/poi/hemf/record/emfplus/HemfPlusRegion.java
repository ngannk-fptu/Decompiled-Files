/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emfplus;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hemf.draw.HemfDrawProperties;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emfplus.HemfPlusDraw;
import org.apache.poi.hemf.record.emfplus.HemfPlusHeader;
import org.apache.poi.hemf.record.emfplus.HemfPlusObject;
import org.apache.poi.hemf.record.emfplus.HemfPlusPath;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInputStream;

public class HemfPlusRegion {
    private static long readNode(LittleEndianInputStream leis, Consumer<EmfPlusRegionNodeData> con) throws IOException {
        EmfPlusRegionNodeDataType type = EmfPlusRegionNodeDataType.valueOf(leis.readInt());
        assert (type != null);
        EmfPlusRegionNodeData nd = type.constructor.get();
        con.accept(nd);
        nd.setNodeType(type);
        return 4L + nd.init(leis);
    }

    public static class EmfPlusRegionNode
    implements EmfPlusRegionNodeData {
        private EmfPlusRegionNodeData left;
        private EmfPlusRegionNodeData right;
        private EmfPlusRegionNodeDataType nodeType;

        @Override
        public long init(LittleEndianInputStream leis) throws IOException {
            long size = HemfPlusRegion.readNode(leis, this::setLeft);
            return size += HemfPlusRegion.readNode(leis, this::setRight);
        }

        private void setLeft(EmfPlusRegionNodeData left) {
            this.left = left;
        }

        private void setRight(EmfPlusRegionNodeData right) {
            this.right = right;
        }

        public EmfPlusRegionNodeData getLeft() {
            return this.left;
        }

        public EmfPlusRegionNodeData getRight() {
            return this.right;
        }

        public EmfPlusRegionNodeDataType getNodeType() {
            return this.nodeType;
        }

        @Override
        public void setNodeType(EmfPlusRegionNodeDataType nodeType) {
            this.nodeType = nodeType;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("nodeType", this::getNodeType, "left", this::getLeft, "right", this::getRight);
        }

        @Override
        public Shape getShape() {
            boolean com = this.nodeType == EmfPlusRegionNodeDataType.COMPLEMENT;
            Shape leftShape = (com ? this.right : this.left).getShape();
            Shape rightShape = (com ? this.left : this.right).getShape();
            if (leftShape == null) {
                return rightShape;
            }
            if (rightShape == null) {
                return leftShape;
            }
            Area leftArea = new Area(leftShape);
            Area rightArea = new Area(rightShape);
            assert (this.nodeType.operation != null);
            this.nodeType.operation.accept(leftArea, rightArea);
            return leftArea;
        }

        public EmfPlusRegionNodeDataType getGenericRecordType() {
            return this.nodeType;
        }
    }

    public static class EmfPlusRegionRect
    implements EmfPlusRegionNodeData {
        private final Rectangle2D rect = new Rectangle2D.Double();

        @Override
        public long init(LittleEndianInputStream leis) {
            return HemfPlusDraw.readRectF(leis, this.rect);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("rect", () -> this.rect);
        }

        @Override
        public Shape getShape() {
            return this.rect;
        }

        public EmfPlusRegionNodeDataType getGenericRecordType() {
            return EmfPlusRegionNodeDataType.RECT;
        }
    }

    public static class EmfPlusRegionEmpty
    implements EmfPlusRegionNodeData {
        @Override
        public long init(LittleEndianInputStream leis) throws IOException {
            return 0L;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return null;
        }

        @Override
        public Shape getShape() {
            return new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);
        }

        public EmfPlusRegionNodeDataType getGenericRecordType() {
            return EmfPlusRegionNodeDataType.EMPTY;
        }
    }

    public static class EmfPlusRegionInfinite
    implements EmfPlusRegionNodeData {
        @Override
        public long init(LittleEndianInputStream leis) throws IOException {
            return 0L;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return null;
        }

        @Override
        public Shape getShape() {
            return null;
        }

        public EmfPlusRegionNodeDataType getGenericRecordType() {
            return EmfPlusRegionNodeDataType.INFINITE;
        }
    }

    public static class EmfPlusRegionPath
    extends HemfPlusPath.EmfPlusPath
    implements EmfPlusRegionNodeData {
        @Override
        public long init(LittleEndianInputStream leis) throws IOException {
            int dataSize = leis.readInt();
            return super.init(leis, dataSize, HemfPlusObject.EmfPlusObjectType.PATH, 0) + 4L;
        }

        @Override
        public Shape getShape() {
            return this.getPath();
        }

        @Override
        public EmfPlusRegionNodeDataType getGenericRecordType() {
            return EmfPlusRegionNodeDataType.PATH;
        }
    }

    public static interface EmfPlusRegionNodeData
    extends GenericRecord {
        public long init(LittleEndianInputStream var1) throws IOException;

        public Shape getShape();

        default public void setNodeType(EmfPlusRegionNodeDataType type) {
        }
    }

    public static class EmfPlusRegion
    implements HemfPlusObject.EmfPlusObjectData {
        private final HemfPlusHeader.EmfPlusGraphicsVersion graphicsVersion = new HemfPlusHeader.EmfPlusGraphicsVersion();
        private EmfPlusRegionNodeData regionNode;

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, HemfPlusObject.EmfPlusObjectType objectType, int flags) throws IOException {
            long size = this.graphicsVersion.init(leis);
            int nodeCount = leis.readInt();
            size += 4L;
            return size += HemfPlusRegion.readNode(leis, this::setRegionNode);
        }

        @Override
        public void applyObject(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            HemfDrawProperties prop = ctx.getProperties();
            Shape shape = this.regionNode.getShape();
            prop.setPath(shape == null ? null : new Path2D.Double(shape));
        }

        @Override
        public HemfPlusHeader.EmfPlusGraphicsVersion getGraphicsVersion() {
            return this.graphicsVersion;
        }

        public HemfPlusObject.EmfPlusObjectType getGenericRecordType() {
            return HemfPlusObject.EmfPlusObjectType.REGION;
        }

        private void setRegionNode(EmfPlusRegionNodeData regionNode) {
            this.regionNode = regionNode;
        }

        public EmfPlusRegionNodeData getRegionNode() {
            return this.regionNode;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("graphicsVersion", this::getGraphicsVersion, "regionNode", this::getRegionNode);
        }
    }

    public static enum EmfPlusRegionNodeDataType {
        AND(1, EmfPlusRegionNode::new, Area::intersect),
        OR(2, EmfPlusRegionNode::new, Area::add),
        XOR(3, EmfPlusRegionNode::new, Area::exclusiveOr),
        EXCLUDE(4, EmfPlusRegionNode::new, Area::subtract),
        COMPLEMENT(5, EmfPlusRegionNode::new, Area::subtract),
        RECT(0x10000000, EmfPlusRegionRect::new, null),
        PATH(0x10000001, EmfPlusRegionPath::new, null),
        EMPTY(0x10000002, EmfPlusRegionEmpty::new, null),
        INFINITE(0x10000003, EmfPlusRegionInfinite::new, null);

        public final int id;
        public final Supplier<EmfPlusRegionNodeData> constructor;
        public final BiConsumer<Area, Area> operation;

        private EmfPlusRegionNodeDataType(int id, Supplier<EmfPlusRegionNodeData> constructor, BiConsumer<Area, Area> operation) {
            this.id = id;
            this.constructor = constructor;
            this.operation = operation;
        }

        public static EmfPlusRegionNodeDataType valueOf(int id) {
            for (EmfPlusRegionNodeDataType wrt : EmfPlusRegionNodeDataType.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }
}

