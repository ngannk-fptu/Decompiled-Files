/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;

public final class DefaultNamespaceContext
implements NamespaceContext {
    private static final DefaultNamespaceContext DEFAULT_NAMESPACE_CONTEXT_INSTANCE = new DefaultNamespaceContext();

    private DefaultNamespaceContext() {
    }

    public static DefaultNamespaceContext getInstance() {
        return DEFAULT_NAMESPACE_CONTEXT_INSTANCE;
    }

    @Override
    public String getNamespaceURI(String string) {
        if (string == null) {
            throw new IllegalArgumentException("Prefix cannot be null.");
        }
        if ("xml".equals(string)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if ("xmlns".equals(string)) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return "";
    }

    @Override
    public String getPrefix(String string) {
        if (string == null) {
            throw new IllegalArgumentException("Namespace URI cannot be null.");
        }
        if ("http://www.w3.org/XML/1998/namespace".equals(string)) {
            return "xml";
        }
        if ("http://www.w3.org/2000/xmlns/".equals(string)) {
            return "xmlns";
        }
        return null;
    }

    public Iterator getPrefixes(String string) {
        if (string == null) {
            throw new IllegalArgumentException("Namespace URI cannot be null.");
        }
        if ("http://www.w3.org/XML/1998/namespace".equals(string)) {
            return new Iterator(){
                boolean more = true;

                @Override
                public boolean hasNext() {
                    return this.more;
                }

                public Object next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.more = false;
                    return "xml";
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        if ("http://www.w3.org/2000/xmlns/".equals(string)) {
            return new Iterator(){
                boolean more = true;

                @Override
                public boolean hasNext() {
                    return this.more;
                }

                public Object next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.more = false;
                    return "xmlns";
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return Collections.EMPTY_LIST.iterator();
    }
}

