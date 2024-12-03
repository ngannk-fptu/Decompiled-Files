/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.whisper.plugin.api.CurrentUserPropertyService
 *  com.atlassian.whisper.plugin.api.UserPropertyManager
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.whisper.plugin.impl;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.whisper.plugin.api.CurrentUserPropertyService;
import com.atlassian.whisper.plugin.api.UserPropertyManager;
import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService
@Named(value="userPropertyService")
public class DefaultCurrentUserPropertyService
implements CurrentUserPropertyService {
    private final UserManager userManager;
    private final UserPropertyManager userPropertyManager;

    @Inject
    public DefaultCurrentUserPropertyService(@ComponentImport UserManager userManager, UserPropertyManager userPropertyManager) {
        this.userManager = userManager;
        this.userPropertyManager = userPropertyManager;
    }

    public void setValue(String key, String value) {
        this.userPropertyManager.setValue(this.getCurrentUser(), key, value);
    }

    public void deleteValue(String key) {
        this.userPropertyManager.deleteValue(this.getCurrentUser(), key);
    }

    public String getValue(String key) {
        return this.userPropertyManager.getValue(this.getCurrentUser(), key);
    }

    public void clear() {
        this.userPropertyManager.clear(this.getCurrentUser());
    }

    private UserKey getCurrentUser() {
        UserProfile remoteUser = this.userManager.getRemoteUser();
        if (remoteUser == null) {
            throw new IllegalStateException("Cannot get current user");
        }
        return remoteUser.getUserKey();
    }
}

