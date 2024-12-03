/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.dsig.facets;

import java.security.MessageDigest;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.security.auth.x500.X500Principal;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.namespace.QName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.SignatureInfo;
import org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet;
import org.apache.poi.poifs.crypt.dsig.facets.SignatureFacetHelper;
import org.apache.poi.poifs.crypt.dsig.services.SignaturePolicyService;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.etsi.uri.x01903.v13.AnyType;
import org.etsi.uri.x01903.v13.CertIDListType;
import org.etsi.uri.x01903.v13.CertIDType;
import org.etsi.uri.x01903.v13.ClaimedRolesListType;
import org.etsi.uri.x01903.v13.CommitmentTypeIndicationType;
import org.etsi.uri.x01903.v13.DataObjectFormatType;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;
import org.etsi.uri.x01903.v13.ObjectIdentifierType;
import org.etsi.uri.x01903.v13.QualifyingPropertiesDocument;
import org.etsi.uri.x01903.v13.QualifyingPropertiesType;
import org.etsi.uri.x01903.v13.SignaturePolicyIdType;
import org.etsi.uri.x01903.v13.SignaturePolicyIdentifierType;
import org.etsi.uri.x01903.v13.SignedDataObjectPropertiesType;
import org.etsi.uri.x01903.v13.SignedPropertiesType;
import org.etsi.uri.x01903.v13.SignedSignaturePropertiesType;
import org.etsi.uri.x01903.v13.SignerRoleType;
import org.w3.x2000.x09.xmldsig.DigestMethodType;
import org.w3.x2000.x09.xmldsig.X509IssuerSerialType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XAdESSignatureFacet
implements SignatureFacet {
    private static final Logger LOG = LogManager.getLogger(XAdESSignatureFacet.class);
    private static final String XADES_TYPE = "http://uri.etsi.org/01903#SignedProperties";
    private final Map<String, String> dataObjectFormatMimeTypes = new HashMap<String, String>();

    @Override
    public void preSign(SignatureInfo signatureInfo, Document document, List<Reference> references, List<XMLObject> objects) throws XMLSignatureException {
        LOG.atDebug().log("preSign");
        SignatureConfig signatureConfig = signatureInfo.getSignatureConfig();
        QualifyingPropertiesDocument qualDoc = QualifyingPropertiesDocument.Factory.newInstance();
        QualifyingPropertiesType qualifyingProperties = qualDoc.addNewQualifyingProperties();
        qualifyingProperties.setTarget("#" + signatureConfig.getPackageSignatureId());
        this.createSignedProperties(signatureInfo, qualifyingProperties);
        objects.add(this.addXadesObject(signatureInfo, document, qualifyingProperties));
        references.add(this.addXadesReference(signatureInfo));
    }

    protected SignedPropertiesType createSignedProperties(SignatureInfo signatureInfo, QualifyingPropertiesType qualifyingProperties) {
        SignedPropertiesType signedProperties = qualifyingProperties.addNewSignedProperties();
        signedProperties.setId(signatureInfo.getSignatureConfig().getXadesSignatureId());
        SignedSignaturePropertiesType signedSignatureProperties = signedProperties.addNewSignedSignatureProperties();
        this.addSigningTime(signatureInfo, signedSignatureProperties);
        this.addCertificate(signatureInfo, signedSignatureProperties);
        this.addXadesRole(signatureInfo, signedSignatureProperties);
        this.addPolicy(signatureInfo, signedSignatureProperties);
        this.addMimeTypes(signatureInfo, signedProperties);
        this.addCommitmentType(signatureInfo, signedProperties);
        return signedProperties;
    }

    protected void addSigningTime(SignatureInfo signatureInfo, SignedSignaturePropertiesType signedSignatureProperties) {
        SignatureConfig signatureConfig = signatureInfo.getSignatureConfig();
        Calendar xmlGregorianCalendar = Calendar.getInstance(TimeZone.getTimeZone("Z"), Locale.ROOT);
        xmlGregorianCalendar.setTime(signatureConfig.getExecutionTime());
        xmlGregorianCalendar.clear(14);
        signedSignatureProperties.setSigningTime(xmlGregorianCalendar);
    }

    protected void addCertificate(SignatureInfo signatureInfo, SignedSignaturePropertiesType signedSignatureProperties) {
        SignatureConfig signatureConfig = signatureInfo.getSignatureConfig();
        List<X509Certificate> chain = signatureConfig.getSigningCertificateChain();
        if (chain == null || chain.isEmpty()) {
            throw new RuntimeException("no signing certificate chain available");
        }
        CertIDListType signingCertificates = signedSignatureProperties.addNewSigningCertificate();
        CertIDType certId = signingCertificates.addNewCert();
        XAdESSignatureFacet.setCertID(certId, signatureConfig, signatureConfig.isXadesIssuerNameNoReverseOrder(), chain.get(0));
    }

    protected void addXadesRole(SignatureInfo signatureInfo, SignedSignaturePropertiesType signedSignatureProperties) {
        SignatureConfig signatureConfig = signatureInfo.getSignatureConfig();
        String role = signatureConfig.getXadesRole();
        if (role == null || role.isEmpty()) {
            return;
        }
        SignerRoleType signerRole = signedSignatureProperties.addNewSignerRole();
        signedSignatureProperties.setSignerRole(signerRole);
        ClaimedRolesListType claimedRolesList = signerRole.addNewClaimedRoles();
        AnyType claimedRole = claimedRolesList.addNewClaimedRole();
        XmlString roleString = XmlString.Factory.newInstance();
        roleString.setStringValue(role);
        XAdESSignatureFacet.insertXChild(claimedRole, roleString);
    }

    protected void addPolicy(SignatureInfo signatureInfo, SignedSignaturePropertiesType signedSignatureProperties) {
        SignatureConfig signatureConfig = signatureInfo.getSignatureConfig();
        SignaturePolicyService policyService = signatureConfig.getSignaturePolicyService();
        if (policyService == null) {
            if (signatureConfig.isXadesSignaturePolicyImplied()) {
                signedSignatureProperties.addNewSignaturePolicyIdentifier().addNewSignaturePolicyImplied();
            }
            return;
        }
        SignaturePolicyIdentifierType policyId = signedSignatureProperties.addNewSignaturePolicyIdentifier();
        SignaturePolicyIdType signaturePolicyId = policyId.addNewSignaturePolicyId();
        ObjectIdentifierType oit = signaturePolicyId.addNewSigPolicyId();
        oit.setDescription(policyService.getSignaturePolicyDescription());
        oit.addNewIdentifier().setStringValue(policyService.getSignaturePolicyIdentifier());
        byte[] signaturePolicyDocumentData = policyService.getSignaturePolicyDocument();
        DigestAlgAndValueType sigPolicyHash = signaturePolicyId.addNewSigPolicyHash();
        XAdESSignatureFacet.setDigestAlgAndValue(sigPolicyHash, signaturePolicyDocumentData, signatureConfig.getDigestAlgo());
        String signaturePolicyDownloadUrl = policyService.getSignaturePolicyDownloadUrl();
        if (signaturePolicyDownloadUrl == null) {
            return;
        }
        AnyType sigPolicyQualifier = signaturePolicyId.addNewSigPolicyQualifiers().addNewSigPolicyQualifier();
        XmlString spUriElement = XmlString.Factory.newInstance();
        spUriElement.setStringValue(signaturePolicyDownloadUrl);
        XAdESSignatureFacet.insertXChild(sigPolicyQualifier, spUriElement);
    }

    protected void addMimeTypes(SignatureInfo signatureInfo, SignedPropertiesType signedProperties) {
        if (this.dataObjectFormatMimeTypes.isEmpty()) {
            return;
        }
        List<DataObjectFormatType> dataObjectFormats = signedProperties.addNewSignedDataObjectProperties().getDataObjectFormatList();
        this.dataObjectFormatMimeTypes.forEach((key, value) -> {
            DataObjectFormatType dof = DataObjectFormatType.Factory.newInstance();
            dof.setObjectReference("#" + key);
            dof.setMimeType((String)value);
            dataObjectFormats.add(dof);
        });
    }

    protected XMLObject addXadesObject(SignatureInfo signatureInfo, Document document, QualifyingPropertiesType qualifyingProperties) {
        Element qualDocEl = XAdESSignatureFacet.importNode(document, qualifyingProperties);
        List<DOMStructure> xadesObjectContent = Collections.singletonList(new DOMStructure(qualDocEl));
        return signatureInfo.getSignatureFactory().newXMLObject(xadesObjectContent, null, null, null);
    }

    protected void addCommitmentType(SignatureInfo signatureInfo, SignedPropertiesType signedProperties) {
        SignatureConfig signatureConfig = signatureInfo.getSignatureConfig();
        String desc = signatureConfig.getSignatureDescription();
        String commit = signatureConfig.getCommitmentType();
        if (desc == null && commit == null) {
            return;
        }
        SignedDataObjectPropertiesType dopt = signedProperties.isSetSignedDataObjectProperties() ? signedProperties.getSignedDataObjectProperties() : signedProperties.addNewSignedDataObjectProperties();
        CommitmentTypeIndicationType cti = dopt.addNewCommitmentTypeIndication();
        if (commit != null) {
            ObjectIdentifierType ctid = cti.addNewCommitmentTypeId();
            ctid.addNewIdentifier().setStringValue("http://uri.etsi.org/01903/v1.2.2#ProofOfOrigin");
            ctid.setDescription(signatureConfig.getCommitmentType());
        }
        if (desc != null) {
            cti.addNewAllSignedDataObjects();
            AnyType ctq = cti.addNewCommitmentTypeQualifiers().addNewCommitmentTypeQualifier();
            ctq.set(XmlString.Factory.newValue(desc));
        }
    }

    protected Reference addXadesReference(SignatureInfo signatureInfo) throws XMLSignatureException {
        SignatureConfig signatureConfig = signatureInfo.getSignatureConfig();
        List<Transform> transforms = Collections.singletonList(SignatureFacetHelper.newTransform(signatureInfo, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"));
        return SignatureFacetHelper.newReference(signatureInfo, "#" + signatureConfig.getXadesSignatureId(), transforms, XADES_TYPE);
    }

    protected static void setDigestAlgAndValue(DigestAlgAndValueType digestAlgAndValue, byte[] data, HashAlgorithm digestAlgo) {
        DigestMethodType digestMethod = digestAlgAndValue.addNewDigestMethod();
        digestMethod.setAlgorithm(SignatureConfig.getDigestMethodUri(digestAlgo));
        MessageDigest messageDigest = CryptoFunctions.getMessageDigest(digestAlgo);
        byte[] digestValue = messageDigest.digest(data);
        digestAlgAndValue.setDigestValue(digestValue);
    }

    protected static void setCertID(CertIDType certId, SignatureConfig signatureConfig, boolean issuerNameNoReverseOrder, X509Certificate certificate) {
        byte[] encodedCertificate;
        X509IssuerSerialType issuerSerial = certId.addNewIssuerSerial();
        X500Principal issuerPrincipal = certificate.getIssuerX500Principal();
        String issuerName = issuerNameNoReverseOrder ? issuerPrincipal.getName().replace(",", ", ") : issuerPrincipal.toString();
        issuerSerial.setX509IssuerName(issuerName);
        issuerSerial.setX509SerialNumber(certificate.getSerialNumber());
        try {
            encodedCertificate = certificate.getEncoded();
        }
        catch (CertificateEncodingException e) {
            throw new RuntimeException("certificate encoding error: " + e.getMessage(), e);
        }
        DigestAlgAndValueType certDigest = certId.addNewCertDigest();
        XAdESSignatureFacet.setDigestAlgAndValue(certDigest, encodedCertificate, signatureConfig.getXadesDigestAlgo());
    }

    public void addMimeType(String dsReferenceUri, String mimetype) {
        this.dataObjectFormatMimeTypes.put(dsReferenceUri, mimetype);
    }

    protected static void insertXChild(XmlObject root, XmlObject child) {
        try (XmlCursor rootCursor = root.newCursor();){
            rootCursor.toEndToken();
            try (XmlCursor childCursor = child.newCursor();){
                childCursor.toNextToken();
                childCursor.moveXml(rootCursor);
            }
        }
    }

    private static Element importNode(Document document, XmlObject xo) {
        try (XmlCursor cur = xo.newCursor();){
            QName elName = cur.getName();
            Element lastNode = document.createElementNS(elName.getNamespaceURI(), elName.getLocalPart());
            block17: while (cur.hasNextToken()) {
                XmlCursor.TokenType nextToken = cur.toNextToken();
                switch (nextToken.intValue()) {
                    default: {
                        continue block17;
                    }
                    case 3: {
                        QName name = cur.getName();
                        Element el = document.createElementNS(name.getNamespaceURI(), name.getLocalPart());
                        lastNode = (Element)lastNode.appendChild(el);
                        continue block17;
                    }
                    case 4: {
                        Element parent = (Element)lastNode.getParentNode();
                        if (parent == null) continue block17;
                        lastNode = parent;
                        continue block17;
                    }
                    case 5: {
                        lastNode.appendChild(document.createTextNode(cur.getTextValue()));
                        continue block17;
                    }
                    case 6: {
                        QName name = cur.getName();
                        lastNode.setAttributeNS(name.getNamespaceURI(), name.getLocalPart(), cur.getTextValue());
                        if (!"Id".equals(name.getLocalPart())) continue block17;
                        lastNode.setIdAttribute("Id", true);
                        continue block17;
                    }
                    case 7: {
                        QName name = cur.getName();
                        lastNode.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + name.getPrefix(), name.getNamespaceURI());
                        continue block17;
                    }
                    case 8: 
                }
                lastNode.appendChild(document.createComment(cur.getTextValue()));
            }
            Element element = lastNode;
            return element;
        }
    }
}

