/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextException
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.web.servlet.handler;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

public abstract class AbstractDetectingUrlHandlerMapping
extends AbstractUrlHandlerMapping {
    private boolean detectHandlersInAncestorContexts = false;

    public void setDetectHandlersInAncestorContexts(boolean detectHandlersInAncestorContexts) {
        this.detectHandlersInAncestorContexts = detectHandlersInAncestorContexts;
    }

    @Override
    public void initApplicationContext() throws ApplicationContextException {
        super.initApplicationContext();
        this.detectHandlers();
    }

    protected void detectHandlers() throws BeansException {
        String[] beanNames;
        ApplicationContext applicationContext = this.obtainApplicationContext();
        for (String beanName : beanNames = this.detectHandlersInAncestorContexts ? BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)applicationContext, Object.class) : applicationContext.getBeanNamesForType(Object.class)) {
            Object[] urls = this.determineUrlsForHandler(beanName);
            if (ObjectUtils.isEmpty((Object[])urls)) continue;
            this.registerHandler((String[])urls, beanName);
        }
        if (this.mappingsLogger.isDebugEnabled()) {
            this.mappingsLogger.debug((Object)(this.formatMappingName() + " " + this.getHandlerMap()));
        } else if (this.logger.isDebugEnabled() && !this.getHandlerMap().isEmpty() || this.logger.isTraceEnabled()) {
            this.logger.debug((Object)("Detected " + this.getHandlerMap().size() + " mappings in " + this.formatMappingName()));
        }
    }

    protected abstract String[] determineUrlsForHandler(String var1);
}

