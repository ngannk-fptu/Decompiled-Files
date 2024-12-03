/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import com.ctc.wstx.util.BaseNsContext;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.ri.EmptyIterator;

public final class EmptyNamespaceContext
extends BaseNsContext {
    static final EmptyNamespaceContext sInstance = new EmptyNamespaceContext();

    private EmptyNamespaceContext() {
    }

    public static EmptyNamespaceContext getInstance() {
        return sInstance;
    }

    public Iterator getNamespaces() {
        return EmptyIterator.getInstance();
    }

    public void outputNamespaceDeclarations(Writer w) {
    }

    public void outputNamespaceDeclarations(XMLStreamWriter w) {
    }

    public String doGetNamespaceURI(String prefix) {
        return null;
    }

    public String doGetPrefix(String nsURI) {
        return null;
    }

    public Iterator doGetPrefixes(String nsURI) {
        return EmptyIterator.getInstance();
    }
}

