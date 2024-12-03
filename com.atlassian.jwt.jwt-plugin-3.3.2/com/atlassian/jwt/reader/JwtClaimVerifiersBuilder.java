/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.reader;

import com.atlassian.jwt.CanonicalHttpRequest;
import com.atlassian.jwt.reader.JwtClaimVerifier;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface JwtClaimVerifiersBuilder {
    public Map<String, ? extends JwtClaimVerifier> build(CanonicalHttpRequest var1) throws UnsupportedEncodingException, NoSuchAlgorithmException;
}

