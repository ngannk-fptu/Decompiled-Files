/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group.interceptors;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.group.AbsoluteOrder;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;

public class SimpleCoordinator
extends ChannelInterceptorBase {
    private Member[] view;
    private final AtomicBoolean membershipChanged = new AtomicBoolean();

    private void membershipChanged() {
        this.membershipChanged.set(true);
    }

    @Override
    public void memberAdded(Member member) {
        super.memberAdded(member);
        this.membershipChanged();
        this.installViewWhenStable();
    }

    @Override
    public void memberDisappeared(Member member) {
        super.memberDisappeared(member);
        this.membershipChanged();
        this.installViewWhenStable();
    }

    protected void viewChange(Member[] view) {
    }

    @Override
    public void start(int svc) throws ChannelException {
        super.start(svc);
        this.installViewWhenStable();
    }

    private void installViewWhenStable() {
        int stableCount = 0;
        while (stableCount < 10) {
            stableCount = this.membershipChanged.compareAndSet(true, false) ? 0 : ++stableCount;
            try {
                TimeUnit.MILLISECONDS.sleep(250L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        Member[] members = this.getMembers();
        Object[] view = new Member[members.length + 1];
        System.arraycopy(members, 0, view, 0, members.length);
        view[members.length] = this.getLocalMember(false);
        Arrays.sort(view, AbsoluteOrder.comp);
        if (Arrays.equals(view, this.view)) {
            return;
        }
        this.view = view;
        this.viewChange((Member[])view);
    }

    @Override
    public void stop(int svc) throws ChannelException {
        super.stop(svc);
    }

    public Member[] getView() {
        return this.view;
    }

    public Member getCoordinator() {
        return this.view == null ? null : this.view[0];
    }

    public boolean isCoordinator() {
        return this.view == null ? false : this.getLocalMember(false).equals(this.getCoordinator());
    }
}

