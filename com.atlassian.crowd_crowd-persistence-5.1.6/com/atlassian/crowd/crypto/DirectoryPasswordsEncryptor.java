/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.Encryptor
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 */
package com.atlassian.crowd.crypto;

import com.atlassian.crowd.directory.LazyAttributesEvaluationDirectory;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.Encryptor;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import java.util.function.UnaryOperator;

public class DirectoryPasswordsEncryptor {
    private final Encryptor encryptor;

    public DirectoryPasswordsEncryptor(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    public Directory decryptPasswords(Directory directory) {
        return this.transformPasswordAttributes(directory, arg_0 -> ((Encryptor)this.encryptor).decrypt(arg_0));
    }

    public Directory encryptPasswords(Directory directory) {
        return this.transformPasswordAttributes(directory, arg_0 -> ((Encryptor)this.encryptor).encrypt(arg_0));
    }

    private LazyAttributesEvaluationDirectory transformPasswordAttributes(Directory directory, UnaryOperator<String> transformer) {
        return new LazyAttributesEvaluationDirectory(directory, DirectoryImpl.PASSWORD_ATTRIBUTES, transformer);
    }
}

