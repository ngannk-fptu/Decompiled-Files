/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.dashboard.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.content.service.WelcomeMessageService;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.actions.RssDescriptor;
import com.atlassian.confluence.event.events.dashboard.DashboardViewEvent;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.access.annotations.RequiresLicensedOrAnonymousConfluenceAccess;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@RequiresLicensedOrAnonymousConfluenceAccess
public class DashboardAction
extends ConfluenceActionSupport {
    private WelcomeMessageService welcomeMessageService;
    private PageContext pageContext;
    private EventPublisher eventPublisher;

    @PermittedMethods(value={HttpMethod.GET})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        this.eventPublisher.publish((Object)new DashboardViewEvent(this));
        return "success";
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @HtmlSafe
    public String getWelcomeMessage() {
        return this.welcomeMessageService.getWelcomeMessage();
    }

    public void setWelcomeMessageService(WelcomeMessageService welcomeMessageService) {
        this.welcomeMessageService = welcomeMessageService;
    }

    public WelcomeMessageService getWelcomeMessageService() {
        return this.welcomeMessageService;
    }

    public PageContext getPageContext() {
        if (this.pageContext == null) {
            this.pageContext = new PageContext();
        }
        return this.pageContext;
    }

    public RssDescriptor getRssDescriptor() {
        return new RssDescriptor("/spaces/createrssfeed.action?types=page&amp;types=blogpost&amp;types=comment&amp;spaces=&amp;sort=modified&amp;title=Dashboard+RSS+Feed&amp;maxResults=15", "Dashboard RSS Feed", this.getAuthenticatedUser() != null);
    }
}

