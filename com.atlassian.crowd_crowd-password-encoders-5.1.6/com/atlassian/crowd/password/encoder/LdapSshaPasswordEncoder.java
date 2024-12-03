/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.springframework.security.crypto.password.LdapShaPasswordEncoder
 *  org.springframework.security.crypto.password.PasswordEncoder
 */
package com.atlassian.crowd.password.encoder;

import com.atlassian.crowd.password.encoder.InternalPasswordEncoder;
import com.atlassian.crowd.password.encoder.LdapPasswordEncoder;
import com.google.common.base.Preconditions;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class LdapSshaPasswordEncoder
implements LdapPasswordEncoder,
InternalPasswordEncoder {
    private final PasswordEncoder encoder = new LdapShaPasswordEncoder();

    @Override
    public String encodePassword(String rawPass, Object salt) {
        return this.encoder.encode((CharSequence)rawPass);
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        Preconditions.checkArgument((boolean)encPass.startsWith("{"), (Object)"SHA prefix missing");
        return this.encoder.matches((CharSequence)rawPass, encPass);
    }

    @Override
    public String getKey() {
        return "ssha";
    }
}

