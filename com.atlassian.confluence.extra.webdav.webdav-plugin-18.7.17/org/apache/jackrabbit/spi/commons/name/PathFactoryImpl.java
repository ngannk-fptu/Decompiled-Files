/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

import java.util.ArrayList;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.commons.name.AbstractPath;
import org.apache.jackrabbit.spi.commons.name.CurrentPath;
import org.apache.jackrabbit.spi.commons.name.IdentifierPath;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.NamePath;
import org.apache.jackrabbit.spi.commons.name.ParentPath;
import org.apache.jackrabbit.spi.commons.name.RootPath;

public class PathFactoryImpl
implements PathFactory {
    private static PathFactory FACTORY = new PathFactoryImpl();

    private PathFactoryImpl() {
    }

    public static PathFactory getInstance() {
        return FACTORY;
    }

    @Override
    public Path create(Path parent, Path relPath, boolean normalize) throws IllegalArgumentException, RepositoryException {
        if (relPath.isAbsolute()) {
            throw new IllegalArgumentException("relPath is not a relative path: " + relPath);
        }
        Path path = parent.resolve(relPath);
        if (normalize) {
            return path.getNormalizedPath();
        }
        return path;
    }

    @Override
    public Path create(Path parent, Name name, boolean normalize) throws RepositoryException {
        return this.create(parent, name, 0, normalize);
    }

    @Override
    public Path create(Path parent, Name name, int index, boolean normalize) throws IllegalArgumentException, RepositoryException {
        if (RootPath.NAME.equals(name)) {
            throw new IllegalArgumentException();
        }
        NamePath path = new NamePath(parent, name, index);
        if (normalize) {
            return path.getNormalizedPath();
        }
        return path;
    }

    @Override
    public Path create(Name name) throws IllegalArgumentException {
        if (name != null) {
            return this.create(name, 0);
        }
        throw new IllegalArgumentException("PathFactory.create(null)");
    }

    @Override
    public Path create(Name name, int index) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("PathFactory.create(null, index");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Index must not be negative: " + name + "[" + index + "]");
        }
        if (CurrentPath.NAME.equals(name)) {
            if (index == 0) {
                return CurrentPath.CURRENT_PATH;
            }
            throw new IllegalArgumentException();
        }
        if (ParentPath.NAME.equals(name)) {
            if (index == 0) {
                return ParentPath.PARENT_PATH;
            }
            throw new IllegalArgumentException();
        }
        if (RootPath.NAME.equals(name)) {
            if (index == 0) {
                return RootPath.ROOT_PATH;
            }
            throw new IllegalArgumentException();
        }
        return new NamePath(null, name, index);
    }

    @Override
    public Path create(Path.Element element) {
        if (element.denotesCurrent()) {
            return CurrentPath.CURRENT_PATH;
        }
        if (element.denotesIdentifier()) {
            return new IdentifierPath(element.getIdentifier());
        }
        if (element.denotesName()) {
            return new NamePath(null, element.getName(), element.getIndex());
        }
        if (element.denotesParent()) {
            return ParentPath.PARENT_PATH;
        }
        if (element.denotesRoot()) {
            return RootPath.ROOT_PATH;
        }
        throw new IllegalArgumentException("Unknown path element type: " + element);
    }

    @Override
    public Path create(Path.Element[] elements) throws IllegalArgumentException {
        AbstractPath path = null;
        for (Path.Element element : elements) {
            if (element.denotesCurrent()) {
                path = new CurrentPath(path);
                continue;
            }
            if (element.denotesIdentifier()) {
                if (path != null) {
                    throw new IllegalArgumentException();
                }
                path = new IdentifierPath(element.getIdentifier());
                continue;
            }
            if (element.denotesName()) {
                path = new NamePath(path, element.getName(), element.getIndex());
                continue;
            }
            if (element.denotesParent()) {
                if (path != null && path.isAbsolute() && path.getDepth() == 0) {
                    throw new IllegalArgumentException();
                }
                path = new ParentPath(path);
                continue;
            }
            if (!element.denotesRoot()) continue;
            if (path != null) {
                throw new IllegalArgumentException();
            }
            path = RootPath.ROOT_PATH;
        }
        return path;
    }

    @Override
    public Path create(String pathString) throws IllegalArgumentException {
        if (pathString == null || "".equals(pathString)) {
            throw new IllegalArgumentException("No Path literal specified");
        }
        int lastPos = 0;
        int pos = pathString.indexOf(9);
        ArrayList<Path.Element> list = new ArrayList<Path.Element>();
        while (lastPos >= 0) {
            Path.Element elem;
            if (pos >= 0) {
                elem = this.createElementFromString(pathString.substring(lastPos, pos));
                lastPos = pos + 1;
                pos = pathString.indexOf(9, lastPos);
            } else {
                elem = this.createElementFromString(pathString.substring(lastPos));
                lastPos = -1;
            }
            list.add(elem);
        }
        return this.create(list.toArray(new Path.Element[list.size()]));
    }

    @Override
    public Path.Element createElement(Name name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (name.equals(ParentPath.NAME)) {
            return ParentPath.PARENT_PATH;
        }
        if (name.equals(CurrentPath.NAME)) {
            return CurrentPath.CURRENT_PATH;
        }
        if (name.equals(RootPath.NAME)) {
            return RootPath.ROOT_PATH;
        }
        return new NamePath(null, name, 0);
    }

    @Override
    public Path.Element createElement(Name name, int index) throws IllegalArgumentException {
        if (index < 0) {
            throw new IllegalArgumentException("The index may not be negative: " + name + "[" + index + "]");
        }
        if (name == null) {
            throw new IllegalArgumentException("The name must not be null");
        }
        if (name.equals(ParentPath.NAME) || name.equals(CurrentPath.NAME) || name.equals(RootPath.NAME)) {
            throw new IllegalArgumentException("Special path elements (root, '.' and '..') can not have an explicit index: " + name + "[" + index + "]");
        }
        return new NamePath(null, name, index);
    }

    @Override
    public Path.Element createElement(String identifier) throws IllegalArgumentException {
        if (identifier == null) {
            throw new IllegalArgumentException("The id must not be null.");
        }
        return new IdentifierPath(identifier);
    }

    private Path.Element createElementFromString(String elementString) {
        if (elementString == null) {
            throw new IllegalArgumentException("null PathElement literal");
        }
        if (elementString.equals(RootPath.NAME.toString())) {
            return RootPath.ROOT_PATH;
        }
        if (elementString.equals(CurrentPath.CURRENT_PATH.getString())) {
            return CurrentPath.CURRENT_PATH;
        }
        if (elementString.equals(ParentPath.PARENT_PATH.getString())) {
            return ParentPath.PARENT_PATH;
        }
        if (elementString.startsWith("[") && elementString.endsWith("]") && elementString.length() > 2) {
            return new IdentifierPath(elementString.substring(1, elementString.length() - 1));
        }
        NameFactory factory = NameFactoryImpl.getInstance();
        int pos = elementString.indexOf(91);
        if (pos == -1) {
            Name name = factory.create(elementString);
            return new NamePath(null, name, 0);
        }
        Name name = factory.create(elementString.substring(0, pos));
        int pos1 = elementString.indexOf(93);
        if (pos1 == -1) {
            throw new IllegalArgumentException("invalid PathElement literal: " + elementString + " (missing ']')");
        }
        try {
            int index = Integer.valueOf(elementString.substring(pos + 1, pos1));
            if (index < 1) {
                throw new IllegalArgumentException("invalid PathElement literal: " + elementString + " (index is 1-based)");
            }
            return new NamePath(null, name, index);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid PathElement literal: " + elementString + " (" + e.getMessage() + ")");
        }
    }

    @Override
    public Path.Element getCurrentElement() {
        return CurrentPath.CURRENT_PATH;
    }

    @Override
    public Path.Element getParentElement() {
        return ParentPath.PARENT_PATH;
    }

    @Override
    public Path.Element getRootElement() {
        return RootPath.ROOT_PATH;
    }

    @Override
    public Path getRootPath() {
        return RootPath.ROOT_PATH;
    }
}

