/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.cluster;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.cluster.ClusterScheme;

public class ClusterSchemeNotAvailableException
extends CacheException {
    private final ClusterScheme unavailableClusterScheme;

    public ClusterSchemeNotAvailableException(ClusterScheme unavailableClusterScheme) {
        this.unavailableClusterScheme = unavailableClusterScheme;
    }

    public ClusterSchemeNotAvailableException(ClusterScheme unavailableClusterScheme, String message, Throwable cause) {
        super(message, cause);
        this.unavailableClusterScheme = unavailableClusterScheme;
    }

    public ClusterSchemeNotAvailableException(ClusterScheme unavailableClusterScheme, String message) {
        super(message);
        this.unavailableClusterScheme = unavailableClusterScheme;
    }

    public ClusterSchemeNotAvailableException(ClusterScheme unavailableClusterScheme, Throwable cause) {
        super(cause);
        this.unavailableClusterScheme = unavailableClusterScheme;
    }

    public ClusterScheme getUnavailableClusterScheme() {
        return this.unavailableClusterScheme;
    }
}

