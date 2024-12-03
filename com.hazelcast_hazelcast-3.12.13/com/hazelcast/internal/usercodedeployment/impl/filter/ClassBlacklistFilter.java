/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment.impl.filter;

import com.hazelcast.internal.util.filter.Filter;
import com.hazelcast.util.collection.ArrayUtils;

public class ClassBlacklistFilter
implements Filter<String> {
    private final String[] blacklist;

    public ClassBlacklistFilter(String ... blacklisted) {
        this.blacklist = ArrayUtils.createCopy(blacklisted);
    }

    @Override
    public boolean accept(String className) {
        for (String blacklisted : this.blacklist) {
            if (!className.startsWith(blacklisted)) continue;
            return false;
        }
        return true;
    }
}

