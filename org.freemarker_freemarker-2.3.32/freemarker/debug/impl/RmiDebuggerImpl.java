/*
 * Decompiled with CFR 0.152.
 */
package freemarker.debug.impl;

import freemarker.debug.Breakpoint;
import freemarker.debug.Debugger;
import freemarker.debug.DebuggerListener;
import freemarker.debug.impl.RmiDebuggerService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.List;

class RmiDebuggerImpl
extends UnicastRemoteObject
implements Debugger {
    private static final long serialVersionUID = 1L;
    private final RmiDebuggerService service;

    protected RmiDebuggerImpl(RmiDebuggerService service) throws RemoteException {
        this.service = service;
    }

    @Override
    public void addBreakpoint(Breakpoint breakpoint) {
        this.service.addBreakpoint(breakpoint);
    }

    @Override
    public Object addDebuggerListener(DebuggerListener listener) {
        return this.service.addDebuggerListener(listener);
    }

    @Override
    public List getBreakpoints() {
        return this.service.getBreakpointsSpi();
    }

    @Override
    public List getBreakpoints(String templateName) {
        return this.service.getBreakpointsSpi(templateName);
    }

    @Override
    public Collection getSuspendedEnvironments() {
        return this.service.getSuspendedEnvironments();
    }

    @Override
    public void removeBreakpoint(Breakpoint breakpoint) {
        this.service.removeBreakpoint(breakpoint);
    }

    @Override
    public void removeDebuggerListener(Object id) {
        this.service.removeDebuggerListener(id);
    }

    @Override
    public void removeBreakpoints() {
        this.service.removeBreakpoints();
    }

    @Override
    public void removeBreakpoints(String templateName) {
        this.service.removeBreakpoints(templateName);
    }
}

