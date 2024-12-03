/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.name.AbstractPath;
import org.apache.jackrabbit.spi.commons.name.CurrentPath;
import org.apache.jackrabbit.spi.commons.name.RelativePath;

final class NamePath
extends RelativePath {
    private static final long serialVersionUID = -2887665244213430950L;
    private final Name name;
    private final int index;

    public NamePath(Path parent, Name name, int index) {
        super(parent);
        assert (name != null);
        assert (index >= 0);
        this.name = name;
        this.index = index;
    }

    @Override
    protected int getDepthModifier() {
        return 1;
    }

    @Override
    protected Path getParent() throws RepositoryException {
        if (this.parent != null) {
            return this.parent;
        }
        return new CurrentPath(null);
    }

    @Override
    protected String getElementString() {
        if (this.index > 1) {
            return this.name + "[" + this.index + "]";
        }
        return this.name.toString();
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public int getNormalizedIndex() {
        if (this.index != 0) {
            return this.index;
        }
        return 1;
    }

    @Override
    public boolean denotesName() {
        return true;
    }

    @Override
    public boolean isCanonical() {
        return this.parent != null && this.parent.isCanonical();
    }

    @Override
    public boolean isNormalized() {
        return this.parent == null || this.parent.isNormalized() && !this.parent.denotesCurrent();
    }

    @Override
    public Path getNormalizedPath() throws RepositoryException {
        if (this.isNormalized()) {
            return this;
        }
        Path normalized = this.parent.getNormalizedPath();
        if (normalized.denotesCurrent()) {
            normalized = null;
        }
        return new NamePath(normalized, this.name, this.index);
    }

    @Override
    public Path getCanonicalPath() throws RepositoryException {
        if (this.isCanonical()) {
            return this;
        }
        if (this.parent != null) {
            return new NamePath(this.parent.getCanonicalPath(), this.name, this.index);
        }
        throw new RepositoryException("There is no canonical representation of " + this);
    }

    @Override
    public AbstractPath getLastElement() {
        if (this.parent != null) {
            return new NamePath(null, this.name, this.index);
        }
        return this;
    }

    @Override
    public final boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof Path) {
            Path path = (Path)that;
            return path.denotesName() && this.name.equals(path.getName()) && this.getNormalizedIndex() == path.getNormalizedIndex() && super.equals(that);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return super.hashCode() * 37 + this.name.hashCode() + this.getNormalizedIndex();
    }
}

