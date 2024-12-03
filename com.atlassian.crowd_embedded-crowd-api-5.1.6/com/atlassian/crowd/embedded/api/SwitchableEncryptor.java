/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.embedded.api;

import com.atlassian.crowd.embedded.api.Encryptor;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;

public interface SwitchableEncryptor
extends Encryptor {
    public void switchEncryptor(@Nullable String var1);

    public Collection<String> getAvailableEncryptorKeys();

    public Optional<String> getCurrentEncryptorKey();
}

