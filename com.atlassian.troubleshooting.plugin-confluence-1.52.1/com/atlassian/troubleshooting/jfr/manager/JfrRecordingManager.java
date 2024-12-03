/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.manager;

import com.atlassian.troubleshooting.jfr.domain.ConfigurationDetails;
import com.atlassian.troubleshooting.jfr.domain.JfrCapabilities;
import com.atlassian.troubleshooting.jfr.domain.JfrSettings;
import com.atlassian.troubleshooting.jfr.domain.RecordingDetails;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface JfrRecordingManager {
    public JfrCapabilities getCapabilities();

    public ConfigurationDetails getActiveConfiguration();

    public List<RecordingDetails> getRecordingDetails();

    public Optional<Path> dumpRecording(long var1);

    public JfrSettings storeSettings(JfrSettings var1);

    public void handleFeatureFlagStateChanged(JfrSettings var1);

    public JfrSettings getSettings();

    public boolean isJfrFeatureFlagEnabled();
}

