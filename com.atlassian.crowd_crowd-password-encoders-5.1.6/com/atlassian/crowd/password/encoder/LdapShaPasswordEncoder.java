/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.ArrayUtils
 *  org.springframework.security.crypto.keygen.BytesKeyGenerator
 *  org.springframework.security.crypto.password.LdapShaPasswordEncoder
 *  org.springframework.security.crypto.password.PasswordEncoder
 */
package com.atlassian.crowd.password.encoder;

import com.atlassian.crowd.exception.PasswordEncoderException;
import com.atlassian.crowd.password.encoder.InternalPasswordEncoder;
import com.atlassian.crowd.password.encoder.LdapPasswordEncoder;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

public class LdapShaPasswordEncoder
implements LdapPasswordEncoder,
InternalPasswordEncoder {
    private final PasswordEncoder encoder = new org.springframework.security.crypto.password.LdapShaPasswordEncoder((BytesKeyGenerator)new NoopBytesKeyGenerator());

    @Override
    public String encodePassword(String rawPass, Object salt) throws PasswordEncoderException {
        return this.encoder.encode((CharSequence)rawPass);
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        Preconditions.checkArgument((boolean)encPass.startsWith("{"), (Object)"SHA prefix missing");
        return this.encoder.matches((CharSequence)rawPass, encPass);
    }

    @Override
    public String getKey() {
        return "sha";
    }

    private static class NoopBytesKeyGenerator
    implements BytesKeyGenerator {
        private NoopBytesKeyGenerator() {
        }

        public int getKeyLength() {
            return 0;
        }

        public byte[] generateKey() {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
    }
}

