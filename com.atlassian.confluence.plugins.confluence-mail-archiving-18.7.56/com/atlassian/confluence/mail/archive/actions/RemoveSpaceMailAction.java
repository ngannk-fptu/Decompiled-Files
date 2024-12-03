/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.actions.AbstractSpaceAction
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.mail.archive.actions;

import com.atlassian.confluence.mail.archive.MailContentManager;
import com.atlassian.confluence.mail.archive.actions.MailActionBreadcrumb;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class RemoveSpaceMailAction
extends AbstractSpaceAction
implements BreadcrumbAware {
    private MailContentManager localMailContentManager;
    private BreadcrumbGenerator breadcrumbGenerator;

    public String doRemove() throws Exception {
        this.localMailContentManager.removeMailInSpace(this.getSpace());
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        return "success";
    }

    public void setLocalMailContentManager(MailContentManager localMailContentManager) {
        this.localMailContentManager = localMailContentManager;
    }

    public void setBreadcrumbGenerator(@ComponentImport BreadcrumbGenerator breadcrumbGenerator) {
        this.breadcrumbGenerator = breadcrumbGenerator;
    }

    public Breadcrumb getBreadcrumb() {
        return new MailActionBreadcrumb((Object)this, this.getSpace(), null, this.breadcrumbGenerator.getAdvancedBreadcrumb(this.getSpace()));
    }

    public boolean isPermitted() {
        return super.isPermitted() && this.spacePermissionManager.hasPermission("REMOVEMAIL", this.getSpace(), (User)this.getAuthenticatedUser());
    }
}

