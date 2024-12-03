/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.inlinecomments.entities;

import com.atlassian.confluence.plugins.inlinecomments.entities.TopLevelInlineComment;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InlineCommentCreationResultBean {
    @XmlElement
    private final long commentId;
    @XmlElement
    private final TopLevelInlineComment inlineComment;

    public InlineCommentCreationResultBean(long pageCommentId) {
        this.commentId = pageCommentId;
        this.inlineComment = null;
    }

    public InlineCommentCreationResultBean(TopLevelInlineComment inlineComment) {
        this.commentId = inlineComment.getId();
        this.inlineComment = inlineComment;
    }

    public long getCommentId() {
        return this.commentId;
    }

    public TopLevelInlineComment getInlineComment() {
        return this.inlineComment;
    }
}

