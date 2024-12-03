/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.name.AbstractPath;

final class IdentifierPath
extends AbstractPath {
    private static final long serialVersionUID = 1602959709588338642L;
    private final String identifier;

    public IdentifierPath(String identifier) {
        assert (identifier != null);
        this.identifier = identifier;
    }

    @Override
    public Name getName() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public boolean denotesIdentifier() {
        return true;
    }

    @Override
    public boolean isIdentifierBased() {
        return true;
    }

    @Override
    public boolean isAbsolute() {
        return true;
    }

    @Override
    public boolean isCanonical() {
        return true;
    }

    @Override
    public boolean isNormalized() {
        return false;
    }

    @Override
    public Path getNormalizedPath() throws RepositoryException {
        throw new RepositoryException("Cannot normalize the identifier-based path " + this);
    }

    @Override
    public Path getCanonicalPath() {
        return this;
    }

    @Override
    public Path getAncestor(int degree) throws IllegalArgumentException, RepositoryException {
        if (degree < 0) {
            throw new IllegalArgumentException(this + ".getAncestor(" + degree + ")");
        }
        if (degree > 0) {
            throw new RepositoryException("Cannot construct ancestor path from an identifier");
        }
        return this;
    }

    @Override
    public int getAncestorCount() {
        return 0;
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public int getDepth() {
        return 0;
    }

    @Override
    public Path subPath(int from, int to) throws IllegalArgumentException {
        if (from == 0 && to == 1) {
            return this;
        }
        throw new IllegalArgumentException(this + ".subPath(" + from + ", " + to + ")");
    }

    @Override
    public Path.Element[] getElements() {
        return new Path.Element[]{this.getNameElement()};
    }

    @Override
    public String getString() {
        return "[" + this.identifier + "]";
    }

    public final boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof Path) {
            Path path = (Path)that;
            return path.denotesIdentifier() && this.identifier.equals(path.getIdentifier());
        }
        return false;
    }

    public final int hashCode() {
        return this.identifier.hashCode();
    }
}

