/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.common.LicenseException
 */
package com.atlassian.extras.decoder.api;

import com.atlassian.extras.common.LicenseException;
import com.atlassian.extras.decoder.api.LicenseDecoder;
import com.atlassian.extras.decoder.api.LicenseVerificationException;
import java.util.Properties;

public abstract class AbstractLicenseDecoder
implements LicenseDecoder {
    @Override
    public final Properties decode(String licenseString) throws LicenseVerificationException {
        Properties licenseProperties = this.doDecode(licenseString);
        if (licenseProperties == null) {
            throw new LicenseException("Invalid License - an error occurred when decoding the license");
        }
        licenseProperties.setProperty("licenseVersion", String.valueOf(this.getLicenseVersion()));
        return licenseProperties;
    }

    protected abstract Properties doDecode(String var1);

    protected abstract int getLicenseVersion();
}

