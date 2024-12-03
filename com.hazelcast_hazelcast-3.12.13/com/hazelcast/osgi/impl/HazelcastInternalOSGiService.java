/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.osgi.impl;

import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.osgi.HazelcastOSGiService;

public interface HazelcastInternalOSGiService
extends HazelcastOSGiService {
    public static final String DEFAULT_ID;
    public static final String DEFAULT_GROUP_NAME;

    public boolean isActive();

    public void activate();

    public void deactivate();

    static {
        DEFAULT_GROUP_NAME = DEFAULT_ID = BuildInfoProvider.getBuildInfo().getVersion() + "#" + (BuildInfoProvider.getBuildInfo().isEnterprise() ? "EE" : "OSS");
    }
}

