/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.decoder.api;

import com.atlassian.extras.decoder.api.LicenseDecoder;
import com.atlassian.extras.decoder.api.LicenseDecoderNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public final class DelegatingLicenseDecoder
implements LicenseDecoder {
    private final List<LicenseDecoder> licenseDecoders;

    public DelegatingLicenseDecoder(List<LicenseDecoder> licenseDecoders) {
        this.licenseDecoders = Collections.unmodifiableList(new ArrayList<LicenseDecoder>(licenseDecoders));
    }

    @Override
    public boolean canDecode(String licenseString) {
        for (LicenseDecoder decoder : this.licenseDecoders) {
            if (!decoder.canDecode(licenseString)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Properties decode(String licenseString) {
        for (LicenseDecoder licenseDecoder : this.licenseDecoders) {
            if (!licenseDecoder.canDecode(licenseString)) continue;
            return licenseDecoder.decode(licenseString);
        }
        throw new LicenseDecoderNotFoundException(licenseString, this.licenseDecoders);
    }
}

