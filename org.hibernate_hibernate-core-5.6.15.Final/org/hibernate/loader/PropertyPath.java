/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader;

import org.hibernate.internal.util.StringHelper;

public class PropertyPath {
    public static final String IDENTIFIER_MAPPER_PROPERTY = "_identifierMapper";
    private final PropertyPath parent;
    private final String property;
    private final String fullPath;

    public PropertyPath(PropertyPath parent, String property) {
        this.parent = parent;
        this.property = property;
        if (IDENTIFIER_MAPPER_PROPERTY.equals(property)) {
            this.fullPath = parent != null ? parent.getFullPath() : "";
        } else {
            String resolvedParent;
            String prefix = parent != null ? (StringHelper.isEmpty(resolvedParent = parent.getFullPath()) ? "" : resolvedParent + '.') : "";
            this.fullPath = prefix + property;
        }
    }

    public PropertyPath(String property) {
        this(null, property);
    }

    public PropertyPath() {
        this("");
    }

    public PropertyPath append(String property) {
        return new PropertyPath(this, property);
    }

    public PropertyPath getParent() {
        return this.parent;
    }

    public String getProperty() {
        return this.property;
    }

    public String getFullPath() {
        return this.fullPath;
    }

    public boolean isRoot() {
        return this.parent == null && StringHelper.isEmpty(this.property);
    }

    public String toString() {
        return this.getClass().getSimpleName() + '[' + this.fullPath + ']';
    }
}

