/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.log;

public enum EntryType {
    PLUGIN_INSTALL("upm.auditLog.entryType.pluginInstall", "upm.auditLog.install"),
    PLUGIN_UNINSTALL("upm.auditLog.entryType.pluginUninstall", "upm.auditLog.uninstall"),
    PLUGIN_ENABLE("upm.auditLog.entryType.pluginEnable", "upm.auditLog.enable"),
    PLUGIN_DISABLE("upm.auditLog.entryType.pluginDisable", "upm.auditLog.disable"),
    PLUGIN_UPDATE("upm.auditLog.entryType.pluginUpdate", "upm.auditLog.update", "upm.auditLog.upgrade"),
    AUTO_PLUGIN_UPDATE("upm.auditLog.entryType.pluginAutoUpdate", "upm.auditLog.auto.update"),
    ENTER_SAFE_MODE("upm.auditLog.entryType.enterSafeMode", "upm.auditLog.safeMode.enter"),
    EXIT_SAFE_MODE("upm.auditLog.entryType.exitSafeMode", "upm.auditLog.safeMode.exit"),
    SYSTEM_STARTUP("upm.auditLog.entryType.systemStartup", "upm.auditLog.system.startup"),
    UPM_STARTUP("upm.auditLog.entryType.upmStartup", "upm.auditLog.upm.startup"),
    CANCELLED_CHANGE("upm.auditLog.entryType.cancelledChange", "upm.auditLog.cancelChange"),
    LICENSE_ADD("upm.auditLog.entryType.licenseAdd", "upm.auditLog.plugin.license.add"),
    LICENSE_REMOVE("upm.auditLog.entryType.licenseRemove", "upm.auditLog.plugin.license.remove"),
    LICENSE_UPDATE("upm.auditLog.entryType.licenseUpdate", "upm.auditLog.plugin.license.update"),
    UNCLASSIFIED_EVENT("upm.auditLog.entryType.unclassified", null);

    private final String i18nName;
    private final String[] i18nPrefixes;

    private EntryType(String i18nName, String ... i18nPrefixes) {
        this.i18nName = i18nName;
        this.i18nPrefixes = i18nPrefixes;
    }

    public String getI18nName() {
        return this.i18nName;
    }

    private String[] getI18nPrefixes() {
        return this.i18nPrefixes;
    }

    private static boolean startsWithI18nPrefix(String i18n, EntryType type) {
        if (type.getI18nPrefixes() != null) {
            for (String prefix : type.getI18nPrefixes()) {
                if (!i18n.startsWith(prefix)) continue;
                return true;
            }
        }
        return false;
    }

    public static EntryType valueOfI18n(String i18n) {
        for (EntryType type : EntryType.values()) {
            if (!EntryType.startsWithI18nPrefix(i18n, type)) continue;
            return type;
        }
        return UNCLASSIFIED_EVENT;
    }
}

