/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.conditions;

import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.conditions.BuildNumberCondition;
import java.util.Map;

public class IsPriorToBuildNumber
extends BuildNumberCondition {
    public IsPriorToBuildNumber(UpmHostApplicationInformation appInfo) {
        super(appInfo);
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        if (this.specifiedBuildNumber == null) {
            return false;
        }
        return this.actualBuildNumber < this.specifiedBuildNumber;
    }
}

