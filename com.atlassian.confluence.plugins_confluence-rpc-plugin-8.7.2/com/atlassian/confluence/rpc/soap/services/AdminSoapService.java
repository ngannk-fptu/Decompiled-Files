/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.index.IndexRecoveryService
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.NodeStatus
 *  com.atlassian.confluence.core.HeartbeatManager
 *  com.atlassian.confluence.importexport.DefaultExportContext
 *  com.atlassian.confluence.importexport.DefaultImportContext
 *  com.atlassian.confluence.importexport.ExportContext
 *  com.atlassian.confluence.importexport.ImportContext
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.importexport.ImportExportManager
 *  com.atlassian.confluence.importexport.impl.ExportDescriptor
 *  com.atlassian.confluence.importexport.impl.UnexpectedImportZipFileContents
 *  com.atlassian.confluence.plugin.descriptor.IndexRecovererModuleDescriptor
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.search.IndexManager
 *  com.atlassian.confluence.security.GateKeeper
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.service.AnonymousUserPermissionsService
 *  com.atlassian.confluence.setup.settings.DarkFeatures
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.setup.settings.init.AdminUiProperties
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.plugin.JarPluginArtifact
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.XmlPluginArtifact
 *  com.atlassian.user.User
 *  com.google.common.base.Stopwatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rpc.soap.services;

