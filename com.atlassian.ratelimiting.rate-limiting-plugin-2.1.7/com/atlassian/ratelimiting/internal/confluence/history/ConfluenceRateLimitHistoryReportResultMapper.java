/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.ratelimiting.internal.confluence.history;

import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.ratelimiting.dmz.DmzRateLimitSettingsModificationService;
import com.atlassian.ratelimiting.history.RateLimitHistoryReportResultMapper;
import com.atlassian.ratelimiting.history.RateLimitingReportSearchResult;
import com.atlassian.ratelimiting.history.UserRateLimitingReport;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Optional;

public class ConfluenceRateLimitHistoryReportResultMapper
implements RateLimitHistoryReportResultMapper {
    private final UserService userService;
    private final UserAccessor userAccessor;
    private final DmzRateLimitSettingsModificationService rateLimitSettingsModificationService;

    public ConfluenceRateLimitHistoryReportResultMapper(UserService userService, DmzRateLimitSettingsModificationService rateLimitSettingsModificationService, UserAccessor userAccessor) {
        this.userService = userService;
        this.rateLimitSettingsModificationService = rateLimitSettingsModificationService;
        this.userAccessor = userAccessor;
    }

    @Override
    public Optional<RateLimitingReportSearchResult> apply(UserRateLimitingReport userRateLimitingReport) {
        Optional<UserProfile> userProfile = this.userService.getUser(userRateLimitingReport.getUser());
        if (!userProfile.isPresent() || this.userAccessor.isDeactivated(userProfile.get().getUsername())) {
            this.rateLimitSettingsModificationService.delete(userRateLimitingReport.getUser());
            return Optional.empty();
        }
        return Optional.of(new RateLimitingReportSearchResult(userProfile.get(), userRateLimitingReport));
    }
}

