/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.proc;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.Key;
import java.util.Collections;
import java.util.List;

public class SingleKeyJWSKeySelector<C extends SecurityContext>
implements JWSKeySelector<C> {
    private final List<Key> singletonKeyList;
    private final JWSAlgorithm expectedJWSAlg;

    public SingleKeyJWSKeySelector(JWSAlgorithm expectedJWSAlg, Key key) {
        if (expectedJWSAlg == null) {
            throw new IllegalArgumentException("The expected JWS algorithm cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("The key cannot be null");
        }
        this.singletonKeyList = Collections.singletonList(key);
        this.expectedJWSAlg = expectedJWSAlg;
    }

    @Override
    public List<? extends Key> selectJWSKeys(JWSHeader header, C context) {
        if (!this.expectedJWSAlg.equals(header.getAlgorithm())) {
            return Collections.emptyList();
        }
        return this.singletonKeyList;
    }
}

