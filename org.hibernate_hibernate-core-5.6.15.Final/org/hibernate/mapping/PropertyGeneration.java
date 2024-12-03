/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.io.Serializable;

@Deprecated
public class PropertyGeneration
implements Serializable {
    public static final PropertyGeneration NEVER = new PropertyGeneration("never");
    public static final PropertyGeneration INSERT = new PropertyGeneration("insert");
    public static final PropertyGeneration ALWAYS = new PropertyGeneration("always");
    private final String name;

    private PropertyGeneration(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static PropertyGeneration parse(String name) {
        if ("insert".equalsIgnoreCase(name)) {
            return INSERT;
        }
        if ("always".equalsIgnoreCase(name)) {
            return ALWAYS;
        }
        return NEVER;
    }

    private Object readResolve() {
        return PropertyGeneration.parse(this.name);
    }

    public String toString() {
        return this.getClass().getName() + "(" + this.getName() + ")";
    }
}

