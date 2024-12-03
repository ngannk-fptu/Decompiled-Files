/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.streaming;

import com.sun.xml.ws.streaming.Attributes;
import com.sun.xml.ws.streaming.XMLStreamReaderException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderUtil {
    private XMLStreamReaderUtil() {
    }

    public static void close(XMLStreamReader reader) {
        try {
            reader.close();
        }
        catch (XMLStreamException e) {
            throw XMLStreamReaderUtil.wrapException(e);
        }
    }

    public static void readRest(XMLStreamReader reader) {
        try {
            while (reader.getEventType() != 8) {
                reader.next();
            }
        }
        catch (XMLStreamException e) {
            throw XMLStreamReaderUtil.wrapException(e);
        }
    }

    public static int next(XMLStreamReader reader) {
        try {
            int readerEvent = reader.next();
            while (readerEvent != 8) {
                switch (readerEvent) {
                    case 1: 
                    case 2: 
                    case 3: 
                    case 4: 
                    case 12: {
                        return readerEvent;
                    }
                }
                readerEvent = reader.next();
            }
            return readerEvent;
        }
        catch (XMLStreamException e) {
            throw XMLStreamReaderUtil.wrapException(e);
        }
    }

    public static int nextElementContent(XMLStreamReader reader) {
        int state = XMLStreamReaderUtil.nextContent(reader);
        if (state == 4) {
            throw new XMLStreamReaderException("xmlreader.unexpectedCharacterContent", reader.getText());
        }
        return state;
    }

    public static void toNextTag(XMLStreamReader reader, QName name) {
        if (reader.getEventType() != 1 && reader.getEventType() != 2) {
            XMLStreamReaderUtil.nextElementContent(reader);
        }
        if (reader.getEventType() == 2 && name.equals(reader.getName())) {
            XMLStreamReaderUtil.nextElementContent(reader);
        }
    }

    public static String nextWhiteSpaceContent(XMLStreamReader reader) {
        XMLStreamReaderUtil.next(reader);
        return XMLStreamReaderUtil.currentWhiteSpaceContent(reader);
    }

    public static String currentWhiteSpaceContent(XMLStreamReader reader) {
        StringBuilder whiteSpaces = null;
        while (true) {
            switch (reader.getEventType()) {
                case 1: 
                case 2: 
                case 8: {
                    return whiteSpaces == null ? null : whiteSpaces.toString();
                }
                case 4: {
                    if (reader.isWhiteSpace()) {
                        if (whiteSpaces == null) {
                            whiteSpaces = new StringBuilder();
                        }
                        whiteSpaces.append(reader.getText());
                        break;
                    }
                    throw new XMLStreamReaderException("xmlreader.unexpectedCharacterContent", reader.getText());
                }
            }
            XMLStreamReaderUtil.next(reader);
        }
    }

    public static int nextContent(XMLStreamReader reader) {
        while (true) {
            int state = XMLStreamReaderUtil.next(reader);
            switch (state) {
                case 1: 
                case 2: 
                case 8: {
                    return state;
                }
                case 4: {
                    if (reader.isWhiteSpace()) break;
                    return 4;
                }
            }
        }
    }

    public static void skipElement(XMLStreamReader reader) {
        assert (reader.getEventType() == 1);
        XMLStreamReaderUtil.skipTags(reader, true);
        assert (reader.getEventType() == 2);
    }

    public static void skipSiblings(XMLStreamReader reader, QName parent) {
        XMLStreamReaderUtil.skipTags(reader, reader.getName().equals(parent));
        assert (reader.getEventType() == 2);
    }

    private static void skipTags(XMLStreamReader reader, boolean exitCondition) {
        try {
            int state;
            int tags = 0;
            while ((state = reader.next()) != 8) {
                if (state == 1) {
                    ++tags;
                    continue;
                }
                if (state != 2) continue;
                if (tags == 0 && exitCondition) {
                    return;
                }
                --tags;
            }
        }
        catch (XMLStreamException e) {
            throw XMLStreamReaderUtil.wrapException(e);
        }
    }

    public static String getElementText(XMLStreamReader reader) {
        try {
            return reader.getElementText();
        }
        catch (XMLStreamException e) {
            throw XMLStreamReaderUtil.wrapException(e);
        }
    }

    public static QName getElementQName(XMLStreamReader reader) {
        try {
            String text = reader.getElementText().trim();
            String prefix = text.substring(0, text.indexOf(58));
            String namespaceURI = reader.getNamespaceContext().getNamespaceURI(prefix);
            if (namespaceURI == null) {
                namespaceURI = "";
            }
            String localPart = text.substring(text.indexOf(58) + 1, text.length());
            return new QName(namespaceURI, localPart);
        }
        catch (XMLStreamException e) {
            throw XMLStreamReaderUtil.wrapException(e);
        }
    }

    public static Attributes getAttributes(XMLStreamReader reader) {
        return reader.getEventType() == 1 || reader.getEventType() == 10 ? new AttributesImpl(reader) : null;
    }

    public static void verifyReaderState(XMLStreamReader reader, int expectedState) {
        int state = reader.getEventType();
        if (state != expectedState) {
            throw new XMLStreamReaderException("xmlreader.unexpectedState", XMLStreamReaderUtil.getStateName(expectedState), XMLStreamReaderUtil.getStateName(state));
        }
    }

    public static void verifyTag(XMLStreamReader reader, String namespaceURI, String localName) {
        if (!localName.equals(reader.getLocalName()) || !namespaceURI.equals(reader.getNamespaceURI())) {
            throw new XMLStreamReaderException("xmlreader.unexpectedState.tag", "{" + namespaceURI + "}" + localName, "{" + reader.getNamespaceURI() + "}" + reader.getLocalName());
        }
    }

    public static void verifyTag(XMLStreamReader reader, QName name) {
        XMLStreamReaderUtil.verifyTag(reader, name.getNamespaceURI(), name.getLocalPart());
    }

    public static String getStateName(XMLStreamReader reader) {
        return XMLStreamReaderUtil.getStateName(reader.getEventType());
    }

    public static String getStateName(int state) {
        switch (state) {
            case 10: {
                return "ATTRIBUTE";
            }
            case 12: {
                return "CDATA";
            }
            case 4: {
                return "CHARACTERS";
            }
            case 5: {
                return "COMMENT";
            }
            case 11: {
                return "DTD";
            }
            case 8: {
                return "END_DOCUMENT";
            }
            case 2: {
                return "END_ELEMENT";
            }
            case 15: {
                return "ENTITY_DECLARATION";
            }
            case 9: {
                return "ENTITY_REFERENCE";
            }
            case 13: {
                return "NAMESPACE";
            }
            case 14: {
                return "NOTATION_DECLARATION";
            }
            case 3: {
                return "PROCESSING_INSTRUCTION";
            }
            case 6: {
                return "SPACE";
            }
            case 7: {
                return "START_DOCUMENT";
            }
            case 1: {
                return "START_ELEMENT";
            }
        }
        return "UNKNOWN";
    }

    private static XMLStreamReaderException wrapException(XMLStreamException e) {
        return new XMLStreamReaderException("xmlreader.ioException", e.getMessage());
    }

    public static class AttributesImpl
    implements Attributes {
        static final String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
        AttributeInfo[] atInfos;

        public AttributesImpl(XMLStreamReader reader) {
            if (reader == null) {
                this.atInfos = new AttributeInfo[0];
            } else {
                int i;
                int index = 0;
                int namespaceCount = reader.getNamespaceCount();
                int attributeCount = reader.getAttributeCount();
                this.atInfos = new AttributeInfo[namespaceCount + attributeCount];
                for (i = 0; i < namespaceCount; ++i) {
                    String namespacePrefix = reader.getNamespacePrefix(i);
                    if (namespacePrefix == null) {
                        namespacePrefix = "";
                    }
                    this.atInfos[index++] = new AttributeInfo(new QName(XMLNS_NAMESPACE_URI, namespacePrefix, "xmlns"), reader.getNamespaceURI(i));
                }
                for (i = 0; i < attributeCount; ++i) {
                    this.atInfos[index++] = new AttributeInfo(reader.getAttributeName(i), reader.getAttributeValue(i));
                }
            }
        }

        @Override
        public int getLength() {
            return this.atInfos.length;
        }

        @Override
        public String getLocalName(int index) {
            if (index >= 0 && index < this.atInfos.length) {
                return this.atInfos[index].getLocalName();
            }
            return null;
        }

        @Override
        public QName getName(int index) {
            if (index >= 0 && index < this.atInfos.length) {
                return this.atInfos[index].getName();
            }
            return null;
        }

        @Override
        public String getPrefix(int index) {
            if (index >= 0 && index < this.atInfos.length) {
                return this.atInfos[index].getName().getPrefix();
            }
            return null;
        }

        @Override
        public String getURI(int index) {
            if (index >= 0 && index < this.atInfos.length) {
                return this.atInfos[index].getName().getNamespaceURI();
            }
            return null;
        }

        @Override
        public String getValue(int index) {
            if (index >= 0 && index < this.atInfos.length) {
                return this.atInfos[index].getValue();
            }
            return null;
        }

        @Override
        public String getValue(QName name) {
            int index = this.getIndex(name);
            if (index != -1) {
                return this.atInfos[index].getValue();
            }
            return null;
        }

        @Override
        public String getValue(String localName) {
            int index = this.getIndex(localName);
            if (index != -1) {
                return this.atInfos[index].getValue();
            }
            return null;
        }

        @Override
        public String getValue(String uri, String localName) {
            int index = this.getIndex(uri, localName);
            if (index != -1) {
                return this.atInfos[index].getValue();
            }
            return null;
        }

        @Override
        public boolean isNamespaceDeclaration(int index) {
            if (index >= 0 && index < this.atInfos.length) {
                return this.atInfos[index].isNamespaceDeclaration();
            }
            return false;
        }

        @Override
        public int getIndex(QName name) {
            for (int i = 0; i < this.atInfos.length; ++i) {
                if (!this.atInfos[i].getName().equals(name)) continue;
                return i;
            }
            return -1;
        }

        @Override
        public int getIndex(String localName) {
            for (int i = 0; i < this.atInfos.length; ++i) {
                if (!this.atInfos[i].getName().getLocalPart().equals(localName)) continue;
                return i;
            }
            return -1;
        }

        @Override
        public int getIndex(String uri, String localName) {
            for (int i = 0; i < this.atInfos.length; ++i) {
                QName qName = this.atInfos[i].getName();
                if (!qName.getNamespaceURI().equals(uri) || !qName.getLocalPart().equals(localName)) continue;
                return i;
            }
            return -1;
        }

        static class AttributeInfo {
            private QName name;
            private String value;

            public AttributeInfo(QName name, String value) {
                this.name = name;
                this.value = value == null ? "" : value;
            }

            QName getName() {
                return this.name;
            }

            String getValue() {
                return this.value;
            }

            String getLocalName() {
                if (this.isNamespaceDeclaration()) {
                    if (this.name.getLocalPart().equals("")) {
                        return "xmlns";
                    }
                    return "xmlns:" + this.name.getLocalPart();
                }
                return this.name.getLocalPart();
            }

            boolean isNamespaceDeclaration() {
                return this.name.getNamespaceURI() == AttributesImpl.XMLNS_NAMESPACE_URI;
            }
        }
    }
}

