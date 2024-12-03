/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.packaging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.commons.packaging.ContentPackage;
import org.apache.jackrabbit.commons.predicate.Predicate;

public class FilterContentPackage
implements ContentPackage {
    protected final List<Content> content = new ArrayList<Content>();
    protected boolean includeProperties = false;

    public void addContent(String path, Predicate filterList) {
        this.content.add(new Content(new String[]{path}, filterList));
    }

    public void addContent(String[] paths, Predicate filterList) {
        this.content.add(new Content(paths, filterList));
    }

    @Override
    public Iterator<Item> getItems(Session session) throws RepositoryException {
        return new FilteringIterator(session, new ArrayList<Content>(this.content), this.includeProperties);
    }

    public boolean isIncludeProperties() {
        return this.includeProperties;
    }

    public void setIncludeProperties(boolean includeProperties) {
        this.includeProperties = includeProperties;
    }

    public static class FilteringIterator
    implements Iterator {
        protected final List<Content> content;
        protected Predicate includeFilter;
        protected int contentIndex;
        protected int pathIndex;
        protected Item nextItem;
        protected Node lastNode;
        protected final Session session;
        protected final List<NodeIterator> nodeIteratorStack = new ArrayList<NodeIterator>();
        protected final boolean includeProperties;
        protected PropertyIterator propertyIterator;

        public FilteringIterator(Session session, List<Content> contentList, boolean includeProperties) {
            this.content = contentList;
            this.session = session;
            this.includeProperties = includeProperties;
        }

        @Override
        public boolean hasNext() {
            if (this.nextItem != null) {
                return true;
            }
            try {
                return this.checkForNextNode();
            }
            catch (RepositoryException e) {
                return false;
            }
        }

        protected boolean checkForNextNode() throws RepositoryException {
            if (this.propertyIterator != null) {
                if (this.propertyIterator.hasNext()) {
                    this.nextItem = this.propertyIterator.nextProperty();
                    return true;
                }
                this.propertyIterator = null;
            } else if (this.includeProperties && this.lastNode != null && this.lastNode.hasProperties()) {
                this.propertyIterator = this.lastNode.getProperties();
                this.propertyIterator.hasNext();
                this.nextItem = this.propertyIterator.nextProperty();
                return true;
            }
            if (this.lastNode != null) {
                NodeIterator iter;
                if (this.lastNode.hasNodes()) {
                    iter = this.lastNode.getNodes();
                    this.nodeIteratorStack.add(iter);
                }
                while (this.nodeIteratorStack.size() > 0) {
                    iter = this.nodeIteratorStack.get(this.nodeIteratorStack.size() - 1);
                    if (iter.hasNext()) {
                        do {
                            Node contextNode;
                            if (!this.includeFilter.evaluate(contextNode = iter.nextNode())) continue;
                            this.lastNode = contextNode;
                            this.nextItem = contextNode;
                            return true;
                        } while (iter.hasNext());
                    }
                    this.nodeIteratorStack.remove(iter);
                }
                ++this.pathIndex;
                this.lastNode = null;
            }
            while (this.contentIndex < this.content.size()) {
                Content content = this.content.get(this.contentIndex);
                this.includeFilter = content.filterList;
                while (this.pathIndex < content.paths.length) {
                    String path = content.paths[this.pathIndex];
                    ++this.pathIndex;
                    Node contextNode = (Node)this.session.getItem(path);
                    if (!this.includeFilter.evaluate(contextNode)) continue;
                    this.lastNode = contextNode;
                    this.nextItem = contextNode;
                    return true;
                }
                ++this.contentIndex;
                this.pathIndex = 0;
            }
            return false;
        }

        public Object next() {
            if (this.hasNext()) {
                Item result = this.nextItem;
                this.nextItem = null;
                return result;
            }
            throw new NoSuchElementException("No more elements available");
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported.");
        }
    }

    protected static class Content {
        protected final String[] paths;
        protected final Predicate filterList;

        public Content(String[] paths, Predicate filterList) {
            this.paths = paths;
            this.filterList = filterList;
        }
    }
}

