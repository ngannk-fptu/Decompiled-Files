/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.BasicConstraints
 *  org.bouncycastle.asn1.x509.CRLDistPoint
 *  org.bouncycastle.asn1.x509.DistributionPoint
 *  org.bouncycastle.asn1.x509.DistributionPointName
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.GeneralNames
 *  org.bouncycastle.asn1.x509.IssuingDistributionPoint
 *  org.bouncycastle.jcajce.PKIXCRLStore
 *  org.bouncycastle.jcajce.PKIXCRLStoreSelector
 *  org.bouncycastle.jcajce.PKIXCRLStoreSelector$Builder
 *  org.bouncycastle.jcajce.PKIXCertStoreSelector
 *  org.bouncycastle.jcajce.PKIXCertStoreSelector$Builder
 *  org.bouncycastle.jcajce.PKIXExtendedBuilderParameters
 *  org.bouncycastle.jcajce.PKIXExtendedBuilderParameters$Builder
 *  org.bouncycastle.jcajce.PKIXExtendedParameters
 *  org.bouncycastle.jcajce.PKIXExtendedParameters$Builder
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.pkix.jcajce;

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CRLSelector;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.pkix.jcajce.AnnotatedException;
import org.bouncycastle.pkix.jcajce.CRLNotFoundException;
import org.bouncycastle.pkix.jcajce.CertStatus;
import org.bouncycastle.pkix.jcajce.PKIXCRLUtil;
import org.bouncycastle.pkix.jcajce.ReasonsMask;
import org.bouncycastle.pkix.jcajce.RevocationUtilities;
import org.bouncycastle.util.Arrays;

class RFC3280CertPathUtilities {
    public static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();
    public static final String FRESHEST_CRL = Extension.freshestCRL.getId();
    public static final String DELTA_CRL_INDICATOR = Extension.deltaCRLIndicator.getId();
    public static final String BASIC_CONSTRAINTS = Extension.basicConstraints.getId();
    public static final String AUTHORITY_KEY_IDENTIFIER = Extension.authorityKeyIdentifier.getId();
    protected static final int KEY_CERT_SIGN = 5;
    protected static final int CRL_SIGN = 6;

    RFC3280CertPathUtilities() {
    }

