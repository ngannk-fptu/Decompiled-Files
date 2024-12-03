/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.highlight.service;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.highlight.SelectionModificationException;
import com.atlassian.confluence.plugins.highlight.model.TextSearch;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;

public abstract class SelectionValidator<T> {
    private final PermissionManager permissionManager;

    protected SelectionValidator(@ComponentImport PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public final void validate(long pageId, AbstractPage abstractPage, long lastFetchTime, TextSearch selection, T modification) throws SelectionModificationException {
        this.validateSelection(selection);
        this.validateModification(modification);
        this.validatePage(pageId, abstractPage, lastFetchTime);
        this.validatePermissions(abstractPage);
    }

    abstract SelectionValidator<T> validateModification(T var1) throws SelectionModificationException;

    protected SelectionValidator<T> validatePage(long pageId, AbstractPage abstractPage, long lastFetchTime) throws SelectionModificationException {
        if (abstractPage == null) {
            throw new SelectionModificationException(SelectionModificationException.Type.NO_OBJECT_TO_MODIFY, "Not found Page/Blogpost with id " + pageId);
        }
        if (abstractPage.getLastModificationDate().getTime() > lastFetchTime) {
            throw new SelectionModificationException(SelectionModificationException.Type.STALE_OBJECT_TO_MODIFY, "The page with id " + pageId + " has been modified since loading");
        }
        return this;
    }

    protected SelectionValidator<T> validatePermissions(AbstractPage abstractPage) throws SelectionModificationException {
        if (!this.permissionManager.hasCreatePermission((User)this.getUser(), (Object)abstractPage.getSpace(), abstractPage.getClass())) {
            throw new SelectionModificationException(SelectionModificationException.Type.NO_PERMISSION, "No permission to edit page/blogpost in space with key " + abstractPage.getSpace().getKey());
        }
        return this;
    }

    protected SelectionValidator<T> validateSelection(TextSearch selection) throws SelectionModificationException {
        if (StringUtils.isEmpty((CharSequence)selection.getText()) || selection.getMatchIndex() < 0 || selection.getNumMatches() < 1 || selection.getMatchIndex() >= selection.getNumMatches()) {
            throw new SelectionModificationException(SelectionModificationException.Type.INCORRECT_MODIFICATION, "The text selection is wrong");
        }
        return this;
    }

    protected SelectionValidator<T> validateModification(String modification) throws SelectionModificationException {
        if (StringUtils.isBlank((CharSequence)modification)) {
            throw new SelectionModificationException(SelectionModificationException.Type.INCORRECT_MODIFICATION, "No content for insert");
        }
        return this;
    }

    private ConfluenceUser getUser() {
        return AuthenticatedUserThreadLocal.get();
    }
}

