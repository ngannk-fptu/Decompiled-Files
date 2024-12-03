/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.gmbal;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.management.ObjectName;
import org.glassfish.gmbal.GmbalException;
import org.glassfish.gmbal.ManagedObjectManager;
import org.glassfish.gmbal.ManagedObjectManagerNOPImpl;
import org.glassfish.gmbal.util.GenericConstructor;

public final class ManagedObjectManagerFactory {
    private static GenericConstructor<ManagedObjectManager> objectNameCons = new GenericConstructor<ManagedObjectManager>(ManagedObjectManager.class, "org.glassfish.gmbal.impl.ManagedObjectManagerImpl", ObjectName.class);
    private static GenericConstructor<ManagedObjectManager> stringCons = new GenericConstructor<ManagedObjectManager>(ManagedObjectManager.class, "org.glassfish.gmbal.impl.ManagedObjectManagerImpl", String.class);

    private ManagedObjectManagerFactory() {
    }

    public static Method getMethod(final Class<?> cls, final String name, final Class<?> ... types) {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    return cls.getDeclaredMethod(name, types);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw new GmbalException("Unexpected exception", ex);
        }
        catch (SecurityException exc) {
            throw new GmbalException("Unexpected exception", exc);
        }
    }

    public static ManagedObjectManager createStandalone(String domain) {
        ManagedObjectManager result = stringCons.create(domain);
        if (result == null) {
            return ManagedObjectManagerNOPImpl.self;
        }
        return result;
    }

    public static ManagedObjectManager createFederated(ObjectName rootParentName) {
        ManagedObjectManager result = objectNameCons.create(rootParentName);
        if (result == null) {
            return ManagedObjectManagerNOPImpl.self;
        }
        return result;
    }

    public static ManagedObjectManager createNOOP() {
        return ManagedObjectManagerNOPImpl.self;
    }
}

