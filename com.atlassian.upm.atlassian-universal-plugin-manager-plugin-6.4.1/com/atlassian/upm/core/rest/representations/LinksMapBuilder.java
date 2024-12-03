/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;

public class LinksMapBuilder {
    private final PermissionEnforcer permissionEnforcer;
    private final Map<String, URI> links = new HashMap<String, URI>();
    private final Map<Pair<Permission, com.atlassian.upm.api.util.Option<Plugin>>, Boolean> permissionCache = new HashMap<Pair<Permission, com.atlassian.upm.api.util.Option<Plugin>>, Boolean>();

    public LinksMapBuilder(PermissionEnforcer permissionEnforcer) {
        this.permissionEnforcer = permissionEnforcer;
    }

    public LinksMapBuilder putIfPermitted(Permission permission, String rel, URI uri) {
        return this.isPermitted(permission, com.atlassian.upm.api.util.Option.none()) ? this.put(rel, uri) : this;
    }

    public LinksMapBuilder putIfPermitted(Permission permission, String rel, com.atlassian.upm.api.util.Option<URI> uri) {
        Iterator<URI> iterator = uri.iterator();
        if (iterator.hasNext()) {
            URI u = iterator.next();
            return this.putIfPermitted(permission, rel, u);
        }
        return this;
    }

    public LinksMapBuilder putIfPermittedAndConditioned(Permission permission, boolean condition, String rel, URI uri) {
        return condition ? this.putIfPermitted(permission, rel, uri) : this;
    }

    public LinksMapBuilder putIfPermitted(Permission permission, com.atlassian.upm.api.util.Option<Plugin> possiblePlugin, String rel, URI uri) {
        if (this.isPermitted(permission, possiblePlugin)) {
            return this.put(rel, uri);
        }
        return this;
    }

    public LinksMapBuilder putIfPermitted(Permission permission, com.atlassian.upm.api.util.Option<Plugin> possiblePlugin, String rel, com.atlassian.upm.api.util.Option<URI> uri) {
        if (possiblePlugin.isDefined()) {
            Iterator<URI> iterator = uri.iterator();
            if (iterator.hasNext()) {
                URI u = iterator.next();
                return this.putIfPermitted(permission, possiblePlugin, rel, u);
            }
            return this;
        }
        return this.putIfPermitted(permission, rel, uri);
    }

    public LinksMapBuilder putIfPermittedAndConditioned(Permission permission, com.atlassian.upm.api.util.Option<Plugin> possiblePlugin, Predicate<Plugin> condition, String rel, URI uri) {
        for (Plugin plugin : possiblePlugin) {
            if (!condition.test(plugin)) continue;
            return this.putIfPermitted(permission, possiblePlugin, rel, uri);
        }
        return this;
    }

    public LinksMapBuilder putIfPermittedAndConditioned(Permission permission, com.atlassian.upm.api.util.Option<Plugin> possiblePlugin, boolean condition, String rel, URI uri) {
        return condition ? this.putIfPermitted(permission, possiblePlugin, rel, uri) : this;
    }

    public LinksMapBuilder putIfPermittedForModule(Permission permission, com.atlassian.upm.api.util.Option<Plugin.Module> possibleModule, String rel, URI uri) {
        Iterator<Plugin.Module> iterator = possibleModule.iterator();
        if (iterator.hasNext()) {
            Plugin.Module module = iterator.next();
            return this.permissionEnforcer.hasPermission(permission, module) ? this.put(rel, uri) : this;
        }
        return this.putIfPermitted(permission, rel, uri);
    }

    public LinksMapBuilder putAll(Map<? extends String, ? extends URI> map) {
        this.links.putAll(map);
        return this;
    }

    public LinksMapBuilder put(String key, URI value) {
        this.links.put(key, value);
        return this;
    }

    public LinksMapBuilder put(String key, com.atlassian.upm.api.util.Option<URI> value) {
        Iterator<URI> iterator = value.iterator();
        if (iterator.hasNext()) {
            URI u = iterator.next();
            return this.put(key, u);
        }
        return this;
    }

    public LinksMapBuilder put(String key, Option<URI> value) {
        Iterator iterator = value.iterator();
        if (iterator.hasNext()) {
            URI u = (URI)iterator.next();
            return this.put(key, u);
        }
        return this;
    }

    public LinksMapBuilder put(String key, String uriString) {
        if (StringUtils.isNotBlank((CharSequence)uriString)) {
            this.links.put(key, URI.create(StringUtils.trim((String)uriString)));
        }
        return this;
    }

    public Map<String, URI> build() {
        return new HashMap<String, URI>(this.links);
    }

    private boolean isPermitted(Permission permission, com.atlassian.upm.api.util.Option<Plugin> possiblePlugin) {
        Pair<Permission, com.atlassian.upm.api.util.Option<Plugin>> key = Pair.pair(permission, possiblePlugin);
        Boolean cachedResult = this.permissionCache.get(key);
        if (cachedResult != null) {
            return cachedResult;
        }
        boolean result = possiblePlugin.isDefined() ? this.permissionEnforcer.hasPermission(permission, possiblePlugin.get()) : this.permissionEnforcer.hasPermission(permission);
        this.permissionCache.put(key, result);
        return result;
    }
}

