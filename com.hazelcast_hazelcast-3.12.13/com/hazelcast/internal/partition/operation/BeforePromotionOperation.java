/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.core.MigrationEvent;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.PartitionStateManager;
import com.hazelcast.internal.partition.operation.AbstractPromotionOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.PartitionMigrationEvent;

final class BeforePromotionOperation
extends AbstractPromotionOperation {
    private Runnable beforePromotionsCallback;

    public BeforePromotionOperation() {
        super(null);
    }

    BeforePromotionOperation(MigrationInfo migrationInfo, Runnable beforePromotionsCallback) {
        super(migrationInfo);
        this.beforePromotionsCallback = beforePromotionsCallback;
    }

    @Override
    public void beforeRun() throws Exception {
        this.sendMigrationEvent(MigrationEvent.MigrationStatus.STARTED);
        InternalPartitionServiceImpl service = (InternalPartitionServiceImpl)this.getService();
        PartitionStateManager partitionStateManager = service.getPartitionStateManager();
        if (!partitionStateManager.trySetMigratingFlag(this.getPartitionId())) {
            throw new IllegalStateException("Cannot set migrating flag, probably previous migration's finalization is not completed yet.");
        }
    }

    @Override
    public void run() throws Exception {
        ILogger logger = this.getLogger();
        if (logger.isFinestEnabled()) {
            logger.finest("Running before promotion for " + this.getPartitionMigrationEvent());
        }
        PartitionMigrationEvent event = this.getPartitionMigrationEvent();
        for (MigrationAwareService service : this.getMigrationAwareServices()) {
            try {
                service.beforeMigration(event);
            }
            catch (Throwable e) {
                logger.warning("While promoting " + this.getPartitionMigrationEvent(), e);
            }
        }
    }

    @Override
    public void afterRun() throws Exception {
        if (this.beforePromotionsCallback != null) {
            this.beforePromotionsCallback.run();
        }
    }
}

