/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 */
package org.apache.catalina.valves;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.servlet.ServletException;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.Store;
import org.apache.catalina.StoreManager;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

public class PersistentValve
extends ValveBase {
    private static final ClassLoader MY_CLASSLOADER = PersistentValve.class.getClassLoader();
    private volatile boolean clBindRequired;
    protected Pattern filter = null;
    private ConcurrentMap<String, UsageCountingSemaphore> sessionToSemaphoreMap = new ConcurrentHashMap<String, UsageCountingSemaphore>();
    private boolean semaphoreFairness = true;
    private boolean semaphoreBlockOnAcquire = true;
    private boolean semaphoreAcquireUninterruptibly = true;

    public PersistentValve() {
        super(true);
    }

    @Override
    public void setContainer(Container container) {
        super.setContainer(container);
        this.clBindRequired = container instanceof Engine || container instanceof Host;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        block44: {
            String sessionId;
            block45: {
                Store store;
                boolean mustReleaseSemaphore;
                UsageCountingSemaphore semaphore;
                Context context;
                block39: {
                    block41: {
                        block42: {
                            block40: {
                                if (this.isRequestWithoutSession(request.getDecodedRequestURI())) {
                                    if (this.containerLog.isDebugEnabled()) {
                                        this.containerLog.debug((Object)sm.getString("persistentValve.requestIgnore", new Object[]{request.getDecodedRequestURI()}));
                                    }
                                    this.getNext().invoke(request, response);
                                    return;
                                }
                                if (this.containerLog.isDebugEnabled()) {
                                    this.containerLog.debug((Object)sm.getString("persistentValve.requestProcess", new Object[]{request.getDecodedRequestURI()}));
                                }
                                if ((context = request.getContext()) == null) {
                                    response.sendError(500, sm.getString("standardHost.noContext"));
                                    return;
                                }
                                sessionId = request.getRequestedSessionId();
                                semaphore = null;
                                mustReleaseSemaphore = true;
                                try {
                                    if (sessionId == null) break block39;
                                    semaphore = this.sessionToSemaphoreMap.compute(sessionId, (k, v) -> v == null ? new UsageCountingSemaphore(this.semaphoreFairness) : ((UsageCountingSemaphore)v).incrementUsageCount());
                                    if (!this.semaphoreBlockOnAcquire) break block40;
                                    if (this.semaphoreAcquireUninterruptibly) {
                                        semaphore.acquireUninterruptibly();
                                        break block39;
                                    }
                                    try {
                                        semaphore.acquire();
                                        break block39;
                                    }
                                    catch (InterruptedException e) {
                                        mustReleaseSemaphore = false;
                                        this.onSemaphoreNotAcquired(request, response);
                                        if (this.containerLog.isDebugEnabled()) {
                                            this.containerLog.debug((Object)sm.getString("persistentValve.acquireInterrupted", new Object[]{request.getDecodedRequestURI()}));
                                        }
                                        if (semaphore != null) {
                                            if (mustReleaseSemaphore) {
                                                semaphore.release();
                                            }
                                            this.sessionToSemaphoreMap.computeIfPresent(sessionId, (k, v) -> ((UsageCountingSemaphore)v).decrementAndGetUsageCount() == 0L ? null : v);
                                        }
                                        return;
                                    }
                                }
                                catch (Throwable throwable) {
                                    if (semaphore != null) {
                                        if (mustReleaseSemaphore) {
                                            semaphore.release();
                                        }
                                        this.sessionToSemaphoreMap.computeIfPresent(sessionId, (k, v) -> ((UsageCountingSemaphore)v).decrementAndGetUsageCount() == 0L ? null : v);
                                    }
                                    throw throwable;
                                }
                            }
                            if (semaphore.tryAcquire()) break block39;
                            this.onSemaphoreNotAcquired(request, response);
                            if (this.containerLog.isDebugEnabled()) {
                                this.containerLog.debug((Object)sm.getString("persistentValve.acquireFailed", new Object[]{request.getDecodedRequestURI()}));
                            }
                            if (semaphore == null) break block41;
                            if (!mustReleaseSemaphore) break block42;
                            semaphore.release();
                        }
                        this.sessionToSemaphoreMap.computeIfPresent(sessionId, (k, v) -> ((UsageCountingSemaphore)v).decrementAndGetUsageCount() == 0L ? null : v);
                    }
                    return;
                }
                Manager manager = context.getManager();
                if (sessionId != null && manager instanceof StoreManager && (store = ((StoreManager)((Object)manager)).getStore()) != null) {
                    Session session = null;
                    try {
                        session = store.load(sessionId);
                    }
                    catch (Exception e) {
                        this.containerLog.error((Object)"deserializeError");
                    }
                    if (session != null) {
                        if (!session.isValid() || this.isSessionStale(session, System.currentTimeMillis())) {
                            if (this.containerLog.isDebugEnabled()) {
                                this.containerLog.debug((Object)"session swapped in is invalid or expired");
                            }
                            session.expire();
                            store.remove(sessionId);
                        } else {
                            session.setManager(manager);
                            manager.add(session);
                            session.access();
                            session.endAccess();
                        }
                    }
                }
                if (this.containerLog.isDebugEnabled()) {
                    this.containerLog.debug((Object)("sessionId: " + sessionId));
                }
                this.getNext().invoke(request, response);
                if (!request.isAsync()) {
                    Session hsess;
                    try {
                        hsess = request.getSessionInternal(false);
                    }
                    catch (Exception ex) {
                        hsess = null;
                    }
                    String newsessionId = null;
                    if (hsess != null) {
                        newsessionId = hsess.getIdInternal();
                    }
                    if (this.containerLog.isDebugEnabled()) {
                        this.containerLog.debug((Object)("newsessionId: " + newsessionId));
                    }
                    if (newsessionId != null) {
                        try {
                            this.bind(context);
                            if (manager instanceof StoreManager) {
                                Session session = manager.findSession(newsessionId);
                                Store store2 = ((StoreManager)((Object)manager)).getStore();
                                boolean stored = false;
                                if (session != null && store2 != null && session.isValid() && !this.isSessionStale(session, System.currentTimeMillis())) {
                                    store2.save(session);
                                    ((StoreManager)((Object)manager)).removeSuper(session);
                                    session.recycle();
                                    stored = true;
                                }
                                if (!stored && this.containerLog.isDebugEnabled()) {
                                    this.containerLog.debug((Object)("newsessionId store: " + store2 + " session: " + session + " valid: " + (session == null ? "N/A" : Boolean.toString(session.isValid())) + " stale: " + this.isSessionStale(session, System.currentTimeMillis())));
                                }
                            } else if (this.containerLog.isDebugEnabled()) {
                                this.containerLog.debug((Object)("newsessionId Manager: " + manager));
                            }
                        }
                        finally {
                            this.unbind(context);
                        }
                    }
                }
                if (semaphore == null) break block44;
                if (!mustReleaseSemaphore) break block45;
                semaphore.release();
            }
            this.sessionToSemaphoreMap.computeIfPresent(sessionId, (k, v) -> ((UsageCountingSemaphore)v).decrementAndGetUsageCount() == 0L ? null : v);
        }
    }

    protected void onSemaphoreNotAcquired(Request request, Response response) throws IOException {
        response.sendError(429);
    }

    protected boolean isSessionStale(Session session, long timeNow) {
        int timeIdle;
        int maxInactiveInterval;
        return session != null && (maxInactiveInterval = session.getMaxInactiveInterval()) > 0 && (timeIdle = (int)(session.getIdleTimeInternal() / 1000L)) >= maxInactiveInterval;
    }

    private void bind(Context context) {
        if (this.clBindRequired) {
            context.bind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);
        }
    }

    private void unbind(Context context) {
        if (this.clBindRequired) {
            context.unbind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);
        }
    }

    protected boolean isRequestWithoutSession(String uri) {
        Pattern f = this.filter;
        return f != null && f.matcher(uri).matches();
    }

    public String getFilter() {
        if (this.filter == null) {
            return null;
        }
        return this.filter.toString();
    }

    public void setFilter(String filter) {
        if (filter == null || filter.length() == 0) {
            this.filter = null;
        } else {
            try {
                this.filter = Pattern.compile(filter);
            }
            catch (PatternSyntaxException pse) {
                this.container.getLogger().error((Object)sm.getString("persistentValve.filter.failure", new Object[]{filter}), (Throwable)pse);
            }
        }
    }

    public boolean isSemaphoreFairness() {
        return this.semaphoreFairness;
    }

    public void setSemaphoreFairness(boolean semaphoreFairness) {
        this.semaphoreFairness = semaphoreFairness;
    }

    public boolean isSemaphoreBlockOnAcquire() {
        return this.semaphoreBlockOnAcquire;
    }

    public void setSemaphoreBlockOnAcquire(boolean semaphoreBlockOnAcquire) {
        this.semaphoreBlockOnAcquire = semaphoreBlockOnAcquire;
    }

    public boolean isSemaphoreAcquireUninterruptibly() {
        return this.semaphoreAcquireUninterruptibly;
    }

    public void setSemaphoreAcquireUninterruptibly(boolean semaphoreAcquireUninterruptibly) {
        this.semaphoreAcquireUninterruptibly = semaphoreAcquireUninterruptibly;
    }

    private static class UsageCountingSemaphore {
        private final AtomicLong usageCount = new AtomicLong(1L);
        private final Semaphore semaphore;

        private UsageCountingSemaphore(boolean fairness) {
            this.semaphore = new Semaphore(1, fairness);
        }

        private UsageCountingSemaphore incrementUsageCount() {
            this.usageCount.incrementAndGet();
            return this;
        }

        private long decrementAndGetUsageCount() {
            return this.usageCount.decrementAndGet();
        }

        private void acquire() throws InterruptedException {
            this.semaphore.acquire();
        }

        private void acquireUninterruptibly() {
            this.semaphore.acquireUninterruptibly();
        }

        private boolean tryAcquire() {
            return this.semaphore.tryAcquire();
        }

        private void release() {
            this.semaphore.release();
        }
    }
}

