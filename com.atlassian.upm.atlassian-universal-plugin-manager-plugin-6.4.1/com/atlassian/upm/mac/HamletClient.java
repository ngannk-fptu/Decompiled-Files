/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.mac;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.mac.HamletException;
import com.atlassian.upm.mac.HamletLicenseCollection;
import com.atlassian.upm.mac.HamletLicenseInfo;
import java.net.URI;

public interface HamletClient {
    public Option<HamletLicenseCollection> getPurchasedLicensesWithCredentials(String var1, String var2) throws HamletException;

    public Option<HamletLicenseCollection> getPurchasedLicensesWithJwtToken() throws HamletException;

    public Option<HamletLicenseInfo> getPurchasedLicense(String var1) throws HamletException;

    public Option<URI> crossgradeAppLicense(String var1) throws HamletException;
}

