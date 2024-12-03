/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.io.PrintWriter;

public class AbandonedConfig {
    private boolean removeAbandonedOnBorrow = false;
    private boolean removeAbandonedOnMaintenance = false;
    private int removeAbandonedTimeout = 300;
    private boolean logAbandoned = false;
    private boolean requireFullStackTrace = true;
    private PrintWriter logWriter = new PrintWriter(System.out);
    private boolean useUsageTracking = false;

    public boolean getRemoveAbandonedOnBorrow() {
        return this.removeAbandonedOnBorrow;
    }

    public void setRemoveAbandonedOnBorrow(boolean removeAbandonedOnBorrow) {
        this.removeAbandonedOnBorrow = removeAbandonedOnBorrow;
    }

    public boolean getRemoveAbandonedOnMaintenance() {
        return this.removeAbandonedOnMaintenance;
    }

    public void setRemoveAbandonedOnMaintenance(boolean removeAbandonedOnMaintenance) {
        this.removeAbandonedOnMaintenance = removeAbandonedOnMaintenance;
    }

    public int getRemoveAbandonedTimeout() {
        return this.removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }

    public boolean getLogAbandoned() {
        return this.logAbandoned;
    }

    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    public boolean getRequireFullStackTrace() {
        return this.requireFullStackTrace;
    }

    public void setRequireFullStackTrace(boolean requireFullStackTrace) {
        this.requireFullStackTrace = requireFullStackTrace;
    }

    public PrintWriter getLogWriter() {
        return this.logWriter;
    }

    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public boolean getUseUsageTracking() {
        return this.useUsageTracking;
    }

    public void setUseUsageTracking(boolean useUsageTracking) {
        this.useUsageTracking = useUsageTracking;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AbandonedConfig [removeAbandonedOnBorrow=");
        builder.append(this.removeAbandonedOnBorrow);
        builder.append(", removeAbandonedOnMaintenance=");
        builder.append(this.removeAbandonedOnMaintenance);
        builder.append(", removeAbandonedTimeout=");
        builder.append(this.removeAbandonedTimeout);
        builder.append(", logAbandoned=");
        builder.append(this.logAbandoned);
        builder.append(", logWriter=");
        builder.append(this.logWriter);
        builder.append(", useUsageTracking=");
        builder.append(this.useUsageTracking);
        builder.append("]");
        return builder.toString();
    }
}

