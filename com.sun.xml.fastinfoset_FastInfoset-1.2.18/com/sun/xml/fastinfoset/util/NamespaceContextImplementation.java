/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.util;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public final class NamespaceContextImplementation
implements NamespaceContext {
    private static int DEFAULT_SIZE = 8;
    private String[] prefixes = new String[DEFAULT_SIZE];
    private String[] namespaceURIs = new String[DEFAULT_SIZE];
    private int namespacePosition;
    private int[] contexts = new int[DEFAULT_SIZE];
    private int contextPosition;
    private int currentContext;

    public NamespaceContextImplementation() {
        this.prefixes[0] = "xml";
        this.namespaceURIs[0] = "http://www.w3.org/XML/1998/namespace";
        this.prefixes[1] = "xmlns";
        this.namespaceURIs[1] = "http://www.w3.org/2000/xmlns/";
        this.namespacePosition = 2;
        this.currentContext = 2;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        for (int i = this.namespacePosition - 1; i >= 0; --i) {
            String declaredPrefix = this.prefixes[i];
            if (!declaredPrefix.equals(prefix)) continue;
            return this.namespaceURIs[i];
        }
        return "";
    }

    @Override
    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        }
        for (int i = this.namespacePosition - 1; i >= 0; --i) {
            String declaredNamespaceURI = this.namespaceURIs[i];
            if (!declaredNamespaceURI.equals(namespaceURI)) continue;
            String declaredPrefix = this.prefixes[i];
            boolean isOutOfScope = false;
            for (int j = i + 1; j < this.namespacePosition; ++j) {
                if (!declaredPrefix.equals(this.prefixes[j])) continue;
                isOutOfScope = true;
                break;
            }
            if (isOutOfScope) continue;
            return declaredPrefix;
        }
        return null;
    }

    public String getNonDefaultPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        }
        for (int i = this.namespacePosition - 1; i >= 0; --i) {
            String declaredNamespaceURI = this.namespaceURIs[i];
            if (!declaredNamespaceURI.equals(namespaceURI) || this.prefixes[i].length() <= 0) continue;
            String declaredPrefix = this.prefixes[i];
            ++i;
            while (i < this.namespacePosition) {
                if (declaredPrefix.equals(this.prefixes[i])) {
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
            if (!declaredNamespaceURI.equals(namespaceURI)) continue;
            String declaredPrefix = this.prefixes[i];
            for (int j = i + 1; j < this.namespacePosition; ++j) {
                if (declaredPrefix.equals(this.prefixes[j])) continue block0;
            }
            l.add(declaredPrefix);
        }
        return l.iterator();
    }

    public String getPrefix(int index) {
        return this.prefixes[index];
    }

    public String getNamespaceURI(int index) {
        return this.namespaceURIs[index];
    }

    public int getCurrentContextStartIndex() {
        return this.currentContext;
    }

    public int getCurrentContextEndIndex() {
        return this.namespacePosition;
    }

    public boolean isCurrentContextEmpty() {
        return this.currentContext == this.namespacePosition;
    }

    public void declarePrefix(String prefix, String namespaceURI) {
        prefix = prefix.intern();
        namespaceURI = namespaceURI.intern();
        if (prefix == "xml" || prefix == "xmlns") {
            return;
        }
        for (int i = this.currentContext; i < this.namespacePosition; ++i) {
            String declaredPrefix = this.prefixes[i];
            if (declaredPrefix != prefix) continue;
            this.prefixes[i] = prefix;
            this.namespaceURIs[i] = namespaceURI;
            return;
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
        this.contexts[this.contextPosition++] = this.currentContext = this.namespacePosition;
    }

    private void resizeContexts() {
        int[] newContexts = new int[this.contexts.length * 3 / 2 + 1];
        System.arraycopy(this.contexts, 0, newContexts, 0, this.contexts.length);
        this.contexts = newContexts;
    }

    public void popContext() {
        if (this.contextPosition > 0) {
            this.namespacePosition = this.currentContext = this.contexts[--this.contextPosition];
        }
    }

    public void reset() {
        this.namespacePosition = 2;
        this.currentContext = 2;
    }
}

