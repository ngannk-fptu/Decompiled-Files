/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 *  com.atlassian.plugins.rest.common.security.AuthorisationException
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.sun.jersey.api.model.AbstractResourceMethod
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.rest.interceptor;

import com.atlassian.confluence.plugins.hipchat.emoticons.rest.interceptor.CustomEmojisUploadPermission;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.AdminConfigurationService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import com.atlassian.plugins.rest.common.security.AuthorisationException;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.sun.jersey.api.model.AbstractResourceMethod;
import java.lang.reflect.InvocationTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomEmojisPermissionInterceptor
implements ResourceInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(CustomEmojisPermissionInterceptor.class);
    private final AdminConfigurationService adminConfigurationService;
    private final UserManager userManager;

    public CustomEmojisPermissionInterceptor(AdminConfigurationService adminConfigurationService, @ComponentImport UserManager userManager) {
        this.adminConfigurationService = adminConfigurationService;
        this.userManager = userManager;
    }

    public void intercept(MethodInvocation methodInvocation) throws IllegalAccessException, InvocationTargetException {
        boolean currentUserIsAdminOrSystemAdmin;
        if (!this.shouldCheckPermission(methodInvocation.getMethod())) {
            logger.debug("Bypass custom emojis upload permission checks because CustomEmojisUploadPermission annotation is not present.");
            methodInvocation.invoke();
            return;
        }
        if (this.adminConfigurationService.isAllowUserUploadCustomEmojis()) {
            logger.debug("All users are allowed to upload custom emojis.");
            methodInvocation.invoke();
            return;
        }
        logger.debug("Only admins are able to upload custom emojis.");
        UserKey currentLoginUser = this.userManager.getRemoteUserKey();
        boolean bl = currentUserIsAdminOrSystemAdmin = this.userManager.isSystemAdmin(currentLoginUser) || this.userManager.isAdmin(currentLoginUser);
        if (!currentUserIsAdminOrSystemAdmin) {
            throw new AuthorisationException();
        }
        methodInvocation.invoke();
    }

    private boolean shouldCheckPermission(AbstractResourceMethod method) {
        CustomEmojisUploadPermission annotation = (CustomEmojisUploadPermission)method.getAnnotation(CustomEmojisUploadPermission.class);
        return annotation != null;
    }
}

