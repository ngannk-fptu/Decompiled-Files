/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoBytes;

public class TagInfoUnknowns
extends TagInfoBytes {
    public TagInfoUnknowns(String name, int tag, int length, TiffDirectoryType exifDirectory) {
        super(name, tag, FieldType.ANY, length, exifDirectory);
    }
}

