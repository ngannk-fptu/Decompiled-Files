/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.labels.Label;
import com.google.common.collect.ImmutableMap;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Map;

public class Namespace
implements Serializable,
Comparable<Namespace> {
    public static final String VISIBILITY_PUBLIC = "public";
    public static final String VISIBILITY_OWNER = "owner";
    public static final String VISIBILITY_SYSTEM = "system";
    public static final Namespace PERSONAL = new Namespace("my", "owner");
    public static final Namespace TEAM = new Namespace("team", "public");
    public static final Namespace GLOBAL = new Namespace("global", "public");
    public static final Namespace SYSTEM = new Namespace("system", "system");
    private static final Map<String, Namespace> officialNamespaces;
    private final String namespacePrefix;
    private final String visibility;

    public static boolean isPersonal(Label l) {
        return PERSONAL.equals(l.getNamespace());
    }

    public static boolean isTeam(Label l) {
        return TEAM.equals(l.getNamespace());
    }

    public static boolean isGlobal(Label l) {
        return GLOBAL.equals(l.getNamespace());
    }

    public static Namespace getNamespace(String prefix) {
        if (prefix == null) {
            return null;
        }
        Namespace namespace = officialNamespaces.get(prefix);
        return namespace == null ? new Namespace(prefix, VISIBILITY_SYSTEM) : namespace;
    }

    private Namespace(String namespacePrefix, String visibility) {
        this.namespacePrefix = namespacePrefix;
        this.visibility = visibility;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Namespace namespace = (Namespace)o;
        return this.namespacePrefix.equals(namespace.namespacePrefix);
    }

    @Override
    public int compareTo(Namespace o) {
        return this.namespacePrefix.compareTo(o.namespacePrefix);
    }

    public String getPrefix() {
        return this.namespacePrefix;
    }

    public String toString() {
        return this.namespacePrefix;
    }

    public int hashCode() {
        return this.namespacePrefix.hashCode();
    }

    public String getVisibility() {
        return this.visibility;
    }

    private Object readResolve() throws ObjectStreamException {
        return Namespace.getNamespace(this.namespacePrefix);
    }

    static {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put((Object)"my", (Object)PERSONAL);
        builder.put((Object)"team", (Object)TEAM);
        builder.put((Object)"global", (Object)GLOBAL);
        builder.put((Object)VISIBILITY_SYSTEM, (Object)SYSTEM);
        officialNamespaces = builder.build();
    }
}

