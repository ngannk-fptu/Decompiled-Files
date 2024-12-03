/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.VCacheException;
import java.util.Objects;

@PublicApi
public class ExternalCacheException
extends VCacheException {
    private final Reason reason;

    public ExternalCacheException(Reason reason) {
        super("Failed due to " + reason.name());
        this.reason = Objects.requireNonNull(reason);
    }

    public ExternalCacheException(Reason reason, Throwable cause) {
        super("Failed due to " + reason.name(), cause);
        this.reason = Objects.requireNonNull(reason);
    }

    public Reason getReason() {
        return this.reason;
    }

    public static enum Reason {
        TIMEOUT,
        NETWORK_FAILURE,
        MARSHALLER_FAILURE,
        FUNCTION_INCORRECT_RESULT,
        CREATION_FAILURE,
        TRANSACTION_FAILURE,
        UNCLASSIFIED_FAILURE;

    }
}

