/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mail.server.auth;

import java.util.Optional;
import java.util.Properties;

public interface Credentials {
    public Optional<Properties> getProperties();
}

