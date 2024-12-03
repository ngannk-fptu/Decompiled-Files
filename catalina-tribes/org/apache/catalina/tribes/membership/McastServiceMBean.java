/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.membership;

import java.util.Properties;
import org.apache.catalina.tribes.Member;

public interface McastServiceMBean {
    public String getAddress();

    public int getPort();

    public long getFrequency();

    public long getDropTime();

    public String getBind();

    public int getTtl();

    public byte[] getDomain();

    public int getSoTimeout();

    public boolean getRecoveryEnabled();

    public int getRecoveryCounter();

    public long getRecoverySleepTime();

    public boolean getLocalLoopbackDisabled();

    public String getLocalMemberName();

    public Properties getProperties();

    public boolean hasMembers();

    public String[] getMembersByName();

    public Member findMemberByName(String var1);
}

