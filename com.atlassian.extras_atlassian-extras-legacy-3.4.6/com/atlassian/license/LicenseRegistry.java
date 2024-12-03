/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.license;

@Deprecated
public interface LicenseRegistry {
    public void setLicenseMessage(String var1);

    public void setLicenseHash(String var1);

    public String getLicenseMessage();

    public String getLicenseHash();
}

