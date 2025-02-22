/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.NameConstraints;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.jcajce.PKIXCertRevocationChecker;
import org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.bouncycastle.jce.provider.CertPathValidatorUtilities;
import org.bouncycastle.jce.provider.CertStatus;
import org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi;
import org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi_8;
import org.bouncycastle.jce.provider.PKIXNameConstraintValidator;
import org.bouncycastle.jce.provider.PKIXNameConstraintValidatorException;
import org.bouncycastle.jce.provider.PKIXPolicyNode;
import org.bouncycastle.jce.provider.PrincipalUtils;
import org.bouncycastle.jce.provider.ReasonsMask;
import org.bouncycastle.jce.provider.RecoverableCertPathValidatorException;
import org.bouncycastle.util.Arrays;

class RFC3280CertPathUtilities {
    private static final Class revChkClass = ClassUtil.loadClass(RFC3280CertPathUtilities.class, "java.security.cert.PKIXRevocationChecker");
    public static final String CERTIFICATE_POLICIES = Extension.certificatePolicies.getId();
    public static final String POLICY_MAPPINGS = Extension.policyMappings.getId();
    public static final String INHIBIT_ANY_POLICY = Extension.inhibitAnyPolicy.getId();
    public static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();
    public static final String FRESHEST_CRL = Extension.freshestCRL.getId();
    public static final String DELTA_CRL_INDICATOR = Extension.deltaCRLIndicator.getId();
    public static final String POLICY_CONSTRAINTS = Extension.policyConstraints.getId();
    public static final String BASIC_CONSTRAINTS = Extension.basicConstraints.getId();
    public static final String CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
    public static final String SUBJECT_ALTERNATIVE_NAME = Extension.subjectAlternativeName.getId();
    public static final String NAME_CONSTRAINTS = Extension.nameConstraints.getId();
    public static final String AUTHORITY_KEY_IDENTIFIER = Extension.authorityKeyIdentifier.getId();
    public static final String KEY_USAGE = Extension.keyUsage.getId();
    public static final String CRL_NUMBER = Extension.cRLNumber.getId();
    public static final String ANY_POLICY = "2.5.29.32.0";
    protected static final int KEY_CERT_SIGN = 5;
    protected static final int CRL_SIGN = 6;
    protected static final String[] crlReasons = new String[]{"unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", "aACompromise"};

    RFC3280CertPathUtilities() {
    }

