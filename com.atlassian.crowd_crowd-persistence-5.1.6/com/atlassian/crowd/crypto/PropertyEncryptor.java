/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Encryptor
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.SetMultimap
 */
package com.atlassian.crowd.crypto;

import com.atlassian.crowd.embedded.api.Encryptor;
import com.atlassian.crowd.model.property.Property;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import java.util.function.UnaryOperator;

public class PropertyEncryptor {
    private Encryptor encryptor;
    private SetMultimap<String, String> encryptedNames;

    public PropertyEncryptor(Encryptor encryptor) {
        this(encryptor, (SetMultimap<String, String>)ImmutableSetMultimap.of((Object)"crowd", (Object)"mailserver.password"));
    }

    public PropertyEncryptor(Encryptor encryptor, SetMultimap<String, String> encryptedNames) {
        this.encryptor = encryptor;
        this.encryptedNames = ImmutableSetMultimap.copyOf(encryptedNames);
    }

    public Property encrypt(Property property) {
        return this.transform(property, arg_0 -> ((Encryptor)this.encryptor).encrypt(arg_0));
    }

    public Property decrypt(Property property) {
        return this.transform(property, arg_0 -> ((Encryptor)this.encryptor).decrypt(arg_0));
    }

    public Property transform(Property property, UnaryOperator<String> transformer) {
        return this.shouldEncrypt(property) ? new Property(property.getKey(), property.getName(), (String)transformer.apply(property.getValue())) : property;
    }

    private boolean shouldEncrypt(Property property) {
        return !Strings.isNullOrEmpty((String)property.getValue()) && this.encryptedNames.get((Object)property.getKey()).contains(property.getName());
    }
}

