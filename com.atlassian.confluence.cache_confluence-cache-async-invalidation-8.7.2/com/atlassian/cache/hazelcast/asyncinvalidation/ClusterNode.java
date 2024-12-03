/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.Member
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.hazelcast.core.Member;
import java.util.Objects;

interface ClusterNode {
    public static ClusterNode from(Member hazelcastMember) {
        return new HazelcastMember(hazelcastMember);
    }

    public static final class HazelcastMember
    implements ClusterNode {
        private final Member hazelcastMember;

        private HazelcastMember(Member hazelcastMember) {
            this.hazelcastMember = hazelcastMember;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            HazelcastMember that = (HazelcastMember)o;
            return this.hazelcastMember.equals(that.hazelcastMember);
        }

        public int hashCode() {
            return Objects.hash(this.hazelcastMember);
        }

        public String toString() {
            return this.hazelcastMember.toString();
        }
    }
}

