/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.iri;

import org.apache.abdera.i18n.iri.IRI;

public interface Scheme {
    public String getName();

    public IRI normalize(IRI var1);

    public String normalizePath(String var1);

    public int getDefaultPort();
}

