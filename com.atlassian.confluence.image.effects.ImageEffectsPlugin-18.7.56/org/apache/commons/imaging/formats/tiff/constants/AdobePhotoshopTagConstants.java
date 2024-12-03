/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoUndefineds;

public final class AdobePhotoshopTagConstants {
    public static final TagInfoUndefineds EXIF_TAG_JPEGTABLES = new TagInfoUndefineds("JPEGTables", 347, -1, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final TagInfoUndefineds EXIF_TAG_IMAGE_SOURCE_DATA = new TagInfoUndefineds("ImageSourceData", 37724, -1, TiffDirectoryType.EXIF_DIRECTORY_IFD0);
    public static final List<TagInfo> ALL_ADOBE_PHOTOSHOP_TAGS = Collections.unmodifiableList(Arrays.asList(EXIF_TAG_JPEGTABLES, EXIF_TAG_IMAGE_SOURCE_DATA));

    private AdobePhotoshopTagConstants() {
    }
}

