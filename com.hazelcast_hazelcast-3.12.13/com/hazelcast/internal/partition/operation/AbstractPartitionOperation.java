/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.internal.partition.NonFragmentedServiceNamespace;
import com.hazelcast.internal.partition.impl.PartitionDataSerializerHook;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.FragmentedMigrationAwareService;
import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.servicemanager.ServiceInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

abstract class AbstractPartitionOperation
extends Operation
implements IdentifiedDataSerializable {
    AbstractPartitionOperation() {
    }

    final Collection<MigrationAwareService> getMigrationAwareServices() {
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        return nodeEngine.getServices(MigrationAwareService.class);
    }

    final Collection<Operation> createAllReplicationOperations(PartitionReplicationEvent event) {
        return this.createReplicationOperations(event, false);
    }

    final Collection<Operation> createNonFragmentedReplicationOperations(PartitionReplicationEvent event) {
        return this.createReplicationOperations(event, true);
    }

    private Collection<Operation> createReplicationOperations(PartitionReplicationEvent event, boolean nonFragmentedOnly) {
        ArrayList<Operation> operations = new ArrayList<Operation>();
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        Collection<ServiceInfo> services = nodeEngine.getServiceInfos(MigrationAwareService.class);
        for (ServiceInfo serviceInfo : services) {
            Operation op;
            MigrationAwareService service = (MigrationAwareService)serviceInfo.getService();
            if (nonFragmentedOnly && service instanceof FragmentedMigrationAwareService || (op = service.prepareReplicationOperation(event)) == null) continue;
            op.setServiceName(serviceInfo.getName());
            operations.add(op);
        }
        return operations;
    }

    final Collection<Operation> createFragmentReplicationOperations(PartitionReplicationEvent event, ServiceNamespace ns, Collection<String> serviceNames) {
        assert (!(ns instanceof NonFragmentedServiceNamespace)) : ns + " should be used only for non-fragmented services!";
        Collection<Operation> operations = Collections.emptySet();
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        for (String serviceName : serviceNames) {
            FragmentedMigrationAwareService service = (FragmentedMigrationAwareService)nodeEngine.getService(serviceName);
            assert (service.isKnownServiceNamespace(ns)) : ns + " should be known by " + service;
            operations = this.prepareAndAppendReplicationOperation(event, ns, service, serviceName, operations);
        }
        return operations;
    }

    final Collection<Operation> createFragmentReplicationOperations(PartitionReplicationEvent event, ServiceNamespace ns) {
        assert (!(ns instanceof NonFragmentedServiceNamespace)) : ns + " should be used only for non-fragmented services!";
        Collection<Operation> operations = Collections.emptySet();
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        Collection<ServiceInfo> services = nodeEngine.getServiceInfos(FragmentedMigrationAwareService.class);
        for (ServiceInfo serviceInfo : services) {
            FragmentedMigrationAwareService service = (FragmentedMigrationAwareService)serviceInfo.getService();
            if (!service.isKnownServiceNamespace(ns)) continue;
            operations = this.prepareAndAppendReplicationOperation(event, ns, service, serviceInfo.getName(), operations);
        }
        return operations;
    }

    private Collection<Operation> prepareAndAppendReplicationOperation(PartitionReplicationEvent event, ServiceNamespace ns, FragmentedMigrationAwareService service, String serviceName, Collection<Operation> operations) {
        Operation op = service.prepareReplicationOperation(event, Collections.singleton(ns));
        if (op == null) {
            return operations;
        }
        op.setServiceName(serviceName);
        if (operations.isEmpty()) {
            operations = Collections.singleton(op);
        } else if (operations.size() == 1) {
            operations = new ArrayList<Operation>(operations);
            operations.add(op);
        } else {
            operations.add(op);
        }
        return operations;
    }

    @Override
    public final int getFactoryId() {
        return PartitionDataSerializerHook.F_ID;
    }
}

