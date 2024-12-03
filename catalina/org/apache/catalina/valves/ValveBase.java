/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.valves;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.ToStringUtil;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;

public abstract class ValveBase
extends LifecycleMBeanBase
implements Contained,
Valve {
    protected static final StringManager sm = StringManager.getManager(ValveBase.class);
    protected boolean asyncSupported;
    protected Container container = null;
    protected Log containerLog = null;
    protected Valve next = null;

    public ValveBase() {
        this(false);
    }

    public ValveBase(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
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
    public boolean isAsyncSupported() {
        return this.asyncSupported;
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    @Override
    public Valve getNext() {
        return this.next;
    }

    @Override
    public void setNext(Valve valve) {
        this.next = valve;
    }

    @Override
    public void backgroundProcess() {
    }

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        this.containerLog = this.getContainer().getLogger();
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        this.setState(LifecycleState.STARTING);
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        this.setState(LifecycleState.STOPPING);
    }

    public String toString() {
        return ToStringUtil.toString(this);
    }

    @Override
    public String getObjectNameKeyProperties() {
        String className;
        int period;
        StringBuilder name = new StringBuilder("type=Valve");
        Container container = this.getContainer();
        name.append(container.getMBeanKeyProperties());
        int seq = 0;
        Pipeline p = container.getPipeline();
        if (p != null) {
            for (Valve valve : p.getValves()) {
                if (valve == null) continue;
                if (valve == this) break;
                if (valve.getClass() != this.getClass()) continue;
                ++seq;
            }
        }
        if (seq > 0) {
            name.append(",seq=");
            name.append(seq);
        }
        if ((period = (className = this.getClass().getName()).lastIndexOf(46)) >= 0) {
            className = className.substring(period + 1);
        }
        name.append(",name=");
        name.append(className);
        return name.toString();
    }

    @Override
    public String getDomainInternal() {
        Container c = this.getContainer();
        if (c == null) {
            return null;
        }
        return c.getDomain();
    }
}

