/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.ddf;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.DeflaterOutputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.ddf.EscherBlipRecord;
import org.apache.poi.ddf.EscherRecordFactory;
import org.apache.poi.ddf.EscherRecordTypes;
import org.apache.poi.ddf.EscherSerializationListener;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Removal;

public final class EscherMetafileBlip
extends EscherBlipRecord {
    private static final Logger LOGGER = LogManager.getLogger(EscherMetafileBlip.class);
    private static final int DEFAULT_MAX_RECORD_LENGTH = 100000000;
    private static int MAX_RECORD_LENGTH = 100000000;
    @Deprecated
    @Removal(version="5.3")
    public static final short RECORD_ID_EMF = EscherRecordTypes.BLIP_EMF.typeID;
    @Deprecated
    @Removal(version="5.3")
    public static final short RECORD_ID_WMF = EscherRecordTypes.BLIP_WMF.typeID;
    @Deprecated
    @Removal(version="5.3")
    public static final short RECORD_ID_PICT = EscherRecordTypes.BLIP_PICT.typeID;
    private static final int HEADER_SIZE = 8;
    private final byte[] field_1_UID = new byte[16];
    private final byte[] field_2_UID = new byte[16];
    private int field_2_cb;
    private int field_3_rcBounds_x1;
    private int field_3_rcBounds_y1;
    private int field_3_rcBounds_x2;
    private int field_3_rcBounds_y2;
    private int field_4_ptSize_w;
    private int field_4_ptSize_h;
    private int field_5_cbSave;
    private byte field_6_fCompression;
    private byte field_7_fFilter;
    private byte[] raw_pictureData;
    private byte[] remainingData;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public EscherMetafileBlip() {
    }

    public EscherMetafileBlip(EscherMetafileBlip other) {
        super(other);
        System.arraycopy(other.field_1_UID, 0, this.field_1_UID, 0, this.field_1_UID.length);
        System.arraycopy(other.field_2_UID, 0, this.field_2_UID, 0, this.field_2_UID.length);
        this.field_2_cb = other.field_2_cb;
        this.field_3_rcBounds_x1 = other.field_3_rcBounds_x1;
        this.field_3_rcBounds_y1 = other.field_3_rcBounds_y1;
        this.field_3_rcBounds_x2 = other.field_3_rcBounds_x2;
        this.field_3_rcBounds_y2 = other.field_3_rcBounds_y2;
        this.field_4_ptSize_h = other.field_4_ptSize_h;
        this.field_4_ptSize_w = other.field_4_ptSize_w;
        this.field_5_cbSave = other.field_5_cbSave;
        this.field_6_fCompression = other.field_6_fCompression;
        this.field_7_fFilter = other.field_7_fFilter;
        this.raw_pictureData = other.raw_pictureData == null ? null : (byte[])other.raw_pictureData.clone();
        this.remainingData = other.remainingData == null ? null : (byte[])other.remainingData.clone();
    }

    @Override
    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesAfterHeader = this.readHeader(data, offset);
        int pos = offset + 8;
        System.arraycopy(data, pos, this.field_1_UID, 0, 16);
        pos += 16;
        if ((this.getOptions() ^ this.getSignature()) == 16) {
            System.arraycopy(data, pos, this.field_2_UID, 0, 16);
            pos += 16;
        }
        this.field_2_cb = LittleEndian.getInt(data, pos);
        this.field_3_rcBounds_x1 = LittleEndian.getInt(data, pos += 4);
        this.field_3_rcBounds_y1 = LittleEndian.getInt(data, pos += 4);
        this.field_3_rcBounds_x2 = LittleEndian.getInt(data, pos += 4);
        this.field_3_rcBounds_y2 = LittleEndian.getInt(data, pos += 4);
        this.field_4_ptSize_w = LittleEndian.getInt(data, pos += 4);
        this.field_4_ptSize_h = LittleEndian.getInt(data, pos += 4);
        this.field_5_cbSave = LittleEndian.getInt(data, pos += 4);
        this.field_6_fCompression = data[pos += 4];
        this.field_7_fFilter = data[++pos];
        this.raw_pictureData = IOUtils.safelyClone(data, ++pos, this.field_5_cbSave, MAX_RECORD_LENGTH);
        pos += this.field_5_cbSave;
        if (this.field_6_fCompression == 0) {
            super.setPictureData(EscherMetafileBlip.inflatePictureData(this.raw_pictureData));
        } else {
            super.setPictureData(this.raw_pictureData);
        }
        int remaining = bytesAfterHeader - pos + offset + 8;
        if (remaining > 0) {
            this.remainingData = IOUtils.safelyClone(data, pos, remaining, MAX_RECORD_LENGTH);
        }
        return bytesAfterHeader + 8;
    }

    @Override
    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        int pos = offset;
        LittleEndian.putShort(data, pos, this.getOptions());
        LittleEndian.putShort(data, pos += 2, this.getRecordId());
        LittleEndian.putInt(data, pos += 2, this.getRecordSize() - 8);
        System.arraycopy(this.field_1_UID, 0, data, pos += 4, this.field_1_UID.length);
        pos += this.field_1_UID.length;
        if ((this.getOptions() ^ this.getSignature()) == 16) {
            System.arraycopy(this.field_2_UID, 0, data, pos, this.field_2_UID.length);
            pos += this.field_2_UID.length;
        }
        LittleEndian.putInt(data, pos, this.field_2_cb);
        LittleEndian.putInt(data, pos += 4, this.field_3_rcBounds_x1);
        LittleEndian.putInt(data, pos += 4, this.field_3_rcBounds_y1);
        LittleEndian.putInt(data, pos += 4, this.field_3_rcBounds_x2);
        LittleEndian.putInt(data, pos += 4, this.field_3_rcBounds_y2);
        LittleEndian.putInt(data, pos += 4, this.field_4_ptSize_w);
        LittleEndian.putInt(data, pos += 4, this.field_4_ptSize_h);
        LittleEndian.putInt(data, pos += 4, this.field_5_cbSave);
        data[pos += 4] = this.field_6_fCompression;
        data[++pos] = this.field_7_fFilter;
        System.arraycopy(this.raw_pictureData, 0, data, ++pos, this.raw_pictureData.length);
        pos += this.raw_pictureData.length;
        if (this.remainingData != null) {
            System.arraycopy(this.remainingData, 0, data, pos, this.remainingData.length);
        }
        listener.afterRecordSerialize(offset + this.getRecordSize(), this.getRecordId(), this.getRecordSize(), this);
        return this.getRecordSize();
    }

    /*
     * Exception decompiling
     */
    private static byte[] inflatePictureData(byte[] data) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public int getRecordSize() {
        int size = 58 + this.raw_pictureData.length;
        if (this.remainingData != null) {
            size += this.remainingData.length;
        }
        if ((this.getOptions() ^ this.getSignature()) == 16) {
            size += this.field_2_UID.length;
        }
        return size;
    }

    public byte[] getUID() {
        return this.field_1_UID;
    }

    public void setUID(byte[] uid) {
        if (uid == null || uid.length != 16) {
            throw new IllegalArgumentException("uid must be byte[16]");
        }
        System.arraycopy(uid, 0, this.field_1_UID, 0, this.field_1_UID.length);
    }

    public byte[] getPrimaryUID() {
        return this.field_2_UID;
    }

    public void setPrimaryUID(byte[] primaryUID) {
        if (primaryUID == null || primaryUID.length != 16) {
            throw new IllegalArgumentException("primaryUID must be byte[16]");
        }
        System.arraycopy(primaryUID, 0, this.field_2_UID, 0, this.field_2_UID.length);
    }

    public int getUncompressedSize() {
        return this.field_2_cb;
    }

    public void setUncompressedSize(int uncompressedSize) {
        this.field_2_cb = uncompressedSize;
    }

    public Rectangle getBounds() {
        return new Rectangle(this.field_3_rcBounds_x1, this.field_3_rcBounds_y1, this.field_3_rcBounds_x2 - this.field_3_rcBounds_x1, this.field_3_rcBounds_y2 - this.field_3_rcBounds_y1);
    }

    public void setBounds(Rectangle bounds) {
        this.field_3_rcBounds_x1 = bounds.x;
        this.field_3_rcBounds_y1 = bounds.y;
        this.field_3_rcBounds_x2 = bounds.x + bounds.width;
        this.field_3_rcBounds_y2 = bounds.y + bounds.height;
    }

    public Dimension getSizeEMU() {
        return new Dimension(this.field_4_ptSize_w, this.field_4_ptSize_h);
    }

    public void setSizeEMU(Dimension sizeEMU) {
        this.field_4_ptSize_w = sizeEMU.width;
        this.field_4_ptSize_h = sizeEMU.height;
    }

    public int getCompressedSize() {
        return this.field_5_cbSave;
    }

    public void setCompressedSize(int compressedSize) {
        this.field_5_cbSave = compressedSize;
    }

    public boolean isCompressed() {
        return this.field_6_fCompression == 0;
    }

    public void setCompressed(boolean compressed) {
        this.field_6_fCompression = (byte)(compressed ? 0 : -2);
    }

    public byte getFilter() {
        return this.field_7_fFilter;
    }

    public void setFilter(byte filter) {
        this.field_7_fFilter = filter;
    }

    public byte[] getRemainingData() {
        return this.remainingData;
    }

    public short getSignature() {
        switch (EscherRecordTypes.forTypeID(this.getRecordId())) {
            case BLIP_EMF: {
                return 15680;
            }
            case BLIP_WMF: {
                return 8544;
            }
            case BLIP_PICT: {
                return 21536;
            }
        }
        LOGGER.atWarn().log("Unknown metafile: {}", (Object)Unbox.box(this.getRecordId()));
        return 0;
    }

    @Override
    public void setPictureData(byte[] pictureData) {
        super.setPictureData(pictureData);
        this.setUncompressedSize(pictureData.length);
        try (UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();){
            try (DeflaterOutputStream dos = new DeflaterOutputStream((OutputStream)bos);){
                dos.write(pictureData);
            }
            this.raw_pictureData = bos.toByteArray();
        }
        catch (IOException e) {
            throw new RuntimeException("Can't compress metafile picture data", e);
        }
        this.setCompressedSize(this.raw_pictureData.length);
        this.setCompressed(true);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap m = new LinkedHashMap(super.getGenericProperties());
        m.put("uid", this::getUID);
        m.put("uncompressedSize", this::getUncompressedSize);
        m.put("bounds", this::getBounds);
        m.put("sizeInEMU", this::getSizeEMU);
        m.put("compressedSize", this::getCompressedSize);
        m.put("isCompressed", this::isCompressed);
        m.put("filter", this::getFilter);
        return Collections.unmodifiableMap(m);
    }

    @Override
    public EscherMetafileBlip copy() {
        return new EscherMetafileBlip(this);
    }
}

