/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.velocity.runtime.resource.loader.ResourceLoader
 */
package com.atlassian.confluence.util.velocity;

import com.atlassian.confluence.util.velocity.ConfluenceVelocityResourceManager;
import com.atlassian.confluence.util.velocity.Velocity13CompatibleResourceLoader;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

public class CompatibleVelocityResourceManager
extends ConfluenceVelocityResourceManager {
    @Override
    protected ResourceLoader postProcessLoader(ResourceLoader loader, ExtendedProperties config) {
        if (config.getBoolean("confluence.velocity13.compatibility", false)) {
            loader = new Velocity13CompatibleResourceLoader(loader, this.rsvc.getString("input.encoding"));
        }
        return super.postProcessLoader(loader, config);
    }
}

