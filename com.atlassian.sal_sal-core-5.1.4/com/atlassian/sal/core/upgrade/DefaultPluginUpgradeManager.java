/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.osgi.util.OsgiHeaderUtil
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.upgrade.PluginUpgradeManager
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.sal.core.upgrade;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.upgrade.PluginUpgradeManager;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.sal.core.message.DefaultMessage;
import com.atlassian.sal.core.upgrade.PluginUpgrader;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class DefaultPluginUpgradeManager
implements PluginUpgradeManager,
LifecycleAware,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(DefaultPluginUpgradeManager.class);
    protected static final String LOCK_TIMEOUT_PROPERTY = "sal.upgrade.task.lock.timeout";
    protected static final int LOCK_TIMEOUT_SECONDS = Integer.getInteger("sal.upgrade.task.lock.timeout", 300000);
    private final String buildSettingsKey;
    private volatile boolean started = false;
    private final List<PluginUpgradeTask> upgradeTasks;
    private final TransactionTemplate transactionTemplate;
    private final PluginAccessor pluginAccessor;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final PluginEventManager pluginEventManager;
    private final ClusterLockService clusterLockService;

    public DefaultPluginUpgradeManager(List<PluginUpgradeTask> upgradeTasks, TransactionTemplate transactionTemplate, PluginAccessor pluginAccessor, PluginSettingsFactory pluginSettingsFactory, PluginEventManager pluginEventManager, ClusterLockService clusterLockService) {
        this(upgradeTasks, transactionTemplate, pluginAccessor, pluginSettingsFactory, pluginEventManager, clusterLockService, ":build");
    }

    public DefaultPluginUpgradeManager(List<PluginUpgradeTask> upgradeTasks, TransactionTemplate transactionTemplate, PluginAccessor pluginAccessor, PluginSettingsFactory pluginSettingsFactory, PluginEventManager pluginEventManager, ClusterLockService clusterLockService, String buildSettingsKey) {
        this.upgradeTasks = upgradeTasks;
        this.transactionTemplate = transactionTemplate;
        this.pluginAccessor = pluginAccessor;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.pluginEventManager = pluginEventManager;
        this.clusterLockService = clusterLockService;
        this.buildSettingsKey = buildSettingsKey;
    }

    @Deprecated
    public void onBind(PluginUpgradeTask task, Map props) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("onbind task = [{}, {}]", (Object)task.getPluginKey(), (Object)task.getBuildNumber());
            }
        }
        catch (Exception | LinkageError e) {
            log.error("Unable to resolve task build number", e);
        }
    }

    public void onStart() {
        log.debug("onStart");
        List<Message> messages = this.upgrade();
        messages.forEach(msg -> log.error("Upgrade error: {}", msg));
        this.started = true;
    }

    public void onStop() {
        this.pluginEventManager.unregister((Object)this);
    }

    @PluginEventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        if (!this.started) {
            log.info("Ignoring event: {}; LifecycleAware.onStart has not occurred yet", (Object)event);
            return;
        }
        List<Message> messages = this.upgradeInternal(event.getPlugin());
        if (!messages.isEmpty()) {
            log.error("Error(s) encountered while upgrading plugin '{}' on event: {}.", (Object)event.getPlugin().getName(), (Object)event);
            messages.forEach(msg -> log.error("Upgrade error: {}", msg));
        }
    }

    public List<Message> upgrade() {
        return this.upgradeInternal();
    }

    public void afterPropertiesSet() {
        this.pluginEventManager.register((Object)this);
    }

    public Map<String, List<PluginUpgradeTask>> getUpgradeTasks() {
        return this.getUpgradeTasksInternal(null);
    }

    @Nonnull
    public List<Message> upgradeInternal() {
        log.info("Running plugin upgrade tasks...");
        return this.upgradeInternal(null);
    }

    @Nonnull
    public List<Message> upgradeInternal(Plugin plugin) {
        Map<String, List<PluginUpgradeTask>> pluginUpgrades = this.getUpgradeTasksInternal(plugin);
        ArrayList<Message> messages = new ArrayList<Message>();
        for (String pluginKey : pluginUpgrades.keySet()) {
            if (!DefaultPluginUpgradeManager.matches(plugin, pluginKey)) continue;
            List<Message> errors = this.upgradePlugin(pluginKey, pluginUpgrades.get(pluginKey));
            messages.addAll(errors);
        }
        return messages;
    }

    @Nonnull
    private Map<String, List<PluginUpgradeTask>> getUpgradeTasksInternal(@Nullable Plugin plugin) {
        HashMap<String, List<PluginUpgradeTask>> pluginUpgrades = new HashMap<String, List<PluginUpgradeTask>>();
        for (PluginUpgradeTask upgradeTask : this.upgradeTasks) {
            if (!DefaultPluginUpgradeManager.validate(upgradeTask) || !DefaultPluginUpgradeManager.matches(plugin, upgradeTask.getPluginKey())) continue;
            pluginUpgrades.computeIfAbsent(upgradeTask.getPluginKey(), k -> new ArrayList()).add(upgradeTask);
        }
        return pluginUpgrades;
    }

    @Nonnull
    private List<Message> upgradePlugin(String pluginKey, List<PluginUpgradeTask> upgrades) {
        return (List)this.transactionTemplate.execute(() -> {
            Plugin plugin = this.pluginAccessor.getPlugin(pluginKey);
            if (plugin == null) {
                throw new IllegalArgumentException("Invalid plugin key: " + pluginKey);
            }
            PluginUpgrader pluginUpgrader = new PluginUpgrader(plugin, this.pluginSettingsFactory.createGlobalSettings(), this.buildSettingsKey, upgrades);
            String lockName = DefaultPluginUpgradeManager.asClusterLockName(pluginKey);
            ClusterLock lock = this.clusterLockService.getLockForName(lockName);
            try {
                if (!lock.tryLock((long)LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    String timeoutMessage = "unable to acquire cluster lock named '" + lockName + "' after waiting " + LOCK_TIMEOUT_SECONDS + " seconds; note that this timeout may be adjusted via the system property '" + LOCK_TIMEOUT_PROPERTY + "'";
                    log.error(timeoutMessage);
                    return ImmutableList.of((Object)new DefaultMessage(timeoutMessage, new Serializable[0]));
                }
            }
            catch (InterruptedException e) {
                String interruptedMessage = "interrupted while trying to acquire cluster lock named '" + lockName + "' " + e.getMessage();
                log.error(interruptedMessage);
                return ImmutableList.of((Object)new DefaultMessage(interruptedMessage, new Serializable[0]));
            }
            try {
                List<Message> list = pluginUpgrader.upgrade();
                return list;
            }
            finally {
                lock.unlock();
            }
        });
    }

    private static boolean validate(PluginUpgradeTask upgradeTask) {
        List<String> methodsToValidate = Arrays.asList("getBuildNumber", "getShortDescription", "getPluginKey");
        List methods = Arrays.stream(PluginUpgradeTask.class.getMethods()).filter(m -> methodsToValidate.contains(m.getName())).collect(Collectors.toList());
        boolean valid = true;
        for (Method method : methods) {
            String error = "";
            try {
                Object ret = method.invoke((Object)upgradeTask, new Object[0]);
                if (ret == null) {
                    error = "returns null";
                    valid = false;
                }
            }
            catch (Throwable e) {
                error = "throws exception " + e + "\n" + ExceptionUtils.getStackTrace((Throwable)e);
                valid = false;
            }
            if (error.isEmpty()) continue;
            log.warn("Invalid upgrade task: {} ({}); {} {}", new Object[]{upgradeTask.getClass().getName(), DefaultPluginUpgradeManager.getPluginKeySafely(upgradeTask), method.getName(), error});
        }
        return valid;
    }

    private static boolean matches(Plugin plugin, String pluginKey) {
        return plugin == null || plugin.getKey().equals(pluginKey);
    }

    private static String getPluginKeySafely(PluginUpgradeTask upgradeTask) {
        Class<?> upgradeTaskClass = upgradeTask.getClass();
        return upgradeTaskClass.getClassLoader() instanceof BundleReference ? OsgiHeaderUtil.getPluginKey((Bundle)((BundleReference)upgradeTaskClass.getClassLoader()).getBundle()) : "Unknown app";
    }

    private static String asClusterLockName(String pluginKey) {
        return "sal.upgrade." + pluginKey;
    }
}

