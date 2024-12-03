/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.ratelimiting.history;

import com.atlassian.ratelimiting.history.UserRateLimitingReport;
import com.atlassian.sal.api.user.UserProfile;

public class RateLimitingReportSearchResult {
    private final UserProfile userProfile;
    private final UserRateLimitingReport userRateLimitingReport;

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RateLimitingReportSearchResult)) {
            return false;
        }
        RateLimitingReportSearchResult other = (RateLimitingReportSearchResult)o;
        if (!other.canEqual(this)) {
            return false;
        }
        UserProfile this$userProfile = this.getUserProfile();
        UserProfile other$userProfile = other.getUserProfile();
        if (this$userProfile == null ? other$userProfile != null : !this$userProfile.equals(other$userProfile)) {
            return false;
        }
        UserRateLimitingReport this$userRateLimitingReport = this.getUserRateLimitingReport();
        UserRateLimitingReport other$userRateLimitingReport = other.getUserRateLimitingReport();
        return !(this$userRateLimitingReport == null ? other$userRateLimitingReport != null : !((Object)this$userRateLimitingReport).equals(other$userRateLimitingReport));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RateLimitingReportSearchResult;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        UserProfile $userProfile = this.getUserProfile();
        result = result * 59 + ($userProfile == null ? 43 : $userProfile.hashCode());
        UserRateLimitingReport $userRateLimitingReport = this.getUserRateLimitingReport();
        result = result * 59 + ($userRateLimitingReport == null ? 43 : ((Object)$userRateLimitingReport).hashCode());
        return result;
    }

    public String toString() {
        return "RateLimitingReportSearchResult(userProfile=" + this.getUserProfile() + ", userRateLimitingReport=" + this.getUserRateLimitingReport() + ")";
    }

    public RateLimitingReportSearchResult(UserProfile userProfile, UserRateLimitingReport userRateLimitingReport) {
        this.userProfile = userProfile;
        this.userRateLimitingReport = userRateLimitingReport;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public UserRateLimitingReport getUserRateLimitingReport() {
        return this.userRateLimitingReport;
    }
}

