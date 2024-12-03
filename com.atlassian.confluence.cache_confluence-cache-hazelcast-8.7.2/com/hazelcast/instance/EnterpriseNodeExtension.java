/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.SocketInterceptorConfig
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.HazelcastInstanceAware
 *  com.hazelcast.instance.DefaultNodeExtension
 *  com.hazelcast.instance.EndpointQualifier
 *  com.hazelcast.instance.Node
 *  com.hazelcast.nio.ClassLoaderUtil
 *  com.hazelcast.nio.MemberSocketInterceptor
 */
package com.hazelcast.instance;

import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.instance.DefaultNodeExtension;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.MemberSocketInterceptor;

public class EnterpriseNodeExtension
extends DefaultNodeExtension {
    private MemberSocketInterceptor memberSocketInterceptor;

    public EnterpriseNodeExtension(Node node) {
        super(node);
    }

    public void beforeStart() {
        super.beforeStart();
        SocketInterceptorConfig config = this.node.getConfig().getNetworkConfig().getSocketInterceptorConfig();
        if (config != null && config.isEnabled()) {
            this.memberSocketInterceptor = this.createInterceptor(config);
        }
    }

    public MemberSocketInterceptor getSocketInterceptor(EndpointQualifier endpointQualifier) {
        return this.memberSocketInterceptor;
    }

    private MemberSocketInterceptor createInterceptor(SocketInterceptorConfig config) {
        MemberSocketInterceptor interceptor = (MemberSocketInterceptor)config.getImplementation();
        if (interceptor != null) {
            return interceptor;
        }
        try {
            interceptor = (MemberSocketInterceptor)ClassLoaderUtil.newInstance((ClassLoader)this.node.getConfigClassLoader(), (String)config.getClassName());
            interceptor.init(config.getProperties());
            if (interceptor instanceof HazelcastInstanceAware) {
                ((HazelcastInstanceAware)interceptor).setHazelcastInstance((HazelcastInstance)this.node.hazelcastInstance);
            }
        }
        catch (Exception e) {
            this.logger.warning("Failed to instantiate MemberSocketInterceptor", (Throwable)e);
        }
        return interceptor;
    }
}

