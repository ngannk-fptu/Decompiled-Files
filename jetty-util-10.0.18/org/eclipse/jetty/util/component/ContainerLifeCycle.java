/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jetty.util.MultiException;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.Container;
import org.eclipse.jetty.util.component.Destroyable;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject(value="Implementation of Container and LifeCycle")
public class ContainerLifeCycle
extends AbstractLifeCycle
implements Container,
Destroyable,
Dumpable.DumpableContainer {
    private static final Logger LOG = LoggerFactory.getLogger(ContainerLifeCycle.class);
    private final List<Bean> _beans = new CopyOnWriteArrayList<Bean>();
    private final List<Container.Listener> _listeners = new CopyOnWriteArrayList<Container.Listener>();
    private boolean _doStarted;
    private boolean _destroyed;

    @Override
    protected void doStart() throws Exception {
        if (this._destroyed) {
            throw new IllegalStateException("Destroyed container cannot be restarted");
        }
        this._doStarted = true;
        try {
            for (Bean b : this._beans) {
                if (this.isStarting()) {
                    if (!(b._bean instanceof LifeCycle)) continue;
                    LifeCycle l = (LifeCycle)b._bean;
                    switch (b._managed) {
                        case MANAGED: {
                            if (!l.isStopped() && !l.isFailed()) break;
                            this.start(l);
                            break;
                        }
                        case AUTO: {
                            if (l.isStopped()) {
                                this.manage(b);
                                this.start(l);
                                break;
                            }
                            this.unmanage(b);
                            break;
                        }
                    }
                    continue;
                }
                break;
            }
        }
        catch (Throwable th) {
            ArrayList<Bean> reverse = new ArrayList<Bean>(this._beans);
            Collections.reverse(reverse);
            for (Bean b : reverse) {
                LifeCycle l;
                if (!(b._bean instanceof LifeCycle) || b._managed != Managed.MANAGED || !(l = (LifeCycle)b._bean).isRunning()) continue;
                try {
                    this.stop(l);
                }
                catch (Throwable th2) {
                    if (th2 == th) continue;
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    protected void start(LifeCycle l) throws Exception {
        l.start();
    }

    protected void stop(LifeCycle l) throws Exception {
        l.stop();
    }

    @Override
    protected void doStop() throws Exception {
        this._doStarted = false;
        super.doStop();
        ArrayList<Bean> reverse = new ArrayList<Bean>(this._beans);
        Collections.reverse(reverse);
        MultiException mex = new MultiException();
        for (Bean b : reverse) {
            if (!this.isStopping()) break;
            if (b._managed != Managed.MANAGED || !(b._bean instanceof LifeCycle)) continue;
            LifeCycle l = (LifeCycle)b._bean;
            try {
                this.stop(l);
            }
            catch (Throwable th) {
                mex.add(th);
            }
        }
        mex.ifExceptionThrow();
    }

    @Override
    public void destroy() {
        this._destroyed = true;
        ArrayList<Bean> reverse = new ArrayList<Bean>(this._beans);
        Collections.reverse(reverse);
        for (Bean b : reverse) {
            if (!(b._bean instanceof Destroyable) || b._managed != Managed.MANAGED && b._managed != Managed.POJO) continue;
            Destroyable d = (Destroyable)b._bean;
            try {
                d.destroy();
            }
            catch (Throwable th) {
                LOG.warn("Unable to destroy", th);
            }
        }
        this._beans.clear();
    }

    public boolean contains(Object bean) {
        for (Bean b : this._beans) {
            if (b._bean != bean) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isManaged(Object bean) {
        for (Bean b : this._beans) {
            if (b._bean != bean) continue;
            return b.isManaged();
        }
        return false;
    }

    public boolean isAuto(Object bean) {
        for (Bean b : this._beans) {
            if (b._bean != bean) continue;
            return b._managed == Managed.AUTO;
        }
        return false;
    }

    public boolean isUnmanaged(Object bean) {
        for (Bean b : this._beans) {
            if (b._bean != bean) continue;
            return b._managed == Managed.UNMANAGED;
        }
        return false;
    }

    @Override
    public boolean addBean(Object o) {
        if (o instanceof LifeCycle) {
            LifeCycle l = (LifeCycle)o;
            return this.addBean(o, l.isRunning() ? Managed.UNMANAGED : Managed.AUTO);
        }
        return this.addBean(o, Managed.POJO);
    }

    @Override
    public boolean addBean(Object o, boolean managed) {
        if (o instanceof LifeCycle) {
            return this.addBean(o, managed ? Managed.MANAGED : Managed.UNMANAGED);
        }
        return this.addBean(o, managed ? Managed.POJO : Managed.UNMANAGED);
    }

    private boolean addBean(Object o, Managed managed) {
        if (o == null || this.contains(o)) {
            return false;
        }
        Bean newBean = new Bean(o);
        this._beans.add(newBean);
        for (Container.Listener l : this._listeners) {
            l.beanAdded(this, o);
        }
        if (o instanceof EventListener) {
            this.addEventListener((EventListener)o);
        }
        try {
            switch (managed) {
                case UNMANAGED: {
                    this.unmanage(newBean);
                    break;
                }
                case MANAGED: {
                    LifeCycle l;
                    this.manage(newBean);
                    if (this.isStarting() && this._doStarted && !(l = (LifeCycle)o).isRunning()) {
                        this.start(l);
                    }
                    break;
                }
                case AUTO: {
                    LifeCycle l;
                    if (o instanceof LifeCycle) {
                        l = (LifeCycle)o;
                        if (this.isStarting()) {
                            if (l.isRunning()) {
                                this.unmanage(newBean);
                                break;
                            }
                            if (this._doStarted) {
                                this.manage(newBean);
                                this.start(l);
                                break;
                            }
                            newBean._managed = Managed.AUTO;
                            break;
                        }
                        if (this.isStarted()) {
                            this.unmanage(newBean);
                            break;
                        }
                        newBean._managed = Managed.AUTO;
                        break;
                    }
                    newBean._managed = Managed.POJO;
                    break;
                }
                case POJO: {
                    newBean._managed = Managed.POJO;
                    break;
                }
                default: {
                    throw new IllegalStateException(managed.toString());
                }
            }
        }
        catch (Error | RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} added {}", (Object)this, (Object)newBean);
        }
        return true;
    }

    public void addManaged(LifeCycle lifecycle) {
        this.addBean((Object)lifecycle, true);
        try {
            if (this.isRunning() && !lifecycle.isRunning()) {
                this.start(lifecycle);
            }
        }
        catch (Error | RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean addEventListener(EventListener listener) {
        if (super.addEventListener(listener)) {
            if (!this.contains(listener)) {
                this.addBean(listener);
            }
            if (listener instanceof Container.Listener) {
                Container.Listener cl = (Container.Listener)listener;
                this._listeners.add(cl);
                for (Bean b : this._beans) {
                    cl.beanAdded(this, b._bean);
                    if (!(listener instanceof Container.InheritedListener) || !b.isManaged() || !(b._bean instanceof Container)) continue;
                    if (b._bean instanceof ContainerLifeCycle) {
                        Container.addBean(b._bean, listener, false);
                        continue;
                    }
                    Container.addBean(b._bean, listener);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEventListener(EventListener listener) {
        if (super.removeEventListener(listener)) {
            this.removeBean(listener);
            if (listener instanceof Container.Listener && this._listeners.remove(listener)) {
                Container.Listener cl = (Container.Listener)listener;
                for (Bean b : this._beans) {
                    cl.beanRemoved(this, b._bean);
                    if (!(listener instanceof Container.InheritedListener) || !b.isManaged()) continue;
                    Container.removeBean(b._bean, listener);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void manage(Object bean) {
        for (Bean b : this._beans) {
            if (b._bean != bean) continue;
            this.manage(b);
            return;
        }
        throw new IllegalArgumentException("Unknown bean " + bean);
    }

    private void manage(Bean bean) {
        if (bean._managed != Managed.MANAGED) {
            bean._managed = Managed.MANAGED;
            if (bean._bean instanceof Container) {
                for (Container.Listener l : this._listeners) {
                    if (!(l instanceof Container.InheritedListener)) continue;
                    if (bean._bean instanceof ContainerLifeCycle) {
                        Container.addBean(bean._bean, l, false);
                        continue;
                    }
                    Container.addBean(bean._bean, l);
                }
            }
        }
    }

    @Override
    public void unmanage(Object bean) {
        for (Bean b : this._beans) {
            if (b._bean != bean) continue;
            this.unmanage(b);
            return;
        }
        throw new IllegalArgumentException("Unknown bean " + bean);
    }

    private void unmanage(Bean bean) {
        if (bean._managed != Managed.UNMANAGED) {
            if (bean._managed == Managed.MANAGED && bean._bean instanceof Container) {
                for (Container.Listener l : this._listeners) {
                    if (!(l instanceof Container.InheritedListener)) continue;
                    Container.removeBean(bean._bean, l);
                }
            }
            bean._managed = Managed.UNMANAGED;
        }
    }

    public void setBeans(Collection<Object> beans) {
        for (Object bean : beans) {
            this.addBean(bean);
        }
    }

    @Override
    public Collection<Object> getBeans() {
        return this.getBeans(Object.class);
    }

    @Override
    public <T> Collection<T> getBeans(Class<T> clazz) {
        ArrayList<T> beans = null;
        for (Bean b : this._beans) {
            if (!clazz.isInstance(b._bean)) continue;
            if (beans == null) {
                beans = new ArrayList<T>();
            }
            beans.add(clazz.cast(b._bean));
        }
        return beans == null ? Collections.emptyList() : beans;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        for (Bean b : this._beans) {
            if (!clazz.isInstance(b._bean)) continue;
            return clazz.cast(b._bean);
        }
        return null;
    }

    private Bean getBean(Object o) {
        for (Bean b : this._beans) {
            if (b._bean != o) continue;
            return b;
        }
        return null;
    }

    public void removeBeans() {
        ArrayList<Bean> beans = new ArrayList<Bean>(this._beans);
        for (Bean b : beans) {
            this.remove(b);
        }
    }

    @Override
    public boolean removeBean(Object o) {
        Bean b = this.getBean(o);
        return b != null && this.remove(b);
    }

    private boolean remove(Bean bean) {
        if (this._beans.remove(bean)) {
            boolean wasManaged = bean.isManaged();
            this.unmanage(bean);
            for (Container.Listener l : this._listeners) {
                l.beanRemoved(this, bean._bean);
            }
            if (bean._bean instanceof EventListener && this.getEventListeners().contains(bean._bean)) {
                this.removeEventListener((EventListener)bean._bean);
            }
            if (wasManaged && bean._bean instanceof LifeCycle) {
                try {
                    this.stop((LifeCycle)bean._bean);
                }
                catch (Error | RuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }
        return false;
    }

    @ManagedOperation(value="Dump the object to stderr")
    public void dumpStdErr() {
        try {
            this.dump(System.err, "");
            System.err.println("key: +- bean, += managed, +~ unmanaged, +? auto, +: iterable, +] array, +@ map, +> undefined");
        }
        catch (IOException e) {
            LOG.warn("Unable to dump", (Throwable)e);
        }
    }

    @Override
    @ManagedOperation(value="Dump the object to a string")
    public String dump() {
        return Dumpable.dump(this);
    }

    @Override
    public void dump(Appendable out, String indent) throws IOException {
        this.dumpObjects(out, indent, new Object[0]);
    }

    public void dump(Appendable out) throws IOException {
        this.dump(out, "");
    }

    protected void dumpObjects(Appendable out, String indent, Object ... items) throws IOException {
        Dumpable.dumpObjects(out, indent, this, items);
    }

    public void updateBean(Object oldBean, Object newBean) {
        if (newBean != oldBean) {
            if (oldBean != null) {
                this.removeBean(oldBean);
            }
            if (newBean != null) {
                this.addBean(newBean);
            }
        }
    }

    public void updateBean(Object oldBean, Object newBean, boolean managed) {
        if (newBean != oldBean) {
            if (oldBean != null) {
                this.removeBean(oldBean);
            }
            if (newBean != null) {
                this.addBean(newBean, managed);
            }
        }
    }

    public void updateBeans(Object[] oldBeans, Object[] newBeans) {
        this.updateBeans(oldBeans == null ? Collections.emptyList() : Arrays.asList(oldBeans), newBeans == null ? Collections.emptyList() : Arrays.asList(newBeans));
    }

    public void updateBeans(Collection<?> oldBeans, Collection<?> newBeans) {
        Objects.requireNonNull(oldBeans);
        Objects.requireNonNull(newBeans);
        for (Object o : oldBeans) {
            if (newBeans.contains(o)) continue;
            this.removeBean(o);
        }
        for (Object n : newBeans) {
            if (oldBeans.contains(n)) continue;
            this.addBean(n);
        }
    }

    @Override
    public <T> Collection<T> getContainedBeans(Class<T> clazz) {
        HashSet beans = new HashSet();
        this.getContainedBeans(clazz, beans);
        return beans;
    }

    protected <T> void getContainedBeans(Class<T> clazz, Collection<T> beans) {
        beans.addAll(this.getBeans(clazz));
        for (Container c : this.getBeans(Container.class)) {
            Bean bean = this.getBean(c);
            if (bean == null || !bean.isManageable()) continue;
            if (c instanceof ContainerLifeCycle) {
                ((ContainerLifeCycle)c).getContainedBeans(clazz, beans);
                continue;
            }
            beans.addAll(c.getContainedBeans(clazz));
        }
    }

    private static class Bean {
        private final Object _bean;
        private volatile Managed _managed = Managed.POJO;

        private Bean(Object b) {
            if (b == null) {
                throw new NullPointerException();
            }
            this._bean = b;
        }

        public boolean isManaged() {
            return this._managed == Managed.MANAGED;
        }

        public boolean isManageable() {
            switch (this._managed) {
                case MANAGED: {
                    return true;
                }
                case AUTO: {
                    return this._bean instanceof LifeCycle && ((LifeCycle)this._bean).isStopped();
                }
            }
            return false;
        }

        public String toString() {
            return String.format("{%s,%s}", new Object[]{this._bean, this._managed});
        }
    }

    static enum Managed {
        POJO,
        MANAGED,
        UNMANAGED,
        AUTO;

    }
}

