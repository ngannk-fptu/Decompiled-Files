/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.SystemException
 */
package org.hibernate.resource.transaction.backend.jta.internal.synchronization;

import java.io.Serializable;
import javax.transaction.SystemException;
import org.hibernate.engine.spi.SessionImplementor;

public interface ExceptionMapper
extends Serializable {
    public RuntimeException mapStatusCheckFailure(String var1, SystemException var2, SessionImplementor var3);

    public RuntimeException mapManagedFlushFailure(String var1, RuntimeException var2, SessionImplementor var3);
}

