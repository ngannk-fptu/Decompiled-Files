/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.web.context.support;

import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public abstract class SpringBeanAutowiringSupport {
    private static final Log logger = LogFactory.getLog(SpringBeanAutowiringSupport.class);

    public SpringBeanAutowiringSupport() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    public static void processInjectionBasedOnCurrentContext(Object target) {
        Assert.notNull((Object)target, (String)"Target object must not be null");
        WebApplicationContext cc = ContextLoader.getCurrentWebApplicationContext();
        if (cc != null) {
            AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
            bpp.setBeanFactory((BeanFactory)cc.getAutowireCapableBeanFactory());
            bpp.processInjection(target);
        } else if (logger.isWarnEnabled()) {
            logger.warn((Object)("Current WebApplicationContext is not available for processing of " + ClassUtils.getShortName(target.getClass()) + ": Make sure this class gets constructed in a Spring web application after the Spring WebApplicationContext has been initialized. Proceeding without injection."));
        }
    }

    public static void processInjectionBasedOnServletContext(Object target, ServletContext servletContext) {
        Assert.notNull((Object)target, (String)"Target object must not be null");
        WebApplicationContext cc = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory((BeanFactory)cc.getAutowireCapableBeanFactory());
        bpp.processInjection(target);
    }
}

