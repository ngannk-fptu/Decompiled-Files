/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Enumerated
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1OutputStream
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.IssuingDistributionPoint
 *  org.bouncycastle.asn1.x509.PolicyInformation
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.jcajce.PKIXCertStoreSelector
 *  org.bouncycastle.util.Encodable
 *  org.bouncycastle.util.Selector
 *  org.bouncycastle.util.Store
 *  org.bouncycastle.util.StoreException
 */
package org.bouncycastle.pkix.jcajce;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.PolicyQualifierInfo;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.pkix.jcajce.AnnotatedException;
import org.bouncycastle.pkix.jcajce.CertStatus;
import org.bouncycastle.pkix.jcajce.PKIXPolicyNode;
import org.bouncycastle.pkix.jcajce.X509CertStoreSelector;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

class CertPathValidatorUtilities {
    protected static final String CERTIFICATE_POLICIES = Extension.certificatePolicies.getId();
    protected static final String BASIC_CONSTRAINTS = Extension.basicConstraints.getId();
    protected static final String POLICY_MAPPINGS = Extension.policyMappings.getId();
    protected static final String SUBJECT_ALTERNATIVE_NAME = Extension.subjectAlternativeName.getId();
    protected static final String NAME_CONSTRAINTS = Extension.nameConstraints.getId();
    protected static final String KEY_USAGE = Extension.keyUsage.getId();
    protected static final String INHIBIT_ANY_POLICY = Extension.inhibitAnyPolicy.getId();
    protected static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();
    protected static final String DELTA_CRL_INDICATOR = Extension.deltaCRLIndicator.getId();
    protected static final String POLICY_CONSTRAINTS = Extension.policyConstraints.getId();
    protected static final String FRESHEST_CRL = Extension.freshestCRL.getId();
    protected static final String CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
    protected static final String AUTHORITY_KEY_IDENTIFIER = Extension.authorityKeyIdentifier.getId();
    protected static final String ANY_POLICY = "2.5.29.32.0";
    protected static final String CRL_NUMBER = Extension.cRLNumber.getId();
    protected static final int KEY_CERT_SIGN = 5;
    protected static final int CRL_SIGN = 6;
    protected static final String[] crlReasons = new String[]{"unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", "aACompromise"};

    CertPathValidatorUtilities() {
    }

    protected static X500Principal getEncodedIssuerPrincipal(Object cert) {
        if (cert instanceof X509Certificate) {
            return ((X509Certificate)cert).getIssuerX500Principal();
        }
        throw new IllegalArgumentException("unknown certificate type");
    }

    protected static Date getValidDate(PKIXParameters paramsPKIX) {
        Date validDate = paramsPKIX.getDate();
        if (validDate == null) {
            validDate = new Date();
        }
        return validDate;
    }

    protected static X500Principal getSubjectPrincipal(X509Certificate cert) {
        return cert.getSubjectX500Principal();
    }

    protected static boolean isSelfIssued(X509Certificate cert) {
        return cert.getSubjectDN().equals(cert.getIssuerDN());
    }

    protected static ASN1Primitive getExtensionValue(X509Extension ext, String oid) throws AnnotatedException {
        byte[] bytes = ext.getExtensionValue(oid);
        if (bytes == null) {
            return null;
        }
        return CertPathValidatorUtilities.getObject(oid, bytes);
    }

    private static ASN1Primitive getObject(String oid, byte[] ext) throws AnnotatedException {
        try {
            ASN1InputStream aIn = new ASN1InputStream(ext);
            ASN1OctetString octs = (ASN1OctetString)aIn.readObject();
            aIn = new ASN1InputStream(octs.getOctets());
            return aIn.readObject();
        }
        catch (Exception e) {
            throw new AnnotatedException("exception processing extension " + oid, e);
        }
    }

    protected static X500Principal getIssuerPrincipal(X509CRL crl) {
        return crl.getIssuerX500Principal();
    }

    protected static AlgorithmIdentifier getAlgorithmIdentifier(PublicKey key) throws CertPathValidatorException {
        try {
            ASN1InputStream aIn = new ASN1InputStream(key.getEncoded());
            SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance((Object)aIn.readObject());
            return info.getAlgorithm();
        }
        catch (Exception e) {
            throw new CertPathValidatorException("Subject public key cannot be decoded.", e);
        }
    }

