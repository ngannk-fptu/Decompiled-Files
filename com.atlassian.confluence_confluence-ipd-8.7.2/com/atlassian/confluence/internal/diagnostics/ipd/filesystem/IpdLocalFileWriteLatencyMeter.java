/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.filesystem;

import com.atlassian.confluence.internal.diagnostics.ipd.filesystem.IpdFileWriteLatencyMeter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;

public class IpdLocalFileWriteLatencyMeter
extends IpdFileWriteLatencyMeter {
    private final File file;

    public IpdLocalFileWriteLatencyMeter(File file, int numberOfMeasurements) {
        super(numberOfMeasurements);
        this.file = file;
    }

    @Override
    protected Duration measureWriteLatency() throws IOException {
        try {
            Duration duration;
            try (FileOutputStream fileStream = new FileOutputStream(this.file);){
                long start = System.nanoTime();
                fileStream.write(SAMPLE_DATA);
                fileStream.getFD().sync();
                long end = System.nanoTime();
                duration = Duration.ofNanos(end - start);
            }
            return duration;
        }
        finally {
            try {
                this.file.delete();
            }
            catch (Exception exception) {}
        }
    }
}

