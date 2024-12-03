/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.model.LicenseType;
import java.util.Optional;

public interface LicenseTypes {
    public Iterable<LicenseType> getAllLicenseTypes() throws MpacException;

    public Optional<LicenseType> safeGetByKey(String var1) throws MpacException;
}

