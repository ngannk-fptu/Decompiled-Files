/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext.stax;

import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecCharacters;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

public interface XMLSecEvent
extends XMLEvent {
    public void setParentXMLSecStartElement(XMLSecStartElement var1);

    public XMLSecStartElement getParentXMLSecStartElement();

    public int getDocumentLevel();

    public void getElementPath(List<QName> var1);

    public List<QName> getElementPath();

    public XMLSecStartElement getStartElementAtLevel(int var1);

    @Override
    public XMLSecStartElement asStartElement();

    @Override
    public XMLSecEndElement asEndElement();

    @Override
    public XMLSecCharacters asCharacters();
}

