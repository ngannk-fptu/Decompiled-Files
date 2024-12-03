/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api.ha;

import javax.xml.ws.WebServiceException;

public final class HighAvailabilityProviderException
extends WebServiceException {
    public HighAvailabilityProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public HighAvailabilityProviderException(String message) {
        super(message);
    }
}

