/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.common.log.Logger
 *  com.atlassian.extras.common.log.Logger$Log
 *  com.atlassian.extras.common.util.LicenseProperties
 *  com.atlassian.extras.legacy.util.OldLicenseTypeResolver
 *  com.atlassian.license.LicenseException
 *  com.atlassian.license.LicenseType
 *  com.atlassian.license.LicenseTypeStore
 */
package com.atlassian.extras.core.transformer;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.common.log.Logger;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.transformer.LicensePropertiesTransformer;
import com.atlassian.extras.core.transformer.OverriddingLicenseProperties;
import com.atlassian.extras.legacy.util.OldLicenseTypeResolver;
import com.atlassian.license.LicenseException;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;
import java.util.Properties;

public class LicenseEditionPropertyTransformer
implements LicensePropertiesTransformer {
    private static final Logger.Log log = Logger.getInstance(LicenseEditionPropertyTransformer.class);

    @Override
    public LicenseProperties transform(Product product, LicenseProperties properties) {
        String licenseTypeName = properties.getProperty("LicenseEdition");
        if (licenseTypeName == null) {
            LicenseTypeStore typeStore;
            String oldLicenseTypeName = properties.getProperty("LicenseType");
            if (oldLicenseTypeName != null && (typeStore = OldLicenseTypeResolver.getLicenseTypeStore((Product)product)) != null) {
                try {
                    LicenseType oldType = typeStore.getLicenseType(oldLicenseTypeName);
                    if (oldType != null) {
                        Properties prop = new Properties();
                        prop.setProperty("LicenseEdition", oldType.getEdition().name());
                        return new OverriddingLicenseProperties(product, properties, prop);
                    }
                }
                catch (LicenseException e) {
                    log.warn((Object)("License type '" + oldLicenseTypeName + "' can not be resolved. This is a potentially corrupt license."), (Throwable)e);
                    return properties;
                }
            }
            log.warn((Object)("License type '" + oldLicenseTypeName + "' can not be resolved. This is a potentially corrupt license."));
            return properties;
        }
        return properties;
    }
}

