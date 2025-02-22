/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce;

import java.security.cert.CertStoreParameters;
import java.util.Collection;

public class MultiCertStoreParameters
implements CertStoreParameters {
    private Collection certStores;
    private boolean searchAllStores;

    public MultiCertStoreParameters(Collection certStores) {
        this(certStores, true);
    }

    public MultiCertStoreParameters(Collection certStores, boolean searchAllStores) {
        this.certStores = certStores;
        this.searchAllStores = searchAllStores;
    }

    public Collection getCertStores() {
        return this.certStores;
    }

    public boolean getSearchAllStores() {
        return this.searchAllStores;
    }

    @Override
    public Object clone() {
        return this;
    }
}

