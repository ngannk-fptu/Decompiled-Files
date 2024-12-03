/*
 * Decompiled with CFR 0.152.
 */
package freemarker.debug.impl;

import freemarker.debug.DebuggerListener;
import freemarker.debug.EnvironmentSuspendedEvent;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;

public final class RmiDebuggerListenerImpl_Stub
extends RemoteStub
implements DebuggerListener,
Remote {
    private static final long serialVersionUID = 2L;
    private static Method $method_environmentSuspended_0;
    static /* synthetic */ Class class$freemarker$debug$DebuggerListener;
    static /* synthetic */ Class class$freemarker$debug$EnvironmentSuspendedEvent;

    static {
        try {
            $method_environmentSuspended_0 = (class$freemarker$debug$DebuggerListener != null ? class$freemarker$debug$DebuggerListener : (class$freemarker$debug$DebuggerListener = RmiDebuggerListenerImpl_Stub.class$("freemarker.debug.DebuggerListener"))).getMethod("environmentSuspended", class$freemarker$debug$EnvironmentSuspendedEvent != null ? class$freemarker$debug$EnvironmentSuspendedEvent : (class$freemarker$debug$EnvironmentSuspendedEvent = RmiDebuggerListenerImpl_Stub.class$("freemarker.debug.EnvironmentSuspendedEvent")));
        }
        catch (NoSuchMethodException noSuchMethodException) {
            throw new NoSuchMethodError("stub class initialization failed");
        }
    }

    public RmiDebuggerListenerImpl_Stub(RemoteRef remoteRef) {
        super(remoteRef);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    public void environmentSuspended(EnvironmentSuspendedEvent environmentSuspendedEvent) throws RemoteException {
        try {
            this.ref.invoke(this, $method_environmentSuspended_0, new Object[]{environmentSuspendedEvent}, -2541155567719209082L);
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }
}

