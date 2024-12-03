/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.context.weaving;

import java.lang.instrument.ClassFileTransformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.ReflectiveLoadTimeWeaver;
import org.springframework.instrument.classloading.glassfish.GlassFishLoadTimeWeaver;
import org.springframework.instrument.classloading.jboss.JBossLoadTimeWeaver;
import org.springframework.instrument.classloading.tomcat.TomcatLoadTimeWeaver;
import org.springframework.instrument.classloading.weblogic.WebLogicLoadTimeWeaver;
import org.springframework.instrument.classloading.websphere.WebSphereLoadTimeWeaver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class DefaultContextLoadTimeWeaver
implements LoadTimeWeaver,
BeanClassLoaderAware,
DisposableBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private LoadTimeWeaver loadTimeWeaver;

    public DefaultContextLoadTimeWeaver() {
    }

    public DefaultContextLoadTimeWeaver(ClassLoader beanClassLoader) {
        this.setBeanClassLoader(beanClassLoader);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        LoadTimeWeaver serverSpecificLoadTimeWeaver = this.createServerSpecificLoadTimeWeaver(classLoader);
        if (serverSpecificLoadTimeWeaver != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Determined server-specific load-time weaver: " + serverSpecificLoadTimeWeaver.getClass().getName()));
            }
            this.loadTimeWeaver = serverSpecificLoadTimeWeaver;
        } else if (InstrumentationLoadTimeWeaver.isInstrumentationAvailable()) {
            this.logger.debug((Object)"Found Spring's JVM agent for instrumentation");
            this.loadTimeWeaver = new InstrumentationLoadTimeWeaver(classLoader);
        } else {
            try {
                this.loadTimeWeaver = new ReflectiveLoadTimeWeaver(classLoader);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Using reflective load-time weaver for class loader: " + this.loadTimeWeaver.getInstrumentableClassLoader().getClass().getName()));
                }
            }
            catch (IllegalStateException ex) {
                throw new IllegalStateException(ex.getMessage() + " Specify a custom LoadTimeWeaver or start your Java virtual machine with Spring's agent: -javaagent:spring-instrument-{version}.jar");
            }
        }
    }

    @Nullable
    protected LoadTimeWeaver createServerSpecificLoadTimeWeaver(ClassLoader classLoader) {
        block7: {
            String name = classLoader.getClass().getName();
            try {
                if (name.startsWith("org.apache.catalina")) {
                    return new TomcatLoadTimeWeaver(classLoader);
                }
                if (name.startsWith("org.glassfish")) {
                    return new GlassFishLoadTimeWeaver(classLoader);
                }
                if (name.startsWith("org.jboss.modules")) {
                    return new JBossLoadTimeWeaver(classLoader);
                }
                if (name.startsWith("com.ibm.ws.classloader")) {
                    return new WebSphereLoadTimeWeaver(classLoader);
                }
                if (name.startsWith("weblogic")) {
                    return new WebLogicLoadTimeWeaver(classLoader);
                }
            }
            catch (Exception ex) {
                if (!this.logger.isInfoEnabled()) break block7;
                this.logger.info((Object)("Could not obtain server-specific LoadTimeWeaver: " + ex.getMessage()));
            }
        }
        return null;
    }

    @Override
    public void destroy() {
        if (this.loadTimeWeaver instanceof InstrumentationLoadTimeWeaver) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Removing all registered transformers for class loader: " + this.loadTimeWeaver.getInstrumentableClassLoader().getClass().getName()));
            }
            ((InstrumentationLoadTimeWeaver)this.loadTimeWeaver).removeTransformers();
        }
    }

    @Override
    public void addTransformer(ClassFileTransformer transformer) {
        Assert.state(this.loadTimeWeaver != null, "Not initialized");
        this.loadTimeWeaver.addTransformer(transformer);
    }

    @Override
    public ClassLoader getInstrumentableClassLoader() {
        Assert.state(this.loadTimeWeaver != null, "Not initialized");
        return this.loadTimeWeaver.getInstrumentableClassLoader();
    }

    @Override
    public ClassLoader getThrowawayClassLoader() {
        Assert.state(this.loadTimeWeaver != null, "Not initialized");
        return this.loadTimeWeaver.getThrowawayClassLoader();
    }
}

