/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import javax.naming.Context;
import javax.naming.NamingException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiObjectLocator;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteInvocationFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiClientInterceptorUtils;
import org.springframework.remoting.rmi.RmiInvocationHandler;
import org.springframework.remoting.support.DefaultRemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationFactory;
import org.springframework.util.Assert;

@Deprecated
public class JndiRmiClientInterceptor
extends JndiObjectLocator
implements MethodInterceptor,
InitializingBean {
    private Class<?> serviceInterface;
    private RemoteInvocationFactory remoteInvocationFactory = new DefaultRemoteInvocationFactory();
    private boolean lookupStubOnStartup = true;
    private boolean cacheStub = true;
    private boolean refreshStubOnConnectFailure = false;
    private boolean exposeAccessContext = false;
    private Object cachedStub;
    private final Object stubMonitor = new Object();

    public void setServiceInterface(Class<?> serviceInterface) {
        Assert.notNull(serviceInterface, "'serviceInterface' must not be null");
        Assert.isTrue(serviceInterface.isInterface(), "'serviceInterface' must be an interface");
        this.serviceInterface = serviceInterface;
    }

    public Class<?> getServiceInterface() {
        return this.serviceInterface;
    }

    public void setRemoteInvocationFactory(RemoteInvocationFactory remoteInvocationFactory) {
        this.remoteInvocationFactory = remoteInvocationFactory;
    }

    public RemoteInvocationFactory getRemoteInvocationFactory() {
        return this.remoteInvocationFactory;
    }

    public void setLookupStubOnStartup(boolean lookupStubOnStartup) {
        this.lookupStubOnStartup = lookupStubOnStartup;
    }

    public void setCacheStub(boolean cacheStub) {
        this.cacheStub = cacheStub;
    }

    public void setRefreshStubOnConnectFailure(boolean refreshStubOnConnectFailure) {
        this.refreshStubOnConnectFailure = refreshStubOnConnectFailure;
    }

    public void setExposeAccessContext(boolean exposeAccessContext) {
        this.exposeAccessContext = exposeAccessContext;
    }

    @Override
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        this.prepare();
    }

    public void prepare() throws RemoteLookupFailureException {
        if (this.lookupStubOnStartup) {
            Object remoteObj = this.lookupStub();
            if (this.logger.isDebugEnabled()) {
                if (remoteObj instanceof RmiInvocationHandler) {
                    this.logger.debug((Object)("JNDI RMI object [" + this.getJndiName() + "] is an RMI invoker"));
                } else if (this.getServiceInterface() != null) {
                    boolean isImpl = this.getServiceInterface().isInstance(remoteObj);
                    this.logger.debug((Object)("Using service interface [" + this.getServiceInterface().getName() + "] for JNDI RMI object [" + this.getJndiName() + "] - " + (!isImpl ? "not " : "") + "directly implemented"));
                }
            }
            if (this.cacheStub) {
                this.cachedStub = remoteObj;
            }
        }
    }

    protected Object lookupStub() throws RemoteLookupFailureException {
        try {
            return this.lookup();
        }
        catch (NamingException ex) {
            throw new RemoteLookupFailureException("JNDI lookup for RMI service [" + this.getJndiName() + "] failed", ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Object getStub() throws NamingException, RemoteLookupFailureException {
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
        Object stub;
        try {
            stub = this.getStub();
        }
        catch (NamingException ex) {
            throw new RemoteLookupFailureException("JNDI lookup for RMI service [" + this.getJndiName() + "] failed", ex);
        }
        Context ctx = this.exposeAccessContext ? this.getJndiTemplate().getContext() : null;
        try {
            Object object = this.doInvoke(invocation, stub);
            return object;
        }
        catch (RemoteConnectFailureException ex) {
            Object object = this.handleRemoteConnectFailure(invocation, ex);
            return object;
        }
        catch (RemoteException ex) {
            if (this.isConnectFailure(ex)) {
                Object object = this.handleRemoteConnectFailure(invocation, ex);
                return object;
            }
            throw ex;
        }
        finally {
            this.getJndiTemplate().releaseContext(ctx);
        }
    }

    protected boolean isConnectFailure(RemoteException ex) {
        return RmiClientInterceptorUtils.isConnectFailure(ex);
    }

    private Object handleRemoteConnectFailure(MethodInvocation invocation, Exception ex) throws Throwable {
        if (this.refreshStubOnConnectFailure) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Could not connect to RMI service [" + this.getJndiName() + "] - retrying"), (Throwable)ex);
            } else if (this.logger.isInfoEnabled()) {
                this.logger.info((Object)("Could not connect to RMI service [" + this.getJndiName() + "] - retrying"));
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
        Object freshStub;
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
    protected Object doInvoke(MethodInvocation invocation, Object stub) throws Throwable {
        if (stub instanceof RmiInvocationHandler) {
            try {
                return this.doInvoke(invocation, (RmiInvocationHandler)stub);
            }
            catch (RemoteException ex) {
                throw this.convertRmiAccessException(ex, invocation.getMethod());
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
            catch (Throwable ex) {
                throw new RemoteInvocationFailureException("Invocation of method [" + invocation.getMethod() + "] failed in RMI service [" + this.getJndiName() + "]", ex);
            }
        }
        try {
            return RmiClientInterceptorUtils.invokeRemoteMethod(invocation, stub);
        }
        catch (InvocationTargetException ex) {
            Throwable targetEx = ex.getTargetException();
            if (targetEx instanceof RemoteException) {
                throw this.convertRmiAccessException((RemoteException)targetEx, invocation.getMethod());
            }
            throw targetEx;
        }
    }

    protected Object doInvoke(MethodInvocation methodInvocation, RmiInvocationHandler invocationHandler) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (AopUtils.isToStringMethod(methodInvocation.getMethod())) {
            return "RMI invoker proxy for service URL [" + this.getJndiName() + "]";
        }
        return invocationHandler.invoke(this.createRemoteInvocation(methodInvocation));
    }

    protected RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation) {
        return this.getRemoteInvocationFactory().createRemoteInvocation(methodInvocation);
    }

    private Exception convertRmiAccessException(RemoteException ex, Method method) {
        return RmiClientInterceptorUtils.convertRmiAccessException(method, ex, this.isConnectFailure(ex), this.getJndiName());
    }
}

