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
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.InputProcessor;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.DocumentContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputProcessorChainImpl
implements InputProcessorChain {
    protected static final transient Logger LOG = LoggerFactory.getLogger(InputProcessorChainImpl.class);
    private List<InputProcessor> inputProcessors;
    private int startPos;
    private int curPos;
    private final InboundSecurityContext inboundSecurityContext;
    private final DocumentContextImpl documentContext;

    public InputProcessorChainImpl(InboundSecurityContext inboundSecurityContext) {
        this(inboundSecurityContext, 0);
    }

    public InputProcessorChainImpl(InboundSecurityContext inboundSecurityContext, int startPos) {
        this(inboundSecurityContext, new DocumentContextImpl(), startPos, new ArrayList<InputProcessor>(20));
    }

    public InputProcessorChainImpl(InboundSecurityContext inboundSecurityContext, DocumentContextImpl documentContext) {
        this(inboundSecurityContext, documentContext, 0, new ArrayList<InputProcessor>(20));
    }

    protected InputProcessorChainImpl(InboundSecurityContext inboundSecurityContext, DocumentContextImpl documentContextImpl, int startPos, List<InputProcessor> inputProcessors) {
        this.inboundSecurityContext = inboundSecurityContext;
        this.curPos = this.startPos = startPos;
        this.documentContext = documentContextImpl;
        this.inputProcessors = inputProcessors;
    }

    @Override
    public void reset() {
        this.curPos = this.startPos;
    }

    @Override
    public InboundSecurityContext getSecurityContext() {
        return this.inboundSecurityContext;
    }

    @Override
    public DocumentContext getDocumentContext() {
        return this.documentContext;
    }

    @Override
    public synchronized void addProcessor(InputProcessor newInputProcessor) {
        int idxToInsert;
        InputProcessor inputProcessor;
        int i;
        int startPhaseIdx = 0;
        int endPhaseIdx = this.inputProcessors.size();
        XMLSecurityConstants.Phase targetPhase = newInputProcessor.getPhase();
        for (i = this.inputProcessors.size() - 1; i >= 0; --i) {
            inputProcessor = this.inputProcessors.get(i);
            if (inputProcessor.getPhase().ordinal() <= targetPhase.ordinal()) continue;
            startPhaseIdx = i + 1;
            break;
        }
        for (i = startPhaseIdx; i < this.inputProcessors.size(); ++i) {
            inputProcessor = this.inputProcessors.get(i);
            if (inputProcessor.getPhase().ordinal() >= targetPhase.ordinal()) continue;
            endPhaseIdx = i;
            break;
        }
        if (newInputProcessor.getBeforeProcessors().isEmpty() && newInputProcessor.getAfterProcessors().isEmpty()) {
            this.inputProcessors.add(startPhaseIdx, newInputProcessor);
        } else if (newInputProcessor.getBeforeProcessors().isEmpty()) {
            idxToInsert = startPhaseIdx;
            for (int i2 = endPhaseIdx - 1; i2 >= startPhaseIdx; --i2) {
                InputProcessor inputProcessor2 = this.inputProcessors.get(i2);
                if (!newInputProcessor.getAfterProcessors().contains(inputProcessor2) && !newInputProcessor.getAfterProcessors().contains(inputProcessor2.getClass().getName())) continue;
                idxToInsert = i2;
                break;
            }
            this.inputProcessors.add(idxToInsert, newInputProcessor);
        } else if (newInputProcessor.getAfterProcessors().isEmpty()) {
            idxToInsert = endPhaseIdx;
            for (int i3 = startPhaseIdx; i3 < endPhaseIdx; ++i3) {
                InputProcessor inputProcessor3 = this.inputProcessors.get(i3);
                if (!newInputProcessor.getBeforeProcessors().contains(inputProcessor3) && !newInputProcessor.getBeforeProcessors().contains(inputProcessor3.getClass().getName())) continue;
                idxToInsert = i3 + 1;
                break;
            }
            this.inputProcessors.add(idxToInsert, newInputProcessor);
        } else {
            InputProcessor inputProcessor4;
            int i4;
            boolean found = false;
            int idxToInsert2 = startPhaseIdx;
            for (i4 = endPhaseIdx - 1; i4 >= startPhaseIdx; --i4) {
                inputProcessor4 = this.inputProcessors.get(i4);
                if (!newInputProcessor.getAfterProcessors().contains(inputProcessor4) && !newInputProcessor.getAfterProcessors().contains(inputProcessor4.getClass().getName())) continue;
                idxToInsert2 = i4;
                found = true;
                break;
            }
            if (found) {
                this.inputProcessors.add(idxToInsert2, newInputProcessor);
            } else {
                for (i4 = startPhaseIdx; i4 < endPhaseIdx; ++i4) {
                    inputProcessor4 = this.inputProcessors.get(i4);
                    if (!newInputProcessor.getBeforeProcessors().contains(inputProcessor4) && !newInputProcessor.getBeforeProcessors().contains(inputProcessor4.getClass().getName())) continue;
                    idxToInsert2 = i4 + 1;
                    break;
                }
                this.inputProcessors.add(idxToInsert2, newInputProcessor);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Added {} to input chain: ", (Object)newInputProcessor.getClass().getName());
            for (i = 0; i < this.inputProcessors.size(); ++i) {
                InputProcessor inputProcessor5 = this.inputProcessors.get(i);
                LOG.debug("Name: {} phase: {}", (Object)inputProcessor5.getClass().getName(), (Object)inputProcessor5.getPhase());
            }
        }
    }

    @Override
    public synchronized void removeProcessor(InputProcessor inputProcessor) {
        LOG.debug("Removing processor {} from input chain", (Object)inputProcessor.getClass().getName());
        if (this.inputProcessors.indexOf(inputProcessor) <= this.curPos) {
            --this.curPos;
        }
        this.inputProcessors.remove(inputProcessor);
    }

    @Override
    public List<InputProcessor> getProcessors() {
        return this.inputProcessors;
    }

    @Override
    public XMLSecEvent processHeaderEvent() throws XMLStreamException, XMLSecurityException {
        return this.inputProcessors.get(this.curPos++).processHeaderEvent(this);
    }

    @Override
    public XMLSecEvent processEvent() throws XMLStreamException, XMLSecurityException {
        return this.inputProcessors.get(this.curPos++).processEvent(this);
    }

    @Override
    public void doFinal() throws XMLStreamException, XMLSecurityException {
        this.inputProcessors.get(this.curPos++).doFinal(this);
    }

    @Override
    public InputProcessorChain createSubChain(InputProcessor inputProcessor) throws XMLStreamException, XMLSecurityException {
        return this.createSubChain(inputProcessor, true);
    }

    @Override
    public InputProcessorChain createSubChain(InputProcessor inputProcessor, boolean clone) throws XMLStreamException, XMLSecurityException {
        InputProcessorChainImpl inputProcessorChain;
        try {
            DocumentContextImpl docContext = clone ? this.documentContext.clone() : this.documentContext;
            inputProcessorChain = new InputProcessorChainImpl(this.inboundSecurityContext, docContext, this.inputProcessors.indexOf(inputProcessor) + 1, new ArrayList<InputProcessor>(this.inputProcessors));
        }
        catch (CloneNotSupportedException e) {
            throw new XMLSecurityException(e);
        }
        return inputProcessorChain;
    }
}

