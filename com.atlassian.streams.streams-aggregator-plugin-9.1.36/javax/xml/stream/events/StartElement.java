/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream.events;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

public interface StartElement
extends XMLEvent {
    public QName getName();

    public Iterator getAttributes();

    public Iterator getNamespaces();

    public Attribute getAttributeByName(QName var1);

    public NamespaceContext getNamespaceContext();

    public String getNamespaceURI(String var1);
}

