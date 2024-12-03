/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.name.AbstractPath;

abstract class RelativePath
extends AbstractPath {
    private static final long serialVersionUID = 5707676677044863127L;
    protected final Path parent;
    private final boolean absolute;
    private final boolean identifier;
    private final int depth;
    private final int length;

    protected RelativePath(Path parent) {
        this.parent = parent;
        if (parent != null) {
            this.absolute = parent.isAbsolute();
            this.identifier = parent.isIdentifierBased();
            this.depth = parent.getDepth() + this.getDepthModifier();
            this.length = parent.getLength() + 1;
        } else {
            this.absolute = false;
            this.identifier = false;
            this.depth = this.getDepthModifier();
            this.length = 1;
        }
    }

    protected abstract int getDepthModifier();

    protected abstract Path getParent() throws RepositoryException;

    protected abstract String getElementString();

    @Override
    public final boolean isIdentifierBased() {
        return this.identifier;
    }

    @Override
    public final boolean isAbsolute() {
        return this.absolute;
    }

    @Override
    public final Path getAncestor(int degree) throws RepositoryException {
        if (degree < 0) {
            throw new IllegalArgumentException("Invalid ancestor degree " + degree);
        }
        if (degree == 0) {
            return this.getNormalizedPath();
        }
        return this.getParent().getAncestor(degree - 1);
    }

    @Override
    public final int getAncestorCount() {
        if (this.absolute) {
            return this.depth;
        }
        return -1;
    }

    @Override
    public final int getDepth() {
        return this.depth;
    }

    @Override
    public final int getLength() {
        return this.length;
    }

    @Override
    public final Path subPath(int from, int to) {
        if (from < 0 || this.length < to || to <= from) {
            throw new IllegalArgumentException(this + ".subPath(" + from + ", " + to + ")");
        }
        if (from == 0 && to == this.length) {
            return this;
        }
        if (to < this.length) {
            return this.parent.subPath(from, to);
        }
        if (from < to - 1) {
            return this.parent.subPath(from, to - 1).resolve(this.getNameElement());
        }
        return this.getLastElement();
    }

    @Override
    public final Path.Element[] getElements() {
        Path.Element[] elements = new Path.Element[this.length];
        Path path = this;
        for (int i = 1; i <= this.length; ++i) {
            elements[this.length - i] = path.getNameElement();
            path = path.getFirstElements();
        }
        return elements;
    }

    @Override
    public Path getFirstElements() {
        return this.parent;
    }

    @Override
    public String getString() {
        if (this.parent != null) {
            return this.parent.getString() + '\t' + this.getElementString();
        }
        return this.getElementString();
    }

    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof RelativePath) {
            RelativePath path = (RelativePath)that;
            if (this.parent != null) {
                return this.parent.equals(path.parent);
            }
            return path.parent == null;
        }
        return false;
    }

    public int hashCode() {
        if (this.parent != null) {
            return this.parent.hashCode();
        }
        return 17;
    }
}

