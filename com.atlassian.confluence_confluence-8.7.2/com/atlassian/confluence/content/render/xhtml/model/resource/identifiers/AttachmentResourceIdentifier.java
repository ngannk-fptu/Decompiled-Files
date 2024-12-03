/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AttachmentResourceIdentifier
implements NamedResourceIdentifier {
    private final AttachmentContainerResourceIdentifier attachmentContainerResourceIdentifier;
    private final String filename;

    public AttachmentResourceIdentifier(String filename) {
        this(null, filename);
    }

    public AttachmentResourceIdentifier(AttachmentContainerResourceIdentifier attachmentContainerResourceIdentifier, String filename) {
        if (StringUtils.isBlank((CharSequence)filename)) {
            throw new IllegalArgumentException("filename cannot be null or blank.");
        }
        this.attachmentContainerResourceIdentifier = attachmentContainerResourceIdentifier;
        this.filename = filename;
    }

    @Override
    public String getResourceName() {
        return this.filename;
    }

    public String getFilename() {
        return this.filename;
    }

    public AttachmentContainerResourceIdentifier getAttachmentContainerResourceIdentifier() {
        return this.attachmentContainerResourceIdentifier;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AttachmentResourceIdentifier that = (AttachmentResourceIdentifier)o;
        if (this.attachmentContainerResourceIdentifier != null ? !this.attachmentContainerResourceIdentifier.equals(that.attachmentContainerResourceIdentifier) : that.attachmentContainerResourceIdentifier != null) {
            return false;
        }
        return this.filename.equals(that.filename);
    }

    public int hashCode() {
        int result = this.attachmentContainerResourceIdentifier != null ? this.attachmentContainerResourceIdentifier.hashCode() : 0;
        result = 31 * result + this.filename.hashCode();
        return result;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

