/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.xa.processor;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import net.sf.ehcache.transaction.xa.EhcacheXAException;
import net.sf.ehcache.transaction.xa.EhcacheXAResourceImpl;
import net.sf.ehcache.transaction.xa.processor.XARequest;
import net.sf.ehcache.transaction.xa.processor.XAThreadPool;

public class XARequestProcessor {
    private static volatile XAThreadPool xaProcessorPool;
    private final ConcurrentMap<Xid, XAThreadPool.MultiRunner> executorMap = new ConcurrentHashMap<Xid, XAThreadPool.MultiRunner>();
    private final EhcacheXAResourceImpl resourceImpl;

    public XARequestProcessor(EhcacheXAResourceImpl resourceImpl) {
        this.resourceImpl = resourceImpl;
        if (xaProcessorPool == null) {
            xaProcessorPool = new XAThreadPool();
        }
    }

    public static void shutdown() {
        if (xaProcessorPool != null) {
            xaProcessorPool.shutdown();
            xaProcessorPool = null;
        }
    }

    public int process(XARequest request) throws XAException {
        XAResponse xaResponse;
        XAThreadPool.MultiRunner multiRunner = this.getOrCreateThread(request.getXid());
        try {
            xaResponse = (XAResponse)multiRunner.execute(new XARequestCallable(this.resourceImpl, request, request.getXid()));
        }
        catch (InterruptedException e) {
            this.cleanupThread(request.getXid());
            throw new EhcacheXAException(e.getMessage(), -3, e);
        }
        catch (ExecutionException e) {
            this.cleanupThread(request.getXid());
            throw new EhcacheXAException(e.getMessage(), -3, e);
        }
        if (xaResponse.getXaException() != null) {
            this.cleanupThread(request.getXid());
            throw new EhcacheXAException("XA " + request.getRequestType().toString().toLowerCase() + " request failed on [" + request.getXid() + "]", xaResponse.getXaException().errorCode, xaResponse.getXaException());
        }
        if (request.getRequestType().equals((Object)XARequest.RequestType.COMMIT) || request.getRequestType().equals((Object)XARequest.RequestType.ROLLBACK) || request.getRequestType().equals((Object)XARequest.RequestType.FORGET) || request.getRequestType().equals((Object)XARequest.RequestType.PREPARE) && xaResponse.getFlags() == 3) {
            this.cleanupThread(request.getXid());
        }
        return xaResponse.getFlags();
    }

    private XAThreadPool.MultiRunner getOrCreateThread(Xid xid) {
        XAThreadPool.MultiRunner service = (XAThreadPool.MultiRunner)this.executorMap.get(xid);
        if (service == null) {
            service = xaProcessorPool.getMultiRunner();
            this.executorMap.put(xid, service);
        }
        return service;
    }

    private void cleanupThread(Xid xid) {
        XAThreadPool.MultiRunner service = (XAThreadPool.MultiRunner)this.executorMap.remove(xid);
        service.release();
    }

    private static class XAResponse {
        private final int flags;
        private final XAException xaException;

        public XAResponse(int flags, XAException xaException) {
            this.flags = flags;
            this.xaException = xaException;
        }

        public int getFlags() {
            return this.flags;
        }

        public XAException getXaException() {
            return this.xaException;
        }
    }

    private static class XARequestCallable
    implements Callable<XAResponse> {
        private final EhcacheXAResourceImpl resourceImpl;
        private final XARequest request;
        private final Xid xid;

        public XARequestCallable(EhcacheXAResourceImpl resourceImpl, XARequest request, Xid xid) {
            this.resourceImpl = resourceImpl;
            this.request = request;
            this.xid = xid;
        }

        @Override
        public XAResponse call() throws Exception {
            Thread.currentThread().setName("XA-Request processor Thread Xid [ " + this.xid + " ]");
            int returnFlag = 0;
            XAException xaException = null;
            try {
                switch (this.request.getRequestType()) {
                    case FORGET: {
                        this.resourceImpl.forgetInternal(this.request.getXid());
                        break;
                    }
                    case PREPARE: {
                        returnFlag = this.resourceImpl.prepareInternal(this.request.getXid());
                        break;
                    }
                    case ROLLBACK: {
                        this.resourceImpl.rollbackInternal(this.request.getXid());
                        break;
                    }
                    case COMMIT: {
                        this.resourceImpl.commitInternal(this.request.getXid(), this.request.isOnePhase());
                        break;
                    }
                    default: {
                        throw new EhcacheXAException("Unknown enum type: " + this.request.getRequestType(), -3);
                    }
                }
            }
            catch (XAException xaE) {
                xaException = xaE;
            }
            catch (Throwable t) {
                xaException = new EhcacheXAException("Some problem happened while processing xa request: " + this.request.getRequestType(), -3, t);
            }
            return new XAResponse(returnFlag, xaException);
        }
    }
}

