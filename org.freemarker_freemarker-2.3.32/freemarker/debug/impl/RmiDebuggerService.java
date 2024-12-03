/*
 * Decompiled with CFR 0.152.
 */
package freemarker.debug.impl;

import freemarker.core.DebugBreak;
import freemarker.core.Environment;
import freemarker.core.TemplateElement;
import freemarker.core._CoreAPI;
import freemarker.debug.Breakpoint;
import freemarker.debug.DebuggerListener;
import freemarker.debug.EnvironmentSuspendedEvent;
import freemarker.debug.impl.DebuggerServer;
import freemarker.debug.impl.DebuggerService;
import freemarker.debug.impl.RmiDebuggedEnvironmentImpl;
import freemarker.debug.impl.RmiDebuggerImpl;
import freemarker.template.Template;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class RmiDebuggerService
extends DebuggerService {
    private final Map templateDebugInfos = new HashMap();
    private final HashSet suspendedEnvironments = new HashSet();
    private final Map listeners = new HashMap();
    private final ReferenceQueue refQueue = new ReferenceQueue();
    private final RmiDebuggerImpl debugger;
    private DebuggerServer server;

    RmiDebuggerService() {
        try {
            this.debugger = new RmiDebuggerImpl(this);
            this.server = new DebuggerServer((Serializable)((Object)RemoteObject.toStub(this.debugger)));
            this.server.start();
        }
        catch (RemoteException e) {
            e.printStackTrace();
            throw new UndeclaredThrowableException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    List getBreakpointsSpi(String templateName) {
        Map map = this.templateDebugInfos;
        synchronized (map) {
            TemplateDebugInfo tdi = this.findTemplateDebugInfo(templateName);
            return tdi == null ? Collections.EMPTY_LIST : tdi.breakpoints;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    List getBreakpointsSpi() {
        ArrayList sumlist = new ArrayList();
        Map map = this.templateDebugInfos;
        synchronized (map) {
            Iterator iter = this.templateDebugInfos.values().iterator();
            while (iter.hasNext()) {
                sumlist.addAll(((TemplateDebugInfo)iter.next()).breakpoints);
            }
        }
        Collections.sort(sumlist);
        return sumlist;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    boolean suspendEnvironmentSpi(Environment env, String templateName, int line) throws RemoteException {
        RmiDebuggedEnvironmentImpl denv = (RmiDebuggedEnvironmentImpl)RmiDebuggedEnvironmentImpl.getCachedWrapperFor(env);
        HashSet hashSet = this.suspendedEnvironments;
        synchronized (hashSet) {
            this.suspendedEnvironments.add(denv);
        }
        try {
            EnvironmentSuspendedEvent breakpointEvent = new EnvironmentSuspendedEvent(this, templateName, line, denv);
            Object object = this.listeners;
            synchronized (object) {
                for (DebuggerListener listener : this.listeners.values()) {
                    listener.environmentSuspended(breakpointEvent);
                }
            }
            object = denv;
            synchronized (object) {
                try {
                    denv.wait();
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
            boolean bl = denv.isStopped();
            return bl;
        }
        finally {
            HashSet hashSet2 = this.suspendedEnvironments;
            synchronized (hashSet2) {
                this.suspendedEnvironments.remove(denv);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void registerTemplateSpi(Template template) {
        String templateName = template.getName();
        Map map = this.templateDebugInfos;
        synchronized (map) {
            TemplateDebugInfo tdi = this.createTemplateDebugInfo(templateName);
            tdi.templates.add(new TemplateReference(templateName, template, this.refQueue));
            for (Breakpoint breakpoint : tdi.breakpoints) {
                RmiDebuggerService.insertDebugBreak(template, breakpoint);
            }
        }
    }

    Collection getSuspendedEnvironments() {
        return (Collection)this.suspendedEnvironments.clone();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Object addDebuggerListener(DebuggerListener listener) {
        Long id;
        Map map = this.listeners;
        synchronized (map) {
            id = System.currentTimeMillis();
            this.listeners.put(id, listener);
        }
        return id;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void removeDebuggerListener(Object id) {
        Map map = this.listeners;
        synchronized (map) {
            this.listeners.remove(id);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void addBreakpoint(Breakpoint breakpoint) {
        String templateName = breakpoint.getTemplateName();
        Map map = this.templateDebugInfos;
        synchronized (map) {
            TemplateDebugInfo tdi = this.createTemplateDebugInfo(templateName);
            List breakpoints = tdi.breakpoints;
            int pos = Collections.binarySearch(breakpoints, breakpoint);
            if (pos < 0) {
                breakpoints.add(-pos - 1, breakpoint);
                Iterator iter = tdi.templates.iterator();
                while (iter.hasNext()) {
                    TemplateReference ref = (TemplateReference)iter.next();
                    Template t = ref.getTemplate();
                    if (t == null) {
                        iter.remove();
                        continue;
                    }
                    RmiDebuggerService.insertDebugBreak(t, breakpoint);
                }
            }
        }
    }

    private static void insertDebugBreak(Template t, Breakpoint breakpoint) {
        TemplateElement te = RmiDebuggerService.findTemplateElement(t.getRootTreeNode(), breakpoint.getLine());
        if (te == null) {
            return;
        }
        TemplateElement parent = _CoreAPI.getParentElement(te);
        DebugBreak db = new DebugBreak(te);
        parent.setChildAt(parent.getIndex(te), db);
    }

    private static TemplateElement findTemplateElement(TemplateElement te, int line) {
        if (te.getBeginLine() > line || te.getEndLine() < line) {
            return null;
        }
        ArrayList<TemplateElement> childMatches = new ArrayList<TemplateElement>();
        Enumeration children = te.children();
        while (children.hasMoreElements()) {
            TemplateElement child = (TemplateElement)children.nextElement();
            TemplateElement childmatch = RmiDebuggerService.findTemplateElement(child, line);
            if (childmatch == null) continue;
            childMatches.add(childmatch);
        }
        TemplateElement bestMatch = null;
        for (int i = 0; i < childMatches.size(); ++i) {
            TemplateElement e = (TemplateElement)childMatches.get(i);
            if (bestMatch == null) {
                bestMatch = e;
            }
            if (e.getBeginLine() == line && e.getEndLine() > line) {
                bestMatch = e;
            }
            if (e.getBeginLine() != e.getEndLine() || e.getBeginLine() != line) continue;
            bestMatch = e;
            break;
        }
        if (bestMatch != null) {
            return bestMatch;
        }
        return te;
    }

    private TemplateDebugInfo findTemplateDebugInfo(String templateName) {
        this.processRefQueue();
        return (TemplateDebugInfo)this.templateDebugInfos.get(templateName);
    }

    private TemplateDebugInfo createTemplateDebugInfo(String templateName) {
        TemplateDebugInfo tdi = this.findTemplateDebugInfo(templateName);
        if (tdi == null) {
            tdi = new TemplateDebugInfo();
            this.templateDebugInfos.put(templateName, tdi);
        }
        return tdi;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void removeBreakpoint(Breakpoint breakpoint) {
        String templateName = breakpoint.getTemplateName();
        Map map = this.templateDebugInfos;
        synchronized (map) {
            TemplateDebugInfo tdi = this.findTemplateDebugInfo(templateName);
            if (tdi != null) {
                List breakpoints = tdi.breakpoints;
                int pos = Collections.binarySearch(breakpoints, breakpoint);
                if (pos >= 0) {
                    breakpoints.remove(pos);
                    Iterator iter = tdi.templates.iterator();
                    while (iter.hasNext()) {
                        TemplateReference ref = (TemplateReference)iter.next();
                        Template t = ref.getTemplate();
                        if (t == null) {
                            iter.remove();
                            continue;
                        }
                        this.removeDebugBreak(t, breakpoint);
                    }
                }
                if (tdi.isEmpty()) {
                    this.templateDebugInfos.remove(templateName);
                }
            }
        }
    }

    private void removeDebugBreak(Template t, Breakpoint breakpoint) {
        TemplateElement te = RmiDebuggerService.findTemplateElement(t.getRootTreeNode(), breakpoint.getLine());
        if (te == null) {
            return;
        }
        DebugBreak db = null;
        while (te != null) {
            if (te instanceof DebugBreak) {
                db = (DebugBreak)te;
                break;
            }
            te = _CoreAPI.getParentElement(te);
        }
        if (db == null) {
            return;
        }
        TemplateElement parent = _CoreAPI.getParentElement(db);
        parent.setChildAt(parent.getIndex(db), _CoreAPI.getChildElement(db, 0));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void removeBreakpoints(String templateName) {
        Map map = this.templateDebugInfos;
        synchronized (map) {
            TemplateDebugInfo tdi = this.findTemplateDebugInfo(templateName);
            if (tdi != null) {
                this.removeBreakpoints(tdi);
                if (tdi.isEmpty()) {
                    this.templateDebugInfos.remove(templateName);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void removeBreakpoints() {
        Map map = this.templateDebugInfos;
        synchronized (map) {
            Iterator iter = this.templateDebugInfos.values().iterator();
            while (iter.hasNext()) {
                TemplateDebugInfo tdi = (TemplateDebugInfo)iter.next();
                this.removeBreakpoints(tdi);
                if (!tdi.isEmpty()) continue;
                iter.remove();
            }
        }
    }

    private void removeBreakpoints(TemplateDebugInfo tdi) {
        tdi.breakpoints.clear();
        Iterator iter = tdi.templates.iterator();
        while (iter.hasNext()) {
            TemplateReference ref = (TemplateReference)iter.next();
            Template t = ref.getTemplate();
            if (t == null) {
                iter.remove();
                continue;
            }
            this.removeDebugBreaks(t.getRootTreeNode());
        }
    }

    private void removeDebugBreaks(TemplateElement te) {
        int count = te.getChildCount();
        for (int i = 0; i < count; ++i) {
            TemplateElement child = _CoreAPI.getChildElement(te, i);
            while (child instanceof DebugBreak) {
                TemplateElement dbchild = _CoreAPI.getChildElement(child, 0);
                te.setChildAt(i, dbchild);
                child = dbchild;
            }
            this.removeDebugBreaks(child);
        }
    }

    private void processRefQueue() {
        TemplateReference ref;
        while ((ref = (TemplateReference)this.refQueue.poll()) != null) {
            TemplateDebugInfo tdi = this.findTemplateDebugInfo(ref.templateName);
            if (tdi == null) continue;
            tdi.templates.remove(ref);
            if (!tdi.isEmpty()) continue;
            this.templateDebugInfos.remove(ref.templateName);
        }
    }

    @Override
    void shutdownSpi() {
        this.server.stop();
        try {
            UnicastRemoteObject.unexportObject(this.debugger, true);
        }
        catch (Exception exception) {
            // empty catch block
        }
        RmiDebuggedEnvironmentImpl.cleanup();
    }

    private static final class TemplateReference
    extends WeakReference {
        final String templateName;

        TemplateReference(String templateName, Template template, ReferenceQueue queue) {
            super(template, queue);
            this.templateName = templateName;
        }

        Template getTemplate() {
            return (Template)this.get();
        }
    }

    private static final class TemplateDebugInfo {
        final List templates = new ArrayList();
        final List breakpoints = new ArrayList();

        private TemplateDebugInfo() {
        }

        boolean isEmpty() {
            return this.templates.isEmpty() && this.breakpoints.isEmpty();
        }
    }
}

