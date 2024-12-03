/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.ratelimiting.requesthandler;

import com.atlassian.ratelimiting.bucket.TokenBucket;
import com.atlassian.sal.api.user.UserKey;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RateLimitResponseHandler {
    public void applyRateLimitingInfo(@Nonnull HttpServletResponse var1, @Nonnull HttpServletRequest var2, @Nonnull UserKey var3, Supplier<Optional<TokenBucket>> var4) throws IOException;

    public void addRateLimitingHeaders(@Nonnull HttpServletResponse var1, @Nonnull UserKey var2, Supplier<Optional<TokenBucket>> var3);

    public static enum RateLimitHeaderOption {
        ENABLED,
        AUTHENTICATED_REQUEST_ONLY,
        DISABLED;

    }
}

