/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.stax.ext;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.InputProcessor;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.impl.DocumentContextImpl;
import org.apache.xml.security.stax.impl.InboundSecurityContextImpl;
import org.apache.xml.security.stax.impl.InputProcessorChainImpl;
import org.apache.xml.security.stax.impl.XMLSecurityStreamReader;
import org.apache.xml.security.stax.impl.processor.input.LogInputProcessor;
import org.apache.xml.security.stax.impl.processor.input.XMLEventReaderInputProcessor;
import org.apache.xml.security.stax.impl.processor.input.XMLSecurityInputProcessor;
import org.apache.xml.security.stax.securityEvent.SecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboundXMLSec {
    protected static final transient Logger LOG = LoggerFactory.getLogger(InboundXMLSec.class);
    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private final XMLSecurityProperties securityProperties;

    public InboundXMLSec(XMLSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public XMLStreamReader processInMessage(XMLStreamReader xmlStreamReader) throws XMLStreamException {
        return this.processInMessage(xmlStreamReader, null, null);
    }

    public XMLStreamReader processInMessage(XMLStreamReader xmlStreamReader, List<SecurityEvent> requestSecurityEvents, SecurityEventListener securityEventListener) throws XMLStreamException {
        if (requestSecurityEvents == null) {
            requestSecurityEvents = Collections.emptyList();
        }
        InboundSecurityContextImpl inboundSecurityContext = new InboundSecurityContextImpl();
        inboundSecurityContext.putList(SecurityEvent.class, requestSecurityEvents);
        inboundSecurityContext.addSecurityEventListener(securityEventListener);
        inboundSecurityContext.put("XMLInputFactory", xmlInputFactory);
        DocumentContextImpl documentContext = new DocumentContextImpl();
        documentContext.setEncoding(xmlStreamReader.getEncoding() != null ? xmlStreamReader.getEncoding() : StandardCharsets.UTF_8.name());
        Location location = xmlStreamReader.getLocation();
        if (location != null) {
            documentContext.setBaseURI(location.getSystemId());
        }
        InputProcessorChainImpl inputProcessorChain = new InputProcessorChainImpl((InboundSecurityContext)inboundSecurityContext, documentContext);
        inputProcessorChain.addProcessor(new XMLEventReaderInputProcessor(this.securityProperties, xmlStreamReader));
        List<InputProcessor> additionalInputProcessors = this.securityProperties.getInputProcessorList();
        if (!additionalInputProcessors.isEmpty()) {
            for (InputProcessor inputProcessor : additionalInputProcessors) {
                inputProcessorChain.addProcessor(inputProcessor);
            }
        }
        inputProcessorChain.addProcessor(new XMLSecurityInputProcessor(this.securityProperties));
        if (LOG.isTraceEnabled()) {
            LogInputProcessor logInputProcessor = new LogInputProcessor(this.securityProperties);
            logInputProcessor.addAfterProcessor(XMLSecurityInputProcessor.class.getName());
            inputProcessorChain.addProcessor(logInputProcessor);
        }
        return new XMLSecurityStreamReader(inputProcessorChain, this.securityProperties);
    }

    static {
        xmlInputFactory.setProperty("javax.xml.stream.supportDTD", false);
        xmlInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
        try {
            xmlInputFactory.setProperty("org.codehaus.stax2.internNames", true);
            xmlInputFactory.setProperty("org.codehaus.stax2.internNsUris", true);
            xmlInputFactory.setProperty("org.codehaus.stax2.preserveLocation", false);
        }
        catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage(), (Throwable)e);
        }
    }
}

