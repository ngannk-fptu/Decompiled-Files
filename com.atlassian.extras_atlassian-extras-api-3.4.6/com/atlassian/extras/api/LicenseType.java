/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.api;

public enum LicenseType {
    ACADEMIC,
    COMMERCIAL,
    COMMUNITY,
    DEMONSTRATION,
    DEVELOPER,
    NON_PROFIT,
    OPEN_SOURCE,
    PERSONAL,
    STARTER,
    HOSTED,
    TESTING;


    public String toString() {
        return "license type <" + this.name() + ">";
    }
}

