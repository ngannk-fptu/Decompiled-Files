/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.spi.util.TypeAccessor
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.host.spring;

import com.atlassian.applinks.host.spring.AbstractAppLinksServiceFactoryBean;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TypeAccessorServiceFactoryBean
extends AbstractAppLinksServiceFactoryBean {
    @Autowired
    public TypeAccessorServiceFactoryBean(OsgiContainerManager osgiContainerManager) {
        super(osgiContainerManager, TypeAccessor.class);
    }
}

