/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.PageContext
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.conversion.impl.ConversionData;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

public class ActionContext
implements Serializable {
    private static final ThreadLocal<ActionContext> actionContext = new ThreadLocal();
    private static final String ACTION_NAME = "org.apache.struts2.ActionContext.name";
    private static final String VALUE_STACK = "com.opensymphony.xwork2.util.ValueStack.ValueStack";
    private static final String SESSION = "org.apache.struts2.ActionContext.session";
    private static final String APPLICATION = "org.apache.struts2.ActionContext.application";
    private static final String PARAMETERS = "org.apache.struts2.ActionContext.parameters";
    private static final String LOCALE = "org.apache.struts2.ActionContext.locale";
    private static final String ACTION_INVOCATION = "org.apache.struts2.ActionContext.actionInvocation";
    private static final String CONVERSION_ERRORS = "org.apache.struts2.ActionContext.conversionErrors";
    private static final String CONTAINER = "org.apache.struts2.ActionContext.container";
    private final Map<String, Object> context;

    protected ActionContext(Map<String, Object> context) {
        this.context = context;
    }

    public static ActionContext of(Map<String, Object> context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null!");
        }
        return new ActionContext(context);
    }

    public static ActionContext of() {
        return ActionContext.of(new HashMap<String, Object>());
    }

    public static ActionContext bind(ActionContext actionContext) {
        ActionContext.setContext(actionContext);
        return ActionContext.getContext();
    }

    public static boolean containsValueStack(Map<String, Object> context) {
        return context != null && context.containsKey(VALUE_STACK);
    }

    public ActionContext bind() {
        ActionContext.setContext(this);
        return ActionContext.getContext();
    }

    public static void clear() {
        actionContext.remove();
    }

    private static void setContext(ActionContext context) {
        actionContext.set(context);
    }

    public static ActionContext getContext() {
        return actionContext.get();
    }

    public ActionContext withActionInvocation(ActionInvocation actionInvocation) {
        this.put(ACTION_INVOCATION, actionInvocation);
        return this;
    }

    public ActionInvocation getActionInvocation() {
        return (ActionInvocation)this.get(ACTION_INVOCATION);
    }

    public ActionContext withApplication(Map<String, Object> application) {
        this.put(APPLICATION, application);
        return this;
    }

    public Map<String, Object> getApplication() {
        return (Map)this.get(APPLICATION);
    }

    public Map<String, Object> getContextMap() {
        return this.context;
    }

    public ActionContext withConversionErrors(Map<String, ConversionData> conversionErrors) {
        this.put(CONVERSION_ERRORS, conversionErrors);
        return this;
    }

    public Map<String, ConversionData> getConversionErrors() {
        Map<String, ConversionData> errors = (Map<String, ConversionData>)this.get(CONVERSION_ERRORS);
        if (errors == null) {
            errors = this.withConversionErrors(new HashMap<String, ConversionData>()).getConversionErrors();
        }
        return errors;
    }

    public ActionContext withLocale(Locale locale) {
        this.put(LOCALE, locale);
        return this;
    }

    public Locale getLocale() {
        Locale locale = (Locale)this.get(LOCALE);
        if (locale == null) {
            locale = Locale.getDefault();
            this.withLocale(locale);
        }
        return locale;
    }

    public ActionContext withActionName(String actionName) {
        this.put(ACTION_NAME, actionName);
        return this;
    }

    public String getActionName() {
        return (String)this.get(ACTION_NAME);
    }

    public ActionContext withParameters(HttpParameters parameters) {
        this.put(PARAMETERS, parameters);
        return this;
    }

    public HttpParameters getParameters() {
        return (HttpParameters)this.get(PARAMETERS);
    }

    public ActionContext withSession(Map<String, Object> session) {
        this.put(SESSION, session);
        return this;
    }

    public Map<String, Object> getSession() {
        return (Map)this.get(SESSION);
    }

    public ActionContext withValueStack(ValueStack valueStack) {
        this.put(VALUE_STACK, valueStack);
        return this;
    }

    public ValueStack getValueStack() {
        return (ValueStack)this.get(VALUE_STACK);
    }

    public ActionContext withContainer(Container container) {
        this.put(CONTAINER, container);
        return this;
    }

    public Container getContainer() {
        return (Container)this.get(CONTAINER);
    }

    public <T> T getInstance(Class<T> type) {
        Container cont = this.getContainer();
        if (cont != null) {
            return cont.getInstance(type);
        }
        throw new StrutsException("Cannot find an initialized container for this request.");
    }

    public Object get(String key) {
        return this.context.get(key);
    }

    public void put(String key, Object value) {
        this.context.put(key, value);
    }

    public ServletContext getServletContext() {
        return (ServletContext)this.get("com.opensymphony.xwork2.dispatcher.ServletContext");
    }

    public ActionContext withServletContext(ServletContext servletContext) {
        this.put("com.opensymphony.xwork2.dispatcher.ServletContext", servletContext);
        return this;
    }

    public HttpServletRequest getServletRequest() {
        return (HttpServletRequest)this.get("com.opensymphony.xwork2.dispatcher.HttpServletRequest");
    }

    public ActionContext withServletRequest(HttpServletRequest request) {
        this.put("com.opensymphony.xwork2.dispatcher.HttpServletRequest", request);
        return this;
    }

    public HttpServletResponse getServletResponse() {
        return (HttpServletResponse)this.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse");
    }

    public ActionContext withServletResponse(HttpServletResponse response) {
        this.put("com.opensymphony.xwork2.dispatcher.HttpServletResponse", response);
        return this;
    }

    public PageContext getPageContext() {
        return (PageContext)this.get("com.opensymphony.xwork2.dispatcher.PageContext");
    }

    public ActionContext withPageContext(PageContext pageContext) {
        this.put("com.opensymphony.xwork2.dispatcher.PageContext", pageContext);
        return this;
    }

    public ActionMapping getActionMapping() {
        return (ActionMapping)this.get("struts.actionMapping");
    }

    public ActionContext withActionMapping(ActionMapping actionMapping) {
        this.put("struts.actionMapping", actionMapping);
        return this;
    }

    public ActionContext withExtraContext(Map<String, Object> extraContext) {
        if (extraContext != null) {
            this.context.putAll(extraContext);
        }
        return this;
    }

    public ActionContext with(String key, Object value) {
        this.put(key, value);
        return this;
    }
}

