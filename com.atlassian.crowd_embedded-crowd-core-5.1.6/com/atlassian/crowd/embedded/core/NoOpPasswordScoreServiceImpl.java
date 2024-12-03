/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.PasswordScore
 *  com.atlassian.crowd.embedded.api.PasswordScoreService
 */
package com.atlassian.crowd.embedded.core;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.PasswordScore;
import com.atlassian.crowd.embedded.api.PasswordScoreService;
import java.util.Collection;

public class NoOpPasswordScoreServiceImpl
implements PasswordScoreService {
    public PasswordScore getPasswordScore(PasswordCredential passwordCredential, Collection<String> userInfo) {
        return PasswordScore.VERY_STRONG;
    }
}

