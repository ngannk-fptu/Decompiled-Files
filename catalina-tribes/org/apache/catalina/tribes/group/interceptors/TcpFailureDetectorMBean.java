/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group.interceptors;

public interface TcpFailureDetectorMBean {
    public int getOptionFlag();

    public long getConnectTimeout();

    public boolean getPerformSendTest();

    public boolean getPerformReadTest();

    public long getReadTestTimeout();

    public int getRemoveSuspectsTimeout();

    public void setPerformReadTest(boolean var1);

    public void setPerformSendTest(boolean var1);

    public void setReadTestTimeout(long var1);

    public void setConnectTimeout(long var1);

    public void setRemoveSuspectsTimeout(int var1);

    public void checkMembers(boolean var1);
}

