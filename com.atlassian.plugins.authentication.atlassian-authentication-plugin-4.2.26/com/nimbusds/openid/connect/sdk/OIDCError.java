/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.oauth2.sdk.ErrorObject;

public final class OIDCError {
    public static final ErrorObject INTERACTION_REQUIRED = new ErrorObject("interaction_required", "User interaction required", 302);
    public static final ErrorObject LOGIN_REQUIRED = new ErrorObject("login_required", "Login required", 302);
    public static final ErrorObject ACCOUNT_SELECTION_REQUIRED = new ErrorObject("account_selection_required", "Session selection required", 302);
    public static final ErrorObject CONSENT_REQUIRED = new ErrorObject("consent_required", "Consent required", 302);
    public static final ErrorObject UNMET_AUTHENTICATION_REQUIREMENTS = new ErrorObject("unmet_authentication_requirements", "Unmet authentication requirements", 302);
    public static final ErrorObject REGISTRATION_NOT_SUPPORTED = new ErrorObject("registration_not_supported", "Registration parameter not supported", 302);

    private OIDCError() {
    }
}

