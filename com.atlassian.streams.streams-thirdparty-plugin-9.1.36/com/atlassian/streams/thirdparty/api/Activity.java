/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.DateUtil
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.UserProfile
 *  com.atlassian.streams.api.UserProfile$Builder
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.joda.time.DateTime
 */
package com.atlassian.streams.thirdparty.api;

import com.atlassian.streams.api.DateUtil;
import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.thirdparty.api.ActivityObject;
import com.atlassian.streams.thirdparty.api.Application;
import com.atlassian.streams.thirdparty.api.Image;
import com.atlassian.streams.thirdparty.api.ValidationErrors;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joda.time.DateTime;

public class Activity {
    private final Long activityId;
    private final Html content;
    private final Application application;
    private final Image icon;
    private final URI id;
    private final ActivityObject object;
    private final ZonedDateTime postedDate;
    private final String poster;
    private final boolean registeredUser;
    private final ActivityObject target;
    private final Html title;
    private final URI url;
    private final UserProfile user;
    private final URI verb;

    @Deprecated
    public static Builder builder(Application application, DateTime postedDate, UserProfile user) {
        return new Builder(application, postedDate, user);
    }

    public static Builder2 builder2(Application application, ZonedDateTime postedDate, UserProfile user) {
        return new Builder2(application, postedDate, user);
    }

    @Deprecated
    public static Builder builder(Application application, DateTime postedDate, String username) {
        return new Builder(application, postedDate, new UserProfile.Builder(username).build());
    }

    public static Builder2 builder2(Application application, ZonedDateTime postedDate, String username) {
        return new Builder2(application, postedDate, new UserProfile.Builder(username).build());
    }

    private Activity(Builder builder) {
        this.activityId = builder.activityId.isDefined() ? (Long)builder.activityId.get() : null;
        this.content = builder.content.isDefined() ? (Html)builder.content.get() : null;
        this.application = builder.application;
        this.icon = builder.icon.isDefined() ? (Image)builder.icon.get() : null;
        this.id = builder.id.isDefined() ? (URI)builder.id.get() : null;
        this.object = builder.object.isDefined() ? (ActivityObject)builder.object.get() : null;
        this.postedDate = DateUtil.toZonedDate((DateTime)builder.postedDate);
        this.poster = builder.poster.isDefined() ? (String)builder.poster.get() : null;
        this.registeredUser = builder.registeredUser;
        this.target = builder.target.isDefined() ? (ActivityObject)builder.target.get() : null;
        this.title = builder.title.isDefined() ? (Html)builder.title.get() : null;
        this.url = builder.url.isDefined() ? (URI)builder.url.get() : null;
        this.user = builder.user;
        this.verb = builder.verb.isDefined() ? (URI)builder.verb.get() : null;
    }

    private Activity(Builder2 builder) {
        this.activityId = builder.activityId;
        this.content = builder.content;
        this.application = builder.application;
        this.icon = builder.icon;
        this.id = builder.id;
        this.object = builder.object;
        this.postedDate = builder.postedDate;
        this.poster = builder.poster;
        this.registeredUser = builder.registeredUser;
        this.target = builder.target;
        this.title = builder.title;
        this.url = builder.url;
        this.user = builder.user;
        this.verb = builder.verb;
    }

    @Deprecated
    public Option<Long> getActivityId() {
        return Option.option((Object)this.activityId);
    }

    @Nullable
    public Long getActivityIdOrNull() {
        return this.activityId;
    }

    public Application getApplication() {
        return this.application;
    }

    @Deprecated
    public Option<Html> getContent() {
        return Option.option((Object)this.content);
    }

    @Nullable
    public Html getContentOrNull() {
        return this.content;
    }

    @Deprecated
    public Option<Image> getIcon() {
        return Option.option((Object)this.icon);
    }

    @Nullable
    public Image getIconOrNull() {
        return this.icon;
    }

    @Deprecated
    public Option<URI> getId() {
        return Option.option((Object)this.id);
    }

    @Nullable
    public URI getIdOrNull() {
        return this.id;
    }

    @Deprecated
    public Option<ActivityObject> getObject() {
        return Option.option((Object)this.object);
    }

    @Nullable
    public ActivityObject getObjectOrNull() {
        return this.object;
    }

    @Deprecated
    public DateTime getPostedDate() {
        return DateUtil.fromZonedDate((ZonedDateTime)this.postedDate);
    }

    @Nonnull
    public ZonedDateTime getZonedPostedDate() {
        return this.postedDate;
    }

