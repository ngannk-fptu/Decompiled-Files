/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.cci.ConnectionFactory
 *  javax.resource.cci.InteractionSpec
 *  javax.resource.cci.Record
 *  javax.resource.cci.RecordFactory
 */
package org.springframework.jca.cci.object;

import java.io.IOException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;
import javax.resource.cci.RecordFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jca.cci.core.support.CommAreaRecord;
import org.springframework.jca.cci.object.MappingRecordOperation;

@Deprecated
public abstract class MappingCommAreaOperation
extends MappingRecordOperation {
    public MappingCommAreaOperation() {
    }

    public MappingCommAreaOperation(ConnectionFactory connectionFactory, InteractionSpec interactionSpec) {
        super(connectionFactory, interactionSpec);
    }

    @Override
    protected final Record createInputRecord(RecordFactory recordFactory, Object inObject) {
        try {
            return new CommAreaRecord(this.objectToBytes(inObject));
        }
        catch (IOException ex) {
            throw new DataRetrievalFailureException("I/O exception during bytes conversion", ex);
        }
    }

    @Override
    protected final Object extractOutputData(Record record) throws DataAccessException {
        CommAreaRecord commAreaRecord = (CommAreaRecord)record;
        try {
            return this.bytesToObject(commAreaRecord.toByteArray());
        }
        catch (IOException ex) {
            throw new DataRetrievalFailureException("I/O exception during bytes conversion", ex);
        }
    }

    protected abstract byte[] objectToBytes(Object var1) throws IOException, DataAccessException;

    protected abstract Object bytesToObject(byte[] var1) throws IOException, DataAccessException;
}

