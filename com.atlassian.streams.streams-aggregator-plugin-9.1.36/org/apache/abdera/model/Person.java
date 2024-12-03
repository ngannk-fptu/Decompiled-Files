/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.IRIElement;

public interface Person
extends ExtensibleElement,
Element {
    public Element getNameElement();

    public Person setNameElement(Element var1);

    public Element setName(String var1);

    public String getName();

    public Element getEmailElement();

    public Person setEmailElement(Element var1);

    public Element setEmail(String var1);

    public String getEmail();

    public IRIElement getUriElement();

    public Person setUriElement(IRIElement var1);

    public IRIElement setUri(String var1);

    public IRI getUri();
}

