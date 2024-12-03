/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 *  org.jboss.logging.Logger
 */
package org.hibernate.context.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.transaction.Synchronization;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.spi.AbstractCurrentSessionContext;
import org.hibernate.engine.jdbc.LobCreationContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.jboss.logging.Logger;

public class ThreadLocalSessionContext
extends AbstractCurrentSessionContext {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)ThreadLocalSessionContext.class.getName());
    private static final Class[] SESSION_PROXY_INTERFACES = new Class[]{Session.class, SessionImplementor.class, EventSource.class, LobCreationContext.class};
    private static final ThreadLocal<Map<SessionFactory, Session>> CONTEXT_TL = ThreadLocal.withInitial(HashMap::new);

    public ThreadLocalSessionContext(SessionFactoryImplementor factory) {
        super(factory);
    }

    @Override
    public final Session currentSession() throws HibernateException {
        Session current = ThreadLocalSessionContext.existingSession(this.factory());
        if (current == null) {
            current = this.buildOrObtainSession();
            current.getTransaction().registerSynchronization(this.buildCleanupSynch());
            if (this.needsWrapping(current)) {
                current = this.wrap(current);
            }
            ThreadLocalSessionContext.doBind(current, this.factory());
        } else {
            this.validateExistingSession(current);
        }
        return current;
    }

    private boolean needsWrapping(Session session) {
        InvocationHandler invocationHandler;
        return !Proxy.isProxyClass(session.getClass()) || (invocationHandler = Proxy.getInvocationHandler(session)) == null || !TransactionProtectionWrapper.class.isInstance(invocationHandler);
    }

    protected SessionFactoryImplementor getFactory() {
        return this.factory();
    }

    protected Session buildOrObtainSession() {
        return this.baseSessionBuilder().autoClose(this.isAutoCloseEnabled()).connectionReleaseMode(this.getConnectionReleaseMode()).flushBeforeCompletion(this.isAutoFlushEnabled()).openSession();
    }

    protected CleanupSync buildCleanupSynch() {
        return new CleanupSync(this.factory());
    }

    protected boolean isAutoCloseEnabled() {
        return true;
    }

    protected boolean isAutoFlushEnabled() {
        return true;
    }

    protected ConnectionReleaseMode getConnectionReleaseMode() {
        return this.factory().getSettings().getConnectionReleaseMode();
    }

    protected Session wrap(Session session) {
        TransactionProtectionWrapper wrapper = new TransactionProtectionWrapper(session);
        Session wrapped = (Session)Proxy.newProxyInstance(Session.class.getClassLoader(), SESSION_PROXY_INTERFACES, (InvocationHandler)wrapper);
        wrapper.setWrapped(wrapped);
        return wrapped;
    }

    public static void bind(Session session) {
        SessionFactory factory = session.getSessionFactory();
        ThreadLocalSessionContext.doBind(session, factory);
    }

    private static void terminateOrphanedSession(Session orphan) {
        if (orphan != null) {
            LOG.alreadySessionBound();
            try {
                Transaction orphanTransaction = orphan.getTransaction();
                if (orphanTransaction != null && orphanTransaction.getStatus() == TransactionStatus.ACTIVE) {
                    try {
                        orphanTransaction.rollback();
                    }
                    catch (Throwable t) {
                        LOG.debug("Unable to rollback transaction for orphaned session", t);
                    }
                }
            }
            finally {
                try {
                    orphan.close();
                }
                catch (Throwable t) {
                    LOG.debug("Unable to close orphaned session", t);
                }
            }
        }
    }

    public static Session unbind(SessionFactory factory) {
        return ThreadLocalSessionContext.doUnbind(factory, true);
    }

    private static Session existingSession(SessionFactory factory) {
        return ThreadLocalSessionContext.sessionMap().get(factory);
    }

    protected static Map<SessionFactory, Session> sessionMap() {
        return CONTEXT_TL.get();
    }

    private static void doBind(Session session, SessionFactory factory) {
        Session orphanedPreviousSession = ThreadLocalSessionContext.sessionMap().put(factory, session);
        ThreadLocalSessionContext.terminateOrphanedSession(orphanedPreviousSession);
    }

    private static Session doUnbind(SessionFactory factory, boolean releaseMapIfEmpty) {
        Map<SessionFactory, Session> sessionMap = ThreadLocalSessionContext.sessionMap();
        Session session = sessionMap.remove(factory);
        if (releaseMapIfEmpty && sessionMap.isEmpty()) {
            CONTEXT_TL.remove();
        }
        return session;
    }

    private class TransactionProtectionWrapper
    implements InvocationHandler,
    Serializable {
        private final Session realSession;
        private Session wrappedSession;

        public TransactionProtectionWrapper(Session realSession) {
            this.realSession = realSession;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if ("equals".equals(methodName) && method.getParameterCount() == 1) {
                if (args[0] == null || !Proxy.isProxyClass(args[0].getClass())) {
                    return false;
                }
                return this.equals(Proxy.getInvocationHandler(args[0]));
            }
            if ("hashCode".equals(methodName) && method.getParameterCount() == 0) {
                return this.hashCode();
            }
            if ("toString".equals(methodName) && method.getParameterCount() == 0) {
                return String.format(Locale.ROOT, "ThreadLocalSessionContext.TransactionProtectionWrapper[%s]", this.realSession);
            }
            try {
                if ("close".equals(methodName)) {
                    ThreadLocalSessionContext.unbind(this.realSession.getSessionFactory());
                } else if ("getStatistics".equals(methodName) || "isOpen".equals(methodName) || "getListeners".equals(methodName)) {
                    LOG.tracef("Allowing invocation [%s] to proceed to real session", methodName);
                } else if (!this.realSession.isOpen()) {
                    LOG.tracef("Allowing invocation [%s] to proceed to real (closed) session", methodName);
                } else if (this.realSession.getTransaction().getStatus() != TransactionStatus.ACTIVE) {
                    if ("beginTransaction".equals(methodName) || "getTransaction".equals(methodName) || "isTransactionInProgress".equals(methodName) || "setFlushMode".equals(methodName) || "setHibernateFlushMode".equals(methodName) || "getFactory".equals(methodName) || "getSessionFactory".equals(methodName) || "getTenantIdentifier".equals(methodName)) {
                        LOG.tracef("Allowing invocation [%s] to proceed to real (non-transacted) session", methodName);
                    } else if ("reconnect".equals(methodName) || "disconnect".equals(methodName)) {
                        LOG.tracef("Allowing invocation [%s] to proceed to real (non-transacted) session - deprecated methods", methodName);
                    } else {
                        throw new HibernateException("Calling method '" + methodName + "' is not valid without an active transaction (Current status: " + (Object)((Object)this.realSession.getTransaction().getStatus()) + ")");
                    }
                }
                LOG.tracef("Allowing proxy invocation [%s] to proceed to real session", methodName);
                return method.invoke((Object)this.realSession, args);
            }
            catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof RuntimeException) {
                    throw (RuntimeException)e.getTargetException();
                }
                throw e;
            }
        }

        public void setWrapped(Session wrapped) {
            this.wrappedSession = wrapped;
        }

        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.defaultWriteObject();
            if (ThreadLocalSessionContext.existingSession(ThreadLocalSessionContext.this.factory()) == this.wrappedSession) {
                ThreadLocalSessionContext.unbind(ThreadLocalSessionContext.this.factory());
            }
        }

        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            ois.defaultReadObject();
            this.realSession.getTransaction().registerSynchronization(ThreadLocalSessionContext.this.buildCleanupSynch());
            ThreadLocalSessionContext.doBind(this.wrappedSession, ThreadLocalSessionContext.this.factory());
        }
    }

    protected static class CleanupSync
    implements Synchronization,
    Serializable {
        protected final SessionFactory factory;

        public CleanupSync(SessionFactory factory) {
            this.factory = factory;
        }

        public void beforeCompletion() {
        }

        public void afterCompletion(int i) {
            ThreadLocalSessionContext.unbind(this.factory);
        }
    }
}

