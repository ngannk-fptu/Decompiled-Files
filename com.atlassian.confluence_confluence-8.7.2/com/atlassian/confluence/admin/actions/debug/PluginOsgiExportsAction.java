/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.hostcomponents.ComponentRegistrar
 *  com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy
 *  com.atlassian.plugin.osgi.hostcomponents.HostComponentProvider
 *  com.atlassian.plugin.osgi.hostcomponents.InstanceBuilder
 *  com.atlassian.plugin.osgi.hostcomponents.PropertyBuilder
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.admin.actions.debug;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.osgi.hostcomponents.ComponentRegistrar;
import com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentProvider;
import com.atlassian.plugin.osgi.hostcomponents.InstanceBuilder;
import com.atlassian.plugin.osgi.hostcomponents.PropertyBuilder;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@WebSudoRequired
@SystemAdminOnly
public class PluginOsgiExportsAction
extends ConfluenceActionSupport {
    private HostComponentProvider provider;
    private StubComponentRegistrar registrar = new StubComponentRegistrar();

    public StubComponentRegistrar getRegistrar() {
        return this.registrar;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        this.provider.provide((ComponentRegistrar)this.registrar);
        this.registrar.calculateDuplicates();
        return "success";
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public void setHostComponentProvider(HostComponentProvider provider) {
        this.provider = provider;
    }

    public static class StubComponentRegistrar
    implements ComponentRegistrar {
        private final Map<String, Class<?>[]> names = Maps.newHashMap();
        private final Map<Class<?>, Integer> interfaceCounts = Maps.newHashMap();

        public InstanceBuilder register(Class<?> ... interfaces) {
            return new StubInstanceBuilder(this, interfaces);
        }

        public List<String> getNames() {
            ArrayList<String> sorted = new ArrayList<String>(this.names.keySet());
            Collections.sort(sorted);
            return sorted;
        }

        public boolean hasInterfaces(String name) {
            return this.names.containsKey(name) && this.names.get(name).length > 0;
        }

        public Class<?>[] getInterfaces(String name) {
            return this.names.get(name);
        }

        public boolean isDuplicated(Class<?> interfaceClass) {
            return this.interfaceCounts.containsKey(interfaceClass) && this.interfaceCounts.get(interfaceClass) > 1;
        }

        public void calculateDuplicates() {
            for (Class<?>[] classes : this.names.values()) {
                for (Class<?> clazz : classes) {
                    this.interfaceCounts.put(clazz, this.interfaceCounts.containsKey(clazz) ? this.interfaceCounts.get(clazz) + 1 : 1);
                }
            }
        }

        private static class StubInstanceBuilder
        implements InstanceBuilder,
        PropertyBuilder {
            private final StubComponentRegistrar registrar;
            private final Class<?>[] interfaces;

            public StubInstanceBuilder(StubComponentRegistrar registrar, Class<?>[] interfaces) {
                this.registrar = registrar;
                this.interfaces = interfaces;
            }

            public PropertyBuilder forInstance(Object instance) {
                return this;
            }

            public PropertyBuilder withName(String name) {
                this.registrar.names.put(name, this.interfaces);
                return this;
            }

            public PropertyBuilder withContextClassLoaderStrategy(ContextClassLoaderStrategy strategy) {
                return this;
            }

            public PropertyBuilder withProperty(String name, String value) {
                return this;
            }

            public PropertyBuilder withTrackBundleEnabled(boolean enabled) {
                return this;
            }
        }
    }
}

