/*
 * Decompiled with CFR 0.152.
 */
package org.ehcache.sizeof.impl;

import org.ehcache.sizeof.SizeOf;
import org.ehcache.sizeof.filters.SizeOfFilter;
import org.ehcache.sizeof.impl.AgentLoader;
import org.ehcache.sizeof.impl.JvmInformation;
import org.ehcache.sizeof.impl.PassThroughFilter;

public class AgentSizeOf
extends SizeOf {
    public static final String BYPASS_LOADING = "org.ehcache.sizeof.AgentSizeOf.bypass";
    private static final boolean AGENT_LOADED = !Boolean.getBoolean("org.ehcache.sizeof.AgentSizeOf.bypass") && AgentLoader.loadAgent();

    public AgentSizeOf() throws UnsupportedOperationException {
        this(new PassThroughFilter());
    }

    public AgentSizeOf(SizeOfFilter filter) throws UnsupportedOperationException {
        this(filter, true, true);
    }

    public AgentSizeOf(SizeOfFilter filter, boolean caching, boolean bypassFlyweight) throws UnsupportedOperationException {
        super(filter, caching, bypassFlyweight);
        if (!AGENT_LOADED) {
            throw new UnsupportedOperationException("Agent not available or loadable");
        }
    }

    @Override
    public long sizeOf(Object obj) {
        long measuredSize = AgentLoader.agentSizeOf(obj);
        return Math.max((long)JvmInformation.CURRENT_JVM_INFORMATION.getMinimumObjectSize(), measuredSize + (long)JvmInformation.CURRENT_JVM_INFORMATION.getAgentSizeOfAdjustment());
    }
}

