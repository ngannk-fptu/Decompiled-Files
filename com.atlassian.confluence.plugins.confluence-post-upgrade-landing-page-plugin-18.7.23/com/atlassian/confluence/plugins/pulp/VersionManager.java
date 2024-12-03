/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.persistence.VersionHistoryDao
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.inject.Named
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.joda.time.DateTime
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.plugins.pulp;

import com.atlassian.confluence.core.persistence.VersionHistoryDao;
import com.atlassian.confluence.plugins.pulp.PulpRedirectDao;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.inject.Named;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;

@Named
public class VersionManager
implements InitializingBean {
    private static final String KEY_NAMESPACE = "com.atlassian.confluence.plugins.pulp";
    private static final String VERSION_HISTORY = "version.history";
    private final VersionHistoryDao versionHistoryDao;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ApplicationProperties applicationProperties;
    private final PulpRedirectDao pulpRedirectDao;

    private VersionManager(@ComponentImport VersionHistoryDao versionHistoryDao, @ComponentImport PluginSettingsFactory pluginSettingsFactory, @ComponentImport ApplicationProperties applicationProperties, PulpRedirectDao pulpRedirectDao) {
        this.versionHistoryDao = versionHistoryDao;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.applicationProperties = applicationProperties;
        this.pulpRedirectDao = pulpRedirectDao;
    }

    public void afterPropertiesSet() {
        Map<String, String> versionHistory = this.getPost7VersionHistory();
        if (versionHistory.containsKey(this.applicationProperties.getVersion())) {
            return;
        }
        HashMap<String, String> newVersionHistory = new HashMap<String, String>(versionHistory);
        newVersionHistory.put(this.applicationProperties.getVersion(), DateTime.now().toString());
        this.pluginSettingsFactory.createSettingsForKey(KEY_NAMESPACE).put(VERSION_HISTORY, newVersionHistory);
    }

    boolean isFreshInstall() {
        Map<String, String> post7VersionHistory = this.getPost7VersionHistory();
        if (post7VersionHistory.keySet().size() == 1) {
            return this.noOlderUpgradesRecordedInHistory();
        }
        String earliestVersion = post7VersionHistory.entrySet().stream().min(Map.Entry.comparingByValue(Comparator.comparing(DateTime::parse))).map(Map.Entry::getKey).orElse(null);
        return this.applicationProperties.getVersion().equals(earliestVersion);
    }

    private boolean noOlderUpgradesRecordedInHistory() {
        return this.versionHistoryDao.getUpgradeHistory(0, 2).size() < 2;
    }

    private @NonNull Map<String, String> getPost7VersionHistory() {
        Object versionHistory = this.pluginSettingsFactory.createSettingsForKey(KEY_NAMESPACE).get(VERSION_HISTORY);
        if (versionHistory == null) {
            return new HashMap<String, String>();
        }
        if (versionHistory instanceof Map) {
            return (Map)versionHistory;
        }
        throw new IllegalStateException("The Version History object is not a Map, this may be caused by programming error or database corruption");
    }

    void addRedirectForUser() {
        this.pulpRedirectDao.addRedirect(AuthenticatedUserThreadLocal.get(), this.applicationProperties.getVersion());
    }

    int getTotalRedirects() {
        return this.pulpRedirectDao.getRedirectCount(this.applicationProperties.getVersion());
    }

    boolean hasBeenRedirectedForThisVersionOfConfluence() {
        return this.pulpRedirectDao.hasBeenRedirected(AuthenticatedUserThreadLocal.get(), this.applicationProperties.getVersion());
    }

    public @NonNull Optional<Date> getUpgradeDate() {
        String currentVersion = this.applicationProperties.getVersion();
        String upgradeDateForThisVersion = this.getPost7VersionHistory().get(currentVersion);
        if (null == upgradeDateForThisVersion) {
            return Optional.empty();
        }
        return Optional.of(DateTime.parse((String)upgradeDateForThisVersion).toDate());
    }
}

