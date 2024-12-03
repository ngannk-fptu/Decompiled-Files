/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.license.model;

public interface LicenseDataVersion {
    public Long getProcessingStartTime();

    public Long getProcessingCompleteTime();

    public Long getProcessingVersion();

    public void setProcessingVersion(Long var1);

    public void setProcessingStartTime(Long var1);

    public void setProcessingCompleteTime(Long var1);
}

