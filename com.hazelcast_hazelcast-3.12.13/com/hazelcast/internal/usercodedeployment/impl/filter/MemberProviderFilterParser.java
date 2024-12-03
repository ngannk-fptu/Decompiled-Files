/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment.impl.filter;

import com.hazelcast.core.Member;
import com.hazelcast.internal.usercodedeployment.impl.filter.MemberAttributeFilter;
import com.hazelcast.internal.util.filter.AlwaysApplyFilter;
import com.hazelcast.internal.util.filter.Filter;

public final class MemberProviderFilterParser {
    private static final String HAS_ATTRIBUTE_PREFIX = "HAS_ATTRIBUTE:";

    private MemberProviderFilterParser() {
    }

    public static Filter<Member> parseMemberFilter(String providerFilter) {
        if (providerFilter == null) {
            return AlwaysApplyFilter.newInstance();
        }
        if ((providerFilter = providerFilter.trim()).startsWith(HAS_ATTRIBUTE_PREFIX)) {
            providerFilter = providerFilter.substring(HAS_ATTRIBUTE_PREFIX.length());
            providerFilter = providerFilter.trim();
            return new MemberAttributeFilter(providerFilter);
        }
        if (providerFilter.isEmpty()) {
            return AlwaysApplyFilter.newInstance();
        }
        throw new IllegalArgumentException("Unknown provider filter: " + providerFilter);
    }
}

