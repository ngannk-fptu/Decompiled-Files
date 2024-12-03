/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webresource.spi;

import com.atlassian.webresource.spi.CompilerEntry;
import com.atlassian.webresource.spi.ResourceCompiler;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class NoOpResourceCompiler
implements ResourceCompiler {
    @Override
    public void compile(@Nonnull Stream<CompilerEntry> entries) {
    }

    @Override
    public String content(@Nonnull String key) {
        return null;
    }
}

