/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ejb.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.naming.Context;
import javax.naming.NamingException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.ejb.access.EjbAccessException;
import org.springframework.jndi.JndiObjectLocator;
import org.springframework.lang.Nullable;

public abstract class AbstractSlsbInvokerInterceptor
extends JndiObjectLocator
implements MethodInterceptor {
    private boolean lookupHomeOnStartup = true;
    private boolean cacheHome = true;
    private boolean exposeAccessContext = false;
    @Nullable
    private Object cachedHome;
    @Nullable
    private Method createMethod;
    private final Object homeMonitor = new Object();

    public void setLookupHomeOnStartup(boolean lookupHomeOnStartup) {
        this.lookupHomeOnStartup = lookupHomeOnStartup;
    }

    public void setCacheHome(boolean cacheHome) {
        this.cacheHome = cacheHome;
    }

    public void setExposeAccessContext(boolean exposeAccessContext) {
        this.exposeAccessContext = exposeAccessContext;
    }

    @Override
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        if (this.lookupHomeOnStartup) {
            this.refreshHome();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void refreshHome() throws NamingException {
        Object object = this.homeMonitor;
        synchronized (object) {
            Object home = this.lookup();
            if (this.cacheHome) {
                this.cachedHome = home;
                this.createMethod = this.getCreateMethod(home);
            }
        }
    }

    @Nullable
    protected Method getCreateMethod(Object home) throws EjbAccessException {
        try {
            return home.getClass().getMethod("create", new Class[0]);
        }
        catch (NoSuchMethodException ex) {
            throw new EjbAccessException("EJB home [" + home + "] has no no-arg create() method");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Object getHome() throws NamingException {
        if (!this.cacheHome || this.lookupHomeOnStartup && !this.isHomeRefreshable()) {
            return this.cachedHome != null ? this.cachedHome : this.lookup();
        }
        Object object = this.homeMonitor;
        synchronized (object) {
            if (this.cachedHome == null) {
                this.cachedHome = this.lookup();
                this.createMethod = this.getCreateMethod(this.cachedHome);
            }
            return this.cachedHome;
        }
    }

    protected boolean isHomeRefreshable() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Context ctx = this.exposeAccessContext ? this.getJndiTemplate().getContext() : null;
        try {
            Object object = this.invokeInContext(invocation);
            return object;
        }
        finally {
            this.getJndiTemplate().releaseContext(ctx);
        }
    }

    @Nullable
    protected abstract Object invokeInContext(MethodInvocation var1) throws Throwable;

    protected Object create() throws NamingException, InvocationTargetException {
        try {
            Object home = this.getHome();
            Method createMethodToUse = this.createMethod;
            if (createMethodToUse == null) {
                createMethodToUse = this.getCreateMethod(home);
            }
            if (createMethodToUse == null) {
                return home;
            }
            return createMethodToUse.invoke(home, (Object[])null);
        }
        catch (IllegalAccessException ex) {
            throw new EjbAccessException("Could not access EJB home create() method", ex);
        }
    }
}

