/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.cci.ConnectionFactory
 *  javax.resource.cci.InteractionSpec
 *  javax.resource.cci.Record
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jca.cci.object;

import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;
import org.springframework.dao.DataAccessException;
import org.springframework.jca.cci.object.EisOperation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
public class SimpleRecordOperation
extends EisOperation {
    public SimpleRecordOperation() {
    }

    public SimpleRecordOperation(ConnectionFactory connectionFactory, InteractionSpec interactionSpec) {
        this.getCciTemplate().setConnectionFactory(connectionFactory);
        this.setInteractionSpec(interactionSpec);
    }

    @Nullable
    public Record execute(Record inputRecord) throws DataAccessException {
        InteractionSpec interactionSpec = this.getInteractionSpec();
        Assert.state((interactionSpec != null ? 1 : 0) != 0, (String)"No InteractionSpec set");
        return this.getCciTemplate().execute(interactionSpec, inputRecord);
    }

    public void execute(Record inputRecord, Record outputRecord) throws DataAccessException {
        InteractionSpec interactionSpec = this.getInteractionSpec();
        Assert.state((interactionSpec != null ? 1 : 0) != 0, (String)"No InteractionSpec set");
        this.getCciTemplate().execute(interactionSpec, inputRecord, outputRecord);
    }
}

