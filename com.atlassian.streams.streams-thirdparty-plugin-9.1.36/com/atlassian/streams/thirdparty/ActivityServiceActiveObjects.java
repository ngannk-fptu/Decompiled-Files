/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 *  com.atlassian.streams.api.common.Option
 */
package com.atlassian.streams.thirdparty;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.thirdparty.api.Activity;
import com.atlassian.streams.thirdparty.api.ActivityQuery;
import com.atlassian.streams.thirdparty.api.Application;

@Transactional
public interface ActivityServiceActiveObjects {
    public Activity postActivity(Activity var1);

    public Option<Activity> getActivity(long var1);

    public Iterable<Activity> activities(ActivityQuery var1);

    public boolean delete(long var1);

    public Iterable<Application> applications();
}

