/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.gatekeeper.conditions;

import com.atlassian.confluence.plugins.gatekeeper.license.AddonLicenseManager;
import com.atlassian.confluence.plugins.gatekeeper.license.LicenseInfo;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class IsLicensedForProductCondition
implements Condition {
    private final AddonLicenseManager licenseManager;
    private LicenseCheck check;

    @Autowired
    public IsLicensedForProductCondition(AddonLicenseManager licenseManager) {
        this.licenseManager = licenseManager;
    }

    public void init(Map<String, String> params) throws PluginParseException {
        this.check = LicenseCheck.valueOf(params.getOrDefault("check", LicenseCheck.DC.name()));
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        LicenseInfo licenseInfo = this.licenseManager.getLicenseInfo();
        return this.check.check(licenseInfo);
    }

    private static enum LicenseCheck {
        DC{

            @Override
            public boolean check(LicenseInfo info) {
                return info.isValid() && info.isDCFeatureLicensed();
            }
        }
        ,
        SERVER{

            @Override
            public boolean check(LicenseInfo info) {
                return info.isValid();
            }
        };


        public abstract boolean check(LicenseInfo var1);
    }
}

