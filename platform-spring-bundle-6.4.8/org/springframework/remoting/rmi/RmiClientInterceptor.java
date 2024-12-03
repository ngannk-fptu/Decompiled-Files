/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.rmi;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteInvocationFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiClientInterceptorUtils;
import org.springframework.remoting.rmi.RmiInvocationHandler;
import org.springframework.remoting.support.RemoteInvocationBasedAccessor;
import org.springframework.remoting.support.RemoteInvocationUtils;

@Deprecated
public class RmiClientInterceptor
extends RemoteInvocationBasedAccessor
implements MethodInterceptor {
    private boolean lookupStubOnStartup = true;
    private boolean cacheStub = true;
    private boolean refreshStubOnConnectFailure = false;
    private RMIClientSocketFactory registryClientSocketFactory;
    private Remote cachedStub;
    private final Object stubMonitor = new Object();

    public void setLookupStubOnStartup(boolean lookupStubOnStartup) {
        this.lookupStubOnStartup = lookupStubOnStartup;
    }

    public void setCacheStub(boolean cacheStub) {
        this.cacheStub = cacheStub;
    }

    public void setRefreshStubOnConnectFailure(boolean refreshStubOnConnectFailure) {
        this.refreshStubOnConnectFailure = refreshStubOnConnectFailure;
    }

    public void setRegistryClientSocketFactory(RMIClientSocketFactory registryClientSocketFactory) {
        this.registryClientSocketFactory = registryClientSocketFactory;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.prepare();
    }

    public void prepare() throws RemoteLookupFailureException {
        if (this.lookupStubOnStartup) {
            Remote remoteObj = this.lookupStub();
            if (this.logger.isDebugEnabled()) {
                if (remoteObj instanceof RmiInvocationHandler) {
                    this.logger.debug((Object)("RMI stub [" + this.getServiceUrl() + "] is an RMI invoker"));
                } else if (this.getServiceInterface() != null) {
                    boolean isImpl = this.getServiceInterface().isInstance(remoteObj);
                    this.logger.debug((Object)("Using service interface [" + this.getServiceInterface().getName() + "] for RMI stub [" + this.getServiceUrl() + "] - " + (!isImpl ? "not " : "") + "directly implemented"));
                }
            }
            if (this.cacheStub) {
                this.cachedStub = remoteObj;
            }
        }
    }

    protected Remote lookupStub() throws RemoteLookupFailureException {
        try {
            Remote stub = null;
            if (this.registryClientSocketFactory != null) {
                URL url = new URL(null, this.getServiceUrl(), new DummyURLStreamHandler());
                String protocol = url.getProtocol();
                if (protocol != null && !"rmi".equals(protocol)) {
                    throw new MalformedURLException("Invalid URL scheme '" + protocol + "'");
                }
                String host = url.getHost();
                int port = url.getPort();
                String name = url.getPath();
                if (name != null && name.startsWith("/")) {
                    name = name.substring(1);
                }
                Registry registry = LocateRegistry.getRegistry(host, port, this.registryClientSocketFactory);
                stub = registry.lookup(name);
            } else {
                stub = Naming.lookup(this.getServiceUrl());
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Located RMI stub with URL [" + this.getServiceUrl() + "]"));
            }
            return stub;
        }
        catch (MalformedURLException ex) {
            throw new RemoteLookupFailureException("Service URL [" + this.getServiceUrl() + "] is invalid", ex);
        }
        catch (NotBoundException ex) {
            throw new RemoteLookupFailureException("Could not find RMI service [" + this.getServiceUrl() + "] in RMI registry", ex);
        }
        catch (RemoteException ex) {
            throw new RemoteLookupFailureException("Lookup of RMI stub failed", ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Remote getStub() throws RemoteLookupFailureException {
        if (!this.cacheStub || this.lookupStubOnStartup && !this.refreshStubOnConnectFailure) {
            return this.cachedStub != null ? this.cachedStub : this.lookupStub();
        }
        Object object = this.stubMonitor;
        synchronized (object) {
            if (this.cachedStub == null) {
                this.cachedStub = this.lookupStub();
            }
            return this.cachedStub;
        }
    }

    @Override
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Remote stub = this.getStub();
        try {
            return this.doInvoke(invocation, stub);
        }
        catch (RemoteConnectFailureException ex) {
            return this.handleRemoteConnectFailure(invocation, ex);
        }
        catch (RemoteException ex) {
            if (this.isConnectFailure(ex)) {
                return this.handleRemoteConnectFailure(invocation, ex);
            }
            throw ex;
        }
    }

    protected boolean isConnectFailure(RemoteException ex) {
        return RmiClientInterceptorUtils.isConnectFailure(ex);
    }

    @Nullable
    private Object handleRemoteConnectFailure(MethodInvocation invocation, Exception ex) throws Throwable {
        if (this.refreshStubOnConnectFailure) {
            String msg = "Could not connect to RMI service [" + this.getServiceUrl() + "] - retrying";
            if (this.logger.isDebugEnabled()) {
                this.logger.warn((Object)msg, (Throwable)ex);
            } else if (this.logger.isWarnEnabled()) {
                this.logger.warn((Object)msg);
            }
            return this.refreshAndRetry(invocation);
        }
        throw ex;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    protected Object refreshAndRetry(MethodInvocation invocation) throws Throwable {
        Remote freshStub = null;
        Object object = this.stubMonitor;
        synchronized (object) {
            this.cachedStub = null;
            freshStub = this.lookupStub();
            if (this.cacheStub) {
                this.cachedStub = freshStub;
            }
        }
        return this.doInvoke(invocation, freshStub);
    }

    @Nullable
    protected Object doInvoke(MethodInvocation invocation, Remote stub) throws Throwable {
        if (stub instanceof RmiInvocationHandler) {
            try {
                return this.doInvoke(invocation, (RmiInvocationHandler)stub);
            }
            catch (RemoteException ex) {
                throw RmiClientInterceptorUtils.convertRmiAccessException(invocation.getMethod(), ex, this.isConnectFailure(ex), this.getServiceUrl());
            }
            catch (InvocationTargetException ex) {
                Throwable exToThrow = ex.getTargetException();
                RemoteInvocationUtils.fillInClientStackTraceIfPossible(exToThrow);
                throw exToThrow;
            }
            catch (Throwable ex) {
                throw new RemoteInvocationFailureException("Invocation of method [" + invocation.getMethod() + "] failed in RMI service [" + this.getServiceUrl() + "]", ex);
            }
        }
        try {
            return RmiClientInterceptorUtils.invokeRemoteMethod(invocation, stub);
        }
        catch (InvocationTargetException ex) {
            Throwable targetEx = ex.getTargetException();
            if (targetEx instanceof RemoteException) {
                RemoteException rex = (RemoteException)targetEx;
                throw RmiClientInterceptorUtils.convertRmiAccessException(invocation.getMethod(), rex, this.isConnectFailure(rex), this.getServiceUrl());
            }
            throw targetEx;
        }
    }

    @Nullable
    protected Object doInvoke(MethodInvocation methodInvocation, RmiInvocationHandler invocationHandler) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (AopUtils.isToStringMethod(methodInvocation.getMethod())) {
            return "RMI invoker proxy for service URL [" + this.getServiceUrl() + "]";
        }
        return invocationHandler.invoke(this.createRemoteInvocation(methodInvocation));
    }

    private static class DummyURLStreamHandler
    extends URLStreamHandler {
        private DummyURLStreamHandler() {
        }

        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            throw new UnsupportedOperationException();
        }
    }
}

