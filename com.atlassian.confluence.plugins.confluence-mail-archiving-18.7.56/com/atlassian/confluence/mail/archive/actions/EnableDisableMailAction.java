/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.actions.AbstractSpaceAction
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.opensymphony.xwork2.Action
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.archive.actions;

import com.atlassian.confluence.mail.archive.MailAccount;
import com.atlassian.confluence.mail.archive.MailAccountManager;
import com.atlassian.confluence.mail.archive.actions.MailActionBreadcrumb;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.opensymphony.xwork2.Action;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnableDisableMailAction
extends AbstractSpaceAction
implements BreadcrumbAware {
    private static final Logger log = LoggerFactory.getLogger(EnableDisableMailAction.class);
    private int id;
    private MailAccountManager mailAccountManager;
    private BreadcrumbGenerator breadcrumbGenerator;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() {
        MailAccount mailAccount = this.mailAccountManager.getMailAccount(this.getSpace(), this.id);
        if (mailAccount == null) {
            log.error("Could not load mailAccount with id [" + this.id + "] and spacekey [" + this.key + "]");
            this.addActionError("error.could.not.load.mail.account", new Object[]{"" + this.id, this.key});
            return "error";
        }
        if (mailAccount.isEnabled()) {
            mailAccount.disable();
        } else {
            mailAccount.enable();
        }
        this.mailAccountManager.updateAccount(this.getSpace(), mailAccount);
        return "success";
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List getMailAccounts() {
        return this.mailAccountManager.getMailAccounts(this.getSpace());
    }

    public void setMailAccountManager(MailAccountManager mailAccountManager) {
        this.mailAccountManager = mailAccountManager;
    }

    public void setBreadcrumbGenerator(@ComponentImport BreadcrumbGenerator breadcrumbGenerator) {
        this.breadcrumbGenerator = breadcrumbGenerator;
    }

    protected List<String> getPermissionTypes() {
        List permissions = super.getPermissionTypes();
        this.addPermissionTypeTo("SETSPACEPERMISSIONS", permissions);
        return permissions;
    }

    public Breadcrumb getBreadcrumb() {
        return new MailActionBreadcrumb((Object)this, this.getSpace(), null, this.breadcrumbGenerator.getSpaceAdminBreadcrumb((Action)this, this.getSpace()));
    }
}

