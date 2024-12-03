/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.config.BeanPostProcessor
 */
package com.atlassian.templaterenderer.velocity;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.templaterenderer.velocity.AbstractCachingWebPanelRenderer;
import java.util.IdentityHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class CachingWebPanelRendererTracker
implements BeanPostProcessor,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(CachingWebPanelRendererTracker.class);
    private static final Object identityMapValue = new Object();
    @TenantAware(value=TenancyScope.TENANTLESS)
    private final Map<AbstractCachingWebPanelRenderer, Object> tracked = new IdentityHashMap<AbstractCachingWebPanelRenderer, Object>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AbstractCachingWebPanelRenderer) {
            log.debug("Tracking a WebPanelRenderer {}", bean);
            Map<AbstractCachingWebPanelRenderer, Object> map = this.tracked;
            synchronized (map) {
                this.tracked.put((AbstractCachingWebPanelRenderer)bean, identityMapValue);
            }
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroy() throws Exception {
        Map<AbstractCachingWebPanelRenderer, Object> map = this.tracked;
        synchronized (map) {
            for (AbstractCachingWebPanelRenderer render : this.tracked.keySet()) {
                this.destroy(render);
            }
            this.tracked.clear();
        }
    }

    private void destroy(AbstractCachingWebPanelRenderer render) {
        try {
            render.destroy();
        }
        catch (Exception e) {
            log.warn("Exception trying to destroy " + render, (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int numberOfTracked() {
        Map<AbstractCachingWebPanelRenderer, Object> map = this.tracked;
        synchronized (map) {
            return this.tracked.size();
        }
    }
}

