/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.util.FileTypeUtil
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.confluence.content.render.xhtml.model.resource;

import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.DefaultStandardTag;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.renderer.util.FileTypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DefaultEmbeddedImage
extends DefaultStandardTag
implements EmbeddedImage {
    private final ResourceIdentifier resourceIdentifier;
    private final String mimeType;
    private String source;
    private String alternativeText;
    private String height;
    private String width;
    private String hspace;
    private String vspace;
    private boolean thumbnail;
    private boolean border;
    private String alignment;
    private String queryParams;

    public DefaultEmbeddedImage(NamedResourceIdentifier resourceIdentifier) {
        if (resourceIdentifier == null) {
            throw new IllegalArgumentException("Resource identifier cannot be null.");
        }
        this.resourceIdentifier = resourceIdentifier;
        String resourceName = (String)StringUtils.defaultIfBlank((CharSequence)resourceIdentifier.getResourceName(), (CharSequence)"");
        if (StringUtils.isBlank((CharSequence)resourceName)) {
            this.mimeType = "image/unknown";
        } else {
            String tempMimeType = FileTypeUtil.getContentType((String)resourceName);
            this.mimeType = resourceIdentifier instanceof UrlResourceIdentifier && "application/octet-stream".equals(tempMimeType) ? "image/unknown" : tempMimeType;
        }
    }

    @Override
    public ResourceIdentifier getResourceIdentifier() {
        return this.resourceIdentifier;
    }

    @Override
    public String getMimeType() {
        return this.mimeType;
    }

    @Override
    public String getAlternativeText() {
        return this.alternativeText;
    }

    @Override
    public String getHeight() {
        return this.height;
    }

    @Override
    public String getSource() {
        return this.source;
    }

    @Override
    public boolean isThumbnail() {
        return this.thumbnail;
    }

    @Override
    public String getWidth() {
        return this.width;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setAlternativeText(String alternativeText) {
        this.alternativeText = alternativeText;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setThumbnail(boolean thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public boolean isBorder() {
        return this.border;
    }

    public void setBorder(boolean border) {
        this.border = border;
    }

    @Override
    public String getAlignment() {
        return this.alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    @Override
    public String getHspace() {
        return this.hspace;
    }

    @Override
    public String getVspace() {
        return this.vspace;
    }

    public void setHspace(String hspace) {
        this.hspace = hspace;
    }

    public void setVspace(String vspace) {
        this.vspace = vspace;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public String getExtraQueryParameters() {
        return this.queryParams;
    }

    public void setExtraQueryParameters(String queryParams) {
        this.queryParams = queryParams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return EqualsBuilder.reflectionEquals((Object)this, (Object)o, (String[])new String[0]);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode((Object)this, (String[])new String[0]);
    }
}

