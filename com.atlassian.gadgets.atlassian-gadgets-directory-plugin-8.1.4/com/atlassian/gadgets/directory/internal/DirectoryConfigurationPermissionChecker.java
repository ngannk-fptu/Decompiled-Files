/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.dashboard.PermissionException
 *  com.atlassian.gadgets.directory.spi.DirectoryPermissionService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.directory.internal;

import com.atlassian.gadgets.dashboard.PermissionException;
import com.atlassian.gadgets.directory.spi.DirectoryPermissionService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DirectoryConfigurationPermissionChecker {
    private final DirectoryPermissionService permissionService;
    private final UserManager userManager;

    @Autowired
    public DirectoryConfigurationPermissionChecker(@ComponentImport DirectoryPermissionService permissionService, @ComponentImport UserManager userManager) {
        this.permissionService = permissionService;
        this.userManager = userManager;
    }

    public void checkForPermissionToConfigureDirectory(HttpServletRequest request) throws PermissionException {
        if (!this.permissionService.canConfigureDirectory(this.userManager.getRemoteUsername(request))) {
            throw new PermissionException();
        }
    }
}

