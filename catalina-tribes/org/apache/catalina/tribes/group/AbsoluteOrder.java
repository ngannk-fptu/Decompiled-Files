/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.apache.catalina.tribes.Member;

public class AbsoluteOrder {
    public static final AbsoluteComparator comp = new AbsoluteComparator();

    protected AbsoluteOrder() {
    }

    public static void absoluteOrder(Member[] members) {
        if (members == null || members.length <= 1) {
            return;
        }
        Arrays.sort(members, comp);
    }

    public static void absoluteOrder(List<Member> members) {
        if (members == null || members.size() <= 1) {
            return;
        }
        members.sort(comp);
    }

    public static class AbsoluteComparator
    implements Comparator<Member>,
    Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Member m1, Member m2) {
            int result = this.compareIps(m1, m2);
            if (result == 0) {
                result = this.comparePorts(m1, m2);
            }
            if (result == 0) {
                result = this.compareIds(m1, m2);
            }
            return result;
        }

        public int compareIps(Member m1, Member m2) {
            return this.compareBytes(m1.getHost(), m2.getHost());
        }

        public int comparePorts(Member m1, Member m2) {
            return this.compareInts(m1.getPort(), m2.getPort());
        }

        public int compareIds(Member m1, Member m2) {
            return this.compareBytes(m1.getUniqueId(), m2.getUniqueId());
        }

        protected int compareBytes(byte[] d1, byte[] d2) {
            int result = 0;
            if (d1.length == d2.length) {
                for (int i = 0; result == 0 && i < d1.length; ++i) {
                    result = this.compareBytes(d1[i], d2[i]);
                }
            } else {
                result = d1.length < d2.length ? -1 : 1;
            }
            return result;
        }

        protected int compareBytes(byte b1, byte b2) {
            return this.compareInts(b1, b2);
        }

        protected int compareInts(int b1, int b2) {
            int result = 0;
            if (b1 != b2) {
                result = b1 < b2 ? -1 : 1;
            }
            return result;
        }
    }
}

