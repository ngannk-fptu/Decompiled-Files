/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleException
 *  org.osgi.framework.ServiceReference
 *  org.osgi.service.startlevel.StartLevel
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.ResourceLoaderAware
 *  org.springframework.core.io.DefaultResourceLoader
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.bundle;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.bundle.BundleActionEnum;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.startlevel.StartLevel;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class BundleFactoryBean
implements FactoryBean<Bundle>,
BundleContextAware,
InitializingBean,
DisposableBean,
ResourceLoaderAware {
    private static Log log = LogFactory.getLog(BundleFactoryBean.class);
    private String location;
    private Resource resource;
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    private String symbolicName;
    private Bundle bundle;
    private BundleContext bundleContext;
    private BundleActionEnum action;
    private BundleActionEnum destroyAction;
    private int startLevel;
    private ClassLoader classLoader;
    private boolean pushBundleAsContextClassLoader = false;

    public Class<? extends Bundle> getObjectType() {
        return this.bundle != null ? this.bundle.getClass() : Bundle.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public Bundle getObject() throws Exception {
        return this.bundle;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull((Object)this.bundleContext, (String)"BundleContext is required");
        if (this.bundle == null && !StringUtils.hasText((String)this.symbolicName) && !StringUtils.hasText((String)this.location)) {
            throw new IllegalArgumentException("at least one of symbolicName, location, bundle properties is required ");
        }
        if (this.getLocation() != null) {
            this.resource = this.resourceLoader.getResource(this.getLocation());
        }
        if (this.bundle == null) {
            this.bundle = this.findBundle();
        }
        this.updateStartLevel(this.getStartLevel());
        if (log.isDebugEnabled()) {
            log.debug((Object)("working with bundle[" + OsgiStringUtils.nullSafeNameAndSymName(this.bundle)));
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("executing start-up action " + (Object)((Object)this.action)));
        }
        if (this.action != null) {
            this.executeAction(this.action);
        }
    }

    public void destroy() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)("executing shutdown action " + (Object)((Object)this.action)));
        }
        if (this.destroyAction != null) {
            this.executeAction(this.destroyAction);
        }
        this.bundle = null;
        this.classLoader = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void executeAction(BundleActionEnum action) {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        try {
            if (this.pushBundleAsContextClassLoader) {
                Thread.currentThread().setContextClassLoader(this.classLoader);
            }
            try {
                switch (action) {
                    case INSTALL: {
                        this.bundle = this.installBundle();
                        break;
                    }
                    case START: {
                        if (this.bundle == null) {
                            this.bundle = this.installBundle();
                        }
                        this.bundle.start();
                        break;
                    }
                    case UPDATE: {
                        if (this.bundle == null) {
                            this.bundle = this.installBundle();
                        }
                        this.bundle.update();
                        break;
                    }
                    case STOP: {
                        if (this.bundle == null) break;
                        this.bundle.stop();
                        break;
                    }
                    case UNINSTALL: {
                        if (this.bundle == null) break;
                        this.bundle.uninstall();
                        break;
                    }
                }
            }
            catch (BundleException be) {
                throw (RuntimeException)new IllegalStateException("cannot execute action " + action.name() + " on bundle " + OsgiStringUtils.nullSafeNameAndSymName(this.bundle)).initCause(be);
            }
        }
        finally {
            if (this.pushBundleAsContextClassLoader) {
                Thread.currentThread().setContextClassLoader(ccl);
            }
        }
    }

    private Bundle installBundle() throws BundleException {
        boolean installBasedOnLocation;
        Assert.hasText((String)this.location, (String)"location parameter required when installing a bundle");
        log.info((Object)("Loading bundle from [" + this.location + "]"));
        Bundle bundle = null;
        boolean bl = installBasedOnLocation = this.resource == null;
        if (!installBasedOnLocation) {
            InputStream stream = null;
            try {
                stream = this.resource.getInputStream();
            }
            catch (IOException ex) {
                installBasedOnLocation = true;
            }
            if (!installBasedOnLocation) {
                bundle = this.bundleContext.installBundle(this.location, stream);
            }
        }
        if (installBasedOnLocation) {
            bundle = this.bundleContext.installBundle(this.location);
        }
        return bundle;
    }

    private Bundle findBundle() {
        Bundle bundle = null;
        if (StringUtils.hasText((String)this.symbolicName)) {
            bundle = OsgiBundleUtils.findBundleBySymbolicName(this.bundleContext, this.symbolicName);
        }
        return bundle;
    }

    public Resource getResource() {
        return this.resource;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String url) {
        this.location = url;
    }

    public String getSymbolicName() {
        return this.symbolicName;
    }

    public void setSymbolicName(String symbolicName) {
        this.symbolicName = symbolicName;
    }

    @Override
    public void setBundleContext(BundleContext context) {
        this.bundleContext = context;
    }

    public BundleActionEnum getBundleAction() {
        return this.action;
    }

    public void setBundleAction(BundleActionEnum action) {
        this.action = action;
    }

    public BundleActionEnum getBundleDestroyAction() {
        return this.destroyAction;
    }

    public void setBundleDestroyAction(BundleActionEnum action) {
        this.destroyAction = action;
    }

    public int getStartLevel() {
        return this.startLevel;
    }

    public void setStartLevel(int startLevel) {
        this.startLevel = startLevel;
    }

    public void setPushBundleAsContextClassLoader(boolean pushBundleAsContextClassLoader) {
        this.pushBundleAsContextClassLoader = pushBundleAsContextClassLoader;
    }

    public void setClassLoader(ClassLoader classloader) {
        this.classLoader = classloader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private void updateStartLevel(int level) {
        if (level == 0 || this.bundle == null) {
            return;
        }
        ServiceReference startref = this.bundleContext.getServiceReference(StartLevel.class.getName());
        if (startref != null) {
            StartLevel start = (StartLevel)this.bundleContext.getService(startref);
            if (start != null) {
                start.setBundleStartLevel(this.bundle, level);
            }
            this.bundleContext.ungetService(startref);
        }
    }

    public Bundle getBundle() {
        return this.bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}

