/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.context.ApplicationContext
 *  org.springframework.core.Ordered
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.PatternMatchUtils
 */
package org.springframework.web.servlet.view;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;

public class UrlBasedViewResolver
extends AbstractCachingViewResolver
implements Ordered {
    public static final String REDIRECT_URL_PREFIX = "redirect:";
    public static final String FORWARD_URL_PREFIX = "forward:";
    @Nullable
    private Class<?> viewClass;
    private String prefix = "";
    private String suffix = "";
    @Nullable
    private String contentType;
    private boolean redirectContextRelative = true;
    private boolean redirectHttp10Compatible = true;
    @Nullable
    private String[] redirectHosts;
    @Nullable
    private String requestContextAttribute;
    private final Map<String, Object> staticAttributes = new HashMap<String, Object>();
    @Nullable
    private Boolean exposePathVariables;
    @Nullable
    private Boolean exposeContextBeansAsAttributes;
    @Nullable
    private String[] exposedContextBeanNames;
    @Nullable
    private String[] viewNames;
    private int order = Integer.MAX_VALUE;

    public void setViewClass(@Nullable Class<?> viewClass) {
        if (viewClass != null && !this.requiredViewClass().isAssignableFrom(viewClass)) {
            throw new IllegalArgumentException("Given view class [" + viewClass.getName() + "] is not of type [" + this.requiredViewClass().getName() + "]");
        }
        this.viewClass = viewClass;
    }

    @Nullable
    protected Class<?> getViewClass() {
        return this.viewClass;
    }

    public void setPrefix(@Nullable String prefix) {
        this.prefix = prefix != null ? prefix : "";
    }

    protected String getPrefix() {
        return this.prefix;
    }

    public void setSuffix(@Nullable String suffix) {
        this.suffix = suffix != null ? suffix : "";
    }

    protected String getSuffix() {
        return this.suffix;
    }

    public void setContentType(@Nullable String contentType) {
        this.contentType = contentType;
    }

    @Nullable
    protected String getContentType() {
        return this.contentType;
    }

    public void setRedirectContextRelative(boolean redirectContextRelative) {
        this.redirectContextRelative = redirectContextRelative;
    }

    protected boolean isRedirectContextRelative() {
        return this.redirectContextRelative;
    }

    public void setRedirectHttp10Compatible(boolean redirectHttp10Compatible) {
        this.redirectHttp10Compatible = redirectHttp10Compatible;
    }

    protected boolean isRedirectHttp10Compatible() {
        return this.redirectHttp10Compatible;
    }

    public void setRedirectHosts(String ... redirectHosts) {
        this.redirectHosts = redirectHosts;
    }

    @Nullable
    public String[] getRedirectHosts() {
        return this.redirectHosts;
    }

    public void setRequestContextAttribute(@Nullable String requestContextAttribute) {
        this.requestContextAttribute = requestContextAttribute;
    }

    @Nullable
    protected String getRequestContextAttribute() {
        return this.requestContextAttribute;
    }

    public void setAttributes(Properties props) {
        CollectionUtils.mergePropertiesIntoMap((Properties)props, this.staticAttributes);
    }

    public void setAttributesMap(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            this.staticAttributes.putAll(attributes);
        }
    }

    public Map<String, Object> getAttributesMap() {
        return this.staticAttributes;
    }

    public void setExposePathVariables(@Nullable Boolean exposePathVariables) {
        this.exposePathVariables = exposePathVariables;
    }

    @Nullable
    protected Boolean getExposePathVariables() {
        return this.exposePathVariables;
    }

    public void setExposeContextBeansAsAttributes(boolean exposeContextBeansAsAttributes) {
        this.exposeContextBeansAsAttributes = exposeContextBeansAsAttributes;
    }

    @Nullable
    protected Boolean getExposeContextBeansAsAttributes() {
        return this.exposeContextBeansAsAttributes;
    }

    public void setExposedContextBeanNames(String ... exposedContextBeanNames) {
        this.exposedContextBeanNames = exposedContextBeanNames;
    }

    @Nullable
    protected String[] getExposedContextBeanNames() {
        return this.exposedContextBeanNames;
    }

    public void setViewNames(String ... viewNames) {
        this.viewNames = viewNames;
    }

    @Nullable
    protected String[] getViewNames() {
        return this.viewNames;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    protected void initApplicationContext() {
        super.initApplicationContext();
        if (this.getViewClass() == null) {
            throw new IllegalArgumentException("Property 'viewClass' is required");
        }
    }

    @Override
    protected Object getCacheKey(String viewName, Locale locale) {
        return viewName;
    }

    @Override
    protected View createView(String viewName, Locale locale) throws Exception {
        if (!this.canHandle(viewName, locale)) {
            return null;
        }
        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
            RedirectView view = new RedirectView(redirectUrl, this.isRedirectContextRelative(), this.isRedirectHttp10Compatible());
            String[] hosts = this.getRedirectHosts();
            if (hosts != null) {
                view.setHosts(hosts);
            }
            return this.applyLifecycleMethods(REDIRECT_URL_PREFIX, view);
        }
        if (viewName.startsWith(FORWARD_URL_PREFIX)) {
            String forwardUrl = viewName.substring(FORWARD_URL_PREFIX.length());
            InternalResourceView view = new InternalResourceView(forwardUrl);
            return this.applyLifecycleMethods(FORWARD_URL_PREFIX, view);
        }
        return super.createView(viewName, locale);
    }

    protected boolean canHandle(String viewName, Locale locale) {
        String[] viewNames = this.getViewNames();
        return viewNames == null || PatternMatchUtils.simpleMatch((String[])viewNames, (String)viewName);
    }

    protected Class<?> requiredViewClass() {
        return AbstractUrlBasedView.class;
    }

    protected AbstractUrlBasedView instantiateView() {
        Class<?> viewClass = this.getViewClass();
        Assert.state((viewClass != null ? 1 : 0) != 0, (String)"No view class");
        return (AbstractUrlBasedView)BeanUtils.instantiateClass(viewClass);
    }

    @Override
    protected View loadView(String viewName, Locale locale) throws Exception {
        AbstractUrlBasedView view = this.buildView(viewName);
        View result = this.applyLifecycleMethods(viewName, view);
        return view.checkResource(locale) ? result : null;
    }

    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        String[] exposedContextBeanNames;
        Boolean exposeContextBeansAsAttributes;
        Boolean exposePathVariables;
        String requestContextAttribute;
        AbstractUrlBasedView view = this.instantiateView();
        view.setUrl(this.getPrefix() + viewName + this.getSuffix());
        view.setAttributesMap(this.getAttributesMap());
        String contentType = this.getContentType();
        if (contentType != null) {
            view.setContentType(contentType);
        }
        if ((requestContextAttribute = this.getRequestContextAttribute()) != null) {
            view.setRequestContextAttribute(requestContextAttribute);
        }
        if ((exposePathVariables = this.getExposePathVariables()) != null) {
            view.setExposePathVariables(exposePathVariables);
        }
        if ((exposeContextBeansAsAttributes = this.getExposeContextBeansAsAttributes()) != null) {
            view.setExposeContextBeansAsAttributes(exposeContextBeansAsAttributes);
        }
        if ((exposedContextBeanNames = this.getExposedContextBeanNames()) != null) {
            view.setExposedContextBeanNames(exposedContextBeanNames);
        }
        return view;
    }

    protected View applyLifecycleMethods(String viewName, AbstractUrlBasedView view) {
        Object initialized;
        ApplicationContext context = this.getApplicationContext();
        if (context != null && (initialized = context.getAutowireCapableBeanFactory().initializeBean((Object)view, viewName)) instanceof View) {
            return (View)initialized;
        }
        return view;
    }
}

