/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoByte;

public final class TagInfoUnknown
extends TagInfoByte {
    public TagInfoUnknown(String name, int tag, TiffDirectoryType exifDirectory) {
        super(name, tag, FieldType.ANY, exifDirectory);
    }
}

