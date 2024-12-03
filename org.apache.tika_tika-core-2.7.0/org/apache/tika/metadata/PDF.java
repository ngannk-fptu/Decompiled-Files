/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata;

import org.apache.tika.metadata.Property;

public interface PDF {
    public static final String PDF_PREFIX = "pdf:";
    public static final String PDFA_PREFIX = "pdfa:";
    public static final String PDFAID_PREFIX = "pdfaid:";
    public static final String PDF_DOC_INFO_PREFIX = "pdf:docinfo:";
    public static final String PDF_DOC_INFO_CUSTOM_PREFIX = "pdf:docinfo:custom:";
    public static final Property DOC_INFO_CREATED = Property.internalDate("pdf:docinfo:created");
    public static final Property DOC_INFO_CREATOR = Property.internalText("pdf:docinfo:creator");
    public static final Property DOC_INFO_CREATOR_TOOL = Property.internalText("pdf:docinfo:creator_tool");
    public static final Property DOC_INFO_MODIFICATION_DATE = Property.internalDate("pdf:docinfo:modified");
    public static final Property DOC_INFO_KEY_WORDS = Property.internalText("pdf:docinfo:keywords");
    public static final Property DOC_INFO_PRODUCER = Property.internalText("pdf:docinfo:producer");
    public static final Property DOC_INFO_SUBJECT = Property.internalText("pdf:docinfo:subject");
    public static final Property DOC_INFO_TITLE = Property.internalText("pdf:docinfo:title");
    public static final Property DOC_INFO_TRAPPED = Property.internalText("pdf:docinfo:trapped");
    public static final Property PDF_VERSION = Property.internalRational("pdf:PDFVersion");
    public static final Property PDFA_VERSION = Property.internalRational("pdfa:PDFVersion");
    public static final Property PDF_EXTENSION_VERSION = Property.internalRational("pdf:PDFExtensionVersion");
    public static final Property PDFAID_CONFORMANCE = Property.internalText("pdfaid:conformance");
    public static final Property PDFAID_PART = Property.internalInteger("pdfaid:part");
    public static final Property PDFUAID_PART = Property.internalInteger("pdfuaid:part");
    public static final Property PDFVT_VERSION = Property.internalText("pdfvt:version");
    public static final Property PDFVT_MODIFIED = Property.internalDate("pdfvt:modified");
    public static final Property PDFXID_VERSION = Property.internalText("pdfxid:version");
    public static final Property PDFX_VERSION = Property.internalText("pdfx:version");
    public static final Property PDFX_CONFORMANCE = Property.internalText("pdfx:conformance");
    public static final Property IS_ENCRYPTED = Property.internalBoolean("pdf:encrypted");
    public static final Property PRODUCER = Property.internalText("pdf:producer");
    public static final Property ACTION_TRIGGER = Property.internalText("pdf:actionTrigger");
    public static final Property ACTION_TRIGGERS = Property.internalTextBag("pdf:actionTriggers");
    public static final Property ACTION_TYPES = Property.internalTextBag("pdf:actionTypes");
    public static final Property CHARACTERS_PER_PAGE = Property.internalIntegerSequence("pdf:charsPerPage");
    public static final Property UNMAPPED_UNICODE_CHARS_PER_PAGE = Property.internalIntegerSequence("pdf:unmappedUnicodeCharsPerPage");
    public static final Property TOTAL_UNMAPPED_UNICODE_CHARS = Property.internalInteger("pdf:totalUnmappedUnicodeChars");
    public static final Property OVERALL_PERCENTAGE_UNMAPPED_UNICODE_CHARS = Property.internalReal("pdf:overallPercentageUnmappedUnicodeChars");
    public static final Property CONTAINS_DAMAGED_FONT = Property.internalBoolean("pdf:containsDamagedFont");
    public static final Property CONTAINS_NON_EMBEDDED_FONT = Property.internalBoolean("pdf:containsNonEmbeddedFont");
    public static final Property HAS_XFA = Property.internalBoolean("pdf:hasXFA");
    public static final Property HAS_XMP = Property.internalBoolean("pdf:hasXMP");
    public static final Property XMP_LOCATION = Property.internalText("pdf:xmpLocation");
    public static final Property HAS_ACROFORM_FIELDS = Property.internalBoolean("pdf:hasAcroFormFields");
    public static final Property HAS_MARKED_CONTENT = Property.internalBoolean("pdf:hasMarkedContent");
    public static final Property HAS_COLLECTION = Property.internalBoolean("pdf:hasCollection");
    public static final Property EMBEDDED_FILE_DESCRIPTION = Property.externalText("pdf:embeddedFileDescription");
    public static final Property EMBEDDED_FILE_ANNOTATION_TYPE = Property.internalText("pdf:embeddedFileAnnotationType");
    public static final Property EMBEDDED_FILE_SUBTYPE = Property.internalText("pdf:embeddedFileSubtype");
    public static final Property HAS_3D = Property.internalBoolean("pdf:has3D");
    public static final Property ANNOTATION_TYPES = Property.internalTextBag("pdf:annotationTypes");
    public static final Property ANNOTATION_SUBTYPES = Property.internalTextBag("pdf:annotationSubtypes");
    public static final Property NUM_3D_ANNOTATIONS = Property.internalInteger("pdf:num3DAnnotations");
}

