/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.dane;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.dane.DANEEntry;
import org.bouncycastle.cert.dane.DANEEntrySelector;
import org.bouncycastle.cert.dane.DANEEntrySelectorFactory;
import org.bouncycastle.cert.dane.DANEException;
import org.bouncycastle.operator.DigestCalculator;

public class DANEEntryFactory {
    private final DANEEntrySelectorFactory selectorFactory;

    public DANEEntryFactory(DigestCalculator digestCalculator) {
        this.selectorFactory = new DANEEntrySelectorFactory(digestCalculator);
    }

    public DANEEntry createEntry(String emailAddress, X509CertificateHolder certificate) throws DANEException {
        return this.createEntry(emailAddress, 3, certificate);
    }

    public DANEEntry createEntry(String emailAddress, int certUsage, X509CertificateHolder certificate) throws DANEException {
        if (certUsage < 0 || certUsage > 3) {
            throw new DANEException("unknown certificate usage: " + certUsage);
        }
        DANEEntrySelector entrySelector = this.selectorFactory.createSelector(emailAddress);
        byte[] flags = new byte[]{(byte)certUsage, 0, 0};
        return new DANEEntry(entrySelector.getDomainName(), flags, certificate);
    }
}

