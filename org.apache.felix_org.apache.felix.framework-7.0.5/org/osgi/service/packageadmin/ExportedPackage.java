/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.packageadmin;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

public interface ExportedPackage {
    public String getName();

    public Bundle getExportingBundle();

    public Bundle[] getImportingBundles();

    public String getSpecificationVersion();

    public Version getVersion();

    public boolean isRemovalPending();
}

