/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext
 *  org.osgi.framework.BundleContext
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.extender.support;

import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.extender.OsgiApplicationContextCreator;
import org.eclipse.gemini.blueprint.extender.support.DefaultOsgiApplicationContextCreator;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class ConditionalApplicationContextCreator
implements OsgiApplicationContextCreator,
InitializingBean {
    private BundleContextFilter filter;
    private OsgiApplicationContextCreator delegatedContextCreator;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull((Object)this.filter, (String)"filter property is required");
        if (this.delegatedContextCreator == null) {
            this.delegatedContextCreator = new DefaultOsgiApplicationContextCreator();
        }
    }

    @Override
    public DelegatedExecutionOsgiBundleApplicationContext createApplicationContext(BundleContext bundleContext) throws Exception {
        if (this.filter.matches(bundleContext)) {
            return this.delegatedContextCreator.createApplicationContext(bundleContext);
        }
        return null;
    }

    public void setFilter(BundleContextFilter filter) {
        this.filter = filter;
    }

    public void setDelegatedApplicationContextCreator(OsgiApplicationContextCreator delegatedContextCreator) {
        this.delegatedContextCreator = delegatedContextCreator;
    }

    public static interface BundleContextFilter {
        public boolean matches(BundleContext var1);
    }
}

