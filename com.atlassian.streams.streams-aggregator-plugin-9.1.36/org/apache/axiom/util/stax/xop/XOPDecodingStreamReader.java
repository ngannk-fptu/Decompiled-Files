/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.util.stax.xop;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.axiom.util.stax.XMLEventUtils;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import org.apache.axiom.util.stax.xop.XOPEncodedStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XOPDecodingStreamReader
extends XMLStreamReaderWrapper
implements DataHandlerReader {
    private static final String SOLE_CHILD_MSG = "Expected xop:Include as the sole child of an element information item (see section 3.2 of http://www.w3.org/TR/xop10/)";
    private static final Log log = LogFactory.getLog(XOPDecodingStreamReader.class);
    private final MimePartProvider mimePartProvider;
    private DataHandlerProviderImpl dh;
    private String base64;

    public XOPDecodingStreamReader(XMLStreamReader parent, MimePartProvider mimePartProvider) {
        super(parent);
        this.mimePartProvider = mimePartProvider;
    }

    private void resetDataHandler() {
        this.dh = null;
        this.base64 = null;
    }

    private String processXopInclude() throws XMLStreamException {
        String contentID;
        if (super.getAttributeCount() != 1 || !super.getAttributeLocalName(0).equals("href")) {
            throw new XMLStreamException("Expected xop:Include element information item with a (single) href attribute");
        }
        String href = super.getAttributeValue(0);
        if (log.isDebugEnabled()) {
            log.debug((Object)("processXopInclude - found href : " + href));
        }
        if (!href.startsWith("cid:")) {
            throw new XMLStreamException("Expected href attribute containing a URL in the cid scheme");
        }
        try {
            contentID = URLDecoder.decode(href.substring(4), "ascii");
            if (log.isDebugEnabled()) {
                log.debug((Object)("processXopInclude - decoded contentID : " + contentID));
            }
        }
        catch (UnsupportedEncodingException ex) {
            throw new XMLStreamException(ex);
        }
        if (super.next() != 2) {
            throw new XMLStreamException("Expected xop:Include element information item to be empty");
        }
        if (super.next() != 2) {
            throw new XMLStreamException(SOLE_CHILD_MSG);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Encountered xop:Include for content ID '" + contentID + "'"));
        }
        return contentID;
    }

    public int next() throws XMLStreamException {
        boolean wasStartElement;
        int event;
        if (this.dh != null) {
            this.resetDataHandler();
            event = 2;
            wasStartElement = false;
        } else {
            wasStartElement = super.getEventType() == 1;
            event = super.next();
        }
        if (event == 1 && super.getLocalName().equals("Include") && super.getNamespaceURI().equals("http://www.w3.org/2004/08/xop/include")) {
            if (!wasStartElement) {
                throw new XMLStreamException(SOLE_CHILD_MSG);
            }
            this.dh = new DataHandlerProviderImpl(this.mimePartProvider, this.processXopInclude());
            return 4;
        }
        return event;
    }

    public int getEventType() {
        return this.dh == null ? super.getEventType() : 4;
    }

    public int nextTag() throws XMLStreamException {
        if (this.dh != null) {
            this.resetDataHandler();
            return 2;
        }
        return super.nextTag();
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        if (DataHandlerReader.PROPERTY.equals(name)) {
            return this;
        }
        return super.getProperty(name);
    }

    public String getElementText() throws XMLStreamException {
        if (super.getEventType() != 1) {
            throw new XMLStreamException("The current event is not a START_ELEMENT event");
        }
        int event = super.next();
        if (event == 1 && super.getLocalName().equals("Include") && super.getNamespaceURI().equals("http://www.w3.org/2004/08/xop/include")) {
            String contentID = this.processXopInclude();
            try {
                return XOPDecodingStreamReader.toBase64(this.mimePartProvider.getDataHandler(contentID));
            }
            catch (IOException ex) {
                throw new XMLStreamException("Failed to load MIME part '" + contentID + "'", ex);
            }
        }
        String text = null;
        StringBuffer buffer = null;
        while (event != 2) {
            switch (event) {
                case 4: 
                case 6: 
                case 9: 
                case 12: {
                    if (text == null && buffer == null) {
                        text = super.getText();
                        break;
                    }
                    String thisText = super.getText();
                    if (buffer == null) {
                        buffer = new StringBuffer(text.length() + thisText.length());
                        buffer.append(text);
                    }
                    buffer.append(thisText);
                    break;
                }
                case 3: 
                case 5: {
                    break;
                }
                default: {
                    throw new XMLStreamException("Unexpected event " + XMLEventUtils.getEventTypeString(event) + " while reading element text");
                }
            }
            event = super.next();
        }
        if (buffer != null) {
            return buffer.toString();
        }
        if (text != null) {
            return text;
        }
        return "";
    }

    public String getPrefix() {
        if (this.dh != null) {
            throw new IllegalStateException();
        }
        return super.getPrefix();
    }

    public String getNamespaceURI() {
        if (this.dh != null) {
            throw new IllegalStateException();
        }
        return super.getNamespaceURI();
    }

    public String getLocalName() {
        if (this.dh != null) {
            throw new IllegalStateException();
        }
        return super.getLocalName();
    }

    public QName getName() {
        if (this.dh != null) {
            throw new IllegalStateException();
        }
        return super.getName();
    }

    public Location getLocation() {
        return super.getLocation();
    }

    public String getNamespaceURI(String prefix) {
        String uri = super.getNamespaceURI(prefix);
        if ("xop".equals(prefix) && uri != null) {
            System.out.println(prefix + " -> " + uri);
        }
        return uri;
    }

    public int getNamespaceCount() {
        if (this.dh != null) {
            throw new IllegalStateException();
        }
        return super.getNamespaceCount();
    }

    public String getNamespacePrefix(int index) {
        if (this.dh != null) {
            throw new IllegalStateException();
        }
        return super.getNamespacePrefix(index);
    }

    public String getNamespaceURI(int index) {
        if (this.dh != null) {
            throw new IllegalStateException();
        }
        return super.getNamespaceURI(index);
    }

    private static String toBase64(DataHandler dh) throws XMLStreamException {
        try {
            return Base64Utils.encode(dh);
        }
        catch (IOException ex) {
            throw new XMLStreamException("Exception when encoding data handler as base64", ex);
        }
    }

    private String toBase64() throws XMLStreamException {
        if (this.base64 == null) {
            try {
                this.base64 = XOPDecodingStreamReader.toBase64(this.dh.getDataHandler());
            }
            catch (IOException ex) {
                throw new XMLStreamException("Failed to load MIME part '" + this.dh.getContentID() + "'", ex);
            }
        }
        return this.base64;
    }

    public String getText() {
        if (this.dh != null) {
            try {
                return this.toBase64();
            }
            catch (XMLStreamException ex) {
                throw new RuntimeException(ex);
            }
        }
        return super.getText();
    }

    public char[] getTextCharacters() {
        if (this.dh != null) {
            try {
                return this.toBase64().toCharArray();
            }
            catch (XMLStreamException ex) {
                throw new RuntimeException(ex);
            }
        }
        return super.getTextCharacters();
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        if (this.dh != null) {
            String text = this.toBase64();
            int copied = Math.min(length, text.length() - sourceStart);
            text.getChars(sourceStart, sourceStart + copied, target, targetStart);
            return copied;
        }
        return super.getTextCharacters(sourceStart, target, targetStart, length);
    }

    public int getTextLength() {
        if (this.dh != null) {
            try {
                return this.toBase64().length();
            }
            catch (XMLStreamException ex) {
                throw new RuntimeException(ex);
            }
        }
        return super.getTextLength();
    }

    public int getTextStart() {
        if (this.dh != null) {
            return 0;
        }
        return super.getTextStart();
    }

    public boolean hasText() {
        return this.dh != null || super.hasText();
    }

    public boolean isCharacters() {
        return this.dh != null || super.isCharacters();
    }

    public boolean isStartElement() {
        return this.dh == null && super.isStartElement();
    }

    public boolean isEndElement() {
        return this.dh == null && super.isEndElement();
    }

    public boolean hasName() {
        return this.dh == null && super.hasName();
    }

    public boolean isWhiteSpace() {
        return this.dh == null && super.isWhiteSpace();
    }

    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        if (this.dh != null) {
            if (type != 4) {
                throw new XMLStreamException("Expected CHARACTERS event");
            }
        } else {
            super.require(type, namespaceURI, localName);
        }
    }

    public boolean isBinary() {
        return this.dh != null;
    }

    public boolean isOptimized() {
        return true;
    }

    public boolean isDeferred() {
        return true;
    }

    public String getContentID() {
        return this.dh.getContentID();
    }

    public DataHandler getDataHandler() throws XMLStreamException {
        try {
            return this.dh.getDataHandler();
        }
        catch (IOException ex) {
            throw new XMLStreamException("Failed to load MIME part '" + this.dh.getContentID() + "'");
        }
    }

    public DataHandlerProvider getDataHandlerProvider() {
        return this.dh;
    }

    XOPEncodedStream getXOPEncodedStream() {
        return new XOPEncodedStream(this.getParent(), this.mimePartProvider);
    }

    private static class DataHandlerProviderImpl
    implements DataHandlerProvider {
        private final MimePartProvider mimePartProvider;
        private final String contentID;

        public DataHandlerProviderImpl(MimePartProvider mimePartProvider, String contentID) {
            this.mimePartProvider = mimePartProvider;
            this.contentID = contentID;
        }

        public String getContentID() {
            return this.contentID;
        }

        public boolean isLoaded() {
            return this.mimePartProvider.isLoaded(this.contentID);
        }

        public DataHandler getDataHandler() throws IOException {
            return this.mimePartProvider.getDataHandler(this.contentID);
        }
    }
}

