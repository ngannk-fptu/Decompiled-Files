/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.processor.output;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.encryption.XMLCipherUtil;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.JCEAlgorithmMapper;
import org.apache.xml.security.stax.ext.AbstractOutputProcessor;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecCharacters;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.EncryptionPartDef;
import org.apache.xml.security.stax.impl.XMLSecurityEventWriter;
import org.apache.xml.security.stax.impl.processor.output.AbstractEncryptEndingOutputProcessor;
import org.apache.xml.security.stax.impl.util.TrimmerOutputStream;
import org.apache.xml.security.utils.XMLUtils;

public abstract class AbstractEncryptOutputProcessor
extends AbstractOutputProcessor {
    private static final XMLSecStartElement wrapperStartElement = XMLSecEventFactory.createXmlSecStartElement(new QName("a"), null, null);
    private static final XMLSecEndElement wrapperEndElement = XMLSecEventFactory.createXmlSecEndElement(new QName("a"));
    private AbstractInternalEncryptionOutputProcessor activeInternalEncryptionOutputProcessor;

    @Override
    public abstract void processEvent(XMLSecEvent var1, OutputProcessorChain var2) throws XMLStreamException, XMLSecurityException;

    @Override
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        this.doFinalInternal(outputProcessorChain);
        super.doFinal(outputProcessorChain);
    }

    protected void doFinalInternal(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        this.verifyEncryptionParts(outputProcessorChain);
    }

    protected void verifyEncryptionParts(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        List encryptionPartDefs = outputProcessorChain.getSecurityContext().getAsList(EncryptionPartDef.class);
        Map dynamicSecureParts = outputProcessorChain.getSecurityContext().getAsMap("encryptionParts");
        block0: for (Map.Entry securePartEntry : dynamicSecureParts.entrySet()) {
            SecurePart securePart = (SecurePart)securePartEntry.getValue();
            if (!securePart.isRequired()) continue;
            for (int i = 0; encryptionPartDefs != null && i < encryptionPartDefs.size(); ++i) {
                EncryptionPartDef encryptionPartDef = (EncryptionPartDef)encryptionPartDefs.get(i);
                if (encryptionPartDef.getSecurePart() == securePart) continue block0;
            }
            throw new XMLSecurityException("stax.encryption.securePartNotFound", new Object[]{securePart.getName()});
        }
    }

    protected AbstractInternalEncryptionOutputProcessor getActiveInternalEncryptionOutputProcessor() {
        return this.activeInternalEncryptionOutputProcessor;
    }

    protected void setActiveInternalEncryptionOutputProcessor(AbstractInternalEncryptionOutputProcessor activeInternalEncryptionOutputProcessor) {
        this.activeInternalEncryptionOutputProcessor = activeInternalEncryptionOutputProcessor;
    }

    private char[] byteToCharArray(byte[] bytes, int off, int len) {
        char[] chars = new char[len - off];
        for (int i = off; i < len; ++i) {
            chars[i] = (char)bytes[i];
        }
        return chars;
    }

    public class CharacterEventGeneratorOutputStream
    extends OutputStream {
        private final Deque<XMLSecCharacters> charactersBuffer = new ArrayDeque<XMLSecCharacters>();

        public Deque<XMLSecCharacters> getCharactersBuffer() {
            return this.charactersBuffer;
        }

        @Override
        public void write(int b) throws IOException {
            this.charactersBuffer.offer(AbstractEncryptOutputProcessor.this.createCharacters(new char[]{(char)b}));
        }

        @Override
        public void write(byte[] b) throws IOException {
            this.charactersBuffer.offer(AbstractEncryptOutputProcessor.this.createCharacters(AbstractEncryptOutputProcessor.this.byteToCharArray(b, 0, b.length)));
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this.charactersBuffer.offer(AbstractEncryptOutputProcessor.this.createCharacters(AbstractEncryptOutputProcessor.this.byteToCharArray(b, off, len)));
        }
    }

    public abstract class AbstractInternalEncryptionOutputProcessor
    extends AbstractOutputProcessor {
        private EncryptionPartDef encryptionPartDef;
        private CharacterEventGeneratorOutputStream characterEventGeneratorOutputStream;
        private XMLEventWriter xmlEventWriter;
        private OutputStream cipherOutputStream;
        private String encoding;
        private XMLSecStartElement xmlSecStartElement;
        private int elementCounter;

        public AbstractInternalEncryptionOutputProcessor(EncryptionPartDef encryptionPartDef, XMLSecStartElement xmlSecStartElement, String encoding) throws XMLSecurityException {
            this.addBeforeProcessor(AbstractEncryptEndingOutputProcessor.class);
            this.addBeforeProcessor(AbstractInternalEncryptionOutputProcessor.class);
            this.addAfterProcessor(AbstractEncryptOutputProcessor.class);
            this.setEncryptionPartDef(encryptionPartDef);
            this.setXmlSecStartElement(xmlSecStartElement);
            this.setEncoding(encoding);
        }

        @Override
        public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
            String encryptionSymAlgorithm = this.securityProperties.getEncryptionSymAlgorithm();
            try {
                String jceAlgorithm = JCEAlgorithmMapper.translateURItoJCEID(encryptionSymAlgorithm);
                if (jceAlgorithm == null) {
                    throw new XMLSecurityException("algorithms.NoSuchMap", new Object[]{encryptionSymAlgorithm});
                }
                Cipher symmetricCipher = Cipher.getInstance(jceAlgorithm);
                int ivLen = JCEMapper.getIVLengthFromURI(encryptionSymAlgorithm) / 8;
                byte[] iv = XMLSecurityConstants.generateBytes(ivLen);
                AlgorithmParameterSpec parameterSpec = XMLCipherUtil.constructBlockCipherParameters(encryptionSymAlgorithm, iv);
                symmetricCipher.init(1, this.encryptionPartDef.getSymmetricKey(), parameterSpec);
                this.characterEventGeneratorOutputStream = new CharacterEventGeneratorOutputStream();
                Base64OutputStream base64EncoderStream = null;
                base64EncoderStream = XMLUtils.isIgnoreLineBreaks() ? new Base64OutputStream(this.characterEventGeneratorOutputStream, true, 0, null) : new Base64OutputStream(this.characterEventGeneratorOutputStream, true);
                base64EncoderStream.write(iv);
                OutputStream outputStream = new CipherOutputStream(base64EncoderStream, symmetricCipher);
                outputStream = this.applyTransforms(outputStream);
                this.cipherOutputStream = new TrimmerOutputStream(outputStream, 81920, 3, 4);
                this.xmlEventWriter = new XMLSecurityEventWriter(XMLSecurityConstants.xmlOutputFactoryNonRepairingNs.createXMLStreamWriter(this.cipherOutputStream, StandardCharsets.UTF_8.name()));
                this.xmlEventWriter.add(wrapperStartElement);
            }
            catch (NoSuchPaddingException e) {
                throw new XMLSecurityException(e);
            }
            catch (NoSuchAlgorithmException e) {
                throw new XMLSecurityException(e);
            }
            catch (IOException e) {
                throw new XMLSecurityException(e);
            }
            catch (XMLStreamException e) {
                throw new XMLSecurityException(e);
            }
            catch (InvalidKeyException e) {
                throw new XMLSecurityException(e);
            }
            catch (InvalidAlgorithmParameterException e) {
                throw new XMLSecurityException(e);
            }
            super.init(outputProcessorChain);
        }

        protected OutputStream applyTransforms(OutputStream outputStream) throws XMLSecurityException {
            return outputStream;
        }

        @Override
        public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
            switch (xmlSecEvent.getEventType()) {
                case 1: {
                    XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();
                    if (this.elementCounter == 0 && xmlSecStartElement.getName().equals(this.getXmlSecStartElement().getName())) {
                        if (SecurePart.Modifier.Element == this.getEncryptionPartDef().getModifier()) {
                            OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);
                            this.processEventInternal(xmlSecStartElement, subOutputProcessorChain);
                            this.encryptEvent(xmlSecEvent);
                        } else if (SecurePart.Modifier.Content == this.getEncryptionPartDef().getModifier()) {
                            OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);
                            outputProcessorChain.processEvent(xmlSecEvent);
                            subOutputProcessorChain = outputProcessorChain.createSubChain(this);
                            this.processEventInternal(xmlSecStartElement, subOutputProcessorChain);
                        }
                    } else {
                        this.encryptEvent(xmlSecEvent);
                    }
                    ++this.elementCounter;
                    break;
                }
                case 2: {
                    --this.elementCounter;
                    if (this.elementCounter == 0 && xmlSecEvent.asEndElement().getName().equals(this.getXmlSecStartElement().getName())) {
                        OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);
                        if (SecurePart.Modifier.Element == this.getEncryptionPartDef().getModifier()) {
                            this.encryptEvent(xmlSecEvent);
                            this.doFinalInternal(subOutputProcessorChain);
                        } else if (SecurePart.Modifier.Content == this.getEncryptionPartDef().getModifier()) {
                            this.doFinalInternal(subOutputProcessorChain);
                            this.outputAsEvent(subOutputProcessorChain, xmlSecEvent);
                        }
                        subOutputProcessorChain.removeProcessor(this);
                        AbstractEncryptOutputProcessor.this.setActiveInternalEncryptionOutputProcessor(null);
                        break;
                    }
                    this.encryptEvent(xmlSecEvent);
                    break;
                }
                default: {
                    this.encryptEvent(xmlSecEvent);
                    Deque<XMLSecCharacters> charactersBuffer = this.characterEventGeneratorOutputStream.getCharactersBuffer();
                    if (charactersBuffer.size() <= 5) break;
                    OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);
                    Iterator<XMLSecCharacters> charactersIterator = charactersBuffer.iterator();
                    while (charactersIterator.hasNext()) {
                        XMLSecCharacters characters = charactersIterator.next();
                        this.outputAsEvent(subOutputProcessorChain, characters);
                        charactersIterator.remove();
                    }
                    break block0;
                }
            }
        }

        private void encryptEvent(XMLSecEvent xmlSecEvent) throws XMLStreamException {
            this.xmlEventWriter.add(xmlSecEvent);
        }

        protected void processEventInternal(XMLSecStartElement xmlSecStartElement, OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
            ArrayList<XMLSecAttribute> attributes = new ArrayList<XMLSecAttribute>(2);
            attributes.add(this.createAttribute(XMLSecurityConstants.ATT_NULL_Id, this.getEncryptionPartDef().getEncRefId()));
            attributes.add(this.createAttribute(XMLSecurityConstants.ATT_NULL_Type, this.getEncryptionPartDef().getModifier().getModifier()));
            this.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptedData, true, attributes);
            attributes = new ArrayList(1);
            attributes.add(this.createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, this.securityProperties.getEncryptionSymAlgorithm()));
            this.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptionMethod, false, attributes);
            this.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptionMethod);
            this.createKeyInfoStructure(outputProcessorChain);
            this.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherData, false, null);
            this.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherValue, false, null);
        }

        protected abstract void createKeyInfoStructure(OutputProcessorChain var1) throws XMLStreamException, XMLSecurityException;

        protected void doFinalInternal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
            try {
                this.xmlEventWriter.add(wrapperEndElement);
                this.xmlEventWriter.close();
                this.cipherOutputStream.close();
            }
            catch (IOException e) {
                throw new XMLStreamException(e);
            }
            Deque<XMLSecCharacters> charactersBuffer = this.characterEventGeneratorOutputStream.getCharactersBuffer();
            if (!charactersBuffer.isEmpty()) {
                Iterator<XMLSecCharacters> charactersIterator = charactersBuffer.iterator();
                while (charactersIterator.hasNext()) {
                    XMLSecCharacters characters = charactersIterator.next();
                    this.outputAsEvent(outputProcessorChain, characters);
                    charactersIterator.remove();
                }
            }
            this.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherValue);
            this.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherData);
            this.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptedData);
        }

        protected EncryptionPartDef getEncryptionPartDef() {
            return this.encryptionPartDef;
        }

        protected void setEncryptionPartDef(EncryptionPartDef encryptionPartDef) {
            this.encryptionPartDef = encryptionPartDef;
        }

        protected XMLSecStartElement getXmlSecStartElement() {
            return this.xmlSecStartElement;
        }

        protected void setXmlSecStartElement(XMLSecStartElement xmlSecStartElement) {
            this.xmlSecStartElement = xmlSecStartElement;
        }

        public String getEncoding() {
            return this.encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }
    }
}

