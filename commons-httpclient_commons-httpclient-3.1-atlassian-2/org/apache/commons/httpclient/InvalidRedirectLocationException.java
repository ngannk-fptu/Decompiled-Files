/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import org.apache.commons.httpclient.RedirectException;

public class InvalidRedirectLocationException
extends RedirectException {
    private final String location;

    public InvalidRedirectLocationException(String message, String location) {
        super(message);
        this.location = location;
    }

    public InvalidRedirectLocationException(String message, String location, Throwable cause) {
        super(message, cause);
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }
}

