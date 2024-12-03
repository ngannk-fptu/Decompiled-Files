/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.jce.MultiCertStoreParameters;

public class MultiCertStoreSpi
extends CertStoreSpi {
    private MultiCertStoreParameters params;

    public MultiCertStoreSpi(CertStoreParameters params) throws InvalidAlgorithmParameterException {
        super(params);
        if (!(params instanceof MultiCertStoreParameters)) {
            throw new InvalidAlgorithmParameterException("org.bouncycastle.jce.provider.MultiCertStoreSpi: parameter must be a MultiCertStoreParameters object\n" + params.toString());
        }
        this.params = (MultiCertStoreParameters)params;
    }

    public Collection engineGetCertificates(CertSelector certSelector) throws CertStoreException {
        List allCerts;
        boolean searchAllStores = this.params.getSearchAllStores();
        Iterator iter = this.params.getCertStores().iterator();
        List list = allCerts = searchAllStores ? new ArrayList() : Collections.EMPTY_LIST;
        while (iter.hasNext()) {
            CertStore store = (CertStore)iter.next();
            Collection<? extends Certificate> certs = store.getCertificates(certSelector);
            if (searchAllStores) {
                allCerts.addAll(certs);
                continue;
            }
            if (certs.isEmpty()) continue;
            return certs;
        }
        return allCerts;
    }

    public Collection engineGetCRLs(CRLSelector crlSelector) throws CertStoreException {
        List allCRLs;
        boolean searchAllStores = this.params.getSearchAllStores();
        Iterator iter = this.params.getCertStores().iterator();
        List list = allCRLs = searchAllStores ? new ArrayList() : Collections.EMPTY_LIST;
        while (iter.hasNext()) {
            CertStore store = (CertStore)iter.next();
            Collection<? extends CRL> crls = store.getCRLs(crlSelector);
            if (searchAllStores) {
                allCRLs.addAll(crls);
                continue;
            }
            if (crls.isEmpty()) continue;
            return crls;
        }
        return allCRLs;
    }
}