    protected static final Set getQualifierSet(ASN1Sequence qualifiers) throws CertPathValidatorException {
        HashSet<PolicyQualifierInfo> pq = new HashSet<PolicyQualifierInfo>();
        if (qualifiers == null) {
            return pq;
        }
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ASN1OutputStream aOut = ASN1OutputStream.create((OutputStream)bOut);
        Enumeration e = qualifiers.getObjects();
        while (e.hasMoreElements()) {
            try {
                aOut.writeObject((ASN1Encodable)e.nextElement());
                pq.add(new PolicyQualifierInfo(bOut.toByteArray()));
            }
            catch (IOException ex) {
                throw new CertPathValidatorException("Policy qualifier info cannot be decoded.", ex);
            }
            bOut.reset();
        }
        return pq;
    }

    protected static PKIXPolicyNode removePolicyNode(PKIXPolicyNode validPolicyTree, List[] policyNodes, PKIXPolicyNode _node) {
        PKIXPolicyNode _parent = (PKIXPolicyNode)_node.getParent();
        if (validPolicyTree == null) {
            return null;
        }
        if (_parent == null) {
            for (int j = 0; j < policyNodes.length; ++j) {
                policyNodes[j] = new ArrayList();
            }
            return null;
        }
        _parent.removeChild(_node);
        CertPathValidatorUtilities.removePolicyNodeRecurse(policyNodes, _node);
        return validPolicyTree;
    }

    private static void removePolicyNodeRecurse(List[] policyNodes, PKIXPolicyNode _node) {
        policyNodes[_node.getDepth()].remove(_node);
        if (_node.hasChildren()) {
            Iterator _iter = _node.getChildren();
            while (_iter.hasNext()) {
                PKIXPolicyNode _child = (PKIXPolicyNode)_iter.next();
                CertPathValidatorUtilities.removePolicyNodeRecurse(policyNodes, _child);
            }
        }
    }

    protected static boolean processCertD1i(int index, List[] policyNodes, ASN1ObjectIdentifier pOid, Set pq) {
        List policyNodeVec = policyNodes[index - 1];
        for (int j = 0; j < policyNodeVec.size(); ++j) {
            PKIXPolicyNode node = (PKIXPolicyNode)policyNodeVec.get(j);
            Set expectedPolicies = node.getExpectedPolicies();
            if (!expectedPolicies.contains(pOid.getId())) continue;
            HashSet<String> childExpectedPolicies = new HashSet<String>();
            childExpectedPolicies.add(pOid.getId());
            PKIXPolicyNode child = new PKIXPolicyNode(new ArrayList(), index, childExpectedPolicies, node, pq, pOid.getId(), false);
            node.addChild(child);
            policyNodes[index].add(child);
            return true;
        }
        return false;
    }

    protected static void processCertD1ii(int index, List[] policyNodes, ASN1ObjectIdentifier _poid, Set _pq) {
        List policyNodeVec = policyNodes[index - 1];
        for (int j = 0; j < policyNodeVec.size(); ++j) {
            PKIXPolicyNode _node = (PKIXPolicyNode)policyNodeVec.get(j);
            if (!ANY_POLICY.equals(_node.getValidPolicy())) continue;
            HashSet<String> _childExpectedPolicies = new HashSet<String>();
            _childExpectedPolicies.add(_poid.getId());
            PKIXPolicyNode _child = new PKIXPolicyNode(new ArrayList(), index, _childExpectedPolicies, _node, _pq, _poid.getId(), false);
            _node.addChild(_child);
            policyNodes[index].add(_child);
            return;
        }
    }

    protected static void prepareNextCertB1(int i, List[] policyNodes, String id_p, Map m_idp, X509Certificate cert) throws AnnotatedException, CertPathValidatorException {
        boolean idp_found = false;
        for (PKIXPolicyNode node : policyNodes[i]) {
            if (!node.getValidPolicy().equals(id_p)) continue;
            idp_found = true;
            node.setExpectedPolicies((Set)m_idp.get(id_p));
            break;
        }
        if (!idp_found) {
            for (PKIXPolicyNode node : policyNodes[i]) {
                PKIXPolicyNode p_node;
                if (!ANY_POLICY.equals(node.getValidPolicy())) continue;
                Set pq = null;
                ASN1Sequence policies = null;
                try {
                    policies = DERSequence.getInstance((Object)CertPathValidatorUtilities.getExtensionValue(cert, CERTIFICATE_POLICIES));
                }
                catch (Exception e) {
                    throw new AnnotatedException("Certificate policies cannot be decoded.", e);
                }
                Enumeration e = policies.getObjects();
                while (e.hasMoreElements()) {
                    PolicyInformation pinfo = null;
                    try {
                        pinfo = PolicyInformation.getInstance(e.nextElement());
                    }
                    catch (Exception ex) {
                        throw new AnnotatedException("Policy information cannot be decoded.", ex);
                    }
                    if (!ANY_POLICY.equals(pinfo.getPolicyIdentifier().getId())) continue;
                    try {
                        pq = CertPathValidatorUtilities.getQualifierSet(pinfo.getPolicyQualifiers());
                        break;
                    }
                    catch (CertPathValidatorException ex) {
                        throw new CertPathValidatorException("Policy qualifier info set could not be built.", ex);
                    }
                }
                boolean ci = false;
                if (cert.getCriticalExtensionOIDs() != null) {
                    ci = cert.getCriticalExtensionOIDs().contains(CERTIFICATE_POLICIES);
                }
                if (!ANY_POLICY.equals((p_node = (PKIXPolicyNode)node.getParent()).getValidPolicy())) break;
                PKIXPolicyNode c_node = new PKIXPolicyNode(new ArrayList(), i, (Set)m_idp.get(id_p), p_node, pq, id_p, ci);
                p_node.addChild(c_node);
                policyNodes[i].add(c_node);
                break;
            }
        }
    }

