/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 */
package org.springframework.jca.cci;

import javax.resource.ResourceException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

@Deprecated
public class RecordTypeNotSupportedException
extends InvalidDataAccessResourceUsageException {
    public RecordTypeNotSupportedException(String msg, ResourceException ex) {
        super(msg, ex);
    }
}

