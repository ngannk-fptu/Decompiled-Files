/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.core.reader;

import com.atlassian.jwt.CanonicalHttpRequest;
import com.atlassian.jwt.core.HttpRequestCanonicalizer;
import com.atlassian.jwt.core.reader.JwtClaimEqualityVerifier;
import com.atlassian.jwt.reader.JwtClaimVerifier;
import com.atlassian.jwt.reader.JwtClaimVerifiersBuilder;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;

public class DefaultJwtClaimVerifiersBuilder
implements JwtClaimVerifiersBuilder {
    @Override
    public Map<String, ? extends JwtClaimVerifier> build(CanonicalHttpRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return Collections.singletonMap("qsh", new JwtClaimEqualityVerifier("qsh", HttpRequestCanonicalizer.computeCanonicalRequestHash(request)));
    }
}

