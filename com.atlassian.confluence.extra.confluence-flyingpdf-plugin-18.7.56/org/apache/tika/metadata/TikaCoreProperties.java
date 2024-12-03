/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata;

import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Geographic;
import org.apache.tika.metadata.MSOffice;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Office;
import org.apache.tika.metadata.OfficeOpenXMLCore;
import org.apache.tika.metadata.OfficeOpenXMLExtended;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.XMP;

public interface TikaCoreProperties {
    public static final String TIKA_META_PREFIX = "X-TIKA:";
    public static final String TIKA_META_EXCEPTION_PREFIX = "X-TIKA:EXCEPTION:";
    public static final Property TIKA_META_EXCEPTION_WARNING = Property.internalTextBag("X-TIKA:EXCEPTION:warn");
    public static final Property TIKA_META_EXCEPTION_EMBEDDED_STREAM = Property.internalTextBag("X-TIKA:EXCEPTION:embedded_stream_exception");
    public static final String EMBEDDED_RESOURCE_TYPE_KEY = "embeddedResourceType";
    public static final Property ORIGINAL_RESOURCE_NAME = Property.internalTextBag("X-TIKA:origResourceName");
    public static final Property CONTENT_TYPE_HINT = Property.internalText("Content-Type-Hint");
    public static final Property CONTENT_TYPE_OVERRIDE = Property.internalText("Content-Type-Override");
    public static final Property FORMAT = Property.composite(DublinCore.FORMAT, new Property[]{Property.internalText("format")});
    public static final Property IDENTIFIER = Property.composite(DublinCore.IDENTIFIER, new Property[]{Property.internalText("identifier")});
    public static final Property CONTRIBUTOR = Property.composite(DublinCore.CONTRIBUTOR, new Property[]{Property.internalText("contributor")});
    public static final Property COVERAGE = Property.composite(DublinCore.COVERAGE, new Property[]{Property.internalText("coverage")});
    public static final Property CREATOR = Property.composite(DublinCore.CREATOR, new Property[]{Office.AUTHOR, Property.internalTextBag("creator"), Property.internalTextBag("Author")});
    public static final Property MODIFIER = Property.composite(Office.LAST_AUTHOR, new Property[]{Property.internalText("Last-Author")});
    public static final Property CREATOR_TOOL = XMP.CREATOR_TOOL;
    public static final Property LANGUAGE = Property.composite(DublinCore.LANGUAGE, new Property[]{Property.internalText("language")});
    public static final Property PUBLISHER = Property.composite(DublinCore.PUBLISHER, new Property[]{Property.internalText("publisher")});
    public static final Property RELATION = Property.composite(DublinCore.RELATION, new Property[]{Property.internalText("relation")});
    public static final Property RIGHTS = Property.composite(DublinCore.RIGHTS, new Property[]{Property.internalText("rights")});
    public static final Property SOURCE = Property.composite(DublinCore.SOURCE, new Property[]{Property.internalText("source")});
    public static final Property TYPE = Property.composite(DublinCore.TYPE, new Property[]{Property.internalText("type")});
    public static final Property TITLE = Property.composite(DublinCore.TITLE, new Property[]{Property.internalText("title")});
    public static final Property DESCRIPTION = Property.composite(DublinCore.DESCRIPTION, new Property[]{Property.internalText("description")});
    public static final Property KEYWORDS = Property.composite(DublinCore.SUBJECT, new Property[]{Office.KEYWORDS, Property.internalTextBag("Keywords"), Property.internalTextBag("subject")});
    public static final Property CREATED = Property.composite(DublinCore.CREATED, new Property[]{Office.CREATION_DATE, MSOffice.CREATION_DATE});
    public static final Property MODIFIED = Property.composite(DublinCore.MODIFIED, new Property[]{Metadata.DATE, Office.SAVE_DATE, MSOffice.LAST_SAVED, Property.internalText("modified"), Property.internalText("Last-Modified")});
    public static final Property PRINT_DATE = Property.composite(Office.PRINT_DATE, new Property[]{MSOffice.LAST_PRINTED});
    public static final Property METADATA_DATE = XMP.METADATA_DATE;
    public static final Property LATITUDE = Geographic.LATITUDE;
    public static final Property LONGITUDE = Geographic.LONGITUDE;
    public static final Property ALTITUDE = Geographic.ALTITUDE;
    public static final Property RATING = XMP.RATING;
    public static final Property COMMENTS = Property.composite(OfficeOpenXMLExtended.COMMENTS, new Property[]{Property.internalTextBag("comment"), Property.internalTextBag("Comments")});
    @Deprecated
    public static final Property TRANSITION_KEYWORDS_TO_DC_SUBJECT = Property.composite(DublinCore.SUBJECT, new Property[]{Property.internalTextBag("Keywords")});
    @Deprecated
    public static final Property TRANSITION_SUBJECT_TO_DC_DESCRIPTION = Property.composite(DublinCore.DESCRIPTION, new Property[]{Property.internalText("subject")});
    @Deprecated
    public static final Property TRANSITION_SUBJECT_TO_DC_TITLE = Property.composite(DublinCore.TITLE, new Property[]{Property.internalText("subject")});
    @Deprecated
    public static final Property TRANSITION_SUBJECT_TO_OO_SUBJECT = Property.composite(OfficeOpenXMLCore.SUBJECT, new Property[]{Property.internalText("subject")});
    public static final Property EMBEDDED_RESOURCE_TYPE = Property.internalClosedChoise("embeddedResourceType", EmbeddedResourceType.ATTACHMENT.toString(), EmbeddedResourceType.INLINE.toString());
    public static final Property HAS_SIGNATURE = Property.internalBoolean("hasSignature");

    public static enum EmbeddedResourceType {
        INLINE,
        ATTACHMENT,
        MACRO,
        METADATA,
        FONT,
        THUMBNAIL;

    }
}

