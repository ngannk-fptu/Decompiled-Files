/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.CollectionStore
 */
package org.bouncycastle.cert.jcajce;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.CollectionStore;

public class JcaCertStore
extends CollectionStore {
    public JcaCertStore(Collection collection) throws CertificateEncodingException {
        super(JcaCertStore.convertCerts(collection));
    }

    private static Collection convertCerts(Collection collection) throws CertificateEncodingException {
        ArrayList<X509CertificateHolder> list = new ArrayList<X509CertificateHolder>(collection.size());
        for (Object o : collection) {
            if (o instanceof X509Certificate) {
                X509Certificate cert = (X509Certificate)o;
                try {
                    list.add(new X509CertificateHolder(cert.getEncoded()));
                    continue;
                }
                catch (IOException e) {
                    throw new CertificateEncodingException("unable to read encoding: " + e.getMessage());
                }
            }
            list.add((X509CertificateHolder)o);
        }
        return list;
    }
}

