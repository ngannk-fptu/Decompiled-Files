/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.crowd.directory.InternalDirectory
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation
 *  com.atlassian.crowd.embedded.impl.ImmutableDirectory
 *  com.atlassian.crowd.embedded.impl.ImmutableDirectory$Builder
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionCallbackWithoutResult
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.setup.actions.AbstractSetupAction;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.crowd.directory.InternalDirectory;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import com.atlassian.crowd.embedded.impl.ImmutableDirectory;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@WebSudoRequired
@SystemAdminOnly
public class ConnectToJiraSyncDirectoryAction
extends AbstractSetupAction {
    private static final Logger log = LoggerFactory.getLogger(ConnectToJiraSyncDirectoryAction.class);
    private CrowdDirectoryService crowdDirectoryService;
    private DirectoryManager crowdDirectoryManager;
    private LongRunningTaskManager longRunningTaskManager;
    private LongRunningTaskId longRunningTaskId;
    private LongRunningTask longRunningTask;

    @Override
    public String doDefault() throws Exception {
        this.startLongRunningJiraDirectorySyncJob();
        return "input";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        this.getSetupPersister().progessSetupStep();
        if ("install".equals(this.getSetupPersister().getSetupType())) {
            return "quick-setup";
        }
        if ("custom".equals(this.getSetupPersister().getSetupType())) {
            return "custom-setup";
        }
        return "success";
    }

    private long getInternalDirectoryId() {
        List directories = this.crowdDirectoryManager.findAllDirectories();
        for (Directory directory : directories) {
            if (!directory.getImplementationClass().equals(InternalDirectory.class.getName())) continue;
            return directory.getId();
        }
        throw new IllegalStateException("No internal directory found. Should have already been created in the previous step.");
    }

    private long getRemoteJiraDirectoryId() {
        List directories = this.crowdDirectoryManager.findAllDirectories();
        for (Directory directory : directories) {
            if (directory.getImplementationClass().equals(InternalDirectory.class.getName())) continue;
            return directory.getId();
        }
        throw new IllegalStateException("No remote Jira directory found. Should have already been created in the previous step.");
    }

    private String getFirstUsername(long directoryId) {
        try {
            List usernames = this.crowdDirectoryManager.searchUsers(directoryId, QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).returningAtMost(1));
            if (!usernames.isEmpty()) {
                return (String)usernames.get(0);
            }
        }
        catch (DirectoryNotFoundException | OperationFailedException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private void startLongRunningJiraDirectorySyncJob() throws Exception {
        long internalDirectoryId = this.getInternalDirectoryId();
        long remoteDirectoryId = this.getRemoteJiraDirectoryId();
        String adminUsername = this.getFirstUsername(internalDirectoryId);
        if (adminUsername == null) {
            throw new IllegalStateException("No admin username found");
        }
        this.longRunningTask = new SyncJiraDirectoryLongRunningTask(this.crowdDirectoryService, this.crowdDirectoryManager, adminUsername, remoteDirectoryId, internalDirectoryId, this.getI18n());
        this.longRunningTaskId = this.longRunningTaskManager.startLongRunningTask(this.getAuthenticatedUser(), this.longRunningTask);
    }

    public void setCrowdDirectoryService(CrowdDirectoryService crowdDirectoryService) {
        this.crowdDirectoryService = crowdDirectoryService;
    }

    public void setLongRunningTaskManager(LongRunningTaskManager longRunningTaskManager) {
        this.longRunningTaskManager = longRunningTaskManager;
    }

    public void setCrowdDirectoryManager(DirectoryManager crowdDirectoryManager) {
        this.crowdDirectoryManager = crowdDirectoryManager;
    }

    public LongRunningTask getTask() {
        return this.longRunningTask;
    }

    public String getTaskId() {
        return this.longRunningTaskId.toString();
    }

    private static class SyncJiraDirectoryLongRunningTask
    extends ConfluenceAbstractLongRunningTask {
        private static final Logger LOG = LoggerFactory.getLogger(SyncJiraDirectoryLongRunningTask.class);
        private final CrowdDirectoryService crowdDirectoryService;
        private final DirectoryManager directoryManager;
        private final String adminUsername;
        private final long remoteDirectoryId;
        private final long internalDirectoryId;
        private final I18NBean i18NBean;

        public SyncJiraDirectoryLongRunningTask(CrowdDirectoryService crowdDirectoryService, DirectoryManager directoryManager, String adminUsername, long remoteDirectoryId, long internalDirectoryId, I18NBean i18NBean) {
            this.crowdDirectoryService = crowdDirectoryService;
            this.directoryManager = directoryManager;
            this.adminUsername = adminUsername;
            this.remoteDirectoryId = remoteDirectoryId;
            this.internalDirectoryId = internalDirectoryId;
            this.i18NBean = i18NBean;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected void runInternal() {
            Class<SyncJiraDirectoryLongRunningTask> clazz = SyncJiraDirectoryLongRunningTask.class;
            synchronized (SyncJiraDirectoryLongRunningTask.class) {
                this.progress.setPercentage(0);
                TransactionTemplate tt = new TransactionTemplate();
                tt.setTransactionManager((PlatformTransactionManager)ContainerManager.getInstance().getContainerContext().getComponent((Object)"transactionManager"));
                tt.execute((TransactionCallback)new TransactionCallbackWithoutResult(){

                    protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                        this.enableRemoteDirectory(remoteDirectoryId);
                    }
                });
                try {
                    this.progress.setPercentage(10);
                    this.crowdDirectoryService.synchroniseDirectory(this.remoteDirectoryId, false);
                    this.progress.setPercentage(80);
                }
                catch (Exception e) {
                    this.progress.setCompletedSuccessfully(false);
                    LOG.error("Synchronization failed.", (Throwable)e);
                    throw new RuntimeException(e);
                }
                try {
                    User remoteAdminUser = this.directoryManager.findUserByName(this.remoteDirectoryId, this.adminUsername);
                    UserTemplate internalAdminUser = new UserTemplate(remoteAdminUser);
                    internalAdminUser.setDirectoryId(this.internalDirectoryId);
                    internalAdminUser.setExternalId(this.directoryManager.findUserByName(this.internalDirectoryId, this.adminUsername).getExternalId());
                    this.directoryManager.updateUser(this.internalDirectoryId, internalAdminUser);
                    this.progress.setPercentage(100);
                }
                catch (DirectoryNotFoundException e) {
                    this.progress.setCompletedSuccessfully(false);
                    LOG.error(e.getMessage(), (Throwable)e);
                    throw new IllegalStateException("Both the internal directory and remote Jira directory must exist", e);
                }
                catch (UserNotFoundException e) {
                    this.progress.setCompletedSuccessfully(false);
                    LOG.error(e.getMessage(), (Throwable)e);
                    throw new IllegalStateException("The admin user was just created in the previous step and so must exist", e);
                }
                catch (Exception e) {
                    this.progress.setCompletedSuccessfully(false);
                    LOG.error(e.getMessage(), (Throwable)e);
                    throw new RuntimeException(e);
                }
                return;
            }
        }

        private void enableRemoteDirectory(long remoteDirectoryId) {
            Directory directory = this.crowdDirectoryService.findDirectoryById(remoteDirectoryId);
            ImmutableDirectory.Builder directoryBuilder = ImmutableDirectory.newBuilder((Directory)directory);
            directoryBuilder.setActive(true);
            this.crowdDirectoryService.updateDirectory(directoryBuilder.toDirectory());
        }

        public String getName() {
            return "Synchronising Jira Directory";
        }

        public String getCurrentStatus() {
            DirectorySynchronisationRoundInformation syncRound;
            DirectorySynchronisationInformation syncInfo = this.crowdDirectoryService.getDirectorySynchronisationInformation(this.remoteDirectoryId);
            DirectorySynchronisationRoundInformation directorySynchronisationRoundInformation = syncRound = syncInfo.isSynchronising() ? syncInfo.getActiveRound() : syncInfo.getLastRound();
            if (syncRound == null) {
                return null;
            }
            String statusKey = syncRound.getStatusKey();
            if (statusKey == null) {
                return null;
            }
            Function encoder = param -> {
                if (param instanceof String) {
                    return HtmlUtil.htmlEncode((String)((Object)param));
                }
                return param;
            };
            return this.i18NBean.getText("embedded.crowd." + statusKey, Lists.transform((List)syncRound.getStatusParameters(), (Function)encoder));
        }
    }
}

