/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.ognl;

import com.opensymphony.xwork2.config.ConfigurationProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProviderAllowlist {
    private final Map<ConfigurationProvider, Set<Class<?>>> allowlistMap = new HashMap();
    private Set<Class<?>> allowlistClasses;

    public ProviderAllowlist() {
        this.reconstructAllowlist();
    }

    public synchronized void registerAllowlist(ConfigurationProvider configurationProvider, Set<Class<?>> allowlist) {
        Set<Class<?>> existingAllowlist = this.allowlistMap.get(configurationProvider);
        if (existingAllowlist != null) {
            this.clearAllowlist(configurationProvider);
        }
        this.allowlistMap.put(configurationProvider, new HashSet(allowlist));
        this.allowlistClasses.addAll(allowlist);
    }

    public synchronized void clearAllowlist(ConfigurationProvider configurationProvider) {
        Set<Class<?>> allowlist = this.allowlistMap.get(configurationProvider);
        if (allowlist == null) {
            return;
        }
        this.allowlistMap.remove(configurationProvider);
        this.reconstructAllowlist();
    }

    public Set<Class<?>> getProviderAllowlist() {
        return Collections.unmodifiableSet(this.allowlistClasses);
    }

    private void reconstructAllowlist() {
        this.allowlistClasses = this.allowlistMap.values().stream().reduce(new HashSet(), (a, b) -> {
            a.addAll(b);
            return a;
        });
    }
}

