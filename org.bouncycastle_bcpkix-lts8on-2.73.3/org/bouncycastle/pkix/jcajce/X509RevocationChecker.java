/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1String
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.CRLDistPoint
 *  org.bouncycastle.asn1.x509.DistributionPoint
 *  org.bouncycastle.asn1.x509.DistributionPointName
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.GeneralNames
 *  org.bouncycastle.jcajce.PKIXCRLStore
 *  org.bouncycastle.jcajce.PKIXCRLStoreSelector
 *  org.bouncycastle.jcajce.PKIXCRLStoreSelector$Builder
 *  org.bouncycastle.jcajce.PKIXExtendedParameters
 *  org.bouncycastle.jcajce.PKIXExtendedParameters$Builder
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 *  org.bouncycastle.util.CollectionStore
 *  org.bouncycastle.util.Iterable
 *  org.bouncycastle.util.Selector
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.pkix.jcajce;

import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.pkix.jcajce.AnnotatedException;
import org.bouncycastle.pkix.jcajce.CRLNotFoundException;
import org.bouncycastle.pkix.jcajce.CertStatus;
import org.bouncycastle.pkix.jcajce.CrlCache;
import org.bouncycastle.pkix.jcajce.PKIXCRLUtil;
import org.bouncycastle.pkix.jcajce.RFC3280CertPathUtilities;
import org.bouncycastle.pkix.jcajce.ReasonsMask;
import org.bouncycastle.pkix.jcajce.RevocationUtilities;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Iterable;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

