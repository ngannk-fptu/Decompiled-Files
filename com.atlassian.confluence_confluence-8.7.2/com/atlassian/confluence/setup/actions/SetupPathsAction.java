/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.plugin.SplitStartupPluginSystemLifecycle
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.struts2.ServletActionContext
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
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.setup.actions.AbstractSetupAction;
import com.atlassian.confluence.setup.actions.ConfluenceSetupPersister;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.tenant.SystemTenant;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.plugin.SplitStartupPluginSystemLifecycle;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@WebSudoRequired
@SystemAdminOnly
public class SetupPathsAction
extends AbstractSetupAction {
    private static final Logger log = LoggerFactory.getLogger(SetupPathsAction.class);
    private SystemTenant systemTenant;
    private SplitStartupPluginSystemLifecycle pluginSystemLifecycle;
    private ClusterConfigurationHelperInternal clusterConfigurationHelper;
    private PlatformTransactionManager transactionManager;
    private FilesystemPath confluenceHome;
    private ApplicationProperties applicationProperties;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        ClusterConfigurationHelperInternal clusterConfigurationHelper = this.getClusterConfigurationHelper();
        clusterConfigurationHelper.createSharedHome();
        Settings settings = this.settingsManager.getGlobalSettings();
        File confHome = this.getConfluenceHome();
        String confluenceHomeVelocityExpression = "${confluenceHome}" + System.getProperty("file.separator");
        this.setupAttachmentPaths();
        this.setupDirectory(confluenceHomeVelocityExpression + "backups", "creating.daily.backup.dir.failed");
        settings.setBackupPath(new File(confHome, "backups").getAbsolutePath());
        if (this.hasErrors()) {
            return "error";
        }
        try {
            settings.setAttachmentDataStore("file.system.based.attachments.storage");
            settings.setBaseUrl(GeneralUtil.lookupDomainName(ServletActionContext.getRequest()));
            this.updateGlobalSettingsInNewTransaction(settings);
            ConfluenceSetupPersister setupPersister = this.getSetupPersister();
            String setupType = setupPersister.getSetupType();
            this.performLateStartup();
            if ("install".equals(setupType)) {
                setupPersister.progessSetupStep();
                return "quick-setup";
            }
            if ("custom".equals(setupType) || "cluster".equals(setupType)) {
                setupPersister.progessSetupStep();
                return "custom-setup";
            }
            throw new RuntimeException("Unexpected setup type:" + setupType);
        }
        catch (Exception e) {
            log.error("Unable to setup paths to system", (Throwable)e);
            this.addActionError(this.getText("error.recording.default.paths.to.system"));
            return "error";
        }
    }

    private void updateGlobalSettingsInNewTransaction(final Settings settings) {
        TransactionTemplate tx = new TransactionTemplate(this.transactionManager);
        tx.setPropagationBehavior(3);
        tx.execute((TransactionCallback)new TransactionCallbackWithoutResult(){

            protected void doInTransactionWithoutResult(TransactionStatus status) {
                SetupPathsAction.this.settingsManager.updateGlobalSettings(settings);
            }
        });
    }

    private void setupAttachmentPaths() {
        String confHome = "${confluenceHome}" + System.getProperty("file.separator");
        this.bootstrapConfigurer().setProperty("attachments.dir", confHome + "attachments");
        this.setupDirectory(confHome + "attachments", "creating.attachments.dir.failed");
    }

    private File setupDirectory(String dirName, String failureMessageKey) {
        File localHome;
        if (dirName == null || dirName.trim().equals("")) {
            return null;
        }
        File home = this.getConfluenceHome();
        File dir = new File(GeneralUtil.replaceConfluenceConstants(dirName, home, localHome = ((Path)this.applicationProperties.getLocalHomeDirectory().get()).toFile()));
        if (!dir.isDirectory() && !dir.mkdirs()) {
            this.addActionError(failureMessageKey, dir);
            return null;
        }
        return dir;
    }

    private File getConfluenceHome() {
        if (this.confluenceHome != null) {
            return this.confluenceHome.asJavaFile();
        }
        return new File(this.applicationProperties.getHomeDirectory().getPath());
    }

    public void setSystemTenant(SystemTenant systemTenant) {
        this.systemTenant = systemTenant;
    }

    public void setPluginManager(SplitStartupPluginSystemLifecycle pluginSystemLifecycle) {
        this.pluginSystemLifecycle = pluginSystemLifecycle;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    private ClusterConfigurationHelperInternal getClusterConfigurationHelper() {
        if (this.clusterConfigurationHelper == null) {
            this.setClusterConfigurationHelper((ClusterConfigurationHelperInternal)BootstrapUtils.getBootstrapContext().getBean("clusterConfigurationHelper"));
        }
        return this.clusterConfigurationHelper;
    }

    void setClusterConfigurationHelper(ClusterConfigurationHelperInternal clusterConfigurationHelper) {
        this.clusterConfigurationHelper = Objects.requireNonNull(clusterConfigurationHelper);
    }

    public void setConfluenceHome(FilesystemPath confluenceHome) {
        this.confluenceHome = confluenceHome;
    }

    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }
}

