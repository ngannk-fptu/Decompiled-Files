/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces.listeners;

import com.atlassian.confluence.event.events.content.user.PersonalInformationUpdateEvent;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.event.api.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdatePersonalSpaceListener {
    private static final Logger log = LoggerFactory.getLogger(UpdatePersonalSpaceListener.class);
    private final SpaceManager spaceManager;

    public UpdatePersonalSpaceListener(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    @EventListener
    public void handleEvent(PersonalInformationUpdateEvent updateEvent) {
        PersonalInformation personalInfo = updateEvent.getPersonalInformation();
        String fullName = personalInfo.getUser() != null ? personalInfo.getUser().getFullName() : null;
        Space personalSpace = this.spaceManager.getPersonalSpace(personalInfo.getUser());
        if (personalSpace == null) {
            return;
        }
        if (fullName == null || fullName.equals(personalSpace.getName())) {
            return;
        }
        try {
            Space originalSpace = (Space)personalSpace.clone();
            personalSpace.setName(fullName);
            this.spaceManager.saveSpace(personalSpace, originalSpace);
            log.info("Personal space name for user " + personalInfo.getUsername() + " has been updated to " + fullName);
        }
        catch (CloneNotSupportedException e) {
            log.error("Could not clone personal space? " + personalSpace);
        }
    }
}

