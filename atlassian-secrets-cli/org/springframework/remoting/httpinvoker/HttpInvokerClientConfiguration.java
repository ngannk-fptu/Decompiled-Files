/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.httpinvoker;

import org.springframework.lang.Nullable;

public interface HttpInvokerClientConfiguration {
    public String getServiceUrl();

    @Nullable
    public String getCodebaseUrl();
}

