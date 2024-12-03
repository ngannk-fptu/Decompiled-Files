/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

public interface XMLStreamReader
extends XMLStreamConstants {
    public void close() throws XMLStreamException;

    public int getAttributeCount();

    public String getAttributeLocalName(int var1);

    public QName getAttributeName(int var1);

    public String getAttributeNamespace(int var1);

    public String getAttributePrefix(int var1);

    public String getAttributeType(int var1);

    public String getAttributeValue(int var1);

    public String getAttributeValue(String var1, String var2);

    public String getCharacterEncodingScheme();

    public String getElementText() throws XMLStreamException;

    public String getEncoding();

    public int getEventType();

    public String getLocalName();

    public Location getLocation();

    public QName getName();

    public NamespaceContext getNamespaceContext();

    public int getNamespaceCount();

    public String getNamespacePrefix(int var1);

    public String getNamespaceURI();

    public String getNamespaceURI(int var1);

    public String getNamespaceURI(String var1);

    public String getPIData();

    public String getPITarget();

    public String getPrefix();

    public Object getProperty(String var1) throws IllegalArgumentException;

    public String getText();

    public char[] getTextCharacters();

    public int getTextCharacters(int var1, char[] var2, int var3, int var4) throws XMLStreamException;

    public int getTextLength();

    public int getTextStart();

    public String getVersion();

    public boolean hasName();

    public boolean hasNext() throws XMLStreamException;

    public boolean hasText();

    public boolean isAttributeSpecified(int var1);

    public boolean isCharacters();

    public boolean isEndElement();

    public boolean isStandalone();

    public boolean isStartElement();

    public boolean isWhiteSpace();

    public int next() throws XMLStreamException;

    public int nextTag() throws XMLStreamException;

    public void require(int var1, String var2, String var3) throws XMLStreamException;

    public boolean standaloneSet();
}

