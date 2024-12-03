/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.cfgxml.internal;

import java.util.Map;
import org.hibernate.boot.cfgxml.internal.CfgXmlAccessServiceImpl;
import org.hibernate.boot.cfgxml.spi.CfgXmlAccessService;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class CfgXmlAccessServiceInitiator
implements StandardServiceInitiator<CfgXmlAccessService> {
    public static final CfgXmlAccessServiceInitiator INSTANCE = new CfgXmlAccessServiceInitiator();

    @Override
    public CfgXmlAccessService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return new CfgXmlAccessServiceImpl(configurationValues);
    }

    @Override
    public Class<CfgXmlAccessService> getServiceInitiated() {
        return CfgXmlAccessService.class;
    }
}

