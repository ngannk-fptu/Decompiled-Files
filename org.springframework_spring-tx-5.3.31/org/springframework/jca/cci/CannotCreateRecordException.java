/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 */
package org.springframework.jca.cci;

import javax.resource.ResourceException;
import org.springframework.dao.DataAccessResourceFailureException;

@Deprecated
public class CannotCreateRecordException
extends DataAccessResourceFailureException {
    public CannotCreateRecordException(String msg, ResourceException ex) {
        super(msg, ex);
    }
}

