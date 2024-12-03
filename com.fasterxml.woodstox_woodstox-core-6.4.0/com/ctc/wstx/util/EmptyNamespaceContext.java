/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import com.ctc.wstx.util.BaseNsContext;
import com.ctc.wstx.util.DataUtil;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Namespace;

public final class EmptyNamespaceContext
extends BaseNsContext {
    static final EmptyNamespaceContext sInstance = new EmptyNamespaceContext();

    private EmptyNamespaceContext() {
    }

    public static EmptyNamespaceContext getInstance() {
        return sInstance;
    }

    @Override
    public Iterator<Namespace> getNamespaces() {
        return DataUtil.emptyIterator();
    }

    @Override
    public void outputNamespaceDeclarations(Writer w) {
    }

    @Override
    public void outputNamespaceDeclarations(XMLStreamWriter w) {
    }

    @Override
    public String doGetNamespaceURI(String prefix) {
        return null;
    }

    @Override
    public String doGetPrefix(String nsURI) {
        return null;
    }

    @Override
    public Iterator<String> doGetPrefixes(String nsURI) {
        return DataUtil.emptyIterator();
    }
}

