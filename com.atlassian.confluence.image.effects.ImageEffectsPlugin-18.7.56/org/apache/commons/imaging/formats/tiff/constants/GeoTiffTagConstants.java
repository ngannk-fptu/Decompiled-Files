/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoDoubles;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShorts;

public final class GeoTiffTagConstants {
    public static final TagInfoDoubles EXIF_TAG_MODEL_PIXEL_SCALE_TAG = new TagInfoDoubles("ModelPixelScaleTag", 33550, 3, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoDoubles EXIF_TAG_INTERGRAPH_MATRIX_TAG = new TagInfoDoubles("IntergraphMatrixTag", 33920, -1, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoDoubles EXIF_TAG_MODEL_TIEPOINT_TAG = new TagInfoDoubles("ModelTiepointTag", 33922, -1, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoDoubles EXIF_TAG_MODEL_TRANSFORMATION_TAG = new TagInfoDoubles("ModelTransformationTag", 34264, 16, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoShorts EXIF_TAG_GEO_KEY_DIRECTORY_TAG = new TagInfoShorts("GeoKeyDirectoryTag", 34735, -1, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoDoubles EXIF_TAG_GEO_DOUBLE_PARAMS_TAG = new TagInfoDoubles("GeoDoubleParamsTag", 34736, -1, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoAscii EXIF_TAG_GEO_ASCII_PARAMS_TAG = new TagInfoAscii("GeoAsciiParamsTag", 34737, -1, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final List<TagInfo> ALL_GEO_TIFF_TAGS = Collections.unmodifiableList(Arrays.asList(EXIF_TAG_MODEL_PIXEL_SCALE_TAG, EXIF_TAG_INTERGRAPH_MATRIX_TAG, EXIF_TAG_MODEL_TIEPOINT_TAG, EXIF_TAG_MODEL_TRANSFORMATION_TAG, EXIF_TAG_GEO_KEY_DIRECTORY_TAG, EXIF_TAG_GEO_DOUBLE_PARAMS_TAG, EXIF_TAG_GEO_ASCII_PARAMS_TAG));

    private GeoTiffTagConstants() {
    }
}

