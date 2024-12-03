/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.id;

import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.openid.connect.sdk.id.InvalidPairwiseSubjectException;
import com.nimbusds.openid.connect.sdk.id.SectorID;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.util.Map;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public abstract class PairwiseSubjectCodec {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    private final byte[] salt;
    private Provider provider;

    public PairwiseSubjectCodec(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getSalt() {
        return this.salt;
    }

    public Provider getProvider() {
        return this.provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Subject encode(URI sectorURI, Subject localSub) {
        return this.encode(new SectorID(sectorURI), localSub);
    }

    public abstract Subject encode(SectorID var1, Subject var2);

    public Map.Entry<SectorID, Subject> decode(Subject pairwiseSubject) throws InvalidPairwiseSubjectException {
        throw new UnsupportedOperationException("Pairwise subject decoding is not supported");
    }
}

