/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.application.ApplicationDAO
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationImpl
 *  com.atlassian.crowd.model.application.ApplicationType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.crowd.dao.application.ApplicationDAO;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationImpl;
import com.atlassian.crowd.model.application.ApplicationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EmbeddedCrowdApplicationFactory {
    private static final Logger log = LoggerFactory.getLogger(EmbeddedCrowdApplicationFactory.class);
    private static final String APPLICATION_NAME = "confluence";
    private ApplicationDAO applicationDao;

    private static ApplicationImpl createApplication() {
        ApplicationImpl application = ApplicationImpl.newInstance((String)APPLICATION_NAME, (ApplicationType)ApplicationType.CONFLUENCE);
        application.setActive(true);
        return application;
    }

    public Application getApplication() {
        try {
            return this.applicationDao.findByName(APPLICATION_NAME);
        }
        catch (ObjectNotFoundException objectNotFoundException) {
            log.info("Creating new internal application for Embedded Crowd user management");
            return this.applicationDao.add((Application)EmbeddedCrowdApplicationFactory.createApplication(), PasswordCredential.NONE);
        }
    }

    public void setApplicationDao(ApplicationDAO applicationDao) {
        this.applicationDao = applicationDao;
    }
}

