/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.vault.support;

import java.time.Duration;
import org.springframework.util.Assert;

public class ClientOptions {
    private final Duration connectionTimeout;
    private final Duration readTimeout;

    public ClientOptions() {
        this(Duration.ofSeconds(5L), Duration.ofSeconds(15L));
    }

    @Deprecated
    public ClientOptions(int connectionTimeout, int readTimeout) {
        this(Duration.ofMillis(connectionTimeout), Duration.ofMillis(readTimeout));
    }

    public ClientOptions(Duration connectionTimeout, Duration readTimeout) {
        Assert.notNull((Object)connectionTimeout, (String)"Connection timeout must not be null");
        Assert.notNull((Object)readTimeout, (String)"Read timeout must not be null");
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    public Duration getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public Duration getReadTimeout() {
        return this.readTimeout;
    }
}

