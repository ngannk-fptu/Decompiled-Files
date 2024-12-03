/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 *  javax.resource.cci.Record
 *  javax.resource.cci.RecordFactory
 */
package org.springframework.jca.cci.core;

import javax.resource.ResourceException;
import javax.resource.cci.Record;
import javax.resource.cci.RecordFactory;
import org.springframework.dao.DataAccessException;

@Deprecated
@FunctionalInterface
public interface RecordCreator {
    public Record createRecord(RecordFactory var1) throws ResourceException, DataAccessException;
}

