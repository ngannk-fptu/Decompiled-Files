/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfRecord;
import org.apache.poi.hwmf.record.HwmfRecordType;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInputStream;

public class HwmfEscape
implements HwmfRecord {
    private static final int MAX_OBJECT_SIZE = 65535;
    private EscapeFunction escapeFunction;
    private HwmfEscapeData escapeData;

    @Override
    public HwmfRecordType getWmfRecordType() {
        return HwmfRecordType.escape;
    }

    @Override
    public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
        this.escapeFunction = EscapeFunction.valueOf(leis.readUShort());
        int byteCount = leis.readUShort();
        int size = 4;
        this.escapeData = this.escapeFunction == null ? new WmfEscapeUnknownData() : (HwmfEscapeData)this.escapeFunction.constructor.get();
        return size += this.escapeData.init(leis, byteCount, this.escapeFunction);
    }

    public EscapeFunction getEscapeFunction() {
        return this.escapeFunction;
    }

    public <T extends HwmfEscapeData> T getEscapeData() {
        return (T)this.escapeData;
    }

    @Override
    public void draw(HwmfGraphics ctx) {
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("escapeFunction", this::getEscapeFunction, "escapeData", this::getEscapeData);
    }

    public static class WmfEscapeEMF
    implements HwmfEscapeData,
    GenericRecord {
        private static final int EMF_COMMENT_IDENTIFIER = 1128680791;
        int commentIdentifier;
        int commentType;
        int version;
        int checksum;
        int flags;
        int commentRecordCount;
        int currentRecordSize;
        int remainingBytes;
        int emfRecordSize;
        byte[] emfData;

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, EscapeFunction escapeFunction) throws IOException {
            if (recordSize < 4L) {
                return 0;
            }
            this.commentIdentifier = leis.readInt();
            if (this.commentIdentifier != 1128680791) {
                this.emfData = IOUtils.toByteArray(leis, (int)(recordSize - 4L), 65535);
                this.remainingBytes = this.emfData.length;
                return (int)recordSize;
            }
            this.commentType = leis.readInt();
            assert (this.commentType == 1);
            this.version = leis.readInt();
            this.checksum = leis.readUShort();
            this.flags = leis.readInt();
            assert (this.flags == 0);
            this.commentRecordCount = leis.readInt();
            this.currentRecordSize = leis.readInt();
            assert (0 <= this.currentRecordSize && this.currentRecordSize <= 8192);
            this.remainingBytes = leis.readInt();
            this.emfRecordSize = leis.readInt();
            this.emfData = IOUtils.toByteArray(leis, this.currentRecordSize, 65535);
            return 34 + this.emfData.length;
        }

        public boolean isValid() {
            return this.commentIdentifier == 1128680791;
        }

        public int getCommentRecordCount() {
            return this.commentRecordCount;
        }

        public int getCurrentRecordSize() {
            return this.currentRecordSize;
        }

        public int getRemainingBytes() {
            return this.remainingBytes;
        }

        public int getEmfRecordSize() {
            return this.emfRecordSize;
        }

        public byte[] getEmfData() {
            return this.emfData;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
            m.put("commentIdentifier", () -> this.commentIdentifier);
            m.put("commentType", () -> this.commentType);
            m.put("version", () -> this.version);
            m.put("checksum", () -> this.checksum);
            m.put("flags", () -> this.flags);
            m.put("commentRecordCount", this::getCommentRecordCount);
            m.put("currentRecordSize", this::getCurrentRecordSize);
            m.put("remainingBytes", this::getRemainingBytes);
            m.put("emfRecordSize", this::getEmfRecordSize);
            m.put("emfData", this::getEmfData);
            return Collections.unmodifiableMap(m);
        }
    }

    public static class WmfEscapeUnknownData
    implements HwmfEscapeData,
    GenericRecord {
        EscapeFunction escapeFunction;
        private byte[] escapeDataBytes;

        public byte[] getEscapeDataBytes() {
            return this.escapeDataBytes;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, EscapeFunction escapeFunction) throws IOException {
            this.escapeFunction = escapeFunction;
            this.escapeDataBytes = IOUtils.toByteArray(leis, (int)recordSize, 65535);
            return (int)recordSize;
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("escapeDataBytes", this::getEscapeDataBytes);
        }
    }

    public static interface HwmfEscapeData {
        public int init(LittleEndianInputStream var1, long var2, EscapeFunction var4) throws IOException;
    }

    public static enum EscapeFunction {
        NEWFRAME(1, WmfEscapeUnknownData::new),
        ABORTDOC(2, WmfEscapeUnknownData::new),
        NEXTBAND(3, WmfEscapeUnknownData::new),
        SETCOLORTABLE(4, WmfEscapeUnknownData::new),
        GETCOLORTABLE(5, WmfEscapeUnknownData::new),
        FLUSHOUT(6, WmfEscapeUnknownData::new),
        DRAFTMODE(7, WmfEscapeUnknownData::new),
        QUERYESCSUPPORT(8, WmfEscapeUnknownData::new),
        SETABORTPROC(9, WmfEscapeUnknownData::new),
        STARTDOC(10, WmfEscapeUnknownData::new),
        ENDDOC(11, WmfEscapeUnknownData::new),
        GETPHYSPAGESIZE(12, WmfEscapeUnknownData::new),
        GETPRINTINGOFFSET(13, WmfEscapeUnknownData::new),
        GETSCALINGFACTOR(14, WmfEscapeUnknownData::new),
        META_ESCAPE_ENHANCED_METAFILE(15, WmfEscapeEMF::new),
        SETPENWIDTH(16, WmfEscapeUnknownData::new),
        SETCOPYCOUNT(17, WmfEscapeUnknownData::new),
        SETPAPERSOURCE(18, WmfEscapeUnknownData::new),
        PASSTHROUGH(19, WmfEscapeUnknownData::new),
        GETTECHNOLOGY(20, WmfEscapeUnknownData::new),
        SETLINECAP(21, WmfEscapeUnknownData::new),
        SETLINEJOIN(22, WmfEscapeUnknownData::new),
        SETMITERLIMIT(23, WmfEscapeUnknownData::new),
        BANDINFO(24, WmfEscapeUnknownData::new),
        DRAWPATTERNRECT(25, WmfEscapeUnknownData::new),
        GETVECTORPENSIZE(26, WmfEscapeUnknownData::new),
        GETVECTORBRUSHSIZE(27, WmfEscapeUnknownData::new),
        ENABLEDUPLEX(28, WmfEscapeUnknownData::new),
        GETSETPAPERBINS(29, WmfEscapeUnknownData::new),
        GETSETPRINTORIENT(30, WmfEscapeUnknownData::new),
        ENUMPAPERBINS(31, WmfEscapeUnknownData::new),
        SETDIBSCALING(32, WmfEscapeUnknownData::new),
        EPSPRINTING(33, WmfEscapeUnknownData::new),
        ENUMPAPERMETRICS(34, WmfEscapeUnknownData::new),
        GETSETPAPERMETRICS(35, WmfEscapeUnknownData::new),
        POSTSCRIPT_DATA(37, WmfEscapeUnknownData::new),
        POSTSCRIPT_IGNORE(38, WmfEscapeUnknownData::new),
        GETDEVICEUNITS(42, WmfEscapeUnknownData::new),
        GETEXTENDEDTEXTMETRICS(256, WmfEscapeUnknownData::new),
        GETPAIRKERNTABLE(258, WmfEscapeUnknownData::new),
        EXTTEXTOUT(512, WmfEscapeUnknownData::new),
        GETFACENAME(513, WmfEscapeUnknownData::new),
        DOWNLOADFACE(514, WmfEscapeUnknownData::new),
        METAFILE_DRIVER(2049, WmfEscapeUnknownData::new),
        QUERYDIBSUPPORT(3073, WmfEscapeUnknownData::new),
        BEGIN_PATH(4096, WmfEscapeUnknownData::new),
        CLIP_TO_PATH(4097, WmfEscapeUnknownData::new),
        END_PATH(4098, WmfEscapeUnknownData::new),
        OPEN_CHANNEL(4110, WmfEscapeUnknownData::new),
        DOWNLOADHEADER(4111, WmfEscapeUnknownData::new),
        CLOSE_CHANNEL(4112, WmfEscapeUnknownData::new),
        POSTSCRIPT_PASSTHROUGH(4115, WmfEscapeUnknownData::new),
        ENCAPSULATED_POSTSCRIPT(4116, WmfEscapeUnknownData::new),
        POSTSCRIPT_IDENTIFY(4117, WmfEscapeUnknownData::new),
        POSTSCRIPT_INJECTION(4118, WmfEscapeUnknownData::new),
        CHECKJPEGFORMAT(4119, WmfEscapeUnknownData::new),
        CHECKPNGFORMAT(4120, WmfEscapeUnknownData::new),
        GET_PS_FEATURESETTING(4121, WmfEscapeUnknownData::new),
        MXDC_ESCAPE(4122, WmfEscapeUnknownData::new),
        SPCLPASSTHROUGH2(4568, WmfEscapeUnknownData::new);

        private final int flag;
        private final Supplier<? extends HwmfEscapeData> constructor;

        private EscapeFunction(int flag, Supplier<? extends HwmfEscapeData> constructor) {
            this.flag = flag;
            this.constructor = constructor;
        }

        public int getFlag() {
            return this.flag;
        }

        public Supplier<? extends HwmfEscapeData> getConstructor() {
            return this.constructor;
        }

        static EscapeFunction valueOf(int flag) {
            for (EscapeFunction hs : EscapeFunction.values()) {
                if (hs.flag != flag) continue;
                return hs;
            }
            return null;
        }
    }
}

