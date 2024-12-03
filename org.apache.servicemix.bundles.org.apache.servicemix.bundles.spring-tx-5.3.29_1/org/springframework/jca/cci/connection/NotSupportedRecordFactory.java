/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.NotSupportedException
 *  javax.resource.ResourceException
 *  javax.resource.cci.IndexedRecord
 *  javax.resource.cci.MappedRecord
 *  javax.resource.cci.RecordFactory
 */
package org.springframework.jca.cci.connection;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.RecordFactory;

@Deprecated
public class NotSupportedRecordFactory
implements RecordFactory {
    public MappedRecord createMappedRecord(String name) throws ResourceException {
        throw new NotSupportedException("The RecordFactory facility is not supported by the connector");
    }

    public IndexedRecord createIndexedRecord(String name) throws ResourceException {
        throw new NotSupportedException("The RecordFactory facility is not supported by the connector");
    }
}

