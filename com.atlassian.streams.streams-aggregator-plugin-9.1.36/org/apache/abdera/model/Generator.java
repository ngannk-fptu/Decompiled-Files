/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;

public interface Generator
extends Element {
    public IRI getUri();

    public IRI getResolvedUri();

    public Generator setUri(String var1);

    public String getVersion();

    public Generator setVersion(String var1);
}

