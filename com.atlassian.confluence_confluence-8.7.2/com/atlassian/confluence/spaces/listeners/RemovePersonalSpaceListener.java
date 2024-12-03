/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Indexer
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.event.api.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.spaces.listeners;

import com.atlassian.bonnie.Indexer;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.event.events.space.SpaceRemoveEvent;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.event.api.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class RemovePersonalSpaceListener {
    private static final Logger log = LoggerFactory.getLogger(RemovePersonalSpaceListener.class);
    private PersonalInformationManager personalInformationManager;
    private Indexer indexer;
    private I18NBeanFactory i18NBeanFactory;

    public RemovePersonalSpaceListener(@Qualifier(value="personalInformationManager") PersonalInformationManager personalInformationManager, @Qualifier(value="indexer") Indexer indexer, @Qualifier(value="userI18NBeanFactory") I18NBeanFactory i18NBeanFactory) {
        this.personalInformationManager = personalInformationManager;
        this.indexer = indexer;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @EventListener
    public void handleEvent(SpaceRemoveEvent event) {
        Space space = event.getSpace();
        if (!space.isPersonal()) {
            return;
        }
        ProgressMeter progress = event.getProgressMeter();
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        if (!progress.isCompletedSuccessfully()) {
            return;
        }
        try {
            ConfluenceUser user = space.getCreator();
            if (user == null) {
                log.warn("The owner of the personal space with key {} was not found.", (Object)space.getKey());
                return;
            }
            PersonalInformation personalInfo = this.personalInformationManager.getOrCreatePersonalInformation(user);
            this.indexer.reIndex((Searchable)personalInfo);
        }
        catch (Error | RuntimeException e) {
            progress.setCompletedSuccessfully(false);
            progress.setStatus(i18NBean.getText("progress.remove.space.failed"));
            throw e;
        }
    }
}

