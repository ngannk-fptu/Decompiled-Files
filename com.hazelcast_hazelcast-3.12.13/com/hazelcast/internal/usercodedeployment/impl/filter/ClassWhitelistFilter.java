/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment.impl.filter;

import com.hazelcast.internal.util.filter.Filter;
import com.hazelcast.util.collection.ArrayUtils;

public class ClassWhitelistFilter
implements Filter<String> {
    private final String[] whitelist;

    public ClassWhitelistFilter(String ... whitelisted) {
        this.whitelist = ArrayUtils.createCopy(whitelisted);
    }

    @Override
    public boolean accept(String className) {
        for (String blacklisted : this.whitelist) {
            if (!className.startsWith(blacklisted)) continue;
            return true;
        }
        return false;
    }
}

