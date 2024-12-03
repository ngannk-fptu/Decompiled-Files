/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.TargetSource
 *  org.springframework.lang.Nullable
 */
package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.aop.TargetSource;
import org.springframework.jndi.JndiLookupFailureException;
import org.springframework.jndi.JndiObjectLocator;
import org.springframework.lang.Nullable;

public class JndiObjectTargetSource
extends JndiObjectLocator
implements TargetSource {
    private boolean lookupOnStartup = true;
    private boolean cache = true;
    @Nullable
    private Object cachedObject;
    @Nullable
    private Class<?> targetClass;

    public void setLookupOnStartup(boolean lookupOnStartup) {
        this.lookupOnStartup = lookupOnStartup;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    @Override
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        if (this.lookupOnStartup) {
            Object object = this.lookup();
            if (this.cache) {
                this.cachedObject = object;
            } else {
                this.targetClass = object.getClass();
            }
        }
    }

    @Nullable
    public Class<?> getTargetClass() {
        if (this.cachedObject != null) {
            return this.cachedObject.getClass();
        }
        if (this.targetClass != null) {
            return this.targetClass;
        }
        return this.getExpectedType();
    }

    public boolean isStatic() {
        return this.cachedObject != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public Object getTarget() {
        try {
            if (this.lookupOnStartup || !this.cache) {
                return this.cachedObject != null ? this.cachedObject : this.lookup();
            }
            JndiObjectTargetSource jndiObjectTargetSource = this;
            synchronized (jndiObjectTargetSource) {
                if (this.cachedObject == null) {
                    this.cachedObject = this.lookup();
                }
                return this.cachedObject;
            }
        }
        catch (NamingException ex) {
            throw new JndiLookupFailureException("JndiObjectTargetSource failed to obtain new target object", ex);
        }
    }

    public void releaseTarget(Object target) {
    }
}

