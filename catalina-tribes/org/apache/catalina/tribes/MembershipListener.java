/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import org.apache.catalina.tribes.Member;

public interface MembershipListener {
    public void memberAdded(Member var1);

    public void memberDisappeared(Member var1);
}

