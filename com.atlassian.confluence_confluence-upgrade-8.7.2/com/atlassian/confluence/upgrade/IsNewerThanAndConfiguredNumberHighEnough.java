/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.confluence.upgrade.BuildNumberUpgradeConstraint;
import com.atlassian.confluence.upgrade.IsNewerThan;

public class IsNewerThanAndConfiguredNumberHighEnough
implements BuildNumberUpgradeConstraint {
    private final boolean applicationConfigPass;
    private final IsNewerThan isNewerThan;

    public IsNewerThanAndConfiguredNumberHighEnough(int applicationConfigBuildNumber, int minApplicationConfig, int constraintBuildNumber) {
        this.applicationConfigPass = applicationConfigBuildNumber >= minApplicationConfig;
        this.isNewerThan = new IsNewerThan(constraintBuildNumber);
    }

    @Override
    public boolean test(int buildNumber) {
        return this.applicationConfigPass && this.isNewerThan.test(buildNumber);
    }

    public String toString() {
        return this.getClass().getName() + "[" + this.isNewerThan.getConstraintBuildNumber() + " > 'buildNumber' && applicationConfigBuildNumberHighEnough (" + this.applicationConfigPass + ")]";
    }
}

