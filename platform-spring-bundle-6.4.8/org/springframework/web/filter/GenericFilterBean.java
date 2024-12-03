/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.filter;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.context.support.StandardServletEnvironment;
import org.springframework.web.util.NestedServletException;

public abstract class GenericFilterBean
implements Filter,
BeanNameAware,
EnvironmentAware,
EnvironmentCapable,
ServletContextAware,
InitializingBean,
DisposableBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private String beanName;
    @Nullable
    private Environment environment;
    @Nullable
    private ServletContext servletContext;
    @Nullable
    private FilterConfig filterConfig;
    private final Set<String> requiredProperties = new HashSet<String>(4);

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void setEnvironment(Environment environment2) {
        this.environment = environment2;
    }

    @Override
    public Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = this.createEnvironment();
        }
        return this.environment;
    }

    protected Environment createEnvironment() {
        return new StandardServletEnvironment();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        this.initFilterBean();
    }

    @Override
    public void destroy() {
    }

    protected final void addRequiredProperty(String property) {
        this.requiredProperties.add(property);
    }

    public final void init(FilterConfig filterConfig) throws ServletException {
        Assert.notNull((Object)filterConfig, "FilterConfig must not be null");
        this.filterConfig = filterConfig;
        FilterConfigPropertyValues pvs = new FilterConfigPropertyValues(filterConfig, this.requiredProperties);
        if (!pvs.isEmpty()) {
            try {
                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
                ServletContextResourceLoader resourceLoader = new ServletContextResourceLoader(filterConfig.getServletContext());
                Environment env = this.environment;
                if (env == null) {
                    env = new StandardServletEnvironment();
                }
                bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, env));
                this.initBeanWrapper(bw);
                bw.setPropertyValues(pvs, true);
            }
            catch (BeansException ex) {
                String msg = "Failed to set bean properties on filter '" + filterConfig.getFilterName() + "': " + ex.getMessage();
                this.logger.error((Object)msg, (Throwable)ex);
                throw new NestedServletException(msg, ex);
            }
        }
        this.initFilterBean();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Filter '" + filterConfig.getFilterName() + "' configured for use"));
        }
    }

    protected void initBeanWrapper(BeanWrapper bw) throws BeansException {
    }

    protected void initFilterBean() throws ServletException {
    }

    @Nullable
    public FilterConfig getFilterConfig() {
        return this.filterConfig;
    }

    @Nullable
    protected String getFilterName() {
        return this.filterConfig != null ? this.filterConfig.getFilterName() : this.beanName;
    }

    protected ServletContext getServletContext() {
        if (this.filterConfig != null) {
            return this.filterConfig.getServletContext();
        }
        if (this.servletContext != null) {
            return this.servletContext;
        }
        throw new IllegalStateException("No ServletContext");
    }

    private static class FilterConfigPropertyValues
    extends MutablePropertyValues {
        public FilterConfigPropertyValues(FilterConfig config, Set<String> requiredProperties) throws ServletException {
            HashSet<String> missingProps = !CollectionUtils.isEmpty(requiredProperties) ? new HashSet<String>(requiredProperties) : null;
            Enumeration paramNames = config.getInitParameterNames();
            while (paramNames.hasMoreElements()) {
                String property = (String)paramNames.nextElement();
                String value = config.getInitParameter(property);
                this.addPropertyValue(new PropertyValue(property, (Object)value));
                if (missingProps == null) continue;
                missingProps.remove(property);
            }
            if (!CollectionUtils.isEmpty(missingProps)) {
                throw new ServletException("Initialization from FilterConfig for filter '" + config.getFilterName() + "' failed; the following required properties were missing: " + StringUtils.collectionToDelimitedString(missingProps, ", "));
            }
        }
    }
}

