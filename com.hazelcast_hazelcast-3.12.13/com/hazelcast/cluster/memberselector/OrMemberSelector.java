/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cluster.memberselector;

import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;

class OrMemberSelector
implements MemberSelector {
    private final MemberSelector[] selectors;

    public OrMemberSelector(MemberSelector ... selectors) {
        this.selectors = selectors;
    }

    @Override
    public boolean select(Member member) {
        for (MemberSelector selector : this.selectors) {
            if (!selector.select(member)) continue;
            return true;
        }
        return false;
    }
}

