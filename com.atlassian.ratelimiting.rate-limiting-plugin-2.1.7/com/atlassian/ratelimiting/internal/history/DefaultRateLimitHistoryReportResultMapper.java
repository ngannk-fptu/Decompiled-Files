/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.ratelimiting.internal.history;

import com.atlassian.ratelimiting.dmz.DmzRateLimitSettingsModificationService;
import com.atlassian.ratelimiting.history.RateLimitHistoryReportResultMapper;
import com.atlassian.ratelimiting.history.RateLimitingReportSearchResult;
import com.atlassian.ratelimiting.history.UserRateLimitingReport;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Optional;

public class DefaultRateLimitHistoryReportResultMapper
implements RateLimitHistoryReportResultMapper {
    private final UserService userService;
    private final DmzRateLimitSettingsModificationService rateLimitSettingsModificationService;

    public DefaultRateLimitHistoryReportResultMapper(UserService userService, DmzRateLimitSettingsModificationService rateLimitSettingsModificationService) {
        this.userService = userService;
        this.rateLimitSettingsModificationService = rateLimitSettingsModificationService;
    }

    @Override
    public Optional<RateLimitingReportSearchResult> apply(UserRateLimitingReport userRateLimitingReport) {
        Optional<UserProfile> userProfile = this.userService.getUser(userRateLimitingReport.getUser());
        if (!userProfile.isPresent()) {
            this.rateLimitSettingsModificationService.delete(userRateLimitingReport.getUser());
        }
        return userProfile.map(up -> new RateLimitingReportSearchResult((UserProfile)up, userRateLimitingReport));
    }
}

