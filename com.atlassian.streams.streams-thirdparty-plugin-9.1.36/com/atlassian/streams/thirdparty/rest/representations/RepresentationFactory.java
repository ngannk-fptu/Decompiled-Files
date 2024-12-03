/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.UserProfile
 *  com.google.common.base.Function
 */
package com.atlassian.streams.thirdparty.rest.representations;

import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.thirdparty.api.Activity;
import com.atlassian.streams.thirdparty.api.ActivityObject;
import com.atlassian.streams.thirdparty.api.ActivityQuery;
import com.atlassian.streams.thirdparty.api.Application;
import com.atlassian.streams.thirdparty.api.Image;
import com.atlassian.streams.thirdparty.rest.representations.ActivityCollectionRepresentation;
import com.atlassian.streams.thirdparty.rest.representations.ActivityObjectRepresentation;
import com.atlassian.streams.thirdparty.rest.representations.ActivityRepresentation;
import com.atlassian.streams.thirdparty.rest.representations.MediaLinkRepresentation;
import com.google.common.base.Function;

public interface RepresentationFactory {
    public ActivityCollectionRepresentation createActivityCollectionRepresentation(Iterable<Activity> var1, ActivityQuery var2);

    public ActivityRepresentation createActivityRepresentation(Activity var1);

    public Function<Activity, ActivityRepresentation> toActivityRepresentation();

    public ActivityObjectRepresentation createActivityObjectRepresentation(ActivityObject var1);

    public ActivityObjectRepresentation createActivityObjectRepresentation(Application var1);

    public ActivityObjectRepresentation createActivityObjectRepresentation(UserProfile var1);

    public MediaLinkRepresentation createMediaLinkRepresentation(Image var1);

    public Function<Image, MediaLinkRepresentation> toMediaLinkRepresentation();
}

