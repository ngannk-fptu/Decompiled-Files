/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.tika.config.ConfigBase;

public class PipesConfigBase
extends ConfigBase {
    public static final long DEFAULT_MAX_FOR_EMIT_BATCH = 100000L;
    public static final long DEFAULT_TIMEOUT_MILLIS = 60000L;
    public static final long DEFAULT_STARTUP_TIMEOUT_MILLIS = 240000L;
    public static final long DEFAULT_SHUTDOWN_CLIENT_AFTER_MILLS = 300000L;
    public static final int DEFAULT_NUM_CLIENTS = 4;
    public static final int DEFAULT_MAX_FILES_PROCESSED_PER_PROCESS = 10000;
    private long maxForEmitBatchBytes = 100000L;
    private long timeoutMillis = 60000L;
    private long startupTimeoutMillis = 240000L;
    private long sleepOnStartupTimeoutMillis = 240000L;
    private long shutdownClientAfterMillis = 300000L;
    private int numClients = 4;
    private int maxFilesProcessedPerProcess = 10000;
    private List<String> forkedJvmArgs = new ArrayList<String>();
    private Path tikaConfig;
    private String javaPath = "java";

    public long getTimeoutMillis() {
        return this.timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public long getShutdownClientAfterMillis() {
        return this.shutdownClientAfterMillis;
    }

    public void setShutdownClientAfterMillis(long shutdownClientAfterMillis) {
        this.shutdownClientAfterMillis = shutdownClientAfterMillis;
    }

    public int getNumClients() {
        return this.numClients;
    }

    public void setNumClients(int numClients) {
        this.numClients = numClients;
    }

    public List<String> getForkedJvmArgs() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.addAll(this.forkedJvmArgs);
        return ret;
    }

    public void setStartupTimeoutMillis(long startupTimeoutMillis) {
        this.startupTimeoutMillis = startupTimeoutMillis;
    }

    public void setForkedJvmArgs(List<String> jvmArgs) {
        this.forkedJvmArgs = Collections.unmodifiableList(jvmArgs);
    }

    public int getMaxFilesProcessedPerProcess() {
        return this.maxFilesProcessedPerProcess;
    }

    public void setMaxFilesProcessedPerProcess(int maxFilesProcessedPerProcess) {
        this.maxFilesProcessedPerProcess = maxFilesProcessedPerProcess;
    }

    public Path getTikaConfig() {
        return this.tikaConfig;
    }

    public void setTikaConfig(Path tikaConfig) {
        this.tikaConfig = tikaConfig;
    }

    public void setTikaConfig(String tikaConfig) {
        this.setTikaConfig(Paths.get(tikaConfig, new String[0]));
    }

    public String getJavaPath() {
        return this.javaPath;
    }

    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    public long getStartupTimeoutMillis() {
        return this.startupTimeoutMillis;
    }

    public long getMaxForEmitBatchBytes() {
        return this.maxForEmitBatchBytes;
    }

    public void setMaxForEmitBatchBytes(long maxForEmitBatchBytes) {
        this.maxForEmitBatchBytes = maxForEmitBatchBytes;
    }

    public long getSleepOnStartupTimeoutMillis() {
        return this.sleepOnStartupTimeoutMillis;
    }

    public void setSleepOnStartupTimeoutMillis(long sleepOnStartupTimeoutMillis) {
        this.sleepOnStartupTimeoutMillis = sleepOnStartupTimeoutMillis;
    }
}

