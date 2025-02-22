/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import java.io.Serializable;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

@Immutable
public final class CompressionAlgorithm
implements JSONAware,
Serializable {
    private static final long serialVersionUID = 1L;
    public static final CompressionAlgorithm DEF = new CompressionAlgorithm("DEF");
    private final String name;

    public CompressionAlgorithm(String name) {
        if (name == null) {
            throw new IllegalArgumentException("The compression algorithm name must not be null");
        }
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(Object object) {
        return object != null && object instanceof CompressionAlgorithm && this.toString().equals(object.toString());
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String toJSONString() {
        return "\"" + JSONObject.escape(this.name) + '\"';
    }
}

