/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.internal.auth;

import com.amazonaws.auth.Signer;
import com.amazonaws.internal.auth.SignerProviderContext;

public abstract class SignerProvider {
    public abstract Signer getSigner(SignerProviderContext var1);
}

