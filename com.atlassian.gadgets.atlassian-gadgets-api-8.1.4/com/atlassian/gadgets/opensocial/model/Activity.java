/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.opensocial.model;

import com.atlassian.gadgets.opensocial.model.ActivityId;
import com.atlassian.gadgets.opensocial.model.AppId;
import com.atlassian.gadgets.opensocial.model.MediaItem;
import com.atlassian.gadgets.opensocial.model.PersonId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.jcip.annotations.Immutable;

@Immutable
public class Activity {
    private final AppId appId;
    private final String body;
    private final String externalId;
    private final ActivityId id;
    private final Date updated;
    private final List<MediaItem> mediaItems;
    private final Long postedTime;
    private final Float priority;
    private final String streamFaviconUrl;
    private final String streamSourceUrl;
    private final String streamTitle;
    private final String streamUrl;
    private final String title;
    private final String url;
    private final PersonId userId;

    private Activity(Builder builder) {
        this.appId = builder.appId;
        this.body = builder.body;
        this.externalId = builder.externalId;
        this.id = builder.id;
        this.updated = builder.updated;
        this.mediaItems = builder.mediaItems != null ? builder.mediaItems : Collections.emptyList();
        this.postedTime = builder.postedTime;
        this.priority = builder.priority;
        this.streamFaviconUrl = builder.streamFaviconUrl;
        this.streamSourceUrl = builder.streamSourceUrl;
        this.streamTitle = builder.streamTitle;
        this.streamUrl = builder.streamUrl;
        this.title = builder.title;
        this.url = builder.url;
        this.userId = builder.userId;
    }

    public AppId getAppId() {
        return this.appId;
    }

