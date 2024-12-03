/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.ErrorObject;

public final class OAuth2Error {
    public static final ErrorObject INVALID_REQUEST = new ErrorObject("invalid_request", "Invalid request", 400);
    public static final ErrorObject UNAUTHORIZED_CLIENT = new ErrorObject("unauthorized_client", "Unauthorized client", 400);
    public static final ErrorObject ACCESS_DENIED = new ErrorObject("access_denied", "Access denied by resource owner or authorization server", 403);
    public static final ErrorObject UNSUPPORTED_RESPONSE_TYPE = new ErrorObject("unsupported_response_type", "Unsupported response type", 400);
    public static final ErrorObject INVALID_SCOPE = new ErrorObject("invalid_scope", "Invalid, unknown or malformed scope", 400);
    public static final ErrorObject SERVER_ERROR = new ErrorObject("server_error", "Unexpected server error", 500);
    public static final ErrorObject TEMPORARILY_UNAVAILABLE = new ErrorObject("temporarily_unavailable", "The authorization server is temporarily unavailable", 503);
    public static final ErrorObject INVALID_CLIENT = new ErrorObject("invalid_client", "Client authentication failed", 401);
    public static final ErrorObject INVALID_GRANT = new ErrorObject("invalid_grant", "Invalid grant", 400);
    public static final ErrorObject UNSUPPORTED_GRANT_TYPE = new ErrorObject("unsupported_grant_type", "Unsupported grant type", 400);
    public static final ErrorObject INVALID_REQUEST_URI = new ErrorObject("invalid_request_uri", "Invalid request URI", 302);
    public static final ErrorObject INVALID_REQUEST_OBJECT = new ErrorObject("invalid_request_object", "Invalid request JWT", 302);
    public static final ErrorObject REQUEST_URI_NOT_SUPPORTED = new ErrorObject("request_uri_not_supported", "Request URI parameter not supported", 302);
    public static final ErrorObject REQUEST_NOT_SUPPORTED = new ErrorObject("request_not_supported", "Request parameter not supported", 302);
    public static final ErrorObject INVALID_RESOURCE = new ErrorObject("invalid_resource", "Invalid or unaccepted resource", 400);
    public static final ErrorObject OVERBROAD_SCOPE = new ErrorObject("overbroad_scope", "Overbroad scope", 400);
    public static final ErrorObject MISSING_TRUST_ANCHOR = new ErrorObject("missing_trust_anchor", "No trusted anchor could be found", 400);
    public static final ErrorObject VALIDATION_FAILED = new ErrorObject("validation_failed", "Trust chain validation failed", 400);

    private OAuth2Error() {
    }
}

