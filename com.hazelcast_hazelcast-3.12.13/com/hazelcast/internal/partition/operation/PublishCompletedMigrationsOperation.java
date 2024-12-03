/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.exception.TargetNotMemberException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class PublishCompletedMigrationsOperation
extends AbstractPartitionOperation
implements MigrationCycleOperation {
    private Collection<MigrationInfo> completedMigrations;
    private transient boolean success;

    public PublishCompletedMigrationsOperation() {
    }

    public PublishCompletedMigrationsOperation(Collection<MigrationInfo> completedMigrations) {
        this.completedMigrations = completedMigrations;
    }

    @Override
    public void run() {
        InternalPartitionServiceImpl service = (InternalPartitionServiceImpl)this.getService();
        this.success = service.applyCompletedMigrations(this.completedMigrations, this.getCallerAddress());
    }

    @Override
    public Object getResponse() {
        return this.success;
    }

    @Override
    public String getServiceName() {
        return "hz:core:partitionService";
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof MemberLeftException || throwable instanceof TargetNotMemberException) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int len = in.readInt();
        this.completedMigrations = new ArrayList<MigrationInfo>(len);
        for (int i = 0; i < len; ++i) {
            MigrationInfo migrationInfo = (MigrationInfo)in.readObject();
            this.completedMigrations.add(migrationInfo);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        int len = this.completedMigrations.size();
        out.writeInt(len);
        for (MigrationInfo migrationInfo : this.completedMigrations) {
            out.writeObject(migrationInfo);
        }
    }

    @Override
    public int getId() {
        return 22;
    }
}

