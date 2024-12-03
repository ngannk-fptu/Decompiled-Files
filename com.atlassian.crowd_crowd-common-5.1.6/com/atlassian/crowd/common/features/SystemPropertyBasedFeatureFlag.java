/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.FeatureFlag
 *  com.atlassian.crowd.embedded.spi.DcLicenseChecker
 */
package com.atlassian.crowd.common.features;

import com.atlassian.crowd.common.properties.BooleanSystemProperty;
import com.atlassian.crowd.embedded.api.FeatureFlag;
import com.atlassian.crowd.embedded.spi.DcLicenseChecker;

public abstract class SystemPropertyBasedFeatureFlag
implements FeatureFlag {
    private final BooleanSystemProperty property;
    private final DcLicenseChecker dcLicenseChecker;

    public SystemPropertyBasedFeatureFlag(BooleanSystemProperty property, DcLicenseChecker dcLicenseChecker) {
        this.property = property;
        this.dcLicenseChecker = dcLicenseChecker;
    }

    public boolean isEnabled() {
        return (Boolean)this.property.getValue() != false && this.dcLicenseChecker.isDcLicense();
    }
}

