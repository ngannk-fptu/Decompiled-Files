/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.FileStore$Path
 */
package com.atlassian.confluence.internal.diagnostics.ipd.filesystem;

import com.atlassian.confluence.internal.diagnostics.ipd.filesystem.IpdFileWriteLatencyMeter;
import com.atlassian.dc.filestore.api.FileStore;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class IpdSharedFileWriteLatencyMeter
extends IpdFileWriteLatencyMeter {
    private final FileStore.Path file;

    public IpdSharedFileWriteLatencyMeter(FileStore.Path file, int numberOfMeasurements) {
        super(numberOfMeasurements);
        this.file = file;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Duration measureWriteLatency() throws IOException {
        try {
            Instant start = this.clock.instant();
            this.file.fileWriter().write(SAMPLE_DATA);
            Instant end = this.clock.instant();
            Duration duration = Duration.between(start, end);
            return duration;
        }
        finally {
            this.file.tryDeleteFile();
        }
    }
}

