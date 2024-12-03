/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import java.util.Properties;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.MembershipService;

public interface MembershipProvider {
    public void init(Properties var1) throws Exception;

    public void start(int var1) throws Exception;

    public boolean stop(int var1) throws Exception;

    public void setMembershipListener(MembershipListener var1);

    public void setMembershipService(MembershipService var1);

    public boolean hasMembers();

    public Member getMember(Member var1);

    public Member[] getMembers();
}

