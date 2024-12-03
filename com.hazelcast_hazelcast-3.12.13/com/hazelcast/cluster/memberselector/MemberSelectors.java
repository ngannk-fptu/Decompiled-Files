/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cluster.memberselector;

import com.hazelcast.cluster.memberselector.AndMemberSelector;
import com.hazelcast.cluster.memberselector.OrMemberSelector;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;

public final class MemberSelectors {
    public static final MemberSelector LITE_MEMBER_SELECTOR = new MemberSelector(){

        @Override
        public boolean select(Member member) {
            return member.isLiteMember();
        }
    };
    public static final MemberSelector DATA_MEMBER_SELECTOR = new MemberSelector(){

        @Override
        public boolean select(Member member) {
            return !member.isLiteMember();
        }
    };
    public static final MemberSelector LOCAL_MEMBER_SELECTOR = new MemberSelector(){

        @Override
        public boolean select(Member member) {
            return member.localMember();
        }
    };
    public static final MemberSelector NON_LOCAL_MEMBER_SELECTOR = new MemberSelector(){

        @Override
        public boolean select(Member member) {
            return !member.localMember();
        }
    };

    private MemberSelectors() {
    }

    public static MemberSelector or(MemberSelector ... selectors) {
        return new OrMemberSelector(selectors);
    }

    public static MemberSelector and(MemberSelector ... selectors) {
        return new AndMemberSelector(selectors);
    }
}

