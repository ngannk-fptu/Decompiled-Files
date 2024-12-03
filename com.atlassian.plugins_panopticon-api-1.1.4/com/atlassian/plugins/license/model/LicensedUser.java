/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.license.model;

public interface LicensedUser {
    public String getUsername();

    public String getEmail();

    public String getDisplayName();

    public Long getCrowdDirectoryId();

    public Long getDirectoryId();

    public String getExternalId();

    public boolean isRemoteCrowdUser();

    public Long getLastLoginTime();

    public String getApplicationKey();

    public Long getVersion();

    public void setUsername(String var1);

    public void setEmail(String var1);

    public void setDisplayName(String var1);

    public void setCrowdDirectoryId(Long var1);

    public void setDirectoryId(Long var1);

    public void setRemoteCrowdUser(boolean var1);

    public void setExternalId(String var1);

    public void setLastLoginTime(Long var1);

    public void setApplicationKey(String var1);

    public void setVersion(Long var1);
}

