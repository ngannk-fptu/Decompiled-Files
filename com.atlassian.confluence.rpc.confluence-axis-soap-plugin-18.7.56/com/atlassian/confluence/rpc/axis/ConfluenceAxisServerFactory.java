/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.rpc.SoapModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rpc.axis;

import com.atlassian.confluence.plugin.descriptor.rpc.SoapModuleDescriptor;
import com.atlassian.confluence.rpc.axis.ConfluenceAxisSoapService;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.spring.container.ContainerManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.server.AxisServer;
import org.apache.axis.server.DefaultAxisServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceAxisServerFactory
extends DefaultAxisServerFactory {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceAxisServerFactory.class);

    @Override
    public AxisServer getServer(Map environment) throws AxisFault {
        log.warn("getServer");
        EngineConfiguration defaultConfig = null;
        if (environment != null) {
            try {
                defaultConfig = (EngineConfiguration)environment.get("engineConfig");
            }
            catch (ClassCastException e) {
                log.warn(e.getMessage(), (Throwable)e);
            }
        } else {
            environment = new HashMap<String, SimpleProvider>();
        }
        SimpleProvider newConfig = new SimpleProvider(defaultConfig);
        List soapDescriptors = ((PluginAccessor)ContainerManager.getComponent((String)"pluginAccessor")).getEnabledModuleDescriptorsByClass(SoapModuleDescriptor.class);
        for (SoapModuleDescriptor descriptor : soapDescriptors) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Publishing to " + descriptor.getServicePath() + " module " + descriptor.getModuleClass() + " with interface " + descriptor.getPublishedInterface());
                }
                ConfluenceAxisSoapService soapService = new ConfluenceAxisSoapService(descriptor);
                newConfig.deployService(soapService.getName(), (SOAPService)soapService);
            }
            catch (Throwable e) {
                log.warn("Error registering soap service: " + e, e);
            }
        }
        environment.put("engineConfig", newConfig);
        return super.getServer(environment);
    }
}

