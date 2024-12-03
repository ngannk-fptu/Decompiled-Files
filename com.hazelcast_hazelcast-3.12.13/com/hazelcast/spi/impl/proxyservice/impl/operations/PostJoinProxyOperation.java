/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.proxyservice.impl.operations;

import com.hazelcast.cache.CacheNotExistsException;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.replicatedmap.ReplicatedMapCantBeCreatedOnLiteMemberException;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import com.hazelcast.spi.impl.proxyservice.impl.ProxyInfo;
import com.hazelcast.spi.impl.proxyservice.impl.ProxyRegistry;
import com.hazelcast.spi.impl.proxyservice.impl.ProxyServiceImpl;
import com.hazelcast.util.EmptyStatement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class PostJoinProxyOperation
extends Operation
implements IdentifiedDataSerializable {
    private Collection<ProxyInfo> proxies;

    public PostJoinProxyOperation() {
    }

    public PostJoinProxyOperation(Collection<ProxyInfo> proxies) {
        this.proxies = proxies;
    }

    @Override
    public void run() throws Exception {
        if (this.proxies == null || this.proxies.size() <= 0) {
            return;
        }
        NodeEngine nodeEngine = this.getNodeEngine();
        ProxyServiceImpl proxyService = (ProxyServiceImpl)this.getService();
        ExecutionService executionService = nodeEngine.getExecutionService();
        for (ProxyInfo proxy : this.proxies) {
            ProxyRegistry registry = proxyService.getOrCreateRegistry(proxy.getServiceName());
            try {
                executionService.execute("hz:system", new CreateProxyTask(registry, proxy));
            }
            catch (Throwable t) {
                this.logProxyCreationFailure(proxy, t);
            }
        }
    }

    private void logProxyCreationFailure(ProxyInfo proxy, Throwable t) {
        this.getLogger().severe("Cannot create proxy [" + proxy.getServiceName() + ":" + proxy.getObjectName() + "]!", t);
    }

    @Override
    public String getServiceName() {
        return "hz:core:proxyService";
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        int len = this.proxies != null ? this.proxies.size() : 0;
        out.writeInt(len);
        if (len > 0) {
            for (ProxyInfo proxy : this.proxies) {
                out.writeUTF(proxy.getServiceName());
                out.writeObject(proxy.getObjectName());
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int len = in.readInt();
        if (len > 0) {
            this.proxies = new ArrayList<ProxyInfo>(len);
            for (int i = 0; i < len; ++i) {
                ProxyInfo proxy = new ProxyInfo(in.readUTF(), (String)in.readObject());
                this.proxies.add(proxy);
            }
        }
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 16;
    }

    private class CreateProxyTask
    implements Runnable {
        private final ProxyRegistry registry;
        private final ProxyInfo proxyInfo;

        CreateProxyTask(ProxyRegistry registry, ProxyInfo proxyInfo) {
            this.registry = registry;
            this.proxyInfo = proxyInfo;
        }

        @Override
        public void run() {
            try {
                this.registry.createProxy(this.proxyInfo.getObjectName(), false, true);
            }
            catch (CacheNotExistsException e) {
                PostJoinProxyOperation.this.getLogger().fine("Could not create Cache[" + this.proxyInfo.getObjectName() + "]. It is already destroyed.", e);
            }
            catch (ReplicatedMapCantBeCreatedOnLiteMemberException e) {
                EmptyStatement.ignore(e);
            }
            catch (Exception e) {
                PostJoinProxyOperation.this.logProxyCreationFailure(this.proxyInfo, e);
            }
        }
    }
}

