/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.stax.impl.processor.output;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.JCEAlgorithmMapper;
import org.apache.xml.security.stax.config.ResourceResolverMapper;
import org.apache.xml.security.stax.ext.AbstractOutputProcessor;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.Transformer;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.SignaturePartDef;
import org.apache.xml.security.stax.impl.transformer.TransformIdentity;
import org.apache.xml.security.stax.impl.util.DigestOutputStream;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSignatureOutputProcessor
extends AbstractOutputProcessor {
    private static final transient Logger LOG = LoggerFactory.getLogger(AbstractSignatureOutputProcessor.class);
    private final List<SignaturePartDef> signaturePartDefList = new ArrayList<SignaturePartDef>();
    private InternalSignatureOutputProcessor activeInternalSignatureOutputProcessor;

    public List<SignaturePartDef> getSignaturePartDefList() {
        return this.signaturePartDefList;
    }

    @Override
    public abstract void processEvent(XMLSecEvent var1, OutputProcessorChain var2) throws XMLStreamException, XMLSecurityException;

    @Override
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        this.doFinalInternal(outputProcessorChain);
        super.doFinal(outputProcessorChain);
    }

    protected void doFinalInternal(OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
        Map dynamicSecureParts = outputProcessorChain.getSecurityContext().getAsMap("signatureParts");
        if (dynamicSecureParts != null) {
            for (Map.Entry securePartEntry : dynamicSecureParts.entrySet()) {
                SecurePart securePart = (SecurePart)securePartEntry.getValue();
                if (securePart.getExternalReference() == null) continue;
                this.digestExternalReference(outputProcessorChain, securePart);
            }
        }
        this.verifySignatureParts(outputProcessorChain);
    }

    protected void digestExternalReference(OutputProcessorChain outputProcessorChain, SecurePart securePart) throws XMLSecurityException, XMLStreamException {
        String externalReference = securePart.getExternalReference();
        ResourceResolver resourceResolver = ResourceResolverMapper.getResourceResolver(externalReference, outputProcessorChain.getDocumentContext().getBaseURI());
        String digestAlgo = securePart.getDigestMethod();
        if (digestAlgo == null) {
            digestAlgo = this.getSecurityProperties().getSignatureDigestAlgorithm();
        }
        DigestOutputStream digestOutputStream = this.createMessageDigestOutputStream(digestAlgo);
        InputStream inputStream = resourceResolver.getInputStreamFromExternalReference();
        SignaturePartDef signaturePartDef = new SignaturePartDef();
        signaturePartDef.setSecurePart(securePart);
        signaturePartDef.setSigRefId(externalReference);
        signaturePartDef.setExternalResource(true);
        signaturePartDef.setTransforms(securePart.getTransforms());
        signaturePartDef.setDigestAlgo(digestAlgo);
        try {
            if (securePart.getTransforms() != null) {
                signaturePartDef.setExcludeVisibleC14Nprefixes(true);
                Transformer transformer = this.buildTransformerChain(digestOutputStream, signaturePartDef, null);
                transformer.transform(inputStream);
                transformer.doFinal();
            } else {
                XMLSecurityUtils.copy(inputStream, digestOutputStream);
            }
            digestOutputStream.close();
        }
        catch (IOException e) {
            throw new XMLSecurityException(e);
        }
        String calculatedDigest = XMLUtils.encodeToString(digestOutputStream.getDigestValue());
        LOG.debug("Calculated Digest: {}", (Object)calculatedDigest);
        signaturePartDef.setDigestValue(calculatedDigest);
        this.getSignaturePartDefList().add(signaturePartDef);
    }

    protected void verifySignatureParts(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        List<SignaturePartDef> signaturePartDefs = this.getSignaturePartDefList();
        Map dynamicSecureParts = outputProcessorChain.getSecurityContext().getAsMap("signatureParts");
        if (dynamicSecureParts != null) {
            block0: for (Map.Entry securePartEntry : dynamicSecureParts.entrySet()) {
                SecurePart securePart = (SecurePart)securePartEntry.getValue();
                if (!securePart.isRequired()) continue;
                for (int i = 0; i < signaturePartDefs.size(); ++i) {
                    SignaturePartDef signaturePartDef = signaturePartDefs.get(i);
                    if (signaturePartDef.getSecurePart() == securePart) continue block0;
                }
                throw new XMLSecurityException("stax.signature.securePartNotFound", new Object[]{securePart.getName()});
            }
        }
    }

    protected InternalSignatureOutputProcessor getActiveInternalSignatureOutputProcessor() {
        return this.activeInternalSignatureOutputProcessor;
    }

    protected void setActiveInternalSignatureOutputProcessor(InternalSignatureOutputProcessor activeInternalSignatureOutputProcessor) {
        this.activeInternalSignatureOutputProcessor = activeInternalSignatureOutputProcessor;
    }

    protected DigestOutputStream createMessageDigestOutputStream(String digestAlgorithm) throws XMLSecurityException {
        MessageDigest messageDigest;
        String jceName = JCEAlgorithmMapper.translateURItoJCEID(digestAlgorithm);
        String jceProvider = JCEAlgorithmMapper.getJCEProviderFromURI(digestAlgorithm);
        if (jceName == null) {
            throw new XMLSecurityException("algorithms.NoSuchMap", new Object[]{digestAlgorithm});
        }
        try {
            messageDigest = jceProvider != null ? MessageDigest.getInstance(jceName, jceProvider) : MessageDigest.getInstance(jceName);
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new XMLSecurityException(e);
        }
        return new DigestOutputStream(messageDigest);
    }

    protected Transformer buildTransformerChain(OutputStream outputStream, SignaturePartDef signaturePartDef, XMLSecStartElement xmlSecStartElement) throws XMLSecurityException {
        String[] transforms = signaturePartDef.getTransforms();
        if (transforms == null || transforms.length == 0) {
            TransformIdentity transformer = new TransformIdentity();
            transformer.setOutputStream(outputStream);
            return transformer;
        }
        Transformer parentTransformer = null;
        for (int i = transforms.length - 1; i >= 0; --i) {
            String transform = transforms[i];
            HashMap<String, ArrayList<String>> transformerProperties = null;
            if (this.getSecurityProperties().isAddExcC14NInclusivePrefixes() && "http://www.w3.org/2001/10/xml-exc-c14n#".equals(transform)) {
                Set<String> prefixSet = XMLSecurityUtils.getExcC14NInclusiveNamespacePrefixes(xmlSecStartElement, signaturePartDef.isExcludeVisibleC14Nprefixes());
                StringBuilder prefixes = new StringBuilder();
                for (String prefix : prefixSet) {
                    if (prefixes.length() != 0) {
                        prefixes.append(' ');
                    }
                    prefixes.append(prefix);
                }
                signaturePartDef.setInclusiveNamespacesPrefixes(prefixes.toString());
                ArrayList<String> inclusiveNamespacePrefixes = new ArrayList<String>(prefixSet);
                transformerProperties = new HashMap<String, ArrayList<String>>();
                transformerProperties.put("inclusiveNamespacePrefixList", inclusiveNamespacePrefixes);
            }
            parentTransformer = parentTransformer != null ? XMLSecurityUtils.getTransformer(parentTransformer, null, transformerProperties, transform, XMLSecurityConstants.DIRECTION.OUT) : XMLSecurityUtils.getTransformer(null, outputStream, transformerProperties, transform, XMLSecurityConstants.DIRECTION.OUT);
        }
        return parentTransformer;
    }

    public class InternalSignatureOutputProcessor
    extends AbstractOutputProcessor {
        private SignaturePartDef signaturePartDef;
        private XMLSecStartElement xmlSecStartElement;
        private int elementCounter;
        private OutputStream bufferedDigestOutputStream;
        private DigestOutputStream digestOutputStream;
        private Transformer transformer;

        public InternalSignatureOutputProcessor(SignaturePartDef signaturePartDef, XMLSecStartElement xmlSecStartElement) throws XMLSecurityException {
            this.addBeforeProcessor(InternalSignatureOutputProcessor.class);
            this.signaturePartDef = signaturePartDef;
            this.xmlSecStartElement = xmlSecStartElement;
        }

        @Override
        public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
            this.digestOutputStream = AbstractSignatureOutputProcessor.this.createMessageDigestOutputStream(this.signaturePartDef.getDigestAlgo());
            this.bufferedDigestOutputStream = new UnsyncBufferedOutputStream(this.digestOutputStream);
            this.transformer = AbstractSignatureOutputProcessor.this.buildTransformerChain(this.bufferedDigestOutputStream, this.signaturePartDef, this.xmlSecStartElement);
            super.init(outputProcessorChain);
        }

        @Override
        public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
            this.transformer.transform(xmlSecEvent);
            if (1 == xmlSecEvent.getEventType()) {
                ++this.elementCounter;
            } else if (2 == xmlSecEvent.getEventType()) {
                --this.elementCounter;
                if (this.elementCounter == 0 && xmlSecEvent.asEndElement().getName().equals(this.xmlSecStartElement.getName())) {
                    this.transformer.doFinal();
                    try {
                        this.bufferedDigestOutputStream.close();
                    }
                    catch (IOException e) {
                        throw new XMLSecurityException(e);
                    }
                    String calculatedDigest = XMLUtils.encodeToString(this.digestOutputStream.getDigestValue());
                    LOG.debug("Calculated Digest: {}", (Object)calculatedDigest);
                    this.signaturePartDef.setDigestValue(calculatedDigest);
                    outputProcessorChain.removeProcessor(this);
                    AbstractSignatureOutputProcessor.this.setActiveInternalSignatureOutputProcessor(null);
                }
            }
            outputProcessorChain.processEvent(xmlSecEvent);
        }
    }
}

