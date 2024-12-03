/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.rest.api;

import com.atlassian.ratelimiting.rest.api.RestTokenBucketSettings;

public class RestAnonymousUserRateLimitSettingsUpdateRequest {
    private RestTokenBucketSettings tokenBucketSettings;

    public static RestAnonymousUserRateLimitSettingsUpdateRequestBuilder builder() {
        return new RestAnonymousUserRateLimitSettingsUpdateRequestBuilder();
    }

    public RestTokenBucketSettings getTokenBucketSettings() {
        return this.tokenBucketSettings;
    }

    public void setTokenBucketSettings(RestTokenBucketSettings tokenBucketSettings) {
        this.tokenBucketSettings = tokenBucketSettings;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestAnonymousUserRateLimitSettingsUpdateRequest)) {
            return false;
        }
        RestAnonymousUserRateLimitSettingsUpdateRequest other = (RestAnonymousUserRateLimitSettingsUpdateRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        RestTokenBucketSettings this$tokenBucketSettings = this.getTokenBucketSettings();
        RestTokenBucketSettings other$tokenBucketSettings = other.getTokenBucketSettings();
        return !(this$tokenBucketSettings == null ? other$tokenBucketSettings != null : !((Object)this$tokenBucketSettings).equals(other$tokenBucketSettings));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestAnonymousUserRateLimitSettingsUpdateRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        RestTokenBucketSettings $tokenBucketSettings = this.getTokenBucketSettings();
        result = result * 59 + ($tokenBucketSettings == null ? 43 : ((Object)$tokenBucketSettings).hashCode());
        return result;
    }

    public String toString() {
        return "RestAnonymousUserRateLimitSettingsUpdateRequest(tokenBucketSettings=" + this.getTokenBucketSettings() + ")";
    }

    public RestAnonymousUserRateLimitSettingsUpdateRequest(RestTokenBucketSettings tokenBucketSettings) {
        this.tokenBucketSettings = tokenBucketSettings;
    }

    public RestAnonymousUserRateLimitSettingsUpdateRequest() {
    }

    public static class RestAnonymousUserRateLimitSettingsUpdateRequestBuilder {
        private RestTokenBucketSettings tokenBucketSettings;

        RestAnonymousUserRateLimitSettingsUpdateRequestBuilder() {
        }

        public RestAnonymousUserRateLimitSettingsUpdateRequestBuilder tokenBucketSettings(RestTokenBucketSettings tokenBucketSettings) {
            this.tokenBucketSettings = tokenBucketSettings;
            return this;
        }

        public RestAnonymousUserRateLimitSettingsUpdateRequest build() {
            return new RestAnonymousUserRateLimitSettingsUpdateRequest(this.tokenBucketSettings);
        }

        public String toString() {
            return "RestAnonymousUserRateLimitSettingsUpdateRequest.RestAnonymousUserRateLimitSettingsUpdateRequestBuilder(tokenBucketSettings=" + this.tokenBucketSettings + ")";
        }
    }
}

