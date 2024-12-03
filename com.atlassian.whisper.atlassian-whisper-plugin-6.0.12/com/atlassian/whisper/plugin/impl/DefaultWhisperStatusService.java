/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.whisper.plugin.api.WhisperStatusService
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.whisper.plugin.impl;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.whisper.plugin.api.WhisperStatusService;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ExportAsService
public class DefaultWhisperStatusService
implements WhisperStatusService {
    private final DarkFeatureManager darkFeatureManager;

    @Inject
    public DefaultWhisperStatusService(@ComponentImport DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = darkFeatureManager;
    }

    public boolean isEnabled() {
        return !this.darkFeatureManager.isFeatureEnabledForAllUsers("whisper.disabled");
    }
}

