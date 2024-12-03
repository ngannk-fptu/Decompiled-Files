/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.stax.impl.processor.input;

import java.io.StringWriter;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.AbstractInputProcessor;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogInputProcessor
extends AbstractInputProcessor {
    private static final transient Logger LOG = LoggerFactory.getLogger(LogInputProcessor.class);

    public LogInputProcessor(XMLSecurityProperties securityProperties) {
        super(securityProperties);
        this.setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
    }

    @Override
    public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        return inputProcessorChain.processHeaderEvent();
    }

    @Override
    public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        XMLSecEvent xmlSecEvent = inputProcessorChain.processEvent();
        StringWriter stringWriter = new StringWriter();
        xmlSecEvent.writeAsEncodedUnicode(stringWriter);
        LOG.trace(stringWriter.toString());
        return xmlSecEvent;
    }
}

