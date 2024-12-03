/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.name.AbstractPath;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.RelativePath;

final class ParentPath
extends RelativePath {
    private static final long serialVersionUID = -688611157827116290L;
    public static final ParentPath PARENT_PATH = new ParentPath(null);
    public static final Name NAME = NameFactoryImpl.getInstance().create("", "..");

    public ParentPath(Path parent) {
        super(parent);
    }

    @Override
    protected int getDepthModifier() {
        return -1;
    }

    @Override
    protected Path getParent() throws RepositoryException {
        if (this.isNormalized()) {
            return new ParentPath(this);
        }
        return this.parent.getAncestor(2);
    }

    @Override
    protected String getElementString() {
        return NAME.getLocalName();
    }

    @Override
    public Name getName() {
        return NAME;
    }

    @Override
    public boolean denotesParent() {
        return true;
    }

    @Override
    public boolean isCanonical() {
        return false;
    }

    @Override
    public boolean isNormalized() {
        return this.parent == null || this.parent.isNormalized() && this.parent.denotesParent();
    }

    @Override
    public Path getNormalizedPath() throws RepositoryException {
        if (this.isNormalized()) {
            return this;
        }
        Path normalized = this.parent.getNormalizedPath();
        if (normalized.denotesParent()) {
            return new ParentPath(normalized);
        }
        if (normalized.denotesCurrent()) {
            return new ParentPath(null);
        }
        return normalized.getAncestor(1);
    }

    @Override
    public Path getCanonicalPath() throws RepositoryException {
        if (this.parent != null) {
            return this.parent.getCanonicalPath().getAncestor(1);
        }
        throw new RepositoryException("There is no canonical representation of ..");
    }

    @Override
    public AbstractPath getLastElement() {
        return PARENT_PATH;
    }

    @Override
    public final boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof Path) {
            Path path = (Path)that;
            return path.denotesParent() && super.equals(that);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return super.hashCode() + 2;
    }
}

