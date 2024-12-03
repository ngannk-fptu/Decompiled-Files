/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jcajce.PKIXCRLStoreSelector
 *  org.bouncycastle.jcajce.PKIXCRLStoreSelector$Builder
 *  org.bouncycastle.util.Selector
 *  org.bouncycastle.util.Store
 *  org.bouncycastle.util.StoreException
 */
package org.bouncycastle.pkix.jcajce;

import java.security.cert.CRLSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.pkix.jcajce.AnnotatedException;
import org.bouncycastle.pkix.jcajce.X509CRLStoreSelector;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

abstract class PKIXCRLUtil {
    PKIXCRLUtil() {
    }

    static Set findCRLs(X509CRLStoreSelector crlselect, PKIXParameters paramsPKIX) throws AnnotatedException {
        return PKIXCRLUtil.findCRLs(new PKIXCRLStoreSelector.Builder((CRLSelector)crlselect).build(), paramsPKIX);
    }

    static Set findCRLs(PKIXCRLStoreSelector crlselect, PKIXParameters paramsPKIX) throws AnnotatedException {
        HashSet completeSet = new HashSet();
        try {
            PKIXCRLUtil.findCRLs(completeSet, crlselect, paramsPKIX.getCertStores());
        }
        catch (AnnotatedException e) {
            throw new AnnotatedException("Exception obtaining complete CRLs.", e);
        }
        return completeSet;
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
                    crls.addAll(store.getMatches((Selector)crlSelect));
                    foundValidStore = true;
                }
                catch (StoreException e) {
                    lastException = new AnnotatedException("Exception searching in X.509 CRL store.", e);
                }
                continue;
            }
            store = (CertStore)obj;
            try {
                crls.addAll(PKIXCRLStoreSelector.getCRLs((PKIXCRLStoreSelector)crlSelect, (CertStore)store));
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

