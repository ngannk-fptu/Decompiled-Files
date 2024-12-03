/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.impl.llom;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;
import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.exception.OMStreamingException;
import org.apache.axiom.om.impl.llom.OMNavigator;
import org.apache.axiom.util.namespace.MapBasedNamespaceContext;
import org.apache.axiom.util.stax.AbstractXMLStreamReader;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class SwitchingWrapper
extends AbstractXMLStreamReader
implements DataHandlerReader,
CharacterDataReader,
XMLStreamConstants {
    private static final Log log = LogFactory.getLog(SwitchingWrapper.class);
    private OMNavigator navigator;
    private OMXMLParserWrapper builder;
    private XMLStreamReader parser;
    private DataHandlerReader dataHandlerReader;
    private boolean _isClosed = false;
    private boolean _releaseParserOnClose = false;
    private OMContainer rootNode;
    private boolean isFirst = true;
    private static final short NAVIGABLE = 0;
    private static final short SWITCH_AT_NEXT = 1;
    private static final short COMPLETED = 2;
    private static final short SWITCHED = 3;
    private static final short DOCUMENT_COMPLETE = 4;
    private short state;
    private int currentEvent;
    private final boolean cache;
    private final boolean preserveNamespaceContext;
    private Stack nodeStack = null;
    private OMSerializable nextNode = null;
    private OMSerializable currentNode = null;
    private OMSerializable lastNode = null;
    int depth = 0;
    private int attributeCount = -1;
    private OMAttribute[] attributes = new OMAttribute[16];
    private int namespaceCount = -1;
    private OMNamespace[] namespaces = new OMNamespace[16];

    public SwitchingWrapper(OMXMLParserWrapper builder, OMContainer startNode, boolean cache, boolean preserveNamespaceContext) {
        this.navigator = new OMNavigator(startNode);
        this.builder = builder;
        this.rootNode = startNode;
        this.cache = cache;
        this.preserveNamespaceContext = preserveNamespaceContext;
        boolean resetCache = false;
        try {
            if (startNode instanceof OMSourcedElement && !cache && builder != null) {
                if (!builder.isCache()) {
                    resetCache = true;
                }
                builder.setCache(true);
            }
        }
        catch (Throwable t) {
            // empty catch block
        }
        this.currentNode = this.navigator.getNext();
        this.updateNextNode(!cache);
        if (startNode instanceof OMDocument) {
            this.currentEvent = -1;
            try {
                this.next();
            }
            catch (XMLStreamException ex) {
                throw new OMException(ex);
            }
        } else {
            this.currentEvent = 7;
        }
        if (resetCache) {
            builder.setCache(cache);
        }
    }

    public String getPrefix() {
        if (this.parser != null && this.currentEvent != 8) {
            return this.parser.getPrefix();
        }
        if (this.currentEvent == 1 || this.currentEvent == 2) {
            OMNamespace ns = ((OMElement)this.lastNode).getNamespace();
            if (ns == null) {
                return null;
            }
            String prefix = ns.getPrefix();
            return prefix.length() == 0 ? null : prefix;
        }
        throw new IllegalStateException();
    }

    public String getNamespaceURI() {
        String returnStr;
        if (this.parser != null && this.currentEvent != 8) {
            returnStr = this.parser.getNamespaceURI();
        } else if (this.currentEvent == 1 || this.currentEvent == 2) {
            String namespaceURI;
            OMNamespace ns = ((OMElement)this.lastNode).getNamespace();
            returnStr = ns == null ? null : ((namespaceURI = ns.getNamespaceURI()).length() == 0 ? null : namespaceURI);
        } else {
            throw new IllegalStateException();
        }
        return returnStr;
    }

    public boolean hasName() {
        if (this.parser != null && this.currentEvent != 8) {
            return this.parser.hasName();
        }
        return this.currentEvent == 1 || this.currentEvent == 2;
    }

    public String getLocalName() {
        if (this.parser != null && this.currentEvent != 8) {
            return this.parser.getLocalName();
        }
        switch (this.currentEvent) {
            case 1: 
            case 2: {
                return ((OMElement)this.lastNode).getLocalName();
            }
            case 9: {
                return ((OMEntityReference)this.lastNode).getName();
            }
        }
        throw new IllegalStateException();
    }

    public QName getName() {
        if (this.parser != null && this.currentEvent != 8) {
            return this.parser.getName();
        }
        if (this.currentEvent == 1 || this.currentEvent == 2) {
            return ((OMElement)this.lastNode).getQName();
        }
        throw new IllegalStateException();
    }

    public int getTextLength() {
        if (this.parser != null) {
            return this.parser.getTextLength();
        }
        return this.getTextFromNode().length();
    }

    public int getTextStart() {
        if (this.parser != null) {
            return this.parser.getTextStart();
        }
        if (this.currentEvent == 11 || this.currentEvent == 9 || !this.hasText()) {
            throw new IllegalStateException();
        }
        return 0;
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        if (this.parser != null) {
            return this.parser.getTextCharacters(sourceStart, target, targetStart, length);
        }
        String text = this.getTextFromNode();
        int copied = Math.min(length, text.length() - sourceStart);
        text.getChars(sourceStart, sourceStart + copied, target, targetStart);
        return copied;
    }

    public char[] getTextCharacters() {
        if (this.parser != null) {
            return this.parser.getTextCharacters();
        }
        return this.getTextFromNode().toCharArray();
    }

    public String getText() {
        if (this.parser != null) {
            return this.parser.getText();
        }
        switch (this.currentEvent) {
            case 11: {
                String internalSubset = ((OMDocType)this.lastNode).getInternalSubset();
                return internalSubset != null ? internalSubset : "";
            }
            case 9: {
                return ((OMEntityReference)this.lastNode).getReplacementText();
            }
        }
        return this.getTextFromNode();
    }

    private String getTextFromNode() {
        switch (this.currentEvent) {
            case 4: 
            case 6: 
            case 12: {
                return ((OMText)this.lastNode).getText();
            }
            case 5: {
                return ((OMComment)this.lastNode).getValue();
            }
        }
        throw new IllegalStateException();
    }

    public void writeTextTo(Writer writer) throws XMLStreamException, IOException {
        if (this.parser != null) {
            XMLStreamReaderUtils.writeTextTo(this.parser, writer);
        } else {
            switch (this.currentEvent) {
                case 4: 
                case 6: 
                case 12: {
                    OMText text = (OMText)this.lastNode;
                    if (text.isCharacters()) {
                        writer.write(text.getTextCharacters());
                        break;
                    }
                    writer.write(text.getText());
                    break;
                }
                case 5: {
                    writer.write(((OMComment)this.lastNode).getValue());
                    break;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
    }

    public int getEventType() {
        return this.currentEvent;
    }

    private void loadAttributes() {
        if (this.attributeCount == -1) {
            this.attributeCount = 0;
            Iterator it = ((OMElement)this.lastNode).getAllAttributes();
            while (it.hasNext()) {
                OMAttribute attr = (OMAttribute)it.next();
                if (this.attributeCount == this.attributes.length) {
                    OMAttribute[] newAttributes = new OMAttribute[this.attributes.length * 2];
                    System.arraycopy(this.attributes, 0, newAttributes, 0, this.attributes.length);
                    this.attributes = newAttributes;
                }
                this.attributes[this.attributeCount] = attr;
                ++this.attributeCount;
            }
        }
    }

    private void loadNamespaces() {
        if (this.namespaceCount == -1) {
            this.namespaceCount = 0;
            Iterator it = ((OMElement)this.lastNode).getAllDeclaredNamespaces();
            while (it.hasNext()) {
                this.addNamespace((OMNamespace)it.next());
            }
            if (this.preserveNamespaceContext && this.lastNode == this.rootNode) {
                OMContainer container;
                OMElement element = (OMElement)this.lastNode;
                while ((container = element.getParent()) instanceof OMElement) {
                    element = (OMElement)container;
                    Iterator it2 = element.getAllDeclaredNamespaces();
                    block2: while (it2.hasNext()) {
                        OMNamespace ns = (OMNamespace)it2.next();
                        String prefix = ns.getPrefix();
                        for (int i = 0; i < this.namespaceCount; ++i) {
                            if (this.namespaces[i].getPrefix().equals(prefix)) continue block2;
                        }
                        this.addNamespace(ns);
                    }
                }
            }
        }
    }

    private void addNamespace(OMNamespace ns) {
        if (!"xml".equals(ns.getPrefix())) {
            if (this.namespaceCount == this.namespaces.length) {
                OMNamespace[] newNamespaces = new OMNamespace[this.namespaces.length * 2];
                System.arraycopy(this.namespaces, 0, newNamespaces, 0, this.namespaces.length);
                this.namespaces = newNamespaces;
            }
            this.namespaces[this.namespaceCount] = ns;
            ++this.namespaceCount;
        }
    }

    public String getNamespaceURI(int i) {
        String returnString = null;
        if (this.parser != null) {
            returnString = this.parser.getNamespaceURI(i);
        } else if (this.isStartElement() || this.isEndElement()) {
            this.loadNamespaces();
            returnString = this.namespaces[i].getNamespaceURI();
        }
        if (returnString == null) {
            returnString = "";
        }
        return returnString;
    }

    public String getNamespacePrefix(int i) {
        String returnString = null;
        if (this.parser != null) {
            returnString = this.parser.getNamespacePrefix(i);
        } else if (this.isStartElement() || this.isEndElement()) {
            this.loadNamespaces();
            String prefix = this.namespaces[i].getPrefix();
            returnString = prefix.length() == 0 ? null : prefix;
        }
        return returnString;
    }

    public int getNamespaceCount() {
        if (this.parser != null && this.currentEvent != 8) {
            return this.parser.getNamespaceCount();
        }
        if (this.isStartElement() || this.isEndElement()) {
            this.loadNamespaces();
            return this.namespaceCount;
        }
        throw new IllegalStateException();
    }

    public boolean isAttributeSpecified(int i) {
        if (this.parser != null) {
            return this.parser.isAttributeSpecified(i);
        }
        if (this.isStartElement()) {
            return true;
        }
        throw new IllegalStateException("attribute type accessed in illegal event!");
    }

    public String getAttributeValue(int i) {
        String returnString = null;
        if (this.parser != null) {
            returnString = this.parser.getAttributeValue(i);
        } else if (this.isStartElement()) {
            this.loadAttributes();
            returnString = this.attributes[i].getAttributeValue();
        } else {
            throw new IllegalStateException("attribute type accessed in illegal event!");
        }
        return returnString;
    }

    public String getAttributeType(int i) {
        String returnString = null;
        if (this.parser != null) {
            returnString = this.parser.getAttributeType(i);
        } else if (this.isStartElement()) {
            this.loadAttributes();
            returnString = this.attributes[i].getAttributeType();
        } else {
            throw new IllegalStateException("attribute type accessed in illegal event!");
        }
        return returnString;
    }

    public String getAttributePrefix(int i) {
        String returnString = null;
        if (this.parser != null) {
            returnString = this.parser.getAttributePrefix(i);
        } else if (this.isStartElement()) {
            OMNamespace nameSpace;
            this.loadAttributes();
            OMAttribute attrib = this.attributes[i];
            if (attrib != null && (nameSpace = attrib.getNamespace()) != null) {
                returnString = nameSpace.getPrefix();
            }
        } else {
            throw new IllegalStateException("attribute prefix accessed in illegal event!");
        }
        return returnString;
    }

    public String getAttributeLocalName(int i) {
        String returnString = null;
        if (this.parser != null) {
            returnString = this.parser.getAttributeLocalName(i);
        } else if (this.isStartElement()) {
            this.loadAttributes();
            returnString = this.attributes[i].getLocalName();
        } else {
            throw new IllegalStateException("attribute localName accessed in illegal event!");
        }
        return returnString;
    }

    public String getAttributeNamespace(int i) {
        String returnString = null;
        if (this.parser != null) {
            returnString = this.parser.getAttributeNamespace(i);
        } else if (this.isStartElement()) {
            OMNamespace nameSpace;
            this.loadAttributes();
            OMAttribute attrib = this.attributes[i];
            if (attrib != null && (nameSpace = attrib.getNamespace()) != null) {
                returnString = nameSpace.getNamespaceURI();
            }
        } else {
            throw new IllegalStateException("attribute nameSpace accessed in illegal event!");
        }
        return returnString;
    }

    public QName getAttributeName(int i) {
        QName returnQName = null;
        if (this.parser != null) {
            returnQName = this.parser.getAttributeName(i);
        } else if (this.isStartElement()) {
            this.loadAttributes();
            returnQName = this.attributes[i].getQName();
        } else {
            throw new IllegalStateException("attribute count accessed in illegal event!");
        }
        return returnQName;
    }

    public int getAttributeCount() {
        int returnCount = 0;
        if (this.parser != null) {
            returnCount = this.parser.getAttributeCount();
        } else if (this.isStartElement()) {
            this.loadAttributes();
            returnCount = this.attributeCount;
        } else {
            throw new IllegalStateException("attribute count accessed in illegal event (" + this.currentEvent + ")!");
        }
        return returnCount;
    }

    public String getAttributeValue(String s, String s1) {
        String returnString = null;
        if (this.parser != null) {
            returnString = this.parser.getAttributeValue(s, s1);
        } else if (this.isStartElement()) {
            QName qname = new QName(s, s1);
            OMAttribute attrib = ((OMElement)this.lastNode).getAttribute(qname);
            if (attrib != null) {
                returnString = attrib.getAttributeValue();
            }
        } else {
            throw new IllegalStateException("attribute type accessed in illegal event!");
        }
        return returnString;
    }

    public boolean isWhiteSpace() {
        if (this.parser != null) {
            return this.parser.isWhiteSpace();
        }
        return super.isWhiteSpace();
    }

    public boolean isCharacters() {
        boolean b = this.parser != null ? this.parser.isCharacters() : this.currentEvent == 4;
        return b;
    }

    public boolean isEndElement() {
        boolean b = this.parser != null && this.currentEvent != 8 ? this.parser.isEndElement() : this.currentEvent == 2;
        return b;
    }

    public boolean isStartElement() {
        boolean b = this.parser != null ? this.parser.isStartElement() : this.currentEvent == 1;
        return b;
    }

    public String getNamespaceURI(String prefix) {
        String returnString = null;
        if (this.parser != null) {
            returnString = this.parser.getNamespaceURI(prefix);
        } else if ((this.isStartElement() || this.isEndElement()) && this.lastNode instanceof OMElement) {
            OMNamespace namespaceURI = ((OMElement)this.lastNode).findNamespaceURI(prefix);
            return namespaceURI != null ? namespaceURI.getNamespaceURI() : null;
        }
        return returnString;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() throws XMLStreamException {
        if (this.builder != null && this.builder instanceof StAXBuilder) {
            StAXBuilder staxBuilder = (StAXBuilder)this.builder;
            staxBuilder.close();
            this.setParser(null);
        } else if (this.parser != null) {
            try {
                if (!this.isClosed()) {
                    this.parser.close();
                }
            }
            finally {
                this._isClosed = true;
                if (this._releaseParserOnClose) {
                    this.setParser(null);
                }
            }
        }
    }

    public boolean hasNext() throws XMLStreamException {
        return this.currentEvent != 8;
    }

    public String getElementText() throws XMLStreamException {
        if (this.parser != null) {
            String elementText = this.parser.getElementText();
            this.currentEvent = 2;
            return elementText;
        }
        return super.getElementText();
    }

    public int next() throws XMLStreamException {
        switch (this.state) {
            case 4: {
                throw new NoSuchElementException("End of the document reached");
            }
            case 2: {
                this.state = (short)4;
                this.currentEvent = 8;
                break;
            }
            case 1: {
                this.state = (short)3;
                try {
                    this.setParser((XMLStreamReader)this.builder.getParser());
                }
                catch (Exception e) {
                    throw new XMLStreamException("problem accessing the parser. " + e.getMessage(), e);
                }
                this.currentEvent = this.currentEvent == 7 && this.currentEvent == this.parser.getEventType() ? this.parser.next() : this.parser.getEventType();
                this.updateCompleteStatus();
                break;
            }
            case 0: {
                this.currentEvent = this.generateEvents(this.currentNode);
                this.updateCompleteStatus();
                this.updateLastNode();
                break;
            }
            case 3: {
                if (this.parser.hasNext()) {
                    this.currentEvent = this.parser.next();
                }
                this.updateCompleteStatus();
                break;
            }
            default: {
                throw new OMStreamingException("unsuppported state!");
            }
        }
        return this.currentEvent;
    }

    public Object getProperty(String s) throws IllegalArgumentException {
        StAXBuilder staxBuilder;
        Object value = XMLStreamReaderUtils.processGetProperty(this, s);
        if (value != null) {
            return value;
        }
        if (CharacterDataReader.PROPERTY.equals(s)) {
            return this;
        }
        if (this.parser != null) {
            return this.parser.getProperty(s);
        }
        if (this.builder != null && this.builder instanceof StAXBuilder && !(staxBuilder = (StAXBuilder)this.builder).isClosed()) {
            try {
                return ((StAXBuilder)this.builder).getReaderProperty(s);
            }
            catch (IllegalStateException ise) {
                return null;
            }
        }
        return null;
    }

    private void updateLastNode() throws XMLStreamException {
        this.lastNode = this.currentNode;
        this.attributeCount = -1;
        this.namespaceCount = -1;
        this.currentNode = this.nextNode;
        try {
            this.updateNextNode(!this.cache);
        }
        catch (Exception e) {
            throw new XMLStreamException(e);
        }
    }

    private void updateNextNode(boolean switchingAllowed) {
        if (this.navigator.isNavigable()) {
            this.nextNode = this.navigator.getNext();
        } else if (!switchingAllowed) {
            if (this.navigator.isCompleted() || this.builder == null || this.builder.isCompleted()) {
                this.nextNode = null;
                if (log.isDebugEnabled() && (this.builder == null || this.builder.isCompleted())) {
                    log.debug((Object)"Builder is complete.  Next node is set to null.");
                }
            } else {
                this.builder.next();
                this.navigator.step();
                this.nextNode = this.navigator.getNext();
            }
        } else if (this.navigator.isCompleted()) {
            this.nextNode = null;
        } else {
            if (this.builder != null) {
                this.builder.setCache(false);
            }
            this.state = 1;
        }
    }

    private void updateCompleteStatus() {
        if (this.currentEvent == 1) {
            ++this.depth;
        } else if (this.currentEvent == 2) {
            --this.depth;
        }
        if (this.state == 0) {
            if (this.rootNode == this.currentNode) {
                if (this.isFirst) {
                    this.isFirst = false;
                } else {
                    this.state = this.currentEvent == 8 ? (short)4 : (short)2;
                }
            }
        } else {
            if (this.state == 3 && this.currentEvent == 2 && this.depth == 0 && this.rootNode instanceof OMElement) {
                this.state = (short)2;
            }
            this.state = (short)(this.currentEvent == 8 ? 4 : (int)this.state);
        }
    }

    public NamespaceContext getNamespaceContext() {
        if (this.parser != null) {
            return this.currentEvent == 8 ? new MapBasedNamespaceContext(Collections.EMPTY_MAP) : this.parser.getNamespaceContext();
        }
        return new MapBasedNamespaceContext(this.currentEvent == 8 ? Collections.EMPTY_MAP : this.getAllNamespaces(this.lastNode));
    }

    public String getEncoding() {
        if (this.parser != null) {
            return this.parser.getEncoding();
        }
        if (this.currentEvent == 7) {
            if (this.lastNode instanceof OMDocument) {
                return ((OMDocument)this.lastNode).getCharsetEncoding();
            }
            return null;
        }
        throw new IllegalStateException();
    }

    public String getVersion() {
        return "1.0";
    }

    public boolean isStandalone() {
        return true;
    }

    public boolean standaloneSet() {
        return false;
    }

    public String getCharacterEncodingScheme() {
        if (this.parser != null) {
            return this.parser.getCharacterEncodingScheme();
        }
        if (this.currentEvent == 7) {
            if (this.lastNode instanceof OMDocument) {
                return ((OMDocument)this.lastNode).getXMLEncoding();
            }
            return null;
        }
        throw new IllegalStateException();
    }

    public String getPITarget() {
        if (this.parser != null) {
            return this.parser.getPITarget();
        }
        if (this.currentEvent == 3) {
            return ((OMProcessingInstruction)this.lastNode).getTarget();
        }
        throw new IllegalStateException();
    }

    public String getPIData() {
        if (this.parser != null) {
            return this.parser.getPIData();
        }
        if (this.currentEvent == 3) {
            return ((OMProcessingInstruction)this.lastNode).getValue();
        }
        throw new IllegalStateException();
    }

    public boolean isBinary() {
        if (this.parser != null) {
            if (this.dataHandlerReader != null) {
                return this.dataHandlerReader.isBinary();
            }
            return false;
        }
        if (this.lastNode instanceof OMText) {
            return ((OMText)this.lastNode).isBinary();
        }
        return false;
    }

    public boolean isOptimized() {
        if (this.parser != null) {
            if (this.dataHandlerReader != null) {
                return this.dataHandlerReader.isOptimized();
            }
            throw new IllegalStateException();
        }
        if (this.lastNode instanceof OMText) {
            return ((OMText)this.lastNode).isOptimized();
        }
        throw new IllegalStateException();
    }

    public boolean isDeferred() {
        if (this.parser != null) {
            if (this.dataHandlerReader != null) {
                return this.dataHandlerReader.isDeferred();
            }
            throw new IllegalStateException();
        }
        if (this.lastNode instanceof OMText) {
            return false;
        }
        throw new IllegalStateException();
    }

    public String getContentID() {
        if (this.parser != null) {
            if (this.dataHandlerReader != null) {
                return this.dataHandlerReader.getContentID();
            }
            throw new IllegalStateException();
        }
        if (this.lastNode instanceof OMText) {
            return ((OMText)this.lastNode).getContentID();
        }
        throw new IllegalStateException();
    }

    public DataHandler getDataHandler() throws XMLStreamException {
        if (this.parser != null) {
            if (this.dataHandlerReader != null) {
                return this.dataHandlerReader.getDataHandler();
            }
            throw new IllegalStateException();
        }
        if (this.lastNode instanceof OMText) {
            return (DataHandler)((OMText)this.lastNode).getDataHandler();
        }
        throw new IllegalStateException();
    }

    public DataHandlerProvider getDataHandlerProvider() {
        if (this.parser != null) {
            if (this.dataHandlerReader != null) {
                return this.dataHandlerReader.getDataHandlerProvider();
            }
            throw new IllegalStateException();
        }
        throw new IllegalStateException();
    }

    private int generateEvents(OMSerializable node) {
        if (node == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Node is null...returning END_DOCUMENT");
            }
            return 8;
        }
        if (node instanceof OMDocument) {
            return this.generateContainerEvents((OMDocument)node, true);
        }
        int nodeType = ((OMNode)node).getType();
        if (nodeType == 1) {
            return this.generateContainerEvents((OMElement)node, false);
        }
        return nodeType;
    }

    private int generateContainerEvents(OMContainer container, boolean isDocument) {
        if (this.nodeStack == null) {
            this.nodeStack = new Stack();
        }
        if (!this.nodeStack.isEmpty() && this.nodeStack.peek().equals(container)) {
            this.nodeStack.pop();
            return isDocument ? 8 : 2;
        }
        this.nodeStack.push(container);
        return isDocument ? 7 : 1;
    }

    private void setParser(XMLStreamReader parser) {
        this.parser = parser;
        this.dataHandlerReader = parser == null ? null : XMLStreamReaderUtils.getDataHandlerReader(parser);
    }

    private Map getAllNamespaces(OMSerializable contextNode) {
        if (contextNode == null) {
            return Collections.EMPTY_MAP;
        }
        OMContainer context = contextNode instanceof OMContainer ? (OMContainer)contextNode : ((OMNode)contextNode).getParent();
        LinkedHashMap nsMap = new LinkedHashMap();
        while (context != null && !(context instanceof OMDocument)) {
            OMElement element = (OMElement)context;
            Iterator i = element.getAllDeclaredNamespaces();
            while (i != null && i.hasNext()) {
                this.addNamespaceToMap((OMNamespace)i.next(), nsMap);
            }
            if (element.getNamespace() != null) {
                this.addNamespaceToMap(element.getNamespace(), nsMap);
            }
            Iterator iter = element.getAllAttributes();
            while (iter != null && iter.hasNext()) {
                OMAttribute attr = (OMAttribute)iter.next();
                if (attr.getNamespace() == null) continue;
                this.addNamespaceToMap(attr.getNamespace(), nsMap);
            }
            context = element.getParent();
        }
        return nsMap;
    }

    private void addNamespaceToMap(OMNamespace ns, Map map) {
        if (map.get(ns.getPrefix()) == null) {
            map.put(ns.getPrefix(), ns.getNamespaceURI());
        }
    }

    public boolean isClosed() {
        if (this.builder != null && this.builder instanceof StAXBuilder) {
            return ((StAXBuilder)this.builder).isClosed();
        }
        return this._isClosed;
    }

    public void releaseParserOnClose(boolean value) {
        if (this.builder != null && this.builder instanceof StAXBuilder) {
            ((StAXBuilder)this.builder).releaseParserOnClose(value);
            if (this.isClosed() && value) {
                this.setParser(null);
            }
            return;
        }
        if (this.isClosed() && value) {
            this.setParser(null);
        }
        this._releaseParserOnClose = value;
    }

    public OMDataSource getDataSource() {
        block11: {
            block10: {
                if (this.getEventType() != 1) break block10;
                if (this.state == 0) break block11;
                if (this.state == 1) break block11;
            }
            return null;
        }
        OMDataSource ds = null;
        if (this.lastNode != null && this.lastNode instanceof OMSourcedElement) {
            try {
                ds = ((OMSourcedElement)this.lastNode).getDataSource();
            }
            catch (UnsupportedOperationException e) {
                ds = null;
            }
            if (log.isDebugEnabled()) {
                if (ds != null) {
                    log.debug((Object)("OMSourcedElement exposed an OMDataSource." + ds));
                } else {
                    log.debug((Object)"OMSourcedElement does not have a OMDataSource.");
                }
            }
        }
        return ds;
    }

    public void enableDataSourceEvents(boolean value) {
        this.navigator.setDataSourceIsLeaf(value);
    }
}

