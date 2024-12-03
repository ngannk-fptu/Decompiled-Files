/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.importexport.ImportExportManager
 *  com.atlassian.confluence.pages.ContentTree
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.security.GateKeeper
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.impl.rpc;

import com.atlassian.confluence.extra.flyingpdf.analytic.SpaceExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.impl.DelegatingPdfExporterService;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class PdfExportRpcDelegatorImpl {
    private final GateKeeper gateKeeper;
    private final SpaceManager spaceManager;
    private final SettingsManager settingsManager;
    private final PermissionManager permissionManager;
    private final ImportExportManager importExportManager;
    private final TransactionTemplate transactionTemplate;
    private final DelegatingPdfExporterService pdfExporterService;

    public PdfExportRpcDelegatorImpl(@ComponentImport GateKeeper gateKeeper, @ComponentImport SpaceManager spaceManager, @ComponentImport SettingsManager settingsManager, @ComponentImport PermissionManager permissionManager, @ComponentImport ImportExportManager importExportManager, @ComponentImport TransactionTemplate transactionTemplate, DelegatingPdfExporterService pdfExporterService) {
        this.gateKeeper = gateKeeper;
        this.spaceManager = spaceManager;
        this.settingsManager = settingsManager;
        this.permissionManager = permissionManager;
        this.importExportManager = importExportManager;
        this.transactionTemplate = transactionTemplate;
        this.pdfExporterService = pdfExporterService;
    }

    public String exportSpace(String spaceKey) throws RemoteException {
        Object obj = this.transactionTemplate.execute(() -> {
            String downloadPath;
            Space space = this.spaceManager.getSpace(spaceKey);
            if (space == null) {
                return new RemoteException("Invalid spaceKey: [" + spaceKey + "]");
            }
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            if (!this.hasPermission((User)user, space)) {
                return new NotPermittedException("You don't have permission to export the space: " + space.getKey());
            }
            ContentTree contentTree = this.importExportManager.getContentTree((User)user, space);
            try {
                String contextPath = ServletContextThreadLocal.getRequest().getContextPath();
                File pdfFile = this.pdfExporterService.createPdfForSpace((User)user, space, contentTree, contextPath, new SpaceExportMetrics());
                downloadPath = this.importExportManager.prepareDownloadPath(pdfFile.getAbsolutePath());
                Predicate<User> permissionPredicate = u -> this.permissionManager.hasPermission(u, Permission.EXPORT, (Object)space);
                this.gateKeeper.addKey(downloadPath, (User)user, permissionPredicate);
            }
            catch (ImportExportException | IOException e) {
                return new RemoteException(e.getMessage());
            }
            return downloadPath;
        });
        if (obj instanceof RemoteException) {
            throw (RemoteException)obj;
        }
        String downloadPath = (String)obj;
        return this.settingsManager.getGlobalSettings().getBaseUrl() + downloadPath;
    }

    private boolean hasPermission(User user, Space space) {
        return this.permissionManager.hasPermission(user, Permission.EXPORT, (Object)space);
    }
}

