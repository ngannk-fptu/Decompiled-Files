/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.iri;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.Scheme;

public abstract class AbstractScheme
implements Scheme {
    protected final String name;
    protected final int port;

    protected AbstractScheme(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public int getDefaultPort() {
        return this.port;
    }

    public String getName() {
        return this.name;
    }

    public IRI normalize(IRI iri) {
        return iri;
    }

    public String normalizePath(String path) {
        return path;
    }
}

