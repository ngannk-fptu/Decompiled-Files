/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import java.util.ArrayList;
import org.apache.catalina.tribes.Member;

public class ChannelException
extends Exception {
    private static final long serialVersionUID = 1L;
    protected static final FaultyMember[] EMPTY_LIST = new FaultyMember[0];
    private ArrayList<FaultyMember> faultyMembers = null;

    public ChannelException() {
    }

    public ChannelException(String message) {
        super(message);
    }

    public ChannelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        StringBuilder buf = new StringBuilder(super.getMessage());
        if (this.faultyMembers == null || this.faultyMembers.size() == 0) {
            buf.append("; No faulty members identified.");
        } else {
            buf.append("; Faulty members:");
            for (FaultyMember mbr : this.faultyMembers) {
                buf.append(mbr.getMember().getName());
                buf.append("; ");
            }
        }
        return buf.toString();
    }

    public boolean addFaultyMember(Member mbr, Exception x) {
        return this.addFaultyMember(new FaultyMember(mbr, x));
    }

    public int addFaultyMember(FaultyMember[] mbrs) {
        int result = 0;
        for (int i = 0; mbrs != null && i < mbrs.length; ++i) {
            if (!this.addFaultyMember(mbrs[i])) continue;
            ++result;
        }
        return result;
    }

    public boolean addFaultyMember(FaultyMember mbr) {
        if (this.faultyMembers == null) {
            this.faultyMembers = new ArrayList();
        }
        if (!this.faultyMembers.contains(mbr)) {
            return this.faultyMembers.add(mbr);
        }
        return false;
    }

    public FaultyMember[] getFaultyMembers() {
        if (this.faultyMembers == null) {
            return EMPTY_LIST;
        }
        return this.faultyMembers.toArray(new FaultyMember[0]);
    }

    public static class FaultyMember {
        protected final Exception cause;
        protected final Member member;

        public FaultyMember(Member mbr, Exception x) {
            this.member = mbr;
            this.cause = x;
        }

        public Member getMember() {
            return this.member;
        }

        public Exception getCause() {
            return this.cause;
        }

        public String toString() {
            return "FaultyMember:" + this.member.toString();
        }

        public int hashCode() {
            return this.member != null ? this.member.hashCode() : 0;
        }

        public boolean equals(Object o) {
            if (this.member == null || !(o instanceof FaultyMember) || ((FaultyMember)o).member == null) {
                return false;
            }
            return this.member.equals(((FaultyMember)o).member);
        }
    }
}

