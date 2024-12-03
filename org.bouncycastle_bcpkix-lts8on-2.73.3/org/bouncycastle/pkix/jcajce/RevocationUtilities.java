/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Enumerated
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x500.style.RFC4519Style
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
 *  org.bouncycastle.jcajce.PKIXExtendedParameters
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.util.Selector
 *  org.bouncycastle.util.Store
 *  org.bouncycastle.util.StoreException
 */
package org.bouncycastle.pkix.jcajce;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CRLSelector;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
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
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.pkix.jcajce.AnnotatedException;
import org.bouncycastle.pkix.jcajce.CRLNotFoundException;
import org.bouncycastle.pkix.jcajce.CertStatus;
import org.bouncycastle.pkix.jcajce.PKIXCRLUtil;
import org.bouncycastle.pkix.jcajce.RFC3280CertPathUtilities;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

class RevocationUtilities {
    protected static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();

    RevocationUtilities() {
    }

    protected static Date getValidityDate(PKIXExtendedParameters paramsPKIX, Date currentDate) {
        Date validityDate = paramsPKIX.getValidityDate();
        return null == validityDate ? currentDate : validityDate;
    }

    protected static ASN1Primitive getExtensionValue(X509Extension ext, ASN1ObjectIdentifier oid) throws AnnotatedException {
        byte[] bytes = ext.getExtensionValue(oid.getId());
        return null == bytes ? null : RevocationUtilities.getObject(oid, bytes);
    }

    private static ASN1Primitive getObject(ASN1ObjectIdentifier oid, byte[] ext) throws AnnotatedException {
        try {
            return ASN1Primitive.fromByteArray((byte[])ASN1OctetString.getInstance((Object)ext).getOctets());
        }
        catch (Exception e) {
            throw new AnnotatedException("exception processing extension " + oid, e);
        }
    }

    protected static void findCertificates(LinkedHashSet certs, PKIXCertStoreSelector certSelect, List certStores) throws AnnotatedException {
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
    }

    static List<PKIXCRLStore> getAdditionalStoresFromCRLDistributionPoint(CRLDistPoint crldp, Map<GeneralName, PKIXCRLStore> namedCRLStoreMap) throws AnnotatedException {
        DistributionPoint[] dps;
        if (crldp == null) {
            return Collections.emptyList();
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
            GeneralName[] genNames = GeneralNames.getInstance((Object)dpn.getName()).getNames();
            for (int j = 0; j < genNames.length; ++j) {
                PKIXCRLStore store = namedCRLStoreMap.get(genNames[j]);
                if (store == null) continue;
                stores.add(store);
            }
        }
        return stores;
    }

