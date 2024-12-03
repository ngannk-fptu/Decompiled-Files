/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.model;

import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.search.service.ContentTypeEnum;

@Deprecated
public class ContentTypeEnumProperty
extends ImportedProperty {
    private String value;

    public ContentTypeEnumProperty(String name, String value) {
        super(name);
        this.value = value;
    }

    public ContentTypeEnum getEnumValueByRepresentation() throws ClassNotFoundException {
        return ContentTypeEnum.getByRepresentation(this.value);
    }
}

