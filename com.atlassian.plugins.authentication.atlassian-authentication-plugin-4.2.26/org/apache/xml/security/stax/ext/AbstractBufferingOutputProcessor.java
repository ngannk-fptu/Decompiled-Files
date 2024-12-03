/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.AbstractOutputProcessor;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

public abstract class AbstractBufferingOutputProcessor
extends AbstractOutputProcessor {
    private final ArrayDeque<XMLSecEvent> xmlSecEventBuffer = new ArrayDeque(100);

    protected AbstractBufferingOutputProcessor() throws XMLSecurityException {
    }

    protected Deque<XMLSecEvent> getXmlSecEventBuffer() {
        return this.xmlSecEventBuffer;
    }

    @Override
    public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        this.xmlSecEventBuffer.offer(xmlSecEvent);
    }

    @Override
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);
        this.flushBufferAndCallbackAfterHeader(subOutputProcessorChain, this.getXmlSecEventBuffer());
        subOutputProcessorChain.doFinal();
        outputProcessorChain.removeProcessor(this);
    }

    protected abstract void processHeaderEvent(OutputProcessorChain var1) throws XMLStreamException, XMLSecurityException;

    protected void flushBufferAndCallbackAfterHeader(OutputProcessorChain outputProcessorChain, Deque<XMLSecEvent> xmlSecEventDeque) throws XMLStreamException, XMLSecurityException {
        this.processHeaderEvent(outputProcessorChain);
        while (!xmlSecEventDeque.isEmpty()) {
            XMLSecEvent xmlSecEvent = xmlSecEventDeque.pop();
            outputProcessorChain.reset();
            outputProcessorChain.processEvent(xmlSecEvent);
        }
        outputProcessorChain.reset();
    }
}

