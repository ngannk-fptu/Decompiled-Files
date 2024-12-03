/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.api;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.application.bamboo.BambooApplicationType;
import com.atlassian.applinks.api.application.bitbucket.BitbucketApplicationType;
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType;
import com.atlassian.applinks.api.application.crowd.CrowdApplicationType;
import com.atlassian.applinks.api.application.fecru.FishEyeCrucibleApplicationType;
import com.atlassian.applinks.api.application.generic.GenericApplicationType;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.api.application.refapp.RefAppApplicationType;
import javax.annotation.Nonnull;

public interface ApplicationTypeVisitor<T> {
    public T visit(@Nonnull BambooApplicationType var1);

    public T visit(@Nonnull BitbucketApplicationType var1);

    public T visit(@Nonnull ConfluenceApplicationType var1);

    public T visit(@Nonnull CrowdApplicationType var1);

    public T visit(@Nonnull FishEyeCrucibleApplicationType var1);

    public T visit(@Nonnull GenericApplicationType var1);

    public T visit(@Nonnull JiraApplicationType var1);

    public T visit(@Nonnull RefAppApplicationType var1);

    public T visitDefault(@Nonnull ApplicationType var1);
}

