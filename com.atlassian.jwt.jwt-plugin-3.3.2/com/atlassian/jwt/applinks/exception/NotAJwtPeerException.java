/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 */
package com.atlassian.jwt.applinks.exception;

import com.atlassian.applinks.api.ApplicationLink;

public class NotAJwtPeerException
extends RuntimeException {
    public NotAJwtPeerException(ApplicationLink applicationLink) {
        super(applicationLink + " is not a valid JWT peer.");
    }
}

