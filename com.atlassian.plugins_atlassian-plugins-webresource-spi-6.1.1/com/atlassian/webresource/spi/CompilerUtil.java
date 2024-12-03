/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webresource.spi;

import com.atlassian.webresource.spi.ResourceCompiler;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class CompilerUtil {
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    private CompilerUtil() {
    }

    public static InputStream toInputStream(ResourceCompiler compiler, String key) {
        if (compiler == null) {
            return null;
        }
        String content = compiler.content(key);
        if (content == null) {
            return null;
        }
        return new ByteArrayInputStream(content.getBytes(CHARSET));
    }
}