    protected static void processCRLB2(DistributionPoint dp, Object cert, X509CRL crl) throws AnnotatedException {
        IssuingDistributionPoint idp = null;
        try {
            idp = IssuingDistributionPoint.getInstance((Object)RevocationUtilities.getExtensionValue(crl, Extension.issuingDistributionPoint));
        }
        catch (Exception e) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e);
        }
        if (idp != null) {
            if (idp.getDistributionPoint() != null) {
                DistributionPointName dpName = IssuingDistributionPoint.getInstance((Object)idp).getDistributionPoint();
                ArrayList<GeneralName> names = new ArrayList<GeneralName>();
                if (dpName.getType() == 0) {
                    GeneralName[] genNames = GeneralNames.getInstance((Object)dpName.getName()).getNames();
                    for (int j = 0; j < genNames.length; ++j) {
                        names.add(genNames[j]);
                    }
                }
                if (dpName.getType() == 1) {
                    ASN1EncodableVector vec = new ASN1EncodableVector();
                    try {
                        Enumeration e = ASN1Sequence.getInstance((Object)crl.getIssuerX500Principal().getEncoded()).getObjects();
                        while (e.hasMoreElements()) {
                            vec.add((ASN1Encodable)e.nextElement());
                        }
                    }
                    catch (Exception e) {
                        throw new AnnotatedException("Could not read CRL issuer.", e);
                    }
                    vec.add(dpName.getName());
                    names.add(new GeneralName(X500Name.getInstance((Object)new DERSequence(vec))));
                }
                boolean matches = false;
                if (dp.getDistributionPoint() != null) {
                    int j;
                    dpName = dp.getDistributionPoint();
                    GeneralName[] genNames = null;
                    if (dpName.getType() == 0) {
                        genNames = GeneralNames.getInstance((Object)dpName.getName()).getNames();
                    }
                    if (dpName.getType() == 1) {
                        if (dp.getCRLIssuer() != null) {
                            genNames = dp.getCRLIssuer().getNames();
                        } else {
                            genNames = new GeneralName[1];
                            try {
                                genNames[0] = new GeneralName(X500Name.getInstance((Object)((X509Certificate)cert).getIssuerX500Principal().getEncoded()));
                            }
                            catch (Exception e) {
                                throw new AnnotatedException("Could not read certificate issuer.", e);
                            }
                        }
                        for (j = 0; j < genNames.length; ++j) {
                            Enumeration e = ASN1Sequence.getInstance((Object)genNames[j].getName().toASN1Primitive()).getObjects();
                            ASN1EncodableVector vec = new ASN1EncodableVector();
                            while (e.hasMoreElements()) {
                                vec.add((ASN1Encodable)e.nextElement());
                            }
                            vec.add(dpName.getName());
                            genNames[j] = new GeneralName(X500Name.getInstance((Object)new DERSequence(vec)));
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
                bc = BasicConstraints.getInstance((Object)RevocationUtilities.getExtensionValue((X509Extension)cert, Extension.basicConstraints));
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
        ASN1Primitive idp = RevocationUtilities.getExtensionValue(crl, Extension.issuingDistributionPoint);
        boolean isIndirect = false;
        if (idp != null && IssuingDistributionPoint.getInstance((Object)idp).isIndirectCRL()) {
            isIndirect = true;
        }
        byte[] issuerBytes = crl.getIssuerX500Principal().getEncoded();
        boolean matchIssuer = false;
        if (dp.getCRLIssuer() != null) {
            GeneralName[] genNames = dp.getCRLIssuer().getNames();
            for (int j = 0; j < genNames.length; ++j) {
                if (genNames[j].getTagNo() != 4) continue;
                try {
                    if (!Arrays.areEqual((byte[])genNames[j].getName().toASN1Primitive().getEncoded(), (byte[])issuerBytes)) continue;
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
        } else if (crl.getIssuerX500Principal().equals(((X509Certificate)cert).getIssuerX500Principal())) {
            matchIssuer = true;
        }
        if (!matchIssuer) {
            throw new AnnotatedException("Cannot find matching CRL issuer for certificate.");
        }
    }

    protected static ReasonsMask processCRLD(X509CRL crl, DistributionPoint dp) throws AnnotatedException {
        IssuingDistributionPoint idp = null;
        try {
            idp = IssuingDistributionPoint.getInstance((Object)RevocationUtilities.getExtensionValue(crl, Extension.issuingDistributionPoint));
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
            byte[] issuerPrincipal = crl.getIssuerX500Principal().getEncoded();
            certSelector.setSubject(issuerPrincipal);
        }
        catch (IOException e) {
            throw new AnnotatedException("subject criteria for certificate selector to find issuer certificate for CRL could not be set", e);
        }
        PKIXCertStoreSelector selector = new PKIXCertStoreSelector.Builder((CertSelector)certSelector).build();
        LinkedHashSet<X509Certificate> coll = new LinkedHashSet<X509Certificate>();
        try {
            RevocationUtilities.findCertificates(coll, selector, paramsPKIX.getCertificateStores());
            RevocationUtilities.findCertificates(coll, selector, paramsPKIX.getCertStores());
        }
        catch (AnnotatedException e) {
            throw new AnnotatedException("Issuer certificate for CRL cannot be searched.", e);
        }
        coll.add(defaultCRLSignCert);
        ArrayList<X509Certificate> validCerts = new ArrayList<X509Certificate>();
        ArrayList<PublicKey> validKeys = new ArrayList<PublicKey>();
        for (X509Certificate signingCert : coll) {
            if (signingCert.equals(defaultCRLSignCert)) {
                validCerts.add(signingCert);
                validKeys.add(defaultCRLSignKey);
                continue;
            }
            try {
                CertPathBuilder builder = helper.createCertPathBuilder("PKIX");
                X509CertSelector tmpCertSelector = new X509CertSelector();
                tmpCertSelector.setCertificate(signingCert);
                PKIXExtendedParameters.Builder paramsBuilder = new PKIXExtendedParameters.Builder(paramsPKIX).setTargetConstraints(new PKIXCertStoreSelector.Builder((CertSelector)tmpCertSelector).build());
                if (certPathCerts.contains(signingCert)) {
                    paramsBuilder.setRevocationEnabled(false);
                } else {
                    paramsBuilder.setRevocationEnabled(true);
                }
                PKIXExtendedBuilderParameters extParams = new PKIXExtendedBuilderParameters.Builder(paramsBuilder.build()).build();
                List<? extends Certificate> certs = builder.build((CertPathParameters)extParams).getCertPath().getCertificates();
                validCerts.add(signingCert);
                validKeys.add(RevocationUtilities.getNextWorkingKey(certs, 0, helper));
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

    protected static Set processCRLA1i(PKIXExtendedParameters paramsPKIX, Date currentDate, X509Certificate cert, X509CRL crl) throws AnnotatedException {
        HashSet set = new HashSet();
        if (paramsPKIX.isUseDeltasEnabled()) {
            CRLDistPoint freshestCRL = null;
            try {
                freshestCRL = CRLDistPoint.getInstance((Object)RevocationUtilities.getExtensionValue(cert, Extension.freshestCRL));
            }
            catch (AnnotatedException e) {
                throw new AnnotatedException("Freshest CRL extension could not be decoded from certificate.", e);
            }
            if (freshestCRL == null) {
                try {
                    freshestCRL = CRLDistPoint.getInstance((Object)RevocationUtilities.getExtensionValue(crl, Extension.freshestCRL));
                }
                catch (AnnotatedException e) {
                    throw new AnnotatedException("Freshest CRL extension could not be decoded from CRL.", e);
                }
            }
            if (freshestCRL != null) {
                ArrayList<PKIXCRLStore> crlStores = new ArrayList<PKIXCRLStore>();
                crlStores.addAll(paramsPKIX.getCRLStores());
                try {
                    crlStores.addAll(RevocationUtilities.getAdditionalStoresFromCRLDistributionPoint(freshestCRL, paramsPKIX.getNamedCRLStoreMap()));
                }
                catch (AnnotatedException e) {
                    throw new AnnotatedException("No new delta CRL locations could be added from Freshest CRL extension.", e);
                }
                try {
                    set.addAll(RevocationUtilities.getDeltaCRLs(currentDate, crl, paramsPKIX.getCertStores(), crlStores));
                }
                catch (AnnotatedException e) {
                    throw new AnnotatedException("Exception obtaining delta CRLs.", e);
                }
            }
        }
        return set;
    }

    protected static Set[] processCRLA1ii(PKIXExtendedParameters paramsPKIX, Date currentDate, Date validityDate, X509Certificate cert, X509CRL crl) throws AnnotatedException {
        X509CRLSelector crlselect = new X509CRLSelector();
        crlselect.setCertificateChecking(cert);
        try {
            crlselect.addIssuerName(crl.getIssuerX500Principal().getEncoded());
        }
        catch (IOException e) {
            throw new AnnotatedException("Cannot extract issuer from CRL." + e, e);
        }
        PKIXCRLStoreSelector extSelect = new PKIXCRLStoreSelector.Builder((CRLSelector)crlselect).setCompleteCRLEnabled(true).build();
        Set completeSet = PKIXCRLUtil.findCRLs(extSelect, validityDate, paramsPKIX.getCertStores(), paramsPKIX.getCRLStores());
        HashSet deltaSet = new HashSet();
        if (paramsPKIX.isUseDeltasEnabled()) {
            try {
                deltaSet.addAll(RevocationUtilities.getDeltaCRLs(validityDate, crl, paramsPKIX.getCertStores(), paramsPKIX.getCRLStores()));
            }
            catch (AnnotatedException e) {
                throw new AnnotatedException("Exception obtaining delta CRLs.", e);
            }
        }
        return new Set[]{completeSet, deltaSet};
    }

    protected static void processCRLC(X509CRL deltaCRL, X509CRL completeCRL, PKIXExtendedParameters pkixParams) throws AnnotatedException {
        if (deltaCRL == null) {
            return;
        }
        IssuingDistributionPoint completeidp = null;
        try {
            completeidp = IssuingDistributionPoint.getInstance((Object)RevocationUtilities.getExtensionValue(completeCRL, Extension.issuingDistributionPoint));
        }
        catch (Exception e) {
            throw new AnnotatedException("issuing distribution point extension could not be decoded.", e);
        }
        if (pkixParams.isUseDeltasEnabled()) {
            if (!deltaCRL.getIssuerX500Principal().equals(completeCRL.getIssuerX500Principal())) {
                throw new AnnotatedException("complete CRL issuer does not match delta CRL issuer");
            }
            IssuingDistributionPoint deltaidp = null;
            try {
                deltaidp = IssuingDistributionPoint.getInstance((Object)RevocationUtilities.getExtensionValue(deltaCRL, Extension.issuingDistributionPoint));
            }
            catch (Exception e) {
                throw new AnnotatedException("Issuing distribution point extension from delta CRL could not be decoded.", e);
            }
            boolean match = false;
            if (completeidp == null) {
                if (deltaidp == null) {
                    match = true;
                }
            } else if (completeidp.equals((Object)deltaidp)) {
                match = true;
            }
            if (!match) {
                throw new AnnotatedException("Issuing distribution point extension from delta CRL and complete CRL does not match.");
            }
            ASN1Primitive completeKeyIdentifier = null;
            try {
                completeKeyIdentifier = RevocationUtilities.getExtensionValue(completeCRL, Extension.authorityKeyIdentifier);
            }
            catch (AnnotatedException e) {
                throw new AnnotatedException("Authority key identifier extension could not be extracted from complete CRL.", e);
            }
            ASN1Primitive deltaKeyIdentifier = null;
            try {
                deltaKeyIdentifier = RevocationUtilities.getExtensionValue(deltaCRL, Extension.authorityKeyIdentifier);
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
            RevocationUtilities.getCertStatus(validDate, deltacrl, cert, certStatus);
        }
    }

    protected static void processCRLJ(Date validDate, X509CRL completecrl, Object cert, CertStatus certStatus) throws AnnotatedException {
        if (certStatus.getCertStatus() == 11) {
            RevocationUtilities.getCertStatus(validDate, completecrl, cert, certStatus);
        }
    }

    static void checkCRL(DistributionPoint dp, PKIXExtendedParameters paramsPKIX, Date currentDate, Date validityDate, X509Certificate cert, X509Certificate defaultCRLSignCert, PublicKey defaultCRLSignKey, CertStatus certStatus, ReasonsMask reasonMask, List certPathCerts, JcaJceHelper helper) throws AnnotatedException, CRLNotFoundException {
        if (validityDate.getTime() > currentDate.getTime()) {
            throw new AnnotatedException("Validation time is in future.");
        }
        Set crls = RevocationUtilities.getCompleteCRLs(dp, cert, validityDate, paramsPKIX.getCertStores(), paramsPKIX.getCRLStores());
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
                    Set deltaCRLs = RevocationUtilities.getDeltaCRLs(validityDate, crl, paramsPKIX.getCertStores(), paramsPKIX.getCRLStores());
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
}

