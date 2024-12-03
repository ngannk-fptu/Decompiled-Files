/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.plugins.whitelist;

import com.atlassian.annotations.PublicApi;

@PublicApi
public class NotAuthorizedException
extends SecurityException {
    public NotAuthorizedException(String message) {
        super(message);
    }
}

