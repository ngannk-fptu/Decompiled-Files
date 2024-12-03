/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.spi;

import java.util.concurrent.Callable;

public interface HostApplication {
    public <T> Callable<T> asUser(String var1, Callable<T> var2);
}

