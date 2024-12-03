/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.admin.util;

public interface ConfigurationWithPassword {
    public long getDirectoryId();

    public void setPassword(String var1);

    public String getPassword();

    public String getPasswordAttributeKey();
}

