/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.confluence.plugins.inlinecomments.entities;

import com.atlassian.confluence.plugins.inlinecomments.entities.AbstractInlineComment;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
public class Reply
extends AbstractInlineComment {
    @XmlElement
    private long commentId;

    public long getCommentId() {
        return this.commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public boolean isCreationBeanValid(Long containerId) {
        return this.commentId > 0L && StringUtils.isNotBlank((CharSequence)this.getBody()) && containerId != null;
    }
}

