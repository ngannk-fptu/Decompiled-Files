/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.blip;

import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.hslf.blip.Bitmap;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Removal;

public final class DIB
extends Bitmap {
    private static final int HEADER_SIZE = 14;

    @Deprecated
    @Removal(version="5.3")
    public DIB() {
        this(new EscherContainerRecord(), new EscherBSERecord());
    }

    @Internal
    public DIB(EscherContainerRecord recordContainer, EscherBSERecord bse) {
        super(recordContainer, bse);
    }

    @Override
    public PictureData.PictureType getType() {
        return PictureData.PictureType.DIB;
    }

    @Override
    public int getSignature() {
        return this.getUIDInstanceCount() == 1 ? 31360 : 31376;
    }

    @Override
    public void setSignature(int signature) {
        switch (signature) {
            case 31360: {
                this.setUIDInstanceCount(1);
                break;
            }
            case 31376: {
                this.setUIDInstanceCount(2);
                break;
            }
            default: {
                throw new IllegalArgumentException(signature + " is not a valid instance/signature value for DIB");
            }
        }
    }

    @Override
    public byte[] getData() {
        return DIB.addBMPHeader(super.getData());
    }

    public static byte[] addBMPHeader(byte[] data) {
        byte[] header = new byte[14];
        LittleEndian.putInt(header, 0, 19778);
        int imageSize = LittleEndian.getInt(data, 20);
        int fileSize = data.length + 14;
        int offset = fileSize - imageSize;
        LittleEndian.putInt(header, 2, fileSize);
        LittleEndian.putInt(header, 6, 0);
        LittleEndian.putInt(header, 10, offset);
        byte[] dib = IOUtils.safelyAllocate((long)header.length + (long)data.length, RecordAtom.getMaxRecordLength());
        System.arraycopy(header, 0, dib, 0, header.length);
        System.arraycopy(data, 0, dib, header.length, data.length);
        return dib;
    }

    @Override
    protected byte[] formatImageForSlideshow(byte[] data) {
        byte[] dib = IOUtils.safelyClone(data, 14, data.length - 14, data.length);
        return super.formatImageForSlideshow(dib);
    }
}

