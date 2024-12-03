/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.impl.security.access;

import com.atlassian.annotations.Internal;

@Internal
public final class AccessDenied {
    public static final AccessDenied INSTANCE = new AccessDenied();

    private AccessDenied() {
    }
}

