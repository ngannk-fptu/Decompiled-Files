/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.io.OutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.crypto.KeyGenerator;
import javax.xml.stream.XMLStreamWriter;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.JCEAlgorithmMapper;
import org.apache.xml.security.stax.ext.OutboundSecurityContext;
import org.apache.xml.security.stax.ext.OutputProcessor;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.impl.DocumentContextImpl;
import org.apache.xml.security.stax.impl.OutboundSecurityContextImpl;
import org.apache.xml.security.stax.impl.OutputProcessorChainImpl;
import org.apache.xml.security.stax.impl.XMLSecurityStreamWriter;
import org.apache.xml.security.stax.impl.processor.output.FinalOutputProcessor;
import org.apache.xml.security.stax.impl.processor.output.XMLEncryptOutputProcessor;
import org.apache.xml.security.stax.impl.processor.output.XMLSignatureOutputProcessor;
import org.apache.xml.security.stax.impl.securityToken.GenericOutboundSecurityToken;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.securityEvent.SecurityEventListener;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;

public class OutboundXMLSec {
    private final XMLSecurityProperties securityProperties;

    public OutboundXMLSec(XMLSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public XMLStreamWriter processOutMessage(OutputStream outputStream, String encoding) throws XMLSecurityException {
        return this.processOutMessage((Object)outputStream, encoding, null);
    }

    public XMLStreamWriter processOutMessage(XMLStreamWriter xmlStreamWriter, String encoding) throws XMLSecurityException {
        return this.processOutMessage((Object)xmlStreamWriter, encoding, null);
    }

    public XMLStreamWriter processOutMessage(OutputStream outputStream, String encoding, SecurityEventListener eventListener) throws XMLSecurityException {
        return this.processOutMessage((Object)outputStream, encoding, eventListener);
    }

    public XMLStreamWriter processOutMessage(XMLStreamWriter xmlStreamWriter, String encoding, SecurityEventListener eventListener) throws XMLSecurityException {
        return this.processOutMessage((Object)xmlStreamWriter, encoding, eventListener);
    }

    private XMLStreamWriter processOutMessage(Object output, String encoding, SecurityEventListener eventListener) throws XMLSecurityException {
        FinalOutputProcessor finalOutputProcessor;
        OutboundSecurityContextImpl outboundSecurityContext = new OutboundSecurityContextImpl();
        if (eventListener != null) {
            outboundSecurityContext.addSecurityEventListener(eventListener);
        }
        DocumentContextImpl documentContext = new DocumentContextImpl();
        documentContext.setEncoding(encoding);
        OutputProcessorChainImpl outputProcessorChain = new OutputProcessorChainImpl((OutboundSecurityContext)outboundSecurityContext, documentContext);
        SecurePart signEntireRequestPart = null;
        SecurePart encryptEntireRequestPart = null;
        int actionOrder = 0;
        for (XMLSecurityConstants.Action action : this.securityProperties.getActions()) {
            if (XMLSecurityConstants.SIGNATURE.equals(action)) {
                XMLSignatureOutputProcessor signatureOutputProcessor = new XMLSignatureOutputProcessor();
                this.initializeOutputProcessor(outputProcessorChain, signatureOutputProcessor, action, actionOrder++);
                this.configureSignatureKeys(outboundSecurityContext);
                List<SecurePart> signatureParts = this.securityProperties.getSignatureSecureParts();
                for (SecurePart securePart : signatureParts) {
                    if (securePart.getIdToSecure() == null && securePart.getName() != null) {
                        outputProcessorChain.getSecurityContext().putAsMap("signatureParts", securePart.getName(), securePart);
                        continue;
                    }
                    if (securePart.getIdToSecure() != null) {
                        outputProcessorChain.getSecurityContext().putAsMap("signatureParts", securePart.getIdToSecure(), securePart);
                        continue;
                    }
                    if (securePart.getExternalReference() != null) {
                        outputProcessorChain.getSecurityContext().putAsMap("signatureParts", securePart.getExternalReference(), securePart);
                        continue;
                    }
                    if (!securePart.isSecureEntireRequest()) continue;
                    signEntireRequestPart = securePart;
                }
                continue;
            }
            if (!XMLSecurityConstants.ENCRYPTION.equals(action)) continue;
            XMLEncryptOutputProcessor encryptOutputProcessor = new XMLEncryptOutputProcessor();
            this.initializeOutputProcessor(outputProcessorChain, encryptOutputProcessor, action, actionOrder++);
            this.configureEncryptionKeys(outboundSecurityContext);
            List<SecurePart> encryptionParts = this.securityProperties.getEncryptionSecureParts();
            for (SecurePart securePart : encryptionParts) {
                if (securePart.getIdToSecure() == null && securePart.getName() != null) {
                    outputProcessorChain.getSecurityContext().putAsMap("encryptionParts", securePart.getName(), securePart);
                    continue;
                }
                if (securePart.getIdToSecure() != null) {
                    outputProcessorChain.getSecurityContext().putAsMap("encryptionParts", securePart.getIdToSecure(), securePart);
                    continue;
                }
                if (!securePart.isSecureEntireRequest()) continue;
                encryptEntireRequestPart = securePart;
            }
        }
        if (output instanceof OutputStream) {
            finalOutputProcessor = new FinalOutputProcessor((OutputStream)output, encoding);
            this.initializeOutputProcessor(outputProcessorChain, finalOutputProcessor, null, -1);
        } else if (output instanceof XMLStreamWriter) {
            finalOutputProcessor = new FinalOutputProcessor((XMLStreamWriter)output);
            this.initializeOutputProcessor(outputProcessorChain, finalOutputProcessor, null, -1);
        } else {
            throw new IllegalArgumentException(output + " is not supported as output");
        }
        XMLSecurityStreamWriter streamWriter = new XMLSecurityStreamWriter(outputProcessorChain);
        streamWriter.setSignEntireRequestPart(signEntireRequestPart);
        streamWriter.setEncryptEntireRequestPart(encryptEntireRequestPart);
        return streamWriter;
    }

    private void initializeOutputProcessor(OutputProcessorChainImpl outputProcessorChain, OutputProcessor outputProcessor, XMLSecurityConstants.Action action, int actionOrder) throws XMLSecurityException {
        outputProcessor.setXMLSecurityProperties(this.securityProperties);
        outputProcessor.setAction(action, actionOrder);
        outputProcessor.init(outputProcessorChain);
    }

    private void configureSignatureKeys(OutboundSecurityContextImpl outboundSecurityContext) throws XMLSecurityException {
        Key key = this.securityProperties.getSignatureKey();
        X509Certificate[] x509Certificates = this.securityProperties.getSignatureCerts();
        if (key instanceof PrivateKey && (x509Certificates == null || x509Certificates.length == 0) && this.securityProperties.getSignatureVerificationKey() == null) {
            throw new XMLSecurityException("stax.signature.publicKeyOrCertificateMissing");
        }
        final String securityTokenid = IDGenerator.generateID("SIG");
        final GenericOutboundSecurityToken securityToken = new GenericOutboundSecurityToken(securityTokenid, SecurityTokenConstants.DefaultToken, key, x509Certificates);
        if (this.securityProperties.getSignatureVerificationKey() instanceof PublicKey) {
            securityToken.setPublicKey((PublicKey)this.securityProperties.getSignatureVerificationKey());
        }
        SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider = new SecurityTokenProvider<OutboundSecurityToken>(){

            @Override
            public OutboundSecurityToken getSecurityToken() throws XMLSecurityException {
                return securityToken;
            }

            @Override
            public String getId() {
                return securityTokenid;
            }
        };
        outboundSecurityContext.registerSecurityTokenProvider(securityTokenid, securityTokenProvider);
        outboundSecurityContext.put("PROP_USE_THIS_TOKEN_ID_FOR_SIGNATURE", securityTokenid);
    }

    private void configureEncryptionKeys(OutboundSecurityContextImpl outboundSecurityContext) throws XMLSecurityException {
        Key transportKey = this.securityProperties.getEncryptionTransportKey();
        X509Certificate transportCert = this.securityProperties.getEncryptionUseThisCertificate();
        X509Certificate[] transportCerts = null;
        if (transportCert != null) {
            transportCerts = new X509Certificate[]{transportCert};
        }
        GenericOutboundSecurityToken transportSecurityToken = new GenericOutboundSecurityToken(IDGenerator.generateID(null), SecurityTokenConstants.DefaultToken, transportKey, transportCerts);
        Key key = this.securityProperties.getEncryptionKey();
        if (key == null) {
            KeyGenerator keyGen;
            if (transportCert == null && transportKey == null) {
                throw new XMLSecurityException("stax.encryption.encryptionKeyMissing");
            }
            String keyAlgorithm = JCEAlgorithmMapper.getJCEKeyAlgorithmFromURI(this.securityProperties.getEncryptionSymAlgorithm());
            try {
                keyGen = KeyGenerator.getInstance(keyAlgorithm);
            }
            catch (NoSuchAlgorithmException e) {
                throw new XMLSecurityException(e);
            }
            if (keyAlgorithm.contains("AES")) {
                int keyLength = JCEAlgorithmMapper.getKeyLengthFromURI(this.securityProperties.getEncryptionSymAlgorithm());
                keyGen.init(keyLength);
            }
            key = keyGen.generateKey();
        }
        final String securityTokenid = IDGenerator.generateID(null);
        final GenericOutboundSecurityToken securityToken = new GenericOutboundSecurityToken(securityTokenid, SecurityTokenConstants.DefaultToken, key);
        securityToken.setKeyWrappingToken(transportSecurityToken);
        SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider = new SecurityTokenProvider<OutboundSecurityToken>(){

            @Override
            public OutboundSecurityToken getSecurityToken() throws XMLSecurityException {
                return securityToken;
            }

            @Override
            public String getId() {
                return securityTokenid;
            }
        };
        outboundSecurityContext.registerSecurityTokenProvider(securityTokenid, securityTokenProvider);
        outboundSecurityContext.put("PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION", securityTokenid);
    }
}

