/*
 * Decompiled with CFR 0.152.
 */
package freemarker.debug;

import freemarker.debug.EnvironmentSuspendedEvent;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

public interface DebuggerListener
extends Remote,
EventListener {
    public void environmentSuspended(EnvironmentSuspendedEvent var1) throws RemoteException;
}