    public Option<String> getPoster() {
        return Option.option((Object)this.poster);
    }

    @Nullable
    public String getPosterOrNull() {
        return this.poster;
    }

    public boolean isRegisteredUser() {
        return this.registeredUser;
    }

    @Deprecated
    public Option<ActivityObject> getTarget() {
        return Option.option((Object)this.target);
    }

    @Nullable
    public ActivityObject getTargetOrNull() {
        return this.target;
    }

    @Deprecated
    public Option<Html> getTitle() {
        return Option.option((Object)this.title);
    }

    @Nullable
    public Html getTitleOrNull() {
        return this.title;
    }

    @Deprecated
    public Option<URI> getUrl() {
        return Option.option((Object)this.url);
    }

    @Nullable
    public URI getUrlOrNull() {
        return this.url;
    }

    public UserProfile getUser() {
        return this.user;
    }

    @Deprecated
    public Option<URI> getVerb() {
        return Option.option((Object)this.verb);
    }

    @Nullable
    public URI getVerbOrNull() {
        return this.verb;
    }

    public boolean equals(Object other) {
        if (other instanceof Activity) {
            Activity a = (Activity)other;
            return this.activityId.equals(a.activityId) && this.application.equals(a.application) && this.content.equals((Object)a.content) && this.icon.equals(a.icon) && this.id.equals(a.id) && this.object.equals(a.object) && this.postedDate.equals(a.postedDate) && this.poster.equals(a.poster) && this.registeredUser == a.registeredUser && this.target.equals(a.target) && this.title.equals((Object)a.title) && this.url.equals(a.url) && this.user.equals((Object)a.user) && this.verb.equals(a.verb);
        }
        return false;
    }

    public int hashCode() {
        return this.activityId.hashCode() + 37 * (this.application.hashCode() + 37 * (this.content.hashCode() + 37 * (this.icon.hashCode() + 37 * (this.id.hashCode() + 37 * (this.object.hashCode() + 37 * (this.postedDate.hashCode() + 37 * (this.poster.hashCode() + 37 * ((this.registeredUser ? 1 : 0) + 37 * (this.target.hashCode() + 37 * (this.title.hashCode() + 37 * (this.url.hashCode() + 37 * (this.user.hashCode() + 37 * this.verb.hashCode()))))))))))));
    }

    public static final class Builder2 {
        private ValidationErrors.Builder errors = new ValidationErrors.Builder();
        private Long activityId = null;
        private Html content = null;
        private final Application application;
        private Image icon = null;
        private URI id = null;
        private ActivityObject object = null;
        private final ZonedDateTime postedDate;
        private String poster = null;
        private boolean registeredUser = false;
        private ActivityObject target = null;
        private Html title = null;
        private URI url = null;
        private final UserProfile user;
        private URI verb = null;

        public Builder2(Application application, ZonedDateTime postedDate, UserProfile user) {
            this((Either<ValidationErrors, Application>)Either.right((Object)application), (Either<ValidationErrors, ZonedDateTime>)Either.right((Object)postedDate), (Either<ValidationErrors, UserProfile>)Either.right((Object)user));
        }

        public Builder2(Either<ValidationErrors, Application> application, Either<ValidationErrors, ZonedDateTime> postedDate, Either<ValidationErrors, UserProfile> user) {
            if (application.isRight()) {
                this.application = (Application)application.right().get();
            } else {
                this.errors.addAll((ValidationErrors)application.left().get(), "application");
                this.application = null;
            }
            if (postedDate.isRight()) {
                this.postedDate = (ZonedDateTime)postedDate.right().get();
            } else {
                this.errors.addAll((ValidationErrors)postedDate.left().get(), "postedDate");
                this.postedDate = null;
            }
            if (user.isRight()) {
                this.user = (UserProfile)user.right().get();
                this.errors.checkString(this.user.getUsername(), "user.username");
                this.errors.checkString(this.user.getFullName(), "user.fullName");
                this.errors.checkAbsoluteUri((Option<URI>)this.user.getProfilePageUri(), "user.profilePageUri");
                this.errors.checkAbsoluteUri((Option<URI>)this.user.getProfilePictureUri(), "user.profilePictureUri");
            } else {
                this.errors.addAll((ValidationErrors)user.left().get(), "user");
                this.user = null;
            }
        }

