/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.language;

import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyConfiguration;
import java.util.List;

public interface StringListConfiguration
extends PolicyConfiguration {
    public void configure(List<String> var1);

    public List<String> getStringListConfiguration();
}