    public String getBody() {
        return this.body;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public ActivityId getId() {
        return this.id;
    }

    public Date getUpdated() {
        if (this.updated != null) {
            return new Date(this.updated.getTime());
        }
        return null;
    }

    public List<MediaItem> getMediaItems() {
        return Collections.unmodifiableList(this.mediaItems);
    }

    public Long getPostedTime() {
        return this.postedTime;
    }

    public Float getPriority() {
        return this.priority;
    }

    public String getStreamFaviconUrl() {
        return this.streamFaviconUrl;
    }

    public String getStreamSourceUrl() {
        return this.streamSourceUrl;
    }

    public String getStreamTitle() {
        return this.streamTitle;
    }

    public String getStreamUrl() {
        return this.streamUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public String getUrl() {
        return this.url;
    }

    public PersonId getUserId() {
        return this.userId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Activity activity = (Activity)o;
        if (this.appId != null ? !this.appId.equals(activity.appId) : activity.appId != null) {
            return false;
        }
        if (this.body != null ? !this.body.equals(activity.body) : activity.body != null) {
            return false;
        }
        if (this.externalId != null ? !this.externalId.equals(activity.externalId) : activity.externalId != null) {
            return false;
        }
        if (this.id != null ? !this.id.equals(activity.id) : activity.id != null) {
            return false;
        }
        if (!this.mediaItems.equals(activity.mediaItems)) {
            return false;
        }
        if (this.postedTime != null ? !this.postedTime.equals(activity.postedTime) : activity.postedTime != null) {
            return false;
        }
        if (this.priority != null ? !this.priority.equals(activity.priority) : activity.priority != null) {
            return false;
        }
        if (this.streamFaviconUrl != null ? !this.streamFaviconUrl.equals(activity.streamFaviconUrl) : activity.streamFaviconUrl != null) {
            return false;
        }
        if (this.streamSourceUrl != null ? !this.streamSourceUrl.equals(activity.streamSourceUrl) : activity.streamSourceUrl != null) {
            return false;
        }
        if (this.streamTitle != null ? !this.streamTitle.equals(activity.streamTitle) : activity.streamTitle != null) {
            return false;
        }
        if (this.streamUrl != null ? !this.streamUrl.equals(activity.streamUrl) : activity.streamUrl != null) {
            return false;
        }
        if (this.title != null ? !this.title.equals(activity.title) : activity.title != null) {
            return false;
        }
        if (this.updated != null ? !this.updated.equals(activity.updated) : activity.updated != null) {
            return false;
        }
        if (this.url != null ? !this.url.equals(activity.url) : activity.url != null) {
            return false;
        }
        return !(this.userId != null ? !this.userId.equals(activity.userId) : activity.userId != null);
    }

    public int hashCode() {
        int result = this.appId != null ? this.appId.hashCode() : 0;
        result = 31 * result + (this.body != null ? this.body.hashCode() : 0);
        result = 31 * result + (this.externalId != null ? this.externalId.hashCode() : 0);
        result = 31 * result + (this.id != null ? this.id.hashCode() : 0);
        result = 31 * result + (this.updated != null ? this.updated.hashCode() : 0);
        result = 31 * result + this.mediaItems.hashCode();
        result = 31 * result + (this.postedTime != null ? this.postedTime.hashCode() : 0);
        result = 31 * result + (this.priority != null ? this.priority.hashCode() : 0);
        result = 31 * result + (this.streamFaviconUrl != null ? this.streamFaviconUrl.hashCode() : 0);
        result = 31 * result + (this.streamSourceUrl != null ? this.streamSourceUrl.hashCode() : 0);
        result = 31 * result + (this.streamTitle != null ? this.streamTitle.hashCode() : 0);
        result = 31 * result + (this.streamUrl != null ? this.streamUrl.hashCode() : 0);
        result = 31 * result + (this.title != null ? this.title.hashCode() : 0);
        result = 31 * result + (this.url != null ? this.url.hashCode() : 0);
        result = 31 * result + (this.userId != null ? this.userId.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Activity{appId=" + this.appId + ", body='" + this.body + '\'' + ", externalId='" + this.externalId + '\'' + ", id=" + this.id + ", updated=" + this.updated + ", mediaItems=" + this.mediaItems + ", postedTime=" + this.postedTime + ", priority=" + this.priority + ", streamFaviconUrl='" + this.streamFaviconUrl + '\'' + ", streamSourceUrl='" + this.streamSourceUrl + '\'' + ", streamTitle='" + this.streamTitle + '\'' + ", streamUrl='" + this.streamUrl + '\'' + ", title='" + this.title + '\'' + ", url='" + this.url + '\'' + ", userId=" + this.userId + '}';
    }

    public static enum Field {
        APP_ID("appId"),
        BODY("body"),
        BODY_ID("bodyId"),
        EXTERNAL_ID("externalId"),
        ID("id"),
        LAST_UPDATED("updated"),
        MEDIA_ITEMS("mediaItems"),
        POSTED_TIME("postedTime"),
        PRIORITY("priority"),
        STREAM_FAVICON_URL("streamFaviconUrl"),
        STREAM_SOURCE_URL("streamSourceUrl"),
        STREAM_TITLE("streamTitle"),
        STREAM_URL("streamUrl"),
        TEMPLATE_PARAMS("templateParams"),
        TITLE("title"),
        TITLE_ID("titleId"),
        URL("url"),
        USER_ID("userId");

        private final String jsonString;

        private Field(String jsonString) {
            this.jsonString = jsonString;
        }

        public String toString() {
            return this.jsonString;
        }
    }

    public static class Builder {
        private AppId appId;
        private String body;
        private String externalId;
        private ActivityId id;
        private Date updated;
        private List<MediaItem> mediaItems;
        private Long postedTime;
        private Float priority;
        private String streamFaviconUrl;
        private String streamSourceUrl;
        private String streamTitle;
        private String streamUrl;
        private String title;
        private String url;
        private PersonId userId;

        public Builder(Activity activity) {
            this.appId(activity.appId);
            this.body(activity.body);
            this.externalId(this.externalId);
            this.id(activity.id);
            this.updated(activity.updated);
            this.mediaItems(activity.mediaItems);
            this.postedTime(activity.postedTime);
            this.priority(activity.priority);
            this.streamFaviconUrl(activity.streamFaviconUrl);
            this.streamSourceUrl(activity.streamSourceUrl);
            this.streamTitle(activity.streamTitle);
            this.streamUrl(activity.streamUrl);
            this.title(activity.title);
            this.url(activity.url);
            this.userId(activity.userId);
        }

        public Builder(String title) {
            if (title == null) {
                throw new NullPointerException("title parameter must not be null when creating a new Activity.Builder");
            }
            this.title = title;
        }

        public Builder appId(AppId appId) {
            this.appId = appId;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder id(ActivityId id) {
            this.id = id;
            return this;
        }

        public Builder updated(Date updated) {
            this.updated = updated;
            return this;
        }

        public Builder mediaItems(List<MediaItem> mediaItems) {
            this.mediaItems = mediaItems;
            return this;
        }

        public Builder postedTime(Long postedTime) {
            this.postedTime = postedTime;
            return this;
        }

        public Builder priority(Float priority) {
            this.priority = priority;
            return this;
        }

        public Builder streamFaviconUrl(String streamFaviconUrl) {
            this.streamFaviconUrl = streamFaviconUrl;
            return this;
        }

        public Builder streamSourceUrl(String streamSourceUrl) {
            this.streamSourceUrl = streamSourceUrl;
            return this;
        }

        public Builder streamTitle(String streamTitle) {
            this.streamTitle = streamTitle;
            return this;
        }

        public Builder streamUrl(String streamUrl) {
            this.streamUrl = streamUrl;
            return this;
        }

        private Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder userId(PersonId userId) {
            this.userId = userId;
            return this;
        }

        public Activity build() {
            return new Activity(this);
        }
    }
}

