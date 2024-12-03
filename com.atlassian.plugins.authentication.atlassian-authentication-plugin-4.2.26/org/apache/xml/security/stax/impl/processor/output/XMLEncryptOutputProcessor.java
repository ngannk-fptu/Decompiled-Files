/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.stax.impl.processor.output;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.JCEAlgorithmMapper;
import org.apache.xml.security.stax.ext.AbstractOutputProcessor;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.EncryptionPartDef;
import org.apache.xml.security.stax.impl.processor.output.AbstractEncryptOutputProcessor;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLEncryptOutputProcessor
extends AbstractEncryptOutputProcessor {
    private static final transient Logger LOG = LoggerFactory.getLogger(XMLEncryptOutputProcessor.class);

    @Override
    public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        if (xmlSecEvent.getEventType() == 1) {
            SecurePart securePart;
            XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();
            if (this.getActiveInternalEncryptionOutputProcessor() == null && (securePart = this.securePartMatches(xmlSecStartElement, outputProcessorChain, "encryptionParts")) != null) {
                LOG.debug("Matched encryptionPart for encryption");
                String tokenId = (String)outputProcessorChain.getSecurityContext().get("PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION");
                SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider = outputProcessorChain.getSecurityContext().getSecurityTokenProvider(tokenId);
                OutboundSecurityToken securityToken = securityTokenProvider.getSecurityToken();
                EncryptionPartDef encryptionPartDef = new EncryptionPartDef();
                encryptionPartDef.setSecurePart(securePart);
                encryptionPartDef.setModifier(securePart.getModifier());
                encryptionPartDef.setEncRefId(IDGenerator.generateID(null));
                encryptionPartDef.setKeyId(securityTokenProvider.getId());
                encryptionPartDef.setSymmetricKey(securityToken.getSecretKey(this.getSecurityProperties().getEncryptionSymAlgorithm()));
                outputProcessorChain.getSecurityContext().putAsList(EncryptionPartDef.class, encryptionPartDef);
                AbstractEncryptOutputProcessor.AbstractInternalEncryptionOutputProcessor internalEncryptionOutputProcessor = this.createInternalEncryptionOutputProcessor(encryptionPartDef, xmlSecStartElement, outputProcessorChain.getDocumentContext().getEncoding(), (OutboundSecurityToken)securityToken.getKeyWrappingToken());
                internalEncryptionOutputProcessor.setXMLSecurityProperties(this.getSecurityProperties());
                internalEncryptionOutputProcessor.setAction(this.getAction(), this.getActionOrder());
                internalEncryptionOutputProcessor.init(outputProcessorChain);
                this.setActiveInternalEncryptionOutputProcessor(internalEncryptionOutputProcessor);
            }
        }
        outputProcessorChain.processEvent(xmlSecEvent);
    }

    protected AbstractEncryptOutputProcessor.AbstractInternalEncryptionOutputProcessor createInternalEncryptionOutputProcessor(EncryptionPartDef encryptionPartDef, XMLSecStartElement startElement, String encoding, final OutboundSecurityToken keyWrappingToken) throws XMLStreamException, XMLSecurityException {
        AbstractEncryptOutputProcessor.AbstractInternalEncryptionOutputProcessor processor = new AbstractEncryptOutputProcessor.AbstractInternalEncryptionOutputProcessor(encryptionPartDef, startElement, encoding){

            @Override
            protected void createKeyInfoStructure(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
                if (keyWrappingToken == null) {
                    return;
                }
                String encryptionKeyTransportAlgorithm = this.getSecurityProperties().getEncryptionKeyTransportAlgorithm();
                PublicKey pubKey = keyWrappingToken.getPublicKey();
                Key secretKey = keyWrappingToken.getSecretKey(encryptionKeyTransportAlgorithm);
                if (pubKey == null && secretKey == null) {
                    return;
                }
                this.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo, true, null);
                ArrayList<XMLSecAttribute> attributes = new ArrayList<XMLSecAttribute>(1);
                String keyId = IDGenerator.generateID("EK");
                attributes.add(this.createAttribute(XMLSecurityConstants.ATT_NULL_Id, keyId));
                this.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptedKey, true, attributes);
                attributes = new ArrayList(1);
                attributes.add(this.createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, encryptionKeyTransportAlgorithm));
                this.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptionMethod, false, attributes);
                String encryptionKeyTransportDigestAlgorithm = this.getSecurityProperties().getEncryptionKeyTransportDigestAlgorithm();
                String encryptionKeyTransportMGFAlgorithm = this.getSecurityProperties().getEncryptionKeyTransportMGFAlgorithm();
                if ("http://www.w3.org/2009/xmlenc11#rsa-oaep".equals(encryptionKeyTransportAlgorithm) || "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(encryptionKeyTransportAlgorithm)) {
                    byte[] oaepParams = this.getSecurityProperties().getEncryptionKeyTransportOAEPParams();
                    if (oaepParams != null) {
                        this.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_OAEPparams, false, null);
                        this.createCharactersAndOutputAsEvent(outputProcessorChain, XMLUtils.encodeToString(oaepParams));
                        this.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_OAEPparams);
                    }
                    if (encryptionKeyTransportDigestAlgorithm != null) {
                        attributes = new ArrayList(1);
                        attributes.add(this.createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, encryptionKeyTransportDigestAlgorithm));
                        this.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestMethod, true, attributes);
                        this.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestMethod);
                    }
                    if (encryptionKeyTransportMGFAlgorithm != null) {
                        attributes = new ArrayList(1);
                        attributes.add(this.createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, encryptionKeyTransportMGFAlgorithm));
                        this.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc11_MGF, true, attributes);
                        this.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc11_MGF);
                    }
                }
                this.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptionMethod);
                this.createKeyInfoStructureForEncryptedKey(outputProcessorChain, keyWrappingToken);
                this.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherData, false, null);
                this.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherValue, false, null);
                String jceid = JCEAlgorithmMapper.translateURItoJCEID(encryptionKeyTransportAlgorithm);
                if (jceid == null) {
                    throw new XMLSecurityException("algorithms.NoSuchMap", new Object[]{encryptionKeyTransportAlgorithm});
                }
                try {
                    int blockSize;
                    Cipher cipher = Cipher.getInstance(jceid);
                    OAEPParameterSpec algorithmParameterSpec = null;
                    if ("http://www.w3.org/2009/xmlenc11#rsa-oaep".equals(encryptionKeyTransportAlgorithm) || "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(encryptionKeyTransportAlgorithm)) {
                        String jceDigestAlgorithm = "SHA-1";
                        if (encryptionKeyTransportDigestAlgorithm != null) {
                            jceDigestAlgorithm = JCEAlgorithmMapper.translateURItoJCEID(encryptionKeyTransportDigestAlgorithm);
                        }
                        PSource.PSpecified pSource = PSource.PSpecified.DEFAULT;
                        byte[] oaepParams = this.getSecurityProperties().getEncryptionKeyTransportOAEPParams();
                        if (oaepParams != null) {
                            pSource = new PSource.PSpecified(oaepParams);
                        }
                        MGF1ParameterSpec mgfParameterSpec = new MGF1ParameterSpec("SHA-1");
                        if (encryptionKeyTransportMGFAlgorithm != null) {
                            String jceMGFAlgorithm = JCEAlgorithmMapper.translateURItoJCEID(encryptionKeyTransportMGFAlgorithm);
                            mgfParameterSpec = new MGF1ParameterSpec(jceMGFAlgorithm);
                        }
                        algorithmParameterSpec = new OAEPParameterSpec(jceDigestAlgorithm, "MGF1", mgfParameterSpec, pSource);
                    }
                    if (pubKey != null) {
                        cipher.init(3, (Key)pubKey, algorithmParameterSpec);
                    } else {
                        cipher.init(3, secretKey, algorithmParameterSpec);
                    }
                    String tokenId = (String)outputProcessorChain.getSecurityContext().get("PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION");
                    SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider = outputProcessorChain.getSecurityContext().getSecurityTokenProvider(tokenId);
                    OutboundSecurityToken securityToken = securityTokenProvider.getSecurityToken();
                    Key sessionKey = securityToken.getSecretKey(this.getSecurityProperties().getEncryptionSymAlgorithm());
                    if (pubKey != null && (blockSize = cipher.getBlockSize()) > 0 && blockSize < sessionKey.getEncoded().length) {
                        throw new XMLSecurityException("stax.unsupportedKeyTransp");
                    }
                    byte[] encryptedEphemeralKey = cipher.wrap(sessionKey);
                    this.createCharactersAndOutputAsEvent(outputProcessorChain, XMLUtils.encodeToString(encryptedEphemeralKey));
                }
                catch (NoSuchPaddingException e) {
                    throw new XMLSecurityException(e);
                }
                catch (NoSuchAlgorithmException e) {
                    throw new XMLSecurityException(e);
                }
                catch (InvalidKeyException e) {
                    throw new XMLSecurityException(e);
                }
                catch (IllegalBlockSizeException e) {
                    throw new XMLSecurityException(e);
                }
                catch (InvalidAlgorithmParameterException e) {
                    throw new XMLSecurityException(e);
                }
                this.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherValue);
                this.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherData);
                this.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptedKey);
                this.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo);
            }

            protected void createKeyInfoStructureForEncryptedKey(OutputProcessorChain outputProcessorChain, OutboundSecurityToken securityToken) throws XMLStreamException, XMLSecurityException {
                SecurityTokenConstants.KeyIdentifier keyIdentifier = this.getSecurityProperties().getEncryptionKeyIdentifier();
                X509Certificate[] x509Certificates = securityToken.getX509Certificates();
                if (x509Certificates == null) {
                    if (securityToken.getPublicKey() != null && SecurityTokenConstants.KeyIdentifier_KeyValue.equals(keyIdentifier)) {
                        this.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo, true, null);
                        XMLSecurityUtils.createKeyValueTokenStructure((AbstractOutputProcessor)this, outputProcessorChain, securityToken.getPublicKey());
                        this.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo);
                    }
                    return;
                }
                if (!SecurityTokenConstants.KeyIdentifier_NoKeyInfo.equals(keyIdentifier)) {
                    this.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo, true, null);
                    if (keyIdentifier == null || SecurityTokenConstants.KeyIdentifier_IssuerSerial.equals(keyIdentifier)) {
                        XMLSecurityUtils.createX509IssuerSerialStructure(this, outputProcessorChain, x509Certificates);
                    } else if (SecurityTokenConstants.KeyIdentifier_KeyValue.equals(keyIdentifier)) {
                        XMLSecurityUtils.createKeyValueTokenStructure((AbstractOutputProcessor)this, outputProcessorChain, x509Certificates);
                    } else if (SecurityTokenConstants.KeyIdentifier_SkiKeyIdentifier.equals(keyIdentifier)) {
                        XMLSecurityUtils.createX509SubjectKeyIdentifierStructure(this, outputProcessorChain, x509Certificates);
                    } else if (SecurityTokenConstants.KeyIdentifier_X509KeyIdentifier.equals(keyIdentifier)) {
                        XMLSecurityUtils.createX509CertificateStructure(this, outputProcessorChain, x509Certificates);
                    } else if (SecurityTokenConstants.KeyIdentifier_X509SubjectName.equals(keyIdentifier)) {
                        XMLSecurityUtils.createX509SubjectNameStructure(this, outputProcessorChain, x509Certificates);
                    } else if (SecurityTokenConstants.KeyIdentifier_KeyName.equals(keyIdentifier)) {
                        String keyName = this.getSecurityProperties().getEncryptionKeyName();
                        XMLSecurityUtils.createKeyNameTokenStructure(this, outputProcessorChain, keyName);
                    } else {
                        throw new XMLSecurityException("stax.unsupportedToken", new Object[]{keyIdentifier});
                    }
                    this.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo);
                }
            }
        };
        processor.getAfterProcessors().add(XMLEncryptOutputProcessor.class);
        return processor;
    }
}

