/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 */
package com.atlassian.crowd.model.authentication;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.model.authentication.AuthenticationContext;
import com.atlassian.crowd.model.authentication.ValidationFactor;

public class ApplicationAuthenticationContext
extends AuthenticationContext {
    public ApplicationAuthenticationContext() {
    }

    public ApplicationAuthenticationContext(String name, PasswordCredential credential, ValidationFactor[] validationFactors) {
        super(name, credential, validationFactors);
    }

    public ApplicationAuthenticationContext(AuthenticationContext authenticationContext) {
        super(authenticationContext.getName(), authenticationContext.getCredential(), authenticationContext.getValidationFactors());
    }
}

