/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  com.google.common.base.Preconditions
 */
package com.atlassian.upm.license.compatibility;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.upm.license.compatibility.PluginLicenseManagerAccessor;
import com.google.common.base.Preconditions;
import java.util.Map;

public class UpmPluginLicenseManagerNotResolvedCondition
implements Condition {
    private final PluginLicenseManagerAccessor accessor;

    public UpmPluginLicenseManagerNotResolvedCondition(PluginLicenseManagerAccessor accessor) {
        this.accessor = (PluginLicenseManagerAccessor)Preconditions.checkNotNull((Object)accessor, (Object)"accessor");
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> stringObjectMap) {
        return !this.accessor.isUpmPluginLicenseManagerResolved();
    }
}

