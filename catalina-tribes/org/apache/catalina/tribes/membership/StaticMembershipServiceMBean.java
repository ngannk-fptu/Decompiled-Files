/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.membership;

import java.util.Properties;
import org.apache.catalina.tribes.Member;

public interface StaticMembershipServiceMBean {
    public long getExpirationTime();

    public int getConnectTimeout();

    public long getRpcTimeout();

    public boolean getUseThread();

    public long getPingInterval();

    public Properties getProperties();

    public boolean hasMembers();

    public String[] getMembersByName();

    public Member findMemberByName(String var1);
}

