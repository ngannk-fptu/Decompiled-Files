/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.namespace;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.axiom.util.namespace.AbstractNamespaceContext;

public class ScopedNamespaceContext
extends AbstractNamespaceContext {
    String[] prefixArray = new String[16];
    String[] uriArray = new String[16];
    int bindings;
    private int[] scopeIndexes = new int[16];
    private int scopes;

    public void setPrefix(String prefix, String namespaceURI) {
        if (prefix == null || namespaceURI == null) {
            throw new IllegalArgumentException("prefix and namespaceURI may not be null");
        }
        if (this.bindings == this.prefixArray.length) {
            int len = this.prefixArray.length;
            int newLen = len * 2;
            String[] newPrefixArray = new String[newLen];
            System.arraycopy(this.prefixArray, 0, newPrefixArray, 0, len);
            String[] newUriArray = new String[newLen];
            System.arraycopy(this.uriArray, 0, newUriArray, 0, len);
            this.prefixArray = newPrefixArray;
            this.uriArray = newUriArray;
        }
        this.prefixArray[this.bindings] = prefix;
        this.uriArray[this.bindings] = namespaceURI;
        ++this.bindings;
    }

    public void startScope() {
        if (this.scopes == this.scopeIndexes.length) {
            int[] newScopeIndexes = new int[this.scopeIndexes.length * 2];
            System.arraycopy(this.scopeIndexes, 0, newScopeIndexes, 0, this.scopeIndexes.length);
            this.scopeIndexes = newScopeIndexes;
        }
        this.scopeIndexes[this.scopes++] = this.bindings;
    }

    public void endScope() {
        this.bindings = this.scopeIndexes[--this.scopes];
    }

    protected String doGetNamespaceURI(String prefix) {
        for (int i = this.bindings - 1; i >= 0; --i) {
            if (!prefix.equals(this.prefixArray[i])) continue;
            return this.uriArray[i];
        }
        return "";
    }

    protected String doGetPrefix(String namespaceURI) {
        block0: for (int i = this.bindings - 1; i >= 0; --i) {
            if (!namespaceURI.equals(this.uriArray[i])) continue;
            String prefix = this.prefixArray[i];
            for (int j = i + 1; j < this.bindings; ++j) {
                if (prefix.equals(this.prefixArray[j])) continue block0;
            }
            return prefix;
        }
        return null;
    }

    protected Iterator doGetPrefixes(final String namespaceURI) {
        return new Iterator(){
            private int binding;
            private String next;
            {
                this.binding = ScopedNamespaceContext.this.bindings;
            }

            public boolean hasNext() {
                if (this.next == null) {
                    block0: while (--this.binding >= 0) {
                        if (!namespaceURI.equals(ScopedNamespaceContext.this.uriArray[this.binding])) continue;
                        String prefix = ScopedNamespaceContext.this.prefixArray[this.binding];
                        for (int j = this.binding + 1; j < ScopedNamespaceContext.this.bindings; ++j) {
                            if (prefix.equals(ScopedNamespaceContext.this.prefixArray[j])) continue block0;
                        }
                        this.next = prefix;
                        break;
                    }
                }
                return this.next != null;
            }

            public Object next() {
                if (this.hasNext()) {
                    String result = this.next;
                    this.next = null;
                    return result;
                }
                throw new NoSuchElementException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}

