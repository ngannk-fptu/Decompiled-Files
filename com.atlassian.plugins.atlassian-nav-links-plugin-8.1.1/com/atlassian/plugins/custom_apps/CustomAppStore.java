/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.custom_apps;

import com.atlassian.plugins.custom_apps.api.CustomApp;
import java.util.List;
import javax.annotation.Nullable;

public interface CustomAppStore {
    public static final String ID = "id";
    public static final String DISPLAY_NAME = "displayName";
    public static final String URL = "url";
    public static final String BASE_URL = "baseUrl";
    public static final String HIDE = "hide";
    public static final String EDITABLE = "editable";
    public static final String APPLICATION_TYPE = "applicationType";
    public static final String APPLICATION_NAME = "applicationName";
    public static final String ALLOWED_GROUPS = "allowedGroups";
    public static final String SELF = "self";

    public List<CustomApp> getAll();

    public void storeAll(@Nullable List<CustomApp> var1);

    public boolean isCustomOrder();

    public void setCustomOrder();
}

