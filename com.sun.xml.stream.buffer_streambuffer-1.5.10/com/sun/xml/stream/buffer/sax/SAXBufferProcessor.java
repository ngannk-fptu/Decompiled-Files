/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.stream.buffer.sax;

import com.sun.xml.stream.buffer.AbstractProcessor;
import com.sun.xml.stream.buffer.AttributesHolder;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.sax.DefaultWithLexicalHandler;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.LocatorImpl;

public class SAXBufferProcessor
extends AbstractProcessor
implements XMLReader {
    protected EntityResolver _entityResolver = DEFAULT_LEXICAL_HANDLER;
    protected DTDHandler _dtdHandler = DEFAULT_LEXICAL_HANDLER;
    protected ContentHandler _contentHandler = DEFAULT_LEXICAL_HANDLER;
    protected ErrorHandler _errorHandler = DEFAULT_LEXICAL_HANDLER;
    protected LexicalHandler _lexicalHandler = DEFAULT_LEXICAL_HANDLER;
    protected boolean _namespacePrefixesFeature = false;
    protected AttributesHolder _attributes = new AttributesHolder();
    protected String[] _namespacePrefixes = new String[16];
    protected int _namespacePrefixesIndex;
    protected int[] _namespaceAttributesStartingStack = new int[16];
    protected int[] _namespaceAttributesStack = new int[16];
    protected int _namespaceAttributesStackIndex;
    private static final DefaultWithLexicalHandler DEFAULT_LEXICAL_HANDLER = new DefaultWithLexicalHandler();

    public SAXBufferProcessor() {
    }

    public SAXBufferProcessor(XMLStreamBuffer buffer) {
        this.setXMLStreamBuffer(buffer);
    }

    public SAXBufferProcessor(XMLStreamBuffer buffer, boolean produceFragmentEvent) {
        this.setXMLStreamBuffer(buffer, produceFragmentEvent);
    }

    @Override
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/features/namespaces")) {
            return true;
        }
        if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
            return this._namespacePrefixesFeature;
        }
        if (name.equals("http://xml.org/sax/features/external-general-entities")) {
            return true;
        }
        if (name.equals("http://xml.org/sax/features/external-parameter-entities")) {
            return true;
        }
        if (name.equals("http://xml.org/sax/features/string-interning")) {
            return this._stringInterningFeature;
        }
        throw new SAXNotRecognizedException("Feature not supported: " + name);
    }

    @Override
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/features/namespaces")) {
            if (!value) {
                throw new SAXNotSupportedException(name + ":" + value);
            }
        } else if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
            this._namespacePrefixesFeature = value;
        } else if (!name.equals("http://xml.org/sax/features/external-general-entities") && !name.equals("http://xml.org/sax/features/external-parameter-entities")) {
            if (name.equals("http://xml.org/sax/features/string-interning")) {
                if (value != this._stringInterningFeature) {
                    throw new SAXNotSupportedException(name + ":" + value);
                }
            } else {
                throw new SAXNotRecognizedException("Feature not supported: " + name);
            }
        }
    }

    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/properties/lexical-handler")) {
            return this.getLexicalHandler();
        }
        throw new SAXNotRecognizedException("Property not recognized: " + name);
    }

    @Override
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/properties/lexical-handler")) {
            if (!(value instanceof LexicalHandler)) {
                throw new SAXNotSupportedException("http://xml.org/sax/properties/lexical-handler");
            }
        } else {
            throw new SAXNotRecognizedException("Property not recognized: " + name);
        }
        this.setLexicalHandler((LexicalHandler)value);
    }

    @Override
    public void setEntityResolver(EntityResolver resolver) {
        this._entityResolver = resolver;
    }

    @Override
    public EntityResolver getEntityResolver() {
        return this._entityResolver;
    }

    @Override
    public void setDTDHandler(DTDHandler handler) {
        this._dtdHandler = handler;
    }

    @Override
    public DTDHandler getDTDHandler() {
        return this._dtdHandler;
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
        this._contentHandler = handler;
    }

    @Override
    public ContentHandler getContentHandler() {
        return this._contentHandler;
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) {
        this._errorHandler = handler;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return this._errorHandler;
    }

    public void setLexicalHandler(LexicalHandler handler) {
        this._lexicalHandler = handler;
    }

    public LexicalHandler getLexicalHandler() {
        return this._lexicalHandler;
    }

    @Override
    public void parse(InputSource input) throws IOException, SAXException {
        this.process();
    }

    @Override
    public void parse(String systemId) throws IOException, SAXException {
        this.process();
    }

    public final void process(XMLStreamBuffer buffer) throws SAXException {
        this.setXMLStreamBuffer(buffer);
        this.process();
    }

    public final void process(XMLStreamBuffer buffer, boolean produceFragmentEvent) throws SAXException {
        this.setXMLStreamBuffer(buffer);
        this.process();
    }

    public void setXMLStreamBuffer(XMLStreamBuffer buffer) {
        this.setBuffer(buffer);
    }

    public void setXMLStreamBuffer(XMLStreamBuffer buffer, boolean produceFragmentEvent) {
        if (!produceFragmentEvent && this._treeCount > 1) {
            throw new IllegalStateException("Can't write a forest to a full XML infoset");
        }
        this.setBuffer(buffer, produceFragmentEvent);
    }

    public final void process() throws SAXException {
        if (!this._fragmentMode) {
            LocatorImpl nullLocator = new LocatorImpl();
            nullLocator.setSystemId(this._buffer.getSystemId());
            nullLocator.setLineNumber(-1);
            nullLocator.setColumnNumber(-1);
            this._contentHandler.setDocumentLocator(nullLocator);
            this._contentHandler.startDocument();
        }
        block13: while (this._treeCount > 0) {
            int item = this.readEiiState();
            switch (item) {
                case 1: {
                    this.processDocument();
                    --this._treeCount;
                    continue block13;
                }
                case 17: {
                    return;
                }
                case 3: {
                    this.processElement(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.isInscope());
                    --this._treeCount;
                    continue block13;
                }
                case 4: {
                    String prefix = this.readStructureString();
                    String uri = this.readStructureString();
                    String localName = this.readStructureString();
                    this.processElement(uri, localName, this.getQName(prefix, localName), this.isInscope());
                    --this._treeCount;
                    continue block13;
                }
                case 5: {
                    String uri = this.readStructureString();
                    String localName = this.readStructureString();
                    this.processElement(uri, localName, localName, this.isInscope());
                    --this._treeCount;
                    continue block13;
                }
                case 6: {
                    String localName = this.readStructureString();
                    this.processElement("", localName, localName, this.isInscope());
                    --this._treeCount;
                    continue block13;
                }
                case 12: {
                    this.processCommentAsCharArraySmall();
                    continue block13;
                }
                case 13: {
                    this.processCommentAsCharArrayMedium();
                    continue block13;
                }
                case 14: {
                    this.processCommentAsCharArrayCopy();
                    continue block13;
                }
                case 15: {
                    this.processComment(this.readContentString());
                    continue block13;
                }
                case 16: {
                    this.processProcessingInstruction(this.readStructureString(), this.readStructureString());
                    continue block13;
                }
            }
            throw this.reportFatalError("Illegal state for DIIs: " + item);
        }
        if (!this._fragmentMode) {
            this._contentHandler.endDocument();
        }
    }

    private void processCommentAsCharArraySmall() throws SAXException {
        int length = this.readStructure();
        int start = this.readContentCharactersBuffer(length);
        this.processComment(this._contentCharactersBuffer, start, length);
    }

    private SAXParseException reportFatalError(String msg) throws SAXException {
        SAXParseException spe = new SAXParseException(msg, null);
        if (this._errorHandler != null) {
            this._errorHandler.fatalError(spe);
        }
        return spe;
    }

    private boolean isInscope() {
        return this._buffer.getInscopeNamespaces().size() > 0;
    }

    private void processDocument() throws SAXException {
        int item;
        block12: while (true) {
            item = this.readEiiState();
            switch (item) {
                case 3: {
                    this.processElement(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.isInscope());
                    continue block12;
                }
                case 4: {
                    String prefix = this.readStructureString();
                    String uri = this.readStructureString();
                    String localName = this.readStructureString();
                    this.processElement(uri, localName, this.getQName(prefix, localName), this.isInscope());
                    continue block12;
                }
                case 5: {
                    String uri = this.readStructureString();
                    String localName = this.readStructureString();
                    this.processElement(uri, localName, localName, this.isInscope());
                    continue block12;
                }
                case 6: {
                    String localName = this.readStructureString();
                    this.processElement("", localName, localName, this.isInscope());
                    continue block12;
                }
                case 12: {
                    this.processCommentAsCharArraySmall();
                    continue block12;
                }
                case 13: {
                    this.processCommentAsCharArrayMedium();
                    continue block12;
                }
                case 14: {
                    this.processCommentAsCharArrayCopy();
                    continue block12;
                }
                case 15: {
                    this.processComment(this.readContentString());
                    continue block12;
                }
                case 16: {
                    this.processProcessingInstruction(this.readStructureString(), this.readStructureString());
                    continue block12;
                }
                case 17: {
                    return;
                }
            }
            break;
        }
        throw this.reportFatalError("Illegal state for child of DII: " + item);
    }

    protected void processElement(String uri, String localName, String qName, boolean inscope) throws SAXException {
        HashSet<String> prefixSet;
        boolean hasAttributes = false;
        boolean hasNamespaceAttributes = false;
        int item = this.peekStructure();
        HashSet<String> hashSet = prefixSet = inscope ? new HashSet<String>() : Collections.emptySet();
        if ((item & 0xF0) == 64) {
            this.cacheNamespacePrefixStartingIndex();
            hasNamespaceAttributes = true;
            item = this.processNamespaceAttributes(item, inscope, prefixSet);
        }
        if (inscope) {
            this.readInscopeNamespaces(prefixSet);
        }
        if ((item & 0xF0) == 48) {
            hasAttributes = true;
            this.processAttributes(item);
        }
        this._contentHandler.startElement(uri, localName, qName, this._attributes);
        if (hasAttributes) {
            this._attributes.clear();
        }
        do {
            item = this.readEiiState();
            switch (item) {
                case 3: {
                    this.processElement(this.readStructureString(), this.readStructureString(), this.readStructureString(), false);
                    break;
                }
                case 4: {
                    String p = this.readStructureString();
                    String u = this.readStructureString();
                    String ln = this.readStructureString();
                    this.processElement(u, ln, this.getQName(p, ln), false);
                    break;
                }
                case 5: {
                    String u = this.readStructureString();
                    String ln = this.readStructureString();
                    this.processElement(u, ln, ln, false);
                    break;
                }
                case 6: {
                    String ln = this.readStructureString();
                    this.processElement("", ln, ln, false);
                    break;
                }
                case 7: {
                    int length = this.readStructure();
                    int start = this.readContentCharactersBuffer(length);
                    this._contentHandler.characters(this._contentCharactersBuffer, start, length);
                    break;
                }
                case 8: {
                    int length = this.readStructure16();
                    int start = this.readContentCharactersBuffer(length);
                    this._contentHandler.characters(this._contentCharactersBuffer, start, length);
                    break;
                }
                case 9: {
                    char[] ch = this.readContentCharactersCopy();
                    this._contentHandler.characters(ch, 0, ch.length);
                    break;
                }
                case 10: {
                    String s = this.readContentString();
                    this._contentHandler.characters(s.toCharArray(), 0, s.length());
                    break;
                }
                case 11: {
                    CharSequence c = (CharSequence)this.readContentObject();
                    String s = c.toString();
                    this._contentHandler.characters(s.toCharArray(), 0, s.length());
                    break;
                }
                case 12: {
                    this.processCommentAsCharArraySmall();
                    break;
                }
                case 13: {
                    this.processCommentAsCharArrayMedium();
                    break;
                }
                case 14: {
                    this.processCommentAsCharArrayCopy();
                    break;
                }
                case 104: {
                    this.processComment(this.readContentString());
                    break;
                }
                case 16: {
                    this.processProcessingInstruction(this.readStructureString(), this.readStructureString());
                    break;
                }
                case 17: {
                    break;
                }
                default: {
                    throw this.reportFatalError("Illegal state for child of EII: " + item);
                }
            }
        } while (item != 17);
        this._contentHandler.endElement(uri, localName, qName);
        if (hasNamespaceAttributes) {
            this.processEndPrefixMapping();
        }
    }

    private void readInscopeNamespaces(Set<String> prefixSet) throws SAXException {
        for (Map.Entry<String, String> e : this._buffer.getInscopeNamespaces().entrySet()) {
            String key = SAXBufferProcessor.fixNull(e.getKey());
            if (prefixSet.contains(key)) continue;
            this.processNamespaceAttribute(key, e.getValue());
        }
    }

    private static String fixNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    private void processCommentAsCharArrayCopy() throws SAXException {
        char[] ch = this.readContentCharactersCopy();
        this.processComment(ch, 0, ch.length);
    }

    private void processCommentAsCharArrayMedium() throws SAXException {
        int length = this.readStructure16();
        int start = this.readContentCharactersBuffer(length);
        this.processComment(this._contentCharactersBuffer, start, length);
    }

    private void processEndPrefixMapping() throws SAXException {
        int end = this._namespaceAttributesStack[--this._namespaceAttributesStackIndex];
        int start = this._namespaceAttributesStackIndex >= 0 ? this._namespaceAttributesStartingStack[this._namespaceAttributesStackIndex] : 0;
        for (int i = end - 1; i >= start; --i) {
            this._contentHandler.endPrefixMapping(this._namespacePrefixes[i]);
        }
        this._namespacePrefixesIndex = start;
    }

    private int processNamespaceAttributes(int item, boolean collectPrefixes, Set<String> prefixSet) throws SAXException {
        do {
            switch (SAXBufferProcessor.getNIIState(item)) {
                case 1: {
                    this.processNamespaceAttribute("", "");
                    if (!collectPrefixes) break;
                    prefixSet.add("");
                    break;
                }
                case 2: {
                    String prefix = this.readStructureString();
                    this.processNamespaceAttribute(prefix, "");
                    if (!collectPrefixes) break;
                    prefixSet.add(prefix);
                    break;
                }
                case 3: {
                    String prefix = this.readStructureString();
                    this.processNamespaceAttribute(prefix, this.readStructureString());
                    if (!collectPrefixes) break;
                    prefixSet.add(prefix);
                    break;
                }
                case 4: {
                    this.processNamespaceAttribute("", this.readStructureString());
                    if (!collectPrefixes) break;
                    prefixSet.add("");
                    break;
                }
                default: {
                    throw this.reportFatalError("Illegal state: " + item);
                }
            }
            this.readStructure();
        } while (((item = this.peekStructure()) & 0xF0) == 64);
        this.cacheNamespacePrefixIndex();
        return item;
    }

    private void processAttributes(int item) throws SAXException {
        do {
            switch (SAXBufferProcessor.getAIIState(item)) {
                case 1: {
                    this._attributes.addAttributeWithQName(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readContentString());
                    break;
                }
                case 2: {
                    String p = this.readStructureString();
                    String u = this.readStructureString();
                    String ln = this.readStructureString();
                    this._attributes.addAttributeWithQName(u, ln, this.getQName(p, ln), this.readStructureString(), this.readContentString());
                    break;
                }
                case 3: {
                    String u = this.readStructureString();
                    String ln = this.readStructureString();
                    this._attributes.addAttributeWithQName(u, ln, ln, this.readStructureString(), this.readContentString());
                    break;
                }
                case 4: {
                    String ln = this.readStructureString();
                    this._attributes.addAttributeWithQName("", ln, ln, this.readStructureString(), this.readContentString());
                    break;
                }
                default: {
                    throw this.reportFatalError("Illegal state: " + item);
                }
            }
            this.readStructure();
        } while (((item = this.peekStructure()) & 0xF0) == 48);
    }

    private void processNamespaceAttribute(String prefix, String uri) throws SAXException {
        this._contentHandler.startPrefixMapping(prefix, uri);
        if (this._namespacePrefixesFeature) {
            if (prefix != "") {
                this._attributes.addAttributeWithQName("http://www.w3.org/2000/xmlns/", prefix, this.getQName("xmlns", prefix), "CDATA", uri);
            } else {
                this._attributes.addAttributeWithQName("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns", "CDATA", uri);
            }
        }
        this.cacheNamespacePrefix(prefix);
    }

    private void cacheNamespacePrefix(String prefix) {
        if (this._namespacePrefixesIndex == this._namespacePrefixes.length) {
            String[] namespaceAttributes = new String[this._namespacePrefixesIndex * 3 / 2 + 1];
            System.arraycopy(this._namespacePrefixes, 0, namespaceAttributes, 0, this._namespacePrefixesIndex);
            this._namespacePrefixes = namespaceAttributes;
        }
        this._namespacePrefixes[this._namespacePrefixesIndex++] = prefix;
    }

    private void cacheNamespacePrefixIndex() {
        if (this._namespaceAttributesStackIndex == this._namespaceAttributesStack.length) {
            int[] namespaceAttributesStack = new int[this._namespaceAttributesStackIndex * 3 / 2 + 1];
            System.arraycopy(this._namespaceAttributesStack, 0, namespaceAttributesStack, 0, this._namespaceAttributesStackIndex);
            this._namespaceAttributesStack = namespaceAttributesStack;
        }
        this._namespaceAttributesStack[this._namespaceAttributesStackIndex++] = this._namespacePrefixesIndex;
    }

    private void cacheNamespacePrefixStartingIndex() {
        if (this._namespaceAttributesStackIndex == this._namespaceAttributesStartingStack.length) {
            int[] namespaceAttributesStart = new int[this._namespaceAttributesStackIndex * 3 / 2 + 1];
            System.arraycopy(this._namespaceAttributesStartingStack, 0, namespaceAttributesStart, 0, this._namespaceAttributesStackIndex);
            this._namespaceAttributesStartingStack = namespaceAttributesStart;
        }
        this._namespaceAttributesStartingStack[this._namespaceAttributesStackIndex] = this._namespacePrefixesIndex;
    }

    private void processComment(String s) throws SAXException {
        this.processComment(s.toCharArray(), 0, s.length());
    }

    private void processComment(char[] ch, int start, int length) throws SAXException {
        this._lexicalHandler.comment(ch, start, length);
    }

    private void processProcessingInstruction(String target, String data) throws SAXException {
        this._contentHandler.processingInstruction(target, data);
    }
}

