/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.persistence;

import com.atlassian.confluence.plugin.persistence.PluginData;
import com.atlassian.confluence.plugin.persistence.PluginDataWithoutBinary;
import java.util.Iterator;

public interface PluginDataDao {
    public PluginData getPluginData(String var1);

    public PluginDataWithoutBinary getPluginDataWithoutBinary(String var1);

    public Iterator<PluginData> getAllPluginData();

    public Iterator<PluginDataWithoutBinary> getAllPluginDataWithoutBinary();

    public void saveOrUpdate(PluginData var1);

    public void remove(String var1);

    public boolean pluginDataExists(String var1);
}

