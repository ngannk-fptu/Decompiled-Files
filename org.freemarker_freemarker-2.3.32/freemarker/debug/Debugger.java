/*
 * Decompiled with CFR 0.152.
 */
package freemarker.debug;

import freemarker.debug.Breakpoint;
import freemarker.debug.DebuggerListener;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

public interface Debugger
extends Remote {
    public static final int DEFAULT_PORT = 7011;

    public void addBreakpoint(Breakpoint var1) throws RemoteException;

    public void removeBreakpoint(Breakpoint var1) throws RemoteException;

    public void removeBreakpoints(String var1) throws RemoteException;

    public void removeBreakpoints() throws RemoteException;

    public List getBreakpoints() throws RemoteException;

    public List getBreakpoints(String var1) throws RemoteException;

    public Collection getSuspendedEnvironments() throws RemoteException;

    public Object addDebuggerListener(DebuggerListener var1) throws RemoteException;

    public void removeDebuggerListener(Object var1) throws RemoteException;
}

