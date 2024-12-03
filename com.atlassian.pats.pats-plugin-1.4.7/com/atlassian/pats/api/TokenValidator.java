/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.pats.api;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import javax.annotation.Nonnull;

public interface TokenValidator {
    public boolean doTokensMatch(@Nonnull String var1, @Nonnull String var2);

    public static class DefaultChecksumGenerator
    implements ChecksumGenerator {
        @Override
        public Long getKey(@Nonnull String token, @Nonnull String hashedToken) {
            byte[] combinedToken = token.concat(hashedToken).getBytes(StandardCharsets.UTF_8);
            CRC32 checksum = new CRC32();
            checksum.update(combinedToken);
            return checksum.getValue();
        }
    }

    public static interface ChecksumGenerator {
        public Long getKey(@Nonnull String var1, @Nonnull String var2);
    }
}

