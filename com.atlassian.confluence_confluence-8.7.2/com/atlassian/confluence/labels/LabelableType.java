/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.EditableLabelable;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.templates.PageTemplate;

public enum LabelableType {
    CONTENT("CONTENT", ContentEntityObject.class),
    PAGE_TEMPLATE("PAGETEMPLATE", PageTemplate.class),
    ATTACHMENT("ATTACHMENT", Attachment.class);

    private final Class<? extends EditableLabelable> clazz;
    private final String type;

    private LabelableType(String type, Class<? extends EditableLabelable> clazz) {
        this.clazz = clazz;
        this.type = type;
    }

    public static String getTypeString(Class<? extends EditableLabelable> labelableClass) {
        return LabelableType.getType(labelableClass).type;
    }

    public static LabelableType getType(Class<? extends EditableLabelable> labelableClass) {
        if (Attachment.class.isAssignableFrom(labelableClass)) {
            return ATTACHMENT;
        }
        for (LabelableType type : LabelableType.values()) {
            if (!type.clazz.isAssignableFrom(labelableClass)) continue;
            return type;
        }
        throw new IllegalArgumentException("Unknown labelable type : " + labelableClass.getName());
    }

    public static LabelableType getFromTypeString(String typeString) {
        for (LabelableType type : LabelableType.values()) {
            if (!type.type.equals(typeString)) continue;
            return type;
        }
        throw new IllegalArgumentException("Unknown labelable type : " + typeString);
    }

    public String typeString() {
        return this.type;
    }

    public Class<? extends EditableLabelable> getLabelableClass() {
        return this.clazz;
    }
}

