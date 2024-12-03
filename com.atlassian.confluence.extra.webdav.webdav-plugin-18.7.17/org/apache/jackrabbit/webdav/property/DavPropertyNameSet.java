/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.property;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameIterator;
import org.apache.jackrabbit.webdav.property.PropContainer;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class DavPropertyNameSet
extends PropContainer
implements Iterable<DavPropertyName> {
    private static Logger log = LoggerFactory.getLogger(DavPropertyNameSet.class);
    private final Set<DavPropertyName> set = new HashSet<DavPropertyName>();

    public DavPropertyNameSet() {
    }

    public DavPropertyNameSet(DavPropertyNameSet initialSet) {
        this.addAll(initialSet);
    }

    public DavPropertyNameSet(Element propElement) {
        if (!DomUtil.matches(propElement, "prop", NAMESPACE)) {
            throw new IllegalArgumentException("'DAV:prop' element expected.");
        }
        ElementIterator it = DomUtil.getChildren(propElement);
        while (it.hasNext()) {
            this.add(DavPropertyName.createFromXml(it.nextElement()));
        }
    }

    public boolean add(DavPropertyName propertyName) {
        return this.set.add(propertyName);
    }

    public boolean add(String localName, Namespace namespace) {
        return this.set.add(DavPropertyName.create(localName, namespace));
    }

    public boolean addAll(DavPropertyNameSet propertyNames) {
        return this.set.addAll(propertyNames.set);
    }

    public boolean remove(DavPropertyName propertyName) {
        return this.set.remove(propertyName);
    }

    public DavPropertyNameIterator iterator() {
        return new PropertyNameIterator();
    }

    @Override
    public boolean contains(DavPropertyName name) {
        return this.set.contains(name);
    }

    @Override
    public boolean addContent(PropEntry contentEntry) {
        if (contentEntry instanceof DavPropertyName) {
            return this.add((DavPropertyName)contentEntry);
        }
        log.debug("DavPropertyName object expected. Found: " + contentEntry.getClass().toString());
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    @Override
    public int getContentSize() {
        return this.set.size();
    }

    public Collection<DavPropertyName> getContent() {
        return this.set;
    }

    private class PropertyNameIterator
    implements DavPropertyNameIterator {
        private Iterator<DavPropertyName> iter;

        private PropertyNameIterator() {
            this.iter = DavPropertyNameSet.this.set.iterator();
        }

        @Override
        public DavPropertyName nextPropertyName() {
            return this.iter.next();
        }

        @Override
        public void remove() {
            this.iter.remove();
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public DavPropertyName next() {
            return this.iter.next();
        }
    }
}

