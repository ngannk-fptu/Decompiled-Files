/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.util.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MemberGroupConfig {
    private final Set<String> interfaces = new HashSet<String>();

    public MemberGroupConfig addInterface(String ip) {
        this.interfaces.add(Preconditions.checkHasText(ip, "ip must contain text"));
        return this;
    }

    public MemberGroupConfig clear() {
        this.interfaces.clear();
        return this;
    }

    public Collection<String> getInterfaces() {
        return Collections.unmodifiableCollection(this.interfaces);
    }

    public MemberGroupConfig setInterfaces(Collection<String> interfaces) {
        Preconditions.isNotNull(interfaces, "interfaces");
        this.clear();
        this.interfaces.addAll(interfaces);
        return this;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof MemberGroupConfig)) {
            return false;
        }
        MemberGroupConfig that = (MemberGroupConfig)o;
        return this.interfaces.equals(that.interfaces);
    }

    public final int hashCode() {
        return this.interfaces.hashCode();
    }

    public String toString() {
        return "MemberGroupConfig{interfaces=" + this.interfaces + '}';
    }
}

