/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hwpf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.zip.InflaterInputStream;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherBlipRecord;
import org.apache.poi.ddf.EscherComplexProperty;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherRecordTypes;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.hwpf.model.PICF;
import org.apache.poi.hwpf.model.PICFAndOfficeArtData;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.sl.image.ImageHeaderPNG;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.Units;

public final class Picture {
    private static final Logger LOGGER = LogManager.getLogger(Picture.class);
    private static final byte[] COMPRESSED1 = new byte[]{-2, 120, -38};
    private static final byte[] COMPRESSED2 = new byte[]{-2, 120, -100};
    private static final byte[] IHDR = new byte[]{73, 72, 68, 82};
    @Deprecated
    private static final byte[] PNG = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
    private PICF _picf;
    private PICFAndOfficeArtData _picfAndOfficeArtData;
    private final List<? extends EscherRecord> _blipRecords;
    private byte[] content;
    private int dataBlockStartOfsset;
    private int height = -1;
    private int width = -1;

    private static int getBigEndianInt(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 24) + ((data[offset + 1] & 0xFF) << 16) + ((data[offset + 2] & 0xFF) << 8) + (data[offset + 3] & 0xFF);
    }

    private static int getBigEndianShort(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) + (data[offset + 1] & 0xFF);
    }

    private static boolean matchSignature(byte[] pictureData, byte[] signature, int offset) {
        boolean matched = offset < pictureData.length;
        for (int i = 0; i + offset < pictureData.length && i < signature.length; ++i) {
            if (pictureData[i + offset] == signature[i]) continue;
            matched = false;
            break;
        }
        return matched;
    }

    public Picture(EscherBlipRecord blipRecord) {
        this._blipRecords = Collections.singletonList(blipRecord);
    }

    public Picture(int dataBlockStartOfsset, byte[] _dataStream, boolean fillBytes) {
        this._picfAndOfficeArtData = new PICFAndOfficeArtData(_dataStream, dataBlockStartOfsset);
        this._picf = this._picfAndOfficeArtData.getPicf();
        this.dataBlockStartOfsset = dataBlockStartOfsset;
        this._blipRecords = this._picfAndOfficeArtData.getBlipRecords();
        if (fillBytes) {
            this.fillImageContent();
        }
    }

    private void fillImageContent() {
        if (this.content != null && this.content.length > 0) {
            return;
        }
        byte[] rawContent = this.getRawContent();
        if (Picture.matchSignature(rawContent, COMPRESSED1, 32) || Picture.matchSignature(rawContent, COMPRESSED2, 32)) {
            try (UnsynchronizedByteArrayInputStream bis = new UnsynchronizedByteArrayInputStream(rawContent, 33, rawContent.length - 33);
                 InflaterInputStream in = new InflaterInputStream((InputStream)bis);
                 UnsynchronizedByteArrayOutputStream out = new UnsynchronizedByteArrayOutputStream();){
                IOUtils.copy((InputStream)in, (OutputStream)out);
                this.content = out.toByteArray();
            }
            catch (IOException e) {
                LOGGER.atInfo().withThrowable(e).log("Possibly corrupt compression or non-compressed data");
            }
        } else {
            this.content = new ImageHeaderPNG(rawContent).extractPNG();
        }
    }

    private void fillJPGWidthHeight() {
        byte[] jpegContent = this.getContent();
        int pointer = 2;
        int endOfPicture = jpegContent.length;
        while (pointer < endOfPicture - 1) {
            byte secondByte;
            byte firstByte;
            do {
                firstByte = jpegContent[pointer];
                secondByte = jpegContent[pointer + 1];
            } while (firstByte != -1 && (pointer += 2) < endOfPicture - 1);
            if (firstByte == -1 && pointer < endOfPicture - 1) {
                if (secondByte == -39 || secondByte == -38) break;
                if ((secondByte & 0xF0) == 192 && secondByte != -60 && secondByte != -56 && secondByte != -52) {
                    this.height = Picture.getBigEndianShort(jpegContent, pointer += 5);
                    this.width = Picture.getBigEndianShort(jpegContent, pointer + 2);
                    break;
                }
                ++pointer;
                int length = Picture.getBigEndianShort(jpegContent, ++pointer);
                pointer += length;
                continue;
            }
            ++pointer;
        }
    }

    void fillPNGWidthHeight() {
        int HEADER_START;
        byte[] pngContent = this.getContent();
        if (Picture.matchSignature(pngContent, IHDR, HEADER_START = PNG.length + 4)) {
            int IHDR_CHUNK_WIDTH = HEADER_START + 4;
            this.width = Picture.getBigEndianInt(pngContent, IHDR_CHUNK_WIDTH);
            this.height = Picture.getBigEndianInt(pngContent, IHDR_CHUNK_WIDTH + 4);
        }
    }

    private void fillWidthHeight() {
        PictureType pictureType = this.suggestPictureType();
        switch (pictureType) {
            case JPEG: {
                this.fillJPGWidthHeight();
                break;
            }
            case PNG: {
                this.fillPNGWidthHeight();
                break;
            }
        }
    }

    public byte[] getContent() {
        this.fillImageContent();
        return this.content;
    }

    public int getDxaCropLeft() {
        return this._picf.getDxaReserved1();
    }

    public double getCropLeft() {
        return this.getCrop(EscherPropertyTypes.BLIP__CROPFROMLEFT);
    }

    public int getDxaCropRight() {
        return this._picf.getDxaReserved2();
    }

    public double getCropRight() {
        return this.getCrop(EscherPropertyTypes.BLIP__CROPFROMRIGHT);
    }

    public int getDxaGoal() {
        return this._picf.getDxaGoal();
    }

    public int getDyaCropBottom() {
        return this._picf.getDyaReserved2();
    }

    public double getCropBottom() {
        return this.getCrop(EscherPropertyTypes.BLIP__CROPFROMBOTTOM);
    }

    public int getDyaCropTop() {
        return this._picf.getDyaReserved1();
    }

    public double getCropTop() {
        return this.getCrop(EscherPropertyTypes.BLIP__CROPFROMTOP);
    }

    private double getCrop(EscherPropertyTypes propType) {
        Object property;
        EscherOptRecord optRecord;
        EscherContainerRecord shape;
        if (this._picfAndOfficeArtData != null && (shape = this._picfAndOfficeArtData.getShape()) != null && (optRecord = (EscherOptRecord)shape.getChildById(EscherRecordTypes.OPT.typeID)) != null && (property = optRecord.lookup(propType)) instanceof EscherSimpleProperty) {
            EscherSimpleProperty simpleProperty = (EscherSimpleProperty)property;
            return Units.fixedPointToDouble(simpleProperty.getPropertyValue());
        }
        return 0.0;
    }

    public int getDyaGoal() {
        return this._picf.getDyaGoal();
    }

    public int getHeight() {
        if (this.height == -1) {
            this.fillWidthHeight();
        }
        return this.height;
    }

    public int getHorizontalScalingFactor() {
        return this._picf.getMx();
    }

    public String getMimeType() {
        return this.suggestPictureType().getMime();
    }

    public byte[] getRawContent() {
        EscherBlipRecord blip;
        if (this._blipRecords.size() != 1) {
            return new byte[0];
        }
        EscherRecord escherRecord = this._blipRecords.get(0);
        if (escherRecord instanceof EscherBlipRecord) {
            return ((EscherBlipRecord)escherRecord).getPicturedata();
        }
        if (escherRecord instanceof EscherBSERecord && (blip = ((EscherBSERecord)escherRecord).getBlipRecord()) != null) {
            return blip.getPicturedata();
        }
        return new byte[0];
    }

    public int getSize() {
        return this.getContent().length;
    }

    public int getStartOffset() {
        return this.dataBlockStartOfsset;
    }

    public int getVerticalScalingFactor() {
        return this._picf.getMy();
    }

    public int getWidth() {
        if (this.width == -1) {
            this.fillWidthHeight();
        }
        return this.width;
    }

    public String getDescription() {
        for (EscherRecord escherRecord : this._picfAndOfficeArtData.getShape()) {
            if (!(escherRecord instanceof EscherOptRecord)) continue;
            EscherOptRecord escherOptRecord = (EscherOptRecord)escherRecord;
            for (EscherProperty property : escherOptRecord.getEscherProperties()) {
                if (EscherPropertyTypes.GROUPSHAPE__DESCRIPTION.propNumber != property.getPropertyNumber()) continue;
                byte[] complexData = ((EscherComplexProperty)property).getComplexData();
                return StringUtil.getFromUnicodeLE(complexData, 0, complexData.length / 2 - 1);
            }
        }
        return null;
    }

    public String suggestFileExtension() {
        return this.suggestPictureType().getExtension();
    }

    public String suggestFullFileName() {
        String fileExt = this.suggestFileExtension();
        return Integer.toHexString(this.dataBlockStartOfsset) + (fileExt.length() > 0 ? "." + fileExt : "");
    }

    public PictureType suggestPictureType() {
        if (this._blipRecords.size() != 1) {
            return PictureType.UNKNOWN;
        }
        EscherRecord escherRecord = this._blipRecords.get(0);
        if (escherRecord instanceof EscherBSERecord) {
            EscherBSERecord bseRecord = (EscherBSERecord)escherRecord;
            switch (bseRecord.getBlipTypeWin32()) {
                case 0: {
                    return PictureType.UNKNOWN;
                }
                case 1: {
                    return PictureType.UNKNOWN;
                }
                case 2: {
                    return PictureType.EMF;
                }
                case 3: {
                    return PictureType.WMF;
                }
                case 4: {
                    return PictureType.PICT;
                }
                case 5: {
                    return PictureType.JPEG;
                }
                case 6: {
                    return PictureType.PNG;
                }
                case 7: {
                    return PictureType.BMP;
                }
                case 17: {
                    return PictureType.TIFF;
                }
                case 18: {
                    return PictureType.JPEG;
                }
            }
            return PictureType.UNKNOWN;
        }
        Enum<?> recordType = escherRecord.getGenericRecordType();
        assert (recordType instanceof EscherRecordTypes);
        switch ((EscherRecordTypes)recordType) {
            case BLIP_EMF: {
                return PictureType.EMF;
            }
            case BLIP_WMF: {
                return PictureType.WMF;
            }
            case BLIP_PICT: {
                return PictureType.PICT;
            }
            case BLIP_JPEG: {
                return PictureType.JPEG;
            }
            case BLIP_PNG: {
                return PictureType.PNG;
            }
            case BLIP_DIB: {
                return PictureType.BMP;
            }
            case BLIP_TIFF: {
                return PictureType.TIFF;
            }
        }
        return PictureType.UNKNOWN;
    }

    public void writeImageContent(OutputStream out) throws IOException {
        byte[] c = this.getContent();
        if (c != null && c.length > 0) {
            out.write(c, 0, c.length);
        }
    }
}

