/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metamodel.model.domain;

import java.io.Serializable;
import java.util.Objects;
import org.hibernate.internal.util.StringHelper;

public class NavigableRole
implements Serializable {
    public static final String IDENTIFIER_MAPPER_PROPERTY = "_identifierMapper";
    private final NavigableRole parent;
    private final String navigableName;
    private final String fullPath;

    public NavigableRole(NavigableRole parent, String navigableName) {
        this.parent = parent;
        this.navigableName = navigableName;
        if (IDENTIFIER_MAPPER_PROPERTY.equals(navigableName)) {
            this.fullPath = parent != null ? parent.getFullPath() : "";
        } else {
            String resolvedParent;
            String prefix = parent != null ? (StringHelper.isEmpty(resolvedParent = parent.getFullPath()) ? "" : resolvedParent + '.') : "";
            this.fullPath = prefix + navigableName;
        }
    }

    public NavigableRole(String navigableName) {
        this(null, navigableName);
    }

    public NavigableRole() {
        this("");
    }

    public NavigableRole append(String property) {
        return new NavigableRole(this, property);
    }

    public NavigableRole getParent() {
        return this.parent;
    }

    public String getNavigableName() {
        return this.navigableName;
    }

    public String getFullPath() {
        return this.fullPath;
    }

    public boolean isRoot() {
        return this.parent == null && StringHelper.isEmpty(this.navigableName);
    }

    public String toString() {
        return this.getClass().getSimpleName() + '[' + this.fullPath + ']';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NavigableRole that = (NavigableRole)o;
        return Objects.equals(this.getFullPath(), that.getFullPath());
    }

    public int hashCode() {
        return Objects.hash(this.getFullPath());
    }
}

