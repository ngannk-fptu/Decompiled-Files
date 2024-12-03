/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class PagedResultsRequestControl
extends AbstractRequestControlDirContextProcessor {
    private static final boolean CRITICAL_CONTROL = true;
    private static final String DEFAULT_REQUEST_CONTROL = "javax.naming.ldap.PagedResultsControl";
    private static final String LDAPBP_REQUEST_CONTROL = "com.sun.jndi.ldap.ctl.PagedResultsControl";
    private static final String DEFAULT_RESPONSE_CONTROL = "javax.naming.ldap.PagedResultsResponseControl";
    private static final String LDAPBP_RESPONSE_CONTROL = "com.sun.jndi.ldap.ctl.PagedResultsResponseControl";
    private int pageSize;
    private PagedResultsCookie cookie;
    private int resultSize;
    private boolean critical = true;
    private Class responseControlClass;
    private Class requestControlClass;

    public PagedResultsRequestControl(int pageSize) {
        this(pageSize, null);
    }

    public PagedResultsRequestControl(int pageSize, PagedResultsCookie cookie) {
        this.pageSize = pageSize;
        this.cookie = cookie;
        this.loadControlClasses();
    }

    private void loadControlClasses() {
        try {
            this.requestControlClass = Class.forName(DEFAULT_REQUEST_CONTROL);
            this.responseControlClass = Class.forName(DEFAULT_RESPONSE_CONTROL);
        }
        catch (ClassNotFoundException e) {
            this.log.debug("Default control classes not found - falling back to LdapBP classes", (Throwable)e);
            try {
                this.requestControlClass = Class.forName(LDAPBP_REQUEST_CONTROL);
                this.responseControlClass = Class.forName(LDAPBP_RESPONSE_CONTROL);
            }
            catch (ClassNotFoundException e1) {
                throw new UncategorizedLdapException("Neither default nor fallback classes are available - unable to proceed", e);
            }
        }
    }

    public PagedResultsCookie getCookie() {
        return this.cookie;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getResultSize() {
        return this.resultSize;
    }

    public void setResponseControlClass(Class responseControlClass) {
        this.responseControlClass = responseControlClass;
    }

    public void setRequestControlClass(Class requestControlClass) {
        this.requestControlClass = requestControlClass;
    }

    @Override
    public Control createRequestControl() {
        Constructor constructor;
        byte[] actualCookie = null;
        if (this.cookie != null) {
            actualCookie = this.cookie.getCookie();
        }
        if ((constructor = ClassUtils.getConstructorIfAvailable((Class)this.requestControlClass, (Class[])new Class[]{Integer.TYPE, byte[].class, Boolean.TYPE})) == null) {
            throw new IllegalArgumentException("Failed to find an appropriate RequestControl constructor");
        }
        Control result = null;
        try {
            result = (Control)constructor.newInstance(this.pageSize, actualCookie, this.critical);
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
        for (int i = 0; i < responseControls.length; ++i) {
            Control responseControl = responseControls[i];
            if (!responseControl.getClass().isAssignableFrom(this.responseControlClass)) continue;
            Control control = responseControl;
            byte[] result = (byte[])this.invokeMethod("getCookie", this.responseControlClass, control);
            this.cookie = new PagedResultsCookie(result);
            Integer wrapper = (Integer)this.invokeMethod("getResultSize", this.responseControlClass, control);
            this.resultSize = wrapper;
            return;
        }
        this.log.error("No matching response control found for paged results - looking for '{}", (Object)this.responseControlClass);
    }

    private Object invokeMethod(String method, Class clazz, Object control) {
        Method actualMethod = ReflectionUtils.findMethod((Class)clazz, (String)method);
        return ReflectionUtils.invokeMethod((Method)actualMethod, (Object)control);
    }
}

