/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sisyphus.plugin.compatiblity;

import com.atlassian.sisyphus.dm.PropScanResult;
import com.atlassian.sisyphus.dm.ScannedProperty;
import com.atlassian.sisyphus.dm.ScannedPropertySet;
import com.atlassian.sisyphus.plugin.compatiblity.CompatibilityData;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductData {
    String productName;
    String productVersion;
    String buildNumber;
    private static final Logger log = LoggerFactory.getLogger(CompatibilityData.class);

    public ProductData(PropScanResult propScanResult) {
        if (null == propScanResult) {
            return;
        }
        List<ScannedPropertySet> scannedProperties = propScanResult.getScannedProperties();
        if (scannedProperties != null) {
            for (ScannedPropertySet scannedProperty : scannedProperties) {
                if (!scannedProperty.getTitle().equals("title.instanceinfo")) continue;
                for (ScannedProperty property : scannedProperty.getProperties()) {
                    String pname = property.getName();
                    if (pname.equals("info.build.number")) {
                        this.buildNumber = property.getValue();
                        continue;
                    }
                    if (pname.equals("info.product.name")) {
                        this.productName = property.getValue();
                        continue;
                    }
                    if (!pname.equals("info.product.version")) continue;
                    this.productVersion = property.getValue();
                }
                break;
            }
        } else {
            log.error("No properties/More than one found for instance info");
        }
    }

    public String getBuildNumber() {
        return this.buildNumber;
    }

    public String getProductVersion() {
        return this.productVersion;
    }

    public String getProductName() {
        return this.productName;
    }
}

