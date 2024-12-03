/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.util;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.lock.LockManager;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

public abstract class Locked {
    private static final String MIX = "http://www.jcp.org/jcr/mix/1.0";
    public static final Object TIMED_OUT = new Object();

    public Object with(Node lockable, boolean isDeep) throws RepositoryException, InterruptedException {
        return this.with(lockable, isDeep, true);
    }

    public Object with(Node lockable, boolean isDeep, boolean isSessionScoped) throws RepositoryException, InterruptedException {
        return this.with(lockable, isDeep, Long.MAX_VALUE, isSessionScoped);
    }

    public Object with(Node lockable, boolean isDeep, long timeout) throws UnsupportedRepositoryOperationException, RepositoryException, InterruptedException {
        return this.with(lockable, isDeep, timeout, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public Object with(Node lockable, boolean isDeep, long timeout, boolean isSessionScoped) throws UnsupportedRepositoryOperationException, RepositoryException, InterruptedException {
        Object object;
        long timelimit;
        Lock lock;
        EventListener listener;
        Session session;
        block17: {
            block16: {
                if (timeout < 0L) {
                    throw new IllegalArgumentException("timeout must be >= 0");
                }
                session = lockable.getSession();
                listener = null;
                String mix = session.getNamespacePrefix(MIX);
                if (!lockable.isNodeType(mix + ":lockable")) {
                    throw new IllegalArgumentException("Node is not lockable");
                }
                lock = Locked.tryLock(lockable, isDeep, timeout, isSessionScoped);
                if (lock == null) break block16;
                Object object2 = this.runAndUnlock(lock);
                if (listener == null) return object2;
                session.getWorkspace().getObservationManager().removeEventListener(listener);
                return object2;
            }
            if (timeout != 0L) break block17;
            Object object3 = TIMED_OUT;
            if (listener == null) return object3;
            session.getWorkspace().getObservationManager().removeEventListener(listener);
            return object3;
        }
        try {
            timelimit = timeout == Long.MAX_VALUE ? Long.MAX_VALUE : System.currentTimeMillis() + timeout;
            if (Locked.isObservationSupported(session)) {
                ObservationManager om = session.getWorkspace().getObservationManager();
                listener = new EventListener(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    public void onEvent(EventIterator events) {
                        Locked locked = Locked.this;
                        synchronized (locked) {
                            Locked.this.notify();
                        }
                    }
                };
                om.addEventListener(listener, 8, lockable.getPath(), false, null, null, true);
            }
            while (true) {
                Locked locked = this;
                // MONITORENTER : locked
                lock = Locked.tryLock(lockable, isDeep, timeout, isSessionScoped);
                if (lock == null) break block18;
                object = this.runAndUnlock(lock);
                // MONITOREXIT : locked
                if (listener == null) return object;
                break;
            }
        }
        catch (Throwable throwable) {
            if (listener == null) throw throwable;
            session.getWorkspace().getObservationManager().removeEventListener(listener);
            throw throwable;
        }
        {
            block18: {
                session.getWorkspace().getObservationManager().removeEventListener(listener);
                return object;
            }
            if (System.currentTimeMillis() > timelimit) {
                Object object4 = TIMED_OUT;
                // MONITOREXIT : locked
                if (listener == null) return object4;
                session.getWorkspace().getObservationManager().removeEventListener(listener);
                return object4;
            }
            if (listener != null) {
                this.wait(Math.min(1000L, timeout));
            } else {
                this.wait(Math.min(50L, timeout));
            }
            // MONITOREXIT : locked
            continue;
        }
    }

    protected abstract Object run(Node var1) throws RepositoryException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object runAndUnlock(Lock lock) throws RepositoryException {
        Node node = lock.getNode();
        try {
            Object object = this.run(node);
            return object;
        }
        finally {
            node.getSession().getWorkspace().getLockManager().unlock(node.getPath());
        }
    }

    private static Lock tryLock(Node lockable, boolean isDeep, long timeout, boolean isSessionScoped) throws UnsupportedRepositoryOperationException, RepositoryException {
        try {
            LockManager lm = lockable.getSession().getWorkspace().getLockManager();
            return lm.lock(lockable.getPath(), isDeep, isSessionScoped, timeout, null);
        }
        catch (LockException lockException) {
            return null;
        }
    }

    private static boolean isObservationSupported(Session s) {
        return "true".equalsIgnoreCase(s.getRepository().getDescriptor("option.observation.supported"));
    }
}

