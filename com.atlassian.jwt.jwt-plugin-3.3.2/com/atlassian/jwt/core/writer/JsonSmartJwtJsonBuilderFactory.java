/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.core.writer;

import com.atlassian.jwt.core.writer.JsonSmartJwtJsonBuilder;
import com.atlassian.jwt.writer.JwtJsonBuilder;
import com.atlassian.jwt.writer.JwtJsonBuilderFactory;
import javax.annotation.Nonnull;

public class JsonSmartJwtJsonBuilderFactory
implements JwtJsonBuilderFactory {
    @Override
    @Nonnull
    public JwtJsonBuilder jsonBuilder() {
        return new JsonSmartJwtJsonBuilder();
    }
}

