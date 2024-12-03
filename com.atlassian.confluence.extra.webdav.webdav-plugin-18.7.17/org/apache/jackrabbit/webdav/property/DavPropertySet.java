/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.property;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyIterator;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.PropContainer;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DavPropertySet
extends PropContainer
implements Iterable<DavProperty<?>> {
    private static Logger log = LoggerFactory.getLogger(DavPropertySet.class);
    private final Map<DavPropertyName, DavProperty<?>> map = new HashMap();

    public DavProperty<?> add(DavProperty<?> property) {
        return this.map.put(property.getName(), property);
    }

    public void addAll(DavPropertySet pset) {
        this.map.putAll(pset.map);
    }

    public DavProperty<?> get(String name) {
        return this.get(DavPropertyName.create(name));
    }

    public DavProperty<?> get(String name, Namespace namespace) {
        return this.get(DavPropertyName.create(name, namespace));
    }

    public DavProperty<?> get(DavPropertyName name) {
        return this.map.get(name);
    }

    public DavProperty<?> remove(DavPropertyName name) {
        return this.map.remove(name);
    }

    public DavProperty<?> remove(String name) {
        return this.remove(DavPropertyName.create(name));
    }

    public DavProperty<?> remove(String name, Namespace namespace) {
        return this.remove(DavPropertyName.create(name, namespace));
    }

    public DavPropertyIterator iterator() {
        return new PropIter();
    }

    public DavPropertyIterator iterator(Namespace namespace) {
        return new PropIter(namespace);
    }

    public DavPropertyName[] getPropertyNames() {
        return this.map.keySet().toArray(new DavPropertyName[this.map.keySet().size()]);
    }

    @Override
    public boolean contains(DavPropertyName name) {
        return this.map.containsKey(name);
    }

    @Override
    public boolean addContent(PropEntry contentEntry) {
        if (contentEntry instanceof DavProperty) {
            this.add((DavProperty)contentEntry);
            return true;
        }
        log.debug("DavProperty object expected. Found: " + contentEntry.getClass().toString());
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public int getContentSize() {
        return this.map.size();
    }

    @Override
    public Collection<? extends PropEntry> getContent() {
        return this.map.values();
    }

    private class PropIter
    implements DavPropertyIterator {
        private final Namespace namespace;
        private final Iterator<DavProperty<?>> iterator;
        private DavProperty<?> next;

        private PropIter() {
            this((Namespace)null);
        }

        private PropIter(Namespace namespace) {
            this.namespace = namespace;
            this.iterator = DavPropertySet.this.map.values().iterator();
            this.seek();
        }

        @Override
        public DavProperty<?> nextProperty() throws NoSuchElementException {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            DavProperty<?> ret = this.next;
            this.seek();
            return ret;
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public DavProperty<?> next() {
            return this.nextProperty();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void seek() {
            while (this.iterator.hasNext()) {
                this.next = this.iterator.next();
                if (this.namespace != null && !this.namespace.equals(this.next.getName().getNamespace())) continue;
                return;
            }
            this.next = null;
        }
    }
}

