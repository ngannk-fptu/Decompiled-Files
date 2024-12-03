/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.portfolioanalyzer.model;

import lombok.Generated;

public final class LinkWithSourceSpaceKey {
    private final String lowerDestPageTitle;
    private final String lowerDestSpaceKey;
    private final String sourceSpaceKey;

    @Generated
    public LinkWithSourceSpaceKey(String lowerDestPageTitle, String lowerDestSpaceKey, String sourceSpaceKey) {
        this.lowerDestPageTitle = lowerDestPageTitle;
        this.lowerDestSpaceKey = lowerDestSpaceKey;
        this.sourceSpaceKey = sourceSpaceKey;
    }

    @Generated
    public String getLowerDestPageTitle() {
        return this.lowerDestPageTitle;
    }

    @Generated
    public String getLowerDestSpaceKey() {
        return this.lowerDestSpaceKey;
    }

    @Generated
    public String getSourceSpaceKey() {
        return this.sourceSpaceKey;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LinkWithSourceSpaceKey)) {
            return false;
        }
        LinkWithSourceSpaceKey other = (LinkWithSourceSpaceKey)o;
        String this$lowerDestPageTitle = this.getLowerDestPageTitle();
        String other$lowerDestPageTitle = other.getLowerDestPageTitle();
        if (this$lowerDestPageTitle == null ? other$lowerDestPageTitle != null : !this$lowerDestPageTitle.equals(other$lowerDestPageTitle)) {
            return false;
        }
        String this$lowerDestSpaceKey = this.getLowerDestSpaceKey();
        String other$lowerDestSpaceKey = other.getLowerDestSpaceKey();
        if (this$lowerDestSpaceKey == null ? other$lowerDestSpaceKey != null : !this$lowerDestSpaceKey.equals(other$lowerDestSpaceKey)) {
            return false;
        }
        String this$sourceSpaceKey = this.getSourceSpaceKey();
        String other$sourceSpaceKey = other.getSourceSpaceKey();
        return !(this$sourceSpaceKey == null ? other$sourceSpaceKey != null : !this$sourceSpaceKey.equals(other$sourceSpaceKey));
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $lowerDestPageTitle = this.getLowerDestPageTitle();
        result = result * 59 + ($lowerDestPageTitle == null ? 43 : $lowerDestPageTitle.hashCode());
        String $lowerDestSpaceKey = this.getLowerDestSpaceKey();
        result = result * 59 + ($lowerDestSpaceKey == null ? 43 : $lowerDestSpaceKey.hashCode());
        String $sourceSpaceKey = this.getSourceSpaceKey();
        result = result * 59 + ($sourceSpaceKey == null ? 43 : $sourceSpaceKey.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "LinkWithSourceSpaceKey(lowerDestPageTitle=" + this.getLowerDestPageTitle() + ", lowerDestSpaceKey=" + this.getLowerDestSpaceKey() + ", sourceSpaceKey=" + this.getSourceSpaceKey() + ")";
    }
}