        public Either<ValidationErrors, Activity> build() {
            if (this.id == null && this.url == null) {
                this.errors.addError("activity id and url cannot both be omitted");
            }
            if (this.errors.isEmpty()) {
                return Either.right((Object)new Activity(this));
            }
            return Either.left((Object)this.errors.build());
        }

        public Builder2 activityId(long activityId) {
            this.activityId = activityId;
            return this;
        }

        public Builder2 content(@Nonnull Html content) {
            this.content = this.errors.checkHtml(content, "content", 5000);
            return this;
        }

        public Builder2 icon(@Nonnull Image icon) {
            this.icon = Objects.requireNonNull(icon, "icon");
            return this;
        }

        public Builder2 icon(Either<ValidationErrors, Image> errorsOrImage) {
            Objects.requireNonNull(errorsOrImage, "errorsOrImage");
            if (errorsOrImage.isRight()) {
                this.icon = (Image)errorsOrImage.right().get();
            } else {
                this.errors.addAll((ValidationErrors)errorsOrImage.left().get(), "icon");
            }
            return this;
        }

        public Builder2 id(URI id) {
            this.id = this.errors.checkAbsoluteUri(id, "id");
            return this;
        }

        public Builder2 idString(String id) {
            this.id = this.errors.checkAbsoluteUriString(id, "id");
            return this;
        }

        public Builder2 object(@Nonnull ActivityObject object) {
            this.object = Objects.requireNonNull(object, "object");
            return this;
        }

        public Builder2 object(Either<ValidationErrors, ActivityObject> errorsOrObject) {
            Objects.requireNonNull(errorsOrObject, "targetOrErrors");
            if (errorsOrObject.isRight()) {
                this.object = (ActivityObject)errorsOrObject.right().get();
            } else {
                this.errors.addAll((ValidationErrors)errorsOrObject.left().get(), "object");
            }
            return this;
        }

        public Builder2 poster(@Nonnull String poster) {
            this.poster = this.errors.checkString(poster, "poster");
            return this;
        }

        public Builder2 registeredUser(boolean registeredUser) {
            this.registeredUser = registeredUser;
            return this;
        }

        public Builder2 target(@Nonnull ActivityObject target) {
            this.target = Objects.requireNonNull(target, "target");
            return this;
        }

        public Builder2 target(Either<ValidationErrors, ActivityObject> targetOrErrors) {
            Objects.requireNonNull(targetOrErrors, "targetOrErrors");
            if (targetOrErrors.isRight()) {
                this.target = (ActivityObject)targetOrErrors.right().get();
            } else {
                this.errors.addAll((ValidationErrors)targetOrErrors.left().get(), "target");
            }
            return this;
        }

        public Builder2 title(@Nonnull Html title) {
            this.title = this.errors.checkHtml(title, "title", 255);
            return this;
        }

        public Builder2 url(@Nonnull URI url) {
            this.url = this.errors.checkAbsoluteUri(url, "url");
            return this;
        }

        public Builder2 urlString(@Nonnull String url) {
            this.url = this.errors.checkAbsoluteUriString(url, "url");
            return this;
        }

        public Builder2 verb(URI verb) {
            this.verb = this.errors.checkSimpleNameOrAbsoluteUri(verb, "verb");
            return this;
        }

        public Builder2 verbString(@Nonnull String verb) {
            this.verb = this.errors.checkSimpleNameOrAbsoluteUriString(verb, "verb");
            return this;
        }
    }

    @Deprecated
    public static final class Builder {
        private ValidationErrors.Builder errors = new ValidationErrors.Builder();
        private Option<Long> activityId = Option.none();
        private Option<Html> content = Option.none();
        private final Application application;
        private Option<Image> icon = Option.none();
        private Option<URI> id = Option.none();
        private Option<ActivityObject> object = Option.none();
        private final DateTime postedDate;
        private Option<String> poster = Option.none();
        private boolean registeredUser = false;
        private Option<ActivityObject> target = Option.none();
        private Option<Html> title = Option.none();
        private Option<URI> url = Option.none();
        private final UserProfile user;
        private Option<URI> verb = Option.none();

        @Deprecated
        public Builder(Application application, DateTime postedDate, UserProfile user) {
            this((Either<ValidationErrors, Application>)Either.right((Object)application), (Either<ValidationErrors, DateTime>)Either.right((Object)postedDate), (Either<ValidationErrors, UserProfile>)Either.right((Object)user));
        }

