/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.user.UserKeyService
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.ratelimiting.internal.jira.user.keyprovider;

import com.atlassian.jira.user.UserKeyService;
import com.atlassian.ratelimiting.user.keyprovider.UserKeyProvider;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;

public class JiraUserKeyProvider
implements UserKeyProvider {
    private final UserKeyService userKeyService;

    public JiraUserKeyProvider(UserKeyService userKeyService) {
        this.userKeyService = userKeyService;
    }

    @Override
    public Optional<UserKey> apply(String username) {
        return Optional.ofNullable(this.userKeyService.getKeyForUsername(username)).map(UserKey::new);
    }
}

