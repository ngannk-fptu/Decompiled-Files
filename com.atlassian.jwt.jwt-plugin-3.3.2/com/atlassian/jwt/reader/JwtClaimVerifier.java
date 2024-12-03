/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.reader;

import com.atlassian.jwt.exception.JwtParseException;
import com.atlassian.jwt.exception.JwtVerificationException;
import javax.annotation.Nonnull;

public interface JwtClaimVerifier {
    public void verify(@Nonnull Object var1) throws JwtVerificationException, JwtParseException;
}

