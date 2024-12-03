/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.api;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.PasswordScore;
import java.util.Collection;

public interface PasswordScoreService {
    public PasswordScore getPasswordScore(PasswordCredential var1, Collection<String> var2);
}

