/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.log;

import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.core.impl.NamespacedPluginSettings;
import java.util.Collection;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditLogUpgradeTask
implements PluginUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(AuditLogUpgradeTask.class);
    private static final String UPM_AUDIT_LOG_LEGACY = "upm_audit_log";
    private static final String UPM_AUDIT_LOG_V2 = "upm_audit_log_v2";
    private final PluginSettingsFactory pluginSettingsFactory;
    private final UpmInformation upm;

    public AuditLogUpgradeTask(PluginSettingsFactory pluginSettingsFactory, UpmInformation upm) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.upm = Objects.requireNonNull(upm, "upm");
    }

    public int getBuildNumber() {
        return 1;
    }

    public String getShortDescription() {
        return "Upgrades audit log to be compatible with UPM 1.6+";
    }

    public Collection<Message> doUpgrade() throws Exception {
        log.info("Running UPM Audit Log upgrade task");
        Object auditLog = this.getPluginSettings().get(UPM_AUDIT_LOG_LEGACY);
        if (auditLog != null) {
            log.debug("Migrating UPM Audit Log to v2");
            this.getPluginSettings().put(UPM_AUDIT_LOG_V2, auditLog);
            this.getPluginSettings().remove(UPM_AUDIT_LOG_LEGACY);
        }
        return null;
    }

    public String getPluginKey() {
        return this.upm.getPluginKey();
    }

    private PluginSettings getPluginSettings() {
        return new NamespacedPluginSettings(this.pluginSettingsFactory.createGlobalSettings(), "com.atlassian.upm.log.PluginSettingsAuditLogService:log:");
    }
}

