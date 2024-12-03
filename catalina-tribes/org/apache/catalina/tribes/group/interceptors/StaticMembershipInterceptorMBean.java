/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group.interceptors;

import org.apache.catalina.tribes.Member;

public interface StaticMembershipInterceptorMBean {
    public int getOptionFlag();

    public Member getLocalMember(boolean var1);
}

