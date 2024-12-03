/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.ListenerConfigHolder;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.ClusterWideConfigurationService;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.security.permission.ConfigPermission;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAddConfigMessageTask<P>
extends AbstractMessageTask<P>
implements ExecutionCallback<Object> {
    private static final ConfigPermission CONFIG_PERMISSION = new ConfigPermission();

    public AbstractAddConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    public String getServiceName() {
        return "configuration-service";
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return CONFIG_PERMISSION;
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }

    @Override
    public final void processMessage() {
        IdentifiedDataSerializable config = this.getConfig();
        ClusterWideConfigurationService service = (ClusterWideConfigurationService)this.getService("configuration-service");
        if (this.checkStaticConfigDoesNotExist(config)) {
            ICompletableFuture<Object> future = service.broadcastConfigAsync(config);
            future.andThen(this);
        } else {
            this.sendResponse(null);
        }
    }

    @Override
    public void onResponse(Object response) {
        this.sendResponse(response);
    }

    @Override
    public void onFailure(Throwable t) {
        this.handleProcessingFailure(t);
    }

    protected MergePolicyConfig mergePolicyConfig(boolean mergePolicyExist, String mergePolicy, int batchSize) {
        if (mergePolicyExist) {
            MergePolicyConfig config = new MergePolicyConfig(mergePolicy, batchSize);
            return config;
        }
        return new MergePolicyConfig();
    }

    protected List<? extends ListenerConfig> adaptListenerConfigs(List<ListenerConfigHolder> listenerConfigHolders) {
        if (listenerConfigHolders == null || listenerConfigHolders.isEmpty()) {
            return null;
        }
        ArrayList itemListenerConfigs = new ArrayList();
        for (ListenerConfigHolder listenerConfigHolder : listenerConfigHolders) {
            itemListenerConfigs.add(listenerConfigHolder.asListenerConfig(this.serializationService));
        }
        return itemListenerConfigs;
    }

    protected abstract IdentifiedDataSerializable getConfig();

    protected abstract boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable var1);
}

