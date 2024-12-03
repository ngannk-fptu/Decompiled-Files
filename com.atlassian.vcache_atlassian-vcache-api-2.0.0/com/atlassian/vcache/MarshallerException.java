/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;

@Deprecated
@PublicApi
public class MarshallerException
extends Exception {
    public MarshallerException(String message) {
        super(message);
    }

    public MarshallerException(String message, Throwable cause) {
        super(message, cause);
    }
}

