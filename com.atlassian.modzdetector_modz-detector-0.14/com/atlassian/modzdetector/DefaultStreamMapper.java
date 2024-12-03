/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.modzdetector;

import com.atlassian.modzdetector.ResourceAccessor;
import com.atlassian.modzdetector.StreamMapper;
import java.io.File;
import java.io.InputStream;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

class DefaultStreamMapper
implements StreamMapper {
    private ResourceAccessor resourceAccessor;

    public DefaultStreamMapper(ResourceAccessor resourceAccessor) {
        this.resourceAccessor = resourceAccessor;
    }

    public InputStream mapStream(String prefix, String resourceName) {
        if ("cp.".equals(prefix)) {
            return this.resourceAccessor.getResourceFromClasspath(resourceName);
        }
        if ("fs.".equals(prefix)) {
            return this.resourceAccessor.getResourceByPath(resourceName);
        }
        return null;
    }

    public String getResourcePath(String resourceKey) {
        if (resourceKey.startsWith("cp.")) {
            return resourceKey.substring("cp.".length());
        }
        if (resourceKey.startsWith("fs.")) {
            return resourceKey.substring("fs.".length());
        }
        throw new IllegalArgumentException("Resource key '" + resourceKey + "' is illegal.");
    }

    public String getResourceKey(File file) {
        throw new NotImplementedException();
    }
}

