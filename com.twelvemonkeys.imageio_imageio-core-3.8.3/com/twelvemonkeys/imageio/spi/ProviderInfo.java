/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.spi;

import com.twelvemonkeys.lang.Validate;

public class ProviderInfo {
    private final String title;
    private final String vendorName;
    private final String version;

    public ProviderInfo(Package package_) {
        Validate.notNull((Object)package_, (String)"package");
        String string = package_.getImplementationTitle();
        this.title = string != null ? string : package_.getName();
        String string2 = package_.getImplementationVendor();
        this.vendorName = string2 != null ? string2 : ProviderInfo.fakeVendor(package_);
        String string3 = package_.getImplementationVersion();
        this.version = string3 != null ? string3 : this.fakeVersion(package_);
    }

    private static String fakeVendor(Package package_) {
        String string = package_.getName();
        return string.startsWith("com.twelvemonkeys") ? "TwelveMonkeys" : string;
    }

    private String fakeVersion(Package package_) {
        String string = package_.getName();
        return string.startsWith("com.twelvemonkeys") ? "DEV" : "Unspecified";
    }

    final String getImplementationTitle() {
        return this.title;
    }

    public final String getVendorName() {
        return this.vendorName;
    }

    public final String getVersion() {
        return this.version;
    }

    public String toString() {
        return this.title + ", " + this.version + " by " + this.vendorName;
    }
}

