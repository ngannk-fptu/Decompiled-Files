/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

abstract class PKIXCRLUtil {
    PKIXCRLUtil() {
    }

    static Set findCRLs(PKIXCRLStoreSelector crlselect, Date validityDate, List certStores, List pkixCrlStores) throws AnnotatedException {
        HashSet initialSet = new HashSet();
        try {
            PKIXCRLUtil.findCRLs(initialSet, crlselect, pkixCrlStores);
            PKIXCRLUtil.findCRLs(initialSet, crlselect, certStores);
        }
        catch (AnnotatedException e) {
            throw new AnnotatedException("Exception obtaining complete CRLs.", e);
        }
        HashSet<X509CRL> finalSet = new HashSet<X509CRL>();
        for (X509CRL crl : initialSet) {
            X509Certificate cert;
            Date nextUpdate = crl.getNextUpdate();
            if (nextUpdate != null && !nextUpdate.after(validityDate) || null != (cert = crlselect.getCertificateChecking()) && !crl.getThisUpdate().before(cert.getNotAfter())) continue;
            finalSet.add(crl);
        }
        return finalSet;
    }

    private static void findCRLs(HashSet crls, PKIXCRLStoreSelector crlSelect, List crlStores) throws AnnotatedException {
        AnnotatedException lastException = null;
        boolean foundValidStore = false;
        for (Object obj : crlStores) {
            Object store;
            if (obj instanceof Store) {
                store = (Store)obj;
                try {
                    crls.addAll(store.getMatches(crlSelect));
                    foundValidStore = true;
                }
                catch (StoreException e) {
                    lastException = new AnnotatedException("Exception searching in X.509 CRL store.", e);
                }
                continue;
            }
            store = (CertStore)obj;
            try {
                crls.addAll(PKIXCRLStoreSelector.getCRLs(crlSelect, (CertStore)store));
                foundValidStore = true;
            }
            catch (CertStoreException e) {
                lastException = new AnnotatedException("Exception searching in X.509 CRL store.", e);
            }
        }
        if (!foundValidStore && lastException != null) {
            throw lastException;
        }
    }
}

