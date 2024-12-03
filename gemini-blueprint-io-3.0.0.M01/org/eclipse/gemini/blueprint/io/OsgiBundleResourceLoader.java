/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.springframework.core.io.DefaultResourceLoader
 *  org.springframework.core.io.Resource
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.io;

import org.eclipse.gemini.blueprint.io.OsgiBundleResource;
import org.osgi.framework.Bundle;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class OsgiBundleResourceLoader
extends DefaultResourceLoader {
    private final Bundle bundle;

    public OsgiBundleResourceLoader(Bundle bundle) {
        this.bundle = bundle;
    }

    protected Resource getResourceByPath(String path) {
        Assert.notNull((Object)path, (String)"Path is required");
        return new OsgiBundleResource(this.bundle, path);
    }

    public Resource getResource(String location) {
        Assert.notNull((Object)location, (String)"location is required");
        return new OsgiBundleResource(this.bundle, location);
    }

    public final Bundle getBundle() {
        return this.bundle;
    }
}

