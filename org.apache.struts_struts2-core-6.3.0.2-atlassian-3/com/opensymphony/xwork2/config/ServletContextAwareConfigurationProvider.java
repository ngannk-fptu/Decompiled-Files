/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.ConfigurationProvider;
import javax.servlet.ServletContext;

public interface ServletContextAwareConfigurationProvider
extends ConfigurationProvider {
    public void initWithContext(ServletContext var1);
}

