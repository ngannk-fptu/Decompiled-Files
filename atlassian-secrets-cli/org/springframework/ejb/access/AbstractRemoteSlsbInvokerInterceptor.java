/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBHome
 *  javax.ejb.EJBObject
 */
package org.springframework.ejb.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.naming.NamingException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.ejb.access.AbstractSlsbInvokerInterceptor;
import org.springframework.ejb.access.EjbAccessException;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiClientInterceptorUtils;

public abstract class AbstractRemoteSlsbInvokerInterceptor
extends AbstractSlsbInvokerInterceptor {
    private boolean refreshHomeOnConnectFailure = false;
    private volatile boolean homeAsComponent = false;

    public void setRefreshHomeOnConnectFailure(boolean refreshHomeOnConnectFailure) {
        this.refreshHomeOnConnectFailure = refreshHomeOnConnectFailure;
    }

    @Override
    protected boolean isHomeRefreshable() {
        return this.refreshHomeOnConnectFailure;
    }

    @Override
    protected Method getCreateMethod(Object home) throws EjbAccessException {
        if (this.homeAsComponent) {
            return null;
        }
        if (!(home instanceof EJBHome)) {
            this.homeAsComponent = true;
            return null;
        }
        return super.getCreateMethod(home);
    }

    @Override
    @Nullable
    public Object invokeInContext(MethodInvocation invocation) throws Throwable {
        try {
            return this.doInvoke(invocation);
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
        if (this.refreshHomeOnConnectFailure) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not connect to remote EJB [" + this.getJndiName() + "] - retrying", ex);
            } else if (this.logger.isWarnEnabled()) {
                this.logger.warn("Could not connect to remote EJB [" + this.getJndiName() + "] - retrying");
            }
            return this.refreshAndRetry(invocation);
        }
        throw ex;
    }

    @Nullable
    protected Object refreshAndRetry(MethodInvocation invocation) throws Throwable {
        try {
            this.refreshHome();
        }
        catch (NamingException ex) {
            throw new RemoteLookupFailureException("Failed to locate remote EJB [" + this.getJndiName() + "]", ex);
        }
        return this.doInvoke(invocation);
    }

    @Nullable
    protected abstract Object doInvoke(MethodInvocation var1) throws Throwable;

    protected Object newSessionBeanInstance() throws NamingException, InvocationTargetException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Trying to create reference to remote EJB");
        }
        Object ejbInstance = this.create();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Obtained reference to remote EJB: " + ejbInstance);
        }
        return ejbInstance;
    }

    protected void removeSessionBeanInstance(@Nullable EJBObject ejb) {
        if (ejb != null && !this.homeAsComponent) {
            try {
                ejb.remove();
            }
            catch (Throwable ex) {
                this.logger.warn("Could not invoke 'remove' on remote EJB proxy", ex);
            }
        }
    }
}

