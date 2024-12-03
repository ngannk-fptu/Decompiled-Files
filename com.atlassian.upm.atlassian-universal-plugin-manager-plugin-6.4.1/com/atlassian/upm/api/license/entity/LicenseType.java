/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.license.entity;

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
        return "LicenseType<" + this.name() + ">";
    }

    public boolean isPaidType() {
        switch (this) {
            case ACADEMIC: 
            case COMMERCIAL: 
            case STARTER: {
                return true;
            }
        }
        return false;
    }
}

