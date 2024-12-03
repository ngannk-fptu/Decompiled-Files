/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.ldap.control;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.control.AbstractRequestControlDirContextProcessor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public abstract class AbstractFallbackRequestAndResponseControlDirContextProcessor
extends AbstractRequestControlDirContextProcessor {
    private static final boolean CRITICAL_CONTROL = true;
    protected Class<?> responseControlClass;
    protected Class<?> requestControlClass;
    protected boolean critical = true;
    protected String defaultRequestControl;
    protected String defaultResponseControl;
    protected String fallbackRequestControl;
    protected String fallbackResponseControl;

    protected void loadControlClasses() {
        Assert.notNull((Object)this.defaultRequestControl, (String)"defaultRequestControl must not be null");
        Assert.notNull((Object)this.defaultResponseControl, (String)"defaultResponseControl must not be null");
        Assert.notNull((Object)this.fallbackRequestControl, (String)"fallbackRequestControl must not be null");
        Assert.notNull((Object)this.fallbackResponseControl, (String)"fallbackReponseControl must not be null");
        try {
            this.requestControlClass = Class.forName(this.defaultRequestControl);
            this.responseControlClass = Class.forName(this.defaultResponseControl);
        }
        catch (ClassNotFoundException e) {
            this.log.debug("Default control classes not found - falling back to LdapBP classes", (Throwable)e);
            try {
                this.requestControlClass = Class.forName(this.fallbackRequestControl);
                this.responseControlClass = Class.forName(this.fallbackResponseControl);
            }
            catch (ClassNotFoundException e1) {
                throw new UncategorizedLdapException("Neither default nor fallback classes are available - unable to proceed", e);
            }
        }
    }

    public void setResponseControlClass(Class<?> responseControlClass) {
        this.responseControlClass = responseControlClass;
    }

    public void setRequestControlClass(Class<?> requestControlClass) {
        this.requestControlClass = requestControlClass;
    }

    protected Object invokeMethod(String method, Class<?> clazz, Object control) {
        Method actualMethod = ReflectionUtils.findMethod(clazz, (String)method);
        return ReflectionUtils.invokeMethod((Method)actualMethod, (Object)control);
    }

    public Control createRequestControl(Class<?>[] paramTypes, Object[] params) {
        Constructor constructor = ClassUtils.getConstructorIfAvailable(this.requestControlClass, (Class[])paramTypes);
        if (constructor == null) {
            throw new IllegalArgumentException("Failed to find an appropriate RequestControl constructor");
        }
        Control result = null;
        try {
            result = (Control)constructor.newInstance(params);
        }
        catch (Exception e) {
            ReflectionUtils.handleReflectionException((Exception)e);
        }
        return result;
    }

    @Override
    public void postProcess(DirContext ctx) throws NamingException {
        LdapContext ldapContext = (LdapContext)ctx;
        Control[] responseControls = ldapContext.getResponseControls();
        if (responseControls == null) {
            responseControls = new Control[]{};
        }
        for (Control responseControl : responseControls) {
            if (!responseControl.getClass().isAssignableFrom(this.responseControlClass)) continue;
            this.handleResponse(responseControl);
            return;
        }
        this.log.info("No matching response control found - looking for '" + this.responseControlClass);
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    protected abstract void handleResponse(Object var1);
}

