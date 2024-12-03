/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.membership.cloud;

import java.util.Properties;
import org.apache.catalina.tribes.Member;

public interface CloudMembershipServiceMBean {
    public int getConnectTimeout();

    public int getReadTimeout();

    public long getExpirationTime();

    public Properties getProperties();

    public boolean hasMembers();

    public String[] getMembersByName();

    public Member findMemberByName(String var1);
}

