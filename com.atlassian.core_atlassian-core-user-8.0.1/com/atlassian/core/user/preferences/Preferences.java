/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.AtlassianCoreException
 */
package com.atlassian.core.user.preferences;

import com.atlassian.core.AtlassianCoreException;

public interface Preferences {
    public long getLong(String var1);

    public void setLong(String var1, long var2) throws AtlassianCoreException;

    public String getString(String var1);

    public void setString(String var1, String var2) throws AtlassianCoreException;

    public boolean getBoolean(String var1);

    public void setBoolean(String var1, boolean var2) throws AtlassianCoreException;

    public void remove(String var1) throws AtlassianCoreException;
}

