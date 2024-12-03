/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.ui;

import com.atlassian.applinks.ui.RequestException;

public class UnauthorizedBecauseUnauthenticatedException
extends RequestException {
    public UnauthorizedBecauseUnauthenticatedException() {
        super(401);
    }
}