    protected static PKIXPolicyNode prepareNextCertB2(int i, List[] policyNodes, String id_p, PKIXPolicyNode validPolicyTree) {
        Iterator nodes_i = policyNodes[i].iterator();
        while (nodes_i.hasNext()) {
            PKIXPolicyNode node = (PKIXPolicyNode)nodes_i.next();
            if (!node.getValidPolicy().equals(id_p)) continue;
            PKIXPolicyNode p_node = (PKIXPolicyNode)node.getParent();
            p_node.removeChild(node);
            nodes_i.remove();
            for (int k = i - 1; k >= 0; --k) {
                PKIXPolicyNode node2;
                List nodes = policyNodes[k];
                for (int l = 0; l < nodes.size() && ((node2 = (PKIXPolicyNode)nodes.get(l)).hasChildren() || (validPolicyTree = CertPathValidatorUtilities.removePolicyNode(validPolicyTree, policyNodes, node2)) != null); ++l) {
                }
            }
        }
        return validPolicyTree;
    }

    protected static boolean isAnyPolicy(Set policySet) {
        return policySet == null || policySet.contains(ANY_POLICY) || policySet.isEmpty();
    }

    protected static Collection findCertificates(X509CertStoreSelector certSelect, List certStores) throws AnnotatedException {
        HashSet<? extends Certificate> certs = new HashSet<Certificate>();
        Iterator iter = certStores.iterator();
        CertificateFactory certFact = null;
        try {
            certFact = CertificateFactory.getInstance("X.509");
        }
        catch (CertificateException e) {
            throw new AnnotatedException(e.getMessage(), e);
        }
        while (iter.hasNext()) {
            Object certStore;
            Object obj = iter.next();
            if (obj instanceof Store) {
                certStore = (Store)obj;
                try {
                    for (Object cert : certStore.getMatches((Selector)certSelect)) {
                        if (cert instanceof Encodable) {
                            certs.add(certFact.generateCertificate(new ByteArrayInputStream(((Encodable)cert).getEncoded())));
                            continue;
                        }
                        if (cert instanceof Certificate) {
                            certs.add((Certificate)cert);
                            continue;
                        }
                        throw new AnnotatedException("Unknown object found in certificate store.");
                    }
                    continue;
                }
                catch (StoreException e) {
                    throw new AnnotatedException("Problem while picking certificates from X.509 store.", e);
                }
                catch (IOException e) {
                    throw new AnnotatedException("Problem while extracting certificates from X.509 store.", e);
                }
                catch (CertificateException e) {
                    throw new AnnotatedException("Problem while extracting certificates from X.509 store.", e);
                }
            }
            certStore = (CertStore)obj;
            try {
                certs.addAll(((CertStore)certStore).getCertificates(certSelect));
            }
            catch (CertStoreException e) {
                throw new AnnotatedException("Problem while picking certificates from certificate store.", e);
            }
        }
        return certs;
    }

    protected static Collection findCertificates(PKIXCertStoreSelector certSelect, List certStores) throws AnnotatedException {
        HashSet certs = new HashSet();
        for (Object obj : certStores) {
            Object certStore;
            if (obj instanceof Store) {
                certStore = (Store)obj;
                try {
                    certs.addAll(certStore.getMatches((Selector)certSelect));
                    continue;
                }
                catch (StoreException e) {
                    throw new AnnotatedException("Problem while picking certificates from X.509 store.", e);
                }
            }
            certStore = (CertStore)obj;
            try {
                certs.addAll(PKIXCertStoreSelector.getCertificates((PKIXCertStoreSelector)certSelect, (CertStore)certStore));
            }
            catch (CertStoreException e) {
                throw new AnnotatedException("Problem while picking certificates from certificate store.", e);
            }
        }
        return certs;
    }

