/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.Immutable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.tinymceplugin.rest.entities;

import com.google.errorprone.annotations.Immutable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Immutable
public class CommentResult {
    @XmlElement
    private final long id;
    @XmlElement
    private final String html;
    @XmlElement
    private final boolean asyncRenderSafe;
    @XmlElement
    private final long ownerId;
    @XmlElement
    private final long parentId;
    @XmlElement
    private final boolean isInlineComment;
    @XmlElement
    private final String serializedHighlights;
    @XmlElement
    private final String dataRef;

    public CommentResult() {
        this.id = 0L;
        this.html = null;
        this.ownerId = 0L;
        this.parentId = 0L;
        this.asyncRenderSafe = false;
        this.isInlineComment = false;
        this.serializedHighlights = null;
        this.dataRef = null;
    }

    public CommentResult(long id, String html, long ownerId, long parentId, boolean asyncRenderSafe, boolean isInlineComment, String serializedHighlights, String dataRef) {
        this.id = id;
        this.html = html;
        this.ownerId = ownerId;
        this.parentId = parentId;
        this.asyncRenderSafe = asyncRenderSafe;
        this.isInlineComment = isInlineComment;
        this.serializedHighlights = serializedHighlights;
        this.dataRef = dataRef;
    }

    public String getHtml() {
        return this.html;
    }
}

