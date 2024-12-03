/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.util.ArrayList;
import java.util.Set;
import javax.management.ObjectName;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.util.LifecycleBase;
import org.apache.catalina.util.ToStringUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

public class StandardPipeline
extends LifecycleBase
implements Pipeline {
    private static final Log log = LogFactory.getLog(StandardPipeline.class);
    private static final StringManager sm = StringManager.getManager(StandardPipeline.class);
    protected Valve basic = null;
    protected Container container = null;
    protected Valve first = null;

    public StandardPipeline() {
        this(null);
    }

    public StandardPipeline(Container container) {
        this.setContainer(container);
    }

    @Override
    public boolean isAsyncSupported() {
        boolean supported;
        Valve valve = this.first != null ? this.first : this.basic;
        for (supported = true; supported && valve != null; supported &= valve.isAsyncSupported(), valve = valve.getNext()) {
        }
        return supported;
    }

    @Override
    public void findNonAsyncValves(Set<String> result) {
        Valve valve;
        Valve valve2 = valve = this.first != null ? this.first : this.basic;
        while (valve != null) {
            if (!valve.isAsyncSupported()) {
                result.add(valve.getClass().getName());
            }
            valve = valve.getNext();
        }
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    protected void initInternal() {
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        Valve current = this.first;
        if (current == null) {
            current = this.basic;
        }
        while (current != null) {
            if (current instanceof Lifecycle) {
                ((Lifecycle)((Object)current)).start();
            }
            current = current.getNext();
        }
        this.setState(LifecycleState.STARTING);
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        this.setState(LifecycleState.STOPPING);
        Valve current = this.first;
        if (current == null) {
            current = this.basic;
        }
        while (current != null) {
            if (current instanceof Lifecycle) {
                ((Lifecycle)((Object)current)).stop();
            }
            current = current.getNext();
        }
    }

    @Override
    protected void destroyInternal() {
        Valve[] valves;
        for (Valve valve : valves = this.getValves()) {
            this.removeValve(valve);
        }
    }

    public String toString() {
        return ToStringUtil.toString(this);
    }

    @Override
    public Valve getBasic() {
        return this.basic;
    }

    @Override
    public void setBasic(Valve valve) {
        Valve oldBasic = this.basic;
        if (oldBasic == valve) {
            return;
        }
        if (oldBasic != null) {
            if (this.getState().isAvailable() && oldBasic instanceof Lifecycle) {
                try {
                    ((Lifecycle)((Object)oldBasic)).stop();
                }
                catch (LifecycleException e) {
                    log.error((Object)sm.getString("standardPipeline.basic.stop"), (Throwable)e);
                }
            }
            if (oldBasic instanceof Contained) {
                try {
                    ((Contained)((Object)oldBasic)).setContainer(null);
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                }
            }
        }
        if (valve == null) {
            return;
        }
        if (valve instanceof Contained) {
            ((Contained)((Object)valve)).setContainer(this.container);
        }
        if (this.getState().isAvailable() && valve instanceof Lifecycle) {
            try {
                ((Lifecycle)((Object)valve)).start();
            }
            catch (LifecycleException e) {
                log.error((Object)sm.getString("standardPipeline.basic.start"), (Throwable)e);
                return;
            }
        }
        for (Valve current = this.first; current != null; current = current.getNext()) {
            if (current.getNext() != oldBasic) continue;
            current.setNext(valve);
            break;
        }
        this.basic = valve;
    }

    @Override
    public void addValve(Valve valve) {
        if (valve instanceof Contained) {
            ((Contained)((Object)valve)).setContainer(this.container);
        }
        if (this.getState().isAvailable() && valve instanceof Lifecycle) {
            try {
                ((Lifecycle)((Object)valve)).start();
            }
            catch (LifecycleException e) {
                log.error((Object)sm.getString("standardPipeline.valve.start"), (Throwable)e);
            }
        }
        if (this.first == null) {
            this.first = valve;
            valve.setNext(this.basic);
        } else {
            for (Valve current = this.first; current != null; current = current.getNext()) {
                if (current.getNext() != this.basic) continue;
                current.setNext(valve);
                valve.setNext(this.basic);
                break;
            }
        }
        this.container.fireContainerEvent("addValve", valve);
    }

    @Override
    public Valve[] getValves() {
        ArrayList<Valve> valveList = new ArrayList<Valve>();
        Valve current = this.first;
        if (current == null) {
            current = this.basic;
        }
        while (current != null) {
            valveList.add(current);
            current = current.getNext();
        }
        return valveList.toArray(new Valve[0]);
    }

    public ObjectName[] getValveObjectNames() {
        ArrayList<ObjectName> valveList = new ArrayList<ObjectName>();
        Valve current = this.first;
        if (current == null) {
            current = this.basic;
        }
        while (current != null) {
            if (current instanceof JmxEnabled) {
                valveList.add(((JmxEnabled)((Object)current)).getObjectName());
            }
            current = current.getNext();
        }
        return valveList.toArray(new ObjectName[0]);
    }

    @Override
    public void removeValve(Valve valve) {
        Valve current;
        if (this.first == valve) {
            this.first = this.first.getNext();
            current = null;
        } else {
            current = this.first;
        }
        while (current != null) {
            if (current.getNext() == valve) {
                current.setNext(valve.getNext());
                break;
            }
            current = current.getNext();
        }
        if (this.first == this.basic) {
            this.first = null;
        }
        if (valve instanceof Contained) {
            ((Contained)((Object)valve)).setContainer(null);
        }
        if (valve instanceof Lifecycle) {
            if (this.getState().isAvailable()) {
                try {
                    ((Lifecycle)((Object)valve)).stop();
                }
                catch (LifecycleException e) {
                    log.error((Object)sm.getString("standardPipeline.valve.stop"), (Throwable)e);
                }
            }
            try {
                ((Lifecycle)((Object)valve)).destroy();
            }
            catch (LifecycleException e) {
                log.error((Object)sm.getString("standardPipeline.valve.destroy"), (Throwable)e);
            }
        }
        this.container.fireContainerEvent("removeValve", valve);
    }

    @Override
    public Valve getFirst() {
        if (this.first != null) {
            return this.first;
        }
        return this.basic;
    }
}

