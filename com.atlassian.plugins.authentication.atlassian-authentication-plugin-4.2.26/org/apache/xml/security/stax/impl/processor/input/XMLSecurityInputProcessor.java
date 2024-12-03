/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.processor.input;

import java.util.ArrayDeque;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.AbstractInputProcessor;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.processor.input.XMLDecryptInputProcessor;
import org.apache.xml.security.stax.impl.processor.input.XMLEventReaderInputProcessor;
import org.apache.xml.security.stax.impl.processor.input.XMLSignatureInputHandler;
import org.apache.xml.security.stax.impl.processor.input.XMLSignatureReferenceVerifyInputProcessor;

public class XMLSecurityInputProcessor
extends AbstractInputProcessor {
    private int startIndexForProcessor;
    private InternalBufferProcessor internalBufferProcessor;
    private boolean signatureElementFound = false;
    private boolean encryptedDataElementFound = false;
    private boolean decryptOnly = false;

    public XMLSecurityInputProcessor(XMLSecurityProperties securityProperties) {
        super(securityProperties);
        this.setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
        this.decryptOnly = securityProperties.getActions().size() == 1 && securityProperties.getActions().contains(XMLSecurityConstants.ENCRYPTION);
    }

    @Override
    public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        return null;
    }

    @Override
    public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        XMLSecEvent xmlSecEvent;
        if (!this.decryptOnly && this.internalBufferProcessor == null) {
            this.internalBufferProcessor = new InternalBufferProcessor(this.getSecurityProperties());
            inputProcessorChain.addProcessor(this.internalBufferProcessor);
        }
        if (1 == (xmlSecEvent = inputProcessorChain.processEvent()).getEventType()) {
            final XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();
            if (!this.decryptOnly && xmlSecStartElement.getName().equals(XMLSecurityConstants.TAG_dsig_Signature)) {
                if (this.signatureElementFound) {
                    throw new XMLSecurityException("stax.multipleSignaturesNotSupported");
                }
                this.signatureElementFound = true;
                this.startIndexForProcessor = this.internalBufferProcessor.getXmlSecEventList().size() - 1;
            } else if (xmlSecStartElement.getName().equals(XMLSecurityConstants.TAG_xenc_EncryptedData)) {
                this.encryptedDataElementFound = true;
                XMLDecryptInputProcessor decryptInputProcessor = new XMLDecryptInputProcessor(this.getSecurityProperties());
                decryptInputProcessor.setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
                decryptInputProcessor.addAfterProcessor(XMLEventReaderInputProcessor.class.getName());
                decryptInputProcessor.addBeforeProcessor(XMLSecurityInputProcessor.class.getName());
                decryptInputProcessor.addBeforeProcessor(InternalBufferProcessor.class.getName());
                inputProcessorChain.addProcessor(decryptInputProcessor);
                if (!this.decryptOnly) {
                    ArrayDeque<XMLSecEvent> xmlSecEventList = this.internalBufferProcessor.getXmlSecEventList();
                    xmlSecEventList.pollFirst();
                }
                AbstractInputProcessor abstractInputProcessor = new AbstractInputProcessor(this.getSecurityProperties()){

                    @Override
                    public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
                        return this.processEvent(inputProcessorChain);
                    }

                    @Override
                    public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
                        inputProcessorChain.removeProcessor(this);
                        return xmlSecStartElement;
                    }
                };
                abstractInputProcessor.setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
                abstractInputProcessor.addBeforeProcessor(decryptInputProcessor);
                inputProcessorChain.addProcessor(abstractInputProcessor);
                inputProcessorChain.reset();
                xmlSecEvent = inputProcessorChain.processEvent();
                if (!this.decryptOnly && xmlSecEvent.isStartElement() && xmlSecEvent.asStartElement().getName().equals(XMLSecurityConstants.TAG_dsig_Signature) && !this.signatureElementFound) {
                    throw new XMLSecurityException("Internal error");
                }
            }
        } else if (2 == xmlSecEvent.getEventType()) {
            XMLSecEndElement xmlSecEndElement = xmlSecEvent.asEndElement();
            if (this.signatureElementFound && xmlSecEndElement.getName().equals(XMLSecurityConstants.TAG_dsig_Signature)) {
                XMLSignatureInputHandler inputHandler = new XMLSignatureInputHandler();
                ArrayDeque<XMLSecEvent> xmlSecEventList = this.internalBufferProcessor.getXmlSecEventList();
                inputHandler.handle(inputProcessorChain, this.getSecurityProperties(), xmlSecEventList, this.startIndexForProcessor);
                inputProcessorChain.removeProcessor(this.internalBufferProcessor);
                InternalReplayProcessor internalReplayProcessor = new InternalReplayProcessor(this.getSecurityProperties(), xmlSecEventList);
                internalReplayProcessor.addBeforeProcessor(XMLSignatureReferenceVerifyInputProcessor.class.getName());
                inputProcessorChain.addProcessor(internalReplayProcessor);
                InputProcessorChain subInputProcessorChain = inputProcessorChain.createSubChain(this, false);
                while (!xmlSecEventList.isEmpty()) {
                    subInputProcessorChain.reset();
                    subInputProcessorChain.processEvent();
                }
                inputProcessorChain.getProcessors().clear();
                inputProcessorChain.getProcessors().addAll(subInputProcessorChain.getProcessors());
            }
        }
        return xmlSecEvent;
    }

    @Override
    public void doFinal(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        if (!this.signatureElementFound && !this.encryptedDataElementFound) {
            throw new XMLSecurityException("stax.unsecuredMessage");
        }
        super.doFinal(inputProcessorChain);
    }

    public static class InternalReplayProcessor
    extends AbstractInputProcessor {
        private final ArrayDeque<XMLSecEvent> xmlSecEventList;

        public InternalReplayProcessor(XMLSecurityProperties securityProperties, ArrayDeque<XMLSecEvent> xmlSecEventList) {
            super(securityProperties);
            this.xmlSecEventList = xmlSecEventList;
        }

        @Override
        public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
            return null;
        }

        @Override
        public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
            if (!this.xmlSecEventList.isEmpty()) {
                return this.xmlSecEventList.pollLast();
            }
            inputProcessorChain.removeProcessor(this);
            return inputProcessorChain.processEvent();
        }
    }

    public class InternalBufferProcessor
    extends AbstractInputProcessor {
        private final ArrayDeque<XMLSecEvent> xmlSecEventList;

        InternalBufferProcessor(XMLSecurityProperties securityProperties) {
            super(securityProperties);
            this.xmlSecEventList = new ArrayDeque();
            this.setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
            this.addBeforeProcessor(XMLSecurityInputProcessor.class.getName());
        }

        public ArrayDeque<XMLSecEvent> getXmlSecEventList() {
            return this.xmlSecEventList;
        }

        @Override
        public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
            return null;
        }

        @Override
        public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
            XMLSecEvent xmlSecEvent = inputProcessorChain.processEvent();
            this.xmlSecEventList.push(xmlSecEvent);
            return xmlSecEvent;
        }
    }
}

