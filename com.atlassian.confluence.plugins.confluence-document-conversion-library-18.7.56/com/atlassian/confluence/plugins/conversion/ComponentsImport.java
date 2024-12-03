/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.capabilities.api.CapabilityService
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugins.conversion;

import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.capabilities.api.CapabilityService;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class ComponentsImport {
    CapabilityService capabilityService;
    PermissionManager permissionManager;
    AttachmentManager attachmentManager;
    HostApplication hostApplication;
    ClusterLockService clusterLockService;
    TransactionTemplate transactionTemplate;
    PageManager pageManager;

    public ComponentsImport(@ComponentImport CapabilityService capabilityService, @ComponentImport PermissionManager permissionManager, @ComponentImport AttachmentManager attachmentManager, @ComponentImport HostApplication hostApplication, @ComponentImport ClusterLockService clusterLockService, @ComponentImport TransactionTemplate transactionTemplate, @ComponentImport PageManager pageManager) {
        this.capabilityService = capabilityService;
        this.permissionManager = permissionManager;
        this.attachmentManager = attachmentManager;
        this.hostApplication = hostApplication;
        this.clusterLockService = clusterLockService;
        this.transactionTemplate = transactionTemplate;
        this.pageManager = pageManager;
    }
}

