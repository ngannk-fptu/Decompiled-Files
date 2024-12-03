/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.pages.actions.beans.BootstrapAware;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.setup.SetupContext;
import com.atlassian.confluence.setup.actions.AbstractSetupAction;
import com.atlassian.confluence.setup.actions.ConfluenceSetupPersister;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.db.DatabaseUtils;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import com.google.common.base.Preconditions;
import java.io.File;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class SetupStartAction
extends AbstractSetupAction
implements BootstrapAware {
    private static final Logger log = LoggerFactory.getLogger(SetupStartAction.class);
    public static final String CLUSTER_UPGRADE = "standalone-to-cluster";
    public static final String CLUSTER_DOWNGRADE = "cluster-to-standalone";
    private static final String EVAL_LICENSE = "evallicense";
    private String setupType;
    private ConfluenceSidManager bootstrapSidManager;
    private ApplicationProperties applicationProperties;
    @Deprecated
    private ClusterManager clusterManager;
    private FilesystemPath confluenceHome;
    private ApplicationProperties setupApplicationProperties;

    @Override
    public String doDefault() throws Exception {
        if (this.isAWSSetup()) {
            return "skipToNextStep";
        }
        if (this.isStandaloneToCluster() && !this.isDatabaseExternal()) {
            this.getSetupPersister().setMigrationCancelled();
            this.getMessageHolder().addActionWarning("setup.start.cluster.upgrade.cannot.start.embedded.db", this.embeddedDatabaseName());
        } else if (this.isStandaloneToCluster()) {
            this.bootstrapConfigurer().setBuildNumber(GeneralUtil.getBuildNumber());
            this.bootstrapConfigurer().save();
            ConfluenceSetupPersister setupPersister = this.getSetupPersister();
            setupPersister.setSetupType("standalone.to.cluster");
            setupPersister.progessSetupStep();
            return "skipToNextStepCluster";
        }
        if (!this.isBuildNumberCorrect()) {
            this.getSetupPersister().setMigrationCancelled();
            this.getMessageHolder().addActionWarning("setup.start.cluster.upgrade.cannot.start.version", "Confluence");
        }
        return "input";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        this.bootstrapConfigurer().setBuildNumber(GeneralUtil.getBuildNumber());
        this.bootstrapConfigurer().save();
        if (!this.setupConfluenceHome()) {
            return "error";
        }
        ConfluenceSetupPersister setupPersister = this.getSetupPersister();
        setupPersister.setSetupType(this.setupType);
        if (setupPersister.getCurrentStep().equals("setupstart")) {
            setupPersister.progessSetupStep();
        }
        if (this.setupType.equals("custom") || this.setupType.equals("cluster")) {
            return "custom-setup";
        }
        if (this.setupType.equals("install")) {
            return EVAL_LICENSE;
        }
        if (this.setupType.equals("standalone.to.cluster")) {
            return CLUSTER_UPGRADE;
        }
        if (this.setupType.equals("cluster.to.standalone")) {
            this.getSetupPersister().removeClusterSetupEntries();
            this.performEarlyStartup();
            this.bootstrapConfigurer().setProperty("hibernate.setup", "true");
            this.performLateStartup();
            return CLUSTER_DOWNGRADE;
        }
        throw new RuntimeException("unexpected setup type " + this.setupType);
    }

    private boolean setupConfluenceHome() {
        String localHomeDir = "${localHome}" + System.getProperty("file.separator");
        String confluenceLuceneIndexDir = localHomeDir + "index";
        String confluenceTempDir = localHomeDir + "temp";
        this.setupDirectory(confluenceLuceneIndexDir, "luceneIndexDir", "creating.lucene.index.dir.failed");
        this.setupDirectory(confluenceTempDir, "tempDir", "creating.temp.dir.failed");
        try {
            this.bootstrapConfigurer().setProperty("lucene.index.dir", confluenceLuceneIndexDir);
            this.bootstrapConfigurer().setProperty("struts.multipart.saveDir", confluenceTempDir);
            return true;
        }
        catch (Exception e) {
            log.error("Unable to setup Confluence home", (Throwable)e);
            this.addActionError(this.getText("error.recording.default.paths.to.system"));
            return false;
        }
    }

    private File setupDirectory(String dirName, String fieldName, String failureMessageKey) {
        File localHome;
        if (dirName == null || dirName.trim().equals("")) {
            return null;
        }
        File home = this.getConfluenceHome();
        File dir = new File(GeneralUtil.replaceConfluenceConstants(dirName, home, localHome = ((Path)this.setupApplicationProperties.getLocalHomeDirectory().get()).toFile()));
        if (!dir.isDirectory() && !dir.mkdirs()) {
            this.addFieldError(fieldName, this.getText(failureMessageKey));
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

    @Override
    public void setBootstrapSidManager(ConfluenceSidManager bootstrapSidManager) throws ConfigurationException {
        this.bootstrapSidManager = bootstrapSidManager;
    }

    @Override
    public void bootstrap() {
        Preconditions.checkState((boolean)SetupContext.isAvailable(), (Object)"Setup context is not available");
        try {
            if (!this.bootstrapSidManager.isSidSet()) {
                this.bootstrapSidManager.initSid();
            }
        }
        catch (ConfigurationException e) {
            throw new RuntimeException("Could not initialize SID", e);
        }
    }

    @Deprecated
    public ClusterManager getClusterManager() {
        if (this.clusterManager == null) {
            this.setClusterManager((ClusterManager)BootstrapUtils.getBootstrapContext().getBean("clusterManager"));
        }
        return this.clusterManager;
    }

    @Deprecated
    public void setClusterManager(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Deprecated
    public boolean isClusteredEdition() {
        return true;
    }

    public boolean isAWSSetup() {
        String currentSetupType = this.getSetupPersister().getSetupType();
        return this.isFromScratch() && !"initial".equals(currentSetupType);
    }

    public boolean isStandaloneToCluster() {
        return "standalone.to.cluster".equals(this.getSetupPersister().getSetupType());
    }

    public boolean isClusterToStandalone() {
        return "cluster.to.standalone".equals(this.getSetupPersister().getSetupType());
    }

    public boolean isFromScratch() {
        return !this.isClusterToStandalone() && !this.isStandaloneToCluster();
    }

    public boolean isDatabaseExternal() {
        return !DatabaseUtils.evaluationDatabaseName().isPresent();
    }

    public String embeddedDatabaseName() {
        return DatabaseUtils.evaluationDatabaseName().get();
    }

    public boolean isBuildNumberCorrect() {
        String configBuildNumber = this.getBootstrapStatusProvider().getApplicationConfig().getBuildNumber();
        return configBuildNumber.equals("0") || configBuildNumber.equals(BuildInformation.INSTANCE.getBuildNumber());
    }

    public void setSetupType(String setupType) {
        this.setupType = setupType;
    }

    public void setConfluenceHome(FilesystemPath confluenceHome) {
        this.confluenceHome = confluenceHome;
    }

    public void setSetupApplicationProperties(ApplicationProperties setupApplicationProperties) {
        this.setupApplicationProperties = setupApplicationProperties;
    }

    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }
}

