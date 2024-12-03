/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.async;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.pipes.PipesConfigBase;
import org.apache.tika.pipes.PipesReporter;

public class AsyncConfig
extends PipesConfigBase {
    private long emitWithinMillis = 10000L;
    private long emitMaxEstimatedBytes = 100000L;
    private int queueSize = 10000;
    private int numEmitters = 1;
    private PipesReporter pipesReporter = PipesReporter.NO_OP_REPORTER;

    public static AsyncConfig load(Path p) throws IOException, TikaConfigException {
        AsyncConfig asyncConfig = new AsyncConfig();
        try (InputStream is = Files.newInputStream(p, new OpenOption[0]);){
            asyncConfig.configure("async", is);
        }
        if (asyncConfig.getTikaConfig() == null) {
            asyncConfig.setTikaConfig(p);
        }
        return asyncConfig;
    }

    public long getEmitWithinMillis() {
        return this.emitWithinMillis;
    }

    public void setEmitWithinMillis(long emitWithinMillis) {
        this.emitWithinMillis = emitWithinMillis;
    }

    public long getEmitMaxEstimatedBytes() {
        return this.emitMaxEstimatedBytes;
    }

    public void setEmitMaxEstimatedBytes(long emitMaxEstimatedBytes) {
        this.emitMaxEstimatedBytes = emitMaxEstimatedBytes;
    }

    public void setNumEmitters(int numEmitters) {
        this.numEmitters = numEmitters;
    }

    public int getQueueSize() {
        return this.queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getNumEmitters() {
        return this.numEmitters;
    }

    public PipesReporter getPipesReporter() {
        return this.pipesReporter;
    }

    public void setPipesReporter(PipesReporter pipesReporter) {
        this.pipesReporter = pipesReporter;
    }
}

