/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.util.DataUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Namespace;

public abstract class BaseNsContext
implements NamespaceContext {
    protected static final String UNDECLARED_NS_URI = "";

    @Override
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

    @Override
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

    @Override
    public final Iterator<String> getPrefixes(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return DataUtil.singletonIterator("xml");
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return DataUtil.singletonIterator("xmlns");
        }
        return this.doGetPrefixes(nsURI);
    }

    public abstract Iterator<Namespace> getNamespaces();

    public abstract void outputNamespaceDeclarations(Writer var1) throws IOException;

    public abstract void outputNamespaceDeclarations(XMLStreamWriter var1) throws XMLStreamException;

    public abstract String doGetNamespaceURI(String var1);

    public abstract String doGetPrefix(String var1);

    public abstract Iterator<String> doGetPrefixes(String var1);
}

