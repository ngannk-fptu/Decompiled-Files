/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content;

import com.atlassian.confluence.core.ContentEntityObject;
import java.io.Serializable;
import java.util.Date;

public class ContentProperty
implements Serializable {
    private long id;
    private String name;
    private String stringValue;
    private Long longValue;
    private Date dateValue;
    private ContentEntityObject content;

    public ContentProperty() {
    }

    public ContentProperty(ContentProperty contentProperty) {
        this.id = contentProperty.id;
        this.name = contentProperty.name;
        this.stringValue = contentProperty.stringValue;
        this.longValue = contentProperty.longValue;
        this.dateValue = contentProperty.dateValue;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Long getLongValue() {
        return this.longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Date getDateValue() {
        return this.dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public ContentEntityObject getContentEntity() {
        return this.content;
    }

    public void setContentEntity(ContentEntityObject content) {
        this.content = content;
    }
}

