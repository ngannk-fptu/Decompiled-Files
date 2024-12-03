/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.password.factory;

import com.atlassian.crowd.exception.PasswordEncoderException;
import com.atlassian.crowd.exception.PasswordEncoderNotFoundException;
import com.atlassian.crowd.password.encoder.PasswordEncoder;
import java.util.Set;

public interface PasswordEncoderFactory {
    public static final String DES_ENCODER = "des";
    public static final String SSHA_ENCODER = "ssha";
    public static final String SHA_ENCODER = "sha";
    public static final String PLAINTEXT_ENCODER = "plaintext";
    public static final String MD5_ENCODER = "md5";
    public static final String ATLASSIAN_SECURITY_ENCODER = "atlassian-security";

    public PasswordEncoder getInternalEncoder(String var1) throws PasswordEncoderNotFoundException;

    public PasswordEncoder getLdapEncoder(String var1) throws PasswordEncoderNotFoundException;

    public PasswordEncoder getEncoder(String var1) throws PasswordEncoderNotFoundException;

    public Set<String> getSupportedInternalEncoders();

    public Set<String> getSupportedLdapEncoders();

    public void addEncoder(PasswordEncoder var1) throws PasswordEncoderException;

    public void removeEncoder(PasswordEncoder var1);
}

