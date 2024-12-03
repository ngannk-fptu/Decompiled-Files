/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.OutOfMemoryHandler
 *  com.hazelcast.instance.OutOfMemoryErrorDispatcher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.OutOfMemoryHandler;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceOutOfMemoryHandler
extends OutOfMemoryHandler {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceOutOfMemoryHandler.class);

    public void onOutOfMemory(OutOfMemoryError oom, HazelcastInstance[] hazelcastInstances) {
        log.warn("OutOfMemoryError occurred attempting to continue operating as normal", (Throwable)oom);
        log.debug("Attempting to re-register {} hazelcast instances", (Object)hazelcastInstances.length);
        for (HazelcastInstance hazelcastInstance : hazelcastInstances) {
            OutOfMemoryErrorDispatcher.registerServer((HazelcastInstance)hazelcastInstance);
            log.trace("Re-registered {}", (Object)hazelcastInstance);
        }
    }
}

