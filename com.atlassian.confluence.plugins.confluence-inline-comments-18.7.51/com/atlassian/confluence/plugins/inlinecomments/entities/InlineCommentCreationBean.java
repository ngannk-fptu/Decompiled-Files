/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.confluence.plugins.inlinecomments.entities;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class InlineCommentCreationBean {
    @XmlElement
    private long containerId;
    @XmlElement
    private int containerVersion;
    @XmlElement
    private long parentCommentId;
    @XmlElement
    private String body;
    @XmlElement
    private int numMatches;
    @XmlElement
    private int matchIndex;
    @XmlElement
    private long lastFetchTime;
    @XmlElement
    private String originalSelection;
    @XmlElement
    private String authorDisplayName;
    @XmlElement
    private String authorUserName;
    @XmlElement
    private String authorAvatarUrl;
    @XmlElement
    private List replies;
    @XmlElement
    private String serializedHighlights;

    public long getContainerId() {
        return this.containerId;
    }

    public void setContainerId(long containerId) {
        this.containerId = containerId;
    }

    public int getContainerVersion() {
        return this.containerVersion;
    }

    public void setVersion(int version) {
        this.containerVersion = version;
    }

    public long getParentCommentId() {
        return this.parentCommentId;
    }

    public void setParentCommentId(long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getNumMatches() {
        return this.numMatches;
    }

    public void setNumMatches(int numMatches) {
        this.numMatches = numMatches;
    }

    public int getMatchIndex() {
        return this.matchIndex;
    }

    public void setMatchIndex(int matchIndex) {
        this.matchIndex = matchIndex;
    }

    public long getLastFetchTime() {
        return this.lastFetchTime;
    }

    public void setLastFetchTime(long lastFetchTime) {
        this.lastFetchTime = lastFetchTime;
    }

    public String getOriginalSelection() {
        return this.originalSelection;
    }

    public void setOriginalSelection(String originalSelection) {
        this.originalSelection = originalSelection;
    }

    public String getSerializedHighlights() {
        return this.serializedHighlights;
    }

    public void setSerializedHighlights(String serializedHighlights) {
        this.serializedHighlights = serializedHighlights;
    }
}

