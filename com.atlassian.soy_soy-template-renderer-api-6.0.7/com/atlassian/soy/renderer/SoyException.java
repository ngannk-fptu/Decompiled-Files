/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.soy.renderer;

import com.atlassian.annotations.PublicApi;

@PublicApi
public class SoyException
extends RuntimeException {
    public SoyException(String message) {
        super(message);
    }

    public SoyException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoyException(Throwable cause) {
        super(cause);
    }
}

