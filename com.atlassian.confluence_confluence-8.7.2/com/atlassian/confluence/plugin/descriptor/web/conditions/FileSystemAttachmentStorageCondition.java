/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.setup.settings.SettingsManager;

public class FileSystemAttachmentStorageCondition
extends BaseConfluenceCondition {
    private SettingsManager settingsManager;

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        return this.isFileSystemStorage();
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    private boolean isFileSystemStorage() {
        String setting = this.settingsManager.getGlobalSettings().getAttachmentDataStore();
        return setting == null || "file.system.based.attachments.storage".equals(setting);
    }
}

