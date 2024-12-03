/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.opensymphony.module.propertyset.PropertySet
 */
package com.atlassian.confluence.plugins.like;

import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.user.preferences.UserPreferences;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.Map;

public class LikeNotificationPreferences {
    public static final String PROPERTY_USER_LIKES_NOTIFY_AUTHOR = "confluence.prefs.likes.notify.author";
    private static final Map<String, Boolean> DEFAULTS = Map.of("confluence.prefs.likes.notify.author", true);
    private final UserPreferences userPreferences;
    private final PropertySet backingPropertySet;

    public LikeNotificationPreferences(PropertySet propertySet) {
        this.userPreferences = new UserPreferences(propertySet);
        this.backingPropertySet = propertySet;
    }

    public boolean isNotifyAuthor() {
        return this.getPreference(PROPERTY_USER_LIKES_NOTIFY_AUTHOR);
    }

    public void setNotifyAuthor(boolean value) {
        this.setPreference(PROPERTY_USER_LIKES_NOTIFY_AUTHOR, value);
    }

    private boolean getPreference(String preferenceKey) {
        if (this.backingPropertySet != null && this.backingPropertySet.exists(preferenceKey)) {
            return this.userPreferences.getBoolean(preferenceKey);
        }
        return DEFAULTS.get(preferenceKey);
    }

    private void setPreference(String preferenceKey, boolean value) {
        if (this.backingPropertySet != null) {
            try {
                this.userPreferences.setBoolean(preferenceKey, value);
            }
            catch (AtlassianCoreException atlassianCoreException) {
                // empty catch block
            }
        }
    }
}