    protected static void processCRLB2(DistributionPoint dp, Object cert, X509CRL crl) throws AnnotatedException {
        IssuingDistributionPoint idp = null;
        try {
            idp = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(crl, ISSUING_DISTRIBUTION_POINT));
        }
        catch (Exception e) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e);
        }
        if (idp != null) {
            if (idp.getDistributionPoint() != null) {
                DistributionPointName dpName = IssuingDistributionPoint.getInstance(idp).getDistributionPoint();
                ArrayList<GeneralName> names = new ArrayList<GeneralName>();
                if (dpName.getType() == 0) {
                    GeneralName[] genNames = GeneralNames.getInstance(dpName.getName()).getNames();
                    for (int j = 0; j < genNames.length; ++j) {
                        names.add(genNames[j]);
                    }
                }
                if (dpName.getType() == 1) {
                    ASN1EncodableVector vec = new ASN1EncodableVector();
                    try {
                        Enumeration e = ASN1Sequence.getInstance(PrincipalUtils.getIssuerPrincipal(crl)).getObjects();
                        while (e.hasMoreElements()) {
                            vec.add((ASN1Encodable)e.nextElement());
                        }
                    }
                    catch (Exception e) {
                        throw new AnnotatedException("Could not read CRL issuer.", e);
                    }
                    vec.add(dpName.getName());
                    names.add(new GeneralName(X500Name.getInstance(new DERSequence(vec))));
                }
                boolean matches = false;
                if (dp.getDistributionPoint() != null) {
                    int j;
                    dpName = dp.getDistributionPoint();
                    GeneralName[] genNames = null;
                    if (dpName.getType() == 0) {
                        genNames = GeneralNames.getInstance(dpName.getName()).getNames();
                    }
                    if (dpName.getType() == 1) {
                        if (dp.getCRLIssuer() != null) {
                            genNames = dp.getCRLIssuer().getNames();
                        } else {
                            genNames = new GeneralName[1];
                            try {
                                genNames[0] = new GeneralName(PrincipalUtils.getEncodedIssuerPrincipal(cert));
                            }
                            catch (Exception e) {
                                throw new AnnotatedException("Could not read certificate issuer.", e);
                            }
                        }
                        for (j = 0; j < genNames.length; ++j) {
                            Enumeration e = ASN1Sequence.getInstance(genNames[j].getName().toASN1Primitive()).getObjects();
                            ASN1EncodableVector vec = new ASN1EncodableVector();
                            while (e.hasMoreElements()) {
                                vec.add((ASN1Encodable)e.nextElement());
                            }
                            vec.add(dpName.getName());
                            genNames[j] = new GeneralName(X500Name.getInstance(new DERSequence(vec)));
                        }
                    }
                    if (genNames != null) {
                        for (j = 0; j < genNames.length; ++j) {
                            if (!names.contains(genNames[j])) continue;
                            matches = true;
                            break;
                        }
                    }
                    if (!matches) {
                        throw new AnnotatedException("No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point.");
                    }
                } else {
                    if (dp.getCRLIssuer() == null) {
                        throw new AnnotatedException("Either the cRLIssuer or the distributionPoint field must be contained in DistributionPoint.");
                    }
                    GeneralName[] genNames = dp.getCRLIssuer().getNames();
                    for (int j = 0; j < genNames.length; ++j) {
                        if (!names.contains(genNames[j])) continue;
                        matches = true;
                        break;
                    }
                    if (!matches) {
                        throw new AnnotatedException("No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point.");
                    }
                }
            }
            BasicConstraints bc = null;
            try {
                bc = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Extension)cert, BASIC_CONSTRAINTS));
            }
            catch (Exception e) {
                throw new AnnotatedException("Basic constraints extension could not be decoded.", e);
            }
            if (cert instanceof X509Certificate) {
                if (idp.onlyContainsUserCerts() && bc != null && bc.isCA()) {
                    throw new AnnotatedException("CA Cert CRL only contains user certificates.");
                }
                if (idp.onlyContainsCACerts() && (bc == null || !bc.isCA())) {
                    throw new AnnotatedException("End CRL only contains CA certificates.");
                }
            }
            if (idp.onlyContainsAttributeCerts()) {
                throw new AnnotatedException("onlyContainsAttributeCerts boolean is asserted.");
            }
        }
    }

    protected static void processCRLB1(DistributionPoint dp, Object cert, X509CRL crl) throws AnnotatedException {
        byte[] issuerBytes;
        ASN1Primitive idp = CertPathValidatorUtilities.getExtensionValue(crl, ISSUING_DISTRIBUTION_POINT);
        boolean isIndirect = false;
        if (idp != null && IssuingDistributionPoint.getInstance(idp).isIndirectCRL()) {
            isIndirect = true;
        }
        try {
            issuerBytes = PrincipalUtils.getIssuerPrincipal(crl).getEncoded();
        }
        catch (IOException e) {
            throw new AnnotatedException("Exception encoding CRL issuer: " + e.getMessage(), e);
        }
        boolean matchIssuer = false;
        if (dp.getCRLIssuer() != null) {
            GeneralName[] genNames = dp.getCRLIssuer().getNames();
            for (int j = 0; j < genNames.length; ++j) {
                if (genNames[j].getTagNo() != 4) continue;
                try {
                    if (!Arrays.areEqual(genNames[j].getName().toASN1Primitive().getEncoded(), issuerBytes)) continue;
                    matchIssuer = true;
                    continue;
                }
                catch (IOException e) {
                    throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", e);
                }
            }
            if (matchIssuer && !isIndirect) {
                throw new AnnotatedException("Distribution point contains cRLIssuer field but CRL is not indirect.");
            }
            if (!matchIssuer) {
                throw new AnnotatedException("CRL issuer of CRL does not match CRL issuer of distribution point.");
            }
        } else if (PrincipalUtils.getIssuerPrincipal(crl).equals(PrincipalUtils.getEncodedIssuerPrincipal(cert))) {
            matchIssuer = true;
        }
        if (!matchIssuer) {
            throw new AnnotatedException("Cannot find matching CRL issuer for certificate.");
        }
    }

    protected static ReasonsMask processCRLD(X509CRL crl, DistributionPoint dp) throws AnnotatedException {
        IssuingDistributionPoint idp = null;
        try {
            idp = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(crl, ISSUING_DISTRIBUTION_POINT));
        }
        catch (Exception e) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e);
        }
        if (idp != null && idp.getOnlySomeReasons() != null && dp.getReasons() != null) {
            return new ReasonsMask(dp.getReasons()).intersect(new ReasonsMask(idp.getOnlySomeReasons()));
        }
        if ((idp == null || idp.getOnlySomeReasons() == null) && dp.getReasons() == null) {
            return ReasonsMask.allReasons;
        }
        return (dp.getReasons() == null ? ReasonsMask.allReasons : new ReasonsMask(dp.getReasons())).intersect(idp == null ? ReasonsMask.allReasons : new ReasonsMask(idp.getOnlySomeReasons()));
    }

    protected static Set processCRLF(X509CRL crl, Object cert, X509Certificate defaultCRLSignCert, PublicKey defaultCRLSignKey, PKIXExtendedParameters paramsPKIX, List certPathCerts, JcaJceHelper helper) throws AnnotatedException {
        X509CertSelector certSelector = new X509CertSelector();
        try {
            byte[] issuerPrincipal = PrincipalUtils.getIssuerPrincipal(crl).getEncoded();
            certSelector.setSubject(issuerPrincipal);
        }
        catch (IOException e) {
            throw new AnnotatedException("Subject criteria for certificate selector to find issuer certificate for CRL could not be set.", e);
        }
        PKIXCertStoreSelector<? extends Certificate> selector = new PKIXCertStoreSelector.Builder(certSelector).build();
        LinkedHashSet<X509Certificate> coll = new LinkedHashSet<X509Certificate>();
        try {
            CertPathValidatorUtilities.findCertificates(coll, selector, paramsPKIX.getCertificateStores());
            CertPathValidatorUtilities.findCertificates(coll, selector, paramsPKIX.getCertStores());
        }
        catch (AnnotatedException e) {
            throw new AnnotatedException("Issuer certificate for CRL cannot be searched.", e);
        }
        coll.add(defaultCRLSignCert);
        Iterator cert_it = coll.iterator();
        ArrayList<X509Certificate> validCerts = new ArrayList<X509Certificate>();
        ArrayList<PublicKey> validKeys = new ArrayList<PublicKey>();
        while (cert_it.hasNext()) {
            X509Certificate signingCert = (X509Certificate)cert_it.next();
            if (signingCert.equals(defaultCRLSignCert)) {
                validCerts.add(signingCert);
                validKeys.add(defaultCRLSignKey);
                continue;
            }
            try {
                CertPathBuilderSpi builder = revChkClass != null ? new PKIXCertPathBuilderSpi_8(true) : new PKIXCertPathBuilderSpi(true);
                X509CertSelector tmpCertSelector = new X509CertSelector();
                tmpCertSelector.setCertificate(signingCert);
                PKIXExtendedParameters.Builder paramsBuilder = new PKIXExtendedParameters.Builder(paramsPKIX).setTargetConstraints(new PKIXCertStoreSelector.Builder(tmpCertSelector).build());
                if (certPathCerts.contains(signingCert)) {
                    paramsBuilder.setRevocationEnabled(false);
                } else {
                    paramsBuilder.setRevocationEnabled(true);
                }
                PKIXExtendedBuilderParameters extParams = new PKIXExtendedBuilderParameters.Builder(paramsBuilder.build()).build();
                List<? extends Certificate> certs = builder.engineBuild(extParams).getCertPath().getCertificates();
                validCerts.add(signingCert);
                validKeys.add(CertPathValidatorUtilities.getNextWorkingKey(certs, 0, helper));
            }
            catch (CertPathBuilderException e) {
                throw new AnnotatedException("CertPath for CRL signer failed to validate.", e);
            }
            catch (CertPathValidatorException e) {
                throw new AnnotatedException("Public key of issuer certificate of CRL could not be retrieved.", e);
            }
            catch (Exception e) {
                throw new AnnotatedException(e.getMessage());
            }
        }
        HashSet checkKeys = new HashSet();
        AnnotatedException lastException = null;
        for (int i = 0; i < validCerts.size(); ++i) {
            X509Certificate signCert = (X509Certificate)validCerts.get(i);
            boolean[] keyUsage = signCert.getKeyUsage();
            if (!(keyUsage == null || keyUsage.length > 6 && keyUsage[6])) {
                lastException = new AnnotatedException("Issuer certificate key usage extension does not permit CRL signing.");
                continue;
            }
            checkKeys.add(validKeys.get(i));
        }
        if (checkKeys.isEmpty() && lastException == null) {
            throw new AnnotatedException("Cannot find a valid issuer certificate.");
        }
        if (checkKeys.isEmpty() && lastException != null) {
            throw lastException;
        }
        return checkKeys;
    }

    protected static PublicKey processCRLG(X509CRL crl, Set keys) throws AnnotatedException {
        Exception lastException = null;
        for (PublicKey key : keys) {
            try {
                crl.verify(key);
                return key;
            }
            catch (Exception e) {
                lastException = e;
            }
        }
        throw new AnnotatedException("Cannot verify CRL.", lastException);
    }

    protected static X509CRL processCRLH(Set deltacrls, PublicKey key) throws AnnotatedException {
        Exception lastException = null;
        for (X509CRL crl : deltacrls) {
            try {
                crl.verify(key);
                return crl;
            }
            catch (Exception e) {
                lastException = e;
            }
        }
        if (lastException != null) {
            throw new AnnotatedException("Cannot verify delta CRL.", lastException);
        }
        return null;
    }

    protected static void processCRLC(X509CRL deltaCRL, X509CRL completeCRL, PKIXExtendedParameters pkixParams) throws AnnotatedException {
        if (deltaCRL == null) {
            return;
        }
        if (deltaCRL.hasUnsupportedCriticalExtension()) {
            throw new AnnotatedException("delta CRL has unsupported critical extensions");
        }
        IssuingDistributionPoint completeidp = null;
        try {
            completeidp = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(completeCRL, ISSUING_DISTRIBUTION_POINT));
        }
        catch (Exception e) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e);
        }
        if (pkixParams.isUseDeltasEnabled()) {
            if (!PrincipalUtils.getIssuerPrincipal(deltaCRL).equals(PrincipalUtils.getIssuerPrincipal(completeCRL))) {
                throw new AnnotatedException("Complete CRL issuer does not match delta CRL issuer.");
            }
            IssuingDistributionPoint deltaidp = null;
            try {
                deltaidp = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(deltaCRL, ISSUING_DISTRIBUTION_POINT));
            }
            catch (Exception e) {
                throw new AnnotatedException("Issuing distribution point extension from delta CRL could not be decoded.", e);
            }
            boolean match = false;
            if (completeidp == null) {
                if (deltaidp == null) {
                    match = true;
                }
            } else if (completeidp.equals(deltaidp)) {
                match = true;
            }
            if (!match) {
                throw new AnnotatedException("Issuing distribution point extension from delta CRL and complete CRL does not match.");
            }
            ASN1Primitive completeKeyIdentifier = null;
            try {
                completeKeyIdentifier = CertPathValidatorUtilities.getExtensionValue(completeCRL, AUTHORITY_KEY_IDENTIFIER);
            }
            catch (AnnotatedException e) {
                throw new AnnotatedException("Authority key identifier extension could not be extracted from complete CRL.", e);
            }
            ASN1Primitive deltaKeyIdentifier = null;
            try {
                deltaKeyIdentifier = CertPathValidatorUtilities.getExtensionValue(deltaCRL, AUTHORITY_KEY_IDENTIFIER);
            }
            catch (AnnotatedException e) {
                throw new AnnotatedException("Authority key identifier extension could not be extracted from delta CRL.", e);
            }
            if (completeKeyIdentifier == null) {
                throw new AnnotatedException("CRL authority key identifier is null.");
            }
            if (deltaKeyIdentifier == null) {
                throw new AnnotatedException("Delta CRL authority key identifier is null.");
            }
            if (!completeKeyIdentifier.equals(deltaKeyIdentifier)) {
                throw new AnnotatedException("Delta CRL authority key identifier does not match complete CRL authority key identifier.");
            }
        }
    }

    protected static void processCRLI(Date validDate, X509CRL deltacrl, Object cert, CertStatus certStatus, PKIXExtendedParameters pkixParams) throws AnnotatedException {
        if (pkixParams.isUseDeltasEnabled() && deltacrl != null) {
            CertPathValidatorUtilities.getCertStatus(validDate, deltacrl, cert, certStatus);
        }
    }

    protected static void processCRLJ(Date validDate, X509CRL completecrl, Object cert, CertStatus certStatus) throws AnnotatedException {
        if (certStatus.getCertStatus() == 11) {
            CertPathValidatorUtilities.getCertStatus(validDate, completecrl, cert, certStatus);
        }
    }

    protected static PKIXPolicyNode prepareCertB(CertPath certPath, int index, List[] policyNodes, PKIXPolicyNode validPolicyTree, int policyMapping) throws CertPathValidatorException {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        int n = certs.size();
        int i = n - index;
        ASN1Sequence pm = null;
        try {
            pm = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, POLICY_MAPPINGS));
        }
        catch (AnnotatedException ex) {
            throw new ExtCertPathValidatorException("Policy mappings extension could not be decoded.", (Throwable)ex, certPath, index);
        }
        PKIXPolicyNode _validPolicyTree = validPolicyTree;
        if (pm != null) {
            ASN1Sequence mappings = pm;
            HashMap m_idp = new HashMap();
            HashSet<String> s_idp = new HashSet<String>();
            for (int j = 0; j < mappings.size(); ++j) {
                Set<String> tmp;
                ASN1Sequence mapping = (ASN1Sequence)mappings.getObjectAt(j);
                String id_p = ((ASN1ObjectIdentifier)mapping.getObjectAt(0)).getId();
                String sd_p = ((ASN1ObjectIdentifier)mapping.getObjectAt(1)).getId();
                if (!m_idp.containsKey(id_p)) {
                    tmp = new HashSet<String>();
                    tmp.add(sd_p);
                    m_idp.put(id_p, tmp);
                    s_idp.add(id_p);
                    continue;
                }
                tmp = (Set)m_idp.get(id_p);
                tmp.add(sd_p);
            }
            block9: for (String id_p : s_idp) {
                if (policyMapping > 0) {
                    boolean idp_found = false;
                    for (PKIXPolicyNode node : policyNodes[i]) {
                        if (!node.getValidPolicy().equals(id_p)) continue;
                        idp_found = true;
                        node.expectedPolicies = (Set)m_idp.get(id_p);
                        break;
                    }
                    if (idp_found) continue;
                    for (PKIXPolicyNode node : policyNodes[i]) {
                        PKIXPolicyNode p_node;
                        if (!ANY_POLICY.equals(node.getValidPolicy())) continue;
                        Set pq = null;
                        ASN1Sequence policies = null;
                        try {
                            policies = (ASN1Sequence)CertPathValidatorUtilities.getExtensionValue(cert, CERTIFICATE_POLICIES);
                        }
                        catch (AnnotatedException e) {
                            throw new ExtCertPathValidatorException("Certificate policies extension could not be decoded.", (Throwable)e, certPath, index);
                        }
                        Enumeration e = policies.getObjects();
                        while (e.hasMoreElements()) {
                            PolicyInformation pinfo = null;
                            try {
                                pinfo = PolicyInformation.getInstance(e.nextElement());
                            }
                            catch (Exception ex) {
                                throw new CertPathValidatorException("Policy information could not be decoded.", (Throwable)ex, certPath, index);
                            }
                            if (!ANY_POLICY.equals(pinfo.getPolicyIdentifier().getId())) continue;
                            try {
                                pq = CertPathValidatorUtilities.getQualifierSet(pinfo.getPolicyQualifiers());
                                break;
                            }
                            catch (CertPathValidatorException ex) {
                                throw new ExtCertPathValidatorException("Policy qualifier info set could not be decoded.", (Throwable)ex, certPath, index);
                            }
                        }
                        boolean ci = false;
                        if (cert.getCriticalExtensionOIDs() != null) {
                            ci = cert.getCriticalExtensionOIDs().contains(CERTIFICATE_POLICIES);
                        }
                        if (!ANY_POLICY.equals((p_node = (PKIXPolicyNode)node.getParent()).getValidPolicy())) continue block9;
                        PKIXPolicyNode c_node = new PKIXPolicyNode(new ArrayList(), i, (Set)m_idp.get(id_p), p_node, pq, id_p, ci);
                        p_node.addChild(c_node);
                        policyNodes[i].add(c_node);
                        continue block9;
                    }
                    continue;
                }
                if (policyMapping > 0) continue;
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
                        for (int l = 0; l < nodes.size() && ((node2 = (PKIXPolicyNode)nodes.get(l)).hasChildren() || (_validPolicyTree = CertPathValidatorUtilities.removePolicyNode(_validPolicyTree, policyNodes, node2)) != null); ++l) {
                        }
                    }
                }
            }
        }
        return _validPolicyTree;
    }

    protected static void prepareNextCertA(CertPath certPath, int index) throws CertPathValidatorException {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        ASN1Sequence pm = null;
        try {
            pm = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, POLICY_MAPPINGS));
        }
        catch (AnnotatedException ex) {
            throw new ExtCertPathValidatorException("Policy mappings extension could not be decoded.", (Throwable)ex, certPath, index);
        }
        if (pm != null) {
            ASN1Sequence mappings = pm;
            for (int j = 0; j < mappings.size(); ++j) {
                ASN1ObjectIdentifier issuerDomainPolicy = null;
                ASN1ObjectIdentifier subjectDomainPolicy = null;
                try {
                    ASN1Sequence mapping = ASN1Sequence.getInstance(mappings.getObjectAt(j));
                    issuerDomainPolicy = ASN1ObjectIdentifier.getInstance(mapping.getObjectAt(0));
                    subjectDomainPolicy = ASN1ObjectIdentifier.getInstance(mapping.getObjectAt(1));
                }
                catch (Exception e) {
                    throw new ExtCertPathValidatorException("Policy mappings extension contents could not be decoded.", (Throwable)e, certPath, index);
                }
                if (ANY_POLICY.equals(issuerDomainPolicy.getId())) {
                    throw new CertPathValidatorException("IssuerDomainPolicy is anyPolicy", null, certPath, index);
                }
                if (!ANY_POLICY.equals(subjectDomainPolicy.getId())) continue;
                throw new CertPathValidatorException("SubjectDomainPolicy is anyPolicy", null, certPath, index);
            }
        }
    }

    protected static void processCertF(CertPath certPath, int index, PKIXPolicyNode validPolicyTree, int explicitPolicy) throws CertPathValidatorException {
        if (explicitPolicy <= 0 && validPolicyTree == null) {
            throw new ExtCertPathValidatorException("No valid policy tree found when one expected.", null, certPath, index);
        }
    }

    protected static PKIXPolicyNode processCertE(CertPath certPath, int index, PKIXPolicyNode validPolicyTree) throws CertPathValidatorException {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        ASN1Sequence certPolicies = null;
        try {
            certPolicies = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, CERTIFICATE_POLICIES));
        }
        catch (AnnotatedException e) {
            throw new ExtCertPathValidatorException("Could not read certificate policies extension from certificate.", (Throwable)e, certPath, index);
        }
        if (certPolicies == null) {
            validPolicyTree = null;
        }
        return validPolicyTree;
    }

    protected static void processCertBC(CertPath certPath, int index, PKIXNameConstraintValidator nameConstraintValidator, boolean isForCRLCheck) throws CertPathValidatorException {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        int n = certs.size();
        int i = n - index;
        if (!CertPathValidatorUtilities.isSelfIssued(cert) || i >= n && !isForCRLCheck) {
            ASN1Sequence dns;
            X500Name principal = PrincipalUtils.getSubjectPrincipal(cert);
            try {
                dns = ASN1Sequence.getInstance(principal);
            }
            catch (Exception e) {
                throw new CertPathValidatorException("Exception extracting subject name when checking subtrees.", (Throwable)e, certPath, index);
            }
            try {
                nameConstraintValidator.checkPermittedDN(dns);
                nameConstraintValidator.checkExcludedDN(dns);
            }
            catch (PKIXNameConstraintValidatorException e) {
                throw new CertPathValidatorException("Subtree check for certificate subject failed.", (Throwable)e, certPath, index);
            }
            GeneralNames altName = null;
            try {
                altName = GeneralNames.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, SUBJECT_ALTERNATIVE_NAME));
            }
            catch (Exception e) {
                throw new CertPathValidatorException("Subject alternative name extension could not be decoded.", (Throwable)e, certPath, index);
            }
            RDN[] emails = X500Name.getInstance(dns).getRDNs(BCStyle.EmailAddress);
            for (int eI = 0; eI != emails.length; ++eI) {
                String email = ((ASN1String)((Object)emails[eI].getFirst().getValue())).getString();
                GeneralName emailAsGeneralName = new GeneralName(1, email);
                try {
                    nameConstraintValidator.checkPermitted(emailAsGeneralName);
                    nameConstraintValidator.checkExcluded(emailAsGeneralName);
                    continue;
                }
                catch (PKIXNameConstraintValidatorException ex) {
                    throw new CertPathValidatorException("Subtree check for certificate subject alternative email failed.", (Throwable)ex, certPath, index);
                }
            }
            if (altName != null) {
                GeneralName[] genNames = null;
                try {
                    genNames = altName.getNames();
                }
                catch (Exception e) {
                    throw new CertPathValidatorException("Subject alternative name contents could not be decoded.", (Throwable)e, certPath, index);
                }
                for (int j = 0; j < genNames.length; ++j) {
                    try {
                        nameConstraintValidator.checkPermitted(genNames[j]);
                        nameConstraintValidator.checkExcluded(genNames[j]);
                        continue;
                    }
                    catch (PKIXNameConstraintValidatorException e) {
                        throw new CertPathValidatorException("Subtree check for certificate subject alternative name failed.", (Throwable)e, certPath, index);
                    }
                }
            }
        }
    }

    protected static PKIXPolicyNode processCertD(CertPath certPath, int index, Set acceptablePolicies, PKIXPolicyNode validPolicyTree, List[] policyNodes, int inhibitAnyPolicy, boolean isForCRLCheck) throws CertPathValidatorException {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        int n = certs.size();
        int i = n - index;
        ASN1Sequence certPolicies = null;
        try {
            certPolicies = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, CERTIFICATE_POLICIES));
        }
        catch (AnnotatedException e) {
            throw new ExtCertPathValidatorException("Could not read certificate policies extension from certificate.", (Throwable)e, certPath, index);
        }
        if (certPolicies != null && validPolicyTree != null) {
            int k;
            PolicyInformation pInfo;
            Enumeration e = certPolicies.getObjects();
            HashSet<String> pols = new HashSet<String>();
            while (e.hasMoreElements()) {
                pInfo = PolicyInformation.getInstance(e.nextElement());
                ASN1ObjectIdentifier pOid = pInfo.getPolicyIdentifier();
                pols.add(pOid.getId());
                if (ANY_POLICY.equals(pOid.getId())) continue;
                Set pq = null;
                try {
                    pq = CertPathValidatorUtilities.getQualifierSet(pInfo.getPolicyQualifiers());
                }
                catch (CertPathValidatorException ex) {
                    throw new ExtCertPathValidatorException("Policy qualifier info set could not be build.", (Throwable)ex, certPath, index);
                }
                boolean match = CertPathValidatorUtilities.processCertD1i(i, policyNodes, pOid, pq);
                if (match) continue;
                CertPathValidatorUtilities.processCertD1ii(i, policyNodes, pOid, pq);
            }
            if (acceptablePolicies.isEmpty() || acceptablePolicies.contains(ANY_POLICY)) {
                acceptablePolicies.clear();
                acceptablePolicies.addAll(pols);
            } else {
                Iterator it = acceptablePolicies.iterator();
                HashSet t1 = new HashSet();
                while (it.hasNext()) {
                    Object o = it.next();
                    if (!pols.contains(o)) continue;
                    t1.add(o);
                }
                acceptablePolicies.clear();
                acceptablePolicies.addAll(t1);
            }
            if (inhibitAnyPolicy > 0 || (i < n || isForCRLCheck) && CertPathValidatorUtilities.isSelfIssued(cert)) {
                e = certPolicies.getObjects();
                while (e.hasMoreElements()) {
                    pInfo = PolicyInformation.getInstance(e.nextElement());
                    if (!ANY_POLICY.equals(pInfo.getPolicyIdentifier().getId())) continue;
                    Set _apq = CertPathValidatorUtilities.getQualifierSet(pInfo.getPolicyQualifiers());
                    List _nodes = policyNodes[i - 1];
                    for (k = 0; k < _nodes.size(); ++k) {
                        PKIXPolicyNode _node = (PKIXPolicyNode)_nodes.get(k);
                        for (Object _tmp : _node.getExpectedPolicies()) {
                            String _policy;
                            if (_tmp instanceof String) {
                                _policy = (String)_tmp;
                            } else {
                                if (!(_tmp instanceof ASN1ObjectIdentifier)) continue;
                                _policy = ((ASN1ObjectIdentifier)_tmp).getId();
                            }
                            boolean _found = false;
                            Iterator _childrenIter = _node.getChildren();
                            while (_childrenIter.hasNext()) {
                                PKIXPolicyNode _child = (PKIXPolicyNode)_childrenIter.next();
                                if (!_policy.equals(_child.getValidPolicy())) continue;
                                _found = true;
                            }
                            if (_found) continue;
                            HashSet<String> _newChildExpectedPolicies = new HashSet<String>();
                            _newChildExpectedPolicies.add(_policy);
                            PKIXPolicyNode _newChild = new PKIXPolicyNode(new ArrayList(), i, _newChildExpectedPolicies, _node, _apq, _policy, false);
                            _node.addChild(_newChild);
                            policyNodes[i].add(_newChild);
                        }
                    }
                }
            }
            PKIXPolicyNode _validPolicyTree = validPolicyTree;
            for (int j = i - 1; j >= 0; --j) {
                PKIXPolicyNode node;
                List nodes = policyNodes[j];
                for (k = 0; k < nodes.size() && ((node = (PKIXPolicyNode)nodes.get(k)).hasChildren() || (_validPolicyTree = CertPathValidatorUtilities.removePolicyNode(_validPolicyTree, policyNodes, node)) != null); ++k) {
                }
            }
            Set<String> criticalExtensionOids = cert.getCriticalExtensionOIDs();
            if (criticalExtensionOids != null) {
                boolean critical = criticalExtensionOids.contains(CERTIFICATE_POLICIES);
                List nodes = policyNodes[i];
                for (int j = 0; j < nodes.size(); ++j) {
                    PKIXPolicyNode node = (PKIXPolicyNode)nodes.get(j);
                    node.setCritical(critical);
                }
            }
            return _validPolicyTree;
        }
        return null;
    }

    protected static void processCertA(CertPath certPath, PKIXExtendedParameters paramsPKIX, Date validityDate, PKIXCertRevocationChecker revocationChecker, int index, PublicKey workingPublicKey, boolean verificationAlreadyPerformed, X500Name workingIssuerName, X509Certificate sign) throws CertPathValidatorException {
        X500Name issuer;
        Date validCertDate;
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        if (!verificationAlreadyPerformed) {
            try {
                CertPathValidatorUtilities.verifyX509Certificate(cert, workingPublicKey, paramsPKIX.getSigProvider());
            }
            catch (GeneralSecurityException e) {
                throw new ExtCertPathValidatorException("Could not validate certificate signature.", (Throwable)e, certPath, index);
            }
        }
        try {
            validCertDate = CertPathValidatorUtilities.getValidCertDateFromValidityModel(validityDate, paramsPKIX.getValidityModel(), certPath, index);
        }
        catch (AnnotatedException e) {
            throw new ExtCertPathValidatorException("Could not validate time of certificate.", (Throwable)e, certPath, index);
        }
        try {
            cert.checkValidity(validCertDate);
        }
        catch (CertificateExpiredException e) {
            throw new ExtCertPathValidatorException("Could not validate certificate: " + e.getMessage(), (Throwable)e, certPath, index);
        }
        catch (CertificateNotYetValidException e) {
            throw new ExtCertPathValidatorException("Could not validate certificate: " + e.getMessage(), (Throwable)e, certPath, index);
        }
        if (revocationChecker != null) {
            revocationChecker.initialize(new PKIXCertRevocationCheckerParameters(paramsPKIX, validCertDate, certPath, index, sign, workingPublicKey));
            revocationChecker.check(cert);
        }
        if (!(issuer = PrincipalUtils.getIssuerPrincipal(cert)).equals(workingIssuerName)) {
            throw new ExtCertPathValidatorException("IssuerName(" + issuer + ") does not match SubjectName(" + workingIssuerName + ") of signing certificate.", null, certPath, index);
        }
    }

    protected static int prepareNextCertI1(CertPath certPath, int index, int explicitPolicy) throws CertPathValidatorException {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        ASN1Sequence pc = null;
        try {
            pc = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, POLICY_CONSTRAINTS));
        }
        catch (Exception e) {
            throw new ExtCertPathValidatorException("Policy constraints extension cannot be decoded.", (Throwable)e, certPath, index);
        }
        if (pc != null) {
            Enumeration policyConstraints = pc.getObjects();
            while (policyConstraints.hasMoreElements()) {
                try {
                    ASN1TaggedObject constraint = ASN1TaggedObject.getInstance(policyConstraints.nextElement());
                    if (constraint.getTagNo() != 0) continue;
                    int tmpInt = ASN1Integer.getInstance(constraint, false).intValueExact();
                    if (tmpInt < explicitPolicy) {
                        return tmpInt;
                    }
                    break;
                }
                catch (IllegalArgumentException e) {
                    throw new ExtCertPathValidatorException("Policy constraints extension contents cannot be decoded.", (Throwable)e, certPath, index);
                }
            }
        }
        return explicitPolicy;
    }

    protected static int prepareNextCertI2(CertPath certPath, int index, int policyMapping) throws CertPathValidatorException {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        ASN1Sequence pc = null;
        try {
            pc = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, POLICY_CONSTRAINTS));
        }
        catch (Exception e) {
            throw new ExtCertPathValidatorException("Policy constraints extension cannot be decoded.", (Throwable)e, certPath, index);
        }
        if (pc != null) {
            Enumeration policyConstraints = pc.getObjects();
            while (policyConstraints.hasMoreElements()) {
                try {
                    ASN1TaggedObject constraint = ASN1TaggedObject.getInstance(policyConstraints.nextElement());
                    if (constraint.getTagNo() != 1) continue;
                    int tmpInt = ASN1Integer.getInstance(constraint, false).intValueExact();
                    if (tmpInt < policyMapping) {
                        return tmpInt;
                    }
                    break;
                }
                catch (IllegalArgumentException e) {
                    throw new ExtCertPathValidatorException("Policy constraints extension contents cannot be decoded.", (Throwable)e, certPath, index);
                }
            }
        }
        return policyMapping;
    }

    protected static void prepareNextCertG(CertPath certPath, int index, PKIXNameConstraintValidator nameConstraintValidator) throws CertPathValidatorException {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        NameConstraints nc = null;
        try {
            ASN1Sequence ncSeq = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, NAME_CONSTRAINTS));
            if (ncSeq != null) {
                nc = NameConstraints.getInstance(ncSeq);
            }
        }
        catch (Exception e) {
            throw new ExtCertPathValidatorException("Name constraints extension could not be decoded.", (Throwable)e, certPath, index);
        }
        if (nc != null) {
            GeneralSubtree[] excluded;
            GeneralSubtree[] permitted = nc.getPermittedSubtrees();
            if (permitted != null) {
                try {
                    nameConstraintValidator.intersectPermittedSubtree(permitted);
                }
                catch (Exception ex) {
                    throw new ExtCertPathValidatorException("Permitted subtrees cannot be build from name constraints extension.", (Throwable)ex, certPath, index);
                }
            }
            if ((excluded = nc.getExcludedSubtrees()) != null) {
                for (int i = 0; i != excluded.length; ++i) {
                    try {
                        nameConstraintValidator.addExcludedSubtree(excluded[i]);
                        continue;
                    }
                    catch (Exception ex) {
                        throw new ExtCertPathValidatorException("Excluded subtrees cannot be build from name constraints extension.", (Throwable)ex, certPath, index);
                    }
                }
            }
        }
    }

    private static void checkCRL(PKIXCertRevocationCheckerParameters params, DistributionPoint dp, PKIXExtendedParameters paramsPKIX, Date currentDate, Date validityDate, X509Certificate cert, X509Certificate defaultCRLSignCert, PublicKey defaultCRLSignKey, CertStatus certStatus, ReasonsMask reasonMask, List certPathCerts, JcaJceHelper helper) throws AnnotatedException, RecoverableCertPathValidatorException {
        if (currentDate == null) {
            boolean bl = true;
        }
        if (validityDate.getTime() > currentDate.getTime()) {
            throw new AnnotatedException("Validation time is in future.");
        }
        Set crls = CertPathValidatorUtilities.getCompleteCRLs(params, dp, cert, paramsPKIX, validityDate);
        boolean validCrlFound = false;
        AnnotatedException lastException = null;
        Iterator crl_iter = crls.iterator();
        while (crl_iter.hasNext() && certStatus.getCertStatus() == 11 && !reasonMask.isAllReasons()) {
            try {
                X509CRL crl = (X509CRL)crl_iter.next();
                ReasonsMask interimReasonsMask = RFC3280CertPathUtilities.processCRLD(crl, dp);
                if (!interimReasonsMask.hasNewReasons(reasonMask)) continue;
                Set keys = RFC3280CertPathUtilities.processCRLF(crl, cert, defaultCRLSignCert, defaultCRLSignKey, paramsPKIX, certPathCerts, helper);
                PublicKey key = RFC3280CertPathUtilities.processCRLG(crl, keys);
                X509CRL deltaCRL = null;
                if (paramsPKIX.isUseDeltasEnabled()) {
                    Set deltaCRLs = CertPathValidatorUtilities.getDeltaCRLs(validityDate, crl, paramsPKIX.getCertStores(), paramsPKIX.getCRLStores(), helper);
                    deltaCRL = RFC3280CertPathUtilities.processCRLH(deltaCRLs, key);
                }
                if (paramsPKIX.getValidityModel() != 1 && cert.getNotAfter().getTime() < crl.getThisUpdate().getTime()) {
                    throw new AnnotatedException("No valid CRL for current time found.");
                }
                RFC3280CertPathUtilities.processCRLB1(dp, cert, crl);
                RFC3280CertPathUtilities.processCRLB2(dp, cert, crl);
                RFC3280CertPathUtilities.processCRLC(deltaCRL, crl, paramsPKIX);
                RFC3280CertPathUtilities.processCRLI(validityDate, deltaCRL, cert, certStatus, paramsPKIX);
                RFC3280CertPathUtilities.processCRLJ(validityDate, crl, cert, certStatus);
                if (certStatus.getCertStatus() == 8) {
                    certStatus.setCertStatus(11);
                }
                reasonMask.addReasons(interimReasonsMask);
                Set<String> criticalExtensions = crl.getCriticalExtensionOIDs();
                if (criticalExtensions != null) {
                    criticalExtensions = new HashSet<String>(criticalExtensions);
                    criticalExtensions.remove(Extension.issuingDistributionPoint.getId());
                    criticalExtensions.remove(Extension.deltaCRLIndicator.getId());
                    if (!criticalExtensions.isEmpty()) {
                        throw new AnnotatedException("CRL contains unsupported critical extensions.");
                    }
                }
                if (deltaCRL != null && (criticalExtensions = deltaCRL.getCriticalExtensionOIDs()) != null) {
                    criticalExtensions = new HashSet<String>(criticalExtensions);
                    criticalExtensions.remove(Extension.issuingDistributionPoint.getId());
                    criticalExtensions.remove(Extension.deltaCRLIndicator.getId());
                    if (!criticalExtensions.isEmpty()) {
                        throw new AnnotatedException("Delta CRL contains unsupported critical extension.");
                    }
                }
                validCrlFound = true;
            }
            catch (AnnotatedException e) {
                lastException = e;
            }
        }
        if (!validCrlFound) {
            throw lastException;
        }
    }

    protected static void checkCRLs(PKIXCertRevocationCheckerParameters params, PKIXExtendedParameters paramsPKIX, Date currentDate, Date validityDate, X509Certificate cert, X509Certificate sign, PublicKey workingPublicKey, List certPathCerts, JcaJceHelper helper) throws AnnotatedException, RecoverableCertPathValidatorException {
        AnnotatedException lastException = null;
        CRLDistPoint crldp = null;
        try {
            crldp = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, CRL_DISTRIBUTION_POINTS));
        }
        catch (Exception e) {
            throw new AnnotatedException("CRL distribution point extension could not be read.", e);
        }
        PKIXExtendedParameters.Builder paramsBldr = new PKIXExtendedParameters.Builder(paramsPKIX);
        try {
            List<PKIXCRLStore> extras = CertPathValidatorUtilities.getAdditionalStoresFromCRLDistributionPoint(crldp, paramsPKIX.getNamedCRLStoreMap(), validityDate, helper);
            Iterator<PKIXCRLStore> it = extras.iterator();
            while (it.hasNext()) {
                paramsBldr.addCRLStore(it.next());
            }
        }
        catch (AnnotatedException e) {
            throw new AnnotatedException("No additional CRL locations could be decoded from CRL distribution point extension.", e);
        }
        CertStatus certStatus = new CertStatus();
        ReasonsMask reasonsMask = new ReasonsMask();
        PKIXExtendedParameters finalParams = paramsBldr.build();
        boolean validCrlFound = false;
        if (crldp != null) {
            DistributionPoint[] dps = null;
            try {
                dps = crldp.getDistributionPoints();
            }
            catch (Exception e) {
                throw new AnnotatedException("Distribution points could not be read.", e);
            }
            if (dps != null) {
                for (int i = 0; i < dps.length && certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons(); ++i) {
                    try {
                        RFC3280CertPathUtilities.checkCRL(params, dps[i], finalParams, currentDate, validityDate, cert, sign, workingPublicKey, certStatus, reasonsMask, certPathCerts, helper);
                        validCrlFound = true;
                        continue;
                    }
                    catch (AnnotatedException e) {
                        lastException = e;
                    }
                }
            }
        }
        if (certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons()) {
            try {
                X500Name issuer;
                try {
                    issuer = PrincipalUtils.getIssuerPrincipal(cert);
                }
                catch (RuntimeException e) {
                    throw new AnnotatedException("Issuer from certificate for CRL could not be reencoded.", e);
                }
                DistributionPoint dp = new DistributionPoint(new DistributionPointName(0, new GeneralNames(new GeneralName(4, issuer))), null, null);
                PKIXExtendedParameters paramsPKIXClone = (PKIXExtendedParameters)paramsPKIX.clone();
                RFC3280CertPathUtilities.checkCRL(params, dp, paramsPKIXClone, currentDate, validityDate, cert, sign, workingPublicKey, certStatus, reasonsMask, certPathCerts, helper);
                validCrlFound = true;
            }
            catch (AnnotatedException e) {
                lastException = e;
            }
        }
        if (!validCrlFound) {
            if (lastException instanceof AnnotatedException) {
                throw lastException;
            }
            throw new AnnotatedException("No valid CRL found.", lastException);
        }
        if (certStatus.getCertStatus() != 11) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            String message = "Certificate revocation after " + df.format(certStatus.getRevocationDate());
            message = message + ", reason: " + crlReasons[certStatus.getCertStatus()];
            throw new AnnotatedException(message);
        }
        if (!reasonsMask.isAllReasons() && certStatus.getCertStatus() == 11) {
            certStatus.setCertStatus(12);
        }
        if (certStatus.getCertStatus() == 12) {
            throw new AnnotatedException("Certificate status could not be determined.");
        }
    }

    protected static int prepareNextCertJ(CertPath certPath, int index, int inhibitAnyPolicy) throws CertPathValidatorException {
        int _inhibitAnyPolicy;
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        ASN1Integer iap = null;
        try {
            iap = ASN1Integer.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, INHIBIT_ANY_POLICY));
        }
        catch (Exception e) {
            throw new ExtCertPathValidatorException("Inhibit any-policy extension cannot be decoded.", (Throwable)e, certPath, index);
        }
        if (iap != null && (_inhibitAnyPolicy = iap.intValueExact()) < inhibitAnyPolicy) {
            return _inhibitAnyPolicy;
        }
        return inhibitAnyPolicy;
    }

    protected static void prepareNextCertK(CertPath certPath, int index) throws CertPathValidatorException {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        BasicConstraints bc = null;
        try {
            bc = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, BASIC_CONSTRAINTS));
        }
        catch (Exception e) {
            throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", (Throwable)e, certPath, index);
        }
        if (bc != null) {
            if (!bc.isCA()) {
                throw new CertPathValidatorException("Not a CA certificate", null, certPath, index);
            }
        } else {
            throw new CertPathValidatorException("Intermediate certificate lacks BasicConstraints", null, certPath, index);
        }
    }

    protected static int prepareNextCertL(CertPath certPath, int index, int maxPathLength) throws CertPathValidatorException {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        if (!CertPathValidatorUtilities.isSelfIssued(cert)) {
            if (maxPathLength <= 0) {
                throw new ExtCertPathValidatorException("Max path length not greater than zero", null, certPath, index);
            }
            return maxPathLength - 1;
        }
        return maxPathLength;
    }

    protected static int prepareNextCertM(CertPath certPath, int index, int maxPathLength) throws CertPathValidatorException {
        ASN1Integer pathLenConstraint;
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        BasicConstraints bc = null;
        try {
            bc = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, BASIC_CONSTRAINTS));
        }
        catch (Exception e) {
            throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", (Throwable)e, certPath, index);
        }
        if (bc != null && bc.isCA() && (pathLenConstraint = bc.getPathLenConstraintInteger()) != null) {
            maxPathLength = Math.min(maxPathLength, pathLenConstraint.intPositiveValueExact());
        }
        return maxPathLength;
    }

    protected static void prepareNextCertN(CertPath certPath, int index) throws CertPathValidatorException {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        boolean[] keyUsage = cert.getKeyUsage();
        if (!(keyUsage == null || keyUsage.length > 5 && keyUsage[5])) {
            throw new ExtCertPathValidatorException("Issuer certificate keyusage extension is critical and does not permit key signing.", null, certPath, index);
        }
    }

    protected static void prepareNextCertO(CertPath certPath, int index, Set criticalExtensions, List pathCheckers) throws CertPathValidatorException {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        Iterator tmpIter = pathCheckers.iterator();
        while (tmpIter.hasNext()) {
            try {
                ((PKIXCertPathChecker)tmpIter.next()).check(cert, criticalExtensions);
            }
            catch (CertPathValidatorException e) {
                throw new CertPathValidatorException(e.getMessage(), e.getCause(), certPath, index);
            }
        }
        if (!criticalExtensions.isEmpty()) {
            throw new ExtCertPathValidatorException("Certificate has unsupported critical extension: " + criticalExtensions, null, certPath, index);
        }
    }

    protected static int prepareNextCertH1(CertPath certPath, int index, int explicitPolicy) {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        if (!CertPathValidatorUtilities.isSelfIssued(cert) && explicitPolicy != 0) {
            return explicitPolicy - 1;
        }
        return explicitPolicy;
    }

    protected static int prepareNextCertH2(CertPath certPath, int index, int policyMapping) {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        if (!CertPathValidatorUtilities.isSelfIssued(cert) && policyMapping != 0) {
            return policyMapping - 1;
        }
        return policyMapping;
    }

    protected static int prepareNextCertH3(CertPath certPath, int index, int inhibitAnyPolicy) {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        if (!CertPathValidatorUtilities.isSelfIssued(cert) && inhibitAnyPolicy != 0) {
            return inhibitAnyPolicy - 1;
        }
        return inhibitAnyPolicy;
    }

    protected static int wrapupCertA(int explicitPolicy, X509Certificate cert) {
        if (!CertPathValidatorUtilities.isSelfIssued(cert) && explicitPolicy != 0) {
            --explicitPolicy;
        }
        return explicitPolicy;
    }

    protected static int wrapupCertB(CertPath certPath, int index, int explicitPolicy) throws CertPathValidatorException {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        ASN1Sequence pc = null;
        try {
            pc = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, POLICY_CONSTRAINTS));
        }
        catch (AnnotatedException e) {
            throw new ExtCertPathValidatorException("Policy constraints could not be decoded.", (Throwable)e, certPath, index);
        }
        if (pc != null) {
            Enumeration policyConstraints = pc.getObjects();
            while (policyConstraints.hasMoreElements()) {
                ASN1TaggedObject constraint = (ASN1TaggedObject)policyConstraints.nextElement();
                switch (constraint.getTagNo()) {
                    case 0: {
                        int tmpInt;
                        try {
                            tmpInt = ASN1Integer.getInstance(constraint, false).intValueExact();
                        }
                        catch (Exception e) {
                            throw new ExtCertPathValidatorException("Policy constraints requireExplicitPolicy field could not be decoded.", (Throwable)e, certPath, index);
                        }
                        if (tmpInt != 0) break;
                        return 0;
                    }
                }
            }
        }
        return explicitPolicy;
    }

    protected static void wrapupCertF(CertPath certPath, int index, List pathCheckers, Set criticalExtensions) throws CertPathValidatorException {
        List<? extends Certificate> certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate)certs.get(index);
        Iterator tmpIter = pathCheckers.iterator();
        while (tmpIter.hasNext()) {
            try {
                ((PKIXCertPathChecker)tmpIter.next()).check(cert, criticalExtensions);
            }
            catch (CertPathValidatorException e) {
                throw new ExtCertPathValidatorException(e.getMessage(), (Throwable)e, certPath, index);
            }
            catch (Exception e) {
                throw new CertPathValidatorException("Additional certificate path checker failed.", (Throwable)e, certPath, index);
            }
        }
        if (!criticalExtensions.isEmpty()) {
            throw new ExtCertPathValidatorException("Certificate has unsupported critical extension: " + criticalExtensions, null, certPath, index);
        }
    }

    protected static PKIXPolicyNode wrapupCertG(CertPath certPath, PKIXExtendedParameters paramsPKIX, Set userInitialPolicySet, int index, List[] policyNodes, PKIXPolicyNode validPolicyTree, Set acceptablePolicies) throws CertPathValidatorException {
        PKIXPolicyNode intersection;
        int n = certPath.getCertificates().size();
        if (validPolicyTree == null) {
            if (paramsPKIX.isExplicitPolicyRequired()) {
                throw new ExtCertPathValidatorException("Explicit policy requested but none available.", null, certPath, index);
            }
            intersection = null;
        } else if (CertPathValidatorUtilities.isAnyPolicy(userInitialPolicySet)) {
            if (paramsPKIX.isExplicitPolicyRequired()) {
                if (acceptablePolicies.isEmpty()) {
                    throw new ExtCertPathValidatorException("Explicit policy requested but none available.", null, certPath, index);
                }
                HashSet _validPolicyNodeSet = new HashSet();
                for (int j = 0; j < policyNodes.length; ++j) {
                    List _nodeDepth = policyNodes[j];
                    for (int k = 0; k < _nodeDepth.size(); ++k) {
                        PKIXPolicyNode _node = (PKIXPolicyNode)_nodeDepth.get(k);
                        if (!ANY_POLICY.equals(_node.getValidPolicy())) continue;
                        Iterator _iter = _node.getChildren();
                        while (_iter.hasNext()) {
                            _validPolicyNodeSet.add(_iter.next());
                        }
                    }
                }
                for (PKIXPolicyNode _node : _validPolicyNodeSet) {
                    String _validPolicy = _node.getValidPolicy();
                    if (acceptablePolicies.contains(_validPolicy)) continue;
                }
                if (validPolicyTree != null) {
                    for (int j = n - 1; j >= 0; --j) {
                        List nodes = policyNodes[j];
                        for (int k = 0; k < nodes.size(); ++k) {
                            PKIXPolicyNode node = (PKIXPolicyNode)nodes.get(k);
                            if (node.hasChildren()) continue;
                            validPolicyTree = CertPathValidatorUtilities.removePolicyNode(validPolicyTree, policyNodes, node);
                        }
                    }
                }
            }
            intersection = validPolicyTree;
        } else {
            HashSet<PKIXPolicyNode> _validPolicyNodeSet = new HashSet<PKIXPolicyNode>();
            for (int j = 0; j < policyNodes.length; ++j) {
                List _nodeDepth = policyNodes[j];
                for (int k = 0; k < _nodeDepth.size(); ++k) {
                    PKIXPolicyNode _node = (PKIXPolicyNode)_nodeDepth.get(k);
                    if (!ANY_POLICY.equals(_node.getValidPolicy())) continue;
                    Iterator _iter = _node.getChildren();
                    while (_iter.hasNext()) {
                        PKIXPolicyNode _c_node = (PKIXPolicyNode)_iter.next();
                        if (ANY_POLICY.equals(_c_node.getValidPolicy())) continue;
                        _validPolicyNodeSet.add(_c_node);
                    }
                }
            }
            for (PKIXPolicyNode _node : _validPolicyNodeSet) {
                String _validPolicy = _node.getValidPolicy();
                if (userInitialPolicySet.contains(_validPolicy)) continue;
                validPolicyTree = CertPathValidatorUtilities.removePolicyNode(validPolicyTree, policyNodes, _node);
            }
            if (validPolicyTree != null) {
                for (int j = n - 1; j >= 0; --j) {
                    List nodes = policyNodes[j];
                    for (int k = 0; k < nodes.size(); ++k) {
                        PKIXPolicyNode node = (PKIXPolicyNode)nodes.get(k);
                        if (node.hasChildren()) continue;
                        validPolicyTree = CertPathValidatorUtilities.removePolicyNode(validPolicyTree, policyNodes, node);
                    }
                }
            }
            intersection = validPolicyTree;
        }
        return intersection;
    }
}

