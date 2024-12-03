/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.spi.link;

import com.atlassian.applinks.spi.link.ReciprocalActionException;

public class LinkCreationResponseException
extends ReciprocalActionException {
    public LinkCreationResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}

