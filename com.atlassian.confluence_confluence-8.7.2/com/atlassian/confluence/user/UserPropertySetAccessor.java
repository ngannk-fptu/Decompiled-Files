/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.user.Entity
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.properties.PropertySetFactory
 *  com.opensymphony.module.propertyset.PropertySet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.properties.PropertySetFactory;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class UserPropertySetAccessor
implements UserPreferencesAccessor {
    private static final Logger log = LoggerFactory.getLogger(UserPropertySetAccessor.class);
    private final PropertySetFactory propertySetFactory;

    UserPropertySetAccessor(PropertySetFactory propertySetFactory) {
        this.propertySetFactory = Objects.requireNonNull(propertySetFactory);
    }

    @Override
    @Nonnull
    public ConfluenceUserPreferences getConfluenceUserPreferences(@Nullable User user) {
        if (user == null || this.getPropertySet(user) == null) {
            return new ConfluenceUserPreferences();
        }
        return new ConfluenceUserPreferences(this.getPropertySet(user));
    }

    @Nullable
    PropertySet getPropertySet(@Nullable User user) {
        if (user == null) {
            return null;
        }
        PropertySet ps = null;
        try {
            ps = this.propertySetFactory.getPropertySet((Entity)user);
        }
        catch (EntityException e) {
            log.error(e.getMessage(), (Throwable)e);
        }
        return ps;
    }

    @Nullable
    public UserPreferences getUserPreferences(@Nullable User user) {
        if (user == null) {
            return null;
        }
        UserPreferences pref = null;
        try {
            pref = new UserPreferences(this.propertySetFactory.getPropertySet((Entity)user));
        }
        catch (EntityException e) {
            log.error(e.getMessage());
        }
        return pref;
    }

    void removeUserProperties(@Nonnull ConfluenceUser user) {
        try {
            PropertySet propertySet = this.propertySetFactory.getPropertySet((Entity)user);
            for (Object o : propertySet.getKeys()) {
                String key = (String)o;
                propertySet.remove(key);
            }
        }
        catch (EntityException e) {
            log.error(e.getMessage(), (Throwable)e);
        }
    }
}

