/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.sal.api.i18n;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.PublicApi;

@PublicApi
public class InvalidOperationException
extends Exception {
    private final String localizedMessage;

    @Internal
    public InvalidOperationException(String message, String localizedMessage) {
        super(message);
        this.localizedMessage = localizedMessage;
    }

    @Override
    public String getLocalizedMessage() {
        return this.localizedMessage;
    }
}

