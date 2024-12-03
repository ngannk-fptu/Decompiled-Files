/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.commons.conversion.MalformedPathException;
import org.apache.jackrabbit.spi.commons.name.NamePath;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.RootPath;

public final class PathBuilder {
    private final PathFactory factory;
    private Path path = null;

    public PathBuilder() {
        this(PathFactoryImpl.getInstance());
    }

    public PathBuilder(PathFactory factory) {
        this.factory = factory;
    }

    public PathBuilder(Path.Element[] elements) {
        this();
        this.path = this.factory.create(elements);
    }

    public PathBuilder(Path parent) {
        this();
        this.path = parent;
    }

    public void addRoot() {
        this.path = this.path != null ? RootPath.ROOT_PATH.resolve(this.path) : RootPath.ROOT_PATH;
    }

    public void addAll(Path.Element[] elements) {
        for (Path.Element element : elements) {
            this.path = this.path != null ? this.path.resolve(element) : this.factory.create(element);
        }
    }

    public void addFirst(Path.Element elem) {
        Path first = this.factory.create(elem);
        this.path = this.path != null ? first.resolve(this.path) : first;
    }

    public void addFirst(Name name) {
        this.addFirst(this.factory.createElement(name));
    }

    public void addFirst(Name name, int index) {
        this.addFirst(this.factory.createElement(name, index));
    }

    public void addLast(Path.Element elem) {
        this.path = this.path != null ? this.path.resolve(elem) : this.factory.create(elem);
    }

    public void addLast(Name name) {
        this.addLast(name, 0);
    }

    public void addLast(Name name, int index) {
        this.path = new NamePath(this.path, name, index);
    }

    public Path getPath() throws MalformedPathException {
        if (this.path != null) {
            return this.path;
        }
        throw new MalformedPathException("empty path");
    }
}

