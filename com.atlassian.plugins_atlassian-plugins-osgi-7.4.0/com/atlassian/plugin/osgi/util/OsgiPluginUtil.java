/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginDependencies
 *  com.atlassian.plugin.PluginDependencies$Builder
 *  com.atlassian.plugin.module.ContainerAccessor
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.wiring.BundleRevision
 *  org.osgi.framework.wiring.BundleRevisions
 *  org.osgi.framework.wiring.BundleWire
 */
package com.atlassian.plugin.osgi.util;

import com.atlassian.plugin.PluginDependencies;
import com.atlassian.plugin.module.ContainerAccessor;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import java.util.Collection;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRevisions;
import org.osgi.framework.wiring.BundleWire;

public final class OsgiPluginUtil {
    private static final PluginDependencies EMPTY_DEPS = new PluginDependencies();
    private static final String ERROR_MESSAGE = "Plugin '%s' has no container";

    public static ContainerAccessor createNonExistingPluginContainer(final String pluginKey) {
        return new ContainerAccessor(){

            public <T> T createBean(Class<T> clazz) {
                throw new UnsupportedOperationException(String.format(OsgiPluginUtil.ERROR_MESSAGE, pluginKey));
            }

            public <T> T injectBean(T bean) {
                throw new UnsupportedOperationException(String.format(OsgiPluginUtil.ERROR_MESSAGE, pluginKey));
            }

            public <T> T getBean(String id) {
                throw new UnsupportedOperationException(String.format(OsgiPluginUtil.ERROR_MESSAGE, pluginKey));
            }

            public <T> Collection<T> getBeansOfType(Class<T> interfaceClass) {
                throw new UnsupportedOperationException(String.format(OsgiPluginUtil.ERROR_MESSAGE, pluginKey));
            }
        };
    }

    public static PluginDependencies getDependencies(Bundle bundle) {
        int state = bundle.getState();
        if (state == 2 || state == 1) {
            return EMPTY_DEPS;
        }
        PluginDependencies.Builder depsBuilder = PluginDependencies.builder();
        if (bundle instanceof BundleRevisions) {
            for (BundleRevision bundleRevision : ((BundleRevisions)bundle).getRevisions()) {
                block9: for (BundleWire requiredWire : bundleRevision.getWiring().getRequiredWires(null)) {
                    String pluginKey = OsgiHeaderUtil.getPluginKey(requiredWire.getProviderWiring().getBundle());
                    String resolutionDirective = (String)requiredWire.getRequirement().getDirectives().get("resolution");
                    if (resolutionDirective == null) {
                        resolutionDirective = "mandatory";
                    }
                    switch (resolutionDirective) {
                        case "optional": {
                            depsBuilder.withOptional(new String[]{pluginKey});
                            continue block9;
                        }
                        case "dynamic": {
                            depsBuilder.withDynamic(new String[]{pluginKey});
                            continue block9;
                        }
                    }
                    depsBuilder.withMandatory(new String[]{pluginKey});
                }
            }
        }
        return depsBuilder.build();
    }
}

