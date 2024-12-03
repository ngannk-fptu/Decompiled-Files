/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.util.Map;
import org.apache.felix.framework.Felix;
import org.osgi.framework.connect.ConnectFrameworkFactory;
import org.osgi.framework.connect.ModuleConnector;
import org.osgi.framework.launch.Framework;

public class FrameworkFactory
implements org.osgi.framework.launch.FrameworkFactory,
ConnectFrameworkFactory {
    public Framework newFramework(Map configuration) {
        return new Felix(configuration);
    }

    @Override
    public Framework newFramework(Map<String, String> configuration, ModuleConnector connectFramework) {
        return new Felix(configuration, connectFramework);
    }
}

