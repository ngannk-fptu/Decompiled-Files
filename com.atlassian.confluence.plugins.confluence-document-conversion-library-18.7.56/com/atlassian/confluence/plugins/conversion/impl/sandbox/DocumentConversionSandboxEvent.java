/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionType
 */
package com.atlassian.confluence.plugins.conversion.impl.sandbox;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionType;

@EventName(value="confluence.document.conversion.sandbox")
public class DocumentConversionSandboxEvent {
    private final int fileHash;
    private final long fileSize;
    private final String fileFormat;
    private final String conversionType;
    private final String status;
    private final long actualTimeInSecs;
    private final long timeLimitInSecs;

    public DocumentConversionSandboxEvent(int fileHash, long fileSize, FileFormat fileFormat, SandboxConversionType conversionType, String status, long timeLimitInSecs, long actualTimeInSecs) {
        this.fileHash = fileHash;
        this.fileSize = fileSize;
        this.fileFormat = fileFormat.name();
        this.conversionType = conversionType.name();
        this.status = status;
        this.timeLimitInSecs = timeLimitInSecs;
        this.actualTimeInSecs = actualTimeInSecs;
    }

    public int getFileHash() {
        return this.fileHash;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public String getFileFormat() {
        return this.fileFormat;
    }

    public String getConversionType() {
        return this.conversionType;
    }

    public String getStatus() {
        return this.status;
    }

    public long getTimeLimitInSecs() {
        return this.timeLimitInSecs;
    }

    public long getActualTimeInSecs() {
        return this.actualTimeInSecs;
    }
}

