/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework.connect;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleException;
import org.osgi.framework.connect.ConnectModule;

@ConsumerType
public interface ModuleConnector {
    public void initialize(File var1, Map<String, String> var2);

    public Optional<ConnectModule> connect(String var1) throws BundleException;

    public Optional<BundleActivator> newBundleActivator();
}