    protected static void getCRLIssuersFromDistributionPoint(DistributionPoint dp, Collection issuerPrincipals, X509CRLSelector selector) throws AnnotatedException {
        Iterator it;
        ArrayList<Object> issuers = new ArrayList<Object>();
        if (dp.getCRLIssuer() != null) {
            GeneralName[] genNames = dp.getCRLIssuer().getNames();
            for (int j = 0; j < genNames.length; ++j) {
                if (genNames[j].getTagNo() != 4) continue;
                try {
                    issuers.add(X500Name.getInstance((Object)genNames[j].getName()));
                    continue;
                }
                catch (IllegalArgumentException e) {
                    throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", e);
                }
            }
        } else {
            if (dp.getDistributionPoint() == null) {
                throw new AnnotatedException("CRL issuer is omitted from distribution point but no distributionPoint field present.");
            }
            it = issuerPrincipals.iterator();
            while (it.hasNext()) {
                issuers.add(it.next());
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

    protected static void getCertStatus(Date validDate, X509CRL crl, Object cert, CertStatus certStatus) throws AnnotatedException {
        Date revocationDate;
        X500Principal certificateIssuer;
        X500Name expectedCertIssuer;
        X500Name crlIssuer;
        boolean isIndirect;
        try {
            isIndirect = RevocationUtilities.isIndirectCRL(crl);
        }
        catch (CRLException exception) {
            throw new AnnotatedException("Failed check for indirect CRL.", exception);
        }
        X509Certificate x509Cert = (X509Certificate)cert;
        X500Name x509CertIssuer = RevocationUtilities.getIssuer(x509Cert);
        if (!isIndirect && !x509CertIssuer.equals((Object)(crlIssuer = RevocationUtilities.getIssuer(crl)))) {
            return;
        }
        X509CRLEntry crl_entry = crl.getRevokedCertificate(x509Cert.getSerialNumber());
        if (null == crl_entry) {
            return;
        }
        if (isIndirect && !x509CertIssuer.equals((Object)(expectedCertIssuer = null == (certificateIssuer = crl_entry.getCertificateIssuer()) ? RevocationUtilities.getIssuer(crl) : RevocationUtilities.getX500Name(certificateIssuer)))) {
            return;
        }
        int reasonCodeValue = 0;
        if (crl_entry.hasExtensions()) {
            try {
                ASN1Primitive extValue = RevocationUtilities.getExtensionValue(crl_entry, Extension.reasonCode);
                ASN1Enumerated reasonCode = ASN1Enumerated.getInstance((Object)extValue);
                if (null != reasonCode) {
                    reasonCodeValue = reasonCode.intValueExact();
                }
            }
            catch (Exception e) {
                throw new AnnotatedException("Reason code CRL entry extension could not be decoded.", e);
            }
        }
        if (validDate.before(revocationDate = crl_entry.getRevocationDate())) {
            switch (reasonCodeValue) {
                case 0: 
                case 1: 
                case 2: 
                case 10: {
                    break;
                }
                default: {
                    return;
                }
            }
        }
        certStatus.setCertStatus(reasonCodeValue);
        certStatus.setRevocationDate(revocationDate);
    }

    protected static Set getDeltaCRLs(Date validityDate, X509CRL completeCRL, List<CertStore> certStores, List<PKIXCRLStore> pkixCrlStores) throws AnnotatedException {
        byte[] idp;
        X509CRLSelector baseDeltaSelect = new X509CRLSelector();
        try {
            baseDeltaSelect.addIssuerName(completeCRL.getIssuerX500Principal().getEncoded());
        }
        catch (IOException e) {
            throw new AnnotatedException("cannot extract issuer from CRL.", e);
        }
        BigInteger completeCRLNumber = null;
        try {
            ASN1Primitive derObject = RevocationUtilities.getExtensionValue(completeCRL, Extension.cRLNumber);
            if (derObject != null) {
                completeCRLNumber = ASN1Integer.getInstance((Object)derObject).getPositiveValue();
            }
        }
        catch (Exception e) {
            throw new AnnotatedException("cannot extract CRL number extension from CRL", e);
        }
        try {
            idp = completeCRL.getExtensionValue(ISSUING_DISTRIBUTION_POINT);
        }
        catch (Exception e) {
            throw new AnnotatedException("issuing distribution point extension value could not be read", e);
        }
        baseDeltaSelect.setMinCRLNumber(completeCRLNumber == null ? null : completeCRLNumber.add(BigInteger.valueOf(1L)));
        PKIXCRLStoreSelector.Builder selBuilder = new PKIXCRLStoreSelector.Builder((CRLSelector)baseDeltaSelect);
        selBuilder.setIssuingDistributionPoint(idp);
        selBuilder.setIssuingDistributionPointEnabled(true);
        selBuilder.setMaxBaseCRLNumber(completeCRLNumber);
        PKIXCRLStoreSelector deltaSelect = selBuilder.build();
        Set temp = PKIXCRLUtil.findCRLs(deltaSelect, validityDate, certStores, pkixCrlStores);
        HashSet<X509CRL> result = new HashSet<X509CRL>();
        for (X509CRL crl : temp) {
            if (!RevocationUtilities.isDeltaCRL(crl)) continue;
            result.add(crl);
        }
        return result;
    }

    private static boolean isDeltaCRL(X509CRL crl) {
        Set<String> critical = crl.getCriticalExtensionOIDs();
        return null == critical ? false : critical.contains(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
    }

    protected static Set getCompleteCRLs(DistributionPoint dp, Object cert, Date validityDate, List certStores, List crlStores) throws AnnotatedException, CRLNotFoundException {
        X509CRLSelector baseCrlSelect = new X509CRLSelector();
        try {
            HashSet<X500Name> issuers = new HashSet<X500Name>();
            issuers.add(RevocationUtilities.getIssuer((X509Certificate)cert));
            RevocationUtilities.getCRLIssuersFromDistributionPoint(dp, issuers, baseCrlSelect);
        }
        catch (AnnotatedException e) {
            throw new AnnotatedException("Could not get issuer information from distribution point.", e);
        }
        if (cert instanceof X509Certificate) {
            baseCrlSelect.setCertificateChecking((X509Certificate)cert);
        }
        PKIXCRLStoreSelector crlSelect = new PKIXCRLStoreSelector.Builder((CRLSelector)baseCrlSelect).setCompleteCRLEnabled(true).build();
        Set crls = PKIXCRLUtil.findCRLs(crlSelect, validityDate, certStores, crlStores);
        RevocationUtilities.checkCRLsNotEmpty(crls, cert);
        return crls;
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

    static void checkCRLsNotEmpty(Set crls, Object cert) throws CRLNotFoundException {
        if (crls.isEmpty()) {
            X500Name certIssuer = RevocationUtilities.getIssuer((X509Certificate)cert);
            throw new CRLNotFoundException("No CRLs found for issuer \"" + RFC4519Style.INSTANCE.toString(certIssuer) + "\"");
        }
    }

    public static boolean isIndirectCRL(X509CRL crl) throws CRLException {
        try {
            byte[] idp = crl.getExtensionValue(Extension.issuingDistributionPoint.getId());
            return idp != null && IssuingDistributionPoint.getInstance((Object)ASN1OctetString.getInstance((Object)idp).getOctets()).isIndirectCRL();
        }
        catch (Exception e) {
            throw new CRLException("exception reading IssuingDistributionPoint", e);
        }
    }

    private static X500Name getIssuer(X509Certificate cert) {
        return RevocationUtilities.getX500Name(cert.getIssuerX500Principal());
    }

    private static X500Name getIssuer(X509CRL crl) {
        return RevocationUtilities.getX500Name(crl.getIssuerX500Principal());
    }

    private static X500Name getX500Name(X500Principal principal) {
        return X500Name.getInstance((Object)principal.getEncoded());
    }
}

