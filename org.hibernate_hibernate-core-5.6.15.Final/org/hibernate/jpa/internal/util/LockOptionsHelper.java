/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 *  javax.persistence.PessimisticLockScope
 */
package org.hibernate.jpa.internal.util;

import java.util.Map;
import java.util.function.Supplier;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockScope;
import org.hibernate.LockOptions;

public final class LockOptionsHelper {
    private LockOptionsHelper() {
    }

    public static void applyPropertiesToLockOptions(Map<String, Object> props, Supplier<LockOptions> lockOptionsSupplier) {
        String lockScopeHint = "javax.persistence.lock.scope";
        Object lockScope = props.get(lockScopeHint);
        if (lockScope == null) {
            lockScopeHint = "jakarta.persistence.lock.scope";
            lockScope = props.get(lockScopeHint);
        }
        if (lockScope instanceof String && PessimisticLockScope.valueOf((String)((String)lockScope)) == PessimisticLockScope.EXTENDED) {
            lockOptionsSupplier.get().setScope(true);
        } else if (lockScope instanceof PessimisticLockScope) {
            boolean extended = PessimisticLockScope.EXTENDED.equals(lockScope);
            lockOptionsSupplier.get().setScope(extended);
        } else if (lockScope != null) {
            throw new PersistenceException("Unable to parse " + lockScopeHint + ": " + lockScope);
        }
        String timeoutHint = "javax.persistence.lock.timeout";
        Object lockTimeout = props.get(timeoutHint);
        if (lockTimeout == null) {
            timeoutHint = "jakarta.persistence.lock.timeout";
            lockTimeout = props.get(timeoutHint);
        }
        int timeout = 0;
        boolean timeoutSet = false;
        if (lockTimeout instanceof String) {
            timeout = Integer.parseInt((String)lockTimeout);
            timeoutSet = true;
        } else if (lockTimeout instanceof Number) {
            timeout = ((Number)lockTimeout).intValue();
            timeoutSet = true;
        } else if (lockTimeout != null) {
            throw new PersistenceException("Unable to parse " + timeoutHint + ": " + lockTimeout);
        }
        if (timeoutSet) {
            if (timeout == -2) {
                lockOptionsSupplier.get().setTimeOut(-2);
            } else if (timeout < 0) {
                lockOptionsSupplier.get().setTimeOut(-1);
            } else if (timeout == 0) {
                lockOptionsSupplier.get().setTimeOut(0);
            } else {
                lockOptionsSupplier.get().setTimeOut(timeout);
            }
        }
    }
}