import com.atlassian.confluence.api.service.index.IndexRecoveryService;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.NodeStatus;
import com.atlassian.confluence.core.HeartbeatManager;
import com.atlassian.confluence.importexport.DefaultExportContext;
import com.atlassian.confluence.importexport.DefaultImportContext;
import com.atlassian.confluence.importexport.ExportContext;
import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.importexport.impl.UnexpectedImportZipFileContents;
import com.atlassian.confluence.plugin.descriptor.IndexRecovererModuleDescriptor;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.soap.beans.RemoteClusterInformation;
import com.atlassian.confluence.rpc.soap.beans.RemoteNodeStatus;
import com.atlassian.confluence.rpc.soap.beans.RemoteServerInfo;
import com.atlassian.confluence.rpc.soap.services.SoapServiceHelper;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.service.AnonymousUserPermissionsService;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.init.AdminUiProperties;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.plugin.JarPluginArtifact;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.XmlPluginArtifact;
import com.atlassian.user.User;
import com.google.common.base.Stopwatch;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminSoapService {
    private static final Logger log = LoggerFactory.getLogger(AdminSoapService.class);
    private IndexManager indexManager;
    private IndexRecoveryService indexRecoveryService;
    private ImportExportManager importExportManager;
    private SpaceManager spaceManager;
    private GateKeeper gateKeeper;
    private SoapServiceHelper soapServiceHelper;
    private ClusterManager clusterManager;
    private PermissionManager permissionManager;
    private GlobalSettingsManager settingsManager;
    private PluginAccessor pluginAccessor;
    private PluginController pluginController;
    private AnonymousUserPermissionsService anonymousUserPermissionsService;
    private AdminUiProperties adminUiProperties;
    private HeartbeatManager heartbeatManager;
    public static final String __PARANAMER_DATA = "exportSite boolean exportAttachments \nimportSpace byte importData \ninstallPlugin java.lang.String,byte pluginFileName,pluginData \nisDarkFeatureEnabled java.lang.String key \nisPluginEnabled java.lang.String pluginKey \nisPluginInstalled java.lang.String pluginKey \nperformBackup boolean exportAttachments \nsetAdminUiProperties com.atlassian.confluence.setup.settings.init.AdminUiProperties adminUiProperties \nsetAnonymousUserPermissionsService com.atlassian.confluence.security.service.AnonymousUserPermissionsService anonymousUserPermissionsService \nsetClusterManager com.atlassian.confluence.cluster.ClusterManager clusterManager \nsetEnableAnonymousAccess boolean value \nsetGateKeeper com.atlassian.confluence.security.GateKeeper gateKeeper \nsetHeartbeatManager com.atlassian.confluence.core.HeartbeatManager heartbeatManager \nsetImportExportManager com.atlassian.confluence.importexport.ImportExportManager importExportManager \nsetIndexManager com.atlassian.confluence.search.IndexManager indexManager \nsetIndexRecoveryService com.atlassian.confluence.api.service.index.IndexRecoveryService indexRecoveryService \nsetPermissionManager com.atlassian.confluence.security.PermissionManager permissionManager \nsetPluginAccessor com.atlassian.plugin.PluginAccessor pluginAccessor \nsetPluginController com.atlassian.plugin.PluginController pluginController \nsetSettingsManager com.atlassian.confluence.setup.settings.GlobalSettingsManager settingsManager \nsetSoapServiceHelper com.atlassian.confluence.rpc.soap.services.SoapServiceHelper soapServiceHelper \nsetSpaceManager com.atlassian.confluence.spaces.SpaceManager spaceManager \nstartActivity java.lang.String,java.lang.String key,username \nstopActivity java.lang.String,java.lang.String key,username \n";

    public String exportSite(boolean exportAttachments) throws RemoteException {
        if (this.isDownloadEnabled()) {
            try {
                ConfluenceUser user = AuthenticatedUserThreadLocal.get();
                if (!this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM)) {
                    throw new RemoteException("Authenticated user [" + user + "] is not a System Administrator.");
                }
                String siteExportZipPath = this.exportImpl(exportAttachments, (User)user);
                String downloadPath = this.importExportManager.prepareDownloadPath(siteExportZipPath);
                Predicate<User> permissionPredicate = u -> this.permissionManager.hasPermission(u, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
                this.gateKeeper.addKey(downloadPath, (User)user, permissionPredicate);
                String downloadUrl = this.settingsManager.getGlobalSettings().getBaseUrl() + downloadPath;
                log.info("Site export stored at [{}] and being made available at [{}]", (Object)siteExportZipPath, (Object)downloadUrl);
                return downloadUrl;
            }
            catch (IOException e) {
                throw new RemoteException("Error preparing download of site export: " + e.getMessage(), (Throwable)e);
            }
        }
        throw new RemoteException("Downloading of exported zip has been disabled, export aborted. Set 'admin.ui.allow.manual.backup.download' in confluence.cfg.xml to true to enable download.");
    }

    private String exportImpl(boolean exportAttachments, User user) throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        try {
            DefaultExportContext exportContext = DefaultExportContext.getXmlBackupInstance();
            exportContext.setExportAttachments(exportAttachments);
            exportContext.setExportHierarchy(false);
            exportContext.setExportComments(false);
            return this.importExportManager.exportAs((ExportContext)exportContext, new ProgressMeter());
        }
        catch (ImportExportException e) {
            throw new RemoteException("Could not export site: " + e.getMessage(), (Throwable)e);
        }
    }

    public String performBackup(boolean exportAttachments) throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.exportImpl(exportAttachments, (User)user);
    }

    private boolean isDownloadEnabled() {
        return this.adminUiProperties.isAllowed("admin.ui.allow.manual.backup.download");
    }

    public RemoteServerInfo getServerInfo() throws RemoteException {
        return new RemoteServerInfo();
    }

    public boolean flushIndexQueue() throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        log.info("Index queue flush requested");
        while (this.indexManager.isReIndexing()) {
            log.info("System is currently re-indexing, pausing before attempting index queue flush");
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RemoteException((Throwable)e);
            }
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        boolean flushSuccessful = this.indexManager.flushQueue();
        if (flushSuccessful) {
            log.info("Index queue flush completed in {} ms", (Object)stopwatch.elapsed(TimeUnit.MILLISECONDS));
        } else {
            log.warn("Failed to perform index queue flush, attempt took {} ms", (Object)stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
        return flushSuccessful;
    }

    public boolean clearIndexQueue() throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        this.indexManager.resetIndexQueue();
        return true;
    }

    public boolean recoverIndex() throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        List indexRecoverers = this.pluginAccessor.getEnabledModuleDescriptorsByClass(IndexRecovererModuleDescriptor.class);
        return indexRecoverers.stream().allMatch(r -> this.indexRecoveryService.recoverIndex(r.getJournalId(), r.getIndexDirName()));
    }

    public RemoteClusterInformation getClusterInformation() {
        return new RemoteClusterInformation(this.clusterManager.getClusterInformation());
    }

    public RemoteNodeStatus[] getClusterNodeStatuses() throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            throw new RemoteException("Unauthorized user, not permitted to perform this operation");
        }
        ArrayList<RemoteNodeStatus> remoteNodeStatuses = new ArrayList<RemoteNodeStatus>();
        Map nodeStatuses = this.clusterManager.getNodeStatuses();
        for (Map.Entry entry : nodeStatuses.entrySet()) {
            remoteNodeStatuses.add(new RemoteNodeStatus((Integer)entry.getKey(), (NodeStatus)entry.getValue()));
        }
        return remoteNodeStatuses.toArray(new RemoteNodeStatus[remoteNodeStatuses.size()]);
    }

    public boolean importSpace(byte[] importData) throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        this.soapServiceHelper.assertCanAdminister();
        try {
            File tmp = this.createTempFile("confluence-import-", ".tmp", importData);
            ExportDescriptor descriptor = ExportDescriptor.getExportDescriptor((File)tmp);
            if (!descriptor.isSpaceImport()) {
                throw new RemoteException("Invalid import type - can only import spaces");
            }
            String spaceKey = descriptor.getSpaceKey();
            if (this.spaceManager.getSpace(spaceKey) != null) {
                throw new RemoteException("Space " + spaceKey + " already exists.  Import aborted.");
            }
            DefaultImportContext defaultImportContext = new DefaultImportContext(tmp.getAbsolutePath(), descriptor, user);
            defaultImportContext.setDeleteWorkingFile(true);
            defaultImportContext.setSpaceKeyOfSpaceImport(spaceKey);
            this.importExportManager.doImport((ImportContext)defaultImportContext);
        }
        catch (ImportExportException | UnexpectedImportZipFileContents | IOException e) {
            throw new RemoteException("Could not import space", e);
        }
        return true;
    }

    public boolean isDarkFeatureEnabled(String key) {
        return DarkFeatures.isDarkFeatureEnabled((String)key);
    }

    public boolean startActivity(String key, String username) throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        this.heartbeatManager.startActivity(key, username);
        return true;
    }

    public boolean stopActivity(String key, String username) throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        this.heartbeatManager.stopActivity(key, username);
        return true;
    }

    public boolean isPluginInstalled(String pluginKey) throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        return this.pluginAccessor.getPlugin(pluginKey) != null;
    }

    public boolean isPluginEnabled(String pluginKey) throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        return this.pluginAccessor.isPluginEnabled(pluginKey);
    }

    public boolean installPlugin(String pluginFileName, byte[] pluginData) throws RemoteException {
        File pluginFile;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM)) {
            throw new RemoteException("Authenticated user [" + user + "] is not a System Administrator.");
        }
        this.soapServiceHelper.assertHasValidWebSudoSession();
        int index = pluginFileName.lastIndexOf(".");
        if (index == -1) {
            throw new RemoteException("Cannot install plugin with bad filename. It must be a jar or xml file with the correct file extension.");
        }
        try {
            pluginFile = this.createTempFile(pluginFileName.substring(0, index), pluginFileName.substring(index), pluginData);
        }
        catch (IOException e) {
            throw new RuntimeException("Could not install plugin", e);
        }
        JarPluginArtifact artifact = null;
        if (pluginFileName.endsWith(".jar")) {
            artifact = new JarPluginArtifact(pluginFile);
        } else if (pluginFileName.endsWith(".xml")) {
            artifact = new XmlPluginArtifact(pluginFile);
        }
        if (artifact != null) {
            this.pluginController.installPlugins(new PluginArtifact[]{artifact});
            return true;
        }
        return false;
    }

    private File createTempFile(String suffix, String prefix, byte[] data) throws IOException {
        File tmp = File.createTempFile(suffix, prefix);
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(tmp);
            fout.write(data);
            fout.flush();
            fout.close();
        }
        catch (IOException ex) {
            log.error("Unable to create temporary file", (Throwable)ex);
            throw ex;
        }
        finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            }
            catch (IOException iOException) {}
        }
        return tmp;
    }

    public boolean setEnableAnonymousAccess(boolean value) throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        this.anonymousUserPermissionsService.setUsePermission(value);
        return true;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    @Deprecated
    public void setImportExportManager(ImportExportManager importExportManager) {
        this.importExportManager = importExportManager;
    }

    public void setGateKeeper(GateKeeper gateKeeper) {
        this.gateKeeper = gateKeeper;
    }

    public void setHeartbeatManager(HeartbeatManager heartbeatManager) {
        this.heartbeatManager = heartbeatManager;
    }

    public void setSoapServiceHelper(SoapServiceHelper soapServiceHelper) {
        this.soapServiceHelper = soapServiceHelper;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public void setClusterManager(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setIndexRecoveryService(IndexRecoveryService indexRecoveryService) {
        this.indexRecoveryService = indexRecoveryService;
    }

    public void setSettingsManager(GlobalSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public void setPluginController(PluginController pluginController) {
        this.pluginController = pluginController;
    }

    public void setAnonymousUserPermissionsService(AnonymousUserPermissionsService anonymousUserPermissionsService) {
        this.anonymousUserPermissionsService = anonymousUserPermissionsService;
    }

    public void setAdminUiProperties(AdminUiProperties adminUiProperties) {
        this.adminUiProperties = adminUiProperties;
    }
}

