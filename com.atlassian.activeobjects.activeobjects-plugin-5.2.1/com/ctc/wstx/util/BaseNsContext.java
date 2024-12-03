/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import com.ctc.wstx.cfg.ErrorConsts;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.ri.SingletonIterator;

public abstract class BaseNsContext
implements NamespaceContext {
    protected static final String UNDECLARED_NS_URI = "";

    public final String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException(ErrorConsts.ERR_NULL_ARG);
        }
        if (prefix.length() > 0) {
            if (prefix.equals("xml")) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            if (prefix.equals("xmlns")) {
                return "http://www.w3.org/2000/xmlns/";
            }
        }
        return this.doGetNamespaceURI(prefix);
    }

    public final String getPrefix(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        return this.doGetPrefix(nsURI);
    }

    public final Iterator getPrefixes(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return new SingletonIterator("xml");
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return new SingletonIterator("xmlns");
        }
        return this.doGetPrefixes(nsURI);
    }

    public abstract Iterator getNamespaces();

    public abstract void outputNamespaceDeclarations(Writer var1) throws IOException;

    public abstract void outputNamespaceDeclarations(XMLStreamWriter var1) throws XMLStreamException;

    public abstract String doGetNamespaceURI(String var1);

    public abstract String doGetPrefix(String var1);

    public abstract Iterator doGetPrefixes(String var1);
}

