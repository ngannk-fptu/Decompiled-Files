/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 *  net.java.ao.schema.Unique
 */
package com.atlassian.troubleshooting.stp.persistence;

import java.util.Date;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;
import net.java.ao.schema.Unique;

public class SupportHealthcheckSchema {

    @Table(value="DISABLED_CHECKS")
    @Preload
    public static interface DisabledHealthChecks
    extends Entity {
        @Unique
        @NotNull
        public String getHealthCheckKey();

        public void setHealthCheckKey(String var1);
    }

    @Table(value="HEALTH_CHECK_WATCHER")
    @Preload
    public static interface Watcher
    extends Entity {
        @Unique
        @NotNull
        public String getUserKey();

        public void setUserKey(String var1);
    }

    @Table(value="PROPERTIES")
    @Preload
    public static interface SupportHealthStatusProperties
    extends Entity {
        @NotNull
        public String getPropertyName();

        public void setPropertyName(String var1);

        @NotNull
        public String getPropertyValue();

        public void setPropertyValue(String var1);
    }

    @Table(value="READ_NOTIFICATIONS")
    @Preload
    public static interface HealthCheckNotificationDismiss
    extends Entity {
        @NotNull
        public String getUserKey();

        public void setUserKey(String var1);

        @NotNull
        public Integer getNotificationId();

        public void setNotificationId(Integer var1);

        public Boolean getIsSnoozed();

        public void setIsSnoozed(Boolean var1);

        public Date getSnoozeDate();

        public void setSnoozeDate(Date var1);

        public Integer getSnoozeCount();

        public void setSnoozeCount(Integer var1);
    }

    @Table(value="HEALTH_CHECK_STATUS")
    @Preload
    public static interface HealthCheckStatusAO
    extends Entity {
        @NotNull
        public String getStatusName();

        public void setStatusName(String var1);

        @StringLength(value=-1)
        public String getDescription();

        public void setDescription(String var1);

        public Boolean getIsHealthy();

        public void setIsHealthy(Boolean var1);

        @StringLength(value=-1)
        public String getFailureReason();

        public void setFailureReason(String var1);

        public String getApplicationName();

        public String getNodeId();

        public void setApplicationName(String var1);

        public String getSeverity();

        public void setSeverity(String var1);

        public Date getFailedDate();

        public void setFailedDate(Date var1);

        public Boolean getIsResolved();

        public void setIsResolved(Boolean var1);

        public Date getResolvedDate();

        public void setResolvedDate(Date var1);

        public String getCompleteKey();

        public void setCompleteKey(String var1);
    }
}

