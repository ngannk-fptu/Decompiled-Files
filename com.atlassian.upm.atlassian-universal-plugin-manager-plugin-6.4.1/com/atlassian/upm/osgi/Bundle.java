/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.upm.osgi;

import com.atlassian.upm.osgi.Package;
import com.atlassian.upm.osgi.Service;
import com.atlassian.upm.osgi.Version;
import java.net.URI;
import java.util.Map;
import javax.annotation.Nullable;

public interface Bundle {
    public State getState();

    public Map<String, String> getUnparsedHeaders();

    public Map<String, Iterable<HeaderClause>> getParsedHeaders();

    public long getId();

    @Nullable
    public URI getLocation();

    public Iterable<Service> getRegisteredServices();

    public Iterable<Service> getServicesInUse();

    public String getSymbolicName();

    @Nullable
    public String getName();

    public Version getVersion();

    public static interface HeaderClause {
        public String getPath();

        public Map<String, String> getParameters();

        public Package getReferencedPackage();
    }

    public static enum State {
        UNINSTALLED,
        INSTALLED,
        RESOLVED,
        STARTING,
        STOPPING,
        ACTIVE;

    }
}

