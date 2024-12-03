/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.provider.core.authorization;

import com.atlassian.oauth2.common.IdGenerator;
import com.atlassian.oauth2.provider.core.credentials.ClientCredentialsGenerator;
import javax.annotation.Nonnull;

public class CodeGenerator
implements IdGenerator {
    private final ClientCredentialsGenerator clientCredentialsGenerator;

    public CodeGenerator(ClientCredentialsGenerator clientCredentialsGenerator) {
        this.clientCredentialsGenerator = clientCredentialsGenerator;
    }

    @Override
    @Nonnull
    public String generate() {
        return this.clientCredentialsGenerator.generate(ClientCredentialsGenerator.Length.THIRTY_TWO);
    }
}

