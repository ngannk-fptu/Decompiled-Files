/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cluster.monitoring.spi.model.Table
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 */
package com.atlassian.confluence.plugins.synchrony.supplier;

import com.atlassian.annotations.Internal;
import com.atlassian.cluster.monitoring.spi.model.Table;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyMonitor;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Internal
public class SynchronyStatusSupplier
implements Supplier<Table> {
    private final SynchronyMonitor synchronyMonitor;
    private final SynchronyConfigurationManager configManager;
    private final I18NBeanFactory i18NBeanFactory;

    public SynchronyStatusSupplier(SynchronyMonitor synchronyMonitor, SynchronyConfigurationManager configManager, I18NBeanFactory i18NBeanFactory) {
        this.synchronyMonitor = synchronyMonitor;
        this.configManager = configManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Override
    public Table get() {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>();
        columns.put("synchrony.monitoring.admin.collab.editing.title", "Collaborative editing mode");
        columns.put("synchrony.monitoring.admin.synchrony.mode.title", "Synchrony mode");
        columns.put("synchrony.monitoring.admin.synchrony.status.title", "Synchrony status");
        columns.put("collab.admin.btf.configure.synchrony.configuration.property.serviceUrl", "Synchrony URL");
        String collabEditingMode = i18NBean.getText(CollabMode.getMode(this.configManager.isSharedDraftsEnabled()).getI18NKey());
        String synchronyMode = i18NBean.getText(this.configManager.isUsingLocalSynchrony() ? "synchrony.monitoring.admin.process.selfmanaged" : "synchrony.monitoring.admin.process.external");
        String synchronyStatus = i18NBean.getText(this.synchronyMonitor.isSynchronyUp() ? "collab.admin.btf.configure.synchrony.running" : "synchrony.monitoring.admin.process.down");
        String externalSynchronyUrl = this.configManager.getExternalServiceUrl();
        HashMap<String, List<String>> rows = new HashMap<String, List<String>>();
        rows.put("synchrony.status", Arrays.asList(collabEditingMode, synchronyMode, synchronyStatus, externalSynchronyUrl));
        return new Table(columns, rows);
    }

    static enum CollabMode {
        ON("collab.admin.btf.configure.on"),
        OFF("collab.admin.btf.configure.off");

        private final String i18NKey;

        private CollabMode(String i18NKey) {
            this.i18NKey = Objects.requireNonNull(i18NKey);
        }

        String getI18NKey() {
            return this.i18NKey;
        }

        static CollabMode getMode(boolean isSharedDraftsEnabled) {
            if (!isSharedDraftsEnabled) {
                return OFF;
            }
            return ON;
        }
    }
}

