/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.querydsl.core.types;

import com.google.common.base.Objects;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathType;
import java.io.Serializable;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PathMetadata
implements Serializable {
    private static final long serialVersionUID = -1055994185028970065L;
    private final Object element;
    private final int hashCode;
    @Nullable
    private final Path<?> parent;
    @Nullable
    private final Path<?> rootPath;
    private final PathType pathType;

    public PathMetadata(@Nullable Path<?> parent, Object element, PathType type) {
        this.parent = parent;
        this.element = element;
        this.pathType = type;
        this.rootPath = parent != null ? parent.getRoot() : null;
        this.hashCode = 31 * element.hashCode() + this.pathType.name().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PathMetadata) {
            PathMetadata p = (PathMetadata)obj;
            return this.element.equals(p.element) && this.pathType == p.pathType && Objects.equal(this.parent, p.parent);
        }
        return false;
    }

    public Object getElement() {
        return this.element;
    }

    public String getName() {
        if (this.pathType == PathType.VARIABLE || this.pathType == PathType.PROPERTY) {
            return (String)this.element;
        }
        throw new IllegalStateException("name property not available for path of type " + this.pathType + ". Use getElement() to access the generic path element.");
    }

    @Nullable
    public Path<?> getParent() {
        return this.parent;
    }

    public PathType getPathType() {
        return this.pathType;
    }

    @Nullable
    public Path<?> getRootPath() {
        return this.rootPath;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean isRoot() {
        return this.parent == null || this.pathType == PathType.DELEGATE && this.parent.getMetadata().isRoot();
    }
}

