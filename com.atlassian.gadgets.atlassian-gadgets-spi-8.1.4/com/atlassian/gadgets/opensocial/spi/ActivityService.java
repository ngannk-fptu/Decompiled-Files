/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.opensocial.OpenSocialRequestContext
 *  com.atlassian.gadgets.opensocial.model.Activity
 *  com.atlassian.gadgets.opensocial.model.AppId
 *  com.atlassian.gadgets.opensocial.model.PersonId
 *  javax.annotation.Nullable
 */
package com.atlassian.gadgets.opensocial.spi;

import com.atlassian.gadgets.opensocial.OpenSocialRequestContext;
import com.atlassian.gadgets.opensocial.model.Activity;
import com.atlassian.gadgets.opensocial.model.AppId;
import com.atlassian.gadgets.opensocial.model.PersonId;
import com.atlassian.gadgets.opensocial.spi.ActivityServiceException;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public interface ActivityService {
    public List<Activity> getActivities(Set<PersonId> var1, AppId var2, OpenSocialRequestContext var3) throws ActivityServiceException;

    public List<Activity> getActivities(PersonId var1, AppId var2, Set<String> var3, OpenSocialRequestContext var4) throws ActivityServiceException;

    @Nullable
    public Activity getActivity(PersonId var1, AppId var2, String var3, OpenSocialRequestContext var4) throws ActivityServiceException;

    public void deleteActivities(PersonId var1, AppId var2, Set<String> var3, OpenSocialRequestContext var4) throws ActivityServiceException;

    public Activity createActivity(PersonId var1, AppId var2, Activity var3, OpenSocialRequestContext var4) throws ActivityServiceException;
}

