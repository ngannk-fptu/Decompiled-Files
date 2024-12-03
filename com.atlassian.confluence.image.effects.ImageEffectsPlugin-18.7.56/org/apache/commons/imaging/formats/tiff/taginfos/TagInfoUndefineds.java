/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoBytes;

public class TagInfoUndefineds
extends TagInfoBytes {
    public TagInfoUndefineds(String name, int tag, int length, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.UNDEFINED, length, directoryType);
    }
}

