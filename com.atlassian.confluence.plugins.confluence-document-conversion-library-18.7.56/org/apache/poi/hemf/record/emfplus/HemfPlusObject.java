/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emfplus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emfplus.HemfPlusBrush;
import org.apache.poi.hemf.record.emfplus.HemfPlusFont;
import org.apache.poi.hemf.record.emfplus.HemfPlusHeader;
import org.apache.poi.hemf.record.emfplus.HemfPlusImage;
import org.apache.poi.hemf.record.emfplus.HemfPlusMisc;
import org.apache.poi.hemf.record.emfplus.HemfPlusPath;
import org.apache.poi.hemf.record.emfplus.HemfPlusPen;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecord;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecordType;
import org.apache.poi.hemf.record.emfplus.HemfPlusRegion;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfObjectTableEntry;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInputStream;

public class HemfPlusObject {
    private static final int MAX_OBJECT_SIZE = 50000000;

    public static class EmfPlusUnknownData
    implements EmfPlusObjectData {
        private EmfPlusObjectType objectType;
        private final HemfPlusHeader.EmfPlusGraphicsVersion graphicsVersion = new HemfPlusHeader.EmfPlusGraphicsVersion();
        private byte[] objectDataBytes;

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, EmfPlusObjectType objectType, int flags) throws IOException {
            this.objectType = objectType;
            long size = this.graphicsVersion.init(leis);
            this.objectDataBytes = IOUtils.toByteArray(leis, (int)(dataSize - size), 50000000);
            return dataSize;
        }

        @Override
        public void applyObject(HemfGraphics ctx, List<? extends EmfPlusObjectData> continuedObjectData) {
        }

        @Override
        public HemfPlusHeader.EmfPlusGraphicsVersion getGraphicsVersion() {
            return this.graphicsVersion;
        }

        public EmfPlusObjectType getGenericRecordType() {
            return this.objectType;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("graphicsVersion", this::getGraphicsVersion, "objectDataBytes", () -> this.objectDataBytes);
        }
    }

    public static interface EmfPlusObjectData
    extends GenericRecord {
        public long init(LittleEndianInputStream var1, long var2, EmfPlusObjectType var4, int var5) throws IOException;

        public void applyObject(HemfGraphics var1, List<? extends EmfPlusObjectData> var2);

        public HemfPlusHeader.EmfPlusGraphicsVersion getGraphicsVersion();

        default public boolean isContinuedRecord() {
            HemfPlusHeader.EmfPlusGraphicsVersion gv = this.getGraphicsVersion();
            return gv.getGraphicsVersion() == null || gv.getMetafileSignature() != 900097;
        }
    }

    public static class EmfPlusObject
    implements HemfPlusRecord,
    HemfPlusMisc.EmfPlusObjectId,
    HwmfObjectTableEntry {
        private static final BitField CONTINUABLE = BitFieldFactory.getInstance(32768);
        private static final BitField OBJECT_TYPE = BitFieldFactory.getInstance(32512);
        private static final int[] FLAGS_MASKS = new int[]{32512, 32768};
        private static final String[] FLAGS_NAMES = new String[]{"OBJECT_TYPE", "CONTINUABLE"};
        private int flags;
        private int objectId;
        private EmfPlusObjectData objectData;
        private List<EmfPlusObjectData> continuedObjectData;
        private int totalObjectSize;

        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.object;
        }

        @Override
        public int getFlags() {
            return this.flags;
        }

        public EmfPlusObjectType getObjectType() {
            return EmfPlusObjectType.valueOf(OBJECT_TYPE.getValue(this.flags));
        }

        public <T extends EmfPlusObjectData> T getObjectData() {
            return (T)this.objectData;
        }

        public int getTotalObjectSize() {
            return this.totalObjectSize;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
            this.flags = flags;
            this.objectId = this.getObjectId();
            EmfPlusObjectType objectType = this.getObjectType();
            assert (objectType != null);
            long size = 0L;
            this.totalObjectSize = 0;
            int dataSize2 = (int)dataSize;
            if (CONTINUABLE.isSet(flags)) {
                this.totalObjectSize = leis.readInt();
                size += 4L;
                dataSize2 -= 4;
            }
            this.objectData = objectType.constructor.get();
            return Math.toIntExact(size += this.objectData.init(leis, dataSize2, objectType, flags));
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public void draw(HemfGraphics ctx) {
            if (this.objectData.isContinuedRecord()) {
                HwmfObjectTableEntry entry = ctx.getPlusObjectTableEntry(this.getObjectId());
                if (!(entry instanceof EmfPlusObject)) throw new RuntimeException("can't find previous record for continued record");
                EmfPlusObject other = (EmfPlusObject)entry;
                if (!this.objectData.getClass().isInstance(other.getObjectData())) throw new RuntimeException("can't find previous record for continued record");
                other.linkContinuedObject(this.objectData);
                return;
            } else {
                ctx.addPlusObjectTableEntry(this, this.getObjectId());
            }
        }

        @Override
        public void applyObject(HwmfGraphics ctx) {
            this.objectData.applyObject((HemfGraphics)ctx, this.continuedObjectData);
        }

        void linkContinuedObject(EmfPlusObjectData continueObject) {
            if (this.continuedObjectData == null) {
                this.continuedObjectData = new ArrayList<EmfPlusObjectData>();
            }
            this.continuedObjectData.add(continueObject);
        }

        List<EmfPlusObjectData> getContinuedObject() {
            return this.continuedObjectData;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", GenericRecordUtil.getBitsAsString(this::getFlags, FLAGS_MASKS, FLAGS_NAMES), "objectId", this::getObjectId, "objectData", () -> this.objectData.isContinuedRecord() ? null : this.getObjectData(), "continuedObject", this.objectData::isContinuedRecord, "totalObjectSize", this::getTotalObjectSize);
        }
    }

    public static enum EmfPlusObjectType {
        INVALID(0, EmfPlusUnknownData::new),
        BRUSH(1, HemfPlusBrush.EmfPlusBrush::new),
        PEN(2, HemfPlusPen.EmfPlusPen::new),
        PATH(3, HemfPlusPath.EmfPlusPath::new),
        REGION(4, HemfPlusRegion.EmfPlusRegion::new),
        IMAGE(5, HemfPlusImage.EmfPlusImage::new),
        FONT(6, HemfPlusFont.EmfPlusFont::new),
        STRING_FORMAT(7, EmfPlusUnknownData::new),
        IMAGE_ATTRIBUTES(8, HemfPlusImage.EmfPlusImageAttributes::new),
        CUSTOM_LINE_CAP(9, EmfPlusUnknownData::new);

        public final int id;
        public final Supplier<? extends EmfPlusObjectData> constructor;

        private EmfPlusObjectType(int id, Supplier<? extends EmfPlusObjectData> constructor) {
            this.id = id;
            this.constructor = constructor;
        }

        public static EmfPlusObjectType valueOf(int id) {
            for (EmfPlusObjectType wrt : EmfPlusObjectType.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }
}

