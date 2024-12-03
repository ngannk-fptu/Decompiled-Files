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

    public final String getNamespaceURI(String string) {
        if (string == null) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (string.length() > 0) {
            if (string.equals("xml")) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            if (string.equals("xmlns")) {
                return "http://www.w3.org/2000/xmlns/";
            }
        }
        return null;
    }

    public final String getPrefix(String string) {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty URI as argument.");
        }
        if (string.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (string.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        return null;
    }

    public final Iterator getPrefixes(String string) {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (string.equals("http://www.w3.org/XML/1998/namespace")) {
            return new SingletonIterator("xml");
        }
        if (string.equals("http://www.w3.org/2000/xmlns/")) {
            return new SingletonIterator("xmlns");
        }
        return EmptyIterator.getInstance();
    }
}

