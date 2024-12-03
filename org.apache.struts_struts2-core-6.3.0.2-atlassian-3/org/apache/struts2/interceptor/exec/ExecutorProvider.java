/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.interceptor.exec;

public interface ExecutorProvider {
    public void execute(Runnable var1);

    public boolean isShutdown();

    public void shutdown();
}

