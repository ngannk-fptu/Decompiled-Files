/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.newexport;

import java.util.HashMap;
import java.util.Map;
import lombok.Generated;

public class CSVExportTaskContext {
    private final String cloudId;
    private final String planId;
    private final String taskId;
    private final String tempDirFilePath;
    private long totalRowCount;
    private long totalCharactersExported;
    private final Map<String, Long> fileRowCount;

    public CSVExportTaskContext(String cloudId, String planId, String taskId, String tempDirFilePath) {
        this.cloudId = cloudId;
        this.planId = planId;
        this.taskId = taskId;
        this.tempDirFilePath = tempDirFilePath;
        this.totalRowCount = 0L;
        this.totalCharactersExported = 0L;
        this.fileRowCount = new HashMap<String, Long>();
    }

    void addFileRowCount(String entityName, long rowCount) {
        this.fileRowCount.put(entityName, rowCount);
    }

    void increaseTotalRowCount(long rowCount) {
        this.totalRowCount += rowCount;
    }

    void increaseTotalCharactersExported(long totalCharacterContentsExported) {
        this.totalCharactersExported += totalCharacterContentsExported;
    }

    @Generated
    public String getCloudId() {
        return this.cloudId;
    }

    @Generated
    public String getPlanId() {
        return this.planId;
    }

    @Generated
    public String getTaskId() {
        return this.taskId;
    }

    @Generated
    public String getTempDirFilePath() {
        return this.tempDirFilePath;
    }

    @Generated
    public long getTotalRowCount() {
        return this.totalRowCount;
    }

    @Generated
    public long getTotalCharactersExported() {
        return this.totalCharactersExported;
    }

    @Generated
    public Map<String, Long> getFileRowCount() {
        return this.fileRowCount;
    }

    @Generated
    public void setTotalRowCount(long totalRowCount) {
        this.totalRowCount = totalRowCount;
    }

    @Generated
    public void setTotalCharactersExported(long totalCharactersExported) {
        this.totalCharactersExported = totalCharactersExported;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CSVExportTaskContext)) {
            return false;
        }
        CSVExportTaskContext other = (CSVExportTaskContext)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$cloudId = this.getCloudId();
        String other$cloudId = other.getCloudId();
        if (this$cloudId == null ? other$cloudId != null : !this$cloudId.equals(other$cloudId)) {
            return false;
        }
        String this$planId = this.getPlanId();
        String other$planId = other.getPlanId();
        if (this$planId == null ? other$planId != null : !this$planId.equals(other$planId)) {
            return false;
        }
        String this$taskId = this.getTaskId();
        String other$taskId = other.getTaskId();
        if (this$taskId == null ? other$taskId != null : !this$taskId.equals(other$taskId)) {
            return false;
        }
        String this$tempDirFilePath = this.getTempDirFilePath();
        String other$tempDirFilePath = other.getTempDirFilePath();
        if (this$tempDirFilePath == null ? other$tempDirFilePath != null : !this$tempDirFilePath.equals(other$tempDirFilePath)) {
            return false;
        }
        if (this.getTotalRowCount() != other.getTotalRowCount()) {
            return false;
        }
        if (this.getTotalCharactersExported() != other.getTotalCharactersExported()) {
            return false;
        }
        Map<String, Long> this$fileRowCount = this.getFileRowCount();
        Map<String, Long> other$fileRowCount = other.getFileRowCount();
        return !(this$fileRowCount == null ? other$fileRowCount != null : !((Object)this$fileRowCount).equals(other$fileRowCount));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof CSVExportTaskContext;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $cloudId = this.getCloudId();
        result = result * 59 + ($cloudId == null ? 43 : $cloudId.hashCode());
        String $planId = this.getPlanId();
        result = result * 59 + ($planId == null ? 43 : $planId.hashCode());
        String $taskId = this.getTaskId();
        result = result * 59 + ($taskId == null ? 43 : $taskId.hashCode());
        String $tempDirFilePath = this.getTempDirFilePath();
        result = result * 59 + ($tempDirFilePath == null ? 43 : $tempDirFilePath.hashCode());
        long $totalRowCount = this.getTotalRowCount();
        result = result * 59 + (int)($totalRowCount >>> 32 ^ $totalRowCount);
        long $totalCharactersExported = this.getTotalCharactersExported();
        result = result * 59 + (int)($totalCharactersExported >>> 32 ^ $totalCharactersExported);
        Map<String, Long> $fileRowCount = this.getFileRowCount();
        result = result * 59 + ($fileRowCount == null ? 43 : ((Object)$fileRowCount).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "CSVExportTaskContext(cloudId=" + this.getCloudId() + ", planId=" + this.getPlanId() + ", taskId=" + this.getTaskId() + ", tempDirFilePath=" + this.getTempDirFilePath() + ", totalRowCount=" + this.getTotalRowCount() + ", totalCharactersExported=" + this.getTotalCharactersExported() + ", fileRowCount=" + this.getFileRowCount() + ")";
    }
}

