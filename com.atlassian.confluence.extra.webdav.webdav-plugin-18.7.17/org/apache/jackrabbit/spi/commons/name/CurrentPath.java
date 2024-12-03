/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.name.AbstractPath;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.ParentPath;
import org.apache.jackrabbit.spi.commons.name.RelativePath;

final class CurrentPath
extends RelativePath {
    private static final long serialVersionUID = 1729196441091297231L;
    public static final CurrentPath CURRENT_PATH = new CurrentPath(null);
    public static final Name NAME = NameFactoryImpl.getInstance().create("", ".");

    public CurrentPath(Path parent) {
        super(parent);
    }

    @Override
    protected int getDepthModifier() {
        return 0;
    }

    @Override
    protected Path getParent() throws RepositoryException {
        if (this.parent != null) {
            return this.parent.getAncestor(1);
        }
        return new ParentPath(null);
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
    public boolean denotesCurrent() {
        return true;
    }

    @Override
    public boolean isCanonical() {
        return false;
    }

    @Override
    public boolean isNormalized() {
        return this.parent == null;
    }

    @Override
    public Path getNormalizedPath() throws RepositoryException {
        if (this.parent != null) {
            return this.parent.getNormalizedPath();
        }
        return this;
    }

    @Override
    public Path getCanonicalPath() throws RepositoryException {
        if (this.parent != null) {
            return this.parent.getCanonicalPath();
        }
        throw new RepositoryException("There is no canonical representation of .");
    }

    @Override
    public AbstractPath getLastElement() {
        return CURRENT_PATH;
    }

    @Override
    public final boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof Path) {
            Path path = (Path)that;
            return path.denotesCurrent() && super.equals(that);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return super.hashCode() + 1;
    }
}

