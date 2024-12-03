/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.decoder.api;

import com.atlassian.extras.decoder.api.LicenseVerificationException;
import java.util.Properties;

public interface LicenseDecoder {
    public Properties decode(String var1) throws LicenseVerificationException;

    public boolean canDecode(String var1);
}

