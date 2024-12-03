/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.remoting.httpinvoker;

import org.springframework.lang.Nullable;

@Deprecated
public interface HttpInvokerClientConfiguration {
    public String getServiceUrl();

    @Nullable
    public String getCodebaseUrl();
}

