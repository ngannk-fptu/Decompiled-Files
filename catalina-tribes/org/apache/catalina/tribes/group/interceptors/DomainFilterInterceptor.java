/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.group.interceptors;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;
import org.apache.catalina.tribes.group.interceptors.DomainFilterInterceptorMBean;
import org.apache.catalina.tribes.membership.Membership;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class DomainFilterInterceptor
extends ChannelInterceptorBase
implements DomainFilterInterceptorMBean {
    private static final Log log = LogFactory.getLog(DomainFilterInterceptor.class);
    protected static final StringManager sm = StringManager.getManager(DomainFilterInterceptor.class);
    protected volatile Membership membership = null;
    protected byte[] domain = new byte[0];
    protected int logInterval = 100;
    private final AtomicInteger logCounter = new AtomicInteger(this.logInterval);

    @Override
    public void messageReceived(ChannelMessage msg) {
        if (Arrays.equals(this.domain, msg.getAddress().getDomain())) {
            super.messageReceived(msg);
        } else if (this.logCounter.incrementAndGet() >= this.logInterval) {
            this.logCounter.set(0);
            if (log.isWarnEnabled()) {
                log.warn((Object)sm.getString("domainFilterInterceptor.message.refused", msg.getAddress()));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void memberAdded(Member member) {
        if (this.membership == null) {
            this.setupMembership();
        }
        boolean notify = false;
        Membership membership = this.membership;
        synchronized (membership) {
            notify = Arrays.equals(this.domain, member.getDomain());
            if (notify) {
                notify = this.membership.memberAlive(member);
            }
        }
        if (notify) {
            super.memberAdded(member);
        } else if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("domainFilterInterceptor.member.refused", member));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void memberDisappeared(Member member) {
        if (this.membership == null) {
            this.setupMembership();
        }
        boolean notify = false;
        Membership membership = this.membership;
        synchronized (membership) {
            notify = Arrays.equals(this.domain, member.getDomain());
            if (notify) {
                this.membership.removeMember(member);
            }
        }
        if (notify) {
            super.memberDisappeared(member);
        }
    }

    @Override
    public boolean hasMembers() {
        if (this.membership == null) {
            this.setupMembership();
        }
        return this.membership.hasMembers();
    }

    @Override
    public Member[] getMembers() {
        if (this.membership == null) {
            this.setupMembership();
        }
        return this.membership.getMembers();
    }

    @Override
    public Member getMember(Member mbr) {
        if (this.membership == null) {
            this.setupMembership();
        }
        return this.membership.getMember(mbr);
    }

    @Override
    public Member getLocalMember(boolean incAlive) {
        return super.getLocalMember(incAlive);
    }

    protected synchronized void setupMembership() {
        if (this.membership == null) {
            this.membership = new Membership(super.getLocalMember(true));
        }
    }

    @Override
    public byte[] getDomain() {
        return this.domain;
    }

    public void setDomain(byte[] domain) {
        this.domain = domain;
    }

    public void setDomain(String domain) {
        if (domain == null) {
            return;
        }
        if (domain.startsWith("{")) {
            this.setDomain(org.apache.catalina.tribes.util.Arrays.fromString(domain));
        } else {
            this.setDomain(org.apache.catalina.tribes.util.Arrays.convert(domain));
        }
    }

    @Override
    public int getLogInterval() {
        return this.logInterval;
    }

    @Override
    public void setLogInterval(int logInterval) {
        this.logInterval = logInterval;
    }
}

