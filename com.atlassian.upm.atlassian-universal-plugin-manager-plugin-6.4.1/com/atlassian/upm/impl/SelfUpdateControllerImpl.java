/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.factory.OsgiPlugin
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.apache.commons.io.IOUtils
 *  org.codehaus.jackson.JsonFactory
 *  org.codehaus.jackson.JsonGenerator
 *  org.joda.time.Duration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.impl;

import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.upm.SelfUpdatePluginAccessor;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.SelfUpdateController;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.lifecycle.UpmLifecycleManager;
import com.atlassian.upm.notification.NotificationCache;
import com.atlassian.upm.notification.NotificationType;
import com.atlassian.upm.osgi.Version;
import com.atlassian.upm.osgi.impl.Versions;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.schedule.UpmScheduler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Objects;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfUpdateControllerImpl
implements SelfUpdateController {
    private static final String SELF_UPDATE_DATA_KEY = "com.atlassian.upm.self-update";
    private static final Logger logger = LoggerFactory.getLogger(SelfUpdateControllerImpl.class);
    private static final String SELFUPDATE_PLUGIN_JAR_RESOURCE = "atlassian-universal-plugin-manager-selfupdate-plugin.jar";
    private static final String SELFUPDATE_PLUGIN_NAME = "Atlassian Universal Plugin Manager Self-Update Plugin";
    private static final String SELFUPDATE_PLUGIN_KEY = "com.atlassian.upm.atlassian-universal-plugin-manager-selfupdate-plugin";
    private static final String SELFUPDATE_GENERAL_ERROR = "upm.plugin.error.unexpected.error";
    private static final String SELFUPDATE_DOWNGRADE_ERROR = "upm.update.error.downgrade";
    private final AuditLogService auditLogService;
    private final UpmLifecycleManager lifecycleManager;
    private final NotificationCache notificationCache;
    private final PluginInstallationService pluginInstallationService;
    private final PluginRetriever pluginRetriever;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final SelfUpdatePluginAccessor selfUpdatePluginAccessor;
    private final UpmScheduler scheduler;
    private final UpmUriBuilder uriBuilder;
    private final UpmInformation upm;

    public SelfUpdateControllerImpl(AuditLogService auditLogService, UpmLifecycleManager lifecycleManager, NotificationCache notificationCache, PluginInstallationService pluginInstallationService, PluginRetriever pluginRetriever, PluginSettingsFactory pluginSettingsFactory, SelfUpdatePluginAccessor selfUpdatePluginAccessor, UpmScheduler scheduler, UpmUriBuilder uriBuilder, UpmInformation upm) {
        this.auditLogService = Objects.requireNonNull(auditLogService, "auditLogService");
        this.lifecycleManager = Objects.requireNonNull(lifecycleManager, "lifecycleManager");
        this.notificationCache = Objects.requireNonNull(notificationCache, "notificationCache");
        this.pluginInstallationService = Objects.requireNonNull(pluginInstallationService, "pluginAccessorAndController");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.selfUpdatePluginAccessor = Objects.requireNonNull(selfUpdatePluginAccessor, "selfUpdatePluginAccessor");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.upm = Objects.requireNonNull(upm, "upm");
    }

    @Override
    public boolean isUpmPlugin(File plugin) {
        String pluginKey = this.getBundleAttribute(plugin, "Bundle-SymbolicName").getOrElse("");
        return this.upm.getPluginKey().equals(pluginKey);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Either<String, URI> prepareSelfUpdate(File upmJar, boolean isAutoUpdate) {
        File selfUpdateJar;
        String newVersionStr = this.getBundleAttribute(upmJar, "Bundle-Version").getOrElse("").trim();
        if (newVersionStr.equals("")) {
            logger.warn("Could not get version string from jar");
            return Either.left(SELFUPDATE_GENERAL_ERROR);
        }
        Version newVersion = Versions.fromString(newVersionStr);
        if (newVersion.compareTo(this.upm.getVersion()) < 0) {
            return Either.left(SELFUPDATE_DOWNGRADE_ERROR);
        }
        InputStream fromJarStream = null;
        FileOutputStream toJarStream = null;
        try {
            selfUpdateJar = File.createTempFile("upm-selfupdate", ".jar");
            fromJarStream = this.getClass().getClassLoader().getResourceAsStream(SELFUPDATE_PLUGIN_JAR_RESOURCE);
            toJarStream = new FileOutputStream(selfUpdateJar);
            IOUtils.copy((InputStream)fromJarStream, (OutputStream)toJarStream);
            ((OutputStream)toJarStream).close();
            logger.info("Extracted self-update plugin to " + selfUpdateJar.getAbsolutePath());
        }
        catch (IOException e) {
            Either<String, URI> either;
            try {
                logger.warn("Unable to extract self-update plugin: " + e);
                either = Either.left(SELFUPDATE_GENERAL_ERROR);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(fromJarStream);
                IOUtils.closeQuietly(toJarStream);
                throw throwable;
            }
            IOUtils.closeQuietly((InputStream)fromJarStream);
            IOUtils.closeQuietly((OutputStream)toJarStream);
            return either;
        }
        IOUtils.closeQuietly((InputStream)fromJarStream);
        IOUtils.closeQuietly((OutputStream)toJarStream);
        logger.info("Installing self-update plugin");
        try {
            Plugin sup = this.pluginInstallationService.install(selfUpdateJar, SELFUPDATE_PLUGIN_NAME, Option.none(String.class), false).getPlugin();
            String pluginKey = sup.getKey();
            if (!pluginKey.equals(SELFUPDATE_PLUGIN_KEY)) {
                logger.warn("Self-update plugin had incorrect key \"" + pluginKey + "\"; not updating");
                return Either.left(SELFUPDATE_GENERAL_ERROR);
            }
            long supId = ((OsgiPlugin)sup.getPlugin()).getBundle().getBundleId();
            String upmPluginKey = this.upm.getPluginKey();
            URI pluginUriWillBe = this.uriBuilder.makeAbsolute(this.uriBuilder.buildPluginUri(upmPluginKey));
            URI selfUpdatePluginUri = this.uriBuilder.makeAbsolute(this.uriBuilder.buildPluginUri(SELFUPDATE_PLUGIN_KEY));
            URI upmPostUpdateUri = this.uriBuilder.makeAbsolute(this.uriBuilder.buildSelfUpdateCompletionUri());
            URI updateUri = this.selfUpdatePluginAccessor.prepareUpdate(upmJar, upmPluginKey, pluginUriWillBe, selfUpdatePluginUri, upmPostUpdateUri);
            this.upm.setCurrentUpmVersionAsMostRecentlyUpdated();
            SelfUpdateData data = new SelfUpdateData(this.upm.getBundleId(), isAutoUpdate, supId);
            this.getPluginSettings().put(SELF_UPDATE_DATA_KEY, (Object)data.encode());
            return Either.right(updateUri);
        }
        catch (Exception e) {
            logger.error("Unable to install self-update plugin: " + e);
            logger.debug(e.toString(), (Throwable)e);
            return Either.left(SELFUPDATE_GENERAL_ERROR);
        }
    }

    @Override
    public Either<String, File> executeInternalSelfUpdate(URI completionUri, File upmJar) {
        URI actionUri = this.selfUpdatePluginAccessor.getInternalUpdateUri(completionUri);
        try {
            StringWriter writer = new StringWriter();
            JsonGenerator json = new JsonFactory().createJsonGenerator((Writer)writer);
            json.writeStartObject();
            json.writeStringField("upmJarPath", upmJar.getPath());
            json.writeEndObject();
            json.close();
            String updateParamsJson = writer.toString();
            int status = this.postUpdate(actionUri, updateParamsJson);
            if (status == 200) {
                return Either.right(upmJar);
            }
            logger.error("Unexpected HTTP error from self-update request: " + status);
            return Either.left(SELFUPDATE_GENERAL_ERROR);
        }
        catch (Exception e) {
            logger.error("Unexpected I/O error when sending self-update request: " + e);
            logger.debug(e.toString(), (Throwable)e);
            return Either.left(SELFUPDATE_GENERAL_ERROR);
        }
    }

    int postUpdate(URI actionUri, String updateParamsJson) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)actionUri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/vnd.atl.plugins.install.self-update+json");
        conn.getOutputStream().write(updateParamsJson.getBytes());
        return conn.getResponseCode();
    }

    @Override
    public boolean isCleanupNeeded() {
        return this.getPluginSettings().get(SELF_UPDATE_DATA_KEY) != null || this.isStubPluginInstalled();
    }

    @Override
    public boolean cleanupAfterSelfUpdate() {
        Object value = this.getPluginSettings().get(SELF_UPDATE_DATA_KEY);
        if (value == null) {
            return false;
        }
        this.getPluginSettings().remove(SELF_UPDATE_DATA_KEY);
        for (SelfUpdateData data : SelfUpdateData.decode(value)) {
            if (data.getFromBundleId() == this.upm.getBundleId()) continue;
            this.lifecycleManager.ensureStarted();
            for (Plugin upmPlugin : this.pluginRetriever.getPlugin(this.upm.getPluginKey())) {
                this.auditLogService.logI18nMessage("upm.auditLog.install.plugin.success", upmPlugin.getName(), this.upm.getPluginKey(), this.upm.getVersionString());
                if (!data.isAutoUpdate()) continue;
                this.notificationCache.addNotificationForPlugin(NotificationType.AUTO_UPDATED_UPM, this.upm.getPluginKey());
                if (!this.isStubPluginInstalled()) continue;
                final long oldSupId = data.getSelfUpdatePluginBundleId();
                Runnable cleanupTask = new Runnable(){

                    @Override
                    public void run() {
                        for (Plugin sup : SelfUpdateControllerImpl.this.pluginRetriever.getPlugin(SelfUpdateControllerImpl.SELFUPDATE_PLUGIN_KEY)) {
                            long newSupId = ((OsgiPlugin)sup.getPlugin()).getBundle().getBundleId();
                            if (newSupId != oldSupId) continue;
                            SelfUpdateControllerImpl.this.cleanupStubPlugin();
                        }
                    }
                };
                this.scheduler.triggerRunnable(cleanupTask, Duration.standardSeconds((long)3L), "self-update plugin removal");
            }
            return true;
        }
        return false;
    }

    private boolean isStubPluginInstalled() {
        return this.pluginRetriever.isPluginInstalled(SELFUPDATE_PLUGIN_KEY);
    }

    private boolean cleanupStubPlugin() {
        try {
            for (Plugin sup : this.pluginRetriever.getPlugin(SELFUPDATE_PLUGIN_KEY)) {
                this.pluginInstallationService.uninstall(sup);
                logger.info("Successfully uninstalled self-update plugin");
            }
            return true;
        }
        catch (Exception e) {
            logger.error("Error in uninstalling self-update plugin: " + e);
            logger.debug(e.toString(), (Throwable)e);
            return false;
        }
    }

    private Option<String> getBundleAttribute(File plugin, String attributeName) {
        try {
            Manifest manifest = new JarFile(plugin).getManifest();
            if (manifest == null) {
                return Option.none();
            }
            return Option.option(manifest.getMainAttributes().getValue(attributeName));
        }
        catch (IOException e) {
            return Option.none();
        }
    }

    private PluginSettings getPluginSettings() {
        return this.pluginSettingsFactory.createGlobalSettings();
    }

    private static class SelfUpdateData {
        private static final String FROM_BUNDLE_ID_KEY = "fromBundleId";
        private static final String AUTO_UPDATE_KEY = "autoUpdate";
        private static final String SELF_UPDATE_PLUGIN_BUNDLE_ID_KEY = "selfUpdatePluginBundleId";
        private final long fromBundleId;
        private final boolean autoUpdate;
        private final long selfUpdatePluginBundleId;

        SelfUpdateData(long fromBundleId, boolean autoUpdate, long selfUpdatePluginBundleId) {
            this.fromBundleId = fromBundleId;
            this.autoUpdate = autoUpdate;
            this.selfUpdatePluginBundleId = selfUpdatePluginBundleId;
        }

        public long getFromBundleId() {
            return this.fromBundleId;
        }

        public boolean isAutoUpdate() {
            return this.autoUpdate;
        }

        public long getSelfUpdatePluginBundleId() {
            return this.selfUpdatePluginBundleId;
        }

        public Properties encode() {
            Properties p = new Properties();
            p.put(FROM_BUNDLE_ID_KEY, String.valueOf(this.fromBundleId));
            p.put(AUTO_UPDATE_KEY, String.valueOf(this.autoUpdate));
            p.put(SELF_UPDATE_PLUGIN_BUNDLE_ID_KEY, String.valueOf(this.selfUpdatePluginBundleId));
            return p;
        }

        public static Option<SelfUpdateData> decode(Object o) {
            try {
                Properties p = (Properties)o;
                long fbi = Long.parseLong(p.getProperty(FROM_BUNDLE_ID_KEY));
                boolean su = Boolean.parseBoolean(p.getProperty(AUTO_UPDATE_KEY));
                long supbi = Long.parseLong(p.getProperty(SELF_UPDATE_PLUGIN_BUNDLE_ID_KEY));
                return Option.some(new SelfUpdateData(fbi, su, supbi));
            }
            catch (Exception exception) {
                return Option.none();
            }
        }
    }
}

