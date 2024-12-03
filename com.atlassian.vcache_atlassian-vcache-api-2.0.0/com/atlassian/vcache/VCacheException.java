/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;

@PublicApi
public class VCacheException
extends RuntimeException {
    public VCacheException(String message) {
        super(message);
    }

    public VCacheException(String message, Throwable cause) {
        super(message, cause);
    }
}

