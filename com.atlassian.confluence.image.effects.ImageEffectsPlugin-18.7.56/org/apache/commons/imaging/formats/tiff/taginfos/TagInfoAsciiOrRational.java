/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoAsciiOrRational
extends TagInfo {
    public TagInfoAsciiOrRational(String name, int tag, int length, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.ASCII_OR_RATIONAL, length, directoryType, false);
    }
}

