/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.control;

import javax.naming.ldap.Control;
import org.springframework.ldap.control.AbstractFallbackRequestAndResponseControlDirContextProcessor;

public class SortControlDirContextProcessor
extends AbstractFallbackRequestAndResponseControlDirContextProcessor {
    private static final String DEFAULT_REQUEST_CONTROL = "javax.naming.ldap.SortControl";
    private static final String FALLBACK_REQUEST_CONTROL = "com.sun.jndi.ldap.ctl.SortControl";
    private static final String DEFAULT_RESPONSE_CONTROL = "javax.naming.ldap.SortResponseControl";
    private static final String FALLBACK_RESPONSE_CONTROL = "com.sun.jndi.ldap.ctl.SortResponseControl";
    String sortKey;
    private boolean sorted;
    private int resultCode;

    public SortControlDirContextProcessor(String sortKey) {
        this.sortKey = sortKey;
        this.sorted = false;
        this.resultCode = -1;
        this.defaultRequestControl = DEFAULT_REQUEST_CONTROL;
        this.defaultResponseControl = DEFAULT_RESPONSE_CONTROL;
        this.fallbackRequestControl = FALLBACK_REQUEST_CONTROL;
        this.fallbackResponseControl = FALLBACK_RESPONSE_CONTROL;
        this.loadControlClasses();
    }

    public boolean isSorted() {
        return this.sorted;
    }

    public int getResultCode() {
        return this.resultCode;
    }

    public String getSortKey() {
        return this.sortKey;
    }

    @Override
    public Control createRequestControl() {
        return super.createRequestControl(new Class[]{String[].class, Boolean.TYPE}, new Object[]{new String[]{this.sortKey}, this.critical});
    }

    @Override
    protected void handleResponse(Object control) {
        this.sorted = (Boolean)this.invokeMethod("isSorted", this.responseControlClass, control);
        this.resultCode = (Integer)this.invokeMethod("getResultCode", this.responseControlClass, control);
    }
}

