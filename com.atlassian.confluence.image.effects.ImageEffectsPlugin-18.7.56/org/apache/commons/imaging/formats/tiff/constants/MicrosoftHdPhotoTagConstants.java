/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.imaging.common.BinaryConstant;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoByte;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoBytes;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoFloat;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoLong;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoUndefineds;

public final class MicrosoftHdPhotoTagConstants {
    public static final TagInfoBytes EXIF_TAG_PIXEL_FORMAT = new TagInfoBytes("PixelFormat", 48129, 16, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_BLACK_AND_WHITE = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)5);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_8_BIT_GRAY = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)8);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_16_BIT_BGR555 = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)9);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_16_BIT_BGR565 = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)10);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_16_BIT_GRAY = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)11);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_24_BIT_BGR = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)12);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_24_BIT_RGB = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)13);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_32_BIT_BGR = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)14);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_32_BIT_BGRA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)15);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_32_BIT_PBGRA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)16);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_32_BIT_GRAY_FLOAT = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)17);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_48_BIT_RGB_FIXED_POINT = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)18);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_32_BIT_BGR101010 = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)19);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_48_BIT_RGB = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)21);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_64_BIT_RGBA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)22);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_64_BIT_PRGBA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)23);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_96_BIT_RGB_FIXED_POINT = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)24);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_128_BIT_RGBA_FLOAT = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)25);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_128_BIT_PRGBA_FLOAT = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)26);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_128_BIT_RGB_FLOAT = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)27);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_32_BIT_CMYK = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)28);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_64_BIT_RGBA_FIXED_POINT = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)29);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_128_BIT_RGBA_FIXED_POINT = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)30);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_64_BIT_CMYK = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)31);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_24_BIT_3_CHANNELS = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)32);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_32_BIT_4_CHANNELS = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)33);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_40_BIT_5_CHANNELS = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)34);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_48_BIT_6_CHANNELS = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)35);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_56_BIT_7_CHANNELS = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)36);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_64_BIT_8_CHANNELS = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)37);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_48_BIT_3_CHANNELS = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)38);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_64_BIT_4_CHANNELS = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)39);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_80_BIT_5_CHANNELS = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)40);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_96_BIT_6_CHANNELS = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)41);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_112_BIT_7_CHANNELS = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)42);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_128_BIT_8_CHANNELS = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)43);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_40_BIT_CMYK_ALPHA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)44);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_80_BIT_CMYK_ALPHA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)45);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_32_BIT_3_CHANNELS_ALPHA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)46);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_40_BIT_4_CHANNELS_ALPHA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)47);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_48_BIT_5_CHANNELS_ALPHA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)48);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_56_BIT_6_CHANNELS_ALPHA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)49);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_64_BIT_7_CHANNELS_ALPHA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)50);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_72_BIT_8_CHANNELS_ALPHA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)51);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_64_BIT_3_CHANNELS_ALPHA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)52);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_80_BIT_4_CHANNELS_ALPHA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)53);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_96_BIT_5_CHANNELS_ALPHA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)54);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_112_BIT_6_CHANNELS_ALPHA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)55);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_128_BIT_7_CHANNELS_ALPHA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)56);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_144_BIT_8_CHANNELS_ALPHA = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)57);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_64_BIT_RGBA_HALF = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)58);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_48_BIT_RGB_HALF = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)59);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_32_BIT_RGBE = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)61);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_16_BIT_GRAY_HALF = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)62);
    public static final BinaryConstant PIXEL_FORMAT_VALUE_32_BIT_GRAY_FIXED_POINT = MicrosoftHdPhotoTagConstants.createMicrosoftHdPhotoGuidEndingWith((byte)63);
    public static final TagInfoLong EXIF_TAG_TRANSFOMATION = new TagInfoLong("Transfomation", 48130, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final int TRANSFOMATION_VALUE_HORIZONTAL_NORMAL = 0;
    public static final int TRANSFOMATION_VALUE_MIRROR_VERTICAL = 1;
    public static final int TRANSFOMATION_VALUE_MIRROR_HORIZONTAL = 2;
    public static final int TRANSFOMATION_VALUE_ROTATE_180 = 3;
    public static final int TRANSFOMATION_VALUE_ROTATE_90_CW = 4;
    public static final int TRANSFOMATION_VALUE_MIRROR_HORIZONTAL_AND_ROTATE_90_CW = 5;
    public static final int TRANSFOMATION_VALUE_MIRROR_HORIZONTAL_AND_ROTATE_270_CW = 6;
    public static final int TRANSFOMATION_VALUE_ROTATE_270_CW = 7;
    public static final TagInfoLong EXIF_TAG_UNCOMPRESSED = new TagInfoLong("Uncompressed", 48131, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final int UNCOMPRESSED_VALUE_NO = 0;
    public static final int UNCOMPRESSED_VALUE_YES = 1;
    public static final TagInfoLong EXIF_TAG_IMAGE_TYPE = new TagInfoLong("ImageType", 48132, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoLong EXIF_TAG_IMAGE_WIDTH = new TagInfoLong("ImageWidth", 48256, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoLong EXIF_TAG_IMAGE_HEIGHT = new TagInfoLong("ImageHeight", 48257, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoFloat EXIF_TAG_WIDTH_RESOLUTION = new TagInfoFloat("WidthResolution", 48258, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoFloat EXIF_TAG_HEIGHT_RESOLUTION = new TagInfoFloat("HeightResolution", 48259, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoLong EXIF_TAG_IMAGE_OFFSET = new TagInfoLong("ImageOffset", 48320, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoLong EXIF_TAG_IMAGE_BYTE_COUNT = new TagInfoLong("ImageByteCount", 48321, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoLong EXIF_TAG_ALPHA_OFFSET = new TagInfoLong("AlphaOffset", 48322, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoLong EXIF_TAG_ALPHA_BYTE_COUNT = new TagInfoLong("AlphaByteCount", 48323, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoByte EXIF_TAG_IMAGE_DATA_DISCARD = new TagInfoByte("ImageDataDiscard", 48324, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final int IMAGE_DATA_DISCARD_VALUE_FULL_RESOLUTION = 0;
    public static final int IMAGE_DATA_DISCARD_VALUE_FLEXBITS_DISCARDED = 1;
    public static final int IMAGE_DATA_DISCARD_VALUE_HIGH_PASS_FREQUENCY_DATA_DISCARDED = 2;
    public static final int IMAGE_DATA_DISCARD_VALUE_HIGHPASS_AND_LOW_PASS_FREQUENCY_DATA_DISCARDED = 3;
    public static final TagInfoByte EXIF_TAG_ALPHA_DATA_DISCARD = new TagInfoByte("AlphaDataDiscard", 48325, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final int ALPHA_DATA_DISCARD_VALUE_FULL_RESOLUTION = 0;
    public static final int ALPHA_DATA_DISCARD_VALUE_FLEXBITS_DISCARDED = 1;
    public static final int ALPHA_DATA_DISCARD_VALUE_HIGH_PASS_FREQUENCY_DATA_DISCARDED = 2;
    public static final int ALPHA_DATA_DISCARD_VALUE_HIGHPASS_AND_LOW_PASS_FREQUENCY_DATA_DISCARDED = 3;
    public static final TagInfoUndefineds EXIF_TAG_PADDING = new TagInfoUndefineds("Padding", 59932, -1, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final List<TagInfo> ALL_MICROSOFT_HD_PHOTO_TAGS = Collections.unmodifiableList(Arrays.asList(EXIF_TAG_PIXEL_FORMAT, EXIF_TAG_TRANSFOMATION, EXIF_TAG_UNCOMPRESSED, EXIF_TAG_IMAGE_TYPE, EXIF_TAG_IMAGE_WIDTH, EXIF_TAG_IMAGE_HEIGHT, EXIF_TAG_WIDTH_RESOLUTION, EXIF_TAG_HEIGHT_RESOLUTION, EXIF_TAG_IMAGE_OFFSET, EXIF_TAG_IMAGE_BYTE_COUNT, EXIF_TAG_ALPHA_OFFSET, EXIF_TAG_ALPHA_BYTE_COUNT, EXIF_TAG_IMAGE_DATA_DISCARD, EXIF_TAG_ALPHA_DATA_DISCARD, EXIF_TAG_PADDING));

    private static BinaryConstant createMicrosoftHdPhotoGuidEndingWith(byte end) {
        return new BinaryConstant(new byte[]{36, -61, -35, 111, 3, 78, -2, 75, -79, -123, 61, 119, 118, -115, -55, end});
    }

    private MicrosoftHdPhotoTagConstants() {
    }
}

