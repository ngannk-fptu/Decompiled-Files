/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est;

import org.bouncycastle.est.ESTClientProvider;
import org.bouncycastle.est.ESTService;

public class ESTServiceBuilder {
    protected final String server;
    protected ESTClientProvider clientProvider;
    protected String label;

    public ESTServiceBuilder(String server) {
        this.server = server;
    }

    public ESTServiceBuilder withLabel(String label) {
        this.label = label;
        return this;
    }

    public ESTServiceBuilder withClientProvider(ESTClientProvider clientProvider) {
        this.clientProvider = clientProvider;
        return this;
    }

    public ESTService build() {
        return new ESTService(this.server, this.label, this.clientProvider);
    }
}

