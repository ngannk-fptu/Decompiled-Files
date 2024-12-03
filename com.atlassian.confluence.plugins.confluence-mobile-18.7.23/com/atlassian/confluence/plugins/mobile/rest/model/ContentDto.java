/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.rest.dto.UserDto
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.rest.model;

import com.atlassian.confluence.plugins.mobile.dto.CommentDto;
import com.atlassian.confluence.plugins.mobile.dto.WebResourceDependenciesDto;
import com.atlassian.confluence.plugins.mobile.rest.model.LikeDto;
import com.atlassian.confluence.plugins.mobile.rest.model.SpaceDto;
import com.atlassian.confluence.plugins.rest.dto.UserDto;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public final class ContentDto {
    @JsonProperty
    private long id;
    @JsonProperty
    private String title;
    @JsonProperty
    private String body;
    @JsonProperty
    private String contentType;
    @JsonProperty
    private UserDto author;
    @JsonProperty
    private String displayDate;
    @JsonProperty
    private List<LikeDto> likes;
    @JsonProperty
    private List<CommentDto> comments;
    @JsonProperty
    private SpaceDto space;
    @JsonProperty
    private boolean watching;
    @JsonProperty
    private WebResourceDependenciesDto webResourceDependencies;

    private ContentDto() {
    }

    public ContentDto(long id, String title, String contentType, String body, UserDto author, String friendlyCreationDate, List<LikeDto> likes, List<CommentDto> comments, SpaceDto space, boolean watching) {
        this.id = id;
        this.title = title;
        this.contentType = contentType;
        this.body = body;
        this.author = author;
        this.displayDate = friendlyCreationDate;
        this.likes = likes;
        this.comments = comments;
        this.space = space;
        this.watching = watching;
    }

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContentType() {
        return this.contentType;
    }

    public UserDto getAuthor() {
        return this.author;
    }

    public String getDisplayDate() {
        return this.displayDate;
    }

    @HtmlSafe
    public String getBody() {
        return this.body;
    }

    public List<LikeDto> getLikes() {
        return this.likes;
    }

    public List<CommentDto> getComments() {
        return this.comments;
    }

    public SpaceDto getSpace() {
        return this.space;
    }

    public boolean isWatching() {
        return this.watching;
    }

    public WebResourceDependenciesDto getWebResourceDependencies() {
        return this.webResourceDependencies;
    }

    public void setWebResourceDependencies(WebResourceDependenciesDto webResourceDependencies) {
        this.webResourceDependencies = webResourceDependencies;
    }
}

