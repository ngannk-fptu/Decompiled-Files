/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.plugins.dailysummary.content.popular;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;

public class PopularContentExcerptDto {
    private final ImmutableMap<String, DataSource> imageDataSources;
    private final ImmutableList<User> networkParticipants;
    private final long contentId;
    private final int commentCount;
    private final int likeCount;
    private final User author;
    private final String excerptHtml;
    private final String contentTitle;
    private final String contentLink;
    private final String creatorName;

    private PopularContentExcerptDto(Builder builder) {
        this.imageDataSources = builder.imageDatasources.build();
        this.networkParticipants = builder.networkParticipants.build();
        this.commentCount = builder.commentCount;
        this.likeCount = builder.likeCount;
        this.author = builder.author;
        this.excerptHtml = builder.excerptHtml;
        this.contentTitle = builder.contentTitle;
        this.contentLink = builder.contentLink;
        this.contentId = builder.contentId;
        this.creatorName = builder.creatorName;
    }

    public Map<String, DataSource> getImageDataSources() {
        return this.imageDataSources;
    }

    public List<User> getNetworkParticipants() {
        return this.networkParticipants;
    }

    public int getCommentCount() {
        return this.commentCount;
    }

    public int getLikeCount() {
        return this.likeCount;
    }

    public User getAuthor() {
        return this.author;
    }

    public String getExcerptHtml() {
        return this.excerptHtml;
    }

    public String getContentTitle() {
        return this.contentTitle;
    }

    public String getContentLink() {
        return this.contentLink;
    }

    public long getContentId() {
        return this.contentId;
    }

    public String getCreatorName() {
        return this.creatorName;
    }

    public String toString() {
        return "PopularContentExcerptDto [contentTitle=" + this.contentTitle + ", imageDatasources=" + this.imageDataSources + ", networkParticipants=" + this.networkParticipants + ", commentCount=" + this.commentCount + ", likeCount=" + this.likeCount + ", author=" + this.author + ", excerptHtml=" + this.excerptHtml + ", contentLink=" + this.contentLink + "]";
    }

    public static class Builder {
        public String contentLink;
        private ImmutableMap.Builder<String, DataSource> imageDatasources = new ImmutableMap.Builder();
        private ImmutableList.Builder<User> networkParticipants = new ImmutableList.Builder();
        private int commentCount;
        private int likeCount;
        private User author;
        private String excerptHtml;
        private String contentTitle;
        private final long contentId;
        private final String creatorName;

        public Builder(ContentEntityObject content, User author) {
            this.contentId = content.getId();
            this.contentTitle = content.getDisplayTitle();
            this.commentCount = content instanceof Comment ? ((Comment)content).getChildren().size() : content.getComments().size();
            this.contentLink = content.getUrlPath();
            this.author = author;
            ConfluenceUser creator = content.getCreator();
            this.creatorName = creator != null ? creator.getName() : "anon";
        }

        public PopularContentExcerptDto build() {
            return new PopularContentExcerptDto(this);
        }

        public Builder addImageDataSource(Map<String, DataSource> imageDataSource) {
            this.imageDatasources.putAll(imageDataSource);
            return this;
        }

        public Builder addNetworkParticipant(Iterable<User> networkParticipants) {
            this.networkParticipants.addAll(networkParticipants);
            return this;
        }

        public Builder commentCount(int count) {
            this.commentCount = count;
            return this;
        }

        public Builder likeCount(int count) {
            this.likeCount = count;
            return this;
        }

        public Builder author(User author) {
            this.author = author;
            return this;
        }

        public Builder excerptBody(String excerptBody) {
            this.excerptHtml = excerptBody;
            return this;
        }

        public Builder contentTitle(String title) {
            this.contentTitle = title;
            return this;
        }

        public Builder contentLink(String link) {
            this.contentLink = link;
            return this;
        }
    }
}

