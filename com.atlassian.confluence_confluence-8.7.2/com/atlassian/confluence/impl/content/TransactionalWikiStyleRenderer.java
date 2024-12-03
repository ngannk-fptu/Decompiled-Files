/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.content;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;

public final class TransactionalWikiStyleRenderer
implements WikiStyleRenderer {
    private final WikiStyleRenderer wikiStyleRenderer;

    public TransactionalWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = Objects.requireNonNull(wikiStyleRenderer);
    }

    @Transactional
    public String convertWikiToXHtml(RenderContext context, String wiki) {
        return this.wikiStyleRenderer.convertWikiToXHtml(context, wiki);
    }
}

