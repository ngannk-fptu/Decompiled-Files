/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata;

import org.apache.tika.metadata.Property;

public interface OfficeOpenXMLExtended {
    public static final String NAMESPACE_URI = "http://schemas.openxmlformats.org/officeDocument/2006/extended-properties/";
    public static final String WORD_PROCESSING_NAMESPACE_URI = "http://schemas.openxmlformats.org/wordprocessingml/2006/main";
    public static final String PREFIX = "extended-properties";
    public static final String WORD_PROCESSING_PREFIX = "w";
    public static final String SECURITY_NONE = "None";
    public static final String SECURITY_PASSWORD_PROTECTED = "PasswordProtected";
    public static final String SECURITY_READ_ONLY_RECOMMENDED = "ReadOnlyRecommended";
    public static final String SECURITY_READ_ONLY_ENFORCED = "ReadOnlyEnforced";
    public static final String SECURITY_LOCKED_FOR_ANNOTATIONS = "LockedForAnnotations";
    public static final String SECURITY_UNKNOWN = "Unknown";
    public static final Property TEMPLATE = Property.externalText("extended-properties:Template");
    public static final Property MANAGER = Property.externalTextBag("extended-properties:Manager");
    public static final Property COMPANY = Property.externalText("extended-properties:Company");
    public static final Property PRESENTATION_FORMAT = Property.externalText("extended-properties:PresentationFormat");
    public static final Property NOTES = Property.externalInteger("extended-properties:Notes");
    public static final Property TOTAL_TIME = Property.externalInteger("extended-properties:TotalTime");
    public static final Property HIDDEN_SLIDES = Property.externalInteger("extended-properties:HiddedSlides");
    public static final Property APPLICATION = Property.externalText("extended-properties:Application");
    public static final Property APP_VERSION = Property.externalText("extended-properties:AppVersion");
    public static final Property DOC_SECURITY = Property.externalInteger("extended-properties:DocSecurity");
    public static final Property DOC_SECURITY_STRING = Property.externalClosedChoise("extended-properties:DocSecurityString", "None", "PasswordProtected", "ReadOnlyRecommended", "ReadOnlyEnforced", "LockedForAnnotations", "Unknown");
    public static final Property COMMENTS = Property.externalTextBag("w:comments");
}

