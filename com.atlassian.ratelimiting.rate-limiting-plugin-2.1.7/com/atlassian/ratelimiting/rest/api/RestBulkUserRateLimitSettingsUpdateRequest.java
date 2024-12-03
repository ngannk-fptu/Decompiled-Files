/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.rest.api;

import com.atlassian.ratelimiting.rest.api.RestTokenBucketSettings;
import java.util.List;

public class RestBulkUserRateLimitSettingsUpdateRequest {
    private RestTokenBucketSettings tokenBucketSettings;
    private List<String> userIds;

    public static RestBulkUserRateLimitSettingsUpdateRequestBuilder builder() {
        return new RestBulkUserRateLimitSettingsUpdateRequestBuilder();
    }

    public RestTokenBucketSettings getTokenBucketSettings() {
        return this.tokenBucketSettings;
    }

    public List<String> getUserIds() {
        return this.userIds;
    }

    public void setTokenBucketSettings(RestTokenBucketSettings tokenBucketSettings) {
        this.tokenBucketSettings = tokenBucketSettings;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestBulkUserRateLimitSettingsUpdateRequest)) {
            return false;
        }
        RestBulkUserRateLimitSettingsUpdateRequest other = (RestBulkUserRateLimitSettingsUpdateRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        RestTokenBucketSettings this$tokenBucketSettings = this.getTokenBucketSettings();
        RestTokenBucketSettings other$tokenBucketSettings = other.getTokenBucketSettings();
        if (this$tokenBucketSettings == null ? other$tokenBucketSettings != null : !((Object)this$tokenBucketSettings).equals(other$tokenBucketSettings)) {
            return false;
        }
        List<String> this$userIds = this.getUserIds();
        List<String> other$userIds = other.getUserIds();
        return !(this$userIds == null ? other$userIds != null : !((Object)this$userIds).equals(other$userIds));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestBulkUserRateLimitSettingsUpdateRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        RestTokenBucketSettings $tokenBucketSettings = this.getTokenBucketSettings();
        result = result * 59 + ($tokenBucketSettings == null ? 43 : ((Object)$tokenBucketSettings).hashCode());
        List<String> $userIds = this.getUserIds();
        result = result * 59 + ($userIds == null ? 43 : ((Object)$userIds).hashCode());
        return result;
    }

    public String toString() {
        return "RestBulkUserRateLimitSettingsUpdateRequest(tokenBucketSettings=" + this.getTokenBucketSettings() + ", userIds=" + this.getUserIds() + ")";
    }

    public RestBulkUserRateLimitSettingsUpdateRequest(RestTokenBucketSettings tokenBucketSettings, List<String> userIds) {
        this.tokenBucketSettings = tokenBucketSettings;
        this.userIds = userIds;
    }

    public RestBulkUserRateLimitSettingsUpdateRequest() {
    }

    public static class RestBulkUserRateLimitSettingsUpdateRequestBuilder {
        private RestTokenBucketSettings tokenBucketSettings;
        private List<String> userIds;

        RestBulkUserRateLimitSettingsUpdateRequestBuilder() {
        }

        public RestBulkUserRateLimitSettingsUpdateRequestBuilder tokenBucketSettings(RestTokenBucketSettings tokenBucketSettings) {
            this.tokenBucketSettings = tokenBucketSettings;
            return this;
        }

        public RestBulkUserRateLimitSettingsUpdateRequestBuilder userIds(List<String> userIds) {
            this.userIds = userIds;
            return this;
        }

        public RestBulkUserRateLimitSettingsUpdateRequest build() {
            return new RestBulkUserRateLimitSettingsUpdateRequest(this.tokenBucketSettings, this.userIds);
        }

        public String toString() {
            return "RestBulkUserRateLimitSettingsUpdateRequest.RestBulkUserRateLimitSettingsUpdateRequestBuilder(tokenBucketSettings=" + this.tokenBucketSettings + ", userIds=" + this.userIds + ")";
        }
    }
}

