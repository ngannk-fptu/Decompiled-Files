/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.core.task.Task
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  com.google.common.base.Joiner
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.impl.ratelimiter.ActionRateLimiter;
import com.atlassian.confluence.mail.template.ConfluenceMailQueueItem;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.CaptchaAware;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.core.task.Task;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

public class SiteSupportAction
extends ConfluenceActionSupport
implements CaptchaAware,
FormAware {
    protected WikiStyleRenderer wikiStyleRenderer;
    protected CaptchaManager captchaManager;
    protected MultiQueueTaskManager taskManager;
    private MailServerManager mailServerManager;
    private String subject = null;
    private String description = null;
    private String contactAddress = null;
    private ActionRateLimiter actionRateLimiterSiteSupport;

    public void setActionRateLimiterSiteSupport(ActionRateLimiter actionRateLimiterSiteSupport) {
        this.actionRateLimiterSiteSupport = actionRateLimiterSiteSupport;
    }

    @HtmlSafe
    public String getMessage() {
        return this.wikiStyleRenderer.convertWikiToXHtml((RenderContext)new PageContext(), this.getGlobalSettings().getCustomContactMessage());
    }

    public boolean isDisplaySupportRequest() {
        return this.getGlobalSettings().isShowContactAdministratorsForm();
    }

    public boolean isAdminstratorEmailAddresses() {
        return StringUtils.isNotBlank((CharSequence)this.getAdministratorEmails());
    }

    public boolean isMailServerAvailable() {
        return this.mailServerManager.isDefaultSMTPMailServerDefined();
    }

    public String getToDisplayValue() {
        return this.getText("administrators.contact.to.content");
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean isEditMode() {
        return true;
    }

    public String doContactAdministrators() {
        if (this.isDisplaySupportRequest()) {
            String isReqAllowed = this.isAnonymousUser() ? this.actionRateLimiterSiteSupport.isRequestAllowed("", "docontactadministrators") : (AuthenticatedUserThreadLocal.get().getEmail() != null && AuthenticatedUserThreadLocal.get().getEmail().equalsIgnoreCase(this.contactAddress) ? this.actionRateLimiterSiteSupport.isRequestAllowed(this.contactAddress, "docontactadministrators") : "request-denied");
            if (!isReqAllowed.equals("success")) {
                ServletActionContext.getResponse().setStatus(429);
                return isReqAllowed;
            }
            if (StringUtils.isBlank((CharSequence)this.subject)) {
                this.subject = this.getText("administrators.contact.default.subject");
            }
            StringBuilder messageBody = new StringBuilder(this.getText("administrators.contact.origination"));
            messageBody.append("\n\n").append(this.getDescription());
            String adminEmails = this.getAdministratorEmails();
            if (StringUtils.isNotBlank((CharSequence)adminEmails)) {
                ConfluenceMailQueueItem item = new ConfluenceMailQueueItem(this.getAdministratorEmails(), this.getSubject(), messageBody.toString(), "text/plain");
                item.setFromAddress(this.getContactAddress());
                this.taskManager.addTask("mail", (Task & Serializable)() -> item.send());
                return "success";
            }
        }
        return "input";
    }

    @Override
    public String doDefault() throws Exception {
        ConfluenceUser user = this.getAuthenticatedUser();
        if (user != null) {
            this.contactAddress = user.getEmail();
        }
        this.subject = this.getText("administrators.contact.default.subject");
        return super.doDefault();
    }

    public String getContactAddress() {
        return this.contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    private String getAdministratorEmails() {
        Group adminGroup = this.userAccessor.getGroup("confluence-administrators");
        Iterable adminUsers = Iterables.filter(this.userAccessor.getMembers(adminGroup), this.active());
        Iterable adminEmails = Iterables.filter((Iterable)Iterables.transform((Iterable)adminUsers, this.email()), (Predicate)Predicates.notNull());
        return Joiner.on((char)',').join(adminEmails);
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public void setCaptchaManager(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }

    public CaptchaManager getCaptchaManager() {
        return this.captchaManager;
    }

    public void setTaskManager(MultiQueueTaskManager tm) {
        this.taskManager = tm;
    }

    public void setMailServerManager(MailServerManager mailServerManager) {
        this.mailServerManager = mailServerManager;
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    private Function<? super ConfluenceUser, String> email() {
        return user -> StringUtils.trimToNull((String)user.getEmail());
    }

    private Predicate<? super ConfluenceUser> active() {
        return input -> !this.userAccessor.isDeactivated((User)input);
    }

    @Override
    public void validate() {
        super.validate();
        if (!this.isAnonymousUser() && this.contactAddress != null && AuthenticatedUserThreadLocal.get().getEmail() != null && !AuthenticatedUserThreadLocal.get().getEmail().equalsIgnoreCase(this.contactAddress)) {
            this.addFieldError("contactAddress", this.getText("administrators.contact.from.mismatch"));
        }
    }
}

