/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.stax.impl.processor.input;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.binding.excc14n.InclusiveNamespaces;
import org.apache.xml.security.binding.xmldsig.ReferenceType;
import org.apache.xml.security.binding.xmldsig.SignatureType;
import org.apache.xml.security.binding.xmldsig.TransformType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.ConfigurationProperties;
import org.apache.xml.security.stax.config.JCEAlgorithmMapper;
import org.apache.xml.security.stax.config.ResourceResolverMapper;
import org.apache.xml.security.stax.ext.AbstractInputProcessor;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.Transformer;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.transformer.canonicalizer.Canonicalizer20010315_OmitCommentsTransformer;
import org.apache.xml.security.stax.impl.util.DigestOutputStream;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.impl.util.KeyValue;
import org.apache.xml.security.stax.securityEvent.AlgorithmSuiteSecurityEvent;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSignatureReferenceVerifyInputProcessor
extends AbstractInputProcessor {
    private static final transient Logger LOG = LoggerFactory.getLogger(AbstractSignatureReferenceVerifyInputProcessor.class);
    protected static final Integer maximumAllowedReferencesPerManifest = Integer.valueOf(ConfigurationProperties.getProperty("MaximumAllowedReferencesPerManifest"));
    protected static final Integer maximumAllowedTransformsPerReference = Integer.valueOf(ConfigurationProperties.getProperty("MaximumAllowedTransformsPerReference"));
    protected static final Boolean doNotThrowExceptionForManifests = Boolean.valueOf(ConfigurationProperties.getProperty("DoNotThrowExceptionForManifests"));
    protected static final Boolean allowNotSameDocumentReferences = Boolean.valueOf(ConfigurationProperties.getProperty("AllowNotSameDocumentReferences"));
    private final SignatureType signatureType;
    private final InboundSecurityToken inboundSecurityToken;
    private final List<KeyValue<ResourceResolver, ReferenceType>> sameDocumentReferences;
    private final List<KeyValue<ResourceResolver, ReferenceType>> externalReferences;
    private final List<ReferenceType> processedReferences;

    public AbstractSignatureReferenceVerifyInputProcessor(InputProcessorChain inputProcessorChain, SignatureType signatureType, InboundSecurityToken inboundSecurityToken, XMLSecurityProperties securityProperties) throws XMLSecurityException {
        super(securityProperties);
        this.signatureType = signatureType;
        this.inboundSecurityToken = inboundSecurityToken;
        List<ReferenceType> referencesTypeList = signatureType.getSignedInfo().getReference();
        if (referencesTypeList.size() > maximumAllowedReferencesPerManifest) {
            throw new XMLSecurityException("secureProcessing.MaximumAllowedReferencesPerManifest", new Object[]{referencesTypeList.size(), maximumAllowedReferencesPerManifest});
        }
        this.sameDocumentReferences = new ArrayList<KeyValue<ResourceResolver, ReferenceType>>(referencesTypeList.size());
        this.externalReferences = new ArrayList<KeyValue<ResourceResolver, ReferenceType>>(referencesTypeList.size());
        this.processedReferences = new ArrayList<ReferenceType>(referencesTypeList.size());
        for (ReferenceType referenceType : referencesTypeList) {
            ResourceResolver resourceResolver;
            if (!doNotThrowExceptionForManifests.booleanValue() && "http://www.w3.org/2000/09/xmldsig#Manifest".equals(referenceType.getType())) {
                throw new XMLSecurityException("secureProcessing.DoNotThrowExceptionForManifests");
            }
            if (referenceType.getURI() == null) {
                throw new XMLSecurityException("stax.emptyReferenceURI");
            }
            if (referenceType.getId() == null) {
                referenceType.setId(IDGenerator.generateID(null));
            }
            if ((resourceResolver = ResourceResolverMapper.getResourceResolver(referenceType.getURI(), inputProcessorChain.getDocumentContext().getBaseURI())).isSameDocumentReference()) {
                this.sameDocumentReferences.add(new KeyValue<ResourceResolver, ReferenceType>(resourceResolver, referenceType));
                continue;
            }
            if (!allowNotSameDocumentReferences.booleanValue()) {
                throw new XMLSecurityException("secureProcessing.AllowNotSameDocumentReferences");
            }
            this.externalReferences.add(new KeyValue<ResourceResolver, ReferenceType>(resourceResolver, referenceType));
        }
    }

    public SignatureType getSignatureType() {
        return this.signatureType;
    }

    public List<ReferenceType> getProcessedReferences() {
        return this.processedReferences;
    }

    public InboundSecurityToken getInboundSecurityToken() {
        return this.inboundSecurityToken;
    }

    @Override
    public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        return inputProcessorChain.processHeaderEvent();
    }

    @Override
    public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        XMLSecStartElement xmlSecStartElement;
        List<ReferenceType> referenceTypes;
        XMLSecEvent xmlSecEvent = inputProcessorChain.processEvent();
        if (1 == xmlSecEvent.getEventType() && !(referenceTypes = this.resolvesResource(xmlSecStartElement = xmlSecEvent.asStartElement())).isEmpty()) {
            for (int i = 0; i < referenceTypes.size(); ++i) {
                ReferenceType referenceType = referenceTypes.get(i);
                if (this.processedReferences.contains(referenceType)) {
                    throw new XMLSecurityException("signature.Verification.MultipleIDs", new Object[]{referenceType.getURI()});
                }
                InternalSignatureReferenceVerifier internalSignatureReferenceVerifier = this.getSignatureReferenceVerifier(this.getSecurityProperties(), inputProcessorChain, referenceType, xmlSecStartElement);
                if (!internalSignatureReferenceVerifier.isFinished()) {
                    internalSignatureReferenceVerifier.processEvent(xmlSecEvent, inputProcessorChain);
                    inputProcessorChain.addProcessor(internalSignatureReferenceVerifier);
                }
                this.processedReferences.add(referenceType);
                inputProcessorChain.getDocumentContext().setIsInSignedContent(inputProcessorChain.getProcessors().indexOf(internalSignatureReferenceVerifier), internalSignatureReferenceVerifier);
                this.processElementPath(internalSignatureReferenceVerifier.getStartElementPath(), inputProcessorChain, internalSignatureReferenceVerifier.getStartElement(), referenceType);
            }
        }
        return xmlSecEvent;
    }

    protected abstract void processElementPath(List<QName> var1, InputProcessorChain var2, XMLSecEvent var3, ReferenceType var4) throws XMLSecurityException;

    protected List<ReferenceType> resolvesResource(XMLSecStartElement xmlSecStartElement) {
        List<ReferenceType> referenceTypes = Collections.emptyList();
        for (int i = 0; i < this.sameDocumentReferences.size(); ++i) {
            KeyValue<ResourceResolver, ReferenceType> keyValue = this.sameDocumentReferences.get(i);
            ResourceResolver resolver = keyValue.getKey();
            boolean resourceMatches = false;
            try {
                Method m = resolver.getClass().getMethod("matches", XMLSecStartElement.class, QName.class);
                if (m != null && ((Boolean)m.invoke((Object)resolver, xmlSecStartElement, this.getSecurityProperties().getIdAttributeNS())).booleanValue()) {
                    if (referenceTypes == Collections.emptyList()) {
                        referenceTypes = new ArrayList<ReferenceType>();
                    }
                    referenceTypes.add(keyValue.getValue());
                    resourceMatches = true;
                }
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
            catch (InvocationTargetException invocationTargetException) {
            }
            catch (IllegalAccessException illegalAccessException) {
                // empty catch block
            }
            if (resourceMatches || !keyValue.getKey().matches(xmlSecStartElement)) continue;
            if (referenceTypes == Collections.emptyList()) {
                referenceTypes = new ArrayList<ReferenceType>();
            }
            referenceTypes.add(keyValue.getValue());
        }
        return referenceTypes;
    }

    @Override
    public void doFinal(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        KeyValue<ResourceResolver, ReferenceType> keyValue;
        int i;
        inputProcessorChain.doFinal();
        for (i = 0; i < this.sameDocumentReferences.size(); ++i) {
            keyValue = this.sameDocumentReferences.get(i);
            if (this.processedReferences.contains(keyValue.getValue())) continue;
            throw new XMLSecurityException("stax.signature.unprocessedReferences");
        }
        if (!this.externalReferences.isEmpty()) {
            for (i = 0; i < this.externalReferences.size(); ++i) {
                keyValue = this.externalReferences.get(i);
                this.verifyExternalReference(inputProcessorChain, keyValue.getKey().getInputStreamFromExternalReference(), keyValue.getValue());
                this.processedReferences.add(keyValue.getValue());
            }
            for (i = 0; i < this.externalReferences.size(); ++i) {
                keyValue = this.externalReferences.get(i);
                if (this.processedReferences.contains(keyValue.getValue())) continue;
                throw new XMLSecurityException("stax.signature.unprocessedReferences");
            }
        }
    }

    protected InternalSignatureReferenceVerifier getSignatureReferenceVerifier(XMLSecurityProperties securityProperties, InputProcessorChain inputProcessorChain, ReferenceType referenceType, XMLSecStartElement startElement) throws XMLSecurityException {
        return new InternalSignatureReferenceVerifier(securityProperties, inputProcessorChain, referenceType, startElement);
    }

    protected void verifyExternalReference(InputProcessorChain inputProcessorChain, InputStream inputStream, ReferenceType referenceType) throws XMLSecurityException, XMLStreamException {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
             DigestOutputStream digestOutputStream = this.createMessageDigestOutputStream(referenceType, inputProcessorChain.getSecurityContext());
             UnsyncBufferedOutputStream bufferedDigestOutputStream = new UnsyncBufferedOutputStream(digestOutputStream);){
            if (referenceType.getTransforms() != null) {
                Transformer transformer = this.buildTransformerChain(referenceType, bufferedDigestOutputStream, inputProcessorChain, null);
                transformer.transform(bufferedInputStream);
                bufferedDigestOutputStream.close();
            } else {
                XMLSecurityUtils.copy(bufferedInputStream, bufferedDigestOutputStream);
                bufferedDigestOutputStream.close();
            }
            this.compareDigest(digestOutputStream.getDigestValue(), referenceType);
        }
        catch (IOException e) {
            throw new XMLSecurityException(e);
        }
    }

    protected DigestOutputStream createMessageDigestOutputStream(ReferenceType referenceType, InboundSecurityContext inboundSecurityContext) throws XMLSecurityException {
        MessageDigest messageDigest;
        String digestMethodAlgorithm = referenceType.getDigestMethod().getAlgorithm();
        String jceName = JCEAlgorithmMapper.translateURItoJCEID(digestMethodAlgorithm);
        String jceProvider = JCEAlgorithmMapper.getJCEProviderFromURI(digestMethodAlgorithm);
        if (jceName == null) {
            throw new XMLSecurityException("algorithms.NoSuchMap", new Object[]{digestMethodAlgorithm});
        }
        AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
        algorithmSuiteSecurityEvent.setAlgorithmURI(digestMethodAlgorithm);
        algorithmSuiteSecurityEvent.setAlgorithmUsage(XMLSecurityConstants.SigDig);
        algorithmSuiteSecurityEvent.setCorrelationID(referenceType.getId());
        inboundSecurityContext.registerSecurityEvent(algorithmSuiteSecurityEvent);
        try {
            messageDigest = jceProvider != null ? MessageDigest.getInstance(jceName, jceProvider) : MessageDigest.getInstance(jceName);
        }
        catch (NoSuchAlgorithmException e) {
            throw new XMLSecurityException(e);
        }
        catch (NoSuchProviderException e) {
            throw new XMLSecurityException(e);
        }
        return new DigestOutputStream(messageDigest);
    }

    protected Transformer buildTransformerChain(ReferenceType referenceType, OutputStream outputStream, InputProcessorChain inputProcessorChain, InternalSignatureReferenceVerifier internalSignatureReferenceVerifier) throws XMLSecurityException {
        if (referenceType.getTransforms() == null || referenceType.getTransforms().getTransform().isEmpty()) {
            AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
            algorithmSuiteSecurityEvent.setAlgorithmURI("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
            algorithmSuiteSecurityEvent.setAlgorithmUsage(XMLSecurityConstants.SigTransform);
            algorithmSuiteSecurityEvent.setCorrelationID(referenceType.getId());
            inputProcessorChain.getSecurityContext().registerSecurityEvent(algorithmSuiteSecurityEvent);
            Canonicalizer20010315_OmitCommentsTransformer transformer = new Canonicalizer20010315_OmitCommentsTransformer();
            transformer.setOutputStream(outputStream);
            return transformer;
        }
        List<TransformType> transformTypeList = referenceType.getTransforms().getTransform();
        if (transformTypeList.size() == 1 && "http://www.w3.org/2000/09/xmldsig#enveloped-signature".equals(transformTypeList.get(0).getAlgorithm())) {
            TransformType transformType = new TransformType();
            transformType.setAlgorithm("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
            transformTypeList.add(transformType);
        }
        if (transformTypeList.size() > maximumAllowedTransformsPerReference) {
            throw new XMLSecurityException("secureProcessing.MaximumAllowedTransformsPerReference", new Object[]{transformTypeList.size(), maximumAllowedTransformsPerReference});
        }
        Transformer parentTransformer = null;
        for (int i = transformTypeList.size() - 1; i >= 0; --i) {
            TransformType transformType = transformTypeList.get(i);
            String algorithm = transformType.getAlgorithm();
            AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
            algorithmSuiteSecurityEvent.setAlgorithmURI(algorithm);
            algorithmSuiteSecurityEvent.setAlgorithmUsage(XMLSecurityConstants.SigTransform);
            algorithmSuiteSecurityEvent.setCorrelationID(referenceType.getId());
            inputProcessorChain.getSecurityContext().registerSecurityEvent(algorithmSuiteSecurityEvent);
            InclusiveNamespaces inclusiveNamespacesType = (InclusiveNamespaces)XMLSecurityUtils.getQNameType(transformType.getContent(), XMLSecurityConstants.TAG_c14nExcl_InclusiveNamespaces);
            HashMap<String, Object> transformerProperties = null;
            if (inclusiveNamespacesType != null) {
                transformerProperties = new HashMap<String, Object>();
                transformerProperties.put("inclusiveNamespacePrefixList", inclusiveNamespacesType.getPrefixList());
            }
            parentTransformer = parentTransformer != null ? XMLSecurityUtils.getTransformer(parentTransformer, null, transformerProperties, algorithm, XMLSecurityConstants.DIRECTION.IN) : XMLSecurityUtils.getTransformer(null, outputStream, transformerProperties, algorithm, XMLSecurityConstants.DIRECTION.IN);
        }
        return parentTransformer;
    }

    protected void compareDigest(byte[] calculatedDigest, ReferenceType referenceType) throws XMLSecurityException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Calculated Digest: {}", (Object)XMLUtils.encodeToString(calculatedDigest));
            LOG.debug("Stored Digest: {}", (Object)XMLUtils.encodeToString(referenceType.getDigestValue()));
        }
        if (!MessageDigest.isEqual(referenceType.getDigestValue(), calculatedDigest)) {
            throw new XMLSecurityException("signature.Verification.InvalidDigestOrReference", new Object[]{referenceType.getURI()});
        }
    }

    public class InternalSignatureReferenceVerifier
    extends AbstractInputProcessor {
        private ReferenceType referenceType;
        private Transformer transformer;
        private DigestOutputStream digestOutputStream;
        private OutputStream bufferedDigestOutputStream;
        private List<QName> startElementPath;
        private XMLSecStartElement startElement;
        private int elementCounter;
        private boolean finished;

        public InternalSignatureReferenceVerifier(XMLSecurityProperties securityProperties, InputProcessorChain inputProcessorChain, ReferenceType referenceType, XMLSecStartElement startElement) throws XMLSecurityException {
            super(securityProperties);
            this.finished = false;
            this.setStartElement(startElement);
            this.setReferenceType(referenceType);
            this.digestOutputStream = AbstractSignatureReferenceVerifyInputProcessor.this.createMessageDigestOutputStream(referenceType, inputProcessorChain.getSecurityContext());
            this.bufferedDigestOutputStream = new UnsyncBufferedOutputStream(this.getDigestOutputStream());
            this.transformer = this.buildTransformerChain(referenceType, this.bufferedDigestOutputStream, inputProcessorChain);
        }

        public Transformer buildTransformerChain(ReferenceType referenceType, OutputStream outputStream, InputProcessorChain inputProcessorChain) throws XMLSecurityException {
            return AbstractSignatureReferenceVerifyInputProcessor.this.buildTransformerChain(referenceType, outputStream, inputProcessorChain, this);
        }

        @Override
        public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
            return inputProcessorChain.processHeaderEvent();
        }

        @Override
        public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
            XMLSecEvent xmlSecEvent = inputProcessorChain.processEvent();
            this.processEvent(xmlSecEvent, inputProcessorChain);
            return xmlSecEvent;
        }

        public void processEvent(XMLSecEvent xmlSecEvent, InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
            this.getTransformer().transform(xmlSecEvent);
            if (1 == xmlSecEvent.getEventType()) {
                ++this.elementCounter;
            } else if (2 == xmlSecEvent.getEventType()) {
                XMLSecEndElement xmlSecEndElement = xmlSecEvent.asEndElement();
                --this.elementCounter;
                if (this.elementCounter == 0 && xmlSecEndElement.getName().equals(this.startElement.getName())) {
                    this.getTransformer().doFinal();
                    try {
                        this.getBufferedDigestOutputStream().close();
                    }
                    catch (IOException e) {
                        throw new XMLSecurityException(e);
                    }
                    AbstractSignatureReferenceVerifyInputProcessor.this.compareDigest(this.getDigestOutputStream().getDigestValue(), this.getReferenceType());
                    inputProcessorChain.removeProcessor(this);
                    inputProcessorChain.getDocumentContext().unsetIsInSignedContent(this);
                    this.setFinished(true);
                }
            }
        }

        public boolean isFinished() {
            return this.finished;
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }

        public ReferenceType getReferenceType() {
            return this.referenceType;
        }

        public void setReferenceType(ReferenceType referenceType) {
            this.referenceType = referenceType;
        }

        public Transformer getTransformer() {
            return this.transformer;
        }

        public void setTransformer(Transformer transformer) {
            this.transformer = transformer;
        }

        public DigestOutputStream getDigestOutputStream() {
            return this.digestOutputStream;
        }

        public void setDigestOutputStream(DigestOutputStream digestOutputStream) {
            this.digestOutputStream = digestOutputStream;
        }

        public OutputStream getBufferedDigestOutputStream() {
            return this.bufferedDigestOutputStream;
        }

        public void setBufferedDigestOutputStream(OutputStream bufferedDigestOutputStream) {
            this.bufferedDigestOutputStream = bufferedDigestOutputStream;
        }

        public XMLSecStartElement getStartElement() {
            return this.startElement;
        }

        public void setStartElement(XMLSecStartElement startElement) {
            this.startElementPath = startElement.getElementPath();
            this.startElement = startElement;
        }

        public List<QName> getStartElementPath() {
            return this.startElementPath;
        }
    }
}

