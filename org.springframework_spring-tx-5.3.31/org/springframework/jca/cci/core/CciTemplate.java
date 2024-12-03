/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.NotSupportedException
 *  javax.resource.ResourceException
 *  javax.resource.cci.Connection
 *  javax.resource.cci.ConnectionFactory
 *  javax.resource.cci.ConnectionSpec
 *  javax.resource.cci.IndexedRecord
 *  javax.resource.cci.Interaction
 *  javax.resource.cci.InteractionSpec
 *  javax.resource.cci.MappedRecord
 *  javax.resource.cci.Record
 *  javax.resource.cci.RecordFactory
 *  javax.resource.cci.ResultSet
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jca.cci.core;

import java.sql.SQLException;
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.Record;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResultSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jca.cci.CannotCreateRecordException;
import org.springframework.jca.cci.CciOperationNotSupportedException;
import org.springframework.jca.cci.InvalidResultSetAccessException;
import org.springframework.jca.cci.RecordTypeNotSupportedException;
import org.springframework.jca.cci.connection.ConnectionFactoryUtils;
import org.springframework.jca.cci.connection.NotSupportedRecordFactory;
import org.springframework.jca.cci.core.CciOperations;
import org.springframework.jca.cci.core.ConnectionCallback;
import org.springframework.jca.cci.core.InteractionCallback;
import org.springframework.jca.cci.core.RecordCreator;
import org.springframework.jca.cci.core.RecordExtractor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
public class CciTemplate
implements CciOperations {
    private final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private ConnectionFactory connectionFactory;
    @Nullable
    private ConnectionSpec connectionSpec;
    @Nullable
    private RecordCreator outputRecordCreator;

    public CciTemplate() {
    }

    public CciTemplate(ConnectionFactory connectionFactory) {
        this.setConnectionFactory(connectionFactory);
        this.afterPropertiesSet();
    }

    public CciTemplate(ConnectionFactory connectionFactory, @Nullable ConnectionSpec connectionSpec) {
        this.setConnectionFactory(connectionFactory);
        if (connectionSpec != null) {
            this.setConnectionSpec(connectionSpec);
        }
        this.afterPropertiesSet();
    }

    public void setConnectionFactory(@Nullable ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Nullable
    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }

    private ConnectionFactory obtainConnectionFactory() {
        ConnectionFactory connectionFactory = this.getConnectionFactory();
        Assert.state((connectionFactory != null ? 1 : 0) != 0, (String)"No ConnectionFactory set");
        return connectionFactory;
    }

    public void setConnectionSpec(@Nullable ConnectionSpec connectionSpec) {
        this.connectionSpec = connectionSpec;
    }

    @Nullable
    public ConnectionSpec getConnectionSpec() {
        return this.connectionSpec;
    }

    public void setOutputRecordCreator(@Nullable RecordCreator creator) {
        this.outputRecordCreator = creator;
    }

    @Nullable
    public RecordCreator getOutputRecordCreator() {
        return this.outputRecordCreator;
    }

    public void afterPropertiesSet() {
        if (this.getConnectionFactory() == null) {
            throw new IllegalArgumentException("Property 'connectionFactory' is required");
        }
    }

    public CciTemplate getDerivedTemplate(ConnectionSpec connectionSpec) {
        CciTemplate derived = new CciTemplate(this.obtainConnectionFactory(), connectionSpec);
        RecordCreator recordCreator = this.getOutputRecordCreator();
        if (recordCreator != null) {
            derived.setOutputRecordCreator(recordCreator);
        }
        return derived;
    }

    @Override
    @Nullable
    public <T> T execute(ConnectionCallback<T> action) throws DataAccessException {
        Assert.notNull(action, (String)"Callback object must not be null");
        ConnectionFactory connectionFactory = this.obtainConnectionFactory();
        Connection con = ConnectionFactoryUtils.getConnection(connectionFactory, this.getConnectionSpec());
        try {
            T t = action.doInConnection(con, connectionFactory);
            return t;
        }
        catch (NotSupportedException ex) {
            throw new CciOperationNotSupportedException("CCI operation not supported by connector", (ResourceException)((Object)ex));
        }
        catch (ResourceException ex) {
            throw new DataAccessResourceFailureException("CCI operation failed", ex);
        }
        catch (SQLException ex) {
            throw new InvalidResultSetAccessException("Parsing of CCI ResultSet failed", ex);
        }
        finally {
            ConnectionFactoryUtils.releaseConnection(con, this.getConnectionFactory());
        }
    }

    @Override
    @Nullable
    public <T> T execute(InteractionCallback<T> action) throws DataAccessException {
        Assert.notNull(action, (String)"Callback object must not be null");
        return (T)this.execute((Connection connection, ConnectionFactory connectionFactory) -> {
            Interaction interaction = connection.createInteraction();
            try {
                Object t = action.doInInteraction(interaction, connectionFactory);
                return t;
            }
            finally {
                this.closeInteraction(interaction);
            }
        });
    }

    @Override
    @Nullable
    public Record execute(InteractionSpec spec, Record inputRecord) throws DataAccessException {
        return this.doExecute(spec, inputRecord, null, new SimpleRecordExtractor());
    }

    @Override
    public void execute(InteractionSpec spec, Record inputRecord, Record outputRecord) throws DataAccessException {
        this.doExecute(spec, inputRecord, outputRecord, null);
    }

    @Override
    public Record execute(InteractionSpec spec, RecordCreator inputCreator) throws DataAccessException {
        Record output = this.doExecute(spec, this.createRecord(inputCreator), null, new SimpleRecordExtractor());
        Assert.state((output != null ? 1 : 0) != 0, (String)"Invalid output record");
        return output;
    }

    @Override
    public <T> T execute(InteractionSpec spec, Record inputRecord, RecordExtractor<T> outputExtractor) throws DataAccessException {
        return this.doExecute(spec, inputRecord, null, outputExtractor);
    }

    @Override
    public <T> T execute(InteractionSpec spec, RecordCreator inputCreator, RecordExtractor<T> outputExtractor) throws DataAccessException {
        return this.doExecute(spec, this.createRecord(inputCreator), null, outputExtractor);
    }

    @Nullable
    protected <T> T doExecute(InteractionSpec spec, Record inputRecord, @Nullable Record outputRecord, @Nullable RecordExtractor<T> outputExtractor) throws DataAccessException {
        return (T)this.execute((Interaction interaction, ConnectionFactory connectionFactory) -> {
            Record outputRecordToUse = outputRecord;
            try {
                if (outputRecord != null || this.getOutputRecordCreator() != null) {
                    if (outputRecord == null) {
                        RecordFactory recordFactory = this.getRecordFactory(connectionFactory);
                        outputRecordToUse = this.getOutputRecordCreator().createRecord(recordFactory);
                    }
                    interaction.execute(spec, inputRecord, outputRecordToUse);
                } else {
                    outputRecordToUse = interaction.execute(spec, inputRecord);
                }
                Object var8_8 = outputExtractor != null ? outputExtractor.extractData(outputRecordToUse) : null;
                return var8_8;
            }
            finally {
                if (outputRecordToUse instanceof ResultSet) {
                    this.closeResultSet((ResultSet)outputRecordToUse);
                }
            }
        });
    }

    public IndexedRecord createIndexedRecord(String name) throws DataAccessException {
        try {
            RecordFactory recordFactory = this.getRecordFactory(this.obtainConnectionFactory());
            return recordFactory.createIndexedRecord(name);
        }
        catch (NotSupportedException ex) {
            throw new RecordTypeNotSupportedException("Creation of indexed Record not supported by connector", (ResourceException)((Object)ex));
        }
        catch (ResourceException ex) {
            throw new CannotCreateRecordException("Creation of indexed Record failed", ex);
        }
    }

    public MappedRecord createMappedRecord(String name) throws DataAccessException {
        try {
            RecordFactory recordFactory = this.getRecordFactory(this.obtainConnectionFactory());
            return recordFactory.createMappedRecord(name);
        }
        catch (NotSupportedException ex) {
            throw new RecordTypeNotSupportedException("Creation of mapped Record not supported by connector", (ResourceException)((Object)ex));
        }
        catch (ResourceException ex) {
            throw new CannotCreateRecordException("Creation of mapped Record failed", ex);
        }
    }

    protected Record createRecord(RecordCreator recordCreator) throws DataAccessException {
        try {
            RecordFactory recordFactory = this.getRecordFactory(this.obtainConnectionFactory());
            return recordCreator.createRecord(recordFactory);
        }
        catch (NotSupportedException ex) {
            throw new RecordTypeNotSupportedException("Creation of the desired Record type not supported by connector", (ResourceException)((Object)ex));
        }
        catch (ResourceException ex) {
            throw new CannotCreateRecordException("Creation of the desired Record failed", ex);
        }
    }

    protected RecordFactory getRecordFactory(ConnectionFactory connectionFactory) throws ResourceException {
        try {
            return connectionFactory.getRecordFactory();
        }
        catch (NotSupportedException ex) {
            return new NotSupportedRecordFactory();
        }
    }

    private void closeInteraction(@Nullable Interaction interaction) {
        if (interaction != null) {
            try {
                interaction.close();
            }
            catch (ResourceException ex) {
                this.logger.trace((Object)"Could not close CCI Interaction", (Throwable)ex);
            }
            catch (Throwable ex) {
                this.logger.trace((Object)"Unexpected exception on closing CCI Interaction", ex);
            }
        }
    }

    private void closeResultSet(@Nullable ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            }
            catch (SQLException ex) {
                this.logger.trace((Object)"Could not close CCI ResultSet", (Throwable)ex);
            }
            catch (Throwable ex) {
                this.logger.trace((Object)"Unexpected exception on closing CCI ResultSet", ex);
            }
        }
    }

    private static class SimpleRecordExtractor
    implements RecordExtractor<Record> {
        private SimpleRecordExtractor() {
        }

        @Override
        public Record extractData(Record record) {
            return record;
        }
    }
}

