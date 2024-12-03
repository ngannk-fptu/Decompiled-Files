/*
 * Decompiled with CFR 0.152.
 */
package freemarker.debug.impl;

import freemarker.debug.Breakpoint;
import freemarker.debug.Debugger;
import freemarker.debug.DebuggerListener;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import java.util.Collection;
import java.util.List;

public final class RmiDebuggerImpl_Stub
extends RemoteStub
implements Debugger,
Remote {
    private static final long serialVersionUID = 2L;
    private static Method $method_addBreakpoint_0;
    private static Method $method_addDebuggerListener_1;
    private static Method $method_getBreakpoints_2;
    private static Method $method_getBreakpoints_3;
    private static Method $method_getSuspendedEnvironments_4;
    private static Method $method_removeBreakpoint_5;
    private static Method $method_removeBreakpoints_6;
    private static Method $method_removeBreakpoints_7;
    private static Method $method_removeDebuggerListener_8;
    static /* synthetic */ Class class$freemarker$debug$Debugger;
    static /* synthetic */ Class class$freemarker$debug$Breakpoint;
    static /* synthetic */ Class class$freemarker$debug$DebuggerListener;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$lang$Object;

    static {
        try {
            $method_addBreakpoint_0 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = RmiDebuggerImpl_Stub.class$("freemarker.debug.Debugger"))).getMethod("addBreakpoint", class$freemarker$debug$Breakpoint != null ? class$freemarker$debug$Breakpoint : (class$freemarker$debug$Breakpoint = RmiDebuggerImpl_Stub.class$("freemarker.debug.Breakpoint")));
            $method_addDebuggerListener_1 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = RmiDebuggerImpl_Stub.class$("freemarker.debug.Debugger"))).getMethod("addDebuggerListener", class$freemarker$debug$DebuggerListener != null ? class$freemarker$debug$DebuggerListener : (class$freemarker$debug$DebuggerListener = RmiDebuggerImpl_Stub.class$("freemarker.debug.DebuggerListener")));
            $method_getBreakpoints_2 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = RmiDebuggerImpl_Stub.class$("freemarker.debug.Debugger"))).getMethod("getBreakpoints", new Class[0]);
            $method_getBreakpoints_3 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = RmiDebuggerImpl_Stub.class$("freemarker.debug.Debugger"))).getMethod("getBreakpoints", class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = RmiDebuggerImpl_Stub.class$("java.lang.String")));
            $method_getSuspendedEnvironments_4 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = RmiDebuggerImpl_Stub.class$("freemarker.debug.Debugger"))).getMethod("getSuspendedEnvironments", new Class[0]);
            $method_removeBreakpoint_5 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = RmiDebuggerImpl_Stub.class$("freemarker.debug.Debugger"))).getMethod("removeBreakpoint", class$freemarker$debug$Breakpoint != null ? class$freemarker$debug$Breakpoint : (class$freemarker$debug$Breakpoint = RmiDebuggerImpl_Stub.class$("freemarker.debug.Breakpoint")));
            $method_removeBreakpoints_6 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = RmiDebuggerImpl_Stub.class$("freemarker.debug.Debugger"))).getMethod("removeBreakpoints", new Class[0]);
            $method_removeBreakpoints_7 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = RmiDebuggerImpl_Stub.class$("freemarker.debug.Debugger"))).getMethod("removeBreakpoints", class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = RmiDebuggerImpl_Stub.class$("java.lang.String")));
            $method_removeDebuggerListener_8 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = RmiDebuggerImpl_Stub.class$("freemarker.debug.Debugger"))).getMethod("removeDebuggerListener", class$java$lang$Object != null ? class$java$lang$Object : (class$java$lang$Object = RmiDebuggerImpl_Stub.class$("java.lang.Object")));
        }
        catch (NoSuchMethodException noSuchMethodException) {
            throw new NoSuchMethodError("stub class initialization failed");
        }
    }

    public RmiDebuggerImpl_Stub(RemoteRef remoteRef) {
        super(remoteRef);
    }

    public void addBreakpoint(Breakpoint breakpoint) throws RemoteException {
        try {
            this.ref.invoke(this, $method_addBreakpoint_0, new Object[]{breakpoint}, -7089035859976030762L);
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

    public Object addDebuggerListener(DebuggerListener debuggerListener) throws RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_addDebuggerListener_1, new Object[]{debuggerListener}, 3973888913376431645L);
            return object;
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

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    public List getBreakpoints() throws RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_getBreakpoints_2, null, 2717170791450965365L);
            return (List)object;
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

    public List getBreakpoints(String string) throws RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_getBreakpoints_3, new Object[]{string}, 2245868106496574916L);
            return (List)object;
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

    public Collection getSuspendedEnvironments() throws RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_getSuspendedEnvironments_4, null, 6416507983008154583L);
            return (Collection)object;
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

    public void removeBreakpoint(Breakpoint breakpoint) throws RemoteException {
        try {
            this.ref.invoke(this, $method_removeBreakpoint_5, new Object[]{breakpoint}, -6894101526753771883L);
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

    public void removeBreakpoints() throws RemoteException {
        try {
            this.ref.invoke(this, $method_removeBreakpoints_6, null, -431815962995809519L);
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

    public void removeBreakpoints(String string) throws RemoteException {
        try {
            this.ref.invoke(this, $method_removeBreakpoints_7, new Object[]{string}, -4131389507095882284L);
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

    public void removeDebuggerListener(Object object) throws RemoteException {
        try {
            this.ref.invoke(this, $method_removeDebuggerListener_8, new Object[]{object}, 8368105080961049709L);
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

