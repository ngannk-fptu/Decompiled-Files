/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.tenancy.api.helper;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.tenancy.api.Tenant;

@ExperimentalApi
@Deprecated
public class Failure {
    private final Tenant tenant;
    private final Exception exception;

    public Failure(Tenant tenant, Exception exception) {
        this.tenant = tenant;
        this.exception = exception;
    }

    @Deprecated
    public Tenant getTenant() {
        return this.tenant;
    }

    @Deprecated
    public Exception getException() {
        return this.exception;
    }
}

