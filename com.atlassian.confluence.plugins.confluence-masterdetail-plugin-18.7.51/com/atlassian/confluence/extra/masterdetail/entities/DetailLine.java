/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.extra.masterdetail.entities;

import com.atlassian.confluence.core.ContentEntityObject;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class DetailLine {
    public static final int COUNT_UNAVAILABLE = -1;
    @JsonIgnore
    private transient ContentEntityObject content;
    @JsonProperty
    private long id;
    @JsonProperty
    private String title;
    @JsonProperty
    private String relativeLink;
    @JsonProperty
    private String subTitle;
    @JsonProperty
    private String subRelativeLink;
    @JsonProperty
    private List<String> details;
    @JsonProperty
    private int likesCount = -1;
    @JsonProperty
    private int commentsCount = -1;

    @JsonCreator
    private DetailLine() {
    }

    public DetailLine(ContentEntityObject content, List<String> details) {
        this.content = content;
        this.id = content.getId();
        this.details = details;
    }

    public ContentEntityObject getContent() {
        return this.content;
    }

    public List<String> getDetails() {
        return this.details;
    }

    public int getLikesCount() {
        return this.likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return this.commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getRelativeLink() {
        return this.relativeLink;
    }

    public void setRelativeLink(String relativeLink) {
        this.relativeLink = relativeLink;
    }

    public String getSubRelativeLink() {
        return this.subRelativeLink;
    }

    public void setSubRelativeLink(String subRelativeLink) {
        this.subRelativeLink = subRelativeLink;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return this.id;
    }
}

