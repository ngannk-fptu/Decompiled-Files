/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.common.util;

import java.util.Date;
import java.util.Map;

public interface LicenseProperties {
    public String getProperty(String var1);

    public String getProperty(String var1, String var2);

    public int getInt(String var1, int var2);

    public Date getDate(String var1, Date var2);

    public boolean getBoolean(String var1);

    public Map<String, String> getPropertiesEndingWith(String var1);
}