    private static BigInteger getSerialNumber(Object cert) {
        return ((X509Certificate)cert).getSerialNumber();
    }

    protected static void getCertStatus(Date validDate, X509CRL crl, Object cert, CertStatus certStatus) throws AnnotatedException {
        int reasonCodeValue;
        boolean isIndirect;
        X509CRLEntry crl_entry = null;
        try {
            isIndirect = CertPathValidatorUtilities.isIndirectCRL(crl);
        }
        catch (CRLException exception) {
            throw new AnnotatedException("Failed check for indirect CRL.", exception);
        }
        if (isIndirect) {
            crl_entry = crl.getRevokedCertificate(CertPathValidatorUtilities.getSerialNumber(cert));
            if (crl_entry == null) {
                return;
            }
            X500Principal certIssuer = crl_entry.getCertificateIssuer();
            if (certIssuer == null) {
                certIssuer = CertPathValidatorUtilities.getIssuerPrincipal(crl);
            }
            if (!CertPathValidatorUtilities.getEncodedIssuerPrincipal(cert).equals(certIssuer)) {
                return;
            }
        } else {
            if (!CertPathValidatorUtilities.getEncodedIssuerPrincipal(cert).equals(CertPathValidatorUtilities.getIssuerPrincipal(crl))) {
                return;
            }
            crl_entry = crl.getRevokedCertificate(CertPathValidatorUtilities.getSerialNumber(cert));
            if (crl_entry == null) {
                return;
            }
        }
        ASN1Enumerated reasonCode = null;
        if (crl_entry.hasExtensions()) {
            try {
                reasonCode = ASN1Enumerated.getInstance((Object)CertPathValidatorUtilities.getExtensionValue(crl_entry, Extension.reasonCode.getId()));
            }
            catch (Exception e) {
                throw new AnnotatedException("Reason code CRL entry extension could not be decoded.", e);
            }
        }
        int n = reasonCodeValue = null == reasonCode ? 0 : reasonCode.getValue().intValue();
        if (validDate.getTime() >= crl_entry.getRevocationDate().getTime() || reasonCodeValue == 0 || reasonCodeValue == 1 || reasonCodeValue == 2 || reasonCodeValue == 10) {
            certStatus.setCertStatus(reasonCodeValue);
            certStatus.setRevocationDate(crl_entry.getRevocationDate());
        }
    }

    protected static PublicKey getNextWorkingKey(List certs, int index) throws CertPathValidatorException {
        Certificate cert = (Certificate)certs.get(index);
        PublicKey pubKey = cert.getPublicKey();
        if (!(pubKey instanceof DSAPublicKey)) {
            return pubKey;
        }
        DSAPublicKey dsaPubKey = (DSAPublicKey)pubKey;
        if (dsaPubKey.getParams() != null) {
            return dsaPubKey;
        }
        for (int i = index + 1; i < certs.size(); ++i) {
            X509Certificate parentCert = (X509Certificate)certs.get(i);
            pubKey = parentCert.getPublicKey();
            if (!(pubKey instanceof DSAPublicKey)) {
                throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
            }
            DSAPublicKey prevDSAPubKey = (DSAPublicKey)pubKey;
            if (prevDSAPubKey.getParams() == null) continue;
            DSAParams dsaParams = prevDSAPubKey.getParams();
            DSAPublicKeySpec dsaPubKeySpec = new DSAPublicKeySpec(dsaPubKey.getY(), dsaParams.getP(), dsaParams.getQ(), dsaParams.getG());
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("DSA");
                return keyFactory.generatePublic(dsaPubKeySpec);
            }
            catch (Exception exception) {
                throw new RuntimeException(exception.getMessage());
            }
        }
        throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
    }

    protected static void verifyX509Certificate(X509Certificate cert, PublicKey publicKey, String sigProvider) throws GeneralSecurityException {
        if (sigProvider == null) {
            cert.verify(publicKey);
        } else {
            cert.verify(publicKey, sigProvider);
        }
    }

    static boolean isIndirectCRL(X509CRL crl) throws CRLException {
        try {
            byte[] idp = crl.getExtensionValue(Extension.issuingDistributionPoint.getId());
            return idp != null && IssuingDistributionPoint.getInstance((Object)ASN1OctetString.getInstance((Object)idp).getOctets()).isIndirectCRL();
        }
        catch (Exception e) {
            throw new CRLException("Exception reading IssuingDistributionPoint: " + e);
        }
    }

    protected static Date getValidityDate(PKIXParameters paramsPKIX, Date currentDate) {
        Date validityDate = paramsPKIX.getDate();
        return null == validityDate ? currentDate : validityDate;
    }
}

