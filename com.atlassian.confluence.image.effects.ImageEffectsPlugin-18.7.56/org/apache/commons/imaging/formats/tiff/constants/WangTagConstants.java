/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoBytes;

public final class WangTagConstants {
    public static final TagInfoBytes EXIF_TAG_WANG_ANNOTATION = new TagInfoBytes("WangAnnotation", 32932, -1, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    public static final List<TagInfo> ALL_WANG_TAGS = Collections.unmodifiableList(Arrays.asList(EXIF_TAG_WANG_ANNOTATION));

    private WangTagConstants() {
    }
}

