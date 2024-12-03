/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.pac;

import com.atlassian.upm.core.pac.ClientContext;

public interface ClientContextFactory {
    public ClientContext getClientContext();

    public ClientContext getClientContext(boolean var1);
}

