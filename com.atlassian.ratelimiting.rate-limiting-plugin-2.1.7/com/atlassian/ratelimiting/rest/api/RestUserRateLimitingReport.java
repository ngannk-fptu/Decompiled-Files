/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.ratelimiting.rest.api;

import com.atlassian.ratelimiting.history.UserRateLimitingReport;
import com.atlassian.ratelimiting.rest.api.RestApplicationUser;
import com.atlassian.sal.api.user.UserProfile;
import java.time.ZoneOffset;
import java.util.Date;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public class RestUserRateLimitingReport {
    private RestApplicationUser user;
    private Long rejectCount;
    private Date lastRejectTime;
    private Boolean hasExemption;

    public RestUserRateLimitingReport(UserRateLimitingReport userRateLimitingReport, UserProfile userProfile) {
        this.user = new RestApplicationUser(userProfile);
        this.rejectCount = userRateLimitingReport.getRejectCount();
        this.lastRejectTime = Date.from(userRateLimitingReport.getLastRejectedTime().atZone(ZoneOffset.UTC).toInstant());
        this.hasExemption = userRateLimitingReport.isHasExemption();
    }

    public RestApplicationUser getUser() {
        return this.user;
    }

    public Long getRejectCount() {
        return this.rejectCount;
    }

    public Date getLastRejectTime() {
        return this.lastRejectTime;
    }

    public Boolean getHasExemption() {
        return this.hasExemption;
    }

    public void setUser(RestApplicationUser user) {
        this.user = user;
    }

    public void setRejectCount(Long rejectCount) {
        this.rejectCount = rejectCount;
    }

    public void setLastRejectTime(Date lastRejectTime) {
        this.lastRejectTime = lastRejectTime;
    }

    public void setHasExemption(Boolean hasExemption) {
        this.hasExemption = hasExemption;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestUserRateLimitingReport)) {
            return false;
        }
        RestUserRateLimitingReport other = (RestUserRateLimitingReport)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$rejectCount = this.getRejectCount();
        Long other$rejectCount = other.getRejectCount();
        if (this$rejectCount == null ? other$rejectCount != null : !((Object)this$rejectCount).equals(other$rejectCount)) {
            return false;
        }
        Boolean this$hasExemption = this.getHasExemption();
        Boolean other$hasExemption = other.getHasExemption();
        if (this$hasExemption == null ? other$hasExemption != null : !((Object)this$hasExemption).equals(other$hasExemption)) {
            return false;
        }
        RestApplicationUser this$user = this.getUser();
        RestApplicationUser other$user = other.getUser();
        if (this$user == null ? other$user != null : !((Object)this$user).equals(other$user)) {
            return false;
        }
        Date this$lastRejectTime = this.getLastRejectTime();
        Date other$lastRejectTime = other.getLastRejectTime();
        return !(this$lastRejectTime == null ? other$lastRejectTime != null : !((Object)this$lastRejectTime).equals(other$lastRejectTime));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestUserRateLimitingReport;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $rejectCount = this.getRejectCount();
        result = result * 59 + ($rejectCount == null ? 43 : ((Object)$rejectCount).hashCode());
        Boolean $hasExemption = this.getHasExemption();
        result = result * 59 + ($hasExemption == null ? 43 : ((Object)$hasExemption).hashCode());
        RestApplicationUser $user = this.getUser();
        result = result * 59 + ($user == null ? 43 : ((Object)$user).hashCode());
        Date $lastRejectTime = this.getLastRejectTime();
        result = result * 59 + ($lastRejectTime == null ? 43 : ((Object)$lastRejectTime).hashCode());
        return result;
    }

    public String toString() {
        return "RestUserRateLimitingReport(user=" + this.getUser() + ", rejectCount=" + this.getRejectCount() + ", lastRejectTime=" + this.getLastRejectTime() + ", hasExemption=" + this.getHasExemption() + ")";
    }

    public RestUserRateLimitingReport() {
    }
}

