/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.compatibility;

public enum CompatibleLicenseStatus {
    ACTIVE,
    INACTIVE,
    INVALID,
    EVALUATION,
    USER_MISMATCH,
    EXPIRED,
    EVALUATION_EXPIRED,
    MAINTENANCE_EXPIRED,
    REQUIRES_RESTART,
    INCOMPATIBLE_FORMAT,
    INCOMPATIBLE_TYPE;


    public String toString() {
        return "CompatibleLicenseStatus<" + this.name() + ">";
    }
}

