/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.ModuleReaderProxy;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.utils.CollectionUtils;

public class ModuleRef
implements Comparable<ModuleRef> {
    private final String name;
    private final Object reference;
    private final Object layer;
    private final Object descriptor;
    private final List<String> packages;
    private final URI location;
    private String locationStr;
    private File locationFile;
    private String rawVersion;
    private final ClassLoader classLoader;
    ReflectionUtils reflectionUtils;

    public ModuleRef(Object moduleReference, Object moduleLayer, ReflectionUtils reflectionUtils) {
        Object moduleLocationOptional;
        Boolean isPresent;
        if (moduleReference == null) {
            throw new IllegalArgumentException("moduleReference cannot be null");
        }
        if (moduleLayer == null) {
            throw new IllegalArgumentException("moduleLayer cannot be null");
        }
        this.reference = moduleReference;
        this.layer = moduleLayer;
        this.reflectionUtils = reflectionUtils;
        this.descriptor = reflectionUtils.invokeMethod(true, moduleReference, "descriptor");
        if (this.descriptor == null) {
            throw new IllegalArgumentException("moduleReference.descriptor() should not return null");
        }
        this.name = (String)reflectionUtils.invokeMethod(true, this.descriptor, "name");
        Set modulePackages = (Set)reflectionUtils.invokeMethod(true, this.descriptor, "packages");
        if (modulePackages == null) {
            throw new IllegalArgumentException("moduleReference.descriptor().packages() should not return null");
        }
        this.packages = new ArrayList<String>(modulePackages);
        CollectionUtils.sortIfNotEmpty(this.packages);
        Object optionalRawVersion = reflectionUtils.invokeMethod(true, this.descriptor, "rawVersion");
        if (optionalRawVersion != null && (isPresent = (Boolean)reflectionUtils.invokeMethod(true, optionalRawVersion, "isPresent")) != null && isPresent.booleanValue()) {
            this.rawVersion = (String)reflectionUtils.invokeMethod(true, optionalRawVersion, "get");
        }
        if ((moduleLocationOptional = reflectionUtils.invokeMethod(true, moduleReference, "location")) == null) {
            throw new IllegalArgumentException("moduleReference.location() should not return null");
        }
        Object moduleLocationIsPresent = reflectionUtils.invokeMethod(true, moduleLocationOptional, "isPresent");
        if (moduleLocationIsPresent == null) {
            throw new IllegalArgumentException("moduleReference.location().isPresent() should not return null");
        }
        if (((Boolean)moduleLocationIsPresent).booleanValue()) {
            this.location = (URI)reflectionUtils.invokeMethod(true, moduleLocationOptional, "get");
            if (this.location == null) {
                throw new IllegalArgumentException("moduleReference.location().get() should not return null");
            }
        } else {
            this.location = null;
        }
        this.classLoader = (ClassLoader)reflectionUtils.invokeMethod(true, moduleLayer, "findLoader", String.class, this.name);
    }

    public String getName() {
        return this.name;
    }

    public Object getReference() {
        return this.reference;
    }

    public Object getLayer() {
        return this.layer;
    }

    public Object getDescriptor() {
        return this.descriptor;
    }

    public List<String> getPackages() {
        return this.packages;
    }

    public URI getLocation() {
        return this.location;
    }

    public String getLocationStr() {
        if (this.locationStr == null && this.location != null) {
            this.locationStr = this.location.toString();
        }
        return this.locationStr;
    }

    public File getLocationFile() {
        if (this.locationFile == null && this.location != null && "file".equals(this.location.getScheme())) {
            this.locationFile = new File(this.location);
        }
        return this.locationFile;
    }

    public String getRawVersion() {
        return this.rawVersion;
    }

    public boolean isSystemModule() {
        if (this.name == null || this.name.isEmpty()) {
            return false;
        }
        return this.name.startsWith("java.") || this.name.startsWith("jdk.") || this.name.startsWith("javafx.") || this.name.startsWith("oracle.");
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ModuleRef)) {
            return false;
        }
        ModuleRef modRef = (ModuleRef)obj;
        return modRef.reference.equals(this.reference) && modRef.layer.equals(this.layer);
    }

    public int hashCode() {
        return this.reference.hashCode() * this.layer.hashCode();
    }

    public String toString() {
        return this.reference.toString();
    }

    @Override
    public int compareTo(ModuleRef o) {
        int diff = this.name.compareTo(o.name);
        return diff != 0 ? diff : this.hashCode() - o.hashCode();
    }

    public ModuleReaderProxy open() throws IOException {
        return new ModuleReaderProxy(this);
    }
}

