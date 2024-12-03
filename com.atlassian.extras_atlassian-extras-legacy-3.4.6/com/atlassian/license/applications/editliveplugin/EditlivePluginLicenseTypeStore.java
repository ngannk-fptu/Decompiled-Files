/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseType
 */
package com.atlassian.license.applications.editliveplugin;

import com.atlassian.license.DefaultLicenseType;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;
import java.util.Collection;

@Deprecated
public class EditlivePluginLicenseTypeStore
extends LicenseTypeStore {
    public static LicenseType EDITLIVE_ACADEMIC = new DefaultLicenseType(700, "EditLive!: Academic", false, false, com.atlassian.extras.api.LicenseType.ACADEMIC.name());
    public static LicenseType EDITLIVE_EVALUATION = new DefaultLicenseType(701, "EditLive!: Evaluation", true, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType EDITLIVE_NON_PROFIT = new DefaultLicenseType(702, "EditLive!: Non-Profit / Open Source", false, false, com.atlassian.extras.api.LicenseType.NON_PROFIT.name());
    public static LicenseType EDITLIVE_FULL_LICENSE = new DefaultLicenseType(703, "EditLive!: Commercial", false, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static final String publicKeyFileName = "com/atlassian/editlive/publickey.byte";
    private static final String privateKeyFileName = "editlive/editlive.byte";

    public EditlivePluginLicenseTypeStore() {
        this.applicationLicenseTypes.add(EDITLIVE_ACADEMIC);
        this.applicationLicenseTypes.add(EDITLIVE_EVALUATION);
        this.applicationLicenseTypes.add(EDITLIVE_NON_PROFIT);
        this.applicationLicenseTypes.add(EDITLIVE_FULL_LICENSE);
    }

    @Override
    public Collection getAllLicenses() {
        return this.applicationLicenseTypes;
    }

    @Override
    public String getPublicKeyFileName() {
        return publicKeyFileName;
    }

    @Override
    public String getPrivateKeyFileName() {
        return privateKeyFileName;
    }
}

