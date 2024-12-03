/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.common.LicenseException
 */
package com.atlassian.extras.decoder.api;

import com.atlassian.extras.common.LicenseException;
import com.atlassian.extras.decoder.api.LicenseDecoder;
import java.util.List;

public class LicenseDecoderNotFoundException
extends LicenseException {
    private final String licenseString;
    private final List<LicenseDecoder> licenseDecoders;

    public LicenseDecoderNotFoundException(String licenseString, List<LicenseDecoder> licenseDecoders) {
        this.licenseString = licenseString;
        this.licenseDecoders = licenseDecoders;
    }

    public String getMessage() {
        return "Could not find any valid decoders in " + this.licenseDecoders + " for license string <" + this.licenseString + ">";
    }
}

