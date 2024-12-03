/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
 *  com.atlassian.activeobjects.external.ModelVersion
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.core.util.PairType
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Sets
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.calendar3.DefaultPrivateCalendarUrlManager;
import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarBandanaContext;
import com.atlassian.confluence.extra.calendar3.upgrade.task.CalendarModelVersion;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.usercompatibility.UserCompatibilityHelper;
import com.atlassian.core.util.PairType;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserNameToUserKeyUpgradeTask
implements ActiveObjectsUpgradeTask {
    private static final Logger LOG = LoggerFactory.getLogger(UserNameToUserKeyUpgradeTask.class);
    private static Set<String> PRIVILEGES_TO_MIGRATE = Collections.unmodifiableSet(Sets.newHashSet((Object[])new String[]{Privilege.__view_events_users__.toString(), Privilege.__edit_events_users__.toString()}));
    private final BuildInformationManager buildInformationManager;
    private final BandanaManager bandanaManager;
    private final SubCalendarDataAccessor subCalendarDataAccessor;

    public UserNameToUserKeyUpgradeTask(BuildInformationManager buildInformationManager, BandanaManager bandanaManager) {
        this.buildInformationManager = buildInformationManager;
        this.bandanaManager = bandanaManager;
        this.subCalendarDataAccessor = new SubCalendarDataAccessor(bandanaManager);
    }

    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf((String)CalendarModelVersion.CALENDAR_MODEL_VERSION_1);
    }

    public void upgrade(ModelVersion currentVersion, ActiveObjects ao) {
        LOG.info("Start migrating for Username to Userkey");
        this.migrateSubCalendars();
        this.migrateSubCalendarPrivateUrls();
        LOG.info("Start migrating for Username to Userkey");
    }

    private void migrateSubCalendarPrivateUrls() {
        this.migrateUserNamesInPrivateUrlUserMap();
        this.migrateUserNamesInPrivateUrlTokenMap();
    }

    private void migrateUserNamesInPrivateUrlTokenMap() {
        for (String secureToken : this.bandanaManager.getKeys((BandanaContext)DefaultPrivateCalendarUrlManager.TOKEN_MAP_CONTEXT)) {
            PairType subCalendarIdUserNamePair = (PairType)this.bandanaManager.getValue((BandanaContext)DefaultPrivateCalendarUrlManager.TOKEN_MAP_CONTEXT, secureToken);
            if (subCalendarIdUserNamePair == null) continue;
            String userName = (String)((Object)subCalendarIdUserNamePair.getValue());
            UserNameToUserKeyConversionResult result = this.tryToGetUserKeyForUserName(userName);
            if (result.isSuccess) {
                subCalendarIdUserNamePair.setValue((Serializable)((Object)result.userKey));
                this.bandanaManager.setValue((BandanaContext)DefaultPrivateCalendarUrlManager.TOKEN_MAP_CONTEXT, secureToken, (Object)subCalendarIdUserNamePair);
                LOG.info(String.format("Migrated user name %s to user key for secure token for sub-calendar %s", userName, subCalendarIdUserNamePair.getKey()));
                continue;
            }
            LOG.debug("Unable to find user key for user {}. Maybe that user name already be migrated.", (Object)subCalendarIdUserNamePair.getValue());
        }
    }

    private void migrateUserNamesInPrivateUrlUserMap() {
        for (String userName : this.bandanaManager.getKeys((BandanaContext)DefaultPrivateCalendarUrlManager.USER_MAP_CONTEXT)) {
            List tokens = (List)this.bandanaManager.getValue((BandanaContext)DefaultPrivateCalendarUrlManager.USER_MAP_CONTEXT, userName);
            if (tokens == null || tokens.isEmpty()) continue;
            UserNameToUserKeyConversionResult result = this.tryToGetUserKeyForUserName(userName);
            if (result.isSuccess) {
                this.bandanaManager.setValue((BandanaContext)DefaultPrivateCalendarUrlManager.USER_MAP_CONTEXT, result.userKey, (Object)tokens);
                LOG.info(String.format("Migrated user name %s to user key for all tokens.", userName));
                continue;
            }
            LOG.debug("Unable to find user key for user {}. Maybe that user name already be migrated.", (Object)userName);
        }
    }

    private void migrateSubCalendars() {
        for (String storeKey : this.subCalendarDataAccessor.getStorageKeys()) {
            for (String subCalendarId : this.subCalendarDataAccessor.getSubCalendarIdsOfStorage(storeKey)) {
                try {
                    JSONObject subCalendarJson = this.subCalendarDataAccessor.getSubCalendarAsJson(storeKey, subCalendarId);
                    if (subCalendarJson == null) continue;
                    this.migrateSubCalendarPermissions(storeKey, subCalendarId);
                    Set<String> fieldsToMigrate = this.subCalendarDataAccessor.getPropertiesToMigrate(storeKey);
                    for (String userNameProperty : fieldsToMigrate) {
                        if (!subCalendarJson.has(userNameProperty)) continue;
                        String userName = subCalendarJson.getString(userNameProperty);
                        UserNameToUserKeyConversionResult result = this.tryToGetUserKeyForUserName(userName);
                        String userKey = result.isSuccess ? result.userKey : userName;
                        subCalendarJson.put(userNameProperty, (Object)userKey);
                        this.subCalendarDataAccessor.updateSubCalendar(storeKey, subCalendarId, subCalendarJson);
                        LOG.info(String.format("Migrated property \"%s\" of sub-calendar %s in store %s: %s -> %s", userNameProperty, subCalendarId, storeKey, userName, userKey));
                    }
                }
                catch (JSONException jsonError) {
                    LOG.error(String.format("Error reading JSON object of sub-calendar %s in store %s", subCalendarId, storeKey), (Throwable)jsonError);
                }
            }
        }
    }

    private void migrateSubCalendarPermissions(String storeKey, String subCalendarId) {
        for (String privilege : PRIVILEGES_TO_MIGRATE) {
            Set<String> privilegedUserNames = this.subCalendarDataAccessor.getUsersPrivileged(storeKey, subCalendarId, privilege);
            HashSet privilegedUserKeys = Sets.newHashSet((Iterable)Collections2.filter((Collection)Collections2.transform(privilegedUserNames, userName -> {
                UserNameToUserKeyConversionResult result = this.tryToGetUserKeyForUserName((String)userName);
                return result.isSuccess ? result.userKey : userName;
            }), StringUtils::isNotBlank));
            this.subCalendarDataAccessor.setUsersPrivileged(storeKey, subCalendarId, privilege, privilegedUserKeys);
            LOG.info(String.format("Migrated \"%s\" restriction to sub-calendar %s in store %s: %s -> %s", privilege, subCalendarId, storeKey, privilegedUserNames, privilegedUserKeys));
        }
    }

    private UserNameToUserKeyConversionResult tryToGetUserKeyForUserName(String userName) {
        String userKey = UserCompatibilityHelper.getStringKeyForUsername(userName);
        return new UserNameToUserKeyConversionResult(StringUtils.isNotBlank(userKey), userKey);
    }

    private static class SubCalendarDataAccessor {
        private static Set<String> DEFAULT_FIELD_TO_MIGRATE = Collections.unmodifiableSet(Sets.newHashSet((Object[])new String[]{"creator"}));
        private static final Map<String, Set<String>> SUB_CALENDAR_STORAGE_KEY_TO_MIGRATE_FIELDS_MAP = Collections.unmodifiableMap(new LinkedHashMap<String, Set<String>>(){
            {
                this.put("JIRA_ISSUE_DATES_SUB_CALENDAR_STORE", DEFAULT_FIELD_TO_MIGRATE);
                this.put("AGILE_SPRINTS_SUB_CALENDAR_STORE", DEFAULT_FIELD_TO_MIGRATE);
                this.put("JIRA_PROJECT_RELEASES_SUB_CALENDAR_STORE", DEFAULT_FIELD_TO_MIGRATE);
                this.put("com.atlassian.confluence.extra.calendar3.calendarstore.generic.BirthdaySubCalendarDataStore", DEFAULT_FIELD_TO_MIGRATE);
                this.put("com.atlassian.confluence.extra.calendar3.calendarstore.generic.TravelSubCalendarDataStore", DEFAULT_FIELD_TO_MIGRATE);
                this.put("com.atlassian.confluence.extra.calendar3.calendarstore.generic.LeaveSubCalendarDataStore", DEFAULT_FIELD_TO_MIGRATE);
                this.put("com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericLocalSubCalendarDataStore", DEFAULT_FIELD_TO_MIGRATE);
                this.put("com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericSubCalendarDataStore", DEFAULT_FIELD_TO_MIGRATE);
                this.put("com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore", DEFAULT_FIELD_TO_MIGRATE);
                this.put("com.atlassian.confluence.extra.calendar3.calendarstore.JiraCalendarDataStore", DEFAULT_FIELD_TO_MIGRATE);
                this.put("com.atlassian.confluence.extra.calendar3.calendarstore.PeopleCalendarDataStore", DEFAULT_FIELD_TO_MIGRATE);
                this.put("com.atlassian.confluence.extra.calendar3.calendarstore.LocalCalendarDataStore", DEFAULT_FIELD_TO_MIGRATE);
            }
        });
        private final BandanaManager bandanaManager;

        private SubCalendarDataAccessor(BandanaManager bandanaManager) {
            this.bandanaManager = bandanaManager;
        }

        private Set<String> getStorageKeys() {
            return Sets.newLinkedHashSet(SUB_CALENDAR_STORAGE_KEY_TO_MIGRATE_FIELDS_MAP.keySet());
        }

        private BandanaContext getSubCalendarContextForStorage(String storageKey) {
            return new CalendarBandanaContext(DigestUtils.sha1Hex(storageKey));
        }

        private Set<String> getSubCalendarIdsOfStorage(String storageKey) {
            return Sets.newHashSet((Iterable)this.bandanaManager.getKeys(this.getSubCalendarContextForStorage(storageKey)));
        }

        private JSONObject getSubCalendarAsJson(String storageKey, String subCalendarId) throws JSONException {
            String json = (String)this.bandanaManager.getValue(this.getSubCalendarContextForStorage(storageKey), subCalendarId);
            return StringUtils.isBlank(json) ? null : new JSONObject(json);
        }

        private Set<String> getPropertiesToMigrate(String storageKey) {
            return SUB_CALENDAR_STORAGE_KEY_TO_MIGRATE_FIELDS_MAP.get(storageKey);
        }

        private void updateSubCalendar(String storageKey, String subCalendarId, JSONObject subCalendarObject) {
            this.bandanaManager.setValue(this.getSubCalendarContextForStorage(storageKey), subCalendarId, (Object)subCalendarObject.toString());
        }

        private Set<String> getUsersPrivileged(String storeKey, String subCalendarId, String privilege) {
            Set userNames = (Set)this.bandanaManager.getValue(this.getSubCalendarPermissionContext(storeKey, subCalendarId), privilege);
            return userNames == null ? Collections.emptySet() : userNames;
        }

        private BandanaContext getSubCalendarPermissionContext(String storageKey, String subCalendarId) {
            return new CalendarBandanaContext(DigestUtils.sha1Hex(storageKey), subCalendarId);
        }

        private void setUsersPrivileged(String storeKey, String subCalendarId, String privilege, Set<String> userKeys) {
            if (userKeys == null || userKeys.isEmpty()) {
                this.bandanaManager.removeValue(this.getSubCalendarPermissionContext(storeKey, subCalendarId), privilege);
            } else {
                this.bandanaManager.setValue(this.getSubCalendarPermissionContext(storeKey, subCalendarId), privilege, userKeys);
            }
        }
    }

    private class UserNameToUserKeyConversionResult {
        public boolean isSuccess;
        public String userKey;

        public UserNameToUserKeyConversionResult(boolean result, String userKey) {
            this.isSuccess = result;
            this.userKey = userKey;
        }
    }

    public static enum Privilege {
        __view_events_users__,
        __edit_events_users__,
        __view_events_groups__,
        __edit_events_groups__;

    }
}

