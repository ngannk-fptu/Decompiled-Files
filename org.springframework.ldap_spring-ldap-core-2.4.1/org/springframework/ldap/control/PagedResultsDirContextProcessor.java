/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.control;

import javax.naming.ldap.Control;
import org.springframework.ldap.control.AbstractFallbackRequestAndResponseControlDirContextProcessor;
import org.springframework.ldap.control.PagedResultsCookie;

public class PagedResultsDirContextProcessor
extends AbstractFallbackRequestAndResponseControlDirContextProcessor {
    private static final String DEFAULT_REQUEST_CONTROL = "javax.naming.ldap.PagedResultsControl";
    private static final String FALLBACK_REQUEST_CONTROL = "com.sun.jndi.ldap.ctl.PagedResultsControl";
    private static final String DEFAULT_RESPONSE_CONTROL = "javax.naming.ldap.PagedResultsResponseControl";
    private static final String FALLBACK_RESPONSE_CONTROL = "com.sun.jndi.ldap.ctl.PagedResultsResponseControl";
    private int pageSize;
    private PagedResultsCookie cookie;
    private int resultSize;
    private boolean more = true;

    public PagedResultsDirContextProcessor(int pageSize) {
        this(pageSize, null);
    }

    public PagedResultsDirContextProcessor(int pageSize, PagedResultsCookie cookie) {
        this.pageSize = pageSize;
        this.cookie = cookie;
        this.defaultRequestControl = DEFAULT_REQUEST_CONTROL;
        this.defaultResponseControl = DEFAULT_RESPONSE_CONTROL;
        this.fallbackRequestControl = FALLBACK_REQUEST_CONTROL;
        this.fallbackResponseControl = FALLBACK_RESPONSE_CONTROL;
        this.loadControlClasses();
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

    @Override
    public Control createRequestControl() {
        byte[] actualCookie = null;
        if (this.cookie != null) {
            actualCookie = this.cookie.getCookie();
        }
        return super.createRequestControl(new Class[]{Integer.TYPE, byte[].class, Boolean.TYPE}, new Object[]{this.pageSize, actualCookie, this.critical});
    }

    public boolean hasMore() {
        return this.more;
    }

    @Override
    protected void handleResponse(Object control) {
        byte[] result = (byte[])this.invokeMethod("getCookie", this.responseControlClass, control);
        if (result == null) {
            this.more = false;
        }
        this.cookie = new PagedResultsCookie(result);
        this.resultSize = (Integer)this.invokeMethod("getResultSize", this.responseControlClass, control);
    }
}

