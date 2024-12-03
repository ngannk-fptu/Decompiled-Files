/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELResolver
 *  javax.el.FunctionMapper
 *  javax.el.VariableMapper
 */
package org.apache.sling.scripting.jsp.jasper.el;

import java.util.Locale;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

public final class ELContextWrapper
extends ELContext {
    private final ELContext target;
    private final FunctionMapper fnMapper;

    public ELContextWrapper(ELContext target, FunctionMapper fnMapper) {
        this.target = target;
        this.fnMapper = fnMapper;
    }

    public ELResolver getELResolver() {
        return this.target.getELResolver();
    }

    public FunctionMapper getFunctionMapper() {
        if (this.fnMapper != null) {
            return this.fnMapper;
        }
        return this.target.getFunctionMapper();
    }

    public VariableMapper getVariableMapper() {
        return this.target.getVariableMapper();
    }

    public Object getContext(Class key) {
        return this.target.getContext(key);
    }

    public Locale getLocale() {
        return this.target.getLocale();
    }

    public boolean isPropertyResolved() {
        return this.target.isPropertyResolved();
    }

    public void putContext(Class key, Object contextObject) throws NullPointerException {
        this.target.putContext(key, contextObject);
    }

    public void setLocale(Locale locale) {
        this.target.setLocale(locale);
    }

    public void setPropertyResolved(boolean resolved) {
        this.target.setPropertyResolved(resolved);
    }
}

