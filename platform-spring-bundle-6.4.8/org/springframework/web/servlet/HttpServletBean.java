/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.servlet;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.context.support.StandardServletEnvironment;

public abstract class HttpServletBean
extends HttpServlet
implements EnvironmentCapable,
EnvironmentAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private ConfigurableEnvironment environment;
    private final Set<String> requiredProperties = new HashSet<String>(4);

    protected final void addRequiredProperty(String property) {
        this.requiredProperties.add(property);
    }

    @Override
    public void setEnvironment(Environment environment2) {
        Assert.isInstanceOf(ConfigurableEnvironment.class, (Object)environment2, "ConfigurableEnvironment required");
        this.environment = (ConfigurableEnvironment)environment2;
    }

    @Override
    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = this.createEnvironment();
        }
        return this.environment;
    }

    protected ConfigurableEnvironment createEnvironment() {
        return new StandardServletEnvironment();
    }

    public final void init() throws ServletException {
        ServletConfigPropertyValues pvs = new ServletConfigPropertyValues(this.getServletConfig(), this.requiredProperties);
        if (!pvs.isEmpty()) {
            try {
                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
                ServletContextResourceLoader resourceLoader = new ServletContextResourceLoader(this.getServletContext());
                bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, this.getEnvironment()));
                this.initBeanWrapper(bw);
                bw.setPropertyValues(pvs, true);
            }
            catch (BeansException ex) {
                if (this.logger.isErrorEnabled()) {
                    this.logger.error((Object)("Failed to set bean properties on servlet '" + this.getServletName() + "'"), (Throwable)ex);
                }
                throw ex;
            }
        }
        this.initServletBean();
    }

    protected void initBeanWrapper(BeanWrapper bw) throws BeansException {
    }

    protected void initServletBean() throws ServletException {
    }

    @Nullable
    public String getServletName() {
        return this.getServletConfig() != null ? this.getServletConfig().getServletName() : null;
    }

    private static class ServletConfigPropertyValues
    extends MutablePropertyValues {
        public ServletConfigPropertyValues(ServletConfig config, Set<String> requiredProperties) throws ServletException {
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
                throw new ServletException("Initialization from ServletConfig for servlet '" + config.getServletName() + "' failed; the following required properties were missing: " + StringUtils.collectionToDelimitedString(missingProps, ", "));
            }
        }
    }
}

