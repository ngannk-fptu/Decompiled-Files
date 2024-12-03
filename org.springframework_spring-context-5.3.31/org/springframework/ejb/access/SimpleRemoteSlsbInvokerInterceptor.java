/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBObject
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.ejb.access;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBObject;
import javax.naming.NamingException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.ejb.access.AbstractRemoteSlsbInvokerInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiClientInterceptorUtils;

public class SimpleRemoteSlsbInvokerInterceptor
extends AbstractRemoteSlsbInvokerInterceptor
implements DisposableBean {
    private boolean cacheSessionBean = false;
    @Nullable
    private Object beanInstance;
    private final Object beanInstanceMonitor = new Object();

    public void setCacheSessionBean(boolean cacheSessionBean) {
        this.cacheSessionBean = cacheSessionBean;
    }

    @Override
    @Nullable
    protected Object doInvoke(MethodInvocation invocation) throws Throwable {
        Object ejb = null;
        try {
            ejb = this.getSessionBeanInstance();
            Object object = RmiClientInterceptorUtils.invokeRemoteMethod(invocation, ejb);
            return object;
        }
        catch (NamingException ex) {
            throw new RemoteLookupFailureException("Failed to locate remote EJB [" + this.getJndiName() + "]", ex);
        }
        catch (InvocationTargetException ex) {
            Throwable targetEx = ex.getTargetException();
            if (targetEx instanceof RemoteException) {
                RemoteException rex = (RemoteException)targetEx;
                throw RmiClientInterceptorUtils.convertRmiAccessException(invocation.getMethod(), rex, this.isConnectFailure(rex), this.getJndiName());
            }
            if (targetEx instanceof CreateException) {
                throw RmiClientInterceptorUtils.convertRmiAccessException(invocation.getMethod(), targetEx, "Could not create remote EJB [" + this.getJndiName() + "]");
            }
            throw targetEx;
        }
        finally {
            if (ejb instanceof EJBObject) {
                this.releaseSessionBeanInstance((EJBObject)ejb);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Object getSessionBeanInstance() throws NamingException, InvocationTargetException {
        if (this.cacheSessionBean) {
            Object object = this.beanInstanceMonitor;
            synchronized (object) {
                if (this.beanInstance == null) {
                    this.beanInstance = this.newSessionBeanInstance();
                }
                return this.beanInstance;
            }
        }
        return this.newSessionBeanInstance();
    }

    protected void releaseSessionBeanInstance(EJBObject ejb) {
        if (!this.cacheSessionBean) {
            this.removeSessionBeanInstance(ejb);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void refreshHome() throws NamingException {
        super.refreshHome();
        if (this.cacheSessionBean) {
            Object object = this.beanInstanceMonitor;
            synchronized (object) {
                this.beanInstance = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroy() {
        if (this.cacheSessionBean) {
            Object object = this.beanInstanceMonitor;
            synchronized (object) {
                if (this.beanInstance instanceof EJBObject) {
                    this.removeSessionBeanInstance((EJBObject)this.beanInstance);
                }
            }
        }
    }
}

