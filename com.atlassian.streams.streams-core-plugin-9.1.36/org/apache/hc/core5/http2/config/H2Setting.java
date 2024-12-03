/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.config;

import org.apache.hc.core5.http2.config.H2Param;
import org.apache.hc.core5.util.Args;

public final class H2Setting {
    private final H2Param param;
    private final int value;

    public H2Setting(H2Param param, int value) {
        Args.notNull(param, "Setting parameter");
        Args.notNegative(value, "Setting value must be a non-negative value");
        this.param = param;
        this.value = value;
    }

    public int getCode() {
        return this.param.code;
    }

    public int getValue() {
        return this.value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder().append((Object)this.param).append(": ").append(this.value);
        return sb.toString();
    }
}

