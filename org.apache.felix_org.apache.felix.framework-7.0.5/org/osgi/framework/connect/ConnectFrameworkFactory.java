/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework.connect;

import java.util.Map;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.connect.ModuleConnector;
import org.osgi.framework.launch.Framework;

@ProviderType
public interface ConnectFrameworkFactory {
    public Framework newFramework(Map<String, String> var1, ModuleConnector var2);
}

