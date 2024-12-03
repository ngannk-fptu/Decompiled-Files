/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.apache.xml.utils.NamespaceSupport2;

class PrefixForUriEnumerator
implements Enumeration {
    private Enumeration allPrefixes;
    private String uri;
    private String lookahead = null;
    private NamespaceSupport2 nsup;

    PrefixForUriEnumerator(NamespaceSupport2 nsup, String uri, Enumeration allPrefixes) {
        this.nsup = nsup;
        this.uri = uri;
        this.allPrefixes = allPrefixes;
    }

    @Override
    public boolean hasMoreElements() {
        if (this.lookahead != null) {
            return true;
        }
        while (this.allPrefixes.hasMoreElements()) {
            String prefix = (String)this.allPrefixes.nextElement();
            if (!this.uri.equals(this.nsup.getURI(prefix))) continue;
            this.lookahead = prefix;
            return true;
        }
        return false;
    }

    public Object nextElement() {
        if (this.hasMoreElements()) {
            String tmp = this.lookahead;
            this.lookahead = null;
            return tmp;
        }
        throw new NoSuchElementException();
    }
}

