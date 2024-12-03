/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emf;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfDraw;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emf.HemfRecordType;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecord;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecordIterator;
import org.apache.poi.hwmf.usermodel.HwmfCharsetAware;
import org.apache.poi.hwmf.usermodel.HwmfPicture;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.RecordFormatException;

@Internal
public class HemfComment {
    private static final Logger LOG = LogManager.getLogger(HemfComment.class);

    public static class EmfCommentDataUnicode
    implements EmfCommentData {
        @Override
        public HemfCommentRecordType getCommentRecordType() {
            return HemfCommentRecordType.emfUnicodeString;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize) throws IOException {
            throw new RecordFormatException("UNICODE_STRING/UNICODE_END values are reserved in CommentPublic records");
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return null;
        }
    }

    public static class EmfCommentDataWMF
    implements EmfCommentData {
        private final Rectangle2D bounds = new Rectangle2D.Double();
        private byte[] wmfData;

        @Override
        public HemfCommentRecordType getCommentRecordType() {
            return HemfCommentRecordType.emfWMF;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize) throws IOException {
            long startIdx = leis.getReadIndex();
            EmfComment.validateCommentType(leis, HemfCommentRecordType.emfWMF);
            int version = leis.readUShort();
            leis.skipFully(2);
            int checksum = leis.readInt();
            int flags = leis.readInt();
            int winMetafileSize = (int)leis.readUInt();
            this.wmfData = IOUtils.safelyAllocate(winMetafileSize, HwmfPicture.getMaxRecordLength());
            int readBytes = leis.read(this.wmfData);
            if (readBytes < this.wmfData.length) {
                LOG.atInfo().log("Emf comment with WMF: expected {} bytes - received only {} bytes.", (Object)Unbox.box(this.wmfData.length), (Object)Unbox.box(readBytes));
            }
            return (long)leis.getReadIndex() - startIdx;
        }

        public byte[] getWMFData() {
            return this.wmfData;
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("bounds", this::getBounds, "wmfData", this::getWMFData);
        }
    }

    public static class EmfCommentDataFormat
    implements GenericRecord {
        private EmfFormatSignature signature;
        private int version;
        private int sizeData;
        private int offData;
        private byte[] rawData;

        public long init(LittleEndianInputStream leis, long dataSize, long startIdx) throws IOException {
            this.signature = EmfFormatSignature.getById(leis.readInt());
            this.version = leis.readInt();
            this.sizeData = leis.readInt();
            this.offData = leis.readInt();
            if (this.sizeData < 0) {
                throw new RecordFormatException("size for emrformat must be > 0");
            }
            if (this.offData < 0) {
                throw new RecordFormatException("offset for emrformat must be > 0");
            }
            return 16L;
        }

        public byte[] getRawData() {
            return this.rawData;
        }

        public EmfFormatSignature getSignature() {
            return this.signature;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("signature", this::getSignature, "version", () -> this.version, "sizeData", () -> this.sizeData, "offData", () -> this.offData);
        }

        static /* synthetic */ byte[] access$102(EmfCommentDataFormat x0, byte[] x1) {
            x0.rawData = x1;
            return x1;
        }
    }

    public static enum EmfFormatSignature {
        ENHMETA_SIGNATURE(1179469088),
        EPS_SIGNATURE(1179865157);

        int id;

        private EmfFormatSignature(int id) {
            this.id = id;
        }

