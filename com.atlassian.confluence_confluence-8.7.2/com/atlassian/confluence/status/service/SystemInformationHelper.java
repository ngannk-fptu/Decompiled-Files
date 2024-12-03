/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 *  javax.servlet.ServletContext
 */
package com.atlassian.confluence.status.service;

import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.AttachmentStorageInfo;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.confluence.status.service.systeminfo.SystemInfo;
import com.atlassian.confluence.status.service.systeminfo.UsageInfo;
import com.atlassian.confluence.util.i18n.I18NBean;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;

public class SystemInformationHelper {
    private final SystemInfo systemInfo;
    private final AttachmentStorageInfo attachmentStorageInfo;
    private final ConfluenceInfo confluenceInfo;
    private final DatabaseInfo databaseInfo;
    private final UsageInfo usageInfo;
    private final AccessMode accessMode;
    private final I18NBean i18n;

    public SystemInformationHelper(I18NBean i18n, SystemInformationService service) {
        this.i18n = i18n;
        this.systemInfo = service.getSystemProperties();
        this.attachmentStorageInfo = service.getAttachmentStorageProperties();
        this.confluenceInfo = service.getConfluenceInfo();
        this.databaseInfo = service.getDatabaseInfo();
        this.usageInfo = service.getUsageInfo();
        this.accessMode = service.getAccessMode();
    }

    public Map<String, String> getSystemSummary() {
        LinkedHashMap<String, String> orderedMap = new LinkedHashMap<String, String>(11);
        orderedMap.put("system.date", this.stringOrNotAvailable(this.systemInfo.getDate()));
        orderedMap.put("system.time", this.stringOrNotAvailable(this.systemInfo.getTime()));
        orderedMap.put("storage.type", this.stringOrNotAvailable(this.attachmentStorageInfo.getStorageType().toString()));
        orderedMap.put("system.installation.date", this.dateFromStringOrNotAvailable(this.confluenceInfo.getInstallationDate()));
        orderedMap.put("system.uptime", this.stringOrNotAvailable(this.confluenceInfo.getUpTime()));
        orderedMap.put("system.version", this.stringOrNotAvailable(this.confluenceInfo.getVersion()));
        orderedMap.put("build.number", this.stringOrNotAvailable(this.confluenceInfo.getBuildNumber()));
        orderedMap.put("server.base.url", this.stringOrNotAvailable(this.confluenceInfo.getBaseUrl()));
        orderedMap.put("confluence.home", this.stringOrNotAvailable(this.confluenceInfo.getHome()));
        orderedMap.put("server.id", this.stringOrNotAvailable(this.confluenceInfo.getServerId()));
        orderedMap.put("license.support.entitlement.number.label", this.stringOrNotAvailable(this.confluenceInfo.getSupportEntitlementNumber()));
        if (this.confluenceInfo.isDevMode()) {
            orderedMap.put("developer.mode", this.i18n.getText("enabled.word"));
        }
        orderedMap.put("access.mode", this.accessMode.name());
        return orderedMap;
    }

