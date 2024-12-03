/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.keys.storage.implementations;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.xml.security.keys.storage.StorageResolverSpi;

public class SingleCertificateResolver
extends StorageResolverSpi {
    private final X509Certificate certificate;

    public SingleCertificateResolver(X509Certificate x509cert) {
        this.certificate = x509cert;
    }

    @Override
    public Iterator<Certificate> getIterator() {
        return new InternalIterator(this.certificate);
    }

    static class InternalIterator
    implements Iterator<Certificate> {
        private boolean alreadyReturned;
        private final X509Certificate certificate;

        public InternalIterator(X509Certificate x509cert) {
            this.certificate = x509cert;
        }

        @Override
        public boolean hasNext() {
            return !this.alreadyReturned;
        }

        @Override
        public Certificate next() {
            if (this.alreadyReturned) {
                throw new NoSuchElementException();
            }
            this.alreadyReturned = true;
            return this.certificate;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }
    }
}

