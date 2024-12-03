/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.content.render.xhtml.model;

import com.atlassian.confluence.xhtml.api.StandardTag;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DefaultStandardTag
implements StandardTag {
    protected String htmlClass;
    protected String id;
    protected String style;
    protected String title;

    @Override
    public String getHtmlClass() {
        return this.htmlClass;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getStyle() {
        return this.style;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public void setHtmlClass(String htmlClass) {
        this.htmlClass = htmlClass;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals((Object)this, (Object)obj, (String[])new String[0]);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode((Object)this, (String[])new String[0]);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}

