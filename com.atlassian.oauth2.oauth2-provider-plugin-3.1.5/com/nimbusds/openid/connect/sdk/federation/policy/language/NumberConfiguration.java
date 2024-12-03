/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.language;

import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyConfiguration;

public interface NumberConfiguration
extends PolicyConfiguration {
    public void configure(Number var1);

    public Number getNumberConfiguration();
}

