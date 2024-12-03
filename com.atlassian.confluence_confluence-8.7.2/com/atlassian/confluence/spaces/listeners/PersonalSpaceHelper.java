/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces.listeners;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PersonalSpaceHelper {
    private static final Logger log = LoggerFactory.getLogger(PersonalSpaceHelper.class);
    private final PersonalInformationManager personalInformationManager;

    public PersonalSpaceHelper(PersonalInformationManager personalInformationManager) {
        this.personalInformationManager = personalInformationManager;
    }

    @Deprecated(since="8.0", forRemoval=true)
    public PersonalSpaceHelper(PersonalInformationManager personalInformationManager, UserAccessor userAccessor) {
        this(personalInformationManager);
    }

    boolean isPersonalSpace(Space space) {
        if (!space.isPersonal()) {
            return false;
        }
        if (!Space.isValidPersonalSpaceKey(space.getKey())) {
            log.warn("The personal Space " + space.getKey() + " does not have a valid space key for a personal space so personal space cannot be created.");
            return false;
        }
        return true;
    }

    PersonalInformation getPersonalInformation(ConfluenceUser user) {
        if (user == null) {
            log.warn("The anonymous user cannot create a personal space.");
            return null;
        }
        PersonalInformation personalInformation = this.personalInformationManager.getOrCreatePersonalInformation(user);
        if (personalInformation == null) {
            log.warn("No personal information found for the user {} so personal space cannot be created.", (Object)user.getName());
            return null;
        }
        return personalInformation;
    }

    void blankPersonalInformation(PersonalInformation personalInfo) {
        String username = personalInfo.getUsername();
        PersonalInformation oldPersonalInfo = (PersonalInformation)personalInfo.clone();
        personalInfo.setBodyAsString(null);
        this.personalInformationManager.savePersonalInformation(personalInfo, oldPersonalInfo);
        if (log.isDebugEnabled()) {
            log.debug("Blanked personal information content for user: " + username);
        }
    }
}

