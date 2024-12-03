/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import org.springframework.vault.authentication.AuthenticationSteps;

@FunctionalInterface
public interface AuthenticationStepsFactory {
    public AuthenticationSteps getAuthenticationSteps();
}

