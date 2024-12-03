/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoByte;

public class TagInfoUndefined
extends TagInfoByte {
    public TagInfoUndefined(String name, int tag, TiffDirectoryType directoryType) {
        super(name, tag, (FieldType)FieldType.UNDEFINED, directoryType);
    }
}

