/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.api.authorization.dao;

import com.atlassian.oauth2.provider.api.authorization.Authorization;
import java.time.Duration;
import java.util.Optional;

public interface AuthorizationDao {
    public Authorization save(Authorization var1);

    public Optional<Authorization> removeByCode(String var1);

    public Optional<Authorization> findByCode(String var1);

    public void removeExpiredAuthorizationsAfter(Duration var1);
}

