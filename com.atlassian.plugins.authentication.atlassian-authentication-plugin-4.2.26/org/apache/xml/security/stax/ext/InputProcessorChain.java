/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.DocumentContext;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.InputProcessor;
import org.apache.xml.security.stax.ext.ProcessorChain;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

public interface InputProcessorChain
extends ProcessorChain {
    public void addProcessor(InputProcessor var1);

    public void removeProcessor(InputProcessor var1);

    public List<InputProcessor> getProcessors();

    public InboundSecurityContext getSecurityContext();

    public DocumentContext getDocumentContext();

    public InputProcessorChain createSubChain(InputProcessor var1) throws XMLStreamException, XMLSecurityException;

    public InputProcessorChain createSubChain(InputProcessor var1, boolean var2) throws XMLStreamException, XMLSecurityException;

    public XMLSecEvent processHeaderEvent() throws XMLStreamException, XMLSecurityException;

    public XMLSecEvent processEvent() throws XMLStreamException, XMLSecurityException;
}

