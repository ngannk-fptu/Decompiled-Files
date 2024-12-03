/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.actions.PageNotPermittedAction
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.requestaccess.contextprovider;

import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.PageNotPermittedAction;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.user.User;
import java.util.Map;
import java.util.Objects;

public class RestrictedPageContextProvider
implements ContextProvider {
    private final PageManager pageManager;
    private final SpacePermissionManager spacePermissionManager;
    private final MailServerManager mailServerManager;
    private final AccessModeService accessModeService;
    private final PermissionManager permissionManager;

    public RestrictedPageContextProvider(@ComponentImport PageManager pageManager, @ComponentImport SpacePermissionManager spacePermissionManager, @ComponentImport MailServerManager mailServerManager, @ComponentImport AccessModeService accessModeService, @ComponentImport PermissionManager permissionManager) {
        this.pageManager = Objects.requireNonNull(pageManager);
        this.spacePermissionManager = Objects.requireNonNull(spacePermissionManager);
        this.mailServerManager = Objects.requireNonNull(mailServerManager);
        this.accessModeService = Objects.requireNonNull(accessModeService);
        this.permissionManager = Objects.requireNonNull(permissionManager);
    }

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        if (!(context.get("action") instanceof PageNotPermittedAction)) {
            return context;
        }
        context.put("readOnlyModeEnabled", this.accessModeService.getAccessMode().equals((Object)AccessMode.READ_ONLY));
        ConfluenceUser user = (ConfluenceUser)context.get("user");
        if (user == null) {
            return context;
        }
        PageNotPermittedAction action = (PageNotPermittedAction)context.get("action");
        if (action.getPage() == null) {
            AbstractPage draft = null;
            if (action.getDraftId() > 0L) {
                draft = this.pageManager.getAbstractPage(action.getDraftId());
            }
            if (draft == null) {
                return context;
            }
            action.setPage(draft);
        }
        boolean isUnpublishedDraft = action.getPage().getContentStatus() != null && action.getPage().getContentStatus().equals("draft") && action.getPage().isUnpublished();
        String spacePermission = action.getPage().getType().equals("blogpost") ? "EDITBLOG" : "EDITSPACE";
        boolean isUnpublishedDraftRestrictedBySpacePermissions = isUnpublishedDraft && !this.spacePermissionManager.hasPermission(spacePermission, action.getPage().getSpace(), (User)user);
        context.put("contentType", action.getPage().getType());
        context.put("inheritedPermissions", this.isPageWithRestrictedParent(user, action.getPage()));
        context.put("shouldDisplayRequestAccessButton", this.mailServerManager.isDefaultSMTPMailServerDefined());
        context.put("isUnpublishedDraft", isUnpublishedDraft);
        context.put("isUnpublishedDraftRestrictedBySpacePermissions", isUnpublishedDraftRestrictedBySpacePermissions);
        return context;
    }

    private boolean isPageWithRestrictedParent(ConfluenceUser user, AbstractPage content) {
        if (content == null || !(content instanceof Page)) {
            return false;
        }
        Page page = (Page)content;
        if (page.isRootLevel()) {
            return false;
        }
        return !this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)page.getParent());
    }
}

