/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.membership;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import org.apache.catalina.tribes.Member;

public class Membership
implements Cloneable {
    protected static final Member[] EMPTY_MEMBERS = new Member[0];
    private Object membersLock = new Object();
    protected final Member local;
    protected HashMap<Member, MbrEntry> map = new HashMap();
    protected volatile Member[] members = EMPTY_MEMBERS;
    protected final Comparator<Member> memberComparator;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Membership clone() {
        Object object = this.membersLock;
        synchronized (object) {
            HashMap tmpclone;
            Membership clone;
            try {
                clone = (Membership)super.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
            clone.map = tmpclone = (HashMap)this.map.clone();
            clone.members = (Member[])this.members.clone();
            clone.membersLock = new Object();
            return clone;
        }
    }

    public Membership(Member local, boolean includeLocal) {
        this(local, Comparator.comparingLong(Member::getMemberAliveTime).reversed(), includeLocal);
    }

    public Membership(Member local) {
        this(local, false);
    }

    public Membership(Member local, Comparator<Member> comp) {
        this(local, comp, false);
    }

    public Membership(Member local, Comparator<Member> comp, boolean includeLocal) {
        this.local = local;
        this.memberComparator = comp;
        if (includeLocal) {
            this.addMember(local);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset() {
        Object object = this.membersLock;
        synchronized (object) {
            this.map.clear();
            this.members = EMPTY_MEMBERS;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean memberAlive(Member member) {
        if (member.equals(this.local)) {
            return false;
        }
        boolean result = false;
        Object object = this.membersLock;
        synchronized (object) {
            MbrEntry entry = this.map.get(member);
            if (entry == null) {
                entry = this.addMember(member);
                result = true;
            } else {
                Member updateMember = entry.getMember();
                if (updateMember.getMemberAliveTime() != member.getMemberAliveTime()) {
                    updateMember.setMemberAliveTime(member.getMemberAliveTime());
                    updateMember.setPayload(member.getPayload());
                    updateMember.setCommand(member.getCommand());
                    Member[] newMembers = (Member[])this.members.clone();
                    Arrays.sort(newMembers, this.memberComparator);
                    this.members = newMembers;
                }
            }
            entry.accessed();
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MbrEntry addMember(Member member) {
        MbrEntry entry = new MbrEntry(member);
        Object object = this.membersLock;
        synchronized (object) {
            if (!this.map.containsKey(member)) {
                this.map.put(member, entry);
                Member[] results = new Member[this.members.length + 1];
                System.arraycopy(this.members, 0, results, 0, this.members.length);
                results[this.members.length] = member;
                Arrays.sort(results, this.memberComparator);
                this.members = results;
            }
        }
        return entry;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeMember(Member member) {
        Object object = this.membersLock;
        synchronized (object) {
            this.map.remove(member);
            int n = -1;
            for (int i = 0; i < this.members.length; ++i) {
                if (this.members[i] != member && !this.members[i].equals(member)) continue;
                n = i;
                break;
            }
            if (n < 0) {
                return;
            }
            Member[] results = new Member[this.members.length - 1];
            int j = 0;
            for (int i = 0; i < this.members.length; ++i) {
                if (i == n) continue;
                results[j++] = this.members[i];
            }
            this.members = results;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Member[] expire(long maxtime) {
        Object object = this.membersLock;
        synchronized (object) {
            if (!this.hasMembers()) {
                return EMPTY_MEMBERS;
            }
            ArrayList<Member> list = null;
            for (MbrEntry entry : this.map.values()) {
                if (!entry.hasExpired(maxtime)) continue;
                if (list == null) {
                    list = new ArrayList<Member>();
                }
                list.add(entry.getMember());
            }
            if (list != null) {
                Member[] result;
                for (Member member : result = list.toArray(new Member[0])) {
                    this.removeMember(member);
                }
                return result;
            }
            return EMPTY_MEMBERS;
        }
    }

    public boolean hasMembers() {
        return this.members.length > 0;
    }

    public Member getMember(Member mbr) {
        Member[] members = this.members;
        if (members.length > 0) {
            for (Member member : members) {
                if (!member.equals(mbr)) continue;
                return member;
            }
        }
        return null;
    }

    public boolean contains(Member mbr) {
        return this.getMember(mbr) != null;
    }

    public Member[] getMembers() {
        return this.members;
    }

    protected static class MbrEntry {
        protected final Member mbr;
        protected long lastHeardFrom;

        public MbrEntry(Member mbr) {
            this.mbr = mbr;
        }

        public void accessed() {
            this.lastHeardFrom = System.currentTimeMillis();
        }

        public Member getMember() {
            return this.mbr;
        }

        public boolean hasExpired(long maxtime) {
            return !this.mbr.isLocal() && System.currentTimeMillis() - this.lastHeardFrom > maxtime;
        }
    }
}

