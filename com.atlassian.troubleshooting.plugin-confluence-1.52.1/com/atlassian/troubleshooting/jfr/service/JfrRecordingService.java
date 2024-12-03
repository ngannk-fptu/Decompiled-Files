/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.service;

import com.atlassian.troubleshooting.jfr.domain.RecordingConfig;
import com.atlassian.troubleshooting.jfr.domain.RecordingWrapper;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import jdk.jfr.Configuration;

public interface JfrRecordingService {
    public Optional<RecordingWrapper> createRecording(RecordingConfig var1);

    public Optional<Path> dumpRecording(long var1);

    public List<RecordingWrapper> getRecordings();

    public Configuration getConfigurationTemplate();

    public Configuration getActiveConfiguration();
}

