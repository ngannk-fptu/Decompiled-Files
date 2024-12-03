/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.spi.link.MutatingEntityLinkService
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.host.spring;

import com.atlassian.applinks.host.spring.AbstractAppLinksServiceFactoryBean;
import com.atlassian.applinks.spi.link.MutatingEntityLinkService;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityLinkServiceFactoryBean
extends AbstractAppLinksServiceFactoryBean {
    @Autowired
    public EntityLinkServiceFactoryBean(OsgiContainerManager osgiContainerManager) {
        super(osgiContainerManager, MutatingEntityLinkService.class);
    }
}

