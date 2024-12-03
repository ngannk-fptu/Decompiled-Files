/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.permission.UserPermissionException
 *  com.atlassian.crowd.manager.permission.UserPermissionService
 *  com.atlassian.crowd.model.permission.UserPermission
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 */
package com.atlassian.crowd.plugin.rest.util;

import com.atlassian.crowd.manager.permission.UserPermissionException;
import com.atlassian.crowd.manager.permission.UserPermissionService;
import com.atlassian.crowd.model.permission.UserPermission;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import java.lang.reflect.InvocationTargetException;

public class SysAdminInterceptor
implements ResourceInterceptor {
    private final UserPermissionService userPermissionService;

    public SysAdminInterceptor(@ComponentImport UserPermissionService userPermissionService) {
        this.userPermissionService = userPermissionService;
    }

    public void intercept(MethodInvocation methodInvocation) throws IllegalAccessException, InvocationTargetException {
        if (!this.userPermissionService.currentUserHasPermission(UserPermission.SYS_ADMIN)) {
            throw new UserPermissionException("You must be an administrator to access this resource.");
        }
        methodInvocation.invoke();
    }
}

