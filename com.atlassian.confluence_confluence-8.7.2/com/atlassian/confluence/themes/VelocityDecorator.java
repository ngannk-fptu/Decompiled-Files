/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.opensymphony.module.sitemesh.Decorator
 *  com.opensymphony.module.sitemesh.mapper.DefaultDecorator
 *  org.apache.struts2.views.velocity.VelocityManager
 *  org.apache.velocity.exception.ParseErrorException
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.themes;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.plugin.descriptor.LayoutModuleDescriptor;
import com.atlassian.confluence.themes.ThemedDecorator;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.mapper.DefaultDecorator;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityDecorator
implements ThemedDecorator {
    private static final Logger log = LoggerFactory.getLogger(VelocityDecorator.class);
    private String templatePath;
    private String name;
    private String moduleCompleteKey;

    @Override
    public Decorator getDecorator(Decorator parentDecorator) {
        try {
            VelocityManager velocityManager = (VelocityManager)BootstrapUtils.getBootstrapContext().getBean("velocityManager", VelocityManager.class);
            velocityManager.getVelocityEngine().getTemplate(this.templatePath);
            return new DefaultDecorator(parentDecorator.getName(), this.templatePath, null);
        }
        catch (ResourceNotFoundException e) {
            log.error("Layout module could not find velocity template:" + this.templatePath, (Throwable)e);
        }
        catch (ParseErrorException e) {
            log.error("Error parsing decorator template:" + this.templatePath, (Throwable)e);
        }
        catch (Exception e) {
            log.error("Error retrieving space decorator:" + this.templatePath, (Throwable)e);
        }
        return null;
    }

    @Override
    public void init(LayoutModuleDescriptor moduleDescriptor) {
        this.templatePath = moduleDescriptor.getResourceDescriptor("velocity", "decorator").getLocation();
        this.name = moduleDescriptor.getDecoratorPath();
        this.moduleCompleteKey = moduleDescriptor.getCompleteKey();
    }

    @Override
    public String getResourceKey() {
        return this.moduleCompleteKey;
    }

    public String getVelocityTemplatePath() {
        return this.templatePath;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

