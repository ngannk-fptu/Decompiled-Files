/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.osgi;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.osgi.HazelcastOSGiService;

public interface HazelcastOSGiInstance
extends HazelcastInstance {
    public HazelcastInstance getDelegatedInstance();

    public HazelcastOSGiService getOwnerService();
}

