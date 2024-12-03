/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.UserProfile
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Functions
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.joda.time.DateTime
 */
package com.atlassian.streams.thirdparty.rest.representations;

import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.thirdparty.api.Activity;
import com.atlassian.streams.thirdparty.api.Application;
import com.atlassian.streams.thirdparty.api.ValidationErrors;
import com.atlassian.streams.thirdparty.rest.representations.ActivityObjectRepresentation;
import com.atlassian.streams.thirdparty.rest.representations.MediaLinkRepresentation;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

public class ActivityRepresentation {
    @JsonProperty
    ActivityObjectRepresentation actor;
    @JsonProperty
    String content;
    @JsonProperty
    ActivityObjectRepresentation generator;
    @JsonProperty
    MediaLinkRepresentation icon;
    @JsonProperty
    String id;
    @JsonProperty
    ActivityObjectRepresentation object;
    @JsonProperty
    Date published;
    @JsonProperty
    ActivityObjectRepresentation provider;
    @JsonProperty
    ActivityObjectRepresentation target;
    @JsonProperty
    String title;
    @JsonProperty
    Date updated;
    @JsonProperty
    String url;
    @JsonProperty
    String verb;
    @JsonProperty
    Map<String, URI> links;

    @JsonCreator
    public ActivityRepresentation(@JsonProperty(value="actor") ActivityObjectRepresentation actor, @JsonProperty(value="content") String content, @JsonProperty(value="generator") ActivityObjectRepresentation generator, @JsonProperty(value="icon") MediaLinkRepresentation icon, @JsonProperty(value="id") String id, @JsonProperty(value="object") ActivityObjectRepresentation object, @JsonProperty(value="published") Date published, @JsonProperty(value="provider") ActivityObjectRepresentation provider, @JsonProperty(value="target") ActivityObjectRepresentation target, @JsonProperty(value="title") String title, @JsonProperty(value="date") Date updated, @JsonProperty(value="url") String url, @JsonProperty(value="verb") String verb, @JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="application") ActivityObjectRepresentation application, @JsonProperty(value="user") ActivityObjectRepresentation user) {
        this.actor = actor == null ? user : actor;
        this.content = content;
        this.generator = generator == null ? application : generator;
        this.icon = icon;
        this.id = id;
        this.object = object;
        this.published = published;
        this.provider = provider;
        this.target = target;
        this.title = title;
        this.updated = updated;
        this.url = url;
        this.verb = verb;
        this.links = links == null ? ImmutableMap.of() : ImmutableMap.copyOf(links);
    }

    public static Builder builder(ActivityObjectRepresentation actor, ActivityObjectRepresentation generator) {
        return new Builder(actor, generator);
    }

    private ActivityRepresentation(Builder builder) {
        this.actor = builder.actor;
        this.content = (String)builder.content.map(Html.htmlToString()).getOrElse((Object)null);
        this.generator = builder.generator;
        this.icon = (MediaLinkRepresentation)builder.icon.getOrElse((Object)null);
        this.id = (String)builder.id.getOrElse((Object)null);
        this.object = (ActivityObjectRepresentation)builder.object.getOrElse((Object)null);
        this.published = (Date)builder.published.getOrElse((Object)null);
        this.target = (ActivityObjectRepresentation)builder.target.getOrElse((Object)null);
        this.title = (String)builder.title.map(Html.htmlToString()).getOrElse((Object)null);
        this.updated = (Date)builder.updated.getOrElse((Object)null);
        this.url = (String)builder.url.getOrElse((Object)null);
        this.verb = (String)builder.verb.getOrElse((Object)null);
        this.links = ImmutableMap.copyOf((Map)((Map)builder.links.getOrElse((Object)ImmutableMap.of())));
    }

    public Either<ValidationErrors, Activity> toActivity(com.atlassian.sal.api.user.UserProfile userProfile) {
        Either<ValidationErrors, UserProfile> userOrError;
        Either<ValidationErrors, Application> applicationOrError = this.getGenerator() == null ? Either.left((Object)ValidationErrors.validationError("activity must have generator")) : Application.application((Option<String>)Option.option((Object)this.getGenerator().getDisplayName()), (Option<String>)Option.option((Object)this.getGenerator().getId()));
        DateTime postedDate = this.getPublished() == null ? new DateTime() : new DateTime((Object)this.getPublished());
        if (userProfile == null) {
            userOrError = Either.left((Object)ValidationErrors.validationError("activity must have actor"));
        } else {
            ActivityObjectRepresentation rep = ActivityObjectRepresentation.builder().idString((Option<String>)Option.option((Object)userProfile.getUsername())).displayName((Option<String>)Option.option((Object)userProfile.getFullName())).image((Option<MediaLinkRepresentation>)Option.option((Object)userProfile.getProfilePictureUri()).map(uri -> MediaLinkRepresentation.builder(uri).build())).build();
            userOrError = rep.toUserProfile();
        }
        Activity.Builder builder = new Activity.Builder(applicationOrError, (Either<ValidationErrors, DateTime>)Either.right((Object)postedDate), userOrError);
        builder.content((Option<Html>)Option.option((Object)this.getContent()).map(Html.html()));
        builder.idString((Option<String>)Option.option((Object)this.getId()));
        if (this.getIcon() != null) {
            builder.icon(this.getIcon().toImage());
        }
        if (this.getObject() != null) {
            builder.object(this.getObject().toActivityObject());
        }
        if (this.getTarget() != null) {
            builder.target(this.getTarget().toActivityObject());
        }
        builder.title((Option<Html>)Option.option((Object)this.getTitle()).map(Html.html()));
        builder.urlString((Option<String>)Option.option((Object)this.getUrl()));
        builder.verbString((Option<String>)Option.option((Object)this.getVerb()));
        return builder.build();
    }

    public ActivityObjectRepresentation getActor() {
        return this.actor;
    }

    public String getContent() {
        return this.content;
    }

    public ActivityObjectRepresentation getGenerator() {
        return this.generator;
    }

    public MediaLinkRepresentation getIcon() {
        return this.icon;
    }

    public String getId() {
        return this.id;
    }

    public ActivityObjectRepresentation getObject() {
        return this.object;
    }

    public Date getPublished() {
        return this.published;
    }

    public ActivityObjectRepresentation getProvider() {
        return this.provider;
    }

    public ActivityObjectRepresentation getTarget() {
        return this.target;
    }

    public String getTitle() {
        return this.title;
    }

    public Date getUpdated() {
        return this.updated;
    }

    public String getUrl() {
        return this.url;
    }

    public String getVerb() {
        return this.verb;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public static class Builder {
        private ActivityObjectRepresentation actor;
        private Option<Html> content = Option.none();
        private ActivityObjectRepresentation generator;
        private Option<MediaLinkRepresentation> icon = Option.none();
        private Option<String> id = Option.none();
        private Option<ActivityObjectRepresentation> object = Option.none();
        private Option<Date> published = Option.none();
        private Option<ActivityObjectRepresentation> target = Option.none();
        private Option<Html> title = Option.none();
        private Option<Date> updated = Option.none();
        private Option<String> url = Option.none();
        private Option<String> verb = Option.none();
        private Option<Map<String, URI>> links = Option.none();

        public Builder(ActivityObjectRepresentation actor, ActivityObjectRepresentation generator) {
            this.actor = (ActivityObjectRepresentation)Preconditions.checkNotNull((Object)actor, (Object)"actor");
            this.generator = (ActivityObjectRepresentation)Preconditions.checkNotNull((Object)generator, (Object)"generator");
        }

        public ActivityRepresentation build() {
            return new ActivityRepresentation(this);
        }

        public Builder content(Option<Html> content) {
            this.content = (Option)Preconditions.checkNotNull(content, (Object)"content");
            return this;
        }

        public Builder icon(Option<MediaLinkRepresentation> icon) {
            this.icon = (Option)Preconditions.checkNotNull(icon, (Object)"icon");
            return this;
        }

        public Builder id(Option<URI> id) {
            this.id = ((Option)Preconditions.checkNotNull(id, (Object)"id")).map(Functions.toStringFunction());
            return this;
        }

        public Builder idString(Option<String> id) {
            this.id = (Option)Preconditions.checkNotNull(id, (Object)"id");
            return this;
        }

        public Builder object(Option<ActivityObjectRepresentation> object) {
            this.object = (Option)Preconditions.checkNotNull(object, (Object)"object");
            return this;
        }

        public Builder published(Option<Date> published) {
            this.published = (Option)Preconditions.checkNotNull(published, (Object)"published");
            return this;
        }

        public Builder target(Option<ActivityObjectRepresentation> target) {
            this.target = (Option)Preconditions.checkNotNull(target, (Object)"target");
            return this;
        }

        public Builder title(Option<Html> title) {
            this.title = (Option)Preconditions.checkNotNull(title, (Object)"title");
            return this;
        }

        public Builder updated(Option<Date> updated) {
            this.updated = (Option)Preconditions.checkNotNull(updated, (Object)"updated");
            return this;
        }

        public Builder url(Option<URI> url) {
            this.url = ((Option)Preconditions.checkNotNull(url, (Object)"url")).map(Functions.toStringFunction());
            return this;
        }

        public Builder urlString(Option<String> url) {
            this.url = (Option)Preconditions.checkNotNull(url, (Object)"url");
            return this;
        }

        public Builder verb(Option<URI> verb) {
            this.verb = ((Option)Preconditions.checkNotNull(verb, (Object)"verb")).map(Functions.toStringFunction());
            return this;
        }

        public Builder verbString(Option<String> verb) {
            this.verb = (Option)Preconditions.checkNotNull(verb, (Object)"verb");
            return this;
        }

        public Builder links(Option<Map<String, URI>> links) {
            this.links = (Option)Preconditions.checkNotNull(links, (Object)"links");
            return this;
        }
    }
}

