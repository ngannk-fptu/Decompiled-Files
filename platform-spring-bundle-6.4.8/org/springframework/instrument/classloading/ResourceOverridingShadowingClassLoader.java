/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.instrument.classloading;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.instrument.classloading.ShadowingClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ResourceOverridingShadowingClassLoader
extends ShadowingClassLoader {
    private static final Enumeration<URL> EMPTY_URL_ENUMERATION = new Enumeration<URL>(){

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public URL nextElement() {
            throw new UnsupportedOperationException("Should not be called. I am empty.");
        }
    };
    private Map<String, String> overrides = new HashMap<String, String>();

    public ResourceOverridingShadowingClassLoader(ClassLoader enclosingClassLoader) {
        super(enclosingClassLoader);
    }

    public void override(String oldPath, String newPath) {
        this.overrides.put(oldPath, newPath);
    }

    public void suppress(String oldPath) {
        this.overrides.put(oldPath, null);
    }

    public void copyOverrides(ResourceOverridingShadowingClassLoader other) {
        Assert.notNull((Object)other, "Other ClassLoader must not be null");
        this.overrides.putAll(other.overrides);
    }

    @Override
    public URL getResource(String requestedPath) {
        if (this.overrides.containsKey(requestedPath)) {
            String overriddenPath = this.overrides.get(requestedPath);
            return overriddenPath != null ? super.getResource(overriddenPath) : null;
        }
        return super.getResource(requestedPath);
    }

    @Override
    @Nullable
    public InputStream getResourceAsStream(String requestedPath) {
        if (this.overrides.containsKey(requestedPath)) {
            String overriddenPath = this.overrides.get(requestedPath);
            return overriddenPath != null ? super.getResourceAsStream(overriddenPath) : null;
        }
        return super.getResourceAsStream(requestedPath);
    }

    @Override
    public Enumeration<URL> getResources(String requestedPath) throws IOException {
        if (this.overrides.containsKey(requestedPath)) {
            String overriddenLocation = this.overrides.get(requestedPath);
            return overriddenLocation != null ? super.getResources(overriddenLocation) : EMPTY_URL_ENUMERATION;
        }
        return super.getResources(requestedPath);
    }
}

