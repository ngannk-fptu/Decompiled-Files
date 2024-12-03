/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.metadata.tiff;

import com.twelvemonkeys.imageio.metadata.AbstractEntry;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.tiff.Rational;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import com.twelvemonkeys.lang.Validate;
import java.lang.reflect.Array;

public final class TIFFEntry
extends AbstractEntry {
    private final short type;

    public TIFFEntry(int n, Object object) {
        this(n, TIFFEntry.guessType(object), object);
    }

    public TIFFEntry(int n, short s, Object object) {
        super(n, object);
        if (s < 1 || s >= TIFF.TYPE_NAMES.length) {
            throw new IllegalArgumentException(String.format("Illegal TIFF type: %s", s));
        }
        this.type = s;
    }

    public short getType() {
        return this.type;
    }

    @Override
    public String getFieldName() {
        switch ((Integer)this.getIdentifier()) {
            case 34665: {
                return "EXIF";
            }
            case 40965: {
                return "Interoperability";
            }
            case 34853: {
                return "GPS";
            }
            case 700: {
                return "XMP";
            }
            case 33723: {
                return "IPTC";
            }
            case 34377: {
                return "Adobe";
            }
            case 37724: {
                return "ImageSourceData";
            }
            case 34675: {
                return "ICCProfile";
            }
            case 256: {
                return "ImageWidth";
            }
            case 257: {
                return "ImageHeight";
            }
            case 258: {
                return "BitsPerSample";
            }
            case 259: {
                return "Compression";
            }
            case 262: {
                return "PhotometricInterpretation";
            }
            case 266: {
                return "FillOrder";
            }
            case 269: {
                return "DocumentName";
            }
            case 270: {
                return "ImageDescription";
            }
            case 271: {
                return "Make";
            }
            case 272: {
                return "Model";
            }
            case 273: {
                return "StripOffsets";
            }
            case 274: {
                return "Orientation";
            }
            case 277: {
                return "SamplesPerPixel";
            }
            case 278: {
                return "RowsPerStrip";
            }
            case 279: {
                return "StripByteCounts";
            }
            case 282: {
                return "XResolution";
            }
            case 283: {
                return "YResolution";
            }
            case 284: {
                return "PlanarConfiguration";
            }
            case 296: {
                return "ResolutionUnit";
            }
            case 285: {
                return "PageName";
            }
            case 297: {
                return "PageNumber";
            }
            case 305: {
                return "Software";
            }
            case 306: {
                return "DateTime";
            }
            case 315: {
                return "Artist";
            }
            case 316: {
                return "HostComputer";
            }
            case 317: {
                return "Predictor";
            }
            case 322: {
                return "TileWidth";
            }
            case 323: {
                return "TileHeight";
            }
            case 324: {
                return "TileOffsets";
            }
            case 325: {
                return "TileByteCounts";
            }
            case 33432: {
                return "Copyright";
            }
            case 529: {
                return "YCbCrCoefficients";
            }
            case 530: {
                return "YCbCrSubSampling";
            }
            case 531: {
                return "YCbCrPositioning";
            }
            case 532: {
                return "ReferenceBlackWhite";
            }
            case 320: {
                return "ColorMap";
            }
            case 332: {
                return "InkSet";
            }
            case 333: {
                return "InkNames";
            }
            case 338: {
                return "ExtraSamples";
            }
            case 339: {
                return "SampleFormat";
            }
            case 347: {
                return "JPEGTables";
            }
            case 513: {
                return "JPEGInterchangeFormat";
            }
            case 514: {
                return "JPEGInterchangeFormatLength";
            }
            case 330: {
                return "SubIFD";
            }
            case 254: {
                return "SubfileType";
            }
            case 33434: {
                return "ExposureTime";
            }
            case 33437: {
                return "FNUmber";
            }
            case 34850: {
                return "ExposureProgram";
            }
            case 34855: {
                return "ISOSpeedRatings";
            }
            case 37377: {
                return "ShutterSpeedValue";
            }
            case 37378: {
                return "ApertureValue";
            }
            case 37379: {
                return "BrightnessValue";
            }
            case 37380: {
                return "ExposureBiasValue";
            }
            case 37381: {
                return "MaxApertureValue";
            }
            case 37382: {
                return "SubjectDistance";
            }
            case 37383: {
                return "MeteringMode";
            }
            case 37384: {
                return "LightSource";
            }
            case 37385: {
                return "Flash";
            }
            case 37386: {
                return "FocalLength";
            }
            case 41495: {
                return "SensingMethod";
            }
            case 41728: {
                return "FileSource";
            }
            case 41729: {
                return "SceneType";
            }
            case 41730: {
                return "CFAPattern";
            }
            case 41985: {
                return "CustomRendered";
            }
            case 41986: {
                return "ExposureMode";
            }
            case 41987: {
                return "WhiteBalance";
            }
            case 41988: {
                return "DigitalZoomRatio";
            }
            case 41989: {
                return "FocalLengthIn35mmFilm";
            }
            case 41990: {
                return "SceneCaptureType";
            }
            case 41991: {
                return "GainControl";
            }
            case 41992: {
                return "Contrast";
            }
            case 41993: {
                return "Saturation";
            }
            case 41994: {
                return "Sharpness";
            }
            case 42016: {
                return "ImageUniqueID";
            }
            case 40960: {
                return "FlashpixVersion";
            }
            case 36864: {
                return "ExifVersion";
            }
            case 36867: {
                return "DateTimeOriginal";
            }
            case 36868: {
                return "DateTimeDigitized";
            }
            case 37393: {
                return "ImageNumber";
            }
            case 37500: {
                return "MakerNote";
            }
            case 37510: {
                return "UserComment";
            }
            case 37121: {
                return "ComponentsConfiguration";
            }
            case 37122: {
                return "CompressedBitsPerPixel";
            }
            case 40961: {
                return "ColorSpace";
            }
            case 40962: {
                return "PixelXDimension";
            }
            case 40963: {
                return "PixelYDimension";
            }
        }
        return null;
    }

    @Override
    public String getTypeName() {
        return TIFF.TYPE_NAMES[this.type];
    }

    static short getType(Entry entry) {
        if (entry instanceof TIFFEntry) {
            TIFFEntry tIFFEntry = (TIFFEntry)entry;
            return tIFFEntry.getType();
        }
        Validate.notNull((Object)entry, (String)"entry");
        String string = entry.getTypeName();
        if (string != null) {
            for (int i = 1; i < TIFF.TYPE_NAMES.length; ++i) {
                if (!string.equals(TIFF.TYPE_NAMES[i])) continue;
                return (short)i;
            }
        }
        return TIFFEntry.guessType(entry.getValue());
    }

    private static short guessType(Object object) {
        Object object2 = Validate.notNull((Object)object);
        boolean bl = object2.getClass().isArray();
        if (bl) {
            object2 = Array.get(object2, 0);
        }
        if (object2 instanceof Byte) {
            return 1;
        }
        if (object2 instanceof Short) {
            if (!bl && (Short)object2 < 127) {
                return 1;
            }
            return 3;
        }
        if (object2 instanceof Integer) {
            if (!bl && (Integer)object2 < Short.MAX_VALUE) {
                return 3;
            }
            return 4;
        }
        if (object2 instanceof Long && !bl && (Long)object2 < Integer.MAX_VALUE) {
            return 4;
        }
        if (object2 instanceof Rational) {
            return 5;
        }
        if (object2 instanceof String) {
            return 2;
        }
        throw new UnsupportedOperationException(String.format("Method guessType not implemented for type %s", object2.getClass()));
    }

    static long getValueLength(int n, long l) {
        if (n > 0 && n < TIFF.TYPE_LENGTHS.length) {
            return (long)TIFF.TYPE_LENGTHS[n] * l;
        }
        return -1L;
    }
}

