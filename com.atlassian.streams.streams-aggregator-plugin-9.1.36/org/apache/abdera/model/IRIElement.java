/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;

public interface IRIElement
extends Element {
    public IRI getValue();

    public IRIElement setValue(String var1);

    public IRIElement setNormalizedValue(String var1);

    public IRI getResolvedValue();
}

