/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata;

import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Property;

public interface OfficeOpenXMLCore {
    public static final String NAMESPACE_URI = "http://schemas.openxmlformats.org/package/2006/metadata/core-properties/";
    public static final String PREFIX = "cp";
    public static final Property CATEGORY = Property.externalText("cp:category");
    public static final Property CONTENT_STATUS = Property.externalText("cp:contentStatus");
    public static final Property LAST_MODIFIED_BY = Property.externalText("cp:lastModifiedBy");
    public static final Property LAST_PRINTED = Property.externalDate("cp:lastPrinted");
    public static final Property REVISION = Property.externalText("cp:revision");
    public static final Property VERSION = Property.externalText("cp:version");
    @Deprecated
    public static final Property SUBJECT = Property.composite(Property.externalText("cp:subject"), new Property[]{DublinCore.SUBJECT});
}

