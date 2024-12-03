/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserDetailsManager;
import com.atlassian.user.User;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultUserDetailsManager
implements UserDetailsManager {
    static final String KEY_PREFIX = "confluence.user.profile.";
    final ContentPropertyManager contentPropertyManager;
    private static final String PERSONAL_DETAILS_GROUP = "personal";
    private static final String BUSINESS_DETAILS_GROUP = "business";
    final PersonalInformationManager personalInformationManager;
    static final List<String> PERSONAL_DETAILS = Arrays.asList("phone", "im", "website");
    static final List<String> BUSINESS_DETAILS = Arrays.asList("position", "department", "location");
    static final List<String> PROFILE_GROUPS = Arrays.asList("personal", "business");
    final Map<String, List<String>> PROFILE_GROUPS_MAP;

    public DefaultUserDetailsManager(ContentPropertyManager contentPropertyManager, PersonalInformationManager personalInformationManager) {
        this.contentPropertyManager = contentPropertyManager;
        this.personalInformationManager = personalInformationManager;
        this.PROFILE_GROUPS_MAP = new HashMap<String, List<String>>();
        this.PROFILE_GROUPS_MAP.put(PERSONAL_DETAILS_GROUP, PERSONAL_DETAILS);
        this.PROFILE_GROUPS_MAP.put(BUSINESS_DETAILS_GROUP, BUSINESS_DETAILS);
    }

    @Override
    public String getStringProperty(User user, String key) {
        PersonalInformation personalInformation = this.personalInformationManager.getOrCreatePersonalInformation(user);
        return this.contentPropertyManager.getStringProperty(personalInformation, KEY_PREFIX + key);
    }

    @Override
    public void setStringProperty(User user, String key, String value) {
        PersonalInformation personalInformation = this.personalInformationManager.getOrCreatePersonalInformation(user);
        this.contentPropertyManager.setStringProperty(personalInformation, KEY_PREFIX + key, value);
    }

    @Override
    public void removeProperty(User user, String key) {
        PersonalInformation personalInformation = this.personalInformationManager.getOrCreatePersonalInformation(user);
        this.contentPropertyManager.removeProperty(personalInformation, KEY_PREFIX + key);
    }

    @Override
    public List<String> getProfileKeys(String groupKey) {
        return this.PROFILE_GROUPS_MAP.get(groupKey);
    }

    @Override
    public List<String> getProfileGroups() {
        return PROFILE_GROUPS;
    }
}

