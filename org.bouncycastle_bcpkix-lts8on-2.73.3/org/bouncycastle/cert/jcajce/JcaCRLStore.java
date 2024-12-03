/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.CollectionStore
 */
package org.bouncycastle.cert.jcajce;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.util.CollectionStore;

public class JcaCRLStore
extends CollectionStore {
    public JcaCRLStore(Collection collection) throws CRLException {
        super(JcaCRLStore.convertCRLs(collection));
    }

    private static Collection convertCRLs(Collection collection) throws CRLException {
        ArrayList<X509CRLHolder> list = new ArrayList<X509CRLHolder>(collection.size());
        for (Object crl : collection) {
            if (crl instanceof X509CRL) {
                try {
                    list.add(new X509CRLHolder(((X509CRL)crl).getEncoded()));
                    continue;
                }
                catch (IOException e) {
                    throw new CRLException("cannot read encoding: " + e.getMessage());
                }
            }
            list.add((X509CRLHolder)crl);
        }
        return list;
    }
}

