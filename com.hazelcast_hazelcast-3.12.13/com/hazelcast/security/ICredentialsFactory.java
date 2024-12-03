/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security;

import com.hazelcast.config.GroupConfig;
import com.hazelcast.security.Credentials;
import java.util.Properties;

public interface ICredentialsFactory {
    public void configure(GroupConfig var1, Properties var2);

    public Credentials newCredentials();

    public void destroy();
}

