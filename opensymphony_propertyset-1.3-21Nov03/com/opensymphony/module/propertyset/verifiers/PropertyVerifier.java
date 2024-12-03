/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.verifiers;

import com.opensymphony.module.propertyset.verifiers.VerifyException;
import java.io.Serializable;

public interface PropertyVerifier
extends Serializable {
    public void verify(Object var1) throws VerifyException;
}

