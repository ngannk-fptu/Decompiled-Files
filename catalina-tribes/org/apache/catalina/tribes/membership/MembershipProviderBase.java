/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.membership;

import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.MembershipProvider;
import org.apache.catalina.tribes.MembershipService;
import org.apache.catalina.tribes.membership.Membership;

public abstract class MembershipProviderBase
implements MembershipProvider {
    protected Membership membership;
    protected MembershipListener membershipListener;
    protected MembershipService service;
    protected ScheduledExecutorService executor;

    @Override
    public void init(Properties properties) throws Exception {
    }

    @Override
    public boolean hasMembers() {
        if (this.membership == null) {
            return false;
        }
        return this.membership.hasMembers();
    }

    @Override
    public Member getMember(Member mbr) {
        if (this.membership.getMembers() == null) {
            return null;
        }
        return this.membership.getMember(mbr);
    }

    @Override
    public Member[] getMembers() {
        if (this.membership.getMembers() == null) {
            return Membership.EMPTY_MEMBERS;
        }
        return this.membership.getMembers();
    }

    @Override
    public void setMembershipListener(MembershipListener listener) {
        this.membershipListener = listener;
    }

    @Override
    public void setMembershipService(MembershipService service) {
        this.service = service;
        this.executor = service.getChannel().getUtilityExecutor();
    }
}

