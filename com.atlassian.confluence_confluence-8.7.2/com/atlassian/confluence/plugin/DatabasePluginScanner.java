/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.loaders.classloading.DeploymentUnit
 *  com.atlassian.plugin.loaders.classloading.Scanner
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.io.ByteStreams
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.impl.tenant.ThreadLocalTenantGate;
import com.atlassian.confluence.plugin.persistence.AbstractPluginData;
import com.atlassian.confluence.plugin.persistence.PluginData;
import com.atlassian.confluence.plugin.persistence.PluginDataDao;
import com.atlassian.confluence.plugin.persistence.PluginDataWithoutBinary;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.confluence.tenant.TenantRegistry;
import com.atlassian.core.util.FileUtils;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.loaders.classloading.DeploymentUnit;
import com.atlassian.plugin.loaders.classloading.Scanner;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabasePluginScanner
implements Scanner {
    private static final Logger log = LoggerFactory.getLogger(DatabasePluginScanner.class);
    private final File workDir;
    private final Map<DeploymentUnit, Long> units = new HashMap<DeploymentUnit, Long>();
    private final Map<Long, Date> loadedPluginData = new HashMap<Long, Date>();
    private final PluginDataDao pluginDataDao;
    private final TransactionalHostContextAccessor hostContextAccessor;
    private final Supplier<Boolean> isDatabaseConfigured;

    public DatabasePluginScanner(PluginDataDao pluginDataDao, File workDir, TransactionalHostContextAccessor hostContextAccessor) {
        this(pluginDataDao, workDir, hostContextAccessor, DatabasePluginScanner::isDatabaseConfigured);
    }

    @Deprecated(forRemoval=true)
    public DatabasePluginScanner(PluginDataDao pluginDataDao, File workDir, TenantRegistry ignored, TransactionalHostContextAccessor hostContextAccessor) {
        this(pluginDataDao, workDir, hostContextAccessor, DatabasePluginScanner::isDatabaseConfigured);
    }

    @VisibleForTesting
    protected DatabasePluginScanner(PluginDataDao pluginDataDao, File workDir, TransactionalHostContextAccessor hostContextAccessor, Supplier<Boolean> isDatabaseConfigured) {
        this.pluginDataDao = pluginDataDao;
        this.hostContextAccessor = hostContextAccessor;
        this.workDir = this.initWorkDir(workDir);
        this.isDatabaseConfigured = isDatabaseConfigured;
    }

    public synchronized Collection<DeploymentUnit> scan() {
        if (!this.isDatabaseConfigured.get().booleanValue()) {
            return Collections.emptySet();
        }
        return this.loadNewPluginsFromDatabase();
    }

    public synchronized Collection<DeploymentUnit> getDeploymentUnits() {
        return Collections.unmodifiableCollection(this.units.keySet());
    }

    public synchronized void reset() {
        this.units.clear();
        this.loadedPluginData.clear();
        if (FileUtils.deleteDir((File)this.workDir)) {
            this.workDir.mkdirs();
        }
    }

    public synchronized void remove(DeploymentUnit deploymentUnit) throws PluginException {
        Long pluginDataId = this.units.get(deploymentUnit);
        if (pluginDataId == null) {
            throw new PluginException("Could not remove " + deploymentUnit + " because it was not loaded by this scanner");
        }
        this.units.remove(deploymentUnit);
        if (!this.units.containsValue(pluginDataId)) {
            this.loadedPluginData.remove(pluginDataId);
        }
        deploymentUnit.getPath().delete();
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private File writePluginData(PluginData pluginData) {
        File pluginFile = new File(this.workDir, pluginData.getLastModificationDate().getTime() + pluginData.getFileName());
        try (InputStream data = pluginData.getData();){
            File file;
            try (BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(pluginFile));){
                ByteStreams.copy((InputStream)data, (OutputStream)fileOutputStream);
                file = pluginFile;
            }
            return file;
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot write plugin data to the filesystem: " + pluginFile, e);
        }
    }

    private Collection<DeploymentUnit> loadNewPluginsFromDatabase() {
        try {
            return ThreadLocalTenantGate.withTenantPermit(() -> (Set)this.hostContextAccessor.doInTransaction(TransactionalHostContextAccessor.Permission.READ_ONLY, () -> {
                HashMap<DeploymentUnit, Long> loadedUnits = new HashMap<DeploymentUnit, Long>();
                Iterator<PluginDataWithoutBinary> pluginIterator = this.pluginDataDao.getAllPluginDataWithoutBinary();
                while (pluginIterator.hasNext()) {
                    PluginDataWithoutBinary pluginDataWithoutBinary = pluginIterator.next();
                    if (this.isAlreadyLoaded(pluginDataWithoutBinary)) continue;
                    PluginData pluginDataWithBinary = this.pluginDataDao.getPluginData(pluginDataWithoutBinary.getKey());
                    File localPluginFile = this.writePluginData(pluginDataWithBinary);
                    if (!localPluginFile.setLastModified(pluginDataWithoutBinary.getLastModificationDate().getTime())) {
                        log.warn("Failed to set last modified time on {}", (Object)localPluginFile.getAbsolutePath());
                    }
                    this.loadedPluginData.put(pluginDataWithoutBinary.getId(), pluginDataWithoutBinary.getLastModificationDate());
                    loadedUnits.put(new DeploymentUnit(localPluginFile), pluginDataWithoutBinary.getId());
                }
                this.units.putAll(loadedUnits);
                return loadedUnits.keySet();
            })).call();
        }
        catch (Exception e) {
            log.warn("Exception when loading plugins from the database", (Throwable)e);
            return Collections.emptySet();
        }
    }

    private boolean isAlreadyLoaded(AbstractPluginData pluginData) {
        return this.loadedPluginData.containsKey(pluginData.getId()) && this.isLessThanASecondDifferent(this.loadedPluginData.get(pluginData.getId()), pluginData.getLastModificationDate());
    }

    private boolean isLessThanASecondDifferent(Date date1, Date date2) {
        return Math.abs(date1.getTime() - date2.getTime()) < 1000L;
    }

    private File initWorkDir(File baseWorkDir) {
        File workDir = baseWorkDir;
        boolean success = true;
        if (workDir.exists()) {
            success = FileUtils.deleteDir((File)workDir);
        }
        if (success) {
            success = workDir.mkdirs();
        }
        if (!success) {
            workDir = new File(baseWorkDir.getParent(), "plugin-cache-" + System.currentTimeMillis());
            log.error("Couldn't remove plugin working directory: " + baseWorkDir.getAbsolutePath() + " New plugin directory is: " + workDir.getAbsolutePath());
            if (!workDir.mkdirs()) {
                throw new RuntimeException("Couldn't create the plugin working directory: " + workDir.getAbsolutePath());
            }
        }
        return workDir;
    }

    private static boolean isDatabaseConfigured() {
        AtlassianBootstrapManager bootstrapManager = BootstrapUtils.getBootstrapManager();
        return bootstrapManager != null && bootstrapManager.getHibernateConfig() != null && bootstrapManager.getHibernateConfig().isHibernateSetup();
    }
}

