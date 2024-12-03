/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.confluence.impl.system.task;

import com.atlassian.confluence.impl.system.task.SystemMaintenanceTask;
import com.atlassian.confluence.internal.index.Index;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class RestoreIndexSnapshotMaintenanceTask
implements SystemMaintenanceTask {
    private String sourceNodeId;
    private Collection<IndexSnapshot> indexSnapshots;

    @JsonCreator
    public RestoreIndexSnapshotMaintenanceTask(@JsonProperty(value="sourceNodeId") String sourceNodeId, @JsonProperty(value="indexSnapshots") Collection<IndexSnapshot> indexSnapshots) {
        this.sourceNodeId = Objects.requireNonNull(sourceNodeId);
        this.indexSnapshots = new ArrayList<IndexSnapshot>(indexSnapshots);
    }

    @JsonProperty(value="sourceNodeId")
    public String getSourceNodeId() {
        return this.sourceNodeId;
    }

    @JsonProperty(value="indexSnapshots")
    public Collection<IndexSnapshot> getIndexSnapshots() {
        return Collections.unmodifiableCollection(this.indexSnapshots);
    }

    public static class IndexSnapshot {
        private Index index;
        private Long journalId;

        @JsonCreator
        public IndexSnapshot(@JsonProperty(value="index") Index index, @JsonProperty(value="journalId") Long journalId) {
            this.index = Objects.requireNonNull(index);
            this.journalId = Objects.requireNonNull(journalId);
        }

        @JsonProperty(value="index")
        public Index getIndex() {
            return this.index;
        }

        @JsonProperty(value="journalId")
        public Long getJournalId() {
            return this.journalId;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            IndexSnapshot that = (IndexSnapshot)o;
            return this.index == that.index && this.journalId.equals(that.journalId);
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.index, this.journalId});
        }
    }
}

