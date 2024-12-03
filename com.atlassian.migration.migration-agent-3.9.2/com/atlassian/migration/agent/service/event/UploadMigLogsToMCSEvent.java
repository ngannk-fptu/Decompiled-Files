/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEvent
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.event;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import lombok.Generated;

public class UploadMigLogsToMCSEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 1709756549982741321L;
    String migrationId;
    String planId;
    String cloudId;

    public UploadMigLogsToMCSEvent(Object source, String cloudId, String migrationId, String planId) {
        super(source);
        this.cloudId = cloudId;
        this.migrationId = migrationId;
        this.planId = planId;
    }

    @Generated
    public String getMigrationId() {
        return this.migrationId;
    }

    @Generated
    public String getPlanId() {
        return this.planId;
    }

    @Generated
    public String getCloudId() {
        return this.cloudId;
    }

    @Generated
    public String toString() {
        return "UploadMigLogsToMCSEvent(migrationId=" + this.getMigrationId() + ", planId=" + this.getPlanId() + ", cloudId=" + this.getCloudId() + ")";
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UploadMigLogsToMCSEvent)) {
            return false;
        }
        UploadMigLogsToMCSEvent other = (UploadMigLogsToMCSEvent)((Object)o);
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$migrationId = this.getMigrationId();
        String other$migrationId = other.getMigrationId();
        if (this$migrationId == null ? other$migrationId != null : !this$migrationId.equals(other$migrationId)) {
            return false;
        }
        String this$planId = this.getPlanId();
        String other$planId = other.getPlanId();
        if (this$planId == null ? other$planId != null : !this$planId.equals(other$planId)) {
            return false;
        }
        String this$cloudId = this.getCloudId();
        String other$cloudId = other.getCloudId();
        return !(this$cloudId == null ? other$cloudId != null : !this$cloudId.equals(other$cloudId));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UploadMigLogsToMCSEvent;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $migrationId = this.getMigrationId();
        result = result * 59 + ($migrationId == null ? 43 : $migrationId.hashCode());
        String $planId = this.getPlanId();
        result = result * 59 + ($planId == null ? 43 : $planId.hashCode());
        String $cloudId = this.getCloudId();
        result = result * 59 + ($cloudId == null ? 43 : $cloudId.hashCode());
        return result;
    }
}

