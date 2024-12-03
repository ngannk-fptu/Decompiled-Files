/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.plugin.webresource.impl.support.factory;

import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.http.Router;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.impl.support.InitialContent;
import com.atlassian.plugin.webresource.impl.support.factory.InitialMinifiedContent;
import com.atlassian.plugin.webresource.impl.support.factory.InitialSourceContent;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.qual.NonNull;

public class InitialContentFactory {
    private final Globals globals;
    private final Router router;

    public InitialContentFactory(@Nonnull Globals globals) {
        this.globals = Objects.requireNonNull(globals, "The globals information is mandatory for the creation of a initial content factory.");
        this.router = Objects.requireNonNull(globals.getRouter(), "The router information is mandatory for the creation of a initial content factory.");
    }

    public @NonNull InitialContent lookup(@NonNull Resource resource) {
        InitialContent minifiedContent = InitialMinifiedContent.builder(this.globals).withContent(resource).withPath(resource).withSourceMap(resource).build();
        if (minifiedContent.getContent().isPresent()) {
            return minifiedContent;
        }
        return InitialSourceContent.builder().withContent(resource).withPath(this.router, resource).withSourceMap(resource).build();
    }
}

