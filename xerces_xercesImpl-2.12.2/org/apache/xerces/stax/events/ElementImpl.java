/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.Namespace;
import org.apache.xerces.stax.events.XMLEventImpl;

abstract class ElementImpl
extends XMLEventImpl {
    private final QName fName;
    private final List fNamespaces;

    ElementImpl(QName qName, boolean bl, Iterator iterator, Location location) {
        super(bl ? 1 : 2, location);
        this.fName = qName;
        if (iterator != null && iterator.hasNext()) {
            this.fNamespaces = new ArrayList();
            do {
                Namespace namespace = (Namespace)iterator.next();
                this.fNamespaces.add(namespace);
            } while (iterator.hasNext());
        } else {
            this.fNamespaces = Collections.EMPTY_LIST;
        }
    }

    public final QName getName() {
        return this.fName;
    }

    public final Iterator getNamespaces() {
        return ElementImpl.createImmutableIterator(this.fNamespaces.iterator());
    }

    static Iterator createImmutableIterator(Iterator iterator) {
        return new NoRemoveIterator(iterator);
    }

    private static final class NoRemoveIterator
    implements Iterator {
        private final Iterator fWrapped;

        public NoRemoveIterator(Iterator iterator) {
            this.fWrapped = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.fWrapped.hasNext();
        }

        public Object next() {
            return this.fWrapped.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Attributes iterator is read-only.");
        }
    }
}

