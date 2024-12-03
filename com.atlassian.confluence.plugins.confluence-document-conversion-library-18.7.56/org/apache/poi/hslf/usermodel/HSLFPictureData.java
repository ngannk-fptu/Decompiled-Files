/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherRecordTypes;
import org.apache.poi.hslf.blip.DIB;
import org.apache.poi.hslf.blip.EMF;
import org.apache.poi.hslf.blip.JPEG;
import org.apache.poi.hslf.blip.PICT;
import org.apache.poi.hslf.blip.PNG;
import org.apache.poi.hslf.blip.WMF;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Removal;
import org.apache.poi.util.Units;

public abstract class HSLFPictureData
implements PictureData,
GenericRecord {
    private static final Logger LOGGER = LogManager.getLogger(HSLFPictureData.class);
    protected static final int CHECKSUM_SIZE = 16;
    static final int PREAMBLE_SIZE = 8;
    private byte[] formattedData;
    private int uidInstanceCount = 1;
    private int index = -1;
    final EscherContainerRecord bStore;
    final EscherBSERecord bse;

    @Deprecated
    @Removal(version="5.3")
    public HSLFPictureData() {
        this(new EscherContainerRecord(), new EscherBSERecord());
        LOGGER.atWarn().log("The no-arg constructor is deprecated. Some functionality such as updating pictures won't work.");
    }

    @Internal
    protected HSLFPictureData(EscherContainerRecord bStore, EscherBSERecord bse) {
        this.bStore = Objects.requireNonNull(bStore);
        this.bse = Objects.requireNonNull(bse);
    }

    protected abstract int getSignature();

    public abstract void setSignature(int var1);

    protected int getUIDInstanceCount() {
        return this.uidInstanceCount;
    }

    protected void setUIDInstanceCount(int uidInstanceCount) {
        this.uidInstanceCount = uidInstanceCount;
    }

    public byte[] getRawData() {
        return this.formattedData;
    }

    @Deprecated
    @Removal(version="5.3")
    public void setRawData(byte[] data) {
        this.formattedData = data == null ? null : (byte[])data.clone();
    }

    public int getOffset() {
        return this.bse.getOffset();
    }

    @Deprecated
    @Removal(version="5.3")
    public void setOffset(int offset) {
        LOGGER.atWarn().log("HSLFPictureData#setOffset is deprecated.");
    }

    public byte[] getUID() {
        return Arrays.copyOf(this.formattedData, 16);
    }

    @Override
    public byte[] getChecksum() {
        return this.getUID();
    }

    public static byte[] getChecksum(byte[] data) {
        MessageDigest md5 = CryptoFunctions.getMessageDigest(HashAlgorithm.md5);
        md5.update(data);
        return md5.digest();
    }

    public void write(OutputStream out) throws IOException {
        LittleEndian.putUShort(this.getSignature(), out);
        PictureData.PictureType pt = this.getType();
        LittleEndian.putUShort(pt.nativeId + EscherRecordTypes.BLIP_START.typeID, out);
        byte[] rd = this.getRawData();
        LittleEndian.putInt(rd.length, out);
        out.write(rd);
    }

    @Deprecated
    @Removal(version="5.3")
    public static HSLFPictureData create(PictureData.PictureType type) {
        LOGGER.atWarn().log("HSLFPictureData#create(PictureType) is deprecated. Some functionality such as updating pictures won't work.");
        EscherContainerRecord record = new EscherContainerRecord();
        EscherBSERecord bse = new EscherBSERecord();
        return new HSLFSlideShowImpl.PictureFactory(record, type, new byte[0], 0, 0).setRecord(bse).build();
    }

    static HSLFPictureData createFromSlideshowData(PictureData.PictureType type, EscherContainerRecord recordContainer, EscherBSERecord bse, byte[] data, int signature) {
        HSLFPictureData instance = HSLFPictureData.newInstance(type, recordContainer, bse);
        instance.setSignature(signature);
        instance.formattedData = data;
        return instance;
    }

    static HSLFPictureData createFromImageData(PictureData.PictureType type, EscherContainerRecord recordContainer, EscherBSERecord bse, byte[] data) {
        HSLFPictureData instance = HSLFPictureData.newInstance(type, recordContainer, bse);
        instance.formattedData = instance.formatImageForSlideshow(data);
        return instance;
    }

    private static HSLFPictureData newInstance(PictureData.PictureType type, EscherContainerRecord recordContainer, EscherBSERecord bse) {
        switch (type) {
            case EMF: {
                return new EMF(recordContainer, bse);
            }
            case WMF: {
                return new WMF(recordContainer, bse);
            }
            case PICT: {
                return new PICT(recordContainer, bse);
            }
            case JPEG: {
                return new JPEG(recordContainer, bse);
            }
            case PNG: {
                return new PNG(recordContainer, bse);
            }
            case DIB: {
                return new DIB(recordContainer, bse);
            }
        }
        throw new IllegalArgumentException("Unsupported picture type: " + (Object)((Object)type));
    }

    public byte[] getHeader() {
        byte[] header = new byte[24];
        LittleEndian.putInt(header, 0, this.getSignature());
        LittleEndian.putInt(header, 4, this.getRawData().length);
        System.arraycopy(this.formattedData, 0, header, 8, 16);
        return header;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    protected abstract byte[] formatImageForSlideshow(byte[] var1);

    int getBseSize() {
        return this.formattedData.length + 8;
    }

    @Override
    public final void setData(byte[] data) throws IOException {
        int oldSize = this.getBseSize();
        this.formattedData = this.formatImageForSlideshow(data);
        int newSize = this.getBseSize();
        int changeInSize = newSize - oldSize;
        byte[] newUid = this.getUID();
        boolean foundBseForOldImage = false;
        List<EscherRecord> bseRecords = this.bStore.getChildRecords();
        bseRecords.sort(Comparator.comparingInt(EscherBSERecord::getOffset));
        for (EscherBSERecord escherBSERecord : bseRecords) {
            if (foundBseForOldImage) {
                escherBSERecord.setOffset(escherBSERecord.getOffset() + changeInSize);
                continue;
            }
            if (escherBSERecord != this.bse) continue;
            foundBseForOldImage = true;
            escherBSERecord.setUid(newUid);
            escherBSERecord.setSize(newSize);
        }
    }

    @Override
    public final String getContentType() {
        return this.getType().contentType;
    }

    @Override
    public Dimension getImageDimensionInPixels() {
        Dimension dim = this.getImageDimension();
        return new Dimension(Units.pointsToPixel(dim.getWidth()), Units.pointsToPixel(dim.getHeight()));
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
        m.put("type", this::getType);
        m.put("imageDimension", this::getImageDimension);
        m.put("signature", this::getSignature);
        m.put("uidInstanceCount", this::getUIDInstanceCount);
        m.put("offset", this::getOffset);
        m.put("uid", this::getUID);
        m.put("checksum", this::getChecksum);
        m.put("index", this::getIndex);
        m.put("rawData", this::getRawData);
        return Collections.unmodifiableMap(m);
    }
}

