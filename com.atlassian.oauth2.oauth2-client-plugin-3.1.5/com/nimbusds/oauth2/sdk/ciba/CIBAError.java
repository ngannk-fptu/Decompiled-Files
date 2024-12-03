/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.ciba;

import com.nimbusds.oauth2.sdk.ErrorObject;

public final class CIBAError {
    public static final ErrorObject EXPIRED_LOGIN_HINT_TOKEN = new ErrorObject("expired_login_hint_token", "Expired login_hint_token", 400);
    public static final ErrorObject UNKNOWN_USER_ID = new ErrorObject("unknown_user_id", "Unknown user ID", 400);
    public static final ErrorObject MISSING_USER_CODE = new ErrorObject("missing_user_code", "Required user_code is missing", 400);
    public static final ErrorObject INVALID_USER_CODE = new ErrorObject("invalid_user_code", "Invalid user_code", 400);
    public static final ErrorObject INVALID_BINDING_MESSAGE = new ErrorObject("invalid_binding_message", "Invalid or unacceptable binding_message", 400);
    public static final ErrorObject EXPIRED_TOKEN = new ErrorObject("expired_token", "The auth_req_id has expired", 0);
    public static final ErrorObject TRANSACTION_FAILED = new ErrorObject("transaction_failed", "The transaction failed due to an unexpected condition", 0);

    private CIBAError() {
    }
}

