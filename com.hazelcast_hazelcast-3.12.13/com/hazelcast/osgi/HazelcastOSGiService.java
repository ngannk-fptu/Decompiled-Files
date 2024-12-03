/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package com.hazelcast.osgi;

import com.hazelcast.config.Config;
import com.hazelcast.osgi.HazelcastOSGiInstance;
import java.util.Set;
import org.osgi.framework.Bundle;

public interface HazelcastOSGiService {
    public static final String HAZELCAST_OSGI_START = "hazelcast.osgi.start";
    public static final String HAZELCAST_OSGI_REGISTER_DISABLED = "hazelcast.osgi.register.disabled";
    public static final String HAZELCAST_OSGI_GROUPING_DISABLED = "hazelcast.osgi.grouping.disabled";
    public static final String HAZELCAST_OSGI_JSR223_DISABLED = "hazelcast.osgi.jsr223.disabled";

    public String getId();

    public Bundle getOwnerBundle();

    public HazelcastOSGiInstance getDefaultHazelcastInstance();

    public HazelcastOSGiInstance newHazelcastInstance(Config var1);

    public HazelcastOSGiInstance newHazelcastInstance();

    public HazelcastOSGiInstance getHazelcastInstanceByName(String var1);

    public Set<HazelcastOSGiInstance> getAllHazelcastInstances();

    public void shutdownHazelcastInstance(HazelcastOSGiInstance var1);

    public void shutdownAll();
}

