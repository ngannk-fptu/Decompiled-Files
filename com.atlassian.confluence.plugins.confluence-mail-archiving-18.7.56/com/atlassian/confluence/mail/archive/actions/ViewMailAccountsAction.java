/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.opensymphony.xwork2.Action
 */
package com.atlassian.confluence.mail.archive.actions;

import com.atlassian.confluence.mail.archive.MailAccount;
import com.atlassian.confluence.mail.archive.MailAccountManager;
import com.atlassian.confluence.mail.archive.actions.MailActionBreadcrumb;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.opensymphony.xwork2.Action;
import java.util.List;

@WebSudoRequired
public class ViewMailAccountsAction
extends AbstractSpaceAdminAction
implements BreadcrumbAware {
    private List<MailAccount> mailAccounts;
    private MailAccountManager mailAccountManager;
    private BreadcrumbGenerator breadcrumbGenerator;

    public String doDefault() throws Exception {
        if (this.getSpace() == null || this.getSpace().isPersonal()) {
            return "pagenotfound";
        }
        this.mailAccounts = this.mailAccountManager.getMailAccounts(this.getSpace());
        return super.doDefault();
    }

    public List getMailAccounts() {
        return this.mailAccounts;
    }

    public void setMailAccountManager(MailAccountManager mailAccountManager) {
        this.mailAccountManager = mailAccountManager;
    }

    public void setBreadcrumbGenerator(@ComponentImport BreadcrumbGenerator breadcrumbGenerator) {
        this.breadcrumbGenerator = breadcrumbGenerator;
    }

    private String getSpaceType(Space space) {
        if (space != null) {
            return space.getSpaceType().toString();
        }
        return null;
    }

    public Breadcrumb getBreadcrumb() {
        return new MailActionBreadcrumb((Object)this, this.getSpace(), null, this.breadcrumbGenerator.getSpaceAdminBreadcrumb((Action)this, this.getSpace()));
    }
}

