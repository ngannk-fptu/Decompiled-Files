/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata;

import org.apache.tika.metadata.Property;

public interface MSOffice {
    @Deprecated
    public static final String KEYWORDS = "Keywords";
    @Deprecated
    public static final String COMMENTS = "Comments";
    @Deprecated
    public static final String LAST_AUTHOR = "Last-Author";
    @Deprecated
    public static final String AUTHOR = "Author";
    @Deprecated
    public static final String APPLICATION_NAME = "Application-Name";
    @Deprecated
    public static final String REVISION_NUMBER = "Revision-Number";
    @Deprecated
    public static final String TEMPLATE = "Template";
    @Deprecated
    public static final String TOTAL_TIME = "Total-Time";
    @Deprecated
    public static final String PRESENTATION_FORMAT = "Presentation-Format";
    @Deprecated
    public static final String NOTES = "Notes";
    @Deprecated
    public static final String MANAGER = "Manager";
    @Deprecated
    public static final String APPLICATION_VERSION = "Application-Version";
    @Deprecated
    public static final String VERSION = "Version";
    @Deprecated
    public static final String CONTENT_STATUS = "Content-Status";
    @Deprecated
    public static final String CATEGORY = "Category";
    @Deprecated
    public static final String COMPANY = "Company";
    @Deprecated
    public static final String SECURITY = "Security";
    @Deprecated
    public static final Property SLIDE_COUNT = Property.internalInteger("Slide-Count");
    @Deprecated
    public static final Property PAGE_COUNT = Property.internalInteger("Page-Count");
    @Deprecated
    public static final Property PARAGRAPH_COUNT = Property.internalInteger("Paragraph-Count");
    @Deprecated
    public static final Property LINE_COUNT = Property.internalInteger("Line-Count");
    @Deprecated
    public static final Property WORD_COUNT = Property.internalInteger("Word-Count");
    @Deprecated
    public static final Property CHARACTER_COUNT = Property.internalInteger("Character Count");
    @Deprecated
    public static final Property CHARACTER_COUNT_WITH_SPACES = Property.internalInteger("Character-Count-With-Spaces");
    @Deprecated
    public static final Property TABLE_COUNT = Property.internalInteger("Table-Count");
    @Deprecated
    public static final Property IMAGE_COUNT = Property.internalInteger("Image-Count");
    @Deprecated
    public static final Property OBJECT_COUNT = Property.internalInteger("Object-Count");
    public static final String EDIT_TIME = "Edit-Time";
    @Deprecated
    public static final Property CREATION_DATE = Property.internalDate("Creation-Date");
    @Deprecated
    public static final Property LAST_SAVED = Property.internalDate("Last-Save-Date");
    @Deprecated
    public static final Property LAST_PRINTED = Property.internalDate("Last-Printed");
    public static final String USER_DEFINED_METADATA_NAME_PREFIX = "custom:";
}

