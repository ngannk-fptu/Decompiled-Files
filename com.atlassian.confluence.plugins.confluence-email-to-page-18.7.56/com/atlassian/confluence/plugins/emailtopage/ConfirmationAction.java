/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.service.NotValidException
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.plugins.emailgateway.api.EmailToContentConverter
 *  com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThreadAdminService
 *  com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThreadKey
 *  com.atlassian.confluence.plugins.emailgateway.api.analytics.CreatePageByEmailAnalytics$CreatePage
 *  com.atlassian.confluence.plugins.emailgateway.api.analytics.CreatePageByEmailAnalytics$RejectPageCreate
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.emailtopage;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.emailgateway.api.EmailToContentConverter;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThreadAdminService;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThreadKey;
import com.atlassian.confluence.plugins.emailgateway.api.analytics.CreatePageByEmailAnalytics;
import com.atlassian.event.api.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfirmationAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(ConfirmationAction.class);
    private StagedEmailThreadAdminService emailThreadConverterService;
    private EventPublisher eventPublisher;
    private String hash;
    private String pageId;
    private Exception validationException;
    private EmailToContentConverter<Page> emailToPageConverter;

    public String doConfirm() {
        StagedEmailThreadKey key = new StagedEmailThreadKey(this.hash);
        try {
            Page newPage = (Page)this.emailThreadConverterService.convertAndPublishStagedEmailThread(key, this.messageHolder, this.emailToPageConverter);
            this.pageId = newPage.getIdAsString();
            this.eventPublisher.publish((Object)new CreatePageByEmailAnalytics.CreatePage(newPage.getSpaceKey(), newPage.getId()));
            return "success";
        }
        catch (NotValidException e) {
            this.validationException = e;
            log.warn("Couldn't complete email-to-page creation, probably because page already exists: " + e.getMessage());
            return "input";
        }
        catch (IllegalArgumentException e) {
            log.warn("Couldn't complete email-to-page creation, probably because staged email has been deleted: " + e.getMessage());
            this.validationException = e;
            return "input";
        }
    }

    public String doReject() {
        this.emailThreadConverterService.deleteStagedEmailThread(new StagedEmailThreadKey(this.hash));
        this.eventPublisher.publish((Object)new CreatePageByEmailAnalytics.RejectPageCreate());
        return "success";
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPageId() {
        return this.pageId;
    }

    public void setEmailToPageConverter(EmailToContentConverter<Page> emailToPageConverter) {
        this.emailToPageConverter = emailToPageConverter;
    }

    public void setEmailThreadConverterService(StagedEmailThreadAdminService emailThreadConverterService) {
        this.emailThreadConverterService = emailThreadConverterService;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public Exception getValidationException() {
        return this.validationException;
    }
}

