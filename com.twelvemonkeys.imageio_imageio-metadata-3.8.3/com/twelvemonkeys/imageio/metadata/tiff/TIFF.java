/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.metadata.tiff;

public interface TIFF {
    public static final short BYTE_ORDER_MARK_BIG_ENDIAN = 19789;
    public static final short BYTE_ORDER_MARK_LITTLE_ENDIAN = 18761;
    public static final int TIFF_MAGIC = 42;
    public static final int BIGTIFF_MAGIC = 43;
    public static final short TYPE_BYTE = 1;
    public static final short TYPE_ASCII = 2;
    public static final short TYPE_SHORT = 3;
    public static final short TYPE_LONG = 4;
    public static final short TYPE_RATIONAL = 5;
    public static final short TYPE_SBYTE = 6;
    public static final short TYPE_UNDEFINED = 7;
    public static final short TYPE_SSHORT = 8;
    public static final short TYPE_SLONG = 9;
    public static final short TYPE_SRATIONAL = 10;
    public static final short TYPE_FLOAT = 11;
    public static final short TYPE_DOUBLE = 12;
    public static final short TYPE_IFD = 13;
    public static final short TYPE_LONG8 = 16;
    public static final short TYPE_SLONG8 = 17;
    public static final short TYPE_IFD8 = 18;
    public static final String[] TYPE_NAMES = new String[]{null, "BYTE", "ASCII", "SHORT", "LONG", "RATIONAL", "SBYTE", "UNDEFINED", "SSHORT", "SLONG", "SRATIONAL", "FLOAT", "DOUBLE", "IFD", null, null, "LONG8", "SLONG8", "IFD8"};
    public static final int[] TYPE_LENGTHS = new int[]{-1, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8, 4, -1, -1, 8, 8, 8};
    public static final int TAG_EXIF_IFD = 34665;
    public static final int TAG_GPS_IFD = 34853;
    public static final int TAG_INTEROP_IFD = 40965;
    public static final int TAG_IMAGE_WIDTH = 256;
    public static final int TAG_IMAGE_HEIGHT = 257;
    public static final int TAG_BITS_PER_SAMPLE = 258;
    public static final int TAG_COMPRESSION = 259;
    public static final int TAG_PHOTOMETRIC_INTERPRETATION = 262;
    public static final int TAG_FILL_ORDER = 266;
    public static final int TAG_ORIENTATION = 274;
    public static final int TAG_SAMPLES_PER_PIXEL = 277;
    public static final int TAG_PLANAR_CONFIGURATION = 284;
    public static final int TAG_SAMPLE_FORMAT = 339;
    public static final int TAG_YCBCR_SUB_SAMPLING = 530;
    public static final int TAG_YCBCR_POSITIONING = 531;
    public static final int TAG_X_RESOLUTION = 282;
    public static final int TAG_Y_RESOLUTION = 283;
    public static final int TAG_X_POSITION = 286;
    public static final int TAG_Y_POSITION = 287;
    public static final int TAG_RESOLUTION_UNIT = 296;
    public static final int TAG_STRIP_OFFSETS = 273;
    public static final int TAG_ROWS_PER_STRIP = 278;
    public static final int TAG_STRIP_BYTE_COUNTS = 279;
    public static final int TAG_FREE_OFFSETS = 288;
    public static final int TAG_FREE_BYTE_COUNTS = 289;
    public static final int TAG_JPEG_INTERCHANGE_FORMAT = 513;
    public static final int TAG_JPEG_INTERCHANGE_FORMAT_LENGTH = 514;
    public static final int TAG_GROUP3OPTIONS = 292;
    public static final int TAG_GROUP4OPTIONS = 293;
    public static final int TAG_TRANSFER_FUNCTION = 301;
    public static final int TAG_PREDICTOR = 317;
    public static final int TAG_WHITE_POINT = 318;
    public static final int TAG_PRIMARY_CHROMATICITIES = 319;
    public static final int TAG_COLOR_MAP = 320;
    public static final int TAG_INK_SET = 332;
    public static final int TAG_INK_NAMES = 333;
    public static final int TAG_NUMBER_OF_INKS = 334;
    public static final int TAG_EXTRA_SAMPLES = 338;
    public static final int TAG_TRANSFER_RANGE = 342;
    public static final int TAG_YCBCR_COEFFICIENTS = 529;
    public static final int TAG_REFERENCE_BLACK_WHITE = 532;
    public static final int TAG_DATE_TIME = 306;
    public static final int TAG_DOCUMENT_NAME = 269;
    public static final int TAG_IMAGE_DESCRIPTION = 270;
    public static final int TAG_MAKE = 271;
    public static final int TAG_MODEL = 272;
    public static final int TAG_PAGE_NAME = 285;
    public static final int TAG_PAGE_NUMBER = 297;
    public static final int TAG_SOFTWARE = 305;
    public static final int TAG_ARTIST = 315;
    public static final int TAG_HOST_COMPUTER = 316;
    public static final int TAG_COPYRIGHT = 33432;
    public static final int TAG_SUBFILE_TYPE = 254;
    public static final int TAG_OLD_SUBFILE_TYPE = 255;
    public static final int TAG_SUB_IFD = 330;
    public static final int TAG_XMP = 700;
    public static final int TAG_IPTC = 33723;
    public static final int TAG_PHOTOSHOP = 34377;
    public static final int TAG_PHOTOSHOP_IMAGE_SOURCE_DATA = 37724;
    public static final int TAG_PHOTOSHOP_ANNOTATIONS = 50255;
    public static final int TAG_ICC_PROFILE = 34675;
    public static final int TAG_MODI_BLC = 34718;
    public static final int TAG_MODI_VECTOR = 34719;
    public static final int TAG_MODI_PTC = 34720;
    public static final int TAG_MODI_PLAIN_TEXT = 37679;
    public static final int TAG_MODI_OLE_PROPERTY_SET = 37680;
    public static final int TAG_MODI_TEXT_POS_INFO = 37681;
    public static final int TAG_TILE_WIDTH = 322;
    public static final int TAG_TILE_HEIGTH = 323;
    public static final int TAG_TILE_OFFSETS = 324;
    public static final int TAG_TILE_BYTE_COUNTS = 325;
    public static final int TAG_JPEG_TABLES = 347;
    public static final int TAG_OLD_JPEG_PROC = 512;
    public static final int TAG_OLD_JPEG_Q_TABLES = 519;
    public static final int TAG_OLD_JPEG_DC_TABLES = 520;
    public static final int TAG_OLD_JPEG_AC_TABLES = 521;
}

