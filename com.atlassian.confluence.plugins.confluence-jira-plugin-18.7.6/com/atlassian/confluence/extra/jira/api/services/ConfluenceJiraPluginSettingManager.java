/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.extra.jira.api.services;

import java.util.Optional;
import javax.annotation.Nonnull;

public interface ConfluenceJiraPluginSettingManager {
    public void setCacheTimeoutInMinutes(@Nonnull Optional<Integer> var1);

    @Nonnull
    public Optional<Integer> getCacheTimeoutInMinutes();
}

