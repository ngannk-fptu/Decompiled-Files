/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.stax.impl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.DocumentContext;
import org.apache.xml.security.stax.ext.OutboundSecurityContext;
import org.apache.xml.security.stax.ext.OutputProcessor;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.DocumentContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputProcessorChainImpl
implements OutputProcessorChain {
    protected static final transient Logger LOG = LoggerFactory.getLogger(OutputProcessorChainImpl.class);
    private List<OutputProcessor> outputProcessors;
    private int startPos;
    private int curPos;
    private XMLSecStartElement parentXmlSecStartElement;
    private final OutboundSecurityContext outboundSecurityContext;
    private final DocumentContextImpl documentContext;

    public OutputProcessorChainImpl(OutboundSecurityContext outboundSecurityContext) {
        this(outboundSecurityContext, 0);
    }

    public OutputProcessorChainImpl(OutboundSecurityContext outboundSecurityContext, int startPos) {
        this(outboundSecurityContext, new DocumentContextImpl(), startPos, new ArrayList<OutputProcessor>(20));
    }

    public OutputProcessorChainImpl(OutboundSecurityContext outboundSecurityContext, DocumentContextImpl documentContext) {
        this(outboundSecurityContext, documentContext, 0, new ArrayList<OutputProcessor>(20));
    }

    protected OutputProcessorChainImpl(OutboundSecurityContext outboundSecurityContext, DocumentContextImpl documentContextImpl, int startPos, List<OutputProcessor> outputProcessors) {
        this.outboundSecurityContext = outboundSecurityContext;
        this.curPos = this.startPos = startPos;
        this.documentContext = documentContextImpl;
        this.outputProcessors = outputProcessors;
    }

    @Override
    public void reset() {
        this.curPos = this.startPos;
    }

    @Override
    public OutboundSecurityContext getSecurityContext() {
        return this.outboundSecurityContext;
    }

    @Override
    public DocumentContext getDocumentContext() {
        return this.documentContext;
    }

    private static int compare(OutputProcessor o1, OutputProcessor o2) {
        int d = o1.getPhase().compareTo(o2.getPhase());
        if (d != 0) {
            return d;
        }
        if (o1.getActionOrder() >= 0 && o2.getActionOrder() >= 0 && (d = o1.getActionOrder() - o2.getActionOrder()) != 0) {
            return d;
        }
        if (o1.getBeforeProcessors().contains(o2.getClass()) || o2.getAfterProcessors().contains(o1.getClass())) {
            if (o1.getAfterProcessors().contains(o2.getClass()) || o2.getBeforeProcessors().contains(o1.getClass())) {
                throw new IllegalArgumentException(String.format("Conflicting order of processors %s and %s", o1, o2));
            }
            return -1;
        }
        if (o1.getAfterProcessors().contains(o2.getClass()) || o2.getBeforeProcessors().contains(o1.getClass())) {
            if (o2.getAfterProcessors().contains(o1.getClass())) {
                throw new IllegalArgumentException(String.format("Conflicting order of processors %s and %s", o1, o2));
            }
            return 1;
        }
        return 0;
    }

    @Override
    public void addProcessor(OutputProcessor newOutputProcessor) {
        OutputProcessor outputProcessor;
        int idxToInsert = this.outputProcessors.size();
        boolean pointOfNoReturn = false;
        int idx = this.outputProcessors.size();
        while (--idx >= 0) {
            outputProcessor = this.outputProcessors.get(idx);
            int d = OutputProcessorChainImpl.compare(newOutputProcessor, outputProcessor);
            if (d < 0) {
                if (pointOfNoReturn) {
                    throw new IllegalArgumentException(String.format("Conflicting order of processors %s and %s", newOutputProcessor, outputProcessor));
                }
                idxToInsert = idx;
                continue;
            }
            if (d <= 0) continue;
            pointOfNoReturn = true;
        }
        this.outputProcessors.add(idxToInsert, newOutputProcessor);
        if (idxToInsert < this.curPos) {
            ++this.curPos;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Added {} to output chain: ", (Object)newOutputProcessor.getClass().getName());
            for (int i = 0; i < this.outputProcessors.size(); ++i) {
                outputProcessor = this.outputProcessors.get(i);
                LOG.debug("Name: {} phase: {}", (Object)outputProcessor.getClass().getName(), (Object)outputProcessor.getPhase());
            }
        }
    }

    @Override
    public void removeProcessor(OutputProcessor outputProcessor) {
        LOG.debug("Removing processor {} from output chain", (Object)outputProcessor.getClass().getName());
        if (this.outputProcessors.indexOf(outputProcessor) <= this.curPos) {
            --this.curPos;
        }
        this.outputProcessors.remove(outputProcessor);
    }

    @Override
    public List<OutputProcessor> getProcessors() {
        return this.outputProcessors;
    }

    private void setParentXmlSecStartElement(XMLSecStartElement xmlSecStartElement) {
        this.parentXmlSecStartElement = xmlSecStartElement;
    }

    @Override
    public void processEvent(XMLSecEvent xmlSecEvent) throws XMLStreamException, XMLSecurityException {
        boolean reparent = false;
        if (this.curPos == this.startPos) {
            switch (xmlSecEvent.getEventType()) {
                case 1: {
                    if (xmlSecEvent == this.parentXmlSecStartElement) {
                        this.parentXmlSecStartElement = null;
                    }
                    xmlSecEvent.setParentXMLSecStartElement(this.parentXmlSecStartElement);
                    this.parentXmlSecStartElement = xmlSecEvent.asStartElement();
                    break;
                }
                case 2: {
                    xmlSecEvent.setParentXMLSecStartElement(this.parentXmlSecStartElement);
                    reparent = true;
                    break;
                }
                default: {
                    xmlSecEvent.setParentXMLSecStartElement(this.parentXmlSecStartElement);
                }
            }
        }
        this.outputProcessors.get(this.curPos++).processEvent(xmlSecEvent, this);
        if (reparent && this.parentXmlSecStartElement != null) {
            this.parentXmlSecStartElement = this.parentXmlSecStartElement.getParentXMLSecStartElement();
        }
    }

    @Override
    public void doFinal() throws XMLStreamException, XMLSecurityException {
        this.outputProcessors.get(this.curPos++).doFinal(this);
    }

    @Override
    public OutputProcessorChain createSubChain(OutputProcessor outputProcessor) throws XMLStreamException, XMLSecurityException {
        return this.createSubChain(outputProcessor, null);
    }

    @Override
    public OutputProcessorChain createSubChain(OutputProcessor outputProcessor, XMLSecStartElement parentXMLSecStartElement) throws XMLStreamException, XMLSecurityException {
        OutputProcessorChainImpl outputProcessorChain;
        try {
            outputProcessorChain = new OutputProcessorChainImpl(this.outboundSecurityContext, this.documentContext.clone(), this.outputProcessors.indexOf(outputProcessor) + 1, this.outputProcessors);
        }
        catch (CloneNotSupportedException e) {
            throw new XMLSecurityException(e);
        }
        if (parentXMLSecStartElement != null) {
            outputProcessorChain.setParentXmlSecStartElement(parentXMLSecStartElement);
        } else {
            outputProcessorChain.setParentXmlSecStartElement(this.parentXmlSecStartElement);
        }
        return outputProcessorChain;
    }
}

