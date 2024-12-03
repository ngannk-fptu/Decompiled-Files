/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.language;

import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyConfiguration;

public interface BooleanConfiguration
extends PolicyConfiguration {
    public void configure(boolean var1);

    public boolean getBooleanConfiguration();
}

