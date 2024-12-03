/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.NonFragmentedServiceNamespace;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.PartitionReplicaVersionManager;
import com.hazelcast.internal.partition.ReplicaFragmentMigrationState;
import com.hazelcast.internal.partition.impl.InternalMigrationListener;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.MigrationManager;
import com.hazelcast.internal.partition.operation.BaseMigrationOperation;
import com.hazelcast.internal.partition.operation.MigrationOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.CallStatus;
import com.hazelcast.spi.FragmentedMigrationAwareService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Offload;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.spi.impl.SimpleExecutionCallback;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.servicemanager.ServiceInfo;
import com.hazelcast.spi.partition.MigrationEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class MigrationRequestOperation
extends BaseMigrationOperation {
    private boolean fragmentedMigrationEnabled;
    private transient ServiceNamespacesContext namespacesContext;

    public MigrationRequestOperation() {
    }

    public MigrationRequestOperation(MigrationInfo migrationInfo, List<MigrationInfo> completedMigrations, int partitionStateVersion, boolean fragmentedMigrationEnabled) {
        super(migrationInfo, completedMigrations, partitionStateVersion);
        this.fragmentedMigrationEnabled = fragmentedMigrationEnabled;
    }

    @Override
    public CallStatus call() throws Exception {
        this.setActiveMigration();
        if (!this.migrationInfo.startProcessing()) {
            this.getLogger().warning("Migration is cancelled -> " + this.migrationInfo);
            this.completeMigration(false);
            return CallStatus.DONE_VOID;
        }
        return new OffloadImpl();
    }

    @Override
    void executeBeforeMigrations() throws Exception {
        boolean ownerMigration;
        NodeEngine nodeEngine = this.getNodeEngine();
        PartitionReplica source = this.migrationInfo.getSource();
        boolean bl = ownerMigration = source != null && source.isIdentical(nodeEngine.getLocalMember());
        if (!ownerMigration) {
            return;
        }
        super.executeBeforeMigrations();
    }

    private void invokeMigrationOperation(ReplicaFragmentMigrationState migrationState, boolean firstFragment) {
        boolean lastFragment = !this.fragmentedMigrationEnabled || !this.namespacesContext.hasNext();
        MigrationOperation operation = new MigrationOperation(this.migrationInfo, firstFragment ? this.completedMigrations : Collections.emptyList(), this.partitionStateVersion, migrationState, firstFragment, lastFragment);
        ILogger logger = this.getLogger();
        if (logger.isFinestEnabled()) {
            Set namespaces = migrationState != null ? migrationState.getNamespaceVersionMap().keySet() : Collections.emptySet();
            logger.finest("Invoking MigrationOperation for namespaces " + namespaces + " and " + this.migrationInfo + ", lastFragment: " + lastFragment);
        }
        NodeEngine nodeEngine = this.getNodeEngine();
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        Address target = this.migrationInfo.getDestinationAddress();
        nodeEngine.getOperationService().createInvocationBuilder("hz:core:partitionService", (Operation)operation, target).setExecutionCallback(new MigrationCallback()).setResultDeserialized(true).setCallTimeout(partitionService.getPartitionMigrationTimeout()).setTryCount(12).setTryPauseMillis(10000L).invoke();
    }

    private void trySendNewFragment() {
        try {
            assert (this.fragmentedMigrationEnabled) : "Fragmented migration should be enabled!";
            this.verifyMaster();
            this.verifyExistingDestination();
            InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
            MigrationManager migrationManager = partitionService.getMigrationManager();
            MigrationInfo currentActiveMigration = migrationManager.setActiveMigration(this.migrationInfo);
            if (!this.migrationInfo.equals(currentActiveMigration)) {
                throw new IllegalStateException("Current active migration " + currentActiveMigration + " is different than expected: " + this.migrationInfo);
            }
            ReplicaFragmentMigrationState migrationState = this.createNextReplicaFragmentMigrationState();
            if (migrationState != null) {
                this.invokeMigrationOperation(migrationState, false);
            } else {
                this.getLogger().finest("All migration fragments done for " + this.migrationInfo);
                this.completeMigration(true);
            }
        }
        catch (Throwable e) {
            this.logThrowable(e);
            this.completeMigration(false);
        }
    }

    private ReplicaFragmentMigrationState createNextReplicaFragmentMigrationState() {
        assert (this.fragmentedMigrationEnabled) : "Fragmented migration should be enabled!";
        if (!this.namespacesContext.hasNext()) {
            return null;
        }
        ServiceNamespace namespace = this.namespacesContext.next();
        if (namespace.equals(NonFragmentedServiceNamespace.INSTANCE)) {
            return this.createNonFragmentedReplicaFragmentMigrationState();
        }
        return this.createReplicaFragmentMigrationStateFor(namespace);
    }

    private ReplicaFragmentMigrationState createNonFragmentedReplicaFragmentMigrationState() {
        PartitionReplicationEvent event = this.getPartitionReplicationEvent();
        Collection<Operation> operations = this.createNonFragmentedReplicationOperations(event);
        Set<ServiceNamespace> namespaces = Collections.singleton(NonFragmentedServiceNamespace.INSTANCE);
        return this.createReplicaFragmentMigrationState(namespaces, operations);
    }

    private ReplicaFragmentMigrationState createReplicaFragmentMigrationStateFor(ServiceNamespace ns) {
        PartitionReplicationEvent event = this.getPartitionReplicationEvent();
        Collection<String> serviceNames = this.namespacesContext.getServiceNames(ns);
        Collection<Operation> operations = this.createFragmentReplicationOperations(event, ns, serviceNames);
        return this.createReplicaFragmentMigrationState(Collections.singleton(ns), operations);
    }

    private ReplicaFragmentMigrationState createAllReplicaFragmentsMigrationState() {
        PartitionReplicationEvent event = this.getPartitionReplicationEvent();
        Collection<Operation> operations = this.createAllReplicationOperations(event);
        return this.createReplicaFragmentMigrationState(this.namespacesContext.allNamespaces, operations);
    }

    private ReplicaFragmentMigrationState createReplicaFragmentMigrationState(Collection<ServiceNamespace> namespaces, Collection<Operation> operations) {
        InternalPartitionService partitionService = (InternalPartitionService)this.getService();
        PartitionReplicaVersionManager versionManager = partitionService.getPartitionReplicaVersionManager();
        HashMap<ServiceNamespace, long[]> versions = new HashMap<ServiceNamespace, long[]>(namespaces.size());
        for (ServiceNamespace namespace : namespaces) {
            long[] v = versionManager.getPartitionReplicaVersions(this.getPartitionId(), namespace);
            versions.put(namespace, v);
        }
        return new ReplicaFragmentMigrationState(versions, operations);
    }

    @Override
    protected PartitionMigrationEvent getMigrationEvent() {
        return new PartitionMigrationEvent(MigrationEndpoint.SOURCE, this.migrationInfo.getPartitionId(), this.migrationInfo.getSourceCurrentReplicaIndex(), this.migrationInfo.getSourceNewReplicaIndex());
    }

    @Override
    protected InternalMigrationListener.MigrationParticipant getMigrationParticipantType() {
        return InternalMigrationListener.MigrationParticipant.SOURCE;
    }

    private PartitionReplicationEvent getPartitionReplicationEvent() {
        return new PartitionReplicationEvent(this.migrationInfo.getPartitionId(), this.migrationInfo.getDestinationNewReplicaIndex());
    }

    private void completeMigration(boolean result) {
        this.success = result;
        this.migrationInfo.doneProcessing();
        this.onMigrationComplete();
        this.sendResponse(result);
    }

    private void logThrowable(Throwable t) {
        Throwable throwableToLog = t;
        if (throwableToLog instanceof ExecutionException) {
            throwableToLog = throwableToLog.getCause() != null ? throwableToLog.getCause() : throwableToLog;
        }
        Level level = this.getLogLevel(throwableToLog);
        this.getLogger().log(level, throwableToLog.getMessage(), throwableToLog);
    }

    private Level getLogLevel(Throwable e) {
        return e instanceof MemberLeftException || e instanceof InterruptedException || !this.getNodeEngine().isRunning() ? Level.INFO : Level.WARNING;
    }

    @Override
    public int getId() {
        return 19;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.fragmentedMigrationEnabled);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.fragmentedMigrationEnabled = in.readBoolean();
    }

    private static class ServiceNamespacesContext {
        final Collection<ServiceNamespace> allNamespaces = new HashSet<ServiceNamespace>();
        final Map<ServiceNamespace, Collection<String>> namespaceToServices = new HashMap<ServiceNamespace, Collection<String>>();
        final Iterator<ServiceNamespace> namespaceIterator;

        ServiceNamespacesContext(NodeEngineImpl nodeEngine, PartitionReplicationEvent event) {
            Collection<ServiceInfo> services = nodeEngine.getServiceInfos(FragmentedMigrationAwareService.class);
            for (ServiceInfo serviceInfo : services) {
                FragmentedMigrationAwareService service = (FragmentedMigrationAwareService)serviceInfo.getService();
                Collection<ServiceNamespace> namespaces = service.getAllServiceNamespaces(event);
                if (namespaces == null) continue;
                String serviceName = serviceInfo.getName();
                this.allNamespaces.addAll(namespaces);
                this.addNamespaceToServiceMappings(namespaces, serviceName);
            }
            this.allNamespaces.add(NonFragmentedServiceNamespace.INSTANCE);
            this.namespaceIterator = this.allNamespaces.iterator();
        }

        private void addNamespaceToServiceMappings(Collection<ServiceNamespace> namespaces, String serviceName) {
            for (ServiceNamespace ns : namespaces) {
                Collection<String> serviceNames = this.namespaceToServices.get(ns);
                if (serviceNames == null) {
                    this.namespaceToServices.put(ns, Collections.singleton(serviceName));
                    continue;
                }
                if (serviceNames.size() == 1) {
                    serviceNames = new HashSet<String>(serviceNames);
                    serviceNames.add(serviceName);
                    this.namespaceToServices.put(ns, serviceNames);
                    continue;
                }
                serviceNames.add(serviceName);
            }
        }

        boolean hasNext() {
            return this.namespaceIterator.hasNext();
        }

        ServiceNamespace next() {
            return this.namespaceIterator.next();
        }

        Collection<String> getServiceNames(ServiceNamespace ns) {
            return this.namespaceToServices.get(ns);
        }
    }

    private final class SendNewMigrationFragmentRunnable
    implements PartitionSpecificRunnable,
    UrgentSystemOperation {
        private SendNewMigrationFragmentRunnable() {
        }

        @Override
        public int getPartitionId() {
            return MigrationRequestOperation.this.getPartitionId();
        }

        @Override
        public void run() {
            MigrationRequestOperation.this.trySendNewFragment();
        }
    }

    private final class MigrationCallback
    extends SimpleExecutionCallback<Object> {
        private MigrationCallback() {
        }

        @Override
        public void notify(Object result) {
            if (Boolean.TRUE.equals(result)) {
                if (MigrationRequestOperation.this.fragmentedMigrationEnabled) {
                    InternalOperationService operationService = (InternalOperationService)MigrationRequestOperation.this.getNodeEngine().getOperationService();
                    operationService.execute(new SendNewMigrationFragmentRunnable());
                } else {
                    MigrationRequestOperation.this.completeMigration(true);
                }
            } else {
                ILogger logger = MigrationRequestOperation.this.getLogger();
                if (logger.isFineEnabled()) {
                    logger.fine("Received false response from migration destination -> " + MigrationRequestOperation.this.migrationInfo);
                }
                MigrationRequestOperation.this.completeMigration(false);
            }
        }
    }

    private final class OffloadImpl
    extends Offload {
        private OffloadImpl() {
            super(MigrationRequestOperation.this);
        }

        @Override
        public void start() {
            NodeEngineImpl nodeEngine = (NodeEngineImpl)MigrationRequestOperation.this.getNodeEngine();
            try {
                MigrationRequestOperation.this.executeBeforeMigrations();
                MigrationRequestOperation.this.namespacesContext = new ServiceNamespacesContext(nodeEngine, MigrationRequestOperation.this.getPartitionReplicationEvent());
                ReplicaFragmentMigrationState migrationState = MigrationRequestOperation.this.fragmentedMigrationEnabled ? MigrationRequestOperation.this.createNextReplicaFragmentMigrationState() : MigrationRequestOperation.this.createAllReplicaFragmentsMigrationState();
                MigrationRequestOperation.this.invokeMigrationOperation(migrationState, true);
            }
            catch (Throwable e) {
                MigrationRequestOperation.this.logThrowable(e);
                MigrationRequestOperation.this.completeMigration(false);
            }
            finally {
                MigrationRequestOperation.this.migrationInfo.doneProcessing();
            }
        }
    }
}