        public static EmfFormatSignature getById(int id) {
            for (EmfFormatSignature wrt : EmfFormatSignature.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }

    public static class EmfCommentDataMultiformats
    implements EmfCommentData {
        private final Rectangle2D bounds = new Rectangle2D.Double();
        private final List<EmfCommentDataFormat> formats = new ArrayList<EmfCommentDataFormat>();

        @Override
        public HemfCommentRecordType getCommentRecordType() {
            return HemfCommentRecordType.emfMultiFormats;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize) throws IOException {
            int startIdx = leis.getReadIndex();
            EmfComment.validateCommentType(leis, HemfCommentRecordType.emfMultiFormats);
            HemfDraw.readRectL(leis, this.bounds);
            int countFormats = (int)leis.readUInt();
            for (int i = 0; i < countFormats; ++i) {
                EmfCommentDataFormat fmt = new EmfCommentDataFormat();
                long readBytes = fmt.init(leis, dataSize, startIdx);
                this.formats.add(fmt);
                if (readBytes == 0L) break;
            }
            for (EmfCommentDataFormat fmt : this.formats) {
                int skip = fmt.offData - (leis.getReadIndex() - startIdx);
                leis.skipFully(skip);
                EmfCommentDataFormat.access$102(fmt, IOUtils.safelyAllocate(fmt.sizeData, HwmfPicture.getMaxRecordLength()));
                int readBytes = leis.read(fmt.rawData);
                if (readBytes >= fmt.sizeData) continue;
                break;
            }
            return (long)leis.getReadIndex() - (long)startIdx;
        }

        public List<EmfCommentDataFormat> getFormats() {
            return Collections.unmodifiableList(this.formats);
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("bounds", this::getBounds);
        }

        public List<EmfCommentDataFormat> getGenericChildren() {
            return this.getFormats();
        }
    }

    public static class EmfCommentDataEndGroup
    implements EmfCommentData {
        @Override
        public HemfCommentRecordType getCommentRecordType() {
            return HemfCommentRecordType.emfEndGroup;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize) throws IOException {
            long startIdx = leis.getReadIndex();
            EmfComment.validateCommentType(leis, HemfCommentRecordType.emfEndGroup);
            return (long)leis.getReadIndex() - startIdx;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return null;
        }
    }

    public static class EmfCommentDataBeginGroup
    implements EmfCommentData {
        private final Rectangle2D bounds = new Rectangle2D.Double();
        private String description;

        @Override
        public HemfCommentRecordType getCommentRecordType() {
            return HemfCommentRecordType.emfBeginGroup;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize) throws IOException {
            long startIdx = leis.getReadIndex();
            EmfComment.validateCommentType(leis, HemfCommentRecordType.emfBeginGroup);
            HemfDraw.readRectL(leis, this.bounds);
            int nDescription = (int)leis.readUInt();
            byte[] buf = IOUtils.safelyAllocate((long)nDescription * 2L, HwmfPicture.getMaxRecordLength());
            leis.readFully(buf);
            this.description = new String(buf, StandardCharsets.UTF_16LE);
            return (long)leis.getReadIndex() - startIdx;
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        public String getDescription() {
            return this.description;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("bounds", this::getBounds, "description", this::getDescription);
        }
    }

    public static class EmfCommentDataPlus
    implements EmfCommentData {
        private final List<HemfPlusRecord> records = new ArrayList<HemfPlusRecord>();

        @Override
        public HemfCommentRecordType getCommentRecordType() {
            return HemfCommentRecordType.emfPlus;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize) throws IOException {
            long startIdx = leis.getReadIndex();
            EmfComment.validateCommentType(leis, HemfCommentRecordType.emfPlus);
            new HemfPlusRecordIterator(leis, (int)dataSize - 4).forEachRemaining(this.records::add);
            return (long)leis.getReadIndex() - startIdx;
        }

        public List<HemfPlusRecord> getRecords() {
            return Collections.unmodifiableList(this.records);
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.setRenderState(HemfGraphics.EmfRenderState.EMFPLUS_ONLY);
            this.records.forEach(ctx::draw);
        }

        @Override
        public void calcBounds(HemfRecord.RenderBounds holder) {
            holder.setState(HemfGraphics.EmfRenderState.EMFPLUS_ONLY);
            for (HemfPlusRecord r : this.records) {
                r.calcBounds(holder);
                if (holder.getWindow().isEmpty() || holder.getViewport().isEmpty()) continue;
                break;
            }
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return null;
        }

        public List<HemfPlusRecord> getGenericChildren() {
            return this.getRecords();
        }
    }

    public static class EmfCommentDataGeneric
    implements EmfCommentData,
    HwmfCharsetAware {
        private byte[] privateData;
        private Supplier<Charset> charsetProvider = () -> LocaleUtil.CHARSET_1252;

        @Override
        public HemfCommentRecordType getCommentRecordType() {
            return HemfCommentRecordType.emfGeneric;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize) throws IOException {
            this.privateData = IOUtils.safelyAllocate(dataSize, HwmfPicture.getMaxRecordLength());
            leis.readFully(this.privateData);
            return this.privateData.length;
        }

        public byte[] getPrivateData() {
            return this.privateData;
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public String getPrivateDataAsString() {
            return new String(this.privateData, this.charsetProvider.get());
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("privateData", this::getPrivateData, "privateDataAsString", this::getPrivateDataAsString);
        }

        @Override
        public void setCharsetProvider(Supplier<Charset> provider) {
            this.charsetProvider = provider;
        }
    }

    public static class EmfCommentDataIterator
    implements Iterator<EmfCommentData> {
        private final LittleEndianInputStream leis;
        private final int startIdx;
        private final int limit;
        private EmfCommentData currentRecord;
        private final boolean emfParent;

        public EmfCommentDataIterator(LittleEndianInputStream leis, int limit, boolean emfParent) {
            this.leis = leis;
            this.limit = limit;
            this.emfParent = emfParent;
            this.startIdx = leis.getReadIndex();
            this.currentRecord = this._next();
        }

        @Override
        public boolean hasNext() {
            return this.currentRecord != null;
        }

        @Override
        public EmfCommentData next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            EmfCommentData toReturn = this.currentRecord;
            boolean isEOF = this.limit == -1 || this.leis.getReadIndex() >= this.startIdx + this.limit;
            this.currentRecord = isEOF ? null : this._next();
            return toReturn;
        }

        private EmfCommentData _next() {
            long recordSize;
            if (this.currentRecord == null && this.emfParent) {
                recordSize = this.limit;
            } else {
                try {
                    long type = this.leis.readUInt();
                    assert (type == HemfRecordType.comment.id);
                }
                catch (RuntimeException e) {
                    return null;
                }
                recordSize = this.leis.readUInt();
            }
            long dataSize = this.leis.readUInt();
            try {
                this.leis.mark(8);
                int commentIdentifier = (int)this.leis.readUInt();
                int publicCommentIdentifier = (int)this.leis.readUInt();
                boolean isEmfPublic = (long)commentIdentifier == HemfCommentRecordType.emfPublic.id;
                this.leis.reset();
                HemfCommentRecordType commentType = HemfCommentRecordType.getById(isEmfPublic ? (long)publicCommentIdentifier : (long)commentIdentifier, isEmfPublic);
                assert (commentType != null);
                EmfCommentData record = commentType.constructor.get();
                long readBytes = record.init(this.leis, dataSize);
                int skipBytes = (int)(recordSize - 4L - readBytes);
                assert (skipBytes >= 0);
                this.leis.skipFully(skipBytes);
                return record;
            }
            catch (IOException e) {
                throw new RecordFormatException(e);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }
    }

    public static class EmfComment
    implements HemfRecord,
    HwmfCharsetAware {
        private EmfCommentData data;

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.comment;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long startIdx = leis.getReadIndex();
            this.data = new EmfCommentDataIterator(leis, (int)recordSize, true).next();
            return (long)leis.getReadIndex() - startIdx;
        }

        public EmfCommentData getCommentData() {
            return this.data;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            this.data.draw(ctx);
        }

        @Override
        public void calcBounds(HemfRecord.RenderBounds holder) {
            this.data.calcBounds(holder);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("data", this::getCommentData);
        }

        static void validateCommentType(LittleEndianInputStream leis, HemfCommentRecordType commentType) {
            int commentIdentifier = (int)leis.readUInt();
            if ((long)commentIdentifier == HemfCommentRecordType.emfPublic.id) {
                commentIdentifier = (int)leis.readUInt();
            }
            assert ((long)commentIdentifier == commentType.id);
        }

        @Override
        public void setCharsetProvider(Supplier<Charset> provider) {
            if (this.data instanceof HwmfCharsetAware) {
                ((HwmfCharsetAware)((Object)this.data)).setCharsetProvider(provider);
            }
        }
    }

    public static interface EmfCommentData
    extends GenericRecord {
        public HemfCommentRecordType getCommentRecordType();

        public long init(LittleEndianInputStream var1, long var2) throws IOException;

        default public void draw(HemfGraphics ctx) {
        }

        default public void calcBounds(HemfRecord.RenderBounds holder) {
        }

        default public HemfCommentRecordType getGenericRecordType() {
            return this.getCommentRecordType();
        }
    }

    public static enum HemfCommentRecordType {
        emfGeneric(-1L, EmfCommentDataGeneric::new, false),
        emfSpool(0L, EmfCommentDataGeneric::new, false),
        emfPlus(726027589L, EmfCommentDataPlus::new, false),
        emfPublic(1128875079L, null, false),
        emfBeginGroup(2L, EmfCommentDataBeginGroup::new, true),
        emfEndGroup(3L, EmfCommentDataEndGroup::new, true),
        emfMultiFormats(0x40000004L, EmfCommentDataMultiformats::new, true),
        emfWMF(-2147483647L, EmfCommentDataWMF::new, true),
        emfUnicodeString(64L, EmfCommentDataUnicode::new, true),
        emfUnicodeEnd(128L, EmfCommentDataUnicode::new, true);

        public final long id;
        public final Supplier<? extends EmfCommentData> constructor;
        public final boolean isEmfPublic;

        private HemfCommentRecordType(long id, Supplier<? extends EmfCommentData> constructor, boolean isEmfPublic) {
            this.id = id;
            this.constructor = constructor;
            this.isEmfPublic = isEmfPublic;
        }

        public static HemfCommentRecordType getById(long id, boolean isEmfPublic) {
            for (HemfCommentRecordType wrt : HemfCommentRecordType.values()) {
                if (wrt.id != id || wrt.isEmfPublic != isEmfPublic) continue;
                return wrt;
            }
            return emfGeneric;
        }
    }
}

