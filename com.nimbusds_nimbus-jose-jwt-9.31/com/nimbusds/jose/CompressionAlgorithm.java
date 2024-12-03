/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.jose;

import com.nimbusds.jose.util.JSONStringUtils;
import java.io.Serializable;
import net.jcip.annotations.Immutable;

@Immutable
public final class CompressionAlgorithm
implements Serializable {
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
        return object instanceof CompressionAlgorithm && this.toString().equals(object.toString());
    }

    public String toString() {
        return this.name;
    }

    public String toJSONString() {
        return JSONStringUtils.toJSONString(this.name);
    }
}

