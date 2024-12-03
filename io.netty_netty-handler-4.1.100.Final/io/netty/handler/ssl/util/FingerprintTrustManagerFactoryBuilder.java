/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.ssl.util;

import io.netty.handler.ssl.util.FingerprintTrustManagerFactory;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FingerprintTrustManagerFactoryBuilder {
    private final String algorithm;
    private final List<String> fingerprints = new ArrayList<String>();

    FingerprintTrustManagerFactoryBuilder(String algorithm) {
        this.algorithm = (String)ObjectUtil.checkNotNull((Object)algorithm, (String)"algorithm");
    }

    public FingerprintTrustManagerFactoryBuilder fingerprints(CharSequence ... fingerprints) {
        return this.fingerprints(Arrays.asList((Object[])ObjectUtil.checkNotNull((Object)fingerprints, (String)"fingerprints")));
    }

    public FingerprintTrustManagerFactoryBuilder fingerprints(Iterable<? extends CharSequence> fingerprints) {
        ObjectUtil.checkNotNull(fingerprints, (String)"fingerprints");
        for (CharSequence charSequence : fingerprints) {
            ObjectUtil.checkNotNullWithIAE((Object)charSequence, (String)"fingerprint");
            this.fingerprints.add(charSequence.toString());
        }
        return this;
    }

    public FingerprintTrustManagerFactory build() {
        if (this.fingerprints.isEmpty()) {
            throw new IllegalStateException("No fingerprints provided");
        }
        return new FingerprintTrustManagerFactory(this.algorithm, FingerprintTrustManagerFactory.toFingerprintArray(this.fingerprints));
    }
}

