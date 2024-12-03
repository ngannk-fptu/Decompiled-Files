/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.password.factory;

import com.atlassian.crowd.exception.PasswordEncoderException;
import com.atlassian.crowd.exception.PasswordEncoderNotFoundException;
import com.atlassian.crowd.password.encoder.AtlassianSecurityPasswordEncoder;
import com.atlassian.crowd.password.encoder.InternalPasswordEncoder;
import com.atlassian.crowd.password.encoder.LdapPasswordEncoder;
import com.atlassian.crowd.password.encoder.PasswordEncoder;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordEncoderFactoryImpl
implements PasswordEncoderFactory {
    private final Map<String, PasswordEncoder> internalEncoders = new ConcurrentHashMap<String, PasswordEncoder>();
    private final Map<String, PasswordEncoder> ldapEncoders = new ConcurrentHashMap<String, PasswordEncoder>();
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordEncoderFactoryImpl.class);

    public PasswordEncoderFactoryImpl() {
        this((Iterable<PasswordEncoder>)ImmutableList.of((Object)new AtlassianSecurityPasswordEncoder()));
    }

    public PasswordEncoderFactoryImpl(Iterable<PasswordEncoder> encoders) {
        this.setEncoders(encoders);
    }

    public void setEncoders(Iterable<PasswordEncoder> encoders) {
        for (PasswordEncoder encoder : encoders) {
            this.addEncoder(encoder);
        }
    }

    @Override
    public PasswordEncoder getInternalEncoder(String encoder) {
        PasswordEncoderFactoryImpl.checkArgument(encoder);
        PasswordEncoder passwordEncoder = this.internalEncoders.get(encoder);
        if (passwordEncoder == null) {
            throw new PasswordEncoderNotFoundException("The PasswordEncoder '" + encoder + "' was not found in the Internal Encoders list by the PasswordEncoderFactory");
        }
        return passwordEncoder;
    }

    @Override
    public PasswordEncoder getLdapEncoder(String encoder) {
        PasswordEncoderFactoryImpl.checkArgument(encoder);
        PasswordEncoder passwordEncoder = this.ldapEncoders.get(encoder);
        if (passwordEncoder == null) {
            throw new PasswordEncoderNotFoundException("The PasswordEncoder '" + encoder + "' was not found in the LDAP Encoders list by the PasswordEncoderFactory");
        }
        return passwordEncoder;
    }

    @Override
    public PasswordEncoder getEncoder(String encoder) throws PasswordEncoderNotFoundException {
        PasswordEncoderFactoryImpl.checkArgument(encoder);
        PasswordEncoder passwordEncoder = this.ldapEncoders.get(encoder);
        if (passwordEncoder == null) {
            passwordEncoder = this.internalEncoders.get(encoder);
        }
        if (passwordEncoder == null) {
            throw new PasswordEncoderNotFoundException("The PasswordEncoder '" + encoder + "' was not found in the encoders list by the PasswordEncoderFactory");
        }
        return passwordEncoder;
    }

    @Override
    public Set<String> getSupportedInternalEncoders() {
        return ImmutableSet.copyOf(this.internalEncoders.keySet());
    }

    @Override
    public Set<String> getSupportedLdapEncoders() {
        return ImmutableSet.copyOf(this.ldapEncoders.keySet());
    }

    @Override
    public void addEncoder(PasswordEncoder passwordEncoder) throws PasswordEncoderException {
        if (passwordEncoder == null) {
            throw new PasswordEncoderException("You cannot add a null password encoder to the factory");
        }
        if (passwordEncoder.getKey() == null) {
            throw new PasswordEncoderException("Your password encoder must contain a 'key' value");
        }
        if (!(passwordEncoder instanceof LdapPasswordEncoder) && !(passwordEncoder instanceof InternalPasswordEncoder)) {
            throw new PasswordEncoderException(String.format("Your password encoder does not support a valid encoder type of <%s> or <%s>, but was <%s>", LdapPasswordEncoder.class.getCanonicalName(), InternalPasswordEncoder.class.getCanonicalName(), passwordEncoder.getClass().getCanonicalName()));
        }
        if (passwordEncoder instanceof LdapPasswordEncoder) {
            LOGGER.debug("Adding LDAP Password Encoder to Factory: {}", (Object)passwordEncoder.getKey());
            this.ldapEncoders.put(passwordEncoder.getKey(), passwordEncoder);
        }
        if (passwordEncoder instanceof InternalPasswordEncoder) {
            LOGGER.debug("Adding Internal Password Encoder to Factory: {}", (Object)passwordEncoder.getKey());
            this.internalEncoders.put(passwordEncoder.getKey(), passwordEncoder);
        }
    }

    @Override
    public void removeEncoder(PasswordEncoder passwordEncoder) {
        this.internalEncoders.remove(passwordEncoder.getKey());
        this.ldapEncoders.remove(passwordEncoder.getKey());
    }

    private static void checkArgument(String encoder) {
        if (encoder == null) {
            throw new PasswordEncoderNotFoundException("You cannot get a null password encoder from the factory");
        }
    }
}

