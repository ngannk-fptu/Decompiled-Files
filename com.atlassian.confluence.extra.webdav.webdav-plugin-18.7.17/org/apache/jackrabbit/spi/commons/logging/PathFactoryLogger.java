/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.logging;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.commons.logging.AbstractLogger;
import org.apache.jackrabbit.spi.commons.logging.LogWriter;

public class PathFactoryLogger
extends AbstractLogger
implements PathFactory {
    private final PathFactory pathFactory;

    public PathFactoryLogger(PathFactory pathFactory, LogWriter writer) {
        super(writer);
        this.pathFactory = pathFactory;
    }

    public PathFactory getPathFactory() {
        return this.pathFactory;
    }

    @Override
    public Path create(final Path parent, final Path relPath, final boolean normalize) throws RepositoryException {
        return (Path)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return PathFactoryLogger.this.pathFactory.create(parent, relPath, normalize);
            }
        }, "create(Path, Path, boolean)", new Object[]{parent, relPath, normalize});
    }

    @Override
    public Path create(final Path parent, final Name name, final boolean normalize) throws RepositoryException {
        return (Path)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return PathFactoryLogger.this.pathFactory.create(parent, name, normalize);
            }
        }, "create(Path, Name, boolean)", new Object[]{parent, name, normalize});
    }

    @Override
    public Path create(final Path parent, final Name name, final int index, final boolean normalize) throws RepositoryException {
        return (Path)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return PathFactoryLogger.this.pathFactory.create(parent, name, index, normalize);
            }
        }, "create(Path, Name, int, boolean)", new Object[]{parent, name, new Integer(index), normalize});
    }

    @Override
    public Path create(final Name name) {
        return (Path)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return PathFactoryLogger.this.pathFactory.create(name);
            }
        }, "create(Name)", new Object[]{name});
    }

    @Override
    public Path create(final Name name, final int index) {
        return (Path)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return PathFactoryLogger.this.pathFactory.create(name, index);
            }
        }, "create(Name, int)", new Object[]{name, new Integer(index)});
    }

    @Override
    public Path create(final Path.Element element) {
        return (Path)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return PathFactoryLogger.this.pathFactory.create(element);
            }
        }, "create(Element)", new Object[]{element});
    }

    @Override
    public Path create(final Path.Element[] elements) {
        return (Path)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return PathFactoryLogger.this.pathFactory.create(elements);
            }
        }, "create(Element[])", new Object[]{elements});
    }

    @Override
    public Path create(final String pathString) {
        return (Path)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return PathFactoryLogger.this.pathFactory.create(pathString);
            }
        }, "create(String)", new Object[]{pathString});
    }

    @Override
    public Path.Element createElement(final Name name) {
        return (Path.Element)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return PathFactoryLogger.this.pathFactory.createElement(name);
            }
        }, "createElement(Name)", new Object[]{name});
    }

    @Override
    public Path.Element createElement(final Name name, final int index) {
        return (Path.Element)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return PathFactoryLogger.this.pathFactory.createElement(name, index);
            }
        }, "createElement(Name)", new Object[]{name, new Integer(index)});
    }

    @Override
    public Path.Element createElement(final String identifier) throws IllegalArgumentException {
        return (Path.Element)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return PathFactoryLogger.this.pathFactory.createElement(identifier);
            }
        }, "createElement(String)", new Object[]{identifier});
    }

    @Override
    public Path.Element getCurrentElement() {
        return (Path.Element)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return PathFactoryLogger.this.pathFactory.getCurrentElement();
            }
        }, "getCurrentElement()", new Object[0]);
    }

    @Override
    public Path.Element getParentElement() {
        return (Path.Element)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return PathFactoryLogger.this.pathFactory.getParentElement();
            }
        }, "getParentElement()", new Object[0]);
    }

    @Override
    public Path.Element getRootElement() {
        return (Path.Element)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return PathFactoryLogger.this.pathFactory.getRootElement();
            }
        }, "getRootElement()", new Object[0]);
    }

    @Override
    public Path getRootPath() {
        return (Path)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return PathFactoryLogger.this.pathFactory.getRootPath();
            }
        }, "getRootPath()", new Object[0]);
    }
}

