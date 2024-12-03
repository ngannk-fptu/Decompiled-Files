/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import org.springframework.lang.Nullable;
import org.springframework.remoting.rmi.RmiBasedExporter;
import org.springframework.remoting.rmi.RmiInvocationHandler;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.util.Assert;

@Deprecated
class RmiInvocationWrapper
implements RmiInvocationHandler {
    private final Object wrappedObject;
    private final RmiBasedExporter rmiExporter;

    public RmiInvocationWrapper(Object wrappedObject, RmiBasedExporter rmiExporter) {
        Assert.notNull(wrappedObject, "Object to wrap is required");
        Assert.notNull((Object)rmiExporter, "RMI exporter is required");
        this.wrappedObject = wrappedObject;
        this.rmiExporter = rmiExporter;
    }

    @Override
    @Nullable
    public String getTargetInterfaceName() {
        Class<?> ifc = this.rmiExporter.getServiceInterface();
        return ifc != null ? ifc.getName() : null;
    }

    @Override
    @Nullable
    public Object invoke(RemoteInvocation invocation) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return this.rmiExporter.invoke(invocation, this.wrappedObject);
    }
}

