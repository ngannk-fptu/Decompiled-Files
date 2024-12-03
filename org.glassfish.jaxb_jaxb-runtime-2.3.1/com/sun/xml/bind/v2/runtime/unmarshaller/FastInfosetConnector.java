/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.fastinfoset.stax.StAXDocumentParser
 */
package com.sun.xml.bind.v2.runtime.unmarshaller;

import com.sun.xml.bind.WhiteSpaceProcessor;
import com.sun.xml.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.bind.v2.runtime.unmarshaller.StAXConnector;
import com.sun.xml.bind.v2.runtime.unmarshaller.XmlVisitor;
import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class FastInfosetConnector
extends StAXConnector {
    private final StAXDocumentParser fastInfosetStreamReader;
    private boolean textReported;
    private final Base64Data base64Data = new Base64Data();
    private final StringBuilder buffer = new StringBuilder();
    private final CharSequenceImpl charArray = new CharSequenceImpl();

    public FastInfosetConnector(StAXDocumentParser fastInfosetStreamReader, XmlVisitor visitor) {
        super(visitor);
        fastInfosetStreamReader.setStringInterning(true);
        this.fastInfosetStreamReader = fastInfosetStreamReader;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void bridge() throws XMLStreamException {
        try {
            int depth = 0;
            int event = this.fastInfosetStreamReader.getEventType();
            if (event == 7) {
                while (!this.fastInfosetStreamReader.isStartElement()) {
                    event = this.fastInfosetStreamReader.next();
                }
            }
            if (event != 1) {
                throw new IllegalStateException("The current event is not START_ELEMENT\n but " + event);
            }
            this.handleStartDocument(this.fastInfosetStreamReader.getNamespaceContext());
            block8: while (true) {
                switch (event) {
                    case 1: {
                        this.handleStartElement();
                        ++depth;
                        break;
                    }
                    case 2: {
                        this.handleEndElement();
                        if (--depth != 0) break;
                        break block8;
                    }
                    case 4: 
                    case 6: 
                    case 12: {
                        if (!this.predictor.expectText()) break;
                        event = this.fastInfosetStreamReader.peekNext();
                        if (event == 2) {
                            this.processNonIgnorableText();
                            break;
                        }
                        if (event == 1) {
                            this.processIgnorableText();
                            break;
                        }
                        this.handleFragmentedCharacters();
                    }
                }
                event = this.fastInfosetStreamReader.next();
            }
            this.fastInfosetStreamReader.next();
            this.handleEndDocument();
            return;
        }
        catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    protected Location getCurrentLocation() {
        return this.fastInfosetStreamReader.getLocation();
    }

    @Override
    protected String getCurrentQName() {
        return this.fastInfosetStreamReader.getNameString();
    }

    private void handleStartElement() throws SAXException {
        this.processUnreportedText();
        for (int i = 0; i < this.fastInfosetStreamReader.accessNamespaceCount(); ++i) {
            this.visitor.startPrefixMapping(this.fastInfosetStreamReader.getNamespacePrefix(i), this.fastInfosetStreamReader.getNamespaceURI(i));
        }
        this.tagName.uri = this.fastInfosetStreamReader.accessNamespaceURI();
        this.tagName.local = this.fastInfosetStreamReader.accessLocalName();
        this.tagName.atts = this.fastInfosetStreamReader.getAttributesHolder();
        this.visitor.startElement(this.tagName);
    }

    private void handleFragmentedCharacters() throws XMLStreamException, SAXException {
        this.buffer.setLength(0);
        this.buffer.append(this.fastInfosetStreamReader.getTextCharacters(), this.fastInfosetStreamReader.getTextStart(), this.fastInfosetStreamReader.getTextLength());
        block5: while (true) {
            switch (this.fastInfosetStreamReader.peekNext()) {
                case 1: {
                    this.processBufferedText(true);
                    return;
                }
                case 2: {
                    this.processBufferedText(false);
                    return;
                }
                case 4: 
                case 6: 
                case 12: {
                    this.fastInfosetStreamReader.next();
                    this.buffer.append(this.fastInfosetStreamReader.getTextCharacters(), this.fastInfosetStreamReader.getTextStart(), this.fastInfosetStreamReader.getTextLength());
                    continue block5;
                }
            }
            this.fastInfosetStreamReader.next();
        }
    }

    private void handleEndElement() throws SAXException {
        this.processUnreportedText();
        this.tagName.uri = this.fastInfosetStreamReader.accessNamespaceURI();
        this.tagName.local = this.fastInfosetStreamReader.accessLocalName();
        this.visitor.endElement(this.tagName);
        for (int i = this.fastInfosetStreamReader.accessNamespaceCount() - 1; i >= 0; --i) {
            this.visitor.endPrefixMapping(this.fastInfosetStreamReader.getNamespacePrefix(i));
        }
    }

    private void processNonIgnorableText() throws SAXException {
        boolean isTextAlgorithmAplied;
        this.textReported = true;
        boolean bl = isTextAlgorithmAplied = this.fastInfosetStreamReader.getTextAlgorithmBytes() != null;
        if (isTextAlgorithmAplied && this.fastInfosetStreamReader.getTextAlgorithmIndex() == 1) {
            this.base64Data.set(this.fastInfosetStreamReader.getTextAlgorithmBytesClone(), null);
            this.visitor.text(this.base64Data);
        } else {
            if (isTextAlgorithmAplied) {
                this.fastInfosetStreamReader.getText();
            }
            this.charArray.set();
            this.visitor.text(this.charArray);
        }
    }

    private void processIgnorableText() throws SAXException {
        boolean isTextAlgorithmAplied;
        boolean bl = isTextAlgorithmAplied = this.fastInfosetStreamReader.getTextAlgorithmBytes() != null;
        if (isTextAlgorithmAplied && this.fastInfosetStreamReader.getTextAlgorithmIndex() == 1) {
            this.base64Data.set(this.fastInfosetStreamReader.getTextAlgorithmBytesClone(), null);
            this.visitor.text(this.base64Data);
            this.textReported = true;
        } else {
            if (isTextAlgorithmAplied) {
                this.fastInfosetStreamReader.getText();
            }
            this.charArray.set();
            if (!WhiteSpaceProcessor.isWhiteSpace(this.charArray)) {
                this.visitor.text(this.charArray);
                this.textReported = true;
            }
        }
    }

    private void processBufferedText(boolean ignorable) throws SAXException {
        if (!ignorable || !WhiteSpaceProcessor.isWhiteSpace(this.buffer)) {
            this.visitor.text(this.buffer);
            this.textReported = true;
        }
    }

    private void processUnreportedText() throws SAXException {
        if (!this.textReported && this.predictor.expectText()) {
            this.visitor.text("");
        }
        this.textReported = false;
    }

    private final class CharSequenceImpl
    implements CharSequence {
        char[] ch;
        int start;
        int length;

        CharSequenceImpl() {
        }

        CharSequenceImpl(char[] ch, int start, int length) {
            this.ch = ch;
            this.start = start;
            this.length = length;
        }

        public void set() {
            this.ch = FastInfosetConnector.this.fastInfosetStreamReader.getTextCharacters();
            this.start = FastInfosetConnector.this.fastInfosetStreamReader.getTextStart();
            this.length = FastInfosetConnector.this.fastInfosetStreamReader.getTextLength();
        }

        @Override
        public final int length() {
            return this.length;
        }

        @Override
        public final char charAt(int index) {
            return this.ch[this.start + index];
        }

        @Override
        public final CharSequence subSequence(int start, int end) {
            return new CharSequenceImpl(this.ch, this.start + start, end - start);
        }

        @Override
        public String toString() {
            return new String(this.ch, this.start, this.length);
        }
    }
}

