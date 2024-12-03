/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.AtlassianLicense
 *  com.atlassian.extras.api.LicenseManager
 *  com.atlassian.extras.common.LicenseException
 *  com.atlassian.extras.decoder.api.LicenseDecoder
 */
package com.atlassian.extras.core;

import com.atlassian.extras.api.AtlassianLicense;
import com.atlassian.extras.api.LicenseManager;
import com.atlassian.extras.common.LicenseException;
import com.atlassian.extras.core.AtlassianLicenseFactory;
import com.atlassian.extras.decoder.api.LicenseDecoder;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultLicenseManager
implements LicenseManager {
    private final Map<String, AtlassianLicense> licenses = new ConcurrentHashMap<String, AtlassianLicense>(1);
    private final LicenseDecoder licenseDecoder;
    private final AtlassianLicenseFactory atlassianLicenseFactory;

    public DefaultLicenseManager(LicenseDecoder licenseDecoder, AtlassianLicenseFactory atlassianLicenseFactory) {
        if (licenseDecoder == null) {
            throw new IllegalArgumentException("licenseDecoder must NOT be null!");
        }
        if (atlassianLicenseFactory == null) {
            throw new IllegalArgumentException("atlassianLicenseFactory must NOT be null!");
        }
        this.atlassianLicenseFactory = atlassianLicenseFactory;
        this.licenseDecoder = licenseDecoder;
    }

    public AtlassianLicense getLicense(String licenseString) {
        if (licenseString == null) {
            throw new IllegalArgumentException("licenseString must NOT be null");
        }
        try {
            return this.getAtlassianLicense(licenseString);
        }
        catch (LicenseException e) {
            throw e;
        }
        catch (RuntimeException t) {
            throw new LicenseException((Throwable)t);
        }
    }

    private AtlassianLicense getAtlassianLicense(String licenseString) {
        AtlassianLicense license = this.licenses.get(licenseString);
        if (license == null) {
            license = this.decodeLicense(licenseString);
            if (license == null) {
                throw new LicenseException("Could not decode license <" + licenseString + ">, decoding returned a null Atlassian license object");
            }
            this.licenses.put(licenseString, license);
        }
        return license;
    }

    private AtlassianLicense decodeLicense(String licenseString) {
        return this.atlassianLicenseFactory.getLicense(this.licenseDecoder.decode(licenseString));
    }

    Map<String, AtlassianLicense> getLicenses() {
        return Collections.unmodifiableMap(this.licenses);
    }

    LicenseDecoder getLicenseDecoder() {
        return this.licenseDecoder;
    }

    AtlassianLicenseFactory getAtlassianLicenseFactory() {
        return this.atlassianLicenseFactory;
    }

    public void clear() {
        this.licenses.clear();
    }
}

