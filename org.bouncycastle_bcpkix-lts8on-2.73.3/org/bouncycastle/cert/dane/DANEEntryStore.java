/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.CollectionStore
 *  org.bouncycastle.util.Selector
 *  org.bouncycastle.util.Store
 *  org.bouncycastle.util.StoreException
 */
package org.bouncycastle.cert.dane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.dane.DANEEntry;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

public class DANEEntryStore
implements Store {
    private final Map entries;

    DANEEntryStore(List entries) {
        HashMap<String, DANEEntry> entryMap = new HashMap<String, DANEEntry>();
        for (DANEEntry entry : entries) {
            entryMap.put(entry.getDomainName(), entry);
        }
        this.entries = Collections.unmodifiableMap(entryMap);
    }

    public Collection getMatches(Selector selector) throws StoreException {
        if (selector == null) {
            return this.entries.values();
        }
        ArrayList results = new ArrayList();
        for (Object next : this.entries.values()) {
            if (!selector.match(next)) continue;
            results.add(next);
        }
        return Collections.unmodifiableList(results);
    }

    public Store toCertificateStore() {
        Collection col = this.getMatches(null);
        ArrayList<X509CertificateHolder> certColl = new ArrayList<X509CertificateHolder>(col.size());
        for (DANEEntry entry : col) {
            certColl.add(entry.getCertificate());
        }
        return new CollectionStore(certColl);
    }
}

