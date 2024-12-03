/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionCallbackWithoutResult
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.importexport.actions;

import com.atlassian.confluence.importexport.DefaultExportContext;
import com.atlassian.confluence.importexport.ExportContext;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.util.Set;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Deprecated
public class ExportSpaceLongRunningTask
extends ConfluenceAbstractLongRunningTask {
    private static final Logger log = LoggerFactory.getLogger(ExportSpaceLongRunningTask.class);
    private final ImportExportManager importExportManager;
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final ConfluenceUser remoteUser;
    private final GateKeeper gateKeeper;
    private final String contextPath;
    private final ExportContext exportContext;
    private final Set<Long> contentToBeExported;
    private final Set<Long> contentToBeExcluded;
    private final String type;
    private final String contentOption;
    private final String spaceKey;
    private String downloadPath;

    public ExportSpaceLongRunningTask(ConfluenceUser remoteUser, String contextPath, ExportContext exportContext, Set<Long> contentToBeExported, Set<Long> contentToBeExcluded, GateKeeper gateKeeper, ImportExportManager importExportManager, PermissionManager permissionManager, SpaceManager spaceManager, String spaceKey, String type, String contentOption) {
        this.remoteUser = remoteUser;
        this.contextPath = contextPath;
        this.exportContext = exportContext;
        this.gateKeeper = gateKeeper;
        this.importExportManager = importExportManager;
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.type = type;
        this.contentOption = contentOption;
        this.spaceKey = spaceKey;
        this.contentToBeExported = contentToBeExported;
        this.contentToBeExcluded = contentToBeExcluded;
    }

    @Override
    protected void runInternal() {
        TransactionTemplate tt = new TransactionTemplate();
        tt.setTransactionManager((PlatformTransactionManager)ContainerManager.getInstance().getContainerContext().getComponent((Object)"transactionManager"));
        tt.execute((TransactionCallback)new TransactionCallbackWithoutResult(){

            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                AuthenticatedUserThreadLocal.set(ExportSpaceLongRunningTask.this.remoteUser);
                RequestCacheThreadLocal.getRequestCache().put("confluence.context.path", ExportSpaceLongRunningTask.this.contextPath);
                try {
                    ContentTree contentTree;
                    DefaultExportContext defaultExportContext = (DefaultExportContext)ExportSpaceLongRunningTask.this.exportContext;
                    defaultExportContext.addWorkingEntity(ExportSpaceLongRunningTask.this.spaceManager.getSpace(ExportSpaceLongRunningTask.this.spaceKey));
                    if ("TYPE_XML".equals(ExportSpaceLongRunningTask.this.type) && "all".equals(ExportSpaceLongRunningTask.this.contentOption) && ExportSpaceLongRunningTask.this.isSpaceAdminOrConfAdmin()) {
                        defaultExportContext.setExportAll(true);
                    } else if ("visibleOnly".equals(ExportSpaceLongRunningTask.this.contentOption)) {
                        contentTree = ExportSpaceLongRunningTask.this.getContentTree();
                        defaultExportContext.setContentTree(contentTree);
                    } else if (!ExportSpaceLongRunningTask.this.contentToBeExported.isEmpty()) {
                        contentTree = ExportSpaceLongRunningTask.this.getContentTree();
                        contentTree.filter(ExportSpaceLongRunningTask.this.contentToBeExported, ExportSpaceLongRunningTask.this.contentToBeExcluded);
                        defaultExportContext.setContentTree(contentTree);
                    } else {
                        defaultExportContext.setContentTree(new ContentTree());
                    }
                    String archivePath = ExportSpaceLongRunningTask.this.importExportManager.exportAs(ExportSpaceLongRunningTask.this.exportContext, ExportSpaceLongRunningTask.this.progress);
                    ExportSpaceLongRunningTask.this.downloadPath = ExportSpaceLongRunningTask.this.importExportManager.prepareDownloadPath(archivePath);
                    Predicate<User> permissionPredicate = u -> ExportSpaceLongRunningTask.this.permissionManager.hasPermission((User)u, Permission.EXPORT, ExportSpaceLongRunningTask.this.spaceManager.getSpace(ExportSpaceLongRunningTask.this.spaceKey));
                    ExportSpaceLongRunningTask.this.gateKeeper.addKey(ExportSpaceLongRunningTask.this.downloadPath, ExportSpaceLongRunningTask.this.remoteUser, permissionPredicate);
                    ExportSpaceLongRunningTask.this.progress.setStatus("Export complete. Download <a class=\"space-export-download-path\" href=\"" + ExportSpaceLongRunningTask.this.contextPath + ExportSpaceLongRunningTask.this.downloadPath + "\">here</a>.");
                    ExportSpaceLongRunningTask.this.progress.setPercentage(100);
                    ExportSpaceLongRunningTask.this.progress.setCompletedSuccessfully(true);
                }
                catch (Exception e) {
                    log.error("Error during export", (Throwable)e);
                    ExportSpaceLongRunningTask.this.progress.setStatus("There was an error in the export. Please check your log files. :" + e.getMessage());
                    ExportSpaceLongRunningTask.this.progress.setCompletedSuccessfully(false);
                }
            }
        });
        log.info("Export of space {} complete", (Object)this.spaceKey);
    }

    private ContentTree getContentTree() {
        if ("TYPE_XML".equals(this.type)) {
            return this.importExportManager.getPageBlogTree(this.remoteUser, this.spaceManager.getSpace(this.spaceKey));
        }
        return this.importExportManager.getContentTree(this.remoteUser, this.spaceManager.getSpace(this.spaceKey));
    }

    private boolean isSpaceAdminOrConfAdmin() {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, this.spaceManager.getSpace(this.spaceKey)) || this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public String getDownloadPath() {
        return this.downloadPath;
    }

    public String getName() {
        return "Export Space";
    }

    public String getNameKey() {
        return "export.space.task.name";
    }
}

