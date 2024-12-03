/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.confluence.upgrade.BuildNumberUpgradeConstraint;

public class IsNewerThan
implements BuildNumberUpgradeConstraint {
    private final int constraintBuildNumber;

    public IsNewerThan(int constraintBuildNumber) {
        this.constraintBuildNumber = constraintBuildNumber;
    }

    public IsNewerThan(String constraintBuildNumber) {
        this.constraintBuildNumber = Integer.parseInt(constraintBuildNumber);
    }

    @Override
    public boolean test(int buildNumber) {
        return this.constraintBuildNumber > buildNumber;
    }

    public int getConstraintBuildNumber() {
        return this.constraintBuildNumber;
    }

    public String toString() {
        return this.getClass().getName() + "[" + this.constraintBuildNumber + " > 'buildNumber']";
    }
}

