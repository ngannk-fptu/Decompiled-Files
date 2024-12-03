/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.writer;

import com.atlassian.jwt.AsymmetricSigningInfo;
import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.SymmetricSigningInfo;
import com.atlassian.jwt.writer.JwtWriter;
import javax.annotation.Nonnull;

public interface JwtWriterFactory {
    @Nonnull
    public JwtWriter macSigningWriter(@Nonnull SigningAlgorithm var1, @Nonnull String var2);

    @Nonnull
    public JwtWriter signingWriter(@Nonnull SymmetricSigningInfo var1);

    @Nonnull
    public JwtWriter signingWriter(@Nonnull AsymmetricSigningInfo var1);
}

