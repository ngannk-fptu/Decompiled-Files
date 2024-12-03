/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.mo;

import lombok.Generated;

public class MigrationOrchestratorServiceStatusResponse {
    private Boolean maintenance;

    @Generated
    public Boolean getMaintenance() {
        return this.maintenance;
    }

    @Generated
    public void setMaintenance(Boolean maintenance) {
        this.maintenance = maintenance;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MigrationOrchestratorServiceStatusResponse)) {
            return false;
        }
        MigrationOrchestratorServiceStatusResponse other = (MigrationOrchestratorServiceStatusResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Boolean this$maintenance = this.getMaintenance();
        Boolean other$maintenance = other.getMaintenance();
        return !(this$maintenance == null ? other$maintenance != null : !((Object)this$maintenance).equals(other$maintenance));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof MigrationOrchestratorServiceStatusResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Boolean $maintenance = this.getMaintenance();
        result = result * 59 + ($maintenance == null ? 43 : ((Object)$maintenance).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "MigrationOrchestratorServiceStatusResponse(maintenance=" + this.getMaintenance() + ")";
    }

    @Generated
    public MigrationOrchestratorServiceStatusResponse() {
    }

    @Generated
    public MigrationOrchestratorServiceStatusResponse(Boolean maintenance) {
        this.maintenance = maintenance;
    }
}

