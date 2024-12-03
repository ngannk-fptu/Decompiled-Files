/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.ExtensibleElement;

public interface Category
extends ExtensibleElement {
    public String getTerm();

    public Category setTerm(String var1);

    public IRI getScheme();

    public Category setScheme(String var1);

    public String getLabel();

    public Category setLabel(String var1);
}

