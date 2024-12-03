/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.osgi;

import com.atlassian.upm.osgi.Bundle;
import com.atlassian.upm.osgi.Version;

public interface Package {
    public String getName();

    public Bundle getExportingBundle();

    public Iterable<Bundle> getImportingBundles();

    public Version getVersion();
}

