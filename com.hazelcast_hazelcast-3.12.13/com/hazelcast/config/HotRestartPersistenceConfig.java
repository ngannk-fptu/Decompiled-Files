/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.HotRestartClusterDataRecoveryPolicy;
import com.hazelcast.util.Preconditions;
import java.io.File;

public class HotRestartPersistenceConfig {
    public static final String HOT_RESTART_BASE_DIR_DEFAULT = "hot-restart";
    public static final int DEFAULT_VALIDATION_TIMEOUT = 120;
    public static final int DEFAULT_DATA_LOAD_TIMEOUT = 900;
    public static final int DEFAULT_PARALLELISM = 1;
    private boolean enabled;
    private File baseDir = new File("hot-restart");
    private File backupDir;
    private int parallelism = 1;
    private int validationTimeoutSeconds = 120;
    private int dataLoadTimeoutSeconds = 900;
    private HotRestartClusterDataRecoveryPolicy clusterDataRecoveryPolicy = HotRestartClusterDataRecoveryPolicy.FULL_RECOVERY_ONLY;
    private boolean autoRemoveStaleData = true;

    public boolean isEnabled() {
        return this.enabled;
    }

    public HotRestartPersistenceConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public HotRestartClusterDataRecoveryPolicy getClusterDataRecoveryPolicy() {
        return this.clusterDataRecoveryPolicy;
    }

    public HotRestartPersistenceConfig setClusterDataRecoveryPolicy(HotRestartClusterDataRecoveryPolicy clusterDataRecoveryPolicy) {
        this.clusterDataRecoveryPolicy = clusterDataRecoveryPolicy;
        return this;
    }

    public File getBaseDir() {
        return this.baseDir;
    }

    public HotRestartPersistenceConfig setBaseDir(File baseDir) {
        Preconditions.checkNotNull(baseDir, "Base directory cannot be null!");
        this.baseDir = baseDir;
        return this;
    }

    public File getBackupDir() {
        return this.backupDir;
    }

    public HotRestartPersistenceConfig setBackupDir(File backupDir) {
        this.backupDir = backupDir;
        return this;
    }

    public int getParallelism() {
        return this.parallelism;
    }

    public HotRestartPersistenceConfig setParallelism(int parallelism) {
        Preconditions.checkPositive(parallelism, "Palallelism must be a positive integer");
        this.parallelism = parallelism;
        return this;
    }

    public int getValidationTimeoutSeconds() {
        return this.validationTimeoutSeconds;
    }

    public HotRestartPersistenceConfig setValidationTimeoutSeconds(int validationTimeoutSeconds) {
        Preconditions.checkPositive(validationTimeoutSeconds, "Validation timeout should be positive!");
        this.validationTimeoutSeconds = validationTimeoutSeconds;
        return this;
    }

    public int getDataLoadTimeoutSeconds() {
        return this.dataLoadTimeoutSeconds;
    }

    public HotRestartPersistenceConfig setDataLoadTimeoutSeconds(int dataLoadTimeoutSeconds) {
        Preconditions.checkPositive(dataLoadTimeoutSeconds, "Load timeout should be positive!");
        this.dataLoadTimeoutSeconds = dataLoadTimeoutSeconds;
        return this;
    }

    public boolean isAutoRemoveStaleData() {
        return this.autoRemoveStaleData;
    }

    public HotRestartPersistenceConfig setAutoRemoveStaleData(boolean autoRemoveStaleData) {
        this.autoRemoveStaleData = autoRemoveStaleData;
        return this;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HotRestartPersistenceConfig)) {
            return false;
        }
        HotRestartPersistenceConfig that = (HotRestartPersistenceConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.parallelism != that.parallelism) {
            return false;
        }
        if (this.validationTimeoutSeconds != that.validationTimeoutSeconds) {
            return false;
        }
        if (this.dataLoadTimeoutSeconds != that.dataLoadTimeoutSeconds) {
            return false;
        }
        if (this.autoRemoveStaleData != that.autoRemoveStaleData) {
            return false;
        }
        if (this.baseDir != null ? !this.baseDir.equals(that.baseDir) : that.baseDir != null) {
            return false;
        }
        if (this.backupDir != null ? !this.backupDir.equals(that.backupDir) : that.backupDir != null) {
            return false;
        }
        return this.clusterDataRecoveryPolicy == that.clusterDataRecoveryPolicy;
    }

    public final int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + (this.baseDir != null ? this.baseDir.hashCode() : 0);
        result = 31 * result + (this.backupDir != null ? this.backupDir.hashCode() : 0);
        result = 31 * result + this.parallelism;
        result = 31 * result + this.validationTimeoutSeconds;
        result = 31 * result + this.dataLoadTimeoutSeconds;
        result = 31 * result + (this.clusterDataRecoveryPolicy != null ? this.clusterDataRecoveryPolicy.hashCode() : 0);
        result = 31 * result + (this.autoRemoveStaleData ? 1 : 0);
        return result;
    }

    public String toString() {
        return "HotRestartPersistenceConfig{enabled=" + this.enabled + ", baseDir=" + this.baseDir + ", backupDir=" + this.backupDir + ", parallelism=" + this.parallelism + ", validationTimeoutSeconds=" + this.validationTimeoutSeconds + ", dataLoadTimeoutSeconds=" + this.dataLoadTimeoutSeconds + ", clusterDataRecoveryPolicy=" + (Object)((Object)this.clusterDataRecoveryPolicy) + ", autoRemoveStaleData=" + this.autoRemoveStaleData + '}';
    }
}

