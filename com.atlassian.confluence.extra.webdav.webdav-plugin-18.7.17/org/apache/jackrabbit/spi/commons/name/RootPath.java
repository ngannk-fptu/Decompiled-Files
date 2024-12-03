/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

import javax.jcr.PathNotFoundException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.name.AbstractPath;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;

final class RootPath
extends AbstractPath {
    public static final RootPath ROOT_PATH = new RootPath();
    private static final long serialVersionUID = 8621451607549214925L;
    public static final Name NAME = NameFactoryImpl.getInstance().create("", "");

    private RootPath() {
    }

    @Override
    public Name getName() {
        return NAME;
    }

    @Override
    public boolean denotesRoot() {
        return true;
    }

    @Override
    public boolean isIdentifierBased() {
        return false;
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
        return true;
    }

    @Override
    public Path getNormalizedPath() {
        return this;
    }

    @Override
    public Path getCanonicalPath() {
        return this;
    }

    @Override
    public Path getAncestor(int degree) throws IllegalArgumentException, PathNotFoundException {
        if (degree < 0) {
            throw new IllegalArgumentException("/.getAncestor(" + degree + ")");
        }
        if (degree > 0) {
            throw new PathNotFoundException("/.getAncestor(" + degree + ")");
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
        throw new IllegalArgumentException("/.subPath(" + from + ", " + to + ")");
    }

    @Override
    public Path.Element[] getElements() {
        return new Path.Element[]{ROOT_PATH};
    }

    @Override
    public Path.Element getNameElement() {
        return ROOT_PATH;
    }

    @Override
    public String getString() {
        return "{}";
    }

    public Object readResolve() {
        return ROOT_PATH;
    }
}

