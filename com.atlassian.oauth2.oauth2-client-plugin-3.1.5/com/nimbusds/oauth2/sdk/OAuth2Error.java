/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.ErrorObject;

public final class OAuth2Error {
    public static final String INVALID_REQUEST_CODE = "invalid_request";
    public static final ErrorObject INVALID_REQUEST = new ErrorObject("invalid_request", "Invalid request", 400);
    public static final String UNAUTHORIZED_CLIENT_CODE = "unauthorized_client";
    public static final ErrorObject UNAUTHORIZED_CLIENT = new ErrorObject("unauthorized_client", "Unauthorized client", 400);
    public static final String ACCESS_DENIED_CODE = "access_denied";
    public static final ErrorObject ACCESS_DENIED = new ErrorObject("access_denied", "Access denied by resource owner or authorization server", 403);
    public static final String UNSUPPORTED_RESPONSE_TYPE_CODE = "unsupported_response_type";
    public static final ErrorObject UNSUPPORTED_RESPONSE_TYPE = new ErrorObject("unsupported_response_type", "Unsupported response type", 400);
    public static final String INVALID_SCOPE_CODE = "invalid_scope";
    public static final ErrorObject INVALID_SCOPE = new ErrorObject("invalid_scope", "Invalid, unknown or malformed scope", 400);
    public static final String SERVER_ERROR_CODE = "server_error";
    public static final ErrorObject SERVER_ERROR = new ErrorObject("server_error", "Unexpected server error", 500);
    public static final String TEMPORARILY_UNAVAILABLE_CODE = "temporarily_unavailable";
    public static final ErrorObject TEMPORARILY_UNAVAILABLE = new ErrorObject("temporarily_unavailable", "The authorization server is temporarily unavailable", 503);
    public static final String INVALID_CLIENT_CODE = "invalid_client";
    public static final ErrorObject INVALID_CLIENT = new ErrorObject("invalid_client", "Client authentication failed", 401);
    public static final String INVALID_GRANT_CODE = "invalid_grant";
    public static final ErrorObject INVALID_GRANT = new ErrorObject("invalid_grant", "Invalid grant", 400);
    public static final String UNSUPPORTED_GRANT_TYPE_CODE = "unsupported_grant_type";
    public static final ErrorObject UNSUPPORTED_GRANT_TYPE = new ErrorObject("unsupported_grant_type", "Unsupported grant type", 400);
    public static final String INVALID_REQUEST_URI_CODE = "invalid_request_uri";
    public static final ErrorObject INVALID_REQUEST_URI = new ErrorObject("invalid_request_uri", "Invalid request URI", 302);
    public static final String INVALID_REQUEST_OBJECT_CODE = "invalid_request_object";
    public static final ErrorObject INVALID_REQUEST_OBJECT = new ErrorObject("invalid_request_object", "Invalid request JWT", 302);
    public static final String REQUEST_URI_NOT_SUPPORTED_CODE = "request_uri_not_supported";
    public static final ErrorObject REQUEST_URI_NOT_SUPPORTED = new ErrorObject("request_uri_not_supported", "Request URI parameter not supported", 302);
    public static final String REQUEST_NOT_SUPPORTED_CODE = "request_not_supported";
    public static final ErrorObject REQUEST_NOT_SUPPORTED = new ErrorObject("request_not_supported", "Request parameter not supported", 302);
    public static final String INVALID_RESOURCE_CODE = "invalid_resource";
    public static final ErrorObject INVALID_RESOURCE = new ErrorObject("invalid_resource", "Invalid or unaccepted resource", 400);
    public static final String OVERBROAD_SCOPE_CODE = "overbroad_scope";
    public static final ErrorObject OVERBROAD_SCOPE = new ErrorObject("overbroad_scope", "Overbroad scope", 400);
    public static final String INVALID_DPOP_PROOF_CODE = "invalid_dpop_proof";
    public static final ErrorObject INVALID_DPOP_PROOF = new ErrorObject("invalid_dpop_proof", "Invalid DPoP proof", 400);
    public static final String MISSING_TRUST_ANCHOR_CODE = "missing_trust_anchor";
    public static final ErrorObject MISSING_TRUST_ANCHOR = new ErrorObject("missing_trust_anchor", "No trusted anchor could be found", 400);
    public static final String VALIDATION_FAILED_CODE = "validation_failed";
    public static final ErrorObject VALIDATION_FAILED = new ErrorObject("validation_failed", "Trust chain validation failed", 400);

    private OAuth2Error() {
    }
}

