/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.util;

import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import java.io.Serializable;

public final class StringPair
implements Serializable {
    public final String namespaceURI;
    public final String localName;

    public StringPair(SimpleNameClass name) {
        this(name.namespaceURI, name.localName);
    }

    public StringPair(String ns, String ln) {
        if (ns == null) {
            throw new InternalError("namespace URI is null");
        }
        if (ln == null) {
            throw new InternalError("local name is null");
        }
        this.namespaceURI = ns;
        this.localName = ln;
    }

    public boolean equals(Object o) {
        if (!(o instanceof StringPair)) {
            return false;
        }
        return this.namespaceURI.equals(((StringPair)o).namespaceURI) && this.localName.equals(((StringPair)o).localName);
    }

    public int hashCode() {
        return this.namespaceURI.hashCode() ^ this.localName.hashCode();
    }

    public String toString() {
        return "{" + this.namespaceURI + "}" + this.localName;
    }
}

