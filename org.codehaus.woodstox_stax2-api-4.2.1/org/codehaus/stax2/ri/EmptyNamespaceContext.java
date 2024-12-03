/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import org.codehaus.stax2.ri.EmptyIterator;
import org.codehaus.stax2.ri.SingletonIterator;

public class EmptyNamespaceContext
implements NamespaceContext {
    static final EmptyNamespaceContext sInstance = new EmptyNamespaceContext();

    private EmptyNamespaceContext() {
    }

    public static EmptyNamespaceContext getInstance() {
        return sInstance;
    }

    @Override
    public final String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (prefix.length() > 0) {
            if (prefix.equals("xml")) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            if (prefix.equals("xmlns")) {
                return "http://www.w3.org/2000/xmlns/";
            }
        }
        return null;
    }

    @Override
    public String getPrefix(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty URI as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        return null;
    }

    @Override
    public Iterator<String> getPrefixes(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return SingletonIterator.create("xml");
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return SingletonIterator.create("xmlns");
        }
        return EmptyIterator.getInstance();
    }
}

