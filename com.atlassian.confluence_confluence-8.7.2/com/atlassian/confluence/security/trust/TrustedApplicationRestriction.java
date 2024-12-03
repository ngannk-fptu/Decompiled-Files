/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.trust;

public abstract class TrustedApplicationRestriction {
    private long id;
    private String restriction;

    public TrustedApplicationRestriction() {
    }

    public TrustedApplicationRestriction(String restriction) {
        this.restriction = restriction;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRestriction() {
        return this.restriction;
    }

    public void setRestriction(String restriction) {
        this.restriction = restriction;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrustedApplicationRestriction)) {
            return false;
        }
        TrustedApplicationRestriction that = (TrustedApplicationRestriction)o;
        return !(this.restriction != null ? !this.restriction.equals(that.restriction) : that.restriction != null);
    }

    public int hashCode() {
        return this.restriction != null ? this.restriction.hashCode() : 0;
    }
}

