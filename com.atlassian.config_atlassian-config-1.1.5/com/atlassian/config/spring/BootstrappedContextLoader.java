/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.web.context.ContextLoader
 */
package com.atlassian.config.spring;

import com.atlassian.config.util.BootstrapUtils;
import javax.servlet.ServletContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

public class BootstrappedContextLoader
extends ContextLoader {
    protected ApplicationContext loadParentContext(ServletContext servletContext) throws BeansException {
        return BootstrapUtils.getBootstrapContext();
    }
}

