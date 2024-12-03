/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  org.springframework.security.crypto.password.NoOpPasswordEncoder
 *  org.springframework.security.crypto.password.PasswordEncoder
 */
package com.atlassian.crowd.password.encoder;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.password.encoder.InternalPasswordEncoder;
import com.atlassian.crowd.password.encoder.LdapPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PlaintextPasswordEncoder
implements InternalPasswordEncoder,
LdapPasswordEncoder {
    private final PasswordEncoder encoder = NoOpPasswordEncoder.getInstance();

    @Override
    public String getKey() {
        return "plaintext";
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        return !rawPass.equals(PasswordCredential.NONE.getCredential()) && this.encoder.matches((CharSequence)rawPass, encPass);
    }

    @Override
    public String encodePassword(String rawPass, Object salt) {
        return salt == null ? this.encoder.encode((CharSequence)rawPass) : String.format("%s{%s}", this.encoder.encode((CharSequence)rawPass), salt);
    }
}