        @Deprecated
        public Builder(Either<ValidationErrors, Application> application, Either<ValidationErrors, DateTime> postedDate, Either<ValidationErrors, UserProfile> user) {
            if (application.isRight()) {
                this.application = (Application)application.right().get();
            } else {
                this.errors.addAll((ValidationErrors)application.left().get(), "application");
                this.application = null;
            }
            if (postedDate.isRight()) {
                this.postedDate = (DateTime)postedDate.right().get();
            } else {
                this.errors.addAll((ValidationErrors)postedDate.left().get(), "postedDate");
                this.postedDate = null;
            }
            if (user.isRight()) {
                this.user = (UserProfile)user.right().get();
                this.errors.checkString(this.user.getUsername(), "user.username");
                this.errors.checkString(this.user.getFullName(), "user.fullName");
                this.errors.checkAbsoluteUri((Option<URI>)this.user.getProfilePageUri(), "user.profilePageUri");
                this.errors.checkAbsoluteUri((Option<URI>)this.user.getProfilePictureUri(), "user.profilePictureUri");
            } else {
                this.errors.addAll((ValidationErrors)user.left().get(), "user");
                this.user = null;
            }
        }

        public Either<ValidationErrors, Activity> build() {
            if (!this.id.isDefined() && !this.url.isDefined()) {
                this.errors.addError("activity id and url cannot both be omitted");
            }
            if (this.errors.isEmpty()) {
                return Either.right((Object)new Activity(this));
            }
            return Either.left((Object)this.errors.build());
        }

        public Builder activityId(long activityId) {
            this.activityId = Option.some((Object)activityId);
            return this;
        }

        public Builder content(Option<Html> content) {
            this.content = this.errors.checkHtml(content, "content", 5000);
            return this;
        }

        public Builder icon(Option<Image> icon) {
            this.icon = (Option)Preconditions.checkNotNull(icon, (Object)"icon");
            return this;
        }

        public Builder icon(Either<ValidationErrors, Image> imageOrErrors) {
            Preconditions.checkNotNull(imageOrErrors, (Object)"imageOrErrors");
            if (imageOrErrors.isRight()) {
                this.icon = imageOrErrors.right().toOption();
            } else {
                this.errors.addAll((ValidationErrors)imageOrErrors.left().get(), "icon");
            }
            return this;
        }

        public Builder id(Option<URI> id) {
            this.id = this.errors.checkAbsoluteUri(id, "id");
            return this;
        }

        public Builder idString(Option<String> id) {
            this.id = this.errors.checkAbsoluteUriString(id, "id");
            return this;
        }

        public Builder object(Option<ActivityObject> object) {
            this.object = (Option)Preconditions.checkNotNull(object, (Object)"object");
            return this;
        }

        public Builder object(Either<ValidationErrors, ActivityObject> objectOrErrors) {
            Preconditions.checkNotNull(objectOrErrors, (Object)"targetOrErrors");
            if (objectOrErrors.isRight()) {
                this.object = objectOrErrors.right().toOption();
            } else {
                this.errors.addAll((ValidationErrors)objectOrErrors.left().get(), "object");
            }
            return this;
        }

        public Builder poster(Option<String> poster) {
            this.poster = this.errors.checkString(poster, "poster");
            return this;
        }

        public Builder registeredUser(boolean registeredUser) {
            this.registeredUser = registeredUser;
            return this;
        }

        public Builder target(Option<ActivityObject> target) {
            this.target = (Option)Preconditions.checkNotNull(target, (Object)"target");
            return this;
        }

        public Builder target(Either<ValidationErrors, ActivityObject> targetOrErrors) {
            Preconditions.checkNotNull(targetOrErrors, (Object)"targetOrErrors");
            if (targetOrErrors.isRight()) {
                this.target = targetOrErrors.right().toOption();
            } else {
                this.errors.addAll((ValidationErrors)targetOrErrors.left().get(), "target");
            }
            return this;
        }

        public Builder title(Option<Html> title) {
            this.title = this.errors.checkHtml(title, "title", 255);
            return this;
        }

        public Builder url(Option<URI> url) {
            this.url = this.errors.checkAbsoluteUri(url, "url");
            return this;
        }

        public Builder urlString(Option<String> url) {
            this.url = this.errors.checkAbsoluteUriString(url, "url");
            return this;
        }

        public Builder verb(Option<URI> verb) {
            this.verb = this.errors.checkSimpleNameOrAbsoluteUri(verb, "verb");
            return this;
        }

        public Builder verbString(Option<String> verb) {
            this.verb = this.errors.checkSimpleNameOrAbsoluteUriString(verb, "verb");
            return this;
        }
    }
}

