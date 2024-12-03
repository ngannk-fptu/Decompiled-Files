/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment.impl.filter;

import com.hazelcast.core.Member;
import com.hazelcast.internal.util.filter.Filter;

public class MemberAttributeFilter
implements Filter<Member> {
    private final String attribute;

    public MemberAttributeFilter(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public boolean accept(Member member) {
        return member.getAttributes().keySet().contains(this.attribute);
    }
}

