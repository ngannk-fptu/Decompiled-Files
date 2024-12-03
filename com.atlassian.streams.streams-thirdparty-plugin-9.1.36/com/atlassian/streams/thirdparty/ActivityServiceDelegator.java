/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Preconditions
 */
package com.atlassian.streams.thirdparty;

import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.thirdparty.ActivityServiceActiveObjects;
import com.atlassian.streams.thirdparty.api.Activity;
import com.atlassian.streams.thirdparty.api.ActivityQuery;
import com.atlassian.streams.thirdparty.api.ActivityService;
import com.atlassian.streams.thirdparty.api.Application;
import com.google.common.base.Preconditions;

public class ActivityServiceDelegator
implements ActivityService {
    private ActivityServiceActiveObjects delegate;

    public ActivityServiceDelegator(ActivityServiceActiveObjects delegate) {
        this.delegate = (ActivityServiceActiveObjects)Preconditions.checkNotNull((Object)delegate);
    }

    @Override
    public Activity postActivity(Activity activity) {
        return this.delegate.postActivity(activity);
    }

    @Override
    public Option<Activity> getActivity(long activityId) {
        return this.delegate.getActivity(activityId);
    }

    @Override
    public Iterable<Activity> activities(ActivityQuery query) {
        return this.delegate.activities(query);
    }

    @Override
    public boolean delete(long activityId) {
        return this.delegate.delete(activityId);
    }

    @Override
    public Iterable<Application> applications() {
        return this.delegate.applications();
    }
}

