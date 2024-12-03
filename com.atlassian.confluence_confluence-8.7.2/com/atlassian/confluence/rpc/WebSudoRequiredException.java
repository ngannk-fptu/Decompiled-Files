/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rpc;

import com.atlassian.confluence.rpc.NotPermittedException;

public class WebSudoRequiredException
extends NotPermittedException {
    public WebSudoRequiredException() {
    }

    public WebSudoRequiredException(String message) {
        super(message);
    }

    public WebSudoRequiredException(Throwable cause) {
        super(cause);
    }

    public WebSudoRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}

