/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.language;

import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;

public interface BooleanOperation
extends PolicyOperation {
    public boolean apply(boolean var1);
}

