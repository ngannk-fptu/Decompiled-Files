/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.exception.VelocityException
 *  org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.ResourceLoaderAware
 *  org.springframework.web.context.ServletContextAware
 */
package org.springframework.web.servlet.view.velocity;

import java.io.IOException;
import javax.servlet.ServletContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.velocity.VelocityConfig;
import org.springframework.web.servlet.view.velocity.VelocityEngineFactory;

public class VelocityConfigurer
extends VelocityEngineFactory
implements VelocityConfig,
InitializingBean,
ResourceLoaderAware,
ServletContextAware {
    private static final String SPRING_MACRO_RESOURCE_LOADER_NAME = "springMacro";
    private static final String SPRING_MACRO_RESOURCE_LOADER_CLASS = "springMacro.resource.loader.class";
    private static final String SPRING_MACRO_LIBRARY = "org/springframework/web/servlet/view/velocity/spring.vm";
    private VelocityEngine velocityEngine;
    private ServletContext servletContext;

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void afterPropertiesSet() throws IOException, VelocityException {
        if (this.velocityEngine == null) {
            this.velocityEngine = this.createVelocityEngine();
        }
    }

    @Override
    protected void postProcessVelocityEngine(VelocityEngine velocityEngine) {
        velocityEngine.setApplicationAttribute((Object)ServletContext.class.getName(), (Object)this.servletContext);
        velocityEngine.setProperty(SPRING_MACRO_RESOURCE_LOADER_CLASS, (Object)ClasspathResourceLoader.class.getName());
        velocityEngine.addProperty("resource.loader", (Object)SPRING_MACRO_RESOURCE_LOADER_NAME);
        velocityEngine.addProperty("velocimacro.library", (Object)SPRING_MACRO_LIBRARY);
        if (this.logger.isInfoEnabled()) {
            this.logger.info((Object)"ClasspathResourceLoader with name 'springMacro' added to configured VelocityEngine");
        }
    }

    @Override
    public VelocityEngine getVelocityEngine() {
        return this.velocityEngine;
    }
}

