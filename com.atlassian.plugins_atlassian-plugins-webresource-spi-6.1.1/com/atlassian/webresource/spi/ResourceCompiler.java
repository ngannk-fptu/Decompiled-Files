/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webresource.spi;

import com.atlassian.webresource.spi.CompilerEntry;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public interface ResourceCompiler {
    public void compile(@Nonnull Stream<CompilerEntry> var1);

    public String content(@Nonnull String var1);

    default public Optional<InputStream> toInputStream(String key) {
        return Optional.ofNullable(this.content(key)).map(content -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }
}

