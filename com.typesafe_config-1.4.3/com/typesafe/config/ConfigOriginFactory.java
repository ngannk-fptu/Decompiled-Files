/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.impl.ConfigImpl;
import java.net.URL;

public final class ConfigOriginFactory {
    private ConfigOriginFactory() {
    }

    public static ConfigOrigin newSimple() {
        return ConfigOriginFactory.newSimple(null);
    }

    public static ConfigOrigin newSimple(String description) {
        return ConfigImpl.newSimpleOrigin(description);
    }

    public static ConfigOrigin newFile(String filename) {
        return ConfigImpl.newFileOrigin(filename);
    }

    public static ConfigOrigin newURL(URL url) {
        return ConfigImpl.newURLOrigin(url);
    }
}

