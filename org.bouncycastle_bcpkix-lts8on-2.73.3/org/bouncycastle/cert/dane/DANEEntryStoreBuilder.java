/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.dane;

import org.bouncycastle.cert.dane.DANEEntryFetcherFactory;
import org.bouncycastle.cert.dane.DANEEntryStore;
import org.bouncycastle.cert.dane.DANEException;

public class DANEEntryStoreBuilder {
    private final DANEEntryFetcherFactory daneEntryFetcher;

    public DANEEntryStoreBuilder(DANEEntryFetcherFactory daneEntryFetcher) {
        this.daneEntryFetcher = daneEntryFetcher;
    }

    public DANEEntryStore build(String domainName) throws DANEException {
        return new DANEEntryStore(this.daneEntryFetcher.build(domainName).getEntries());
    }
}

