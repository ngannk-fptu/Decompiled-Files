/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.api;

public enum LicenseEdition {
    BASIC,
    STANDARD,
    PROFESSIONAL,
    ENTERPRISE,
    UNLIMITED;


    public String toString() {
        return "license edition <" + this.name() + ">";
    }
}

