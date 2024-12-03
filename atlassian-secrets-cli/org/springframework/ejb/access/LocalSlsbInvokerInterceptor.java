/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBLocalHome
 *  javax.ejb.EJBLocalObject
 */
package org.springframework.ejb.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.naming.NamingException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.ejb.access.AbstractSlsbInvokerInterceptor;
import org.springframework.ejb.access.EjbAccessException;
import org.springframework.lang.Nullable;

public class LocalSlsbInvokerInterceptor
extends AbstractSlsbInvokerInterceptor {
    private volatile boolean homeAsComponent = false;

    @Override
    @Nullable
    public Object invokeInContext(MethodInvocation invocation) throws Throwable {
        Object ejb = null;
        try {
            ejb = this.getSessionBeanInstance();
            Method method = invocation.getMethod();
            if (method.getDeclaringClass().isInstance(ejb)) {
                Object object = method.invoke(ejb, invocation.getArguments());
                return object;
            }
            Method ejbMethod = ejb.getClass().getMethod(method.getName(), method.getParameterTypes());
            Object object = ejbMethod.invoke(ejb, invocation.getArguments());
            return object;
        }
        catch (InvocationTargetException ex) {
            Throwable targetEx = ex.getTargetException();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Method of local EJB [" + this.getJndiName() + "] threw exception", targetEx);
            }
            if (targetEx instanceof CreateException) {
                throw new EjbAccessException("Could not create local EJB [" + this.getJndiName() + "]", targetEx);
            }
            throw targetEx;
        }
        catch (NamingException ex) {
            throw new EjbAccessException("Failed to locate local EJB [" + this.getJndiName() + "]", ex);
        }
        catch (IllegalAccessException ex) {
            throw new EjbAccessException("Could not access method [" + invocation.getMethod().getName() + "] of local EJB [" + this.getJndiName() + "]", ex);
        }
        finally {
            if (ejb instanceof EJBLocalObject) {
                this.releaseSessionBeanInstance((EJBLocalObject)ejb);
            }
        }
    }

    @Override
    protected Method getCreateMethod(Object home) throws EjbAccessException {
        if (this.homeAsComponent) {
            return null;
        }
        if (!(home instanceof EJBLocalHome)) {
            this.homeAsComponent = true;
            return null;
        }
        return super.getCreateMethod(home);
    }

    protected Object getSessionBeanInstance() throws NamingException, InvocationTargetException {
        return this.newSessionBeanInstance();
    }

    protected void releaseSessionBeanInstance(EJBLocalObject ejb) {
        this.removeSessionBeanInstance(ejb);
    }

    protected Object newSessionBeanInstance() throws NamingException, InvocationTargetException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Trying to create reference to local EJB");
        }
        Object ejbInstance = this.create();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Obtained reference to local EJB: " + ejbInstance);
        }
        return ejbInstance;
    }

    protected void removeSessionBeanInstance(@Nullable EJBLocalObject ejb) {
        if (ejb != null && !this.homeAsComponent) {
            try {
                ejb.remove();
            }
            catch (Throwable ex) {
                this.logger.warn("Could not invoke 'remove' on local EJB proxy", ex);
            }
        }
    }
}

