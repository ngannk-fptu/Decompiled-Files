/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jvnet.staxex.NamespaceContextEx
 *  org.jvnet.staxex.NamespaceContextEx$Binding
 */
package com.sun.xml.stream.buffer.stax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.jvnet.staxex.NamespaceContextEx;

public final class NamespaceContexHelper
implements NamespaceContextEx {
    private static int DEFAULT_SIZE = 8;
    private String[] prefixes = new String[DEFAULT_SIZE];
    private String[] namespaceURIs = new String[DEFAULT_SIZE];
    private int namespacePosition;
    private int[] contexts = new int[DEFAULT_SIZE];
    private int contextPosition;

    public NamespaceContexHelper() {
        this.prefixes[0] = "xml";
        this.namespaceURIs[0] = "http://www.w3.org/XML/1998/namespace";
        this.prefixes[1] = "xmlns";
        this.namespaceURIs[1] = "http://www.w3.org/2000/xmlns/";
        this.namespacePosition = 2;
    }

    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        prefix = prefix.intern();
        for (int i = this.namespacePosition - 1; i >= 0; --i) {
            String declaredPrefix = this.prefixes[i];
            if (declaredPrefix != prefix) continue;
            return this.namespaceURIs[i];
        }
        return "";
    }

    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        }
        for (int i = this.namespacePosition - 1; i >= 0; --i) {
            String declaredNamespaceURI = this.namespaceURIs[i];
            if (declaredNamespaceURI != namespaceURI && !declaredNamespaceURI.equals(namespaceURI)) continue;
            String declaredPrefix = this.prefixes[i];
            ++i;
            while (i < this.namespacePosition) {
                if (declaredPrefix == this.prefixes[i]) {
                    return null;
                }
                ++i;
            }
            return declaredPrefix;
        }
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        }
        ArrayList<String> l = new ArrayList<String>();
        block0: for (int i = this.namespacePosition - 1; i >= 0; --i) {
            String declaredNamespaceURI = this.namespaceURIs[i];
            if (declaredNamespaceURI != namespaceURI && !declaredNamespaceURI.equals(namespaceURI)) continue;
            String declaredPrefix = this.prefixes[i];
            for (int j = i + 1; j < this.namespacePosition; ++j) {
                if (declaredPrefix == this.prefixes[j]) continue block0;
            }
            l.add(declaredPrefix);
        }
        return l.iterator();
    }

    public Iterator<NamespaceContextEx.Binding> iterator() {
        if (this.namespacePosition == 2) {
            return Collections.EMPTY_LIST.iterator();
        }
        ArrayList<NamespaceBindingImpl> namespaces = new ArrayList<NamespaceBindingImpl>(this.namespacePosition);
        for (int i = this.namespacePosition - 1; i >= 2; --i) {
            String declaredPrefix = this.prefixes[i];
            for (int j = i + 1; j < this.namespacePosition && declaredPrefix != this.prefixes[j]; ++j) {
                namespaces.add(new NamespaceBindingImpl(i));
            }
        }
        return namespaces.iterator();
    }

    public void declareDefaultNamespace(String namespaceURI) {
        this.declareNamespace("", namespaceURI);
    }

    public void declareNamespace(String prefix, String namespaceURI) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        if ((prefix = prefix.intern()) == "xml" || prefix == "xmlns") {
            return;
        }
        if (namespaceURI != null) {
            namespaceURI = namespaceURI.intern();
        }
        if (this.namespacePosition == this.namespaceURIs.length) {
            this.resizeNamespaces();
        }
        this.prefixes[this.namespacePosition] = prefix;
        this.namespaceURIs[this.namespacePosition++] = namespaceURI;
    }

    private void resizeNamespaces() {
        int newLength = this.namespaceURIs.length * 3 / 2 + 1;
        String[] newPrefixes = new String[newLength];
        System.arraycopy(this.prefixes, 0, newPrefixes, 0, this.prefixes.length);
        this.prefixes = newPrefixes;
        String[] newNamespaceURIs = new String[newLength];
        System.arraycopy(this.namespaceURIs, 0, newNamespaceURIs, 0, this.namespaceURIs.length);
        this.namespaceURIs = newNamespaceURIs;
    }

    public void pushContext() {
        if (this.contextPosition == this.contexts.length) {
            this.resizeContexts();
        }
        this.contexts[this.contextPosition++] = this.namespacePosition;
    }

    private void resizeContexts() {
        int[] newContexts = new int[this.contexts.length * 3 / 2 + 1];
        System.arraycopy(this.contexts, 0, newContexts, 0, this.contexts.length);
        this.contexts = newContexts;
    }

    public void popContext() {
        if (this.contextPosition > 0) {
            this.namespacePosition = this.contexts[--this.contextPosition];
        }
    }

    public void resetContexts() {
        this.namespacePosition = 2;
    }

    private final class NamespaceBindingImpl
    implements NamespaceContextEx.Binding {
        int index;

        NamespaceBindingImpl(int index) {
            this.index = index;
        }

        public String getPrefix() {
            return NamespaceContexHelper.this.prefixes[this.index];
        }

        public String getNamespaceURI() {
            return NamespaceContexHelper.this.namespaceURIs[this.index];
        }
    }
}

