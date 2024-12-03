/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.websocket;

public interface BackgroundProcess {
    public void backgroundProcess();

    public void setProcessPeriod(int var1);

    public int getProcessPeriod();
}

