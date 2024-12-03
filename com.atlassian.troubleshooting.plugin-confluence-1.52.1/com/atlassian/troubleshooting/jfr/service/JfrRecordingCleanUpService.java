/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.service;

public interface JfrRecordingCleanUpService {
    public void cleanUpStaleRecordings();

    public void cleanUpDumpOnExitStaleRecordings();
}

