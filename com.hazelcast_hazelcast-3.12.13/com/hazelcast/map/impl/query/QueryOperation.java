/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.core.MemberLeftException;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.QueryRunner;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.exception.TargetNotMemberException;
import java.io.IOException;

public class QueryOperation
extends MapOperation
implements ReadonlyOperation {
    private Query query;
    private Result result;

    public QueryOperation() {
    }

    public QueryOperation(Query query) {
        super(query.getMapName());
        this.query = query;
    }

    @Override
    public void run() throws Exception {
        QueryRunner queryRunner = this.mapServiceContext.getMapQueryRunner(this.getName());
        this.result = queryRunner.runIndexOrPartitionScanQueryOnOwnedPartitions(this.query);
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof MemberLeftException || throwable instanceof TargetNotMemberException) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }

    @Override
    public Object getResponse() {
        return this.result;
    }

    @Override
    public int getId() {
        return 60;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.query);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.query = (Query)in.readObject();
    }
}

