/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.rpc.SoapModuleDescriptor
 */
package com.atlassian.confluence.rpc.axis;

import com.atlassian.confluence.plugin.descriptor.rpc.SoapModuleDescriptor;
import com.atlassian.confluence.rpc.axis.SpringRpcProvider;
import org.apache.axis.handlers.soap.SOAPService;

public class ConfluenceAxisSoapService
extends SOAPService {
    public ConfluenceAxisSoapService(SoapModuleDescriptor descriptor) throws ClassNotFoundException {
        super(new SpringRpcProvider(descriptor));
        this.setName(descriptor.getServicePath());
        this.setServiceInterfaceName(descriptor.getPublishedInterface().getName());
        this.setOption("allowedMethods", "*");
        this.setOption("scope", "Application");
    }

    private void setServiceInterfaceName(String interfaceName) {
        this.setOption("className", interfaceName);
    }
}

