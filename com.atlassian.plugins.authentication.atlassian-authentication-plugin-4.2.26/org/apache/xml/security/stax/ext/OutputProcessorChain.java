/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.DocumentContext;
import org.apache.xml.security.stax.ext.OutboundSecurityContext;
import org.apache.xml.security.stax.ext.OutputProcessor;
import org.apache.xml.security.stax.ext.ProcessorChain;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

public interface OutputProcessorChain
extends ProcessorChain {
    public void addProcessor(OutputProcessor var1);

    public void removeProcessor(OutputProcessor var1);

    public List<OutputProcessor> getProcessors();

    public OutboundSecurityContext getSecurityContext();

    public DocumentContext getDocumentContext();

    public OutputProcessorChain createSubChain(OutputProcessor var1) throws XMLStreamException, XMLSecurityException;

    public OutputProcessorChain createSubChain(OutputProcessor var1, XMLSecStartElement var2) throws XMLStreamException, XMLSecurityException;

    public void processEvent(XMLSecEvent var1) throws XMLStreamException, XMLSecurityException;
}

