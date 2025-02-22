/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class InterfacesConfig {
    private boolean enabled;
    private final Set<String> interfaceSet = new HashSet<String>();

    public boolean isEnabled() {
        return this.enabled;
    }

    public InterfacesConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public InterfacesConfig addInterface(String ip) {
        this.interfaceSet.add(ip);
        return this;
    }

    public InterfacesConfig clear() {
        this.interfaceSet.clear();
        return this;
    }

    public Collection<String> getInterfaces() {
        return Collections.unmodifiableCollection(this.interfaceSet);
    }

    public InterfacesConfig setInterfaces(Collection<String> interfaces) {
        this.clear();
        this.interfaceSet.addAll(interfaces);
        return this;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof InterfacesConfig)) {
            return false;
        }
        InterfacesConfig that = (InterfacesConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        return this.interfaceSet.equals(that.interfaceSet);
    }

    public final int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + this.interfaceSet.hashCode();
        return result;
    }

    public String toString() {
        return "InterfacesConfig{enabled=" + this.enabled + ", interfaces=" + this.interfaceSet + '}';
    }
}

