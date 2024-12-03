/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus.dm;

import com.atlassian.sisyphus.dm.ScannedProperty;
import java.util.ArrayList;
import java.util.List;

public class ScannedPropertySet {
    String title;
    List<ScannedProperty> properties = new ArrayList<ScannedProperty>();

    public void addScannedProperty(ScannedProperty scannedProperty) {
        this.properties.add(scannedProperty);
    }

    public List<ScannedProperty> getProperties() {
        return this.properties;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

