/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.api.authorization;

import com.atlassian.oauth2.provider.api.authorization.Authorization;
import com.atlassian.oauth2.provider.api.authorization.TokenResponseErrorDescription;
import java.util.function.Consumer;
import java.util.function.Function;

public class AuthorizationFlowResult {
    private final Authorization authorization;
    private final TokenResponseErrorDescription errorReason;

    public static AuthorizationFlowResult success(Authorization authorization) {
        return new AuthorizationFlowResult(authorization, null);
    }

    public static AuthorizationFlowResult failed(TokenResponseErrorDescription errorReason) {
        return new AuthorizationFlowResult(null, errorReason);
    }

    private AuthorizationFlowResult(Authorization authorization, TokenResponseErrorDescription errorReason) {
        this.authorization = authorization;
        this.errorReason = errorReason;
    }

    public <T> T fold(Function<Authorization, T> ifSuccess, Function<TokenResponseErrorDescription, T> ifFailure) {
        if (this.authorization == null) {
            return ifFailure.apply(this.errorReason);
        }
        return ifSuccess.apply(this.authorization);
    }

    public void ifSuccess(Consumer<Authorization> ifSuccess) {
        if (this.authorization == null) {
            return;
        }
        ifSuccess.accept(this.authorization);
    }

    public void ifFailure(Consumer<TokenResponseErrorDescription> ifFailure) {
        if (this.errorReason == null) {
            return;
        }
        ifFailure.accept(this.errorReason);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AuthorizationFlowResult)) {
            return false;
        }
        AuthorizationFlowResult other = (AuthorizationFlowResult)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Authorization this$authorization = this.authorization;
        Authorization other$authorization = other.authorization;
        if (this$authorization == null ? other$authorization != null : !this$authorization.equals(other$authorization)) {
            return false;
        }
        TokenResponseErrorDescription this$errorReason = this.errorReason;
        TokenResponseErrorDescription other$errorReason = other.errorReason;
        return !(this$errorReason == null ? other$errorReason != null : !((Object)((Object)this$errorReason)).equals((Object)other$errorReason));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AuthorizationFlowResult;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Authorization $authorization = this.authorization;
        result = result * 59 + ($authorization == null ? 43 : $authorization.hashCode());
        TokenResponseErrorDescription $errorReason = this.errorReason;
        result = result * 59 + ($errorReason == null ? 43 : ((Object)((Object)$errorReason)).hashCode());
        return result;
    }

    public String toString() {
        return "AuthorizationFlowResult(authorization=" + this.authorization + ", errorReason=" + (Object)((Object)this.errorReason) + ")";
    }
}

