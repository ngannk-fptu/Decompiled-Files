/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus.dm;

import com.atlassian.sisyphus.dm.ScannedPlugin;
import com.atlassian.sisyphus.dm.ScannedPropertySet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PropScanResult {
    private Date scannedDate;
    private List<ScannedPlugin> plugins = new ArrayList<ScannedPlugin>();
    private List<ScannedPropertySet> scannedProperties = new ArrayList<ScannedPropertySet>();

    public Date getScannedDate() {
        return this.scannedDate;
    }

    public void setScannedDate(Date scannedDate) {
        this.scannedDate = scannedDate;
    }

    public List<ScannedPlugin> getPlugins() {
        return this.plugins;
    }

    public List<ScannedPropertySet> getScannedProperties() {
        return this.scannedProperties;
    }

    public void addProperties(ScannedPropertySet properties) {
        this.scannedProperties.add(properties);
    }

    public void addPlugin(ScannedPlugin plugin) {
        this.plugins.add(plugin);
    }
}

