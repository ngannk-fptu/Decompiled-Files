/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.Map;

public final class WebSocketExtensionData {
    private final String name;
    private final Map<String, String> parameters;

    public WebSocketExtensionData(String name, Map<String, String> parameters) {
        this.name = (String)ObjectUtil.checkNotNull((Object)name, (String)"name");
        this.parameters = Collections.unmodifiableMap((Map)ObjectUtil.checkNotNull(parameters, (String)"parameters"));
    }

    public String name() {
        return this.name;
    }

    public Map<String, String> parameters() {
        return this.parameters;
    }
}

