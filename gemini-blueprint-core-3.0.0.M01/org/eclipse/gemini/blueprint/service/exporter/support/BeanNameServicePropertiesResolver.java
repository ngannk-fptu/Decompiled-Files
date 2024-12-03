/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.service.exporter.support;

import java.util.Map;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.service.exporter.OsgiServicePropertiesResolver;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.eclipse.gemini.blueprint.util.internal.MapBasedDictionary;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class BeanNameServicePropertiesResolver
implements OsgiServicePropertiesResolver,
BundleContextAware,
InitializingBean {
    private BundleContext bundleContext;

    @Override
    public Map getServiceProperties(String beanName) {
        String version;
        String name;
        MapBasedDictionary<String, String> p = new MapBasedDictionary<String, String>();
        if (StringUtils.hasText((String)beanName)) {
            p.put("org.eclipse.gemini.blueprint.bean.name", beanName);
            p.put("org.springframework.osgi.bean.name", beanName);
            p.put("osgi.service.blueprint.compname", beanName);
        }
        if (StringUtils.hasLength((String)(name = this.getSymbolicName()))) {
            p.put("Bundle-SymbolicName", name);
        }
        if (StringUtils.hasLength((String)(version = this.getBundleVersion()))) {
            p.put("Bundle-Version", version);
        }
        return p;
    }

    private String getBundleVersion() {
        return OsgiBundleUtils.getBundleVersion(this.bundleContext.getBundle()).toString();
    }

    private String getSymbolicName() {
        return this.bundleContext.getBundle().getSymbolicName();
    }

    @Override
    public void setBundleContext(BundleContext context) {
        this.bundleContext = context;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull((Object)this.bundleContext, (String)"required property bundleContext has not been set");
    }
}

