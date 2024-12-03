/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata;

import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Geographic;
import org.apache.tika.metadata.Office;
import org.apache.tika.metadata.OfficeOpenXMLExtended;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.XMP;

public interface TikaCoreProperties {
    public static final String NAMESPACE_PREFIX_DELIMITER = ":";
    public static final String TIKA_META_PREFIX = "X-TIKA:";
    public static final Property EMBEDDED_DEPTH = Property.internalInteger("X-TIKA:embedded_depth");
    public static final Property EMBEDDED_RESOURCE_PATH = Property.internalText("X-TIKA:embedded_resource_path");
    public static final Property PARSE_TIME_MILLIS = Property.internalText("X-TIKA:parse_time_millis");
    public static final Property TIKA_CONTENT_HANDLER = Property.internalText("X-TIKA:content_handler");
    public static final Property TIKA_CONTENT = Property.internalText("X-TIKA:content");
    public static final String TIKA_META_EXCEPTION_PREFIX = "X-TIKA:EXCEPTION:";
    public static final String TIKA_META_WARN_PREFIX = "X-TIKA:WARN:";
    public static final Property CONTAINER_EXCEPTION = Property.internalText("X-TIKA:EXCEPTION:container_exception");
    public static final Property EMBEDDED_EXCEPTION = Property.internalTextBag("X-TIKA:EXCEPTION:embedded_exception");
    public static final Property EMBEDDED_WARNING = Property.internalTextBag("X-TIKA:EXCEPTION:embedded_warning");
    public static final Property WRITE_LIMIT_REACHED = Property.internalBoolean("X-TIKA:EXCEPTION:write_limit_reached");
    public static final Property TIKA_META_EXCEPTION_WARNING = Property.internalTextBag("X-TIKA:EXCEPTION:warn");
    public static final Property TRUNCATED_METADATA = Property.internalBoolean("X-TIKA:WARN:truncated_metadata");
    public static final Property TIKA_META_EXCEPTION_EMBEDDED_STREAM = Property.internalTextBag("X-TIKA:EXCEPTION:embedded_stream_exception");
    public static final Property TIKA_PARSED_BY = Property.internalTextBag("X-TIKA:Parsed-By");
    public static final Property TIKA_PARSED_BY_FULL_SET = Property.internalTextBag("X-TIKA:Parsed-By-Full-Set");
    public static final Property TIKA_DETECTED_LANGUAGE = Property.externalTextBag("X-TIKA:detected_language");
    public static final Property TIKA_DETECTED_LANGUAGE_CONFIDENCE = Property.externalTextBag("X-TIKA:detected_language_confidence");
    public static final Property TIKA_DETECTED_LANGUAGE_CONFIDENCE_RAW = Property.externalRealSeq("X-TIKA:detected_language_confidence_raw");
    public static final String RESOURCE_NAME_KEY = "resourceName";
    public static final String PROTECTED = "protected";
    public static final String EMBEDDED_RELATIONSHIP_ID = "embeddedRelationshipId";
    public static final String EMBEDDED_STORAGE_CLASS_ID = "embeddedStorageClassId";
    public static final String EMBEDDED_RESOURCE_TYPE_KEY = "embeddedResourceType";
    public static final Property ORIGINAL_RESOURCE_NAME = Property.internalTextBag("X-TIKA:origResourceName");
    public static final Property SOURCE_PATH = Property.internalText("X-TIKA:sourcePath");
    public static final Property CONTENT_TYPE_HINT = Property.internalText("Content-Type-Hint");
    public static final Property CONTENT_TYPE_USER_OVERRIDE = Property.internalText("Content-Type-Override");
    public static final Property CONTENT_TYPE_PARSER_OVERRIDE = Property.internalText("Content-Type-Parser-Override");
    public static final Property FORMAT = DublinCore.FORMAT;
    public static final Property IDENTIFIER = DublinCore.IDENTIFIER;
    public static final Property CONTRIBUTOR = DublinCore.CONTRIBUTOR;
    public static final Property COVERAGE = DublinCore.COVERAGE;
    public static final Property CREATOR = DublinCore.CREATOR;
    public static final Property MODIFIER = Office.LAST_AUTHOR;
    public static final Property CREATOR_TOOL = XMP.CREATOR_TOOL;
    public static final Property LANGUAGE = DublinCore.LANGUAGE;
    public static final Property PUBLISHER = DublinCore.PUBLISHER;
    public static final Property RELATION = DublinCore.RELATION;
    public static final Property RIGHTS = DublinCore.RIGHTS;
    public static final Property SOURCE = DublinCore.SOURCE;
    public static final Property TYPE = DublinCore.TYPE;
    public static final Property TITLE = DublinCore.TITLE;
    public static final Property DESCRIPTION = DublinCore.DESCRIPTION;
    public static final Property SUBJECT = DublinCore.SUBJECT;
    public static final Property CREATED = DublinCore.CREATED;
    public static final Property MODIFIED = DublinCore.MODIFIED;
    public static final Property PRINT_DATE = Office.PRINT_DATE;
    public static final Property METADATA_DATE = XMP.METADATA_DATE;
    public static final Property LATITUDE = Geographic.LATITUDE;
    public static final Property LONGITUDE = Geographic.LONGITUDE;
    public static final Property ALTITUDE = Geographic.ALTITUDE;
    public static final Property RATING = XMP.RATING;
    public static final Property COMMENTS = OfficeOpenXMLExtended.COMMENTS;
    public static final Property EMBEDDED_RESOURCE_TYPE = Property.internalClosedChoise("embeddedResourceType", EmbeddedResourceType.ATTACHMENT.toString(), EmbeddedResourceType.INLINE.toString(), EmbeddedResourceType.METADATA.toString(), EmbeddedResourceType.MACRO.toString(), EmbeddedResourceType.THUMBNAIL.toString(), EmbeddedResourceType.RENDERING.toString());
    public static final Property HAS_SIGNATURE = Property.internalBoolean("hasSignature");

    public static enum EmbeddedResourceType {
        INLINE,
        ATTACHMENT,
        MACRO,
        METADATA,
        FONT,
        THUMBNAIL,
        RENDERING;

    }
}

