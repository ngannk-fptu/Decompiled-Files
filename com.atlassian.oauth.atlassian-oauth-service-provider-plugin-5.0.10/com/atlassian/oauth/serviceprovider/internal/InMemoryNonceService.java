/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.oauth.OAuthProblemException
 */
package com.atlassian.oauth.serviceprovider.internal;

import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;
import net.oauth.OAuthProblemException;

@Deprecated
public class InMemoryNonceService {
    static final TreeSet<NonceEntry> NONCES = new TreeSet();
    private volatile long lastCleaned = 0L;
    private long validityWindowSeconds;

    public InMemoryNonceService(long validityWindowSeconds) {
        this.validityWindowSeconds = validityWindowSeconds;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void validateNonce(String consumerKey, long timestamp, String nonce) throws OAuthProblemException {
        Objects.requireNonNull(consumerKey);
        Objects.requireNonNull(nonce);
        NonceEntry entry = new NonceEntry(consumerKey, timestamp, nonce);
        TreeSet<NonceEntry> treeSet = NONCES;
        synchronized (treeSet) {
            if (NONCES.contains(entry)) {
                throw new OAuthProblemException("nonce_used");
            }
            NONCES.add(entry);
            this.cleanupNonces();
        }
    }

    private void cleanupNonces() {
        long now = System.currentTimeMillis() / 1000L;
        if (now - this.lastCleaned > 1L) {
            NonceEntry nextNonce;
            long difference;
            Iterator<NonceEntry> iterator = NONCES.iterator();
            while (iterator.hasNext() && (difference = now - (nextNonce = iterator.next()).timestamp) > this.getValidityWindowSeconds()) {
                iterator.remove();
            }
            this.lastCleaned = now;
        }
    }

    public long getValidityWindowSeconds() {
        return this.validityWindowSeconds;
    }

    public void setValidityWindowSeconds(long validityWindowSeconds) {
        this.validityWindowSeconds = validityWindowSeconds;
    }

    static class NonceEntry
    implements Comparable<NonceEntry> {
        private final String consumerKey;
        private final long timestamp;
        private final String nonce;

        public NonceEntry(String consumerKey, long timestamp, String nonce) {
            this.consumerKey = consumerKey;
            this.timestamp = timestamp;
            this.nonce = nonce;
        }

        public int hashCode() {
            return this.consumerKey.hashCode() * this.nonce.hashCode() * Long.valueOf(this.timestamp).hashCode();
        }

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof NonceEntry)) {
                return false;
            }
            NonceEntry arg = (NonceEntry)obj;
            return this.timestamp == arg.timestamp && this.consumerKey.equals(arg.consumerKey) && this.nonce.equals(arg.nonce);
        }

        @Override
        public int compareTo(NonceEntry o) {
            if (this.timestamp < o.timestamp) {
                return -1;
            }
            if (this.timestamp == o.timestamp) {
                int consumerKeyCompare = this.consumerKey.compareTo(o.consumerKey);
                if (consumerKeyCompare == 0) {
                    return this.nonce.compareTo(o.nonce);
                }
                return consumerKeyCompare;
            }
            return 1;
        }

        public String toString() {
            return this.timestamp + " " + this.consumerKey + " " + this.nonce;
        }
    }
}

