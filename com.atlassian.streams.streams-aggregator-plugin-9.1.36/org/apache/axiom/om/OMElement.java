/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamedInformationItem;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLParserWrapper;

public interface OMElement
extends OMNode,
OMContainer,
OMNamedInformationItem {
    public Iterator getChildElements();

    public OMNamespace declareNamespace(String var1, String var2);

    public OMNamespace declareDefaultNamespace(String var1);

    public OMNamespace getDefaultNamespace();

    public OMNamespace declareNamespace(OMNamespace var1);

    public void undeclarePrefix(String var1);

    public OMNamespace findNamespace(String var1, String var2);

    public OMNamespace findNamespaceURI(String var1);

    public Iterator getAllDeclaredNamespaces() throws OMException;

    public Iterator getNamespacesInScope();

    public NamespaceContext getNamespaceContext(boolean var1);

    public Iterator getAllAttributes();

    public OMAttribute getAttribute(QName var1);

    public String getAttributeValue(QName var1);

    public OMAttribute addAttribute(OMAttribute var1);

    public OMAttribute addAttribute(String var1, String var2, OMNamespace var3);

    public void removeAttribute(OMAttribute var1);

    public void setBuilder(OMXMLParserWrapper var1);

    public OMElement getFirstElement();

    public void setText(String var1);

    public void setText(QName var1);

    public String getText();

    public Reader getTextAsStream(boolean var1);

    public void writeTextTo(Writer var1, boolean var2) throws IOException;

    public QName getTextAsQName();

    public void setNamespace(OMNamespace var1);

    public void setNamespaceWithNoFindInCurrentScope(OMNamespace var1);

    public String toString();

    public String toStringWithConsume() throws XMLStreamException;

    public QName resolveQName(String var1);

    public OMElement cloneOMElement();

    public void setLineNumber(int var1);

    public int getLineNumber();

    public void serialize(OutputStream var1) throws XMLStreamException;

    public void serialize(Writer var1) throws XMLStreamException;

    public void serialize(OutputStream var1, OMOutputFormat var2) throws XMLStreamException;

    public void serialize(Writer var1, OMOutputFormat var2) throws XMLStreamException;

    public void serializeAndConsume(OutputStream var1) throws XMLStreamException;

    public void serializeAndConsume(Writer var1) throws XMLStreamException;

    public void serializeAndConsume(OutputStream var1, OMOutputFormat var2) throws XMLStreamException;

    public void serializeAndConsume(Writer var1, OMOutputFormat var2) throws XMLStreamException;
}

