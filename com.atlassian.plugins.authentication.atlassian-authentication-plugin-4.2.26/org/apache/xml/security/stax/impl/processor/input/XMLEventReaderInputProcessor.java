/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.processor.input;

import java.util.NoSuchElementException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.ConfigurationProperties;
import org.apache.xml.security.stax.ext.AbstractInputProcessor;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

public class XMLEventReaderInputProcessor
extends AbstractInputProcessor {
    private static final Integer maximumAllowedXMLStructureDepth = Integer.valueOf(ConfigurationProperties.getProperty("MaximumAllowedXMLStructureDepth"));
    private int currentXMLStructureDepth;
    private final XMLStreamReader xmlStreamReader;
    private XMLSecStartElement parentXmlSecStartElement;
    private boolean EOF = false;

    public XMLEventReaderInputProcessor(XMLSecurityProperties securityProperties, XMLStreamReader xmlStreamReader) {
        super(securityProperties);
        this.setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
        this.xmlStreamReader = xmlStreamReader;
    }

    @Override
    public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        return this.processEventInternal();
    }

    @Override
    public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        return this.processEventInternal();
    }

    private XMLSecEvent processEventInternal() throws XMLStreamException {
        XMLSecEvent xmlSecEvent = XMLSecEventFactory.allocate(this.xmlStreamReader, this.parentXmlSecStartElement);
        if (1 == xmlSecEvent.getEventType()) {
            ++this.currentXMLStructureDepth;
            if (this.currentXMLStructureDepth > maximumAllowedXMLStructureDepth) {
                XMLSecurityException xmlSecurityException = new XMLSecurityException("secureProcessing.MaximumAllowedXMLStructureDepth", new Object[]{maximumAllowedXMLStructureDepth});
                throw new XMLStreamException(xmlSecurityException);
            }
            this.parentXmlSecStartElement = (XMLSecStartElement)xmlSecEvent;
        } else if (2 == xmlSecEvent.getEventType()) {
            --this.currentXMLStructureDepth;
            if (this.parentXmlSecStartElement != null) {
                this.parentXmlSecStartElement = this.parentXmlSecStartElement.getParentXMLSecStartElement();
            }
        }
        if (this.xmlStreamReader.hasNext()) {
            this.xmlStreamReader.next();
        } else {
            if (this.EOF) {
                throw new NoSuchElementException();
            }
            this.EOF = true;
        }
        return xmlSecEvent;
    }

    @Override
    public void doFinal(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
    }
}

