/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.internal.management.dto.ConnectionManagerDTO;
import com.hazelcast.internal.management.dto.EventServiceDTO;
import com.hazelcast.internal.management.dto.ManagedExecutorDTO;
import com.hazelcast.internal.management.dto.OperationServiceDTO;
import com.hazelcast.internal.management.dto.PartitionServiceBeanDTO;
import com.hazelcast.internal.management.dto.ProxyServiceDTO;
import com.hazelcast.util.JsonUtil;
import java.util.HashMap;
import java.util.Map;

public class MXBeansDTO
implements JsonSerializable {
    private EventServiceDTO eventServiceBean;
    private OperationServiceDTO operationServiceBean;
    private ConnectionManagerDTO connectionManagerBean;
    private PartitionServiceBeanDTO partitionServiceBean;
    private ProxyServiceDTO proxyServiceBean;
    private Map<String, ManagedExecutorDTO> managedExecutorBeans = new HashMap<String, ManagedExecutorDTO>();

    public EventServiceDTO getEventServiceBean() {
        return this.eventServiceBean;
    }

    public void setEventServiceBean(EventServiceDTO eventServiceBean) {
        this.eventServiceBean = eventServiceBean;
    }

    public OperationServiceDTO getOperationServiceBean() {
        return this.operationServiceBean;
    }

    public void setOperationServiceBean(OperationServiceDTO operationServiceBean) {
        this.operationServiceBean = operationServiceBean;
    }

    public ConnectionManagerDTO getConnectionManagerBean() {
        return this.connectionManagerBean;
    }

    public void setConnectionManagerBean(ConnectionManagerDTO connectionManagerBean) {
        this.connectionManagerBean = connectionManagerBean;
    }

    public PartitionServiceBeanDTO getPartitionServiceBean() {
        return this.partitionServiceBean;
    }

    public void setPartitionServiceBean(PartitionServiceBeanDTO partitionServiceBean) {
        this.partitionServiceBean = partitionServiceBean;
    }

    public ProxyServiceDTO getProxyServiceBean() {
        return this.proxyServiceBean;
    }

    public void setProxyServiceBean(ProxyServiceDTO proxyServiceBean) {
        this.proxyServiceBean = proxyServiceBean;
    }

    public ManagedExecutorDTO getManagedExecutorBean(String name) {
        return this.managedExecutorBeans.get(name);
    }

    public void putManagedExecutor(String name, ManagedExecutorDTO bean) {
        this.managedExecutorBeans.put(name, bean);
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        JsonObject managedExecutors = new JsonObject();
        for (Map.Entry<String, ManagedExecutorDTO> entry : this.managedExecutorBeans.entrySet()) {
            managedExecutors.add(entry.getKey(), entry.getValue().toJson());
        }
        root.add("managedExecutorBeans", managedExecutors);
        root.add("eventServiceBean", this.eventServiceBean.toJson());
        root.add("operationServiceBean", this.operationServiceBean.toJson());
        root.add("connectionManagerBean", this.connectionManagerBean.toJson());
        root.add("partitionServiceBean", this.partitionServiceBean.toJson());
        root.add("proxyServiceBean", this.proxyServiceBean.toJson());
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        for (JsonObject.Member next : JsonUtil.getObject(json, "managedExecutorBeans")) {
            ManagedExecutorDTO managedExecutorBean = new ManagedExecutorDTO();
            managedExecutorBean.fromJson(next.getValue().asObject());
            this.managedExecutorBeans.put(next.getName(), managedExecutorBean);
        }
        this.eventServiceBean = new EventServiceDTO();
        this.eventServiceBean.fromJson(JsonUtil.getObject(json, "eventServiceBean"));
        this.operationServiceBean = new OperationServiceDTO();
        this.operationServiceBean.fromJson(JsonUtil.getObject(json, "operationServiceBean"));
        this.connectionManagerBean = new ConnectionManagerDTO();
        this.connectionManagerBean.fromJson(JsonUtil.getObject(json, "connectionManagerBean"));
        this.proxyServiceBean = new ProxyServiceDTO();
        this.proxyServiceBean.fromJson(JsonUtil.getObject(json, "proxyServiceBean"));
        this.partitionServiceBean = new PartitionServiceBeanDTO();
        this.partitionServiceBean.fromJson(JsonUtil.getObject(json, "partitionServiceBean"));
    }
}

