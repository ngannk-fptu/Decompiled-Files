/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.apache.axis.configuration;

import javax.servlet.ServletContext;
import org.apache.axis.configuration.DefaultEngineConfigurationFactory;
import org.apache.axis.configuration.EngineConfigurationFactoryFinder;

public class ServletEngineConfigurationFactory
extends DefaultEngineConfigurationFactory {
    public ServletEngineConfigurationFactory(ServletContext ctx) {
        super(EngineConfigurationFactoryFinder.newFactory(ctx));
    }
}