    public Map<String, String> getRuntimeEnvironment(ServletContext context) {
        LinkedHashMap<String, String> orderedMap = new LinkedHashMap<String, String>(20);
        orderedMap.put("operating.system", this.systemInfo.getOperatingSystem());
        orderedMap.put("os.architecture", this.systemInfo.getOperatingSystemArchitecture());
        if (context != null) {
            orderedMap.put("application.server", context.getServerInfo());
            orderedMap.put("servlet.version", context.getMajorVersion() + "." + context.getMinorVersion());
        }
        orderedMap.put("java.version", this.stringOrNotAvailable(this.systemInfo.getJavaVersion()));
        orderedMap.put("java.vendor", this.stringOrNotAvailable(this.systemInfo.getJavaVendor()));
        orderedMap.put("jvm.version", this.stringOrNotAvailable(this.systemInfo.getJvmVersion()));
        orderedMap.put("jvm.vendor", this.stringOrNotAvailable(this.systemInfo.getJvmVendor()));
        orderedMap.put("jvm.implementation.version", this.stringOrNotAvailable(this.systemInfo.getJvmImplementationVersion()));
        orderedMap.put("java.runtime", this.stringOrNotAvailable(this.systemInfo.getJavaRuntime()));
        orderedMap.put("java.vm", this.stringOrNotAvailable(this.systemInfo.getJavaVm()));
        orderedMap.put("java.vm.args", this.stringOrNotAvailable(this.systemInfo.getJvmInputArguments()));
        orderedMap.put("working.directory", this.stringOrNotAvailable(this.systemInfo.getWorkingDirectory()));
        orderedMap.put("temp.directory", this.stringOrNotAvailable(this.systemInfo.getTempDirectory()));
        orderedMap.put("user.name.word", this.stringOrNotAvailable(this.systemInfo.getUserName()));
        orderedMap.put("system.language", this.stringOrNotAvailable(this.systemInfo.getSystemLanguage()));
        orderedMap.put("system.timezone", this.stringOrNotAvailable(this.systemInfo.getSystemTimezone()));
        orderedMap.put("fs.encoding", this.stringOrNotAvailable(this.systemInfo.getFileSystemEncoding()));
        return orderedMap;
    }

    public Map<String, String> getDatabaseInfo() {
        LinkedHashMap<String, String> orderedMap = new LinkedHashMap<String, String>(8);
        orderedMap.put("database.name", this.stringOrNotAvailable(this.databaseInfo.getName()));
        orderedMap.put("database.version", this.stringOrNotAvailable(this.databaseInfo.getVersion()));
        orderedMap.put("database.dialect", this.stringOrNotAvailable(this.databaseInfo.getDialect()));
        orderedMap.put("database.driver.name", this.stringOrNotAvailable(this.databaseInfo.getDriverName()));
        orderedMap.put("database.driver.version", this.stringOrNotAvailable(this.databaseInfo.getDriverVersion()));
        orderedMap.put("database.connection.url", this.stringOrNotAvailable(this.databaseInfo.getUrl()));
        orderedMap.put("database.transaction.isolation", this.stringOrNotAvailable(this.databaseInfo.getIsolationLevel()));
        orderedMap.put("database.latency", this.numberOrNotAvailable(this.databaseInfo.getExampleLatency()));
        return orderedMap;
    }

    public Map<String, String> getUsageInfo() {
        LinkedHashMap<String, String> orderedMap = new LinkedHashMap<String, String>(8);
        orderedMap.put("usage.total.spaces", this.numberOrNotAvailable(this.usageInfo.getTotalSpaces()));
        orderedMap.put("usage.global.spaces", this.numberOrNotAvailable(this.usageInfo.getGlobalSpaces()));
        orderedMap.put("usage.personal.spaces", this.numberOrNotAvailable(this.usageInfo.getPersonalSpaces()));
        orderedMap.put("usage.total.content", this.numberOrNotAvailable(this.usageInfo.getAllContent()));
        orderedMap.put("usage.current.content", this.numberOrNotAvailable(this.usageInfo.getCurrentContent()));
        orderedMap.put("usage.local.users", this.numberOrNotAvailable(this.usageInfo.getLocalUsers()));
        orderedMap.put("usage.local.groups", this.numberOrNotAvailable(this.usageInfo.getLocalGroups()));
        return orderedMap;
    }

    public Map<String, String> getAttachmentStorageInfo() {
        LinkedHashMap<String, String> orderedMap = new LinkedHashMap<String, String>(1);
        orderedMap.put("storage.type", this.stringOrNotAvailable(this.attachmentStorageInfo.getStorageType().toString()));
        return orderedMap;
    }

    private String stringOrNotAvailable(String val) {
        if (val == null) {
            return this.i18n.getText("not.available");
        }
        return val;
    }

    private String numberOrNotAvailable(Number n) {
        if (n == null || n.longValue() == -1L) {
            return this.i18n.getText("not.available");
        }
        return n.toString();
    }

    private String dateFromStringOrNotAvailable(Date installationDate) {
        if (installationDate == null) {
            return this.i18n.getText("not.available");
        }
        return DateFormat.getDateInstance(0, Locale.ENGLISH).format(installationDate);
    }
}

