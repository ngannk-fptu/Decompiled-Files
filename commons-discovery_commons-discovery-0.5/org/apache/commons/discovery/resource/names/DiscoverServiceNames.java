/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.resource.names;

import org.apache.commons.discovery.ResourceDiscover;
import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.names.DiscoverNamesInFile;

public class DiscoverServiceNames
extends DiscoverNamesInFile
implements ResourceNameDiscover {
    protected static final String SERVICE_HOME = "META-INF/services/";

    public DiscoverServiceNames() {
        super(SERVICE_HOME, null);
    }

    public DiscoverServiceNames(String prefix, String suffix) {
        super(prefix == null ? SERVICE_HOME : SERVICE_HOME + prefix, suffix);
    }

    public DiscoverServiceNames(ClassLoaders loaders) {
        super(loaders, SERVICE_HOME, null);
    }

    public DiscoverServiceNames(ClassLoaders loaders, String prefix, String suffix) {
        super(loaders, prefix == null ? SERVICE_HOME : SERVICE_HOME + prefix, suffix);
    }

    public DiscoverServiceNames(ResourceDiscover discoverer) {
        super(discoverer, SERVICE_HOME, null);
    }

    public DiscoverServiceNames(ResourceDiscover discoverer, String prefix, String suffix) {
        super(discoverer, prefix == null ? SERVICE_HOME : SERVICE_HOME + prefix, suffix);
    }
}

