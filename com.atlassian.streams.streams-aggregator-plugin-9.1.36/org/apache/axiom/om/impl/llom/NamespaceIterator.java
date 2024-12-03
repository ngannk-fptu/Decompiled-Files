/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;

public class NamespaceIterator
implements Iterator {
    private final Set seenPrefixes = new HashSet();
    private OMElement element;
    private Iterator declaredNamespaces;
    private boolean hasNextCalled;
    private OMNamespace next;

    public NamespaceIterator(OMElement element) {
        this.element = element;
    }

    public boolean hasNext() {
        if (!this.hasNextCalled) {
            block4: {
                while (true) {
                    if (this.declaredNamespaces == null) {
                        this.declaredNamespaces = this.element.getAllDeclaredNamespaces();
                        continue;
                    }
                    if (this.declaredNamespaces.hasNext()) {
                        OMNamespace namespace = (OMNamespace)this.declaredNamespaces.next();
                        if (!this.seenPrefixes.add(namespace.getPrefix()) || namespace.getNamespaceURI().length() <= 0) continue;
                        this.next = namespace;
                        break block4;
                    }
                    this.declaredNamespaces = null;
                    OMContainer parent = this.element.getParent();
                    if (!(parent instanceof OMElement)) break;
                    this.element = (OMElement)parent;
                }
                this.next = null;
            }
            this.hasNextCalled = true;
        }
        return this.next != null;
    }

    public Object next() {
        if (this.hasNext()) {
            OMNamespace result = this.next;
            this.hasNextCalled = false;
            this.next = null;
            return result;
        }
        throw new NoSuchElementException();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}

