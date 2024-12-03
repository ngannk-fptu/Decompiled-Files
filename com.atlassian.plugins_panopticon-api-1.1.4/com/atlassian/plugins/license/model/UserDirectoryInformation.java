/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.license.model;

public interface UserDirectoryInformation {
    public Long getCrowdDirectoryId();

    public Long getLocalDirectoryId();

    public String getDirectoryName();

    public Long getVersion();

    public void setVersion(Long var1);

    public void setCrowdDirectoryId(Long var1);

    public void setDirectoryName(String var1);

    public void setLocalDirectoryId(Long var1);
}

