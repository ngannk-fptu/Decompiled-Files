/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.dane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.dane.DANEEntry;
import org.bouncycastle.cert.dane.DANEEntryFetcherFactory;
import org.bouncycastle.cert.dane.DANEEntrySelector;
import org.bouncycastle.cert.dane.DANEEntrySelectorFactory;
import org.bouncycastle.cert.dane.DANEException;
import org.bouncycastle.operator.DigestCalculator;

public class DANECertificateFetcher {
    private final DANEEntryFetcherFactory fetcherFactory;
    private final DANEEntrySelectorFactory selectorFactory;

    public DANECertificateFetcher(DANEEntryFetcherFactory fetcherFactory, DigestCalculator digestCalculator) {
        this.fetcherFactory = fetcherFactory;
        this.selectorFactory = new DANEEntrySelectorFactory(digestCalculator);
    }

    public List fetch(String emailAddress) throws DANEException {
        DANEEntrySelector daneSelector = this.selectorFactory.createSelector(emailAddress);
        List matches = this.fetcherFactory.build(daneSelector.getDomainName()).getEntries();
        ArrayList<X509CertificateHolder> certs = new ArrayList<X509CertificateHolder>(matches.size());
        for (DANEEntry next : matches) {
            if (!daneSelector.match(next)) continue;
            certs.add(next.getCertificate());
        }
        return Collections.unmodifiableList(certs);
    }
}

