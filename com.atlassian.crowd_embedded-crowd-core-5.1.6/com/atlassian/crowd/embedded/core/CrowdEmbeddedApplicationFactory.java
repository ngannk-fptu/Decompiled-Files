/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.application.ApplicationDAO
 *  com.atlassian.crowd.embedded.api.ApplicationFactory
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.model.application.Application
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.embedded.core;

import com.atlassian.crowd.dao.application.ApplicationDAO;
import com.atlassian.crowd.embedded.api.ApplicationFactory;
import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.model.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrowdEmbeddedApplicationFactory
implements ApplicationFactory {
    private static final Logger log = LoggerFactory.getLogger(CrowdEmbeddedApplicationFactory.class);
    private static final String APPLICATION_NAME = "crowd-embedded";
    private final ApplicationDAO applicationDao;

    public CrowdEmbeddedApplicationFactory(ApplicationDAO applicationDao) {
        this.applicationDao = applicationDao;
    }

    public Application getApplication() {
        try {
            return this.applicationDao.findByName(APPLICATION_NAME);
        }
        catch (ApplicationNotFoundException e) {
            log.debug("Crowd application : crowd-embedded not found.");
            return null;
        }
    }

    public String getApplicationName() {
        return APPLICATION_NAME;
    }

    public boolean isEmbeddedCrowd() {
        return true;
    }
}

