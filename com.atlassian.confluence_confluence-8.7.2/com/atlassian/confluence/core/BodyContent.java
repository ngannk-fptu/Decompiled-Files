/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.content.ContentCleaner;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.google.common.base.Throwables;
import java.io.Serializable;

public class BodyContent
implements Cloneable,
Serializable {
    private long id;
    private ContentEntityObject content;
    private String body;
    private boolean bodyCleaned = false;
    private BodyType bodyType;

    public BodyContent() {
    }

    public BodyContent(BodyContent orig) {
        this.shallowCopy(orig);
    }

    public BodyContent(ContentEntityObject content, String body, BodyType bodyType) {
        this.content = content;
        this.body = body;
        this.bodyType = bodyType;
        this.bodyCleaned = false;
    }

    public void shallowCopy(BodyContent orig) {
        this.content = orig.getContent();
        this.body = orig.getBody();
        this.bodyType = orig.getBodyType();
        this.bodyCleaned = orig.isBodyCleaned();
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        if (body != null && body.equals(this.body)) {
            return;
        }
        if (body == null) {
            body = "";
        }
        this.body = body;
        this.bodyCleaned = false;
    }

    public BodyType getBodyType() {
        return this.bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        if (bodyType != null && bodyType.equals(this.bodyType)) {
            return;
        }
        this.bodyType = bodyType;
        this.bodyCleaned = false;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ContentEntityObject getContent() {
        return this.content;
    }

    public void setContent(ContentEntityObject content) {
        this.content = content;
    }

    public boolean isBodyCleaned() {
        return this.bodyCleaned;
    }

    public String cleanBody(ContentCleaner cleaner) {
        this.body = cleaner.clean(this.body);
        this.bodyCleaned = true;
        return this.body;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BodyContent that = (BodyContent)o;
        if (this.id != that.id) {
            return false;
        }
        if (!this.body.equals(that.body)) {
            return false;
        }
        if (this.content.getId() != that.content.getId()) {
            return false;
        }
        if (this.bodyCleaned != that.bodyCleaned) {
            return false;
        }
        if (this.bodyType == null) {
            return that.bodyType == null;
        }
        return this.bodyType.equals(that.bodyType);
    }

    public int hashCode() {
        int result = (int)(this.id ^ this.id >>> 32);
        long contentId = this.content == null ? 0L : this.content.getId();
        result = 29 * result + (int)(contentId ^ contentId >>> 32);
        result = 29 * result + (this.bodyCleaned ? 1 : 0);
        result = 29 * result + this.body.hashCode();
        if (this.bodyType != null) {
            result = 29 * this.bodyType.hashCode();
        }
        return result;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw Throwables.propagate((Throwable)ex);
        }
    }
}

