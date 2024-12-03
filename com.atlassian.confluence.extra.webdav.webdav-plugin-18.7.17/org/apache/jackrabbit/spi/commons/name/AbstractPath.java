/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.name.CurrentPath;
import org.apache.jackrabbit.spi.commons.name.IdentifierPath;
import org.apache.jackrabbit.spi.commons.name.NamePath;
import org.apache.jackrabbit.spi.commons.name.ParentPath;
import org.apache.jackrabbit.spi.commons.name.RootPath;

abstract class AbstractPath
implements Path,
Path.Element {
    private static final long serialVersionUID = 3018771833963770499L;

    AbstractPath() {
    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public int getNormalizedIndex() {
        return 1;
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public boolean denotesRoot() {
        return false;
    }

    @Override
    public boolean denotesIdentifier() {
        return false;
    }

    @Override
    public boolean denotesParent() {
        return false;
    }

    @Override
    public boolean denotesCurrent() {
        return false;
    }

    @Override
    public boolean denotesName() {
        return false;
    }

    @Override
    public Path.Element getNameElement() {
        return this.getLastElement();
    }

    @Override
    public AbstractPath getLastElement() {
        return this;
    }

    @Override
    public Path getFirstElements() {
        return null;
    }

    @Override
    public final Path resolve(Path.Element element) {
        if (element.denotesName()) {
            return new NamePath(this, element.getName(), element.getIndex());
        }
        if (element.denotesParent()) {
            if (this.isAbsolute() && this.getDepth() == 0) {
                throw new IllegalArgumentException("An absolute paths with negative depth is not allowed");
            }
            return new ParentPath(this);
        }
        if (element.denotesCurrent()) {
            return new CurrentPath(this);
        }
        if (element.denotesRoot()) {
            return RootPath.ROOT_PATH;
        }
        if (element.denotesIdentifier()) {
            return new IdentifierPath(element.getIdentifier());
        }
        throw new IllegalArgumentException("Unknown path element type: " + element);
    }

    @Override
    public final Path resolve(Path relative) {
        if (relative.isAbsolute()) {
            return relative;
        }
        if (relative.getLength() > 1) {
            Path first = relative.getFirstElements();
            Path last = relative.getLastElement();
            return this.resolve(first).resolve(last);
        }
        if (relative.denotesCurrent()) {
            return new CurrentPath(this);
        }
        if (relative.denotesParent()) {
            return new ParentPath(this);
        }
        if (relative.denotesName()) {
            return new NamePath(this, relative.getName(), relative.getIndex());
        }
        throw new IllegalArgumentException("Unknown path type: " + relative);
    }

    @Override
    public final Path computeRelativePath(Path other) throws RepositoryException {
        if (other != null && this.isAbsolute() && other.isAbsolute()) {
            Path.Element[] a = this.getElements();
            Path.Element[] b = other.getElements();
            if (a.length > 0 && b.length > 0 && a[0].equals(b[0])) {
                int ai = 1;
                int bi = 1;
                while (ai < a.length && bi < b.length) {
                    if (a[ai].equals(b[bi])) {
                        ++ai;
                        ++bi;
                        continue;
                    }
                    if (a[ai].denotesCurrent()) {
                        ++ai;
                        continue;
                    }
                    if (!b[bi].denotesCurrent()) break;
                    ++bi;
                }
                Path path = null;
                while (ai < a.length) {
                    if (a[ai].denotesName()) {
                        path = new ParentPath(path);
                        ++ai;
                        continue;
                    }
                    if (a[ai].denotesCurrent()) {
                        ++ai;
                        continue;
                    }
                    throw new RepositoryException("Unexpected path element: " + a[ai]);
                }
                if (path == null) {
                    path = new CurrentPath(null);
                }
                while (bi < b.length) {
                    path = path.resolve(b[bi++]);
                }
                return path;
            }
        }
        throw new RepositoryException("No relative path from " + this + " to " + other);
    }

    @Override
    public final boolean isEquivalentTo(Path other) throws IllegalArgumentException, RepositoryException {
        if (other != null) {
            return this.getNormalizedPath().equals(other.getNormalizedPath());
        }
        throw new IllegalArgumentException(this + ".isEquivalentTo(" + other + ")");
    }

    @Override
    public final boolean isAncestorOf(Path other) throws IllegalArgumentException, RepositoryException {
        if (other != null && this.isAbsolute() == other.isAbsolute() && this.isIdentifierBased() == other.isIdentifierBased()) {
            int d = other.getDepth() - this.getDepth();
            return d > 0 && this.isEquivalentTo(other.getAncestor(d));
        }
        throw new IllegalArgumentException(this + ".isAncestorOf(" + other + ")");
    }

    @Override
    public final boolean isDescendantOf(Path other) throws IllegalArgumentException, RepositoryException {
        if (other != null && this.isAbsolute() == other.isAbsolute() && this.isIdentifierBased() == other.isIdentifierBased()) {
            int d = this.getDepth() - other.getDepth();
            return d > 0 && this.getAncestor(d).isEquivalentTo(other);
        }
        throw new IllegalArgumentException(this + ".isDescendantOf(" + other + ")");
    }

    public final String toString() {
        return this.getString();
    }
}

