/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;

public interface OMContainer
extends OMSerializable {
    public OMXMLParserWrapper getBuilder();

    public void addChild(OMNode var1);

    public Iterator getChildrenWithName(QName var1);

    public Iterator getChildrenWithLocalName(String var1);

    public Iterator getChildrenWithNamespaceURI(String var1);

    public OMElement getFirstChildWithName(QName var1) throws OMException;

    public Iterator getChildren();

    public Iterator getDescendants(boolean var1);

    public OMNode getFirstOMChild();

    public void removeChildren();

    public void serialize(OutputStream var1) throws XMLStreamException;

    public void serialize(Writer var1) throws XMLStreamException;

    public void serialize(OutputStream var1, OMOutputFormat var2) throws XMLStreamException;

    public void serialize(Writer var1, OMOutputFormat var2) throws XMLStreamException;

    public void serializeAndConsume(OutputStream var1) throws XMLStreamException;

    public void serializeAndConsume(Writer var1) throws XMLStreamException;

    public void serializeAndConsume(OutputStream var1, OMOutputFormat var2) throws XMLStreamException;

    public void serializeAndConsume(Writer var1, OMOutputFormat var2) throws XMLStreamException;

    public XMLStreamReader getXMLStreamReader();

    public XMLStreamReader getXMLStreamReaderWithoutCaching();

    public XMLStreamReader getXMLStreamReader(boolean var1);

    public XMLStreamReader getXMLStreamReader(boolean var1, OMXMLStreamReaderConfiguration var2);

    public SAXSource getSAXSource(boolean var1);
}

