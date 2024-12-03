/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.impl.builder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMHierarchyException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMElementEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.BuilderUtil;
import org.apache.axiom.om.impl.builder.CustomBuilder;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.util.stax.XMLEventUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StAXOMBuilder
extends StAXBuilder {
    private static final Log log = LogFactory.getLog(StAXOMBuilder.class);
    private boolean doTrace = log.isDebugEnabled();
    private static int nsCount = 0;
    private boolean namespaceURIInterning = false;
    private int lookAheadToken = -1;

    public StAXOMBuilder(OMFactory ombuilderFactory, XMLStreamReader parser) {
        super(ombuilderFactory, parser);
    }

    public StAXOMBuilder(OMFactory factory, XMLStreamReader parser, OMElement element, String characterEncoding) {
        super(factory, parser, characterEncoding);
        this.target = (OMContainerEx)((Object)element);
        this.populateOMElement(element);
    }

    public StAXOMBuilder(OMFactory factory, XMLStreamReader parser, OMElement element) {
        this(factory, parser, element, null);
    }

    public StAXOMBuilder(String filePath) throws XMLStreamException, FileNotFoundException {
        this(StAXUtils.createXMLStreamReader(new FileInputStream(filePath)));
    }

    public StAXOMBuilder(XMLStreamReader parser) {
        this(OMAbstractFactory.getOMFactory(), parser);
    }

    public StAXOMBuilder(InputStream inStream) throws XMLStreamException {
        this(StAXUtils.createXMLStreamReader(inStream));
    }

    public StAXOMBuilder() {
    }

    protected OMDocument createDocument() {
        return this.omfactory.createOMDocument(this);
    }

    public int next() throws OMException {
        try {
            int token;
            block16: while (true) {
                int currentParserToken;
                if (this.done) {
                    throw new OMException();
                }
                this.createDocumentIfNecessary();
                token = this.parserNext();
                if (!this.cache) {
                    return token;
                }
                if (this.doTrace && (currentParserToken = this.parser.getEventType()) != token) {
                    log.debug((Object)("WARNING: The current state of the parser is not equal to the state just received from the parser. The current state in the paser is " + XMLEventUtils.getEventTypeString(currentParserToken) + " the state just received is " + XMLEventUtils.getEventTypeString(token)));
                }
                if (this.doTrace) {
                    this.logParserState();
                }
                switch (token) {
                    case 1: {
                        OMNode node = this.createNextOMElement();
                        if (node.isComplete()) break block16;
                        this.target = (OMContainerEx)((Object)node);
                        break block16;
                    }
                    case 4: {
                        this.createOMText(4);
                        break block16;
                    }
                    case 12: {
                        this.createOMText(12);
                        break block16;
                    }
                    case 2: {
                        this.endElement();
                        break block16;
                    }
                    case 8: {
                        this.done = true;
                        ((OMContainerEx)((Object)this.document)).setComplete(true);
                        this.target = null;
                        break block16;
                    }
                    case 6: {
                        try {
                            OMNode node = this.createOMText(6);
                            if (node != null) break block16;
                        }
                        catch (OMHierarchyException ex) {}
                        continue block16;
                    }
                    case 5: {
                        this.createComment();
                        break block16;
                    }
                    case 11: {
                        this.createDTD();
                        break block16;
                    }
                    case 3: {
                        this.createPI();
                        break block16;
                    }
                    case 9: {
                        this.createEntityReference();
                        break block16;
                    }
                    default: {
                        throw new OMException();
                    }
                }
                break;
            }
            return token;
        }
        catch (XMLStreamException e) {
            throw new OMException(e);
        }
    }

    protected OMNode createNextOMElement() {
        String localPart;
        String namespace;
        CustomBuilder customBuilder;
        OMNode newElement = null;
        if (this.elementLevel == 1 && this.customBuilderForPayload != null) {
            newElement = this.createWithCustomBuilder(this.customBuilderForPayload, this.omfactory);
        } else if (this.customBuilders != null && this.elementLevel <= this.maxDepthForCustomBuilders && (customBuilder = this.getCustomBuilder(namespace = this.parser.getNamespaceURI(), localPart = this.parser.getLocalName())) != null) {
            newElement = this.createWithCustomBuilder(customBuilder, this.omfactory);
        }
        if (newElement == null) {
            newElement = this.createOMElement();
        } else {
            --this.elementLevel;
        }
        return newElement;
    }

    protected OMNode createWithCustomBuilder(CustomBuilder customBuilder, OMFactory factory) {
        String namespace = this.parser.getNamespaceURI();
        String localPart = this.parser.getLocalName();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Invoking CustomBuilder, " + customBuilder.toString() + ", to the OMNode for {" + namespace + "}" + localPart));
        }
        this.target.setComplete(true);
        OMElement node = customBuilder.create(namespace, localPart, this.target, this.parser, factory);
        this.target.setComplete(false);
        if (log.isDebugEnabled()) {
            if (node != null) {
                log.debug((Object)("The CustomBuilder, " + customBuilder.toString() + "successfully constructed the OMNode for {" + namespace + "}" + localPart));
            } else {
                log.debug((Object)("The CustomBuilder, " + customBuilder.toString() + " did not construct an OMNode for {" + namespace + "}" + localPart + ". The OMNode will be constructed using the installed stax om builder"));
            }
            log.debug((Object)"The current state of the parser is: ");
            this.logParserState();
        }
        return node;
    }

    protected void logParserState() {
        if (this.doTrace) {
            int currentEvent = this.parser.getEventType();
            switch (currentEvent) {
                case 1: {
                    log.trace((Object)"START_ELEMENT: ");
                    log.trace((Object)("  QName: " + this.parser.getName()));
                    break;
                }
                case 7: {
                    log.trace((Object)"START_DOCUMENT: ");
                    break;
                }
                case 4: {
                    log.trace((Object)"CHARACTERS: ");
                    break;
                }
                case 12: {
                    log.trace((Object)"CDATA: ");
                    break;
                }
                case 2: {
                    log.trace((Object)"END_ELEMENT: ");
                    log.trace((Object)("  QName: " + this.parser.getName()));
                    break;
                }
                case 8: {
                    log.trace((Object)"END_DOCUMENT: ");
                    break;
                }
                case 6: {
                    log.trace((Object)"SPACE: ");
                    break;
                }
                case 5: {
                    log.trace((Object)"COMMENT: ");
                    break;
                }
                case 11: {
                    log.trace((Object)"DTD: ");
                    log.trace((Object)("[" + this.parser.getText() + "]"));
                    break;
                }
                case 3: {
                    log.trace((Object)"PROCESSING_INSTRUCTION: ");
                    log.trace((Object)("   [" + this.parser.getPITarget() + "][" + this.parser.getPIData() + "]"));
                    break;
                }
                case 9: {
                    log.trace((Object)"ENTITY_REFERENCE: ");
                    log.trace((Object)("    " + this.parser.getLocalName() + "[" + this.parser.getText() + "]"));
                    break;
                }
                default: {
                    log.trace((Object)("UNKNOWN_STATE: " + currentEvent));
                }
            }
        }
    }

    private void populateOMElement(OMElement node) {
        this.processNamespaceData(node);
        this.processAttributes(node);
        Location location = this.parser.getLocation();
        if (location != null) {
            node.setLineNumber(location.getLineNumber());
        }
    }

    protected final OMNode createOMElement() throws OMException {
        OMElement node = this.constructNode(this.target, this.parser.getLocalName());
        this.populateOMElement(node);
        return node;
    }

    protected OMElement constructNode(OMContainer parent, String elementName) {
        return this.omfactory.createOMElement(this.parser.getLocalName(), this.target, this);
    }

    protected OMNode createComment() throws OMException {
        return this.omfactory.createOMComment(this.target, this.parser.getText(), true);
    }

    protected OMNode createDTD() throws OMException {
        DTDReader dtdReader;
        try {
            dtdReader = (DTDReader)this.parser.getProperty(DTDReader.PROPERTY);
        }
        catch (IllegalArgumentException ex) {
            dtdReader = null;
        }
        if (dtdReader == null) {
            throw new OMException("Cannot create OMDocType because the XMLStreamReader doesn't support the DTDReader extension");
        }
        String internalSubset = this.getDTDText();
        if (internalSubset != null && internalSubset.length() == 0) {
            internalSubset = null;
        }
        return this.omfactory.createOMDocType(this.target, dtdReader.getRootName(), dtdReader.getPublicId(), dtdReader.getSystemId(), internalSubset, true);
    }

    private String getDTDText() throws OMException {
        String text;
        block3: {
            text = null;
            try {
                text = this.parser.getText();
            }
            catch (RuntimeException e) {
                Boolean b = (Boolean)this.parser.getProperty("javax.xml.stream.isSupportingExternalEntities");
                if (b == null || b == Boolean.TRUE) {
                    throw e;
                }
                if (!log.isDebugEnabled()) break block3;
                log.debug((Object)("An exception occurred while calling getText() for a DOCTYPE.  The exception is ignored because external entites support is disabled.  The ignored exception is " + e));
            }
        }
        return text;
    }

    protected OMNode createPI() throws OMException {
        return this.omfactory.createOMProcessingInstruction(this.target, this.parser.getPITarget(), this.parser.getPIData(), true);
    }

    protected OMNode createEntityReference() {
        return this.omfactory.createOMEntityReference(this.target, this.parser.getLocalName(), this.parser.getText(), true);
    }

    private void endElement() {
        this.target.setComplete(true);
        this.target = (OMContainerEx)((OMElement)((Object)this.target)).getParent();
    }

    public OMElement getDocumentElement() {
        return this.getDocumentElement(false);
    }

    public OMElement getDocumentElement(boolean discardDocument) {
        OMElement element = this.getDocument().getOMDocumentElement();
        if (discardDocument) {
            OMNodeEx nodeEx = (OMNodeEx)((Object)element);
            nodeEx.setParent(null);
            nodeEx.setPreviousOMSibling(null);
            nodeEx.setNextOMSibling(null);
        }
        return element;
    }

    protected void processNamespaceData(OMElement node) {
        String prefix;
        int namespaceCount = this.parser.getNamespaceCount();
        for (int i = 0; i < namespaceCount; ++i) {
            prefix = this.parser.getNamespacePrefix(i);
            String namespaceURI = this.parser.getNamespaceURI(i);
            if (namespaceURI == null) {
                namespaceURI = "";
            } else if (this.isNamespaceURIInterning()) {
                namespaceURI = namespaceURI.intern();
            }
            if (prefix == null) {
                prefix = "";
            }
            ((OMElementEx)node).addNamespaceDeclaration(namespaceURI, prefix);
        }
        String namespaceURI = this.parser.getNamespaceURI();
        prefix = this.parser.getPrefix();
        BuilderUtil.setNamespace(node, namespaceURI, prefix, this.isNamespaceURIInterning());
    }

    public void setDoDebug(boolean doDebug) {
        this.doTrace = doDebug;
    }

    protected String createPrefix() {
        return "ns" + nsCount++;
    }

    public void setNamespaceURIInterning(boolean b) {
        this.namespaceURIInterning = b;
    }

    public boolean isNamespaceURIInterning() {
        return this.namespaceURIInterning;
    }

    int parserNext() throws XMLStreamException {
        int event;
        if (this.lookAheadToken >= 0) {
            int token = this.lookAheadToken;
            this.lookAheadToken = -1;
            return token;
        }
        if (this.parserException != null) {
            log.warn((Object)"Attempt to access a parser that has thrown a parse exception before; rethrowing the original exception.");
            if (this.parserException instanceof XMLStreamException) {
                throw (XMLStreamException)this.parserException;
            }
            throw (RuntimeException)this.parserException;
        }
        try {
            event = this.parser.next();
        }
        catch (XMLStreamException ex) {
            this.parserException = ex;
            throw ex;
        }
        switch (event) {
            case 1: {
                ++this.elementLevel;
                break;
            }
            case 2: {
                --this.elementLevel;
                break;
            }
            case 8: {
                if (this.elementLevel == 0) break;
                throw new OMException("Unexpected END_DOCUMENT event");
            }
        }
        return event;
    }

    public boolean lookahead() {
        try {
            while (true) {
                if (this.lookAheadToken < 0) {
                    this.lookAheadToken = this.parserNext();
                }
                if (this.lookAheadToken == 1) {
                    return true;
                }
                if (this.lookAheadToken == 2 || this.lookAheadToken == 7 || this.lookAheadToken == 8) {
                    this.next();
                    return false;
                }
                this.next();
            }
        }
        catch (XMLStreamException e) {
            throw new OMException(e);
        }
    }

    public boolean isLookahead() {
        return this.lookAheadToken >= 0;
    }
}

