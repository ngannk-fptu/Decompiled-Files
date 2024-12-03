/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.opensymphony.module.propertyset.PropertySet
 *  org.apache.commons.lang3.StringUtils
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.extra.calendar3.CalendarUserPreferenceStore;
import com.atlassian.confluence.extra.calendar3.model.UserCalendarPreference;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultCalendarUserPreferenceStore
implements CalendarUserPreferenceStore,
InitializingBean {
    private static final String CACHE_NAME = DefaultCalendarUserPreferenceStore.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger(DefaultCalendarUserPreferenceStore.class);
    private static final String CALENDAR_PREFERENCE_PROPERTY_KEY = "calendar";
    private final UserAccessor userAccessor;
    private final CacheManager cacheManager;

    @Autowired
    public DefaultCalendarUserPreferenceStore(@ComponentImport UserAccessor userAccessor, @ComponentImport CacheManager cacheManager) {
        this.userAccessor = userAccessor;
        this.cacheManager = cacheManager;
    }

    public void afterPropertiesSet() {
        this.getUserPreferenceCache().removeAll();
    }

    @Override
    public List<String> list(long start, long limit) throws Exception {
        return this.getUserPreferenceCache().getKeys().stream().filter(key -> key != null && !key.isEmpty()).skip(start).limit(limit).collect(Collectors.toList());
    }

    @Override
    public void setUserPreference(ConfluenceUser user, UserCalendarPreference userCalendarPreference) {
        PropertySet userPreferenceSet = this.getUserPreferencesSet(user);
        if (null != userPreferenceSet) {
            userPreferenceSet.setText(CALENDAR_PREFERENCE_PROPERTY_KEY, userCalendarPreference.toJson().toString());
        }
        this.getUserPreferenceCache().remove((Object)this.getUserPreferenceCacheKey(user));
    }

    @Override
    public void clearUserPreferenceCache(ConfluenceUser user) {
        this.getUserPreferenceCache().remove((Object)this.getUserPreferenceCacheKey(user));
    }

    private PropertySet getUserPreferencesSet(ConfluenceUser user) {
        return this.userAccessor.getPropertySet(user);
    }

    @Override
    public UserCalendarPreference getUserPreference(ConfluenceUser user) {
        return new UserCalendarPreference(this.getCachedUserPreference(user));
    }

    private UserCalendarPreference loadOrCreateUserCalendarPreference(ConfluenceUser user) {
        UserCalendarPreference userCalendarPreference;
        PropertySet userPreferenceSet = this.getUserPreferencesSet(user);
        if (null != userPreferenceSet) {
            String userCalendarPreferenceJson = userPreferenceSet.getText(CALENDAR_PREFERENCE_PROPERTY_KEY);
            if (StringUtils.isNotBlank((CharSequence)userCalendarPreferenceJson)) {
                try {
                    userCalendarPreference = this.toUserPreference(userCalendarPreferenceJson);
                }
                catch (JSONException jsonError) {
                    LOG.error("Unable to parse user calendar preference", (Throwable)jsonError);
                    userCalendarPreference = new UserCalendarPreference();
                    userPreferenceSet.setText(CALENDAR_PREFERENCE_PROPERTY_KEY, userCalendarPreference.toJson().toString());
                }
            } else {
                userCalendarPreference = new UserCalendarPreference();
            }
        } else {
            userCalendarPreference = new UserCalendarPreference();
        }
        return userCalendarPreference;
    }

    private String getUserPreferenceCacheKey(ConfluenceUser user) {
        return null == user ? "" : user.getKey().toString();
    }

    private UserCalendarPreference toUserPreference(String preferenceJson) throws JSONException {
        int i;
        int j;
        JSONObject jsonObject = new JSONObject(preferenceJson);
        UserCalendarPreference userCalendarPreference = new UserCalendarPreference();
        userCalendarPreference.setCalendarView(jsonObject.getString("view"));
        if (jsonObject.has("subCalendarsInView")) {
            JSONArray subCalendarsInViewArray = jsonObject.getJSONArray("subCalendarsInView");
            HashSet<String> subCalendarsInView = new HashSet<String>();
            j = subCalendarsInViewArray.length();
            for (i = 0; i < j; ++i) {
                subCalendarsInView.add(subCalendarsInViewArray.getString(i));
            }
            userCalendarPreference.setSubCalendarsInView(subCalendarsInView);
        }
        if (jsonObject.has("watchedSubCalendars")) {
            JSONArray watchedSubCalendarsArray = jsonObject.getJSONArray("watchedSubCalendars");
            HashSet<String> watchedSubCalendars = new HashSet<String>();
            j = watchedSubCalendarsArray.length();
            for (i = 0; i < j; ++i) {
                watchedSubCalendars.add(watchedSubCalendarsArray.getString(i));
            }
            userCalendarPreference.setWatchedSubCalendars(watchedSubCalendars);
        }
        if (jsonObject.has("disabledMessageKeys")) {
            JSONArray disabledMessageKeysArray = jsonObject.getJSONArray("disabledMessageKeys");
            HashSet<String> disabledMessageKeys = new HashSet<String>();
            j = disabledMessageKeysArray.length();
            for (i = 0; i < j; ++i) {
                disabledMessageKeys.add(disabledMessageKeysArray.getString(i));
            }
            userCalendarPreference.setDisabledMessageKeys(disabledMessageKeys);
        }
        if (jsonObject.has("disabledSubCalendars")) {
            JSONArray disabledSubCalendarsArray = jsonObject.getJSONArray("disabledSubCalendars");
            HashSet<String> disabledSubCalendars = new HashSet<String>();
            j = disabledSubCalendarsArray.length();
            for (i = 0; i < j; ++i) {
                disabledSubCalendars.add(disabledSubCalendarsArray.getString(i));
            }
            userCalendarPreference.setDisabledSubCalendars(disabledSubCalendars);
        }
        return userCalendarPreference;
    }

    private UserCalendarPreference getCachedUserPreference(ConfluenceUser user) {
        return (UserCalendarPreference)this.getUserPreferenceCache().get((Object)this.getUserPreferenceCacheKey(user), () -> this.loadOrCreateUserCalendarPreference(user));
    }

    private Cache<String, UserCalendarPreference> getUserPreferenceCache() {
        return this.cacheManager.getCache(CACHE_NAME, null, new CacheSettingsBuilder().remote().replicateAsynchronously().replicateViaInvalidation().build());
    }
}

