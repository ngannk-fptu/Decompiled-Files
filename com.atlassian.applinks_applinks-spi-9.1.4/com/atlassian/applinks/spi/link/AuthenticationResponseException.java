/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.spi.link;

import com.atlassian.applinks.spi.link.ReciprocalActionException;

public class AuthenticationResponseException
extends ReciprocalActionException {
    public AuthenticationResponseException() {
        super("A valid response was not received to the request to authenticate with the remote application.");
    }
}

