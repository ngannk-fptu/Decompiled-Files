/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util.singleuse;

import com.nimbusds.oauth2.sdk.util.singleuse.AlreadyUsedException;

public interface SingleUseChecker<C> {
    public void markAsUsed(C var1) throws AlreadyUsedException;
}

