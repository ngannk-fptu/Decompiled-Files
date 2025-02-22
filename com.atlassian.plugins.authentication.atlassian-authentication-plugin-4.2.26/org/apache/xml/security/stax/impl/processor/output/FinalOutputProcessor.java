/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.processor.output;

import java.io.OutputStream;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.AbstractOutputProcessor;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.XMLSecurityEventWriter;

public class FinalOutputProcessor
extends AbstractOutputProcessor {
    private XMLEventWriter xmlEventWriter;

    public FinalOutputProcessor(OutputStream outputStream, String encoding) throws XMLSecurityException {
        this.setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
        try {
            this.xmlEventWriter = new XMLSecurityEventWriter(XMLSecurityConstants.xmlOutputFactory.createXMLStreamWriter(outputStream, encoding));
        }
        catch (XMLStreamException e) {
            throw new XMLSecurityException(e);
        }
    }

    public FinalOutputProcessor(XMLStreamWriter xmlStreamWriter) throws XMLSecurityException {
        this.setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
        this.xmlEventWriter = new XMLSecurityEventWriter(xmlStreamWriter);
    }

    @Override
    public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        this.xmlEventWriter.add(xmlSecEvent);
    }

    @Override
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        try {
            this.xmlEventWriter.flush();
            this.xmlEventWriter.close();
        }
        catch (XMLStreamException e) {
            throw new XMLSecurityException(e);
        }
    }
}

