/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordConstraint
 *  com.atlassian.crowd.embedded.api.PasswordScore
 *  com.atlassian.crowd.embedded.api.PasswordScoreService
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.ValidatePasswordRequest
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.embedded.api.PasswordConstraint;
import com.atlassian.crowd.embedded.api.PasswordScore;
import com.atlassian.crowd.embedded.api.PasswordScoreService;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.ValidatePasswordRequest;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class PasswordScoreConstraint
implements PasswordConstraint {
    private static final String WORD_BOUNDARY_REGEX = "(?=\\b)";
    private final PasswordScore minimumPasswordScore;
    private final PasswordScoreService passwordScoreService;

    public PasswordScoreConstraint(PasswordScore passwordScore, PasswordScoreService passwordScoreService) {
        this.minimumPasswordScore = (PasswordScore)Preconditions.checkNotNull((Object)passwordScore);
        this.passwordScoreService = (PasswordScoreService)Preconditions.checkNotNull((Object)passwordScoreService);
    }

    public boolean validate(ValidatePasswordRequest request) {
        PasswordScore actualPasswordScore = this.passwordScoreService.getPasswordScore(request.getPassword(), this.getUserInfo(request));
        return actualPasswordScore.isAtLeast(this.minimumPasswordScore);
    }

    public PasswordScore getMinimumPasswordScore() {
        return this.minimumPasswordScore;
    }

    public String toString() {
        return "PasswordScoreConstraint(minimum=" + this.minimumPasswordScore + ")";
    }

    private Collection<String> getUserInfo(ValidatePasswordRequest request) {
        User user = request.getUser();
        return ImmutableList.builder().addAll(this.splitOnWordBoundary(user.getName())).addAll(this.splitOnWordBoundary(user.getDisplayName())).addAll(this.splitOnWordBoundary(user.getEmailAddress())).build();
    }

    private List<String> splitOnWordBoundary(String string) {
        if (StringUtils.isBlank((CharSequence)string)) {
            return ImmutableList.of();
        }
        return ImmutableList.copyOf((Object[])string.split(WORD_BOUNDARY_REGEX));
    }
}

