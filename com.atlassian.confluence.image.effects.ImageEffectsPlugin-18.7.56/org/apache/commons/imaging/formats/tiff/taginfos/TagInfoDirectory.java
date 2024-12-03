/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoLong;

public class TagInfoDirectory
extends TagInfoLong {
    private static final List<FieldType> fieldList = Collections.unmodifiableList(Arrays.asList(FieldType.LONG, FieldType.IFD));

    public TagInfoDirectory(String name, int tag, TiffDirectoryType directoryType) {
        super(name, tag, fieldList, 1, directoryType, true);
    }
}

