/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.PolicyQualifierInfo;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.internal.asn1.isismtt.ISISMTTObjectIdentifiers;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import org.bouncycastle.jcajce.PKIXCertStore;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathBuilderException;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.bouncycastle.jce.provider.CertStatus;
import org.bouncycastle.jce.provider.CrlCache;
import org.bouncycastle.jce.provider.PKIXCRLUtil;
import org.bouncycastle.jce.provider.PKIXPolicyNode;
import org.bouncycastle.jce.provider.PrincipalUtils;
import org.bouncycastle.jce.provider.RFC3280CertPathUtilities;
import org.bouncycastle.jce.provider.RecoverableCertPathValidatorException;
import org.bouncycastle.jce.provider.X509CRLObject;
import org.bouncycastle.util.Properties;
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

    static Collection findTargets(PKIXExtendedBuilderParameters paramsPKIX) throws CertPathBuilderException {
        PKIXExtendedParameters baseParams = paramsPKIX.getBaseParameters();
        PKIXCertStoreSelector certSelect = baseParams.getTargetConstraints();
        LinkedHashSet targets = new LinkedHashSet();
        try {
            CertPathValidatorUtilities.findCertificates(targets, certSelect, baseParams.getCertificateStores());
            CertPathValidatorUtilities.findCertificates(targets, certSelect, baseParams.getCertStores());
        }
        catch (AnnotatedException e) {
            throw new ExtCertPathBuilderException("Error finding target certificate.", e);
        }
        if (!targets.isEmpty()) {
            return targets;
        }
        Certificate target = certSelect.getCertificate();
        if (null == target) {
            throw new CertPathBuilderException("No certificate found matching targetConstraints.");
        }
        return Collections.singleton(target);
    }

    protected static TrustAnchor findTrustAnchor(X509Certificate cert, Set trustAnchors) throws AnnotatedException {
        return CertPathValidatorUtilities.findTrustAnchor(cert, trustAnchors, null);
    }

    protected static TrustAnchor findTrustAnchor(X509Certificate cert, Set trustAnchors, String sigProvider) throws AnnotatedException {
        TrustAnchor trust = null;
        PublicKey trustPublicKey = null;
        Exception invalidKeyEx = null;
        X509CertSelector certSelectX509 = new X509CertSelector();
        X500Principal certIssuerPrincipal = cert.getIssuerX500Principal();
        certSelectX509.setSubject(certIssuerPrincipal);
        X500Name certIssuerName = null;
        Iterator iter = trustAnchors.iterator();
        while (iter.hasNext() && trust == null) {
            block14: {
                trust = (TrustAnchor)iter.next();
                if (trust.getTrustedCert() != null) {
                    if (certSelectX509.match(trust.getTrustedCert())) {
                        trustPublicKey = trust.getTrustedCert().getPublicKey();
                    } else {
                        trust = null;
                    }
                } else if (trust.getCA() != null && trust.getCAName() != null && trust.getCAPublicKey() != null) {
                    if (certIssuerName == null) {
                        certIssuerName = X500Name.getInstance(certIssuerPrincipal.getEncoded());
                    }
                    try {
                        X500Name caName = X500Name.getInstance(trust.getCA().getEncoded());
                        if (certIssuerName.equals(caName)) {
                            trustPublicKey = trust.getCAPublicKey();
                            break block14;
                        }
                        trust = null;
                    }
                    catch (IllegalArgumentException ex) {
                        trust = null;
                    }
                } else {
                    trust = null;
                }
            }
            if (trustPublicKey == null) continue;
            try {
                CertPathValidatorUtilities.verifyX509Certificate(cert, trustPublicKey, sigProvider);
            }
            catch (Exception ex) {
                invalidKeyEx = ex;
                trust = null;
                trustPublicKey = null;
            }
        }
        if (trust == null && invalidKeyEx != null) {
            throw new AnnotatedException("TrustAnchor found but certificate validation failed.", invalidKeyEx);
        }
        return trust;
    }

    static boolean isIssuerTrustAnchor(X509Certificate cert, Set trustAnchors, String sigProvider) throws AnnotatedException {
        try {
            return CertPathValidatorUtilities.findTrustAnchor(cert, trustAnchors, sigProvider) != null;
        }
        catch (Exception e) {
            return false;
        }
    }

    static List<PKIXCertStore> getAdditionalStoresFromAltNames(byte[] issuerAlternativeName, Map<GeneralName, PKIXCertStore> altNameCertStoreMap) throws CertificateParsingException {
        if (issuerAlternativeName == null) {
            return Collections.EMPTY_LIST;
        }
        GeneralNames issuerAltName = GeneralNames.getInstance(ASN1OctetString.getInstance(issuerAlternativeName).getOctets());
        GeneralName[] names = issuerAltName.getNames();
        ArrayList<PKIXCertStore> stores = new ArrayList<PKIXCertStore>();
        for (int i = 0; i != names.length; ++i) {
            GeneralName altName = names[i];
            PKIXCertStore altStore = altNameCertStoreMap.get(altName);
            if (altStore == null) continue;
            stores.add(altStore);
        }
        return stores;
    }

    protected static Date getValidityDate(PKIXExtendedParameters paramsPKIX, Date currentDate) {
        Date validityDate = paramsPKIX.getValidityDate();
        return null == validityDate ? currentDate : validityDate;
    }

    protected static boolean isSelfIssued(X509Certificate cert) {
        return cert.getSubjectDN().equals(cert.getIssuerDN());
    }

    protected static ASN1Primitive getExtensionValue(X509Extension ext, String oid) throws AnnotatedException {
        byte[] bytes = ext.getExtensionValue(oid);
        return null == bytes ? null : CertPathValidatorUtilities.getObject(oid, bytes);
    }

    private static ASN1Primitive getObject(String oid, byte[] ext) throws AnnotatedException {
        try {
            ASN1OctetString octs = ASN1OctetString.getInstance(ext);
            return ASN1Primitive.fromByteArray(octs.getOctets());
        }
        catch (Exception e) {
            throw new AnnotatedException("exception processing extension " + oid, e);
        }
    }

    protected static AlgorithmIdentifier getAlgorithmIdentifier(PublicKey key) throws CertPathValidatorException {
        try {
            return SubjectPublicKeyInfo.getInstance(key.getEncoded()).getAlgorithm();
        }
        catch (Exception e) {
            throw new ExtCertPathValidatorException("Subject public key cannot be decoded.", e);
        }
    }

    protected static final Set getQualifierSet(ASN1Sequence qualifiers) throws CertPathValidatorException {
        HashSet<PolicyQualifierInfo> pq = new HashSet<PolicyQualifierInfo>();
        if (qualifiers == null) {
            return pq;
        }
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ASN1OutputStream aOut = ASN1OutputStream.create(bOut);
        Enumeration e = qualifiers.getObjects();
        while (e.hasMoreElements()) {
            try {
                aOut.writeObject((ASN1Encodable)e.nextElement());
                pq.add(new PolicyQualifierInfo(bOut.toByteArray()));
            }
            catch (IOException ex) {
                throw new ExtCertPathValidatorException("Policy qualifier info cannot be decoded.", ex);
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
            node.expectedPolicies = (Set)m_idp.get(id_p);
            break;
        }
        if (!idp_found) {
            for (PKIXPolicyNode node : policyNodes[i]) {
                PKIXPolicyNode p_node;
                if (!ANY_POLICY.equals(node.getValidPolicy())) continue;
                Set pq = null;
                ASN1Sequence policies = null;
                try {
                    policies = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, CERTIFICATE_POLICIES));
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
                        throw new ExtCertPathValidatorException("Policy qualifier info set could not be built.", ex);
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

    protected static void findCertificates(LinkedHashSet certs, PKIXCertStoreSelector certSelect, List certStores) throws AnnotatedException {
        for (Object obj : certStores) {
            Object certStore;
            if (obj instanceof Store) {
                certStore = (Store)obj;
                try {
                    certs.addAll(certStore.getMatches(certSelect));
                    continue;
                }
                catch (StoreException e) {
                    throw new AnnotatedException("Problem while picking certificates from X.509 store.", e);
                }
            }
            certStore = (CertStore)obj;
            try {
                certs.addAll(PKIXCertStoreSelector.getCertificates(certSelect, (CertStore)certStore));
            }
            catch (CertStoreException e) {
                throw new AnnotatedException("Problem while picking certificates from certificate store.", e);
            }
        }
    }

    static List<PKIXCRLStore> getAdditionalStoresFromCRLDistributionPoint(CRLDistPoint crldp, Map<GeneralName, PKIXCRLStore> namedCRLStoreMap, Date validDate, JcaJceHelper helper) throws AnnotatedException {
        DistributionPoint[] dps;
        if (null == crldp) {
            return Collections.EMPTY_LIST;
        }
        try {
            dps = crldp.getDistributionPoints();
        }
        catch (Exception e) {
            throw new AnnotatedException("Distribution points could not be read.", e);
        }
        ArrayList<PKIXCRLStore> stores = new ArrayList<PKIXCRLStore>();
        for (int i = 0; i < dps.length; ++i) {
            DistributionPointName dpn = dps[i].getDistributionPoint();
            if (dpn == null || dpn.getType() != 0) continue;
            GeneralName[] genNames = GeneralNames.getInstance(dpn.getName()).getNames();
            for (int j = 0; j < genNames.length; ++j) {
                PKIXCRLStore store = namedCRLStoreMap.get(genNames[j]);
                if (store == null) continue;
                stores.add(store);
            }
        }
        if (stores.isEmpty() && Properties.isOverrideSet("org.bouncycastle.x509.enableCRLDP")) {
            CertificateFactory certFact;
            try {
                certFact = helper.createCertificateFactory("X.509");
            }
            catch (Exception e) {
                throw new AnnotatedException("cannot create certificate factory: " + e.getMessage(), e);
            }
            block8: for (int i = 0; i < dps.length; ++i) {
                DistributionPointName dpn = dps[i].getDistributionPoint();
                if (dpn == null || dpn.getType() != 0) continue;
                GeneralName[] genNames = GeneralNames.getInstance(dpn.getName()).getNames();
                for (int j = 0; j < genNames.length; ++j) {
                    GeneralName name = genNames[j];
                    if (name.getTagNo() != 6) continue;
                    try {
                        URI distributionPoint = new URI(((ASN1String)((Object)name.getName())).getString());
                        PKIXCRLStore store = CrlCache.getCrl(certFact, validDate, distributionPoint);
                        if (store == null) continue block8;
                        stores.add(store);
                        continue block8;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
        }
        return stores;
    }

    protected static void getCRLIssuersFromDistributionPoint(DistributionPoint dp, Collection issuerPrincipals, X509CRLSelector selector) throws AnnotatedException {
        Iterator it;
        ArrayList<X500Name> issuers = new ArrayList<X500Name>();
        if (dp.getCRLIssuer() != null) {
            GeneralName[] genNames = dp.getCRLIssuer().getNames();
            for (int j = 0; j < genNames.length; ++j) {
                if (genNames[j].getTagNo() != 4) continue;
                try {
                    issuers.add(X500Name.getInstance(genNames[j].getName().toASN1Primitive().getEncoded()));
                    continue;
                }
                catch (IOException e) {
                    throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", e);
                }
            }
        } else {
            if (dp.getDistributionPoint() == null) {
                throw new AnnotatedException("CRL issuer is omitted from distribution point but no distributionPoint field present.");
            }
            it = issuerPrincipals.iterator();
            while (it.hasNext()) {
                issuers.add((X500Name)it.next());
            }
        }
        it = issuers.iterator();
        while (it.hasNext()) {
            try {
                selector.addIssuerName(((X500Name)it.next()).getEncoded());
            }
            catch (IOException ex) {
                throw new AnnotatedException("Cannot decode CRL issuer information.", ex);
            }
        }
    }

    private static BigInteger getSerialNumber(Object cert) {
        return ((X509Certificate)cert).getSerialNumber();
    }

    protected static void getCertStatus(Date validDate, X509CRL crl, Object cert, CertStatus certStatus) throws AnnotatedException {
        int reasonCodeValue;
        X509CRLEntry crl_entry;
        boolean isIndirect;
        try {
            isIndirect = X509CRLObject.isIndirectCRL(crl);
        }
        catch (CRLException exception) {
            throw new AnnotatedException("Failed check for indirect CRL.", exception);
        }
        if (isIndirect) {
            crl_entry = crl.getRevokedCertificate(CertPathValidatorUtilities.getSerialNumber(cert));
            if (crl_entry == null) {
                return;
            }
            X500Principal certificateIssuer = crl_entry.getCertificateIssuer();
            X500Name certIssuer = certificateIssuer == null ? PrincipalUtils.getIssuerPrincipal(crl) : PrincipalUtils.getX500Name(certificateIssuer);
            if (!PrincipalUtils.getEncodedIssuerPrincipal(cert).equals(certIssuer)) {
                return;
            }
        } else {
            if (!PrincipalUtils.getEncodedIssuerPrincipal(cert).equals(PrincipalUtils.getIssuerPrincipal(crl))) {
                return;
            }
            crl_entry = crl.getRevokedCertificate(CertPathValidatorUtilities.getSerialNumber(cert));
            if (crl_entry == null) {
                return;
            }
        }
        ASN1Enumerated reasonCode = null;
        if (crl_entry.hasExtensions()) {
            if (crl_entry.hasUnsupportedCriticalExtension()) {
                throw new AnnotatedException("CRL entry has unsupported critical extensions.");
            }
            try {
                reasonCode = ASN1Enumerated.getInstance(CertPathValidatorUtilities.getExtensionValue(crl_entry, Extension.reasonCode.getId()));
            }
            catch (Exception e) {
                throw new AnnotatedException("Reason code CRL entry extension could not be decoded.", e);
            }
        }
        int n = reasonCodeValue = null == reasonCode ? 0 : reasonCode.intValueExact();
        if (validDate.getTime() >= crl_entry.getRevocationDate().getTime() || reasonCodeValue == 0 || reasonCodeValue == 1 || reasonCodeValue == 2 || reasonCodeValue == 10) {
            certStatus.setCertStatus(reasonCodeValue);
            certStatus.setRevocationDate(crl_entry.getRevocationDate());
        }
    }

    protected static Set getDeltaCRLs(Date validityDate, X509CRL completeCRL, List<CertStore> certStores, List<PKIXCRLStore> pkixCrlStores, JcaJceHelper helper) throws AnnotatedException {
        byte[] idp;
        X509CRLSelector baseDeltaSelect = new X509CRLSelector();
        try {
            baseDeltaSelect.addIssuerName(PrincipalUtils.getIssuerPrincipal(completeCRL).getEncoded());
        }
        catch (IOException e) {
            throw new AnnotatedException("Cannot extract issuer from CRL.", e);
        }
        BigInteger completeCRLNumber = null;
        try {
            ASN1Primitive derObject = CertPathValidatorUtilities.getExtensionValue(completeCRL, CRL_NUMBER);
            if (derObject != null) {
                completeCRLNumber = ASN1Integer.getInstance(derObject).getPositiveValue();
            }
        }
        catch (Exception e) {
            throw new AnnotatedException("CRL number extension could not be extracted from CRL.", e);
        }
        try {
            idp = completeCRL.getExtensionValue(ISSUING_DISTRIBUTION_POINT);
        }
        catch (Exception e) {
            throw new AnnotatedException("Issuing distribution point extension value could not be read.", e);
        }
        baseDeltaSelect.setMinCRLNumber(completeCRLNumber == null ? null : completeCRLNumber.add(BigInteger.valueOf(1L)));
        PKIXCRLStoreSelector.Builder selBuilder = new PKIXCRLStoreSelector.Builder(baseDeltaSelect);
        selBuilder.setIssuingDistributionPoint(idp);
        selBuilder.setIssuingDistributionPointEnabled(true);
        selBuilder.setMaxBaseCRLNumber(completeCRLNumber);
        PKIXCRLStoreSelector<? extends CRL> deltaSelect = selBuilder.build();
        Set temp = PKIXCRLUtil.findCRLs(deltaSelect, validityDate, certStores, pkixCrlStores);
        if (temp.isEmpty() && Properties.isOverrideSet("org.bouncycastle.x509.enableCRLDP")) {
            CertificateFactory certFact;
            try {
                certFact = helper.createCertificateFactory("X.509");
            }
            catch (Exception e) {
                throw new AnnotatedException("cannot create certificate factory: " + e.getMessage(), e);
            }
            CRLDistPoint id = CRLDistPoint.getInstance(idp);
            DistributionPoint[] dps = id.getDistributionPoints();
            block10: for (int i = 0; i < dps.length; ++i) {
                DistributionPointName dpn = dps[i].getDistributionPoint();
                if (dpn == null || dpn.getType() != 0) continue;
                GeneralName[] genNames = GeneralNames.getInstance(dpn.getName()).getNames();
                for (int j = 0; j < genNames.length; ++j) {
                    GeneralName name = genNames[i];
                    if (name.getTagNo() != 6) continue;
                    try {
                        PKIXCRLStore store = CrlCache.getCrl(certFact, validityDate, new URI(((ASN1String)((Object)name.getName())).getString()));
                        if (store == null) continue block10;
                        temp = PKIXCRLUtil.findCRLs(deltaSelect, validityDate, Collections.EMPTY_LIST, Collections.singletonList(store));
                        continue block10;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
        }
        HashSet<X509CRL> result = new HashSet<X509CRL>();
        for (X509CRL crl : temp) {
            if (!CertPathValidatorUtilities.isDeltaCRL(crl)) continue;
            result.add(crl);
        }
        return result;
    }

    private static boolean isDeltaCRL(X509CRL crl) {
        Set<String> critical = crl.getCriticalExtensionOIDs();
        if (critical == null) {
            return false;
        }
        return critical.contains(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
    }

    protected static Set getCompleteCRLs(PKIXCertRevocationCheckerParameters params, DistributionPoint dp, Object cert, PKIXExtendedParameters paramsPKIX, Date validityDate) throws AnnotatedException, RecoverableCertPathValidatorException {
        X509CRLSelector baseCrlSelect = new X509CRLSelector();
        try {
            HashSet<X500Name> issuers = new HashSet<X500Name>();
            issuers.add(PrincipalUtils.getEncodedIssuerPrincipal(cert));
            CertPathValidatorUtilities.getCRLIssuersFromDistributionPoint(dp, issuers, baseCrlSelect);
        }
        catch (AnnotatedException e) {
            throw new AnnotatedException("Could not get issuer information from distribution point.", e);
        }
        if (cert instanceof X509Certificate) {
            baseCrlSelect.setCertificateChecking((X509Certificate)cert);
        }
        PKIXCRLStoreSelector<? extends CRL> crlSelect = new PKIXCRLStoreSelector.Builder(baseCrlSelect).setCompleteCRLEnabled(true).build();
        Set crls = PKIXCRLUtil.findCRLs(crlSelect, validityDate, paramsPKIX.getCertStores(), paramsPKIX.getCRLStores());
        CertPathValidatorUtilities.checkCRLsNotEmpty(params, crls, cert);
        return crls;
    }

    protected static Date getValidCertDateFromValidityModel(Date validityDate, int validityModel, CertPath certPath, int index) throws AnnotatedException {
        if (1 != validityModel || index <= 0) {
            return validityDate;
        }
        X509Certificate issuedCert = (X509Certificate)certPath.getCertificates().get(index - 1);
        if (index - 1 == 0) {
            ASN1GeneralizedTime dateOfCertgen = null;
            try {
                byte[] extBytes = ((X509Certificate)certPath.getCertificates().get(index - 1)).getExtensionValue(ISISMTTObjectIdentifiers.id_isismtt_at_dateOfCertGen.getId());
                if (extBytes != null) {
                    dateOfCertgen = ASN1GeneralizedTime.getInstance(ASN1Primitive.fromByteArray(extBytes));
                }
            }
            catch (IOException e) {
                throw new AnnotatedException("Date of cert gen extension could not be read.");
            }
            catch (IllegalArgumentException e) {
                throw new AnnotatedException("Date of cert gen extension could not be read.");
            }
            if (dateOfCertgen != null) {
                try {
                    return dateOfCertgen.getDate();
                }
                catch (ParseException e) {
                    throw new AnnotatedException("Date from date of cert gen extension could not be parsed.", e);
                }
            }
        }
        return issuedCert.getNotBefore();
    }

    protected static PublicKey getNextWorkingKey(List certs, int index, JcaJceHelper helper) throws CertPathValidatorException {
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
                KeyFactory keyFactory = helper.createKeyFactory("DSA");
                return keyFactory.generatePublic(dsaPubKeySpec);
            }
            catch (Exception exception) {
                throw new RuntimeException(exception.getMessage());
            }
        }
        throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
    }

    static Collection findIssuerCerts(X509Certificate cert, List<CertStore> certStores, List<PKIXCertStore> pkixCertStores) throws AnnotatedException {
        X509CertSelector selector = new X509CertSelector();
        try {
            selector.setSubject(PrincipalUtils.getIssuerPrincipal(cert).getEncoded());
        }
        catch (Exception e) {
            throw new AnnotatedException("Subject criteria for certificate selector to find issuer certificate could not be set.", e);
        }
        try {
            ASN1OctetString aki;
            byte[] authorityKeyIdentifier;
            byte[] akiExtensionValue = cert.getExtensionValue(AUTHORITY_KEY_IDENTIFIER);
            if (akiExtensionValue != null && (authorityKeyIdentifier = AuthorityKeyIdentifier.getInstance((aki = ASN1OctetString.getInstance(akiExtensionValue)).getOctets()).getKeyIdentifier()) != null) {
                selector.setSubjectKeyIdentifier(new DEROctetString(authorityKeyIdentifier).getEncoded());
            }
        }
        catch (Exception akiExtensionValue) {
            // empty catch block
        }
        PKIXCertStoreSelector<? extends Certificate> certSelect = new PKIXCertStoreSelector.Builder(selector).build();
        LinkedHashSet certs = new LinkedHashSet();
        try {
            CertPathValidatorUtilities.findCertificates(certs, certSelect, certStores);
            CertPathValidatorUtilities.findCertificates(certs, certSelect, pkixCertStores);
        }
        catch (AnnotatedException e) {
            throw new AnnotatedException("Issuer certificate cannot be searched.", e);
        }
        return certs;
    }

    protected static void verifyX509Certificate(X509Certificate cert, PublicKey publicKey, String sigProvider) throws GeneralSecurityException {
        if (sigProvider == null) {
            cert.verify(publicKey);
        } else {
            cert.verify(publicKey, sigProvider);
        }
    }

    static void checkCRLsNotEmpty(PKIXCertRevocationCheckerParameters params, Set crls, Object cert) throws RecoverableCertPathValidatorException {
        if (crls.isEmpty()) {
            X509Certificate xCert = (X509Certificate)cert;
            throw new RecoverableCertPathValidatorException("No CRLs found for issuer \"" + RFC4519Style.INSTANCE.toString(PrincipalUtils.getIssuerPrincipal(xCert)) + "\"", null, params.getCertPath(), params.getIndex());
        }
    }
}

