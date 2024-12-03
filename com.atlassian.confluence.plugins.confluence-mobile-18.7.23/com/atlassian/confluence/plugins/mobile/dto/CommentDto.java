/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.rest.dto.UserDto
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  net.jcip.annotations.Immutable
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.atlassian.confluence.plugins.mobile.rest.model.LikeDto;
import com.atlassian.confluence.plugins.rest.dto.UserDto;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import java.util.List;
import net.jcip.annotations.Immutable;
import org.codehaus.jackson.annotate.JsonProperty;

@Immutable
public class CommentDto {
    @JsonProperty
    private long id;
    @JsonProperty
    private String html;
    @JsonProperty
    private UserDto commenter;
    @JsonProperty
    private String displayDate;
    @JsonProperty
    private long parentId;
    @JsonProperty
    private List<LikeDto> likes;
    @JsonProperty
    private boolean isTopInlineComment;
    @JsonProperty
    private boolean isInlineComment;
    @JsonProperty
    private String highlightContent;
    @JsonProperty
    private boolean isResolved;

    private CommentDto() {
    }

    public CommentDto(long id, UserDto commenter, String body, String displayDate, long parentId) {
        this.id = id;
        this.commenter = commenter;
        this.html = body;
        this.displayDate = displayDate;
        this.parentId = parentId;
    }

    public long getId() {
        return this.id;
    }

    public UserDto getCommenter() {
        return this.commenter;
    }

    @HtmlSafe
    public String getHtml() {
        return this.html;
    }

    public String getDisplayDate() {
        return this.displayDate;
    }

    public long getParentId() {
        return this.parentId;
    }

    public List<LikeDto> getLikes() {
        return this.likes;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void setCommenter(UserDto commenter) {
        this.commenter = commenter;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public void setLikes(List<LikeDto> likes) {
        this.likes = likes;
    }

    public boolean isInlineComment() {
        return this.isInlineComment;
    }

    public void setInlineComment(boolean isInlineComment) {
        this.isInlineComment = isInlineComment;
    }

    public String getHighlightContent() {
        return this.highlightContent;
    }

    public void setHighlightContent(String highlightContent) {
        this.highlightContent = highlightContent;
    }

    public boolean isResolved() {
        return this.isResolved;
    }

    public void setResolved(boolean isResolved) {
        this.isResolved = isResolved;
    }

    public void setTopInlineComment(boolean isTopInlineComment) {
        this.isTopInlineComment = isTopInlineComment;
    }

    public boolean isTopInlineComment() {
        return this.isTopInlineComment;
    }
}

