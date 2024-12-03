/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.factories;

public class PolicyFormulationException
extends Exception {
    private static final long serialVersionUID = -1254653984673380779L;

    public PolicyFormulationException(String message) {
        super(message);
    }

    public PolicyFormulationException(String message, Throwable cause) {
        super(message);
    }
}

