/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.commons.conversion.MalformedPathException;
import org.apache.jackrabbit.spi.commons.name.PathBuilder;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;

public class PathMap<T> {
    private static final PathFactory PATH_FACTORY = PathFactoryImpl.getInstance();
    private final Element<T> root = new Element(PATH_FACTORY.getRootElement());

    public Element<T> map(Path path, boolean exact) {
        Path.Element[] elements = path.getElements();
        Element current = this.root;
        for (int i = 1; i < elements.length; ++i) {
            Element next = current.getChild(elements[i]);
            if (next == null) {
                if (!exact) break;
                return null;
            }
            current = next;
        }
        return current;
    }

    public Element<T> put(Path path, T obj) {
        Element<T> element = this.put(path);
        ((Element)element).obj = obj;
        return element;
    }

    public void put(Path path, Element<T> element) {
        Path.Element[] elements = path.getElements();
        Element current = this.root;
        for (int i = 1; i < elements.length - 1; ++i) {
            Element next = current.getChild(elements[i]);
            if (next == null) {
                next = current.createChild(elements[i]);
            }
            current = next;
        }
        current.put(path.getNameElement(), element);
    }

    public Element<T> put(Path path) {
        Path.Element[] elements = path.getElements();
        Element current = this.root;
        for (int i = 1; i < elements.length; ++i) {
            Element next = current.getChild(elements[i]);
            if (next == null) {
                next = current.createChild(elements[i]);
            }
            current = next;
        }
        return current;
    }

    public void traverse(ElementVisitor<T> visitor, boolean includeEmpty) {
        this.root.traverse(visitor, includeEmpty);
    }

    public static interface ElementVisitor<T> {
        public void elementVisited(Element<T> var1);
    }

    public static final class Element<T> {
        private Element<T> parent;
        private Map<Name, List<Element<T>>> children;
        private int childrenCount;
        private T obj;
        private Path.Element pathElement;
        private int index;

        private Element(Path.Element nameIndex) {
            this.index = nameIndex.getIndex();
            if (nameIndex.denotesName()) {
                this.updatePathElement(nameIndex.getName(), this.index);
            } else {
                this.pathElement = nameIndex;
            }
        }

        private Element<T> createChild(Path.Element nameIndex) {
            Element<T> element = new Element<T>(nameIndex);
            this.put(nameIndex, element);
            return element;
        }

        private void updatePathElement(Name name, int index) {
            this.pathElement = index == 1 ? PATH_FACTORY.createElement(name) : PATH_FACTORY.createElement(name, index);
        }

        public void insert(Path.Element nameIndex) {
            List<Element<T>> list;
            int index = Element.getZeroBasedIndex(nameIndex);
            if (this.children != null && (list = this.children.get(nameIndex.getName())) != null && list.size() > index) {
                for (int i = index; i < list.size(); ++i) {
                    Element<T> element = list.get(i);
                    if (element == null) continue;
                    element.index = element.getNormalizedIndex() + 1;
                    super.updatePathElement(element.getName(), element.index);
                }
                list.add(index, null);
            }
        }

        private Element<T> getChild(Path.Element nameIndex) {
            List<Element<T>> list;
            int index = Element.getZeroBasedIndex(nameIndex);
            Element<T> element = null;
            if (this.children != null && (list = this.children.get(nameIndex.getName())) != null && list.size() > index) {
                element = list.get(index);
            }
            return element;
        }

        public void put(Path.Element nameIndex, Element<T> element) {
            List<Element<T>> list;
            int index = Element.getZeroBasedIndex(nameIndex);
            if (this.children == null) {
                this.children = new HashMap<Name, List<Element<T>>>();
            }
            if ((list = this.children.get(nameIndex.getName())) == null) {
                list = new ArrayList<Element<T>>();
                this.children.put(nameIndex.getName(), list);
            }
            while (list.size() < index) {
                list.add(null);
            }
            if (list.size() == index) {
                list.add(element);
            } else {
                list.set(index, element);
            }
            element.parent = this;
            element.index = nameIndex.getIndex();
            super.updatePathElement(nameIndex.getName(), element.index);
            ++this.childrenCount;
        }

        public Element<T> remove(Path.Element nameIndex) {
            return this.remove(nameIndex, true, true);
        }

