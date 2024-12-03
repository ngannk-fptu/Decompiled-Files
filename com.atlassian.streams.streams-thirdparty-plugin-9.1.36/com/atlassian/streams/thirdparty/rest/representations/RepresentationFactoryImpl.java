/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.UserProfile
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.thirdparty.rest.representations;

import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.thirdparty.api.Activity;
import com.atlassian.streams.thirdparty.api.ActivityObject;
import com.atlassian.streams.thirdparty.api.ActivityQuery;
import com.atlassian.streams.thirdparty.api.Application;
import com.atlassian.streams.thirdparty.api.Image;
import com.atlassian.streams.thirdparty.rest.LinkBuilder;
import com.atlassian.streams.thirdparty.rest.representations.ActivityCollectionRepresentation;
import com.atlassian.streams.thirdparty.rest.representations.ActivityObjectRepresentation;
import com.atlassian.streams.thirdparty.rest.representations.ActivityRepresentation;
import com.atlassian.streams.thirdparty.rest.representations.MediaLinkRepresentation;
import com.atlassian.streams.thirdparty.rest.representations.RepresentationFactory;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class RepresentationFactoryImpl
implements RepresentationFactory {
    private final LinkBuilder linkBuilder;
    private Function<Activity, ActivityRepresentation> toActivityRepresentation = new Function<Activity, ActivityRepresentation>(){

        public ActivityRepresentation apply(Activity from) {
            return RepresentationFactoryImpl.this.createActivityRepresentation(from);
        }
    };
    private Function<Image, MediaLinkRepresentation> toMediaLinkRepresentation = new Function<Image, MediaLinkRepresentation>(){

        public MediaLinkRepresentation apply(Image from) {
            return RepresentationFactoryImpl.this.createMediaLinkRepresentation(from);
        }
    };

    public RepresentationFactoryImpl(LinkBuilder linkBuilder) {
        this.linkBuilder = (LinkBuilder)Preconditions.checkNotNull((Object)linkBuilder, (Object)"linkBuilder");
    }

    @Override
    public ActivityCollectionRepresentation createActivityCollectionRepresentation(Iterable<Activity> activities, ActivityQuery query) {
        ImmutableList representations = ImmutableList.copyOf((Iterable)Iterables.transform(activities, this.toActivityRepresentation()));
        return new ActivityCollectionRepresentation((Collection<ActivityRepresentation>)representations, this.linkBuilder.build(activities, query));
    }

    @Override
    public ActivityRepresentation createActivityRepresentation(Activity activity) {
        Option date = Option.some((Object)activity.getPostedDate().toDate());
        return ActivityRepresentation.builder(this.createActivityObjectRepresentation(activity.getUser()), this.createActivityObjectRepresentation(activity.getApplication())).content(activity.getContent()).id(activity.getId()).icon((Option<MediaLinkRepresentation>)activity.getIcon().map(this.toMediaLinkRepresentation())).title(activity.getTitle()).published((Option<Date>)date).updated((Option<Date>)date).url(activity.getUrl()).verb(activity.getVerb()).links((Option<Map<String, URI>>)Option.some(this.linkBuilder.build(activity))).build();
    }

    @Override
    public Function<Activity, ActivityRepresentation> toActivityRepresentation() {
        return this.toActivityRepresentation;
    }

    @Override
    public ActivityObjectRepresentation createActivityObjectRepresentation(ActivityObject object) {
        return ActivityObjectRepresentation.builder().displayName(object.getDisplayName()).id(object.getId()).objectType(object.getType()).summary(object.getSummary()).url(object.getUrl()).build();
    }

    @Override
    public ActivityObjectRepresentation createActivityObjectRepresentation(Application application) {
        return ActivityObjectRepresentation.builder().displayName((Option<String>)Option.some((Object)application.getDisplayName())).id((Option<URI>)Option.some((Object)application.getId())).build();
    }

    @Override
    public ActivityObjectRepresentation createActivityObjectRepresentation(UserProfile userProfile) {
        ActivityObjectRepresentation.Builder builder = ActivityObjectRepresentation.builder().displayName((Option<String>)Option.some((Object)userProfile.getFullName())).idString((Option<String>)Option.some((Object)userProfile.getUsername())).url((Option<URI>)userProfile.getProfilePageUri());
        for (URI pictureUri : userProfile.getProfilePictureUri()) {
            builder.image((Option<MediaLinkRepresentation>)Option.some((Object)MediaLinkRepresentation.builder(pictureUri).build()));
        }
        return builder.build();
    }

    @Override
    public MediaLinkRepresentation createMediaLinkRepresentation(Image image) {
        return MediaLinkRepresentation.builder(image.getUrl()).height(image.getHeight()).width(image.getWidth()).build();
    }

    @Override
    public Function<Image, MediaLinkRepresentation> toMediaLinkRepresentation() {
        return this.toMediaLinkRepresentation;
    }
}

