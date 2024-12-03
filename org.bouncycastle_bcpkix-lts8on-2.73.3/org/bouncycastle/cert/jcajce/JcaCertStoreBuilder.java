/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.cert.jcajce;

import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.cert.CRLException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Extension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.util.Store;

public class JcaCertStoreBuilder {
    private List certs = new ArrayList();
    private List crls = new ArrayList();
    private Object provider;
    private JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
    private JcaX509CRLConverter crlConverter = new JcaX509CRLConverter();
    private String type = "Collection";

    public JcaCertStoreBuilder addCertificates(Store certStore) {
        this.certs.addAll(certStore.getMatches(null));
        return this;
    }

    public JcaCertStoreBuilder addCertificate(X509CertificateHolder cert) {
        this.certs.add(cert);
        return this;
    }

    public JcaCertStoreBuilder addCRLs(Store crlStore) {
        this.crls.addAll(crlStore.getMatches(null));
        return this;
    }

    public JcaCertStoreBuilder addCRL(X509CRLHolder crl) {
        this.crls.add(crl);
        return this;
    }

    public JcaCertStoreBuilder setProvider(String providerName) {
        this.certificateConverter.setProvider(providerName);
        this.crlConverter.setProvider(providerName);
        this.provider = providerName;
        return this;
    }

    public JcaCertStoreBuilder setProvider(Provider provider) {
        this.certificateConverter.setProvider(provider);
        this.crlConverter.setProvider(provider);
        this.provider = provider;
        return this;
    }

    public JcaCertStoreBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public CertStore build() throws GeneralSecurityException {
        CollectionCertStoreParameters params = this.convertHolders(this.certificateConverter, this.crlConverter);
        if (this.provider instanceof String) {
            return CertStore.getInstance(this.type, (CertStoreParameters)params, (String)this.provider);
        }
        if (this.provider instanceof Provider) {
            return CertStore.getInstance(this.type, (CertStoreParameters)params, (Provider)this.provider);
        }
        return CertStore.getInstance(this.type, params);
    }

    private CollectionCertStoreParameters convertHolders(JcaX509CertificateConverter certificateConverter, JcaX509CRLConverter crlConverter) throws CertificateException, CRLException {
        ArrayList<X509Extension> jcaObjs = new ArrayList<X509Extension>(this.certs.size() + this.crls.size());
        Iterator it = this.certs.iterator();
        while (it.hasNext()) {
            jcaObjs.add(certificateConverter.getCertificate((X509CertificateHolder)it.next()));
        }
        it = this.crls.iterator();
        while (it.hasNext()) {
            jcaObjs.add(crlConverter.getCRL((X509CRLHolder)it.next()));
        }
        return new CollectionCertStoreParameters(jcaObjs);
    }
}