        private Element<T> remove(Path.Element nameIndex, boolean shift, boolean removeIfEmpty) {
            int index = Element.getZeroBasedIndex(nameIndex);
            if (this.children == null) {
                return null;
            }
            List<Element<T>> list = this.children.get(nameIndex.getName());
            if (list == null || list.size() <= index) {
                return null;
            }
            Element element = list.set(index, null);
            if (shift) {
                for (int i = index + 1; i < list.size(); ++i) {
                    Element<T> sibling = list.get(i);
                    if (sibling == null) continue;
                    --sibling.index;
                    super.updatePathElement(sibling.getName(), sibling.index);
                }
                list.remove(index);
            }
            if (element != null) {
                element.parent = null;
                --this.childrenCount;
            }
            if (removeIfEmpty && this.childrenCount == 0 && this.obj == null && this.parent != null) {
                super.remove(this.getPathElement(), shift, true);
            }
            return element;
        }

        public void remove() {
            this.remove(true);
        }

        public void remove(boolean shift) {
            if (this.parent != null) {
                super.remove(this.getPathElement(), shift, true);
            } else {
                this.children = null;
                this.childrenCount = 0;
                this.obj = null;
            }
        }

        public void removeAll() {
            this.children = null;
            this.childrenCount = 0;
            if (this.obj == null && this.parent != null) {
                super.remove(this.getPathElement(), false, true);
            }
        }

        public void setChildren(Map<Path.Element, Element<T>> children) {
            this.children = null;
            this.childrenCount = 0;
            for (Map.Entry<Path.Element, Element<T>> entry : children.entrySet()) {
                this.put(entry.getKey(), entry.getValue());
            }
            if (this.childrenCount == 0 && this.obj == null && this.parent != null) {
                super.remove(this.getPathElement(), false, true);
            }
        }

        public T get() {
            return this.obj;
        }

        public void set(T obj) {
            this.obj = obj;
            if (obj == null && this.childrenCount == 0 && this.parent != null) {
                super.remove(this.getPathElement(), false, true);
            }
        }

        public Name getName() {
            return this.pathElement.getName();
        }

        public int getIndex() {
            return this.index;
        }

        public int getNormalizedIndex() {
            return this.pathElement.getNormalizedIndex();
        }

        public Path.Element getPathElement() {
            if (this.index < 1) {
                return PATH_FACTORY.createElement(this.getName());
            }
            return PATH_FACTORY.createElement(this.getName(), this.index);
        }

        public Path getPath() throws MalformedPathException {
            if (this.parent == null) {
                return PATH_FACTORY.getRootPath();
            }
            PathBuilder builder = new PathBuilder();
            this.getPath(builder);
            return builder.getPath();
        }

        private void getPath(PathBuilder builder) {
            if (this.parent == null) {
                builder.addRoot();
                return;
            }
            super.getPath(builder);
            builder.addLast(this.pathElement);
        }

        public boolean hasPath(Path path) {
            return this.hasPath(path.getElements(), path.getLength());
        }

        private boolean hasPath(Path.Element[] elements, int len) {
            if (this.getPathElement().equals(elements[len - 1])) {
                if (this.parent != null) {
                    return super.hasPath(elements, len - 1);
                }
                return true;
            }
            return false;
        }

        private static int getZeroBasedIndex(Path.Element nameIndex) {
            return nameIndex.getNormalizedIndex() - 1;
        }

        public void traverse(ElementVisitor<T> visitor, boolean includeEmpty) {
            if (includeEmpty || this.obj != null) {
                visitor.elementVisited(this);
            }
            if (this.children != null) {
                for (List<Element<T>> list : this.children.values()) {
                    for (Element<T> element : list) {
                        if (element == null) continue;
                        element.traverse(visitor, includeEmpty);
                    }
                }
            }
        }

        public int getDepth() {
            if (this.parent != null) {
                return this.parent.getDepth() + 1;
            }
            return 0;
        }

        public boolean isAncestorOf(Element<T> other) {
            Element<T> parent = other.parent;
            while (parent != null) {
                if (parent == this) {
                    return true;
                }
                parent = parent.parent;
            }
            return false;
        }

        public Element<T> getParent() {
            return this.parent;
        }

        public int getChildrenCount() {
            return this.childrenCount;
        }

        public List<Element<T>> getChildren() {
            ArrayList<Element<T>> result = new ArrayList<Element<T>>();
            if (this.children != null) {
                for (List<Element<T>> list : this.children.values()) {
                    for (Element<T> element : list) {
                        if (element == null) continue;
                        result.add(element);
                    }
                }
            }
            return result;
        }

        public Element<T> getDescendant(Path relPath, boolean exact) {
            Path.Element[] elements = relPath.getElements();
            Element<T> current = this;
            for (int i = 0; i < elements.length; ++i) {
                Element<T> next = current.getChild(elements[i]);
                if (next == null) {
                    if (!exact) break;
                    return null;
                }
                current = next;
            }
            return current;
        }
    }
}

