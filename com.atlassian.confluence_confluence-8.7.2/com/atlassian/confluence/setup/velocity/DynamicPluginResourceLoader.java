/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.spring.container.ComponentNotFoundException
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.resource.Resource
 *  org.apache.velocity.runtime.resource.loader.ResourceLoader
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.spring.container.ComponentNotFoundException;
import com.atlassian.spring.container.ContainerManager;
import java.io.InputStream;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

public class DynamicPluginResourceLoader
extends ResourceLoader {
    private PluginAccessor pluginAccessor;

    public void init(ExtendedProperties extendedProperties) {
    }

    public InputStream getResourceStream(String name) throws ResourceNotFoundException {
        if (!ContainerManager.isContainerSetup()) {
            throw new ResourceNotFoundException("Spring context is not set yet.");
        }
        while (name.startsWith("/") && name.length() > 1) {
            name = name.substring(1);
        }
        if (this.pluginAccessor == null) {
            try {
                this.pluginAccessor = (PluginAccessor)ContainerManager.getComponent((String)"pluginAccessor");
            }
            catch (ComponentNotFoundException e) {
                throw new ResourceNotFoundException("No plugin manager.");
            }
        }
        return this.pluginAccessor.getDynamicResourceAsStream(name);
    }

    public boolean isSourceModified(Resource resource) {
        return false;
    }

    public long getLastModified(Resource resource) {
        return 0L;
    }
}

