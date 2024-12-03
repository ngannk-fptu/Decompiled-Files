/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.sizeof;

import net.sf.ehcache.pool.sizeof.AgentLoader;
import net.sf.ehcache.pool.sizeof.JvmInformation;
import net.sf.ehcache.pool.sizeof.SizeOf;
import net.sf.ehcache.pool.sizeof.filter.PassThroughFilter;
import net.sf.ehcache.pool.sizeof.filter.SizeOfFilter;

public class AgentSizeOf
extends SizeOf {
    public static final String BYPASS_LOADING = "net.sf.ehcache.pool.sizeof.AgentSizeOf.bypass";
    private static final boolean AGENT_LOADED = !Boolean.getBoolean("net.sf.ehcache.pool.sizeof.AgentSizeOf.bypass") && AgentLoader.loadAgent();

    public AgentSizeOf() throws UnsupportedOperationException {
        this(new PassThroughFilter());
    }

    public AgentSizeOf(SizeOfFilter filter) throws UnsupportedOperationException {
        this(filter, true);
    }

    public AgentSizeOf(SizeOfFilter filter, boolean caching) throws UnsupportedOperationException {
        super(filter, caching);
        if (!AGENT_LOADED) {
            throw new UnsupportedOperationException("Agent not available or loadable");
        }
    }

    @Override
    public long sizeOf(Object obj) {
        return Math.max((long)JvmInformation.CURRENT_JVM_INFORMATION.getMinimumObjectSize(), AgentLoader.agentSizeOf(obj) + (long)JvmInformation.CURRENT_JVM_INFORMATION.getAgentSizeOfAdjustment());
    }
}

