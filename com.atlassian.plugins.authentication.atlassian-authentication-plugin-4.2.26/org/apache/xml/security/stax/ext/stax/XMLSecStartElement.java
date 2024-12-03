/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext.stax;

import java.util.List;
import javax.xml.stream.events.StartElement;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;

public interface XMLSecStartElement
extends XMLSecEvent,
StartElement {
    public XMLSecNamespace getElementNamespace();

    public void getNamespacesFromCurrentScope(List<XMLSecNamespace> var1);

    public List<XMLSecNamespace> getOnElementDeclaredNamespaces();

    public void addNamespace(XMLSecNamespace var1);

    public void getAttributesFromCurrentScope(List<XMLSecAttribute> var1);

    public List<XMLSecAttribute> getOnElementDeclaredAttributes();

    public void addAttribute(XMLSecAttribute var1);

    @Override
    public XMLSecStartElement asStartElement();
}

