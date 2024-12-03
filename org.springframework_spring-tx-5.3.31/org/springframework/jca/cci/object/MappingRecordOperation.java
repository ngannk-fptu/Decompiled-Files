/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 *  javax.resource.cci.ConnectionFactory
 *  javax.resource.cci.InteractionSpec
 *  javax.resource.cci.Record
 *  javax.resource.cci.RecordFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jca.cci.object;

import java.sql.SQLException;
import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;
import javax.resource.cci.RecordFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jca.cci.core.RecordCreator;
import org.springframework.jca.cci.core.RecordExtractor;
import org.springframework.jca.cci.object.EisOperation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
public abstract class MappingRecordOperation
extends EisOperation {
    public MappingRecordOperation() {
    }

    public MappingRecordOperation(ConnectionFactory connectionFactory, InteractionSpec interactionSpec) {
        this.getCciTemplate().setConnectionFactory(connectionFactory);
        this.setInteractionSpec(interactionSpec);
    }

    public void setOutputRecordCreator(RecordCreator creator) {
        this.getCciTemplate().setOutputRecordCreator(creator);
    }

    @Nullable
    public Object execute(Object inputObject) throws DataAccessException {
        InteractionSpec interactionSpec = this.getInteractionSpec();
        Assert.state((interactionSpec != null ? 1 : 0) != 0, (String)"No InteractionSpec set");
        return this.getCciTemplate().execute(interactionSpec, new RecordCreatorImpl(inputObject), new RecordExtractorImpl());
    }

    protected abstract Record createInputRecord(RecordFactory var1, Object var2) throws ResourceException, DataAccessException;

    protected abstract Object extractOutputData(Record var1) throws ResourceException, SQLException, DataAccessException;

    protected class RecordExtractorImpl
    implements RecordExtractor<Object> {
        protected RecordExtractorImpl() {
        }

        @Override
        public Object extractData(Record record) throws ResourceException, SQLException, DataAccessException {
            return MappingRecordOperation.this.extractOutputData(record);
        }
    }

    protected class RecordCreatorImpl
    implements RecordCreator {
        private final Object inputObject;

        public RecordCreatorImpl(Object inObject) {
            this.inputObject = inObject;
        }

        @Override
        public Record createRecord(RecordFactory recordFactory) throws ResourceException, DataAccessException {
            return MappingRecordOperation.this.createInputRecord(recordFactory, this.inputObject);
        }
    }
}

