/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.keys.storage;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.xml.security.keys.storage.StorageResolverException;
import org.apache.xml.security.keys.storage.StorageResolverSpi;
import org.apache.xml.security.keys.storage.implementations.KeyStoreResolver;
import org.apache.xml.security.keys.storage.implementations.SingleCertificateResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageResolver {
    private static final Logger LOG = LoggerFactory.getLogger(StorageResolver.class);
    private final List<StorageResolverSpi> storageResolvers = new ArrayList<StorageResolverSpi>();

    public StorageResolver(StorageResolverSpi resolver) {
        this.add(resolver);
    }

    public StorageResolver(KeyStore keyStore) {
        this.add(keyStore);
    }

    public StorageResolver(X509Certificate x509certificate) {
        this.add(x509certificate);
    }

    public void add(StorageResolverSpi resolver) {
        this.storageResolvers.add(resolver);
    }

    public void add(KeyStore keyStore) {
        try {
            this.add(new KeyStoreResolver(keyStore));
        }
        catch (StorageResolverException ex) {
            LOG.error("Could not add KeyStore because of: ", (Throwable)ex);
        }
    }

    public void add(X509Certificate x509certificate) {
        this.add(new SingleCertificateResolver(x509certificate));
    }

    public Iterator<Certificate> getIterator() {
        return new StorageResolverIterator(this.storageResolvers.iterator());
    }

    static class StorageResolverIterator
    implements Iterator<Certificate> {
        private final Iterator<StorageResolverSpi> resolvers;
        private Iterator<Certificate> currentResolver;

        public StorageResolverIterator(Iterator<StorageResolverSpi> resolvers) {
            this.resolvers = resolvers;
            this.currentResolver = this.findNextResolver();
        }

        @Override
        public boolean hasNext() {
            if (this.currentResolver == null) {
                return false;
            }
            if (this.currentResolver.hasNext()) {
                return true;
            }
            this.currentResolver = this.findNextResolver();
            return this.currentResolver != null;
        }

        @Override
        public Certificate next() {
            if (this.hasNext()) {
                return this.currentResolver.next();
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }

        private Iterator<Certificate> findNextResolver() {
            while (this.resolvers.hasNext()) {
                StorageResolverSpi resolverSpi = this.resolvers.next();
                Iterator<Certificate> iter = resolverSpi.getIterator();
                if (!iter.hasNext()) continue;
                return iter;
            }
            return null;
        }
    }
}

