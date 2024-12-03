/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.app.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.velocity.app.event.EventHandler;
import org.apache.velocity.app.event.IncludeEventHandler;
import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.app.event.NullSetEventHandler;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalEventContext;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.util.RuntimeServicesAware;

public class EventCartridge {
    private List referenceHandlers = new ArrayList();
    private List nullSetHandlers = new ArrayList();
    private List methodExceptionHandlers = new ArrayList();
    private List includeHandlers = new ArrayList();
    private List invalidReferenceHandlers = new ArrayList();
    Set initializedHandlers = new HashSet();

    public boolean addEventHandler(EventHandler ev) {
        if (ev == null) {
            return false;
        }
        boolean found = false;
        if (ev instanceof ReferenceInsertionEventHandler) {
            this.addReferenceInsertionEventHandler((ReferenceInsertionEventHandler)ev);
            found = true;
        }
        if (ev instanceof NullSetEventHandler) {
            this.addNullSetEventHandler((NullSetEventHandler)ev);
            found = true;
        }
        if (ev instanceof MethodExceptionEventHandler) {
            this.addMethodExceptionHandler((MethodExceptionEventHandler)ev);
            found = true;
        }
        if (ev instanceof IncludeEventHandler) {
            this.addIncludeEventHandler((IncludeEventHandler)ev);
            found = true;
        }
        if (ev instanceof InvalidReferenceEventHandler) {
            this.addInvalidReferenceEventHandler((InvalidReferenceEventHandler)ev);
            found = true;
        }
        return found;
    }

    public void addReferenceInsertionEventHandler(ReferenceInsertionEventHandler ev) {
        this.referenceHandlers.add(ev);
    }

    public void addNullSetEventHandler(NullSetEventHandler ev) {
        this.nullSetHandlers.add(ev);
    }

    public void addMethodExceptionHandler(MethodExceptionEventHandler ev) {
        this.methodExceptionHandlers.add(ev);
    }

    public void addIncludeEventHandler(IncludeEventHandler ev) {
        this.includeHandlers.add(ev);
    }

    public void addInvalidReferenceEventHandler(InvalidReferenceEventHandler ev) {
        this.invalidReferenceHandlers.add(ev);
    }

    public boolean removeEventHandler(EventHandler ev) {
        if (ev == null) {
            return false;
        }
        boolean found = false;
        if (ev instanceof ReferenceInsertionEventHandler) {
            return this.referenceHandlers.remove(ev);
        }
        if (ev instanceof NullSetEventHandler) {
            return this.nullSetHandlers.remove(ev);
        }
        if (ev instanceof MethodExceptionEventHandler) {
            return this.methodExceptionHandlers.remove(ev);
        }
        if (ev instanceof IncludeEventHandler) {
            return this.includeHandlers.remove(ev);
        }
        if (ev instanceof InvalidReferenceEventHandler) {
            return this.invalidReferenceHandlers.remove(ev);
        }
        return found;
    }

    public Iterator getReferenceInsertionEventHandlers() {
        return this.referenceHandlers.size() == 0 ? null : this.referenceHandlers.iterator();
    }

    public Iterator getNullSetEventHandlers() {
        return this.nullSetHandlers.iterator();
    }

    public Iterator getMethodExceptionEventHandlers() {
        return this.methodExceptionHandlers.iterator();
    }

    public Iterator getIncludeEventHandlers() {
        return this.includeHandlers.iterator();
    }

    public Iterator getInvalidReferenceEventHandlers() {
        return this.invalidReferenceHandlers.iterator();
    }

    public final boolean attachToContext(Context context) {
        if (context instanceof InternalEventContext) {
            InternalEventContext iec = (InternalEventContext)((Object)context);
            iec.attachEventCartridge(this);
            return true;
        }
        return false;
    }

    public void initialize(RuntimeServices rs) throws Exception {
        for (EventHandler eh : this.referenceHandlers) {
            if (!(eh instanceof RuntimeServicesAware) || this.initializedHandlers.contains(eh)) continue;
            ((RuntimeServicesAware)((Object)eh)).setRuntimeServices(rs);
            this.initializedHandlers.add(eh);
        }
        for (EventHandler eh : this.nullSetHandlers) {
            if (!(eh instanceof RuntimeServicesAware) || this.initializedHandlers.contains(eh)) continue;
            ((RuntimeServicesAware)((Object)eh)).setRuntimeServices(rs);
            this.initializedHandlers.add(eh);
        }
        for (EventHandler eh : this.methodExceptionHandlers) {
            if (!(eh instanceof RuntimeServicesAware) || this.initializedHandlers.contains(eh)) continue;
            ((RuntimeServicesAware)((Object)eh)).setRuntimeServices(rs);
            this.initializedHandlers.add(eh);
        }
        for (EventHandler eh : this.includeHandlers) {
            if (!(eh instanceof RuntimeServicesAware) || this.initializedHandlers.contains(eh)) continue;
            ((RuntimeServicesAware)((Object)eh)).setRuntimeServices(rs);
            this.initializedHandlers.add(eh);
        }
        for (EventHandler eh : this.invalidReferenceHandlers) {
            if (!(eh instanceof RuntimeServicesAware) || this.initializedHandlers.contains(eh)) continue;
            ((RuntimeServicesAware)((Object)eh)).setRuntimeServices(rs);
            this.initializedHandlers.add(eh);
        }
    }
}

