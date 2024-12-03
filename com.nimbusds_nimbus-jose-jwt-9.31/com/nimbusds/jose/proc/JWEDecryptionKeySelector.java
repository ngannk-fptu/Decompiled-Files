/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.jose.proc;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.KeyConverter;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.AbstractJWKSelectorWithSource;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.Key;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class JWEDecryptionKeySelector<C extends SecurityContext>
extends AbstractJWKSelectorWithSource<C>
implements JWEKeySelector<C> {
    private final JWEAlgorithm jweAlg;
    private final EncryptionMethod jweEnc;

    public JWEDecryptionKeySelector(JWEAlgorithm jweAlg, EncryptionMethod jweEnc, JWKSource<C> jwkSource) {
        super(jwkSource);
        if (jweAlg == null) {
            throw new IllegalArgumentException("The JWE algorithm must not be null");
        }
        this.jweAlg = jweAlg;
        if (jweEnc == null) {
            throw new IllegalArgumentException("The JWE encryption method must not be null");
        }
        this.jweEnc = jweEnc;
    }

    public JWEAlgorithm getExpectedJWEAlgorithm() {
        return this.jweAlg;
    }

    public EncryptionMethod getExpectedJWEEncryptionMethod() {
        return this.jweEnc;
    }

    protected JWKMatcher createJWKMatcher(JWEHeader jweHeader) {
        if (!this.getExpectedJWEAlgorithm().equals(jweHeader.getAlgorithm())) {
            return null;
        }
        if (!this.getExpectedJWEEncryptionMethod().equals(jweHeader.getEncryptionMethod())) {
            return null;
        }
        return JWKMatcher.forJWEHeader(jweHeader);
    }

    @Override
    public List<Key> selectJWEKeys(JWEHeader jweHeader, C context) throws KeySourceException {
        if (!this.jweAlg.equals(jweHeader.getAlgorithm()) || !this.jweEnc.equals(jweHeader.getEncryptionMethod())) {
            return Collections.emptyList();
        }
        JWKMatcher jwkMatcher = this.createJWKMatcher(jweHeader);
        List<JWK> jwkMatches = this.getJWKSource().get(new JWKSelector(jwkMatcher), context);
        LinkedList<Key> sanitizedKeyList = new LinkedList<Key>();
        for (Key key : KeyConverter.toJavaKeys(jwkMatches)) {
            if (!(key instanceof PrivateKey) && !(key instanceof SecretKey)) continue;
            sanitizedKeyList.add(key);
        }
        return sanitizedKeyList;
    }
}

