/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.stepexecutor.space;

import java.util.Map;
import lombok.Generated;

public class SpaceExportResult {
    private String exportFile;
    private Map<String, Object> attributes;

    @Generated
    public String getExportFile() {
        return this.exportFile;
    }

    @Generated
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Generated
    public void setExportFile(String exportFile) {
        this.exportFile = exportFile;
    }

    @Generated
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SpaceExportResult)) {
            return false;
        }
        SpaceExportResult other = (SpaceExportResult)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$exportFile = this.getExportFile();
        String other$exportFile = other.getExportFile();
        if (this$exportFile == null ? other$exportFile != null : !this$exportFile.equals(other$exportFile)) {
            return false;
        }
        Map<String, Object> this$attributes = this.getAttributes();
        Map<String, Object> other$attributes = other.getAttributes();
        return !(this$attributes == null ? other$attributes != null : !((Object)this$attributes).equals(other$attributes));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof SpaceExportResult;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $exportFile = this.getExportFile();
        result = result * 59 + ($exportFile == null ? 43 : $exportFile.hashCode());
        Map<String, Object> $attributes = this.getAttributes();
        result = result * 59 + ($attributes == null ? 43 : ((Object)$attributes).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "SpaceExportResult(exportFile=" + this.getExportFile() + ", attributes=" + this.getAttributes() + ")";
    }

    @Generated
    public SpaceExportResult(String exportFile, Map<String, Object> attributes) {
        this.exportFile = exportFile;
        this.attributes = attributes;
    }
}

