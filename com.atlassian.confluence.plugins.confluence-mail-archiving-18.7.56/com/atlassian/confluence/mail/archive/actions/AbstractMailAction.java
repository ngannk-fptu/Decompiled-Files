/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.actions.AbstractSpaceAction
 *  com.atlassian.confluence.themes.ThemeHelper
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.opensymphony.xwork2.Action
 */
package com.atlassian.confluence.mail.archive.actions;

import com.atlassian.confluence.mail.archive.Mail;
import com.atlassian.confluence.mail.archive.MailContentManager;
import com.atlassian.confluence.mail.archive.MailHelper;
import com.atlassian.confluence.mail.archive.actions.MailActionBreadcrumb;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.themes.ThemeHelper;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.opensymphony.xwork2.Action;

public abstract class AbstractMailAction
extends AbstractSpaceAction
implements BreadcrumbAware {
    protected long id;
    protected Mail mail;
    protected MailContentManager localMailContentManager;
    private ThemeHelper mailHelper;
    private BreadcrumbGenerator breadcrumbGenerator;

    public Mail getMail() {
        if (this.mail == null) {
            this.mail = this.localMailContentManager.getById(this.id);
        }
        return this.mail;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setLocalMailContentManager(MailContentManager localMailContentManager) {
        this.localMailContentManager = localMailContentManager;
    }

    public void setBreadcrumbGenerator(@ComponentImport BreadcrumbGenerator breadcrumbGenerator) {
        this.breadcrumbGenerator = breadcrumbGenerator;
    }

    public ThemeHelper getMailHelper() {
        if (this.mailHelper == null) {
            this.mailHelper = new MailHelper(this);
        }
        return this.mailHelper;
    }

    public Breadcrumb getBreadcrumb() {
        return new MailActionBreadcrumb((Object)this, this.getSpace(), this.getMail(), this.breadcrumbGenerator.getSpaceAdminBreadcrumb((Action)this, this.getSpace()));
    }
}

