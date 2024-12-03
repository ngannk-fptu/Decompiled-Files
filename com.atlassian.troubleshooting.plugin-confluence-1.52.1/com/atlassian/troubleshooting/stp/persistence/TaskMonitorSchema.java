/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.java.ao.Accessor
 *  net.java.ao.Entity
 *  net.java.ao.Mutator
 *  net.java.ao.Preload
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 *  net.java.ao.schema.Unique
 */
package com.atlassian.troubleshooting.stp.persistence;

import javax.annotation.Nonnull;
import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;
import net.java.ao.schema.Unique;

final class TaskMonitorSchema {
    private TaskMonitorSchema() {
    }

    @Table(value="TASK_MONITOR")
    @Preload
    public static interface TaskMonitorAO
    extends Entity {
        public static final String TASK_ID = "TASK_ID";
        public static final String NODE_ID = "NODE_ID";
        public static final String CLUSTERED_TASK_ID = "CLUSTERED_TASK_ID";
        public static final String CREATED_TIMESTAMP = "CREATED_TIMESTAMP";
        public static final String TASK_MONITOR_KIND = "TASK_MONITOR_KIND";
        public static final String TASK_ATTRIBUTES = "TASK_STATUS";

        @Nonnull
        @Indexed
        public String getTaskMonitorKind();

        public void setTaskMonitorKind(@Nonnull String var1);

        @StringLength(value=-1)
        public String getSerializedErrors();

        public void setSerializedErrors(String var1);

        @StringLength(value=-1)
        public String getSerializedWarnings();

        public void setSerializedWarnings(String var1);

        @Nonnull
        @StringLength(value=-1)
        public String getProgressMessage();

        public void setProgressMessage(@Nonnull String var1);

        @Nonnull
        public Integer getProgressPercentage();

        public void setProgressPercentage(@Nonnull Integer var1);

        @Nonnull
        @Unique
        public String getTaskId();

        public void setTaskId(@Nonnull String var1);

        public String getNodeId();

        public void setNodeId(String var1);

        public String getClusteredTaskId();

        public void setClusteredTaskId(String var1);

        public long getCreatedTimestamp();

        public void setCreatedTimestamp(long var1);

        @Accessor(value="TASK_STATUS")
        @StringLength(value=-1)
        public String getAttributes();

        @Mutator(value="TASK_STATUS")
        public void setAttributes(String var1);
    }
}

