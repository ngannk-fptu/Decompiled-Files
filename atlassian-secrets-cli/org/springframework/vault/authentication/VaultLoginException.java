/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import org.springframework.vault.VaultException;
import org.springframework.vault.client.VaultResponses;
import org.springframework.web.client.RestClientResponseException;

public class VaultLoginException
extends VaultException {
    public VaultLoginException(String msg) {
        super(msg);
    }

    public VaultLoginException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public static VaultLoginException create(String authMethod, Throwable cause) {
        if (cause instanceof RestClientResponseException) {
            String response = ((RestClientResponseException)cause).getResponseBodyAsString();
            return new VaultLoginException(String.format("Cannot login using %s: %s", authMethod, VaultResponses.getError(response)), cause);
        }
        return new VaultLoginException(String.format("Cannot login using %s", cause), cause);
    }
}

