/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.license.entity;

public enum LicenseError {
    EXPIRED,
    TYPE_MISMATCH,
    USER_MISMATCH,
    EDITION_MISMATCH,
    ROLE_EXCEEDED,
    ROLE_UNDEFINED,
    VERSION_MISMATCH;


    public String toString() {
        return "LicenseError<" + this.name() + ">";
    }
}

