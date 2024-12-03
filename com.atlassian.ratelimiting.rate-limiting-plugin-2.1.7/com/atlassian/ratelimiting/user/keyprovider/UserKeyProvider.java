/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.ratelimiting.user.keyprovider;

import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import java.util.function.Function;

public interface UserKeyProvider
extends Function<String, Optional<UserKey>> {
}

