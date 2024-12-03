/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.custom_apps.api;

import com.atlassian.plugins.custom_apps.api.CustomApp;
import com.atlassian.plugins.custom_apps.api.CustomAppNotFoundException;
import com.atlassian.plugins.custom_apps.api.CustomAppsValidationException;
import java.util.List;
import javax.annotation.Nonnull;

public interface CustomAppService {
    @Nonnull
    public List<CustomApp> getCustomApps();

    @Nonnull
    public List<CustomApp> getLocalCustomAppsAndRemoteLinks();

    public CustomApp get(String var1) throws CustomAppNotFoundException;

    public void delete(String var1) throws CustomAppNotFoundException;

    public CustomApp create(String var1, String var2, String var3, boolean var4, List<String> var5) throws CustomAppsValidationException;

    public CustomApp update(String var1, String var2, String var3, boolean var4, List<String> var5) throws CustomAppNotFoundException, CustomAppsValidationException;

    public void moveAfter(int var1, int var2) throws CustomAppNotFoundException;

    public void moveToStart(int var1) throws CustomAppNotFoundException;
}

