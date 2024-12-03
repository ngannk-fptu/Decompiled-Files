/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.troubleshooting.stp.events;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="stp.support.zip.download")
public class StpSupportZipDownloadEvent {
    private final String applicationName;
    private final long fileSize;

    public StpSupportZipDownloadEvent(String applicationName, long fileSize) {
        this.applicationName = applicationName;
        this.fileSize = fileSize;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public long getFileSize() {
        return this.fileSize;
    }
}

