/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.stax.impl.processor.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.binding.xmlenc.EncryptedDataType;
import org.apache.xml.security.binding.xmlenc.EncryptedKeyType;
import org.apache.xml.security.binding.xmlenc.ReferenceList;
import org.apache.xml.security.binding.xmlenc.ReferenceType;
import org.apache.xml.security.binding.xop.Include;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.ConfigurationProperties;
import org.apache.xml.security.stax.config.JCEAlgorithmMapper;
import org.apache.xml.security.stax.ext.AbstractInputProcessor;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.UncheckedXMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.XMLSecurityEventReader;
import org.apache.xml.security.stax.impl.processor.input.XMLEncryptedKeyInputHandler;
import org.apache.xml.security.stax.impl.util.FullyBufferedOutputStream;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.impl.util.IVSplittingOutputStream;
import org.apache.xml.security.stax.impl.util.MultiInputStream;
import org.apache.xml.security.stax.impl.util.ReplaceableOuputStream;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenFactory;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;
import org.apache.xml.security.utils.UnsyncByteArrayInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDecryptInputProcessor
extends AbstractInputProcessor {
    private static final transient Logger LOG = LoggerFactory.getLogger(AbstractDecryptInputProcessor.class);
    protected static final Integer maximumAllowedXMLStructureDepth = Integer.valueOf(ConfigurationProperties.getProperty("MaximumAllowedXMLStructureDepth"));
    protected static final Integer maximumAllowedEncryptedDataEvents = Integer.valueOf(ConfigurationProperties.getProperty("MaximumAllowedEncryptedDataEvents"));
    private final KeyInfoType keyInfoType;
    private final Map<String, ReferenceType> references;
    private final List<ReferenceType> processedReferences;
    private final String uuid = IDGenerator.generateID(null);
    private final QName wrapperElementName = new QName("http://dummy", "dummy", this.uuid);
    private final ArrayDeque<XMLSecEvent> tmpXmlEventList = new ArrayDeque();

    public AbstractDecryptInputProcessor(XMLSecurityProperties securityProperties) throws XMLSecurityException {
        super(securityProperties);
        this.keyInfoType = null;
        this.references = null;
        this.processedReferences = null;
    }

    public AbstractDecryptInputProcessor(KeyInfoType keyInfoType, ReferenceList referenceList, XMLSecurityProperties securityProperties) throws XMLSecurityException {
        super(securityProperties);
        this.keyInfoType = keyInfoType;
        List<JAXBElement<ReferenceType>> dataReferenceOrKeyReference = referenceList.getDataReferenceOrKeyReference();
        int dataReferenceOrKeyReferenceSize = dataReferenceOrKeyReference.size();
        this.references = new HashMap<String, ReferenceType>((int)Math.ceil((double)dataReferenceOrKeyReferenceSize / 0.75));
        this.processedReferences = new ArrayList<ReferenceType>(dataReferenceOrKeyReferenceSize);
        Iterator<JAXBElement<ReferenceType>> referenceTypeIterator = dataReferenceOrKeyReference.iterator();
        while (referenceTypeIterator.hasNext()) {
            ReferenceType referenceType = (ReferenceType)referenceTypeIterator.next().getValue();
            if (referenceType.getURI() == null) {
                throw new XMLSecurityException("stax.emptyReferenceURI");
            }
            this.references.put(XMLSecurityUtils.dropReferenceMarker(referenceType.getURI()), referenceType);
        }
    }

    public Map<String, ReferenceType> getReferences() {
        return this.references;
    }

    public List<ReferenceType> getProcessedReferences() {
        return this.processedReferences;
    }

    @Override
    public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        return this.processEvent(inputProcessorChain, true);
    }

    @Override
    public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        return this.processEvent(inputProcessorChain, false);
    }

    private XMLSecEvent processEvent(InputProcessorChain inputProcessorChain, boolean isSecurityHeaderEvent) throws XMLStreamException, XMLSecurityException {
        if (!this.tmpXmlEventList.isEmpty()) {
            return this.tmpXmlEventList.pollLast();
        }
        XMLSecEvent xmlSecEvent = isSecurityHeaderEvent ? inputProcessorChain.processHeaderEvent() : inputProcessorChain.processEvent();
        boolean encryptedHeader = false;
        if (xmlSecEvent.getEventType() == 1) {
            XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();
            if (xmlSecStartElement.getName().equals(XMLSecurityConstants.TAG_wsse11_EncryptedHeader)) {
                xmlSecEvent = this.readAndBufferEncryptedHeader(inputProcessorChain, isSecurityHeaderEvent, xmlSecEvent);
                xmlSecStartElement = xmlSecEvent.asStartElement();
                encryptedHeader = true;
            }
            if (xmlSecStartElement.getName().equals(XMLSecurityConstants.TAG_xenc_EncryptedData)) {
                InputStream epilogInputStream;
                InputStream prologInputStream;
                ReferenceType referenceType = null;
                if (this.references != null) {
                    referenceType = this.matchesReferenceId(xmlSecStartElement);
                    if (referenceType == null) {
                        if (!this.tmpXmlEventList.isEmpty()) {
                            return this.tmpXmlEventList.pollLast();
                        }
                        return xmlSecEvent;
                    }
                    if (this.processedReferences.contains(referenceType)) {
                        throw new XMLSecurityException("signature.Verification.MultipleIDs");
                    }
                    this.processedReferences.add(referenceType);
                }
                this.tmpXmlEventList.clear();
                InputProcessorChain subInputProcessorChain = inputProcessorChain.createSubChain(this);
                EncryptedDataType encryptedDataType = this.parseEncryptedDataStructure(isSecurityHeaderEvent, xmlSecEvent, subInputProcessorChain);
                if (encryptedDataType.getId() == null) {
                    encryptedDataType.setId(IDGenerator.generateID(null));
                }
                InboundSecurityToken inboundSecurityToken = this.getSecurityToken(inputProcessorChain, xmlSecStartElement, encryptedDataType);
                this.handleSecurityToken(inboundSecurityToken, inputProcessorChain.getSecurityContext(), encryptedDataType);
                String algorithmURI = encryptedDataType.getEncryptionMethod().getAlgorithm();
                int ivLength = JCEAlgorithmMapper.getIVLengthFromURI(algorithmURI) / 8;
                Cipher symCipher = this.getCipher(algorithmURI);
                if (encryptedDataType.getCipherData().getCipherReference() != null) {
                    this.handleCipherReference(inputProcessorChain, encryptedDataType, symCipher, inboundSecurityToken);
                    subInputProcessorChain.reset();
                    return isSecurityHeaderEvent ? subInputProcessorChain.processHeaderEvent() : subInputProcessorChain.processEvent();
                }
                XMLSecStartElement parentXMLSecStartElement = xmlSecStartElement.getParentXMLSecStartElement();
                if (encryptedHeader) {
                    parentXMLSecStartElement = parentXMLSecStartElement.getParentXMLSecStartElement();
                }
                AbstractDecryptedEventReaderInputProcessor decryptedEventReaderInputProcessor = this.newDecryptedEventReaderInputProcessor(encryptedHeader, parentXMLSecStartElement, encryptedDataType, inboundSecurityToken, inputProcessorChain.getSecurityContext());
                inputProcessorChain.addProcessor(decryptedEventReaderInputProcessor);
                inputProcessorChain.getDocumentContext().setIsInEncryptedContent(inputProcessorChain.getProcessors().indexOf(decryptedEventReaderInputProcessor), decryptedEventReaderInputProcessor);
                if (SecurePart.Modifier.Content.getModifier().equals(encryptedDataType.getType())) {
                    this.handleEncryptedContent(inputProcessorChain, xmlSecStartElement.getParentXMLSecStartElement(), inboundSecurityToken, encryptedDataType);
                }
                XMLSecEvent nextEvent = null;
                subInputProcessorChain.reset();
                nextEvent = isSecurityHeaderEvent ? subInputProcessorChain.processHeaderEvent() : subInputProcessorChain.processEvent();
                InputStream decryptInputStream = null;
                if (nextEvent.isStartElement() && nextEvent.asStartElement().getName().equals(XMLSecurityConstants.TAG_XOP_INCLUDE)) {
                    try {
                        ArrayDeque<XMLSecEvent> xmlSecEvents = new ArrayDeque<XMLSecEvent>();
                        xmlSecEvents.push(nextEvent);
                        xmlSecEvents.push(XMLSecEventFactory.createXmlSecEndElement(XMLSecurityConstants.TAG_XOP_INCLUDE));
                        Unmarshaller unmarshaller = XMLSecurityConstants.getJaxbUnmarshaller(this.getSecurityProperties().isDisableSchemaValidation());
                        JAXBElement includeJAXBElement = (JAXBElement)unmarshaller.unmarshal((XMLEventReader)new XMLSecurityEventReader(xmlSecEvents, 0));
                        Include include = (Include)includeJAXBElement.getValue();
                        String href = include.getHref();
                        decryptInputStream = this.handleXOPInclude(inputProcessorChain, encryptedDataType, href, symCipher, inboundSecurityToken);
                    }
                    catch (JAXBException e) {
                        throw new XMLSecurityException((Exception)((Object)e));
                    }
                } else {
                    DecryptionThread decryptionThread = new DecryptionThread(subInputProcessorChain, isSecurityHeaderEvent, nextEvent);
                    Key decryptionKey = inboundSecurityToken.getSecretKey(algorithmURI, XMLSecurityConstants.Enc, encryptedDataType.getId());
                    decryptionKey = XMLSecurityUtils.prepareSecretKey(algorithmURI, decryptionKey.getEncoded());
                    decryptionThread.setSecretKey(decryptionKey);
                    decryptionThread.setSymmetricCipher(symCipher);
                    decryptionThread.setIvLength(ivLength);
                    Thread thread = new Thread(decryptionThread);
                    thread.setPriority(6);
                    thread.setName("decryption thread");
                    thread.setUncaughtExceptionHandler(decryptedEventReaderInputProcessor);
                    decryptedEventReaderInputProcessor.setDecryptionThread(thread);
                    LOG.debug("Starting decryption thread");
                    thread.start();
                    decryptInputStream = decryptionThread.getPipedInputStream();
                }
                try {
                    prologInputStream = this.writeWrapperStartElement(xmlSecStartElement);
                    epilogInputStream = this.writeWrapperEndElement();
                }
                catch (IOException e) {
                    throw new XMLSecurityException(e);
                }
                decryptInputStream = this.applyTransforms(referenceType, decryptInputStream);
                XMLStreamReader xmlStreamReader = ((XMLInputFactory)inputProcessorChain.getSecurityContext().get("XMLInputFactory")).createXMLStreamReader(new MultiInputStream(prologInputStream, decryptInputStream, epilogInputStream), StandardCharsets.UTF_8.name());
                this.forwardToWrapperElement(xmlStreamReader);
                decryptedEventReaderInputProcessor.setXmlStreamReader(xmlStreamReader);
                if (isSecurityHeaderEvent) {
                    return decryptedEventReaderInputProcessor.processHeaderEvent(inputProcessorChain);
                }
                return decryptedEventReaderInputProcessor.processEvent(inputProcessorChain);
            }
        }
        return xmlSecEvent;
    }

    protected InputStream applyTransforms(ReferenceType referenceType, InputStream inputStream) throws XMLSecurityException {
        return inputStream;
    }

    private InputStream writeWrapperStartElement(XMLSecStartElement xmlSecStartElement) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('<');
        stringBuilder.append(this.wrapperElementName.getPrefix());
        stringBuilder.append(':');
        stringBuilder.append(this.wrapperElementName.getLocalPart());
        stringBuilder.append(" xmlns:");
        stringBuilder.append(this.wrapperElementName.getPrefix());
        stringBuilder.append("=\"");
        stringBuilder.append(this.wrapperElementName.getNamespaceURI());
        stringBuilder.append('\"');
        ArrayList<XMLSecNamespace> comparableNamespacesToApply = new ArrayList<XMLSecNamespace>();
        ArrayList<XMLSecNamespace> comparableNamespaceList = new ArrayList<XMLSecNamespace>();
        xmlSecStartElement.getNamespacesFromCurrentScope(comparableNamespaceList);
        for (int i = comparableNamespaceList.size() - 1; i >= 0; --i) {
            XMLSecNamespace comparableNamespace = (XMLSecNamespace)comparableNamespaceList.get(i);
            if (comparableNamespacesToApply.contains(comparableNamespace)) continue;
            comparableNamespacesToApply.add(comparableNamespace);
            stringBuilder.append(' ');
            String prefix = comparableNamespace.getPrefix();
            String uri = comparableNamespace.getNamespaceURI();
            if (prefix == null || prefix.isEmpty()) {
                stringBuilder.append("xmlns=\"");
                stringBuilder.append(uri);
                stringBuilder.append('\"');
                continue;
            }
            stringBuilder.append("xmlns:");
            stringBuilder.append(prefix);
            stringBuilder.append("=\"");
            stringBuilder.append(uri);
            stringBuilder.append('\"');
        }
        stringBuilder.append('>');
        return new UnsyncByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private InputStream writeWrapperEndElement() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("</");
        stringBuilder.append(this.wrapperElementName.getPrefix());
        stringBuilder.append(':');
        stringBuilder.append(this.wrapperElementName.getLocalPart());
        stringBuilder.append('>');
        return new UnsyncByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void forwardToWrapperElement(XMLStreamReader xmlStreamReader) throws XMLStreamException {
        do {
            if (xmlStreamReader.getEventType() == 1 && xmlStreamReader.getName().equals(this.wrapperElementName)) {
                xmlStreamReader.next();
                break;
            }
            xmlStreamReader.next();
        } while (xmlStreamReader.hasNext());
    }

    private Cipher getCipher(String algorithmURI) throws XMLSecurityException {
        Cipher symCipher;
        try {
            String jceName = JCEAlgorithmMapper.translateURItoJCEID(algorithmURI);
            String jceProvider = JCEAlgorithmMapper.getJCEProviderFromURI(algorithmURI);
            if (jceName == null) {
                throw new XMLSecurityException("algorithms.NoSuchMap", new Object[]{algorithmURI});
            }
            symCipher = jceProvider != null ? Cipher.getInstance(jceName, jceProvider) : Cipher.getInstance(jceName);
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            throw new XMLSecurityException(e);
        }
        return symCipher;
    }

    private InboundSecurityToken getSecurityToken(InputProcessorChain inputProcessorChain, XMLSecStartElement xmlSecStartElement, EncryptedDataType encryptedDataType) throws XMLSecurityException {
        EncryptedKeyType encryptedKeyType;
        KeyInfoType keyInfoType = this.keyInfoType;
        if (keyInfoType == null) {
            keyInfoType = encryptedDataType.getKeyInfo();
        }
        if (keyInfoType != null && (encryptedKeyType = (EncryptedKeyType)XMLSecurityUtils.getQNameType(keyInfoType.getContent(), XMLSecurityConstants.TAG_xenc_EncryptedKey)) != null) {
            XMLEncryptedKeyInputHandler handler = new XMLEncryptedKeyInputHandler();
            handler.handle(inputProcessorChain, encryptedKeyType, xmlSecStartElement, this.getSecurityProperties());
            SecurityTokenProvider<? extends InboundSecurityToken> securityTokenProvider = inputProcessorChain.getSecurityContext().getSecurityTokenProvider(encryptedKeyType.getId());
            return securityTokenProvider.getSecurityToken();
        }
        return SecurityTokenFactory.getInstance().getSecurityToken(keyInfoType, SecurityTokenConstants.KeyUsage_Decryption, this.getSecurityProperties(), inputProcessorChain.getSecurityContext());
    }

    private EncryptedDataType parseEncryptedDataStructure(boolean isSecurityHeaderEvent, XMLSecEvent xmlSecEvent, InputProcessorChain subInputProcessorChain) throws XMLStreamException, XMLSecurityException {
        EncryptedDataType encryptedDataType;
        XMLSecEvent encryptedDataXMLSecEvent;
        ArrayDeque<XMLSecEvent> xmlSecEvents = new ArrayDeque<XMLSecEvent>();
        xmlSecEvents.push(xmlSecEvent);
        int count = 0;
        int keyInfoCount = 0;
        do {
            subInputProcessorChain.reset();
            encryptedDataXMLSecEvent = isSecurityHeaderEvent ? subInputProcessorChain.processHeaderEvent() : subInputProcessorChain.processEvent();
            xmlSecEvents.push(encryptedDataXMLSecEvent);
            if (++count >= maximumAllowedEncryptedDataEvents) {
                throw new XMLSecurityException("stax.xmlStructureSizeExceeded", new Object[]{maximumAllowedEncryptedDataEvents});
            }
            if (encryptedDataXMLSecEvent.getEventType() == 1 && encryptedDataXMLSecEvent.asStartElement().getName().equals(XMLSecurityConstants.TAG_dsig_KeyInfo)) {
                ++keyInfoCount;
                continue;
            }
            if (encryptedDataXMLSecEvent.getEventType() != 2 || !encryptedDataXMLSecEvent.asEndElement().getName().equals(XMLSecurityConstants.TAG_dsig_KeyInfo)) continue;
            --keyInfoCount;
        } while ((encryptedDataXMLSecEvent.getEventType() != 1 || !encryptedDataXMLSecEvent.asStartElement().getName().equals(XMLSecurityConstants.TAG_xenc_CipherValue)) && (encryptedDataXMLSecEvent.getEventType() != 2 || !encryptedDataXMLSecEvent.asEndElement().getName().equals(XMLSecurityConstants.TAG_xenc_EncryptedData)) || keyInfoCount != 0);
        xmlSecEvents.push(XMLSecEventFactory.createXmlSecEndElement(XMLSecurityConstants.TAG_xenc_CipherValue));
        xmlSecEvents.push(XMLSecEventFactory.createXmlSecEndElement(XMLSecurityConstants.TAG_xenc_CipherData));
        xmlSecEvents.push(XMLSecEventFactory.createXmlSecEndElement(XMLSecurityConstants.TAG_xenc_EncryptedData));
        try {
            Unmarshaller unmarshaller = XMLSecurityConstants.getJaxbUnmarshaller(this.getSecurityProperties().isDisableSchemaValidation());
            JAXBElement encryptedDataTypeJAXBElement = (JAXBElement)unmarshaller.unmarshal((XMLEventReader)new XMLSecurityEventReader(xmlSecEvents, 0));
            encryptedDataType = (EncryptedDataType)encryptedDataTypeJAXBElement.getValue();
        }
        catch (JAXBException e) {
            throw new XMLSecurityException((Exception)((Object)e));
        }
        return encryptedDataType;
    }

    private XMLSecEvent readAndBufferEncryptedHeader(InputProcessorChain inputProcessorChain, boolean isSecurityHeaderEvent, XMLSecEvent xmlSecEvent) throws XMLStreamException, XMLSecurityException {
        InputProcessorChain subInputProcessorChain = inputProcessorChain.createSubChain(this);
        do {
            this.tmpXmlEventList.push(xmlSecEvent);
            subInputProcessorChain.reset();
        } while ((xmlSecEvent = isSecurityHeaderEvent ? subInputProcessorChain.processHeaderEvent() : subInputProcessorChain.processEvent()).getEventType() != 1 || !xmlSecEvent.asStartElement().getName().equals(XMLSecurityConstants.TAG_xenc_EncryptedData));
        this.tmpXmlEventList.push(xmlSecEvent);
        return xmlSecEvent;
    }

    protected abstract AbstractDecryptedEventReaderInputProcessor newDecryptedEventReaderInputProcessor(boolean var1, XMLSecStartElement var2, EncryptedDataType var3, InboundSecurityToken var4, InboundSecurityContext var5) throws XMLSecurityException;

    protected abstract void handleSecurityToken(InboundSecurityToken var1, InboundSecurityContext var2, EncryptedDataType var3) throws XMLSecurityException;

    protected abstract void handleEncryptedContent(InputProcessorChain var1, XMLSecStartElement var2, InboundSecurityToken var3, EncryptedDataType var4) throws XMLSecurityException;

    protected abstract void handleCipherReference(InputProcessorChain var1, EncryptedDataType var2, Cipher var3, InboundSecurityToken var4) throws XMLSecurityException;

    protected abstract InputStream handleXOPInclude(InputProcessorChain var1, EncryptedDataType var2, String var3, Cipher var4, InboundSecurityToken var5) throws XMLSecurityException;

    protected ReferenceType matchesReferenceId(XMLSecStartElement xmlSecStartElement) {
        Attribute refId = this.getReferenceIDAttribute(xmlSecStartElement);
        if (refId != null) {
            return this.references.get(refId.getValue());
        }
        return null;
    }

    @Override
    public void doFinal(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        inputProcessorChain.doFinal();
        if (this.references != null) {
            for (Map.Entry<String, ReferenceType> referenceTypeEntry : this.references.entrySet()) {
                if (this.processedReferences.contains(referenceTypeEntry.getValue())) continue;
                throw new XMLSecurityException("stax.encryption.unprocessedReferences");
            }
        }
    }

    static class DecryptionThread
    implements Runnable {
        private final InputProcessorChain inputProcessorChain;
        private final boolean header;
        private final PipedOutputStream pipedOutputStream;
        private final PipedInputStream pipedInputStream;
        private Cipher symmetricCipher;
        private int ivLength;
        private Key secretKey;
        private final XMLSecEvent firstEvent;

        protected DecryptionThread(InputProcessorChain inputProcessorChain, boolean header, XMLSecEvent firstEvent) throws XMLStreamException, XMLSecurityException {
            this.inputProcessorChain = inputProcessorChain;
            this.header = header;
            this.firstEvent = firstEvent;
            this.pipedInputStream = new PipedInputStream(40960);
            try {
                this.pipedOutputStream = new PipedOutputStream(this.pipedInputStream);
            }
            catch (IOException e) {
                throw new XMLStreamException(e);
            }
        }

        public PipedInputStream getPipedInputStream() {
            return this.pipedInputStream;
        }

        private XMLSecEvent processNextEvent() throws XMLSecurityException, XMLStreamException {
            this.inputProcessorChain.reset();
            if (this.header) {
                return this.inputProcessorChain.processHeaderEvent();
            }
            return this.inputProcessorChain.processEvent();
        }

        @Override
        public void run() {
            try {
                final Cipher cipher = this.getSymmetricCipher();
                final OutputStream outputStream = cipher.getAlgorithm().toUpperCase().contains("GCM") ? new FullyBufferedOutputStream(this.pipedOutputStream) : this.pipedOutputStream;
                CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher){

                    @Override
                    public void close() throws IOException {
                        super.flush();
                        try {
                            byte[] bytes = cipher.doFinal();
                            outputStream.write(bytes);
                            outputStream.close();
                        }
                        catch (BadPaddingException | IllegalBlockSizeException e) {
                            throw new IOException(e);
                        }
                    }
                };
                IVSplittingOutputStream ivSplittingOutputStream = new IVSplittingOutputStream(cipherOutputStream, cipher, this.getSecretKey(), this.getIvLength());
                ReplaceableOuputStream replaceableOuputStream = new ReplaceableOuputStream(ivSplittingOutputStream);
                Base64OutputStream base64OutputStream = new Base64OutputStream(replaceableOuputStream, false);
                ivSplittingOutputStream.setParentOutputStream(replaceableOuputStream);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter((OutputStream)base64OutputStream, Charset.forName(this.inputProcessorChain.getDocumentContext().getEncoding()));
                XMLSecEvent xmlSecEvent = this.firstEvent;
                while (xmlSecEvent.getEventType() != 2) {
                    if (xmlSecEvent.getEventType() != 4) {
                        throw new XMLSecurityException("stax.unexpectedXMLEvent", new Object[]{XMLSecurityUtils.getXMLEventAsString(xmlSecEvent)});
                    }
                    char[] data = xmlSecEvent.asCharacters().getText();
                    outputStreamWriter.write(data);
                    xmlSecEvent = this.processNextEvent();
                }
                outputStreamWriter.close();
                if (this.secretKey instanceof Destroyable) {
                    try {
                        ((Destroyable)((Object)this.secretKey)).destroy();
                    }
                    catch (DestroyFailedException e) {
                        LOG.debug("Error destroying key: {}", (Object)e.getMessage());
                    }
                }
                LOG.debug("Decryption thread finished");
            }
            catch (Exception e) {
                try {
                    this.pipedOutputStream.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                throw new UncheckedXMLSecurityException(e);
            }
        }

        protected Cipher getSymmetricCipher() {
            return this.symmetricCipher;
        }

        protected void setSymmetricCipher(Cipher symmetricCipher) {
            this.symmetricCipher = symmetricCipher;
        }

        int getIvLength() {
            return this.ivLength;
        }

        void setIvLength(int ivLength) {
            this.ivLength = ivLength;
        }

        protected Key getSecretKey() {
            return this.secretKey;
        }

        protected void setSecretKey(Key secretKey) {
            this.secretKey = secretKey;
        }
    }

    public abstract class AbstractDecryptedEventReaderInputProcessor
    extends AbstractInputProcessor
    implements Thread.UncaughtExceptionHandler {
        private int currentXMLStructureDepth;
        private XMLStreamReader xmlStreamReader;
        private XMLSecStartElement parentXmlSecStartElement;
        private boolean encryptedHeader;
        private final InboundSecurityToken inboundSecurityToken;
        private boolean rootElementProcessed;
        private EncryptedDataType encryptedDataType;
        private Thread decryptionThread;
        private volatile Throwable thrownException;

        public AbstractDecryptedEventReaderInputProcessor(XMLSecurityProperties securityProperties, SecurePart.Modifier encryptionModifier, boolean encryptedHeader, XMLSecStartElement xmlSecStartElement, EncryptedDataType encryptedDataType, AbstractDecryptInputProcessor abstractDecryptInputProcessor, InboundSecurityToken inboundSecurityToken) {
            super(securityProperties);
            this.encryptedHeader = false;
            this.addAfterProcessor(abstractDecryptInputProcessor);
            this.rootElementProcessed = encryptionModifier == SecurePart.Modifier.Content;
            this.encryptedHeader = encryptedHeader;
            this.inboundSecurityToken = inboundSecurityToken;
            this.parentXmlSecStartElement = xmlSecStartElement;
            this.encryptedDataType = encryptedDataType;
            if (xmlSecStartElement != null) {
                this.currentXMLStructureDepth = xmlSecStartElement.getDocumentLevel();
            }
        }

        public void setDecryptionThread(Thread decryptionThread) {
            this.decryptionThread = decryptionThread;
        }

        public void setXmlStreamReader(XMLStreamReader xmlStreamReader) {
            this.xmlStreamReader = xmlStreamReader;
        }

        @Override
        public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
            return this.processEvent(inputProcessorChain, true);
        }

        @Override
        public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
            return this.processEvent(inputProcessorChain, false);
        }

        private XMLSecEvent processEvent(InputProcessorChain inputProcessorChain, boolean headerEvent) throws XMLStreamException, XMLSecurityException {
            this.testAndThrowUncaughtException();
            XMLSecEvent xmlSecEvent = XMLSecEventFactory.allocate(this.xmlStreamReader, this.parentXmlSecStartElement);
            if (1 == xmlSecEvent.getEventType()) {
                ++this.currentXMLStructureDepth;
                if (this.currentXMLStructureDepth > maximumAllowedXMLStructureDepth) {
                    throw new XMLSecurityException("secureProcessing.MaximumAllowedXMLStructureDepth", new Object[]{maximumAllowedXMLStructureDepth});
                }
                this.parentXmlSecStartElement = xmlSecEvent.asStartElement();
                if (!this.rootElementProcessed) {
                    this.handleEncryptedElement(inputProcessorChain, this.parentXmlSecStartElement, this.inboundSecurityToken, this.encryptedDataType);
                    this.rootElementProcessed = true;
                }
            } else if (2 == xmlSecEvent.getEventType()) {
                --this.currentXMLStructureDepth;
                if (this.parentXmlSecStartElement != null) {
                    this.parentXmlSecStartElement = this.parentXmlSecStartElement.getParentXMLSecStartElement();
                }
                if (xmlSecEvent.asEndElement().getName().equals(AbstractDecryptInputProcessor.this.wrapperElementName)) {
                    XMLSecEvent endEvent;
                    InputProcessorChain subInputProcessorChain = inputProcessorChain.createSubChain(this);
                    QName endElement = this.encryptedHeader ? XMLSecurityConstants.TAG_wsse11_EncryptedHeader : XMLSecurityConstants.TAG_xenc_EncryptedData;
                    do {
                        subInputProcessorChain.reset();
                    } while ((endEvent = headerEvent ? subInputProcessorChain.processHeaderEvent() : subInputProcessorChain.processEvent()).getEventType() != 2 || !endEvent.asEndElement().getName().equals(endElement));
                    inputProcessorChain.getDocumentContext().unsetIsInEncryptedContent(this);
                    xmlSecEvent = headerEvent ? inputProcessorChain.processHeaderEvent() : inputProcessorChain.processEvent();
                    if (this.decryptionThread != null) {
                        try {
                            this.decryptionThread.join();
                        }
                        catch (InterruptedException e) {
                            throw new XMLStreamException(e);
                        }
                        this.testAndThrowUncaughtException();
                    }
                    inputProcessorChain.removeProcessor(this);
                }
            }
            this.xmlStreamReader.next();
            return xmlSecEvent;
        }

        protected abstract void handleEncryptedElement(InputProcessorChain var1, XMLSecStartElement var2, InboundSecurityToken var3, EncryptedDataType var4) throws XMLSecurityException;

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            this.thrownException = e;
        }

        private void testAndThrowUncaughtException() throws XMLStreamException {
            if (this.thrownException != null) {
                if (this.thrownException instanceof UncheckedXMLSecurityException) {
                    UncheckedXMLSecurityException uxse = (UncheckedXMLSecurityException)this.thrownException;
                    throw new XMLStreamException(uxse.getCause());
                }
                throw new XMLStreamException(this.thrownException.getCause());
            }
        }
    }
}

