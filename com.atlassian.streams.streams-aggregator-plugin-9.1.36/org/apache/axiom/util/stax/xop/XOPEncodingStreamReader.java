/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.xop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.util.stax.xop.ContentIDGenerator;
import org.apache.axiom.util.stax.xop.OptimizationPolicy;
import org.apache.axiom.util.stax.xop.XOPConstants;
import org.apache.axiom.util.stax.xop.XOPEncodingStreamWrapper;

public class XOPEncodingStreamReader
extends XOPEncodingStreamWrapper
implements XMLStreamReader {
    private static final int STATE_PASS_THROUGH = 0;
    private static final int STATE_XOP_INCLUDE_START_ELEMENT = 1;
    private static final int STATE_XOP_INCLUDE_END_ELEMENT = 2;
    private final XMLStreamReader parent;
    private final DataHandlerReader dataHandlerReader;
    private int state = 0;
    private String currentContentID;

    public XOPEncodingStreamReader(XMLStreamReader parent, ContentIDGenerator contentIDGenerator, OptimizationPolicy optimizationPolicy) {
        super(contentIDGenerator, optimizationPolicy);
        DataHandlerReader dataHandlerReader;
        this.parent = parent;
        try {
            dataHandlerReader = (DataHandlerReader)parent.getProperty(DataHandlerReader.PROPERTY);
        }
        catch (IllegalArgumentException ex) {
            dataHandlerReader = null;
        }
        if (dataHandlerReader == null) {
            throw new IllegalArgumentException("The supplied XMLStreamReader doesn't implement the DataHandlerReader extension");
        }
        this.dataHandlerReader = dataHandlerReader;
    }

    public int next() throws XMLStreamException {
        switch (this.state) {
            case 1: {
                this.state = 2;
                return 2;
            }
            case 2: {
                this.state = 0;
                this.currentContentID = null;
            }
        }
        int event = this.parent.next();
        if (event == 4 && this.dataHandlerReader.isBinary()) {
            String contentID;
            try {
                contentID = this.dataHandlerReader.isDeferred() ? this.processDataHandler(this.dataHandlerReader.getDataHandlerProvider(), this.dataHandlerReader.getContentID(), this.dataHandlerReader.isOptimized()) : this.processDataHandler(this.dataHandlerReader.getDataHandler(), this.dataHandlerReader.getContentID(), this.dataHandlerReader.isOptimized());
            }
            catch (IOException ex) {
                throw new XMLStreamException("Error while processing data handler", ex);
            }
            if (contentID != null) {
                this.currentContentID = contentID;
                this.state = 1;
                return 1;
            }
            return 4;
        }
        return event;
    }

    public boolean hasNext() throws XMLStreamException {
        return this.state == 0 ? this.parent.hasNext() : true;
    }

    public int nextTag() throws XMLStreamException {
        switch (this.state) {
            case 1: {
                this.state = 2;
                return 2;
            }
            case 2: {
                this.currentContentID = null;
            }
        }
        return this.parent.nextTag();
    }

    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        if (this.state == 0) {
            this.parent.require(type, namespaceURI, localName);
        } else if (this.state == 1 && type != 1 || this.state == 2 && type != 2 || namespaceURI != null && !namespaceURI.equals("http://www.w3.org/2004/08/xop/include") || localName != null && !localName.equals("Include")) {
            throw new XMLStreamException();
        }
    }

    public Location getLocation() {
        return this.parent.getLocation();
    }

    public void close() throws XMLStreamException {
        this.parent.close();
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return this.parent.getProperty(name);
    }

    public String getEncoding() {
        return this.parent.getEncoding();
    }

    public String getCharacterEncodingScheme() {
        return this.parent.getCharacterEncodingScheme();
    }

    public String getVersion() {
        return this.parent.getVersion();
    }

    public boolean isStandalone() {
        return this.parent.isStandalone();
    }

    public boolean standaloneSet() {
        return this.parent.standaloneSet();
    }

    public String getPIData() {
        return this.parent.getPIData();
    }

    public String getPITarget() {
        return this.parent.getPITarget();
    }

    public int getAttributeCount() {
        switch (this.state) {
            case 1: {
                return 1;
            }
            case 2: {
                throw new IllegalStateException();
            }
        }
        return this.parent.getAttributeCount();
    }

    public String getAttributeLocalName(int index) {
        switch (this.state) {
            case 1: {
                if (index != 0) {
                    throw new IllegalArgumentException();
                }
                return "href";
            }
            case 2: {
                throw new IllegalStateException();
            }
        }
        return this.parent.getAttributeLocalName(index);
    }

    public QName getAttributeName(int index) {
        switch (this.state) {
            case 1: {
                if (index != 0) {
                    throw new IllegalArgumentException();
                }
                return new QName("href");
            }
            case 2: {
                throw new IllegalStateException();
            }
        }
        return this.parent.getAttributeName(index);
    }

    public String getAttributeNamespace(int index) {
        switch (this.state) {
            case 1: {
                if (index != 0) {
                    throw new IllegalArgumentException();
                }
                return null;
            }
            case 2: {
                throw new IllegalStateException();
            }
        }
        return this.parent.getAttributeNamespace(index);
    }

    public String getAttributePrefix(int index) {
        switch (this.state) {
            case 1: {
                if (index != 0) {
                    throw new IllegalArgumentException();
                }
                return null;
            }
            case 2: {
                throw new IllegalStateException();
            }
        }
        return this.parent.getAttributePrefix(index);
    }

    public String getAttributeType(int index) {
        switch (this.state) {
            case 1: {
                if (index != 0) {
                    throw new IllegalArgumentException();
                }
                return "CDATA";
            }
            case 2: {
                throw new IllegalStateException();
            }
        }
        return this.parent.getAttributeType(index);
    }

    public String getAttributeValue(int index) {
        switch (this.state) {
            case 1: {
                if (index != 0) {
                    throw new IllegalArgumentException();
                }
                return "cid:" + this.currentContentID.replaceAll("%", "%25");
            }
            case 2: {
                throw new IllegalStateException();
            }
        }
        return this.parent.getAttributeValue(index);
    }

    public boolean isAttributeSpecified(int index) {
        switch (this.state) {
            case 1: {
                if (index != 0) {
                    throw new IllegalArgumentException();
                }
                return true;
            }
            case 2: {
                throw new IllegalStateException();
            }
        }
        return this.parent.isAttributeSpecified(index);
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        switch (this.state) {
            case 1: {
                if ((namespaceURI == null || namespaceURI.length() == 0) && localName.equals("href")) {
                    return "cid:" + this.currentContentID;
                }
                return null;
            }
            case 2: {
                throw new IllegalStateException();
            }
        }
        return this.parent.getAttributeValue(namespaceURI, localName);
    }

    public String getElementText() throws XMLStreamException {
        switch (this.state) {
            case 1: {
                this.state = 2;
                return "";
            }
            case 2: {
                throw new IllegalStateException();
            }
        }
        return this.parent.getElementText();
    }

    public int getEventType() {
        switch (this.state) {
            case 1: {
                return 1;
            }
            case 2: {
                return 2;
            }
        }
        return this.parent.getEventType();
    }

    public String getNamespaceURI() {
        return this.state == 0 ? this.parent.getNamespaceURI() : "http://www.w3.org/2004/08/xop/include";
    }

    public String getLocalName() {
        return this.state == 0 ? this.parent.getLocalName() : "Include";
    }

    public String getPrefix() {
        return this.state == 0 ? this.parent.getPrefix() : "xop";
    }

    public QName getName() {
        return this.state == 0 ? this.parent.getName() : XOPConstants.INCLUDE_QNAME;
    }

    public NamespaceContext getNamespaceContext() {
        NamespaceContext ctx = this.parent.getNamespaceContext();
        if (this.state != 0) {
            ctx = new NamespaceContextWrapper(ctx);
        }
        return ctx;
    }

    public String getNamespaceURI(String prefix) {
        if (this.state != 0 && "xop".equals(prefix)) {
            return "http://www.w3.org/2004/08/xop/include";
        }
        return this.parent.getNamespaceURI(prefix);
    }

    public int getNamespaceCount() {
        return this.state == 0 ? this.parent.getNamespaceCount() : 1;
    }

    public String getNamespacePrefix(int index) {
        if (this.state == 0) {
            return this.parent.getNamespacePrefix(index);
        }
        if (index != 0) {
            throw new IllegalArgumentException();
        }
        return "xop";
    }

    public String getNamespaceURI(int index) {
        if (this.state == 0) {
            return this.parent.getNamespaceURI(index);
        }
        if (index != 0) {
            throw new IllegalArgumentException();
        }
        return "http://www.w3.org/2004/08/xop/include";
    }

    public String getText() {
        if (this.state == 0) {
            return this.parent.getText();
        }
        throw new IllegalStateException();
    }

    public int getTextStart() {
        if (this.state == 0) {
            return this.parent.getTextStart();
        }
        throw new IllegalStateException();
    }

    public int getTextLength() {
        if (this.state == 0) {
            return this.parent.getTextLength();
        }
        throw new IllegalStateException();
    }

    public char[] getTextCharacters() {
        if (this.state == 0) {
            return this.parent.getTextCharacters();
        }
        throw new IllegalStateException();
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        if (this.state == 0) {
            return this.parent.getTextCharacters(sourceStart, target, targetStart, length);
        }
        throw new IllegalStateException();
    }

    public boolean hasName() {
        return this.state == 0 ? this.parent.hasName() : true;
    }

    public boolean hasText() {
        return this.state == 0 ? this.parent.hasText() : false;
    }

    public boolean isCharacters() {
        return this.state == 0 ? this.parent.isCharacters() : false;
    }

    public boolean isWhiteSpace() {
        return this.state == 0 ? this.parent.isWhiteSpace() : false;
    }

    public boolean isStartElement() {
        switch (this.state) {
            case 1: {
                return true;
            }
            case 2: {
                return false;
            }
        }
        return this.parent.isStartElement();
    }

    public boolean isEndElement() {
        switch (this.state) {
            case 1: {
                return false;
            }
            case 2: {
                return true;
            }
        }
        return this.parent.isEndElement();
    }

    private static class NamespaceContextWrapper
    implements NamespaceContext {
        private static final List xopPrefixList = Arrays.asList("xop");
        private final NamespaceContext parent;

        public NamespaceContextWrapper(NamespaceContext parent) {
            this.parent = parent;
        }

        public String getNamespaceURI(String prefix) {
            return "xop".equals(prefix) ? "http://www.w3.org/2004/08/xop/include" : this.parent.getNamespaceURI(prefix);
        }

        public String getPrefix(String namespaceURI) {
            return "http://www.w3.org/2004/08/xop/include".equals(namespaceURI) ? "xop" : this.parent.getPrefix(namespaceURI);
        }

        public Iterator getPrefixes(String namespaceURI) {
            Iterator<String> prefixes = this.parent.getPrefixes(namespaceURI);
            if ("http://www.w3.org/2004/08/xop/include".equals(namespaceURI)) {
                if (!prefixes.hasNext()) {
                    return xopPrefixList.iterator();
                }
                ArrayList<String> prefixList = new ArrayList<String>();
                do {
                    prefixList.add(prefixes.next());
                } while (prefixes.hasNext());
                prefixList.add("xop");
                return prefixList.iterator();
            }
            return prefixes;
        }
    }
}

