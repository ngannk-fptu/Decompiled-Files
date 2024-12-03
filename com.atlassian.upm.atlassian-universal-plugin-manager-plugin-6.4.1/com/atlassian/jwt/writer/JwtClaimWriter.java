/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.writer;

import com.atlassian.jwt.writer.JwtJsonBuilder;
import javax.annotation.Nonnull;

public interface JwtClaimWriter {
    public void write(@Nonnull JwtJsonBuilder var1);
}

