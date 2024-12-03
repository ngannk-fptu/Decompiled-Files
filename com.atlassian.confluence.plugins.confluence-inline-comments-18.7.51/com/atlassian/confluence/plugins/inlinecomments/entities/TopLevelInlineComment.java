/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.confluence.plugins.inlinecomments.entities;

import com.atlassian.confluence.plugins.inlinecomments.entities.AbstractInlineComment;
import com.atlassian.confluence.plugins.inlinecomments.entities.ResolveProperties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
public class TopLevelInlineComment
extends AbstractInlineComment {
    @XmlElement
    private String markerRef;
    @XmlElement
    private long parentCommentId;
    @XmlElement
    private String originalSelection;
    @XmlElement
    private ResolveProperties resolveProperties;
    @XmlElement
    private boolean hasResolvePermission;
    @XmlElement
    private boolean hasReplyPermission;

    public String getMarkerRef() {
        return this.markerRef;
    }

    public void setMarkerRef(String markerRef) {
        this.markerRef = markerRef;
    }

    public long getParentCommentId() {
        return this.parentCommentId;
    }

    public void setParentCommentId(long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getOriginalSelection() {
        return this.originalSelection;
    }

    public void setOriginalSelection(String originalSelection) {
        this.originalSelection = originalSelection;
    }

    public ResolveProperties getResolveProperties() {
        return this.resolveProperties;
    }

    public boolean getHasResolvePermission() {
        return this.hasResolvePermission;
    }

    public void setHasResolvePermission(boolean hasResolvePermission) {
        this.hasResolvePermission = hasResolvePermission;
    }

    public boolean getHasReplyPermission() {
        return this.hasReplyPermission;
    }

    public void setHasReplyPermission(boolean hasReplyPermission) {
        this.hasReplyPermission = hasReplyPermission;
    }

    public void setResolveProperties(ResolveProperties resolveProperties) {
        this.resolveProperties = resolveProperties;
    }
}

