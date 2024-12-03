/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.annotation.component.Component
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.compat.struts2.actioncontext;

import aQute.bnd.annotation.component.Component;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.compat.struts2.actioncontext.ActionContextCompat;
import com.atlassian.confluence.compat.struts2.actioncontext.ActionContextStruts2AndWWCompat;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ActionContextCompatManager
implements ActionContextCompat {
    private static final Logger log = LoggerFactory.getLogger(ActionContextCompatManager.class);
    private final Supplier<ActionContextCompat> delegate = Suppliers.memoize(() -> this.initialiseActionContextCompat(classLoader));

    public ActionContextCompatManager() {
        this(ActionContextCompatManager.class.getClassLoader());
    }

    public ActionContextCompatManager(ClassLoader classLoader) {
    }

    private ActionContextCompat initialiseActionContextCompat(ClassLoader classLoader) {
        ActionContextStruts2AndWWCompat internalDelegate;
        try {
            Class.forName("com.opensymphony.xwork2.ActionContext", false, classLoader);
            internalDelegate = new ActionContextStruts2AndWWCompat("com.opensymphony.xwork2.ActionContext", classLoader);
        }
        catch (ClassNotFoundException e) {
            log.debug("Could not find struts2 ActionContext, falling back to webwork ActionContext", (Throwable)e);
            try {
                internalDelegate = new ActionContextStruts2AndWWCompat("com.opensymphony.xwork.ActionContext", classLoader);
            }
            catch (ReflectiveOperationException ex) {
                throw new ServiceException("ActionContext couldn't be initialized.", (Throwable)ex);
            }
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("ActionContext couldn't be initialized.", (Throwable)e);
        }
        return internalDelegate;
    }

    @Override
    public void setApplication(Map application) {
        try {
            ((ActionContextCompat)this.delegate.get()).setApplication(application);
        }
        catch (NullPointerException ex) {
            log.error("Couldn't set the Application");
        }
    }

    @Override
    public Map getApplication() {
        try {
            return ((ActionContextCompat)this.delegate.get()).getApplication();
        }
        catch (NullPointerException ex) {
            log.error("Couldn't get the Application");
            return null;
        }
    }

    @Override
    public void setContextMap(Map contextMap) {
        try {
            ((ActionContextCompat)this.delegate.get()).setContextMap(contextMap);
        }
        catch (NullPointerException ex) {
            log.error("Couldn't set the ContextMap");
        }
    }

    @Override
    public Map getContextMap() {
        try {
            return ((ActionContextCompat)this.delegate.get()).getContextMap();
        }
        catch (NullPointerException ex) {
            log.error("Couldn't get the ContextMap");
            return null;
        }
    }

    @Override
    public void setConversionErrors(Map conversionErrors) {
        try {
            ((ActionContextCompat)this.delegate.get()).setConversionErrors(conversionErrors);
        }
        catch (NullPointerException ex) {
            log.error("Couldn't set the ConversionErrors");
        }
    }

    @Override
    public Map getConversionErrors() {
        try {
            return ((ActionContextCompat)this.delegate.get()).getConversionErrors();
        }
        catch (NullPointerException ex) {
            log.error("Couldn't get the ConversionErrors");
            return null;
        }
    }

    @Override
    public void setLocale(Locale locale) {
        try {
            ((ActionContextCompat)this.delegate.get()).setLocale(locale);
        }
        catch (NullPointerException ex) {
            log.error("Couldn't set the Locale");
        }
    }

    @Override
    public Locale getLocale() {
        try {
            return ((ActionContextCompat)this.delegate.get()).getLocale();
        }
        catch (NullPointerException ex) {
            log.error("Couldn't get the Locale");
            return null;
        }
    }

    @Override
    public void setName(String name) {
        try {
            ((ActionContextCompat)this.delegate.get()).setName(name);
        }
        catch (NullPointerException ex) {
            log.error("Couldn't get the Naame");
        }
    }

    @Override
    public String getName() {
        try {
            return ((ActionContextCompat)this.delegate.get()).getName();
        }
        catch (NullPointerException ex) {
            log.error("Couldn't get the Name");
            return null;
        }
    }

    @Override
    public void setParameters(Map parameters) {
        try {
            ((ActionContextCompat)this.delegate.get()).setParameters(parameters);
        }
        catch (NullPointerException ex) {
            log.error("Couldn't set the Parameters");
        }
    }

    @Override
    public Map getParameters() {
        try {
            return ((ActionContextCompat)this.delegate.get()).getParameters();
        }
        catch (NullPointerException ex) {
            log.error("Couldn't get the Parameters");
            return null;
        }
    }

    @Override
    public void setSession(Map session) {
        try {
            ((ActionContextCompat)this.delegate.get()).setSession(session);
        }
        catch (NullPointerException ex) {
            log.error("Couldn't set the Session");
        }
    }

    @Override
    public Map getSession() {
        try {
            return ((ActionContextCompat)this.delegate.get()).getSession();
        }
        catch (NullPointerException ex) {
            log.error("Couldn't get the Session");
            return null;
        }
    }

    @Override
    public Object get(Object key) {
        try {
            return ((ActionContextCompat)this.delegate.get()).get(key);
        }
        catch (NullPointerException ex) {
            log.error("Couldn't get the value");
            return null;
        }
    }

    @Override
    public void put(Object key, Object value) {
        try {
            ((ActionContextCompat)this.delegate.get()).put(key, value);
        }
        catch (NullPointerException ex) {
            log.error("Couldn't pet the value");
        }
    }
}

