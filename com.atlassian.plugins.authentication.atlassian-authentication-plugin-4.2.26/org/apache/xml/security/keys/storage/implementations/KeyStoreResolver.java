/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.keys.storage.implementations;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.xml.security.keys.storage.StorageResolverException;
import org.apache.xml.security.keys.storage.StorageResolverSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyStoreResolver
extends StorageResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger(KeyStoreResolver.class);
    private final KeyStore keyStore;

    public KeyStoreResolver(KeyStore keyStore) throws StorageResolverException {
        this.keyStore = keyStore;
        try {
            keyStore.aliases();
        }
        catch (KeyStoreException ex) {
            throw new StorageResolverException(ex);
        }
    }

    @Override
    public Iterator<Certificate> getIterator() {
        return new KeyStoreIterator(this.keyStore);
    }

    static class KeyStoreIterator
    implements Iterator<Certificate> {
        private final List<Certificate> certs;
        private int i;

        public KeyStoreIterator(KeyStore keyStore) {
            ArrayList<Certificate> tmpCerts = new ArrayList<Certificate>();
            try {
                Enumeration<String> aliases = keyStore.aliases();
                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    Certificate cert = keyStore.getCertificate(alias);
                    if (cert == null) continue;
                    tmpCerts.add(cert);
                }
            }
            catch (KeyStoreException ex) {
                LOG.debug("Error reading certificates: {}", (Object)ex.getMessage());
            }
            this.certs = Collections.unmodifiableList(tmpCerts);
            this.i = 0;
        }

        @Override
        public boolean hasNext() {
            return this.i < this.certs.size();
        }

        @Override
        public Certificate next() {
            if (this.hasNext()) {
                return this.certs.get(this.i++);
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }
    }
}

