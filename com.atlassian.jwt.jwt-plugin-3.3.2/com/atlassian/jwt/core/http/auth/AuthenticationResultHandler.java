/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.core.http.auth;

import com.atlassian.jwt.Jwt;
import java.security.Principal;

public interface AuthenticationResultHandler<R, S> {
    public S createAndSendInternalError(Exception var1, R var2, String var3);

    public S createAndSendBadRequestError(Exception var1, R var2, String var3);

    public S createAndSendUnauthorisedFailure(Exception var1, R var2, String var3);

    public S createAndSendForbiddenError(Exception var1, R var2);

    public S success(String var1, Principal var2, Jwt var3);
}