public class X509RevocationChecker
extends PKIXCertPathChecker {
    public static final int PKIX_VALIDITY_MODEL = 0;
    public static final int CHAIN_VALIDITY_MODEL = 1;
    private static Logger LOG = Logger.getLogger(X509RevocationChecker.class.getName());
    private final Map<X500Principal, Long> failures = new HashMap<X500Principal, Long>();
    private final Set<TrustAnchor> trustAnchors;
    private final boolean isCheckEEOnly;
    private final int validityModel;
    private final List<Store<CRL>> crls;
    private final List<CertStore> crlCertStores;
    private final JcaJceHelper helper;
    private final boolean canSoftFail;
    private final long failLogMaxTime;
    private final long failHardMaxTime;
    private final Date validationDate;
    private Date currentDate;
    private X500Principal workingIssuerName;
    private PublicKey workingPublicKey;
    private X509Certificate signingCert;
    protected static final String[] crlReasons = new String[]{"unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", "aACompromise"};

    private X509RevocationChecker(Builder bldr) {
        this.crls = new ArrayList<Store<CRL>>(bldr.crls);
        this.crlCertStores = new ArrayList<CertStore>(bldr.crlCertStores);
        this.isCheckEEOnly = bldr.isCheckEEOnly;
        this.validityModel = bldr.validityModel;
        this.trustAnchors = bldr.trustAnchors;
        this.canSoftFail = bldr.canSoftFail;
        this.failLogMaxTime = bldr.failLogMaxTime;
        this.failHardMaxTime = bldr.failHardMaxTime;
        this.validationDate = bldr.validityDate;
        this.helper = bldr.provider != null ? new ProviderJcaJceHelper(bldr.provider) : (bldr.providerName != null ? new NamedJcaJceHelper(bldr.providerName) : new DefaultJcaJceHelper());
    }

    @Override
    public void init(boolean forward) throws CertPathValidatorException {
        if (forward) {
            throw new IllegalArgumentException("forward processing not supported");
        }
        this.currentDate = new Date();
        this.workingIssuerName = null;
    }

    @Override
    public boolean isForwardCheckingSupported() {
        return false;
    }

    @Override
    public Set<String> getSupportedExtensions() {
        return null;
    }

    @Override
    public void check(Certificate certificate, Collection<String> collection) throws CertPathValidatorException {
        int i;
        PKIXExtendedParameters.Builder pkixBuilder;
        X509Certificate cert = (X509Certificate)certificate;
        if (this.isCheckEEOnly && cert.getBasicConstraints() != -1) {
            this.workingIssuerName = cert.getSubjectX500Principal();
            this.workingPublicKey = cert.getPublicKey();
            this.signingCert = cert;
            return;
        }
        TrustAnchor trustAnchor = null;
        if (this.workingIssuerName == null) {
            this.workingIssuerName = cert.getIssuerX500Principal();
            for (TrustAnchor anchor : this.trustAnchors) {
                if (!this.workingIssuerName.equals(anchor.getCA()) && !this.workingIssuerName.equals(anchor.getTrustedCert().getSubjectX500Principal())) continue;
                trustAnchor = anchor;
            }
            if (trustAnchor == null) {
                throw new CertPathValidatorException("no trust anchor found for " + this.workingIssuerName);
            }
            this.signingCert = trustAnchor.getTrustedCert();
            this.workingPublicKey = this.signingCert.getPublicKey();
        }
        ArrayList<X500Principal> issuerList = new ArrayList<X500Principal>();
        try {
            PKIXParameters pkixParams = new PKIXParameters(this.trustAnchors);
            pkixParams.setRevocationEnabled(false);
            pkixParams.setDate(this.validationDate);
            for (int i2 = 0; i2 != this.crlCertStores.size(); ++i2) {
                if (LOG.isLoggable(Level.INFO)) {
                    this.addIssuers(issuerList, this.crlCertStores.get(i2));
                }
                pkixParams.addCertStore(this.crlCertStores.get(i2));
            }
            pkixBuilder = new PKIXExtendedParameters.Builder(pkixParams);
            pkixBuilder.setValidityModel(this.validityModel);
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException("error setting up baseParams: " + e.getMessage());
        }
        for (i = 0; i != this.crls.size(); ++i) {
            if (LOG.isLoggable(Level.INFO)) {
                this.addIssuers(issuerList, this.crls.get(i));
            }
            pkixBuilder.addCRLStore((PKIXCRLStore)new LocalCRLStore(this.crls.get(i)));
        }
        if (issuerList.isEmpty()) {
            LOG.log(Level.INFO, "configured with 0 pre-loaded CRLs");
        } else if (LOG.isLoggable(Level.FINE)) {
            for (i = 0; i != issuerList.size(); ++i) {
                LOG.log(Level.FINE, "configuring with CRL for issuer \"" + issuerList.get(i) + "\"");
            }
        } else {
            LOG.log(Level.INFO, "configured with " + issuerList.size() + " pre-loaded CRLs");
        }
        PKIXExtendedParameters pkixParams = pkixBuilder.build();
        Date validityDate = RevocationUtilities.getValidityDate(pkixParams, this.validationDate);
        try {
            this.checkCRLs(pkixParams, this.currentDate, validityDate, cert, this.signingCert, this.workingPublicKey, new ArrayList(), this.helper);
        }
        catch (AnnotatedException e) {
            throw new CertPathValidatorException(e.getMessage(), e.getCause());
        }
        catch (CRLNotFoundException e) {
            Set<CRL> crls;
            if (null == cert.getExtensionValue(Extension.cRLDistributionPoints.getId())) {
                throw e;
            }
            try {
                crls = this.downloadCRLs(cert.getIssuerX500Principal(), validityDate, RevocationUtilities.getExtensionValue(cert, Extension.cRLDistributionPoints), this.helper);
            }
            catch (AnnotatedException e1) {
                throw new CertPathValidatorException(e1.getMessage(), e1.getCause());
            }
            if (!crls.isEmpty()) {
                try {
                    pkixBuilder.addCRLStore((PKIXCRLStore)new LocalCRLStore((Store<CRL>)new CollectionStore(crls)));
                    pkixParams = pkixBuilder.build();
                    validityDate = RevocationUtilities.getValidityDate(pkixParams, this.validationDate);
                    this.checkCRLs(pkixParams, this.currentDate, validityDate, cert, this.signingCert, this.workingPublicKey, new ArrayList(), this.helper);
                }
                catch (AnnotatedException e1) {
                    throw new CertPathValidatorException(e1.getMessage(), e1.getCause());
                }
            }
            if (!this.canSoftFail) {
                throw e;
            }
            X500Principal issuer = cert.getIssuerX500Principal();
            Long initial = this.failures.get(issuer);
            if (initial != null) {
                long period = System.currentTimeMillis() - initial;
                if (this.failHardMaxTime != -1L && this.failHardMaxTime < period) {
                    throw e;
                }
                if (period < this.failLogMaxTime) {
                    LOG.log(Level.WARNING, "soft failing for issuer: \"" + issuer + "\"");
                } else {
                    LOG.log(Level.SEVERE, "soft failing for issuer: \"" + issuer + "\"");
                }
            }
            this.failures.put(issuer, System.currentTimeMillis());
        }
        this.signingCert = cert;
        this.workingPublicKey = cert.getPublicKey();
        this.workingIssuerName = cert.getSubjectX500Principal();
    }

    private void addIssuers(final List<X500Principal> issuerList, CertStore certStore) throws CertStoreException {
        certStore.getCRLs(new X509CRLSelector(){

            @Override
            public boolean match(CRL crl) {
                if (!(crl instanceof X509CRL)) {
                    return false;
                }
                issuerList.add(((X509CRL)crl).getIssuerX500Principal());
                return false;
            }
        });
    }

    private void addIssuers(final List<X500Principal> issuerList, Store<CRL> certStore) {
        certStore.getMatches((Selector)new Selector<CRL>(){

            public boolean match(CRL crl) {
                if (!(crl instanceof X509CRL)) {
                    return false;
                }
                issuerList.add(((X509CRL)crl).getIssuerX500Principal());
                return false;
            }

            public Object clone() {
                return this;
            }
        });
    }

    private Set<CRL> downloadCRLs(X500Principal issuer, Date currentDate, ASN1Primitive crlDpPrimitive, JcaJceHelper helper) {
        CertificateFactory certFact;
        CRLDistPoint crlDp = CRLDistPoint.getInstance((Object)crlDpPrimitive);
        DistributionPoint[] points = crlDp.getDistributionPoints();
        try {
            certFact = helper.createCertificateFactory("X.509");
        }
        catch (Exception e) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "could not create certFact: " + e.getMessage(), e);
            } else {
                LOG.log(Level.INFO, "could not create certFact: " + e.getMessage());
            }
            return null;
        }
        X509CRLSelector crlSelector = new X509CRLSelector();
        crlSelector.addIssuer(issuer);
        PKIXCRLStoreSelector crlselect = new PKIXCRLStoreSelector.Builder((CRLSelector)crlSelector).build();
        HashSet<CRL> crls = new HashSet<CRL>();
        for (int i = 0; i != points.length; ++i) {
            DistributionPoint dp = points[i];
            DistributionPointName dpn = dp.getDistributionPoint();
            if (dpn == null || dpn.getType() != 0) continue;
            GeneralName[] names = GeneralNames.getInstance((Object)dpn.getName()).getNames();
            for (int n = 0; n != names.length; ++n) {
                GeneralName name = names[n];
                if (name.getTagNo() != 6) continue;
                URI url = null;
                try {
                    url = new URI(((ASN1String)name.getName()).getString());
                    PKIXCRLStore store = CrlCache.getCrl(certFact, this.validationDate, url);
                    if (store == null) continue;
                    crls.addAll(PKIXCRLUtil.findCRLs(crlselect, currentDate, Collections.EMPTY_LIST, Collections.singletonList(store)));
                    continue;
                }
                catch (Exception e) {
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.log(Level.FINE, "CrlDP " + url + " ignored: " + e.getMessage(), e);
                        continue;
                    }
                    LOG.log(Level.INFO, "CrlDP " + url + " ignored: " + e.getMessage());
                }
            }
        }
        return crls;
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
            throw new AnnotatedException("could not read distribution points could not be read", e);
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

    protected void checkCRLs(PKIXExtendedParameters pkixParams, Date currentDate, Date validityDate, X509Certificate cert, X509Certificate sign, PublicKey workingPublicKey, List certPathCerts, JcaJceHelper helper) throws AnnotatedException, CertPathValidatorException {
        CRLDistPoint crldp;
        try {
            crldp = CRLDistPoint.getInstance((Object)RevocationUtilities.getExtensionValue(cert, Extension.cRLDistributionPoints));
        }
        catch (Exception e) {
            throw new AnnotatedException("cannot read CRL distribution point extension", e);
        }
        CertStatus certStatus = new CertStatus();
        ReasonsMask reasonsMask = new ReasonsMask();
        AnnotatedException lastException = null;
        boolean validCrlFound = false;
        if (crldp != null) {
            DistributionPoint[] dps;
            try {
                dps = crldp.getDistributionPoints();
            }
            catch (Exception e) {
                throw new AnnotatedException("cannot read distribution points", e);
            }
            if (dps != null) {
                PKIXExtendedParameters.Builder pkixBuilder = new PKIXExtendedParameters.Builder(pkixParams);
                try {
                    List<PKIXCRLStore> extras = X509RevocationChecker.getAdditionalStoresFromCRLDistributionPoint(crldp, pkixParams.getNamedCRLStoreMap());
                    Iterator<PKIXCRLStore> it = extras.iterator();
                    while (it.hasNext()) {
                        pkixBuilder.addCRLStore(it.next());
                    }
                }
                catch (AnnotatedException e) {
                    throw new AnnotatedException("no additional CRL locations could be decoded from CRL distribution point extension", e);
                }
                PKIXExtendedParameters pkixParamsFinal = pkixBuilder.build();
                Date validityDateFinal = RevocationUtilities.getValidityDate(pkixParamsFinal, currentDate);
                for (int i = 0; i < dps.length && certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons(); ++i) {
                    try {
                        RFC3280CertPathUtilities.checkCRL(dps[i], pkixParamsFinal, currentDate, validityDateFinal, cert, sign, workingPublicKey, certStatus, reasonsMask, certPathCerts, helper);
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
                X500Principal issuer = cert.getIssuerX500Principal();
                DistributionPoint dp = new DistributionPoint(new DistributionPointName(0, (ASN1Encodable)new GeneralNames(new GeneralName(4, (ASN1Encodable)X500Name.getInstance((Object)issuer.getEncoded())))), null, null);
                PKIXExtendedParameters pkixParamsClone = (PKIXExtendedParameters)pkixParams.clone();
                RFC3280CertPathUtilities.checkCRL(dp, pkixParamsClone, currentDate, validityDate, cert, sign, workingPublicKey, certStatus, reasonsMask, certPathCerts, helper);
                validCrlFound = true;
            }
            catch (AnnotatedException e) {
                lastException = e;
            }
        }
        if (!validCrlFound) {
            if (lastException instanceof AnnotatedException) {
                throw new CRLNotFoundException("no valid CRL found", lastException);
            }
            throw new CRLNotFoundException("no valid CRL found");
        }
        if (certStatus.getCertStatus() != 11) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            String message = "certificate [issuer=\"" + cert.getIssuerX500Principal() + "\",serialNumber=" + cert.getSerialNumber() + ",subject=\"" + cert.getSubjectX500Principal() + "\"] revoked after " + df.format(certStatus.getRevocationDate());
            message = message + ", reason: " + crlReasons[certStatus.getCertStatus()];
            throw new AnnotatedException(message);
        }
        if (!reasonsMask.isAllReasons() && certStatus.getCertStatus() == 11) {
            certStatus.setCertStatus(12);
        }
        if (certStatus.getCertStatus() == 12) {
            throw new AnnotatedException("certificate status could not be determined");
        }
    }

    @Override
    public Object clone() {
        return this;
    }

    public static class Builder {
        private Set<TrustAnchor> trustAnchors;
        private List<CertStore> crlCertStores = new ArrayList<CertStore>();
        private List<Store<CRL>> crls = new ArrayList<Store<CRL>>();
        private boolean isCheckEEOnly;
        private int validityModel = 0;
        private Provider provider;
        private String providerName;
        private boolean canSoftFail;
        private long failLogMaxTime;
        private long failHardMaxTime;
        private Date validityDate = new Date();

        public Builder(TrustAnchor trustAnchor) {
            this.trustAnchors = Collections.singleton(trustAnchor);
        }

        public Builder(Set<TrustAnchor> trustAnchors) {
            this.trustAnchors = new HashSet<TrustAnchor>(trustAnchors);
        }

        public Builder(KeyStore trustStore) throws KeyStoreException {
            this.trustAnchors = new HashSet<TrustAnchor>();
            Enumeration<String> en = trustStore.aliases();
            while (en.hasMoreElements()) {
                String alias = en.nextElement();
                if (!trustStore.isCertificateEntry(alias)) continue;
                this.trustAnchors.add(new TrustAnchor((X509Certificate)trustStore.getCertificate(alias), null));
            }
        }

        public Builder addCrls(CertStore crls) {
            this.crlCertStores.add(crls);
            return this;
        }

        public Builder addCrls(Store<CRL> crls) {
            this.crls.add(crls);
            return this;
        }

        public Builder setDate(Date validityDate) {
            this.validityDate = new Date(validityDate.getTime());
            return this;
        }

        public Builder setCheckEndEntityOnly(boolean isTrue) {
            this.isCheckEEOnly = isTrue;
            return this;
        }

        public Builder setSoftFail(boolean isTrue, long maxTime) {
            this.canSoftFail = isTrue;
            this.failLogMaxTime = maxTime;
            this.failHardMaxTime = -1L;
            return this;
        }

        public Builder setSoftFailHardLimit(boolean isTrue, long maxTime) {
            this.canSoftFail = isTrue;
            this.failLogMaxTime = maxTime * 3L / 4L;
            this.failHardMaxTime = maxTime;
            return this;
        }

        public Builder setValidityModel(int validityModel) {
            this.validityModel = validityModel;
            return this;
        }

        public Builder usingProvider(Provider provider) {
            this.provider = provider;
            return this;
        }

        public Builder usingProvider(String providerName) {
            this.providerName = providerName;
            return this;
        }

        public X509RevocationChecker build() {
            return new X509RevocationChecker(this);
        }
    }

    private static class LocalCRLStore
    implements PKIXCRLStore<CRL>,
    Iterable<CRL> {
        private Collection<CRL> _local;

        public LocalCRLStore(Store<CRL> collection) {
            this._local = new ArrayList<CRL>(collection.getMatches(null));
        }

        public Collection<CRL> getMatches(Selector<CRL> selector) {
            if (selector == null) {
                return new ArrayList<CRL>(this._local);
            }
            ArrayList<CRL> col = new ArrayList<CRL>();
            for (CRL obj : this._local) {
                if (!selector.match((Object)obj)) continue;
                col.add(obj);
            }
            return col;
        }

        public Iterator<CRL> iterator() {
            return this.getMatches(null).iterator();
        }
    }
}

